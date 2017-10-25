package syndred.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;
import syndred.tasks.EchoTask;

public class Threading {

	private static ExecutorService executorService = Executors.newCachedThreadPool();
	private static Map<String, Future<Parser>> futures = new HashMap<String, Future<Parser>>();
	private static Map<String, BlockingQueue<RawDraftContentState>> input = new HashMap<String, BlockingQueue<RawDraftContentState>>();
	private static Map<String, Function<RawDraftContentState, Exception>> output = new HashMap<String, Function<RawDraftContentState, Exception>>();
	private static Map<String, Parser> parsers = new HashMap<String, Parser>();
	private static Map<String, RawDraftContentState> states = new HashMap<String, RawDraftContentState>();

	public static void callback(String instence, Function<RawDraftContentState, Exception> callback) {
		output.put(instence, new Function<RawDraftContentState, Exception>() {
			@Override
			public Exception apply(RawDraftContentState state) {
				states.put(instence, state);
				return callback.apply(state);
			}
		});
	}

	public static void connect(String instance) throws InterruptedException {
		input.put(instance, new LinkedBlockingQueue<RawDraftContentState>(1));
		states.put(instance, new RawDraftContentState());
		parsers.put(instance, new Parser());
	}

	public static boolean connected(String instance) {
		return states.containsKey(instance);
	}

	public static void disconnect(String instance) {
		Future<Parser> future = futures.remove(instance);
		if (future != null)
			future.cancel(true);

		input.remove(instance);
		parsers.remove(instance);
		output.remove(instance);
		states.remove(instance);
	}

	public static RawDraftContentState pull(String instance) {
		return states.get(instance);
	}

	public static void push(String instance, RawDraftContentState state) throws InterruptedException {
		input.get(instance).put(state);
	}

	public static Parser parser(String instance) {
		return parsers.get(instance);
	}

	public static Parser run(String instance, Parser parser) throws InterruptedException, ExecutionException {
		if (!output.containsKey(instance))
			throw new InterruptedException();

		Future<Parser> future = futures.get(instance);
		if (future != null && !future.isDone() && !future.isCancelled())
			future.cancel(true);

		switch (parser.getName()) {
		case "abnf":
			parser.setError("Not implemented");
			parser.setRunning(false);
			return parser;

		case "ebnf":
			parser.setError("Not implemented");
			parser.setRunning(false);
			return parser;

		case "regex":
			parser.setError("Not implemented");
			parser.setRunning(false);
			return parser;

		case "test":
			future = executorService.submit(new EchoTask(input.get(instance), output.get(instance), parser));
			break;

		default:
			parser.setError("Not implemented");
			parser.setRunning(false);
			return parser;
		}

		parser.setError("");
		parser.setRunning(true);

		futures.put(instance, future);
		parsers.put(instance, parser);
		return parser;
	}

}
