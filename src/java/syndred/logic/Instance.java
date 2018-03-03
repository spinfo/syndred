package syndred.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import org.springframework.messaging.MessagingException;

import ch.qos.logback.classic.pattern.ThreadConverter;
import syndred.entities.Editor;
import syndred.entities.Parser;

public class Instance {

	public volatile Future<Parser> future = null;

	public volatile BlockingQueue<Editor> input = new LinkedBlockingQueue<Editor>(1);

	public volatile Map<Class<?>, Function<Object, MessagingException>> pipes = new HashMap<Class<?>, Function<Object, MessagingException>>();

	public volatile Map<Class<?>, Object> values = new HashMap<Class<?>, Object>();

	public boolean active() {
		return future != null && !future.isDone();
	}

	public void close() {
		System.out.println("++++++++++ instance.close(" + Thread.currentThread().getId() + ")");
		if (future != null && !future.isCancelled())
			future.cancel(true);
	}

	public void enqueue(Editor editor) {
		try {
			input.put(editor);
		} catch (InterruptedException expected) {
		}
	}

	public MessagingException pipe(Object obj) {
		return pipes.get(obj.getClass()).apply(obj);
	}

	@SuppressWarnings("unchecked")
	public <T> T value(Class<T> key) {
		try {
			return (T) Optional.ofNullable(values.get(key)).orElse(key.newInstance());
		} catch (Exception exception) {
			return null;
		}
	}

}
