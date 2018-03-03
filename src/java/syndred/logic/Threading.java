package syndred.logic;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.springframework.messaging.MessagingException;

import syndred.entities.Editor;
import syndred.entities.Parser;
import syndred.tasks.EchoTask;
import syndred.tasks.RbnfTask;
import syndred.tasks.RegexTask;

public class Threading {

	private static ExecutorService executor = Executors.newCachedThreadPool();

	private static ConcurrentMap<String, Instance> instances = new ConcurrentHashMap<String, Instance>();

	public static void connect(String id) {
		if (!instances.containsKey(id))
			instances.put(id, new Instance());
	}

	public static void disconnect(String id) {
		instances.remove(id).close();
	}

	public static void setCallback(String id, Class<?> key, Function<Object, MessagingException> callback) {
		instances.get(id).pipes.put(key, new Function<Object, MessagingException>() {
			@Override
			public MessagingException apply(Object entity) {
				instances.get(id).values.put(entity.getClass(), entity);
				return callback.apply(entity);
			}
		});
	}

	public static Editor getEditor(String id) {
		return instances.get(id).value(Editor.class);
	}

	public static void setEditor(String id, Editor editor) {
		instances.get(id).enqueue(editor);
	}

	public static Parser getParser(String id) {
		return instances.get(id).value(Parser.class);
	}

	public static void setParser(String id, Parser parser) {
		Instance instance = instances.get(id);

		parser.setError("");
		parser.setRunning(false);

		instance.close();
		instance.pipe(parser);

		try {
			switch (parser.getName()) {
			case "rbnf":
				instance.future = executor.submit(new RbnfTask(instance));
				break;

			case "regex":
				instance.future = executor.submit(new RegexTask(instance));
				break;

			case "test":
				instance.future = executor.submit(new EchoTask(instance));
				break;

			default:
				throw new ClassNotFoundException("Parser not implemented");
			}
		} catch (Throwable thrown) {
			parser.setError(thrown.getMessage());
		} finally {
			parser.setRunning(instance.active());
			instance.pipe(parser);
		}

		// try {
		// instance.pipe(Parser.class).apply(instance.future.get());
		// } catch (CancellationException expected) {
		// } catch (InterruptedException expected) {
		// } catch (Throwable thrown) {
		// parser.setError(thrown.getMessage());
		// parser.setRunning(instance.active());
		// instance.pipe(Parser.class).apply(parser);
		// }
	}

}
