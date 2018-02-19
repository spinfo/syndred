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
import syndred.tasks.EbnfTask;
import syndred.tasks.EchoTask;
<<<<<<< ours:frontend/src/syndred/logic/Threading.java
=======
import syndred.tasks.RbnfTask;
>>>>>>> theirs:src/java/syndred/logic/Threading.java
import syndred.tasks.RegexTask;

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

	public static void connect(String instance) {
		if (!connected(instance)) {
			input.put(instance, new LinkedBlockingQueue<RawDraftContentState>(1));
			parsers.put(instance, new Parser());
			states.put(instance, new RawDraftContentState());
		}
	}

	public static boolean connected(String instance) {
		return states.containsKey(instance);
	}

	public static void disconnect(String instance) {
		Future<Parser> future = futures.remove(instance);

		if (future != null && !future.isCancelled())
			future.cancel(true);

		input.remove(instance);
		output.remove(instance);
		parsers.remove(instance);
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
		Future<Parser> future = futures.get(instance);

<<<<<<< ours:frontend/src/syndred/logic/Threading.java
		case "ebnf":
			future = executorService.submit(new EbnfTask(input.get(instance), output.get(instance), parser));
			break;

		case "regex":
			future = executorService.submit(new RegexTask(input.get(instance), output.get(instance), parser));
//			parser.setError("Not implemented");
//			parser.setRunning(false);
			break;

		case "test":
			future = executorService.submit(new EchoTask(input.get(instance), output.get(instance), parser));
			break;
=======
		if (future != null && !future.isCancelled())
			future.cancel(true);
>>>>>>> theirs:src/java/syndred/logic/Threading.java

		parser.setError("");
		parser.setRunning(false);

		try {
			switch (parser.getName()) {
			// case "abnf":
			// future = executorService.submit(new AbnfTask(input.get(instance),
			// output.get(instance), parser));
			// break;

			case "rbnf":
				future = executorService.submit(new RbnfTask(input.get(instance), output.get(instance), parser));
				break;

			case "regex":
				future = executorService.submit(new RegexTask(input.get(instance), output.get(instance), parser));
				break;

			case "test":
				future = executorService.submit(new EchoTask(input.get(instance), output.get(instance), parser));
				break;

			default:
				parser.setError("Parser not implemented");
				return parser;
			}
		} catch (ExecutionException error) {
			parser.setError(error.getMessage());
			return parser;
		}

		parser.setRunning(true);
		futures.put(instance, future);
		parsers.put(instance, parser);
		return parser;
	}

}
