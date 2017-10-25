package syndred.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;
import syndred.tasks.Task;

public class Threading {

	private static Map<String, BlockingQueue<RawDraftContentState>> input = new HashMap<String, BlockingQueue<RawDraftContentState>>();
	private static Map<String, BlockingQueue<RawDraftContentState>> output = new HashMap<String, BlockingQueue<RawDraftContentState>>();
	private static Map<String, Function<RawDraftContentState, Exception>> pipes = new HashMap<String, Function<RawDraftContentState, Exception>>();
	private static Map<String, Parser> parsers = new HashMap<String, Parser>();
	private static Map<String, RawDraftContentState> states = new HashMap<String, RawDraftContentState>();

	public static void callback(String instence, Function<RawDraftContentState, Exception> callback) {
		pipes.put(instence, callback);
	}

	public static void connect(String instance) throws InterruptedException {
		if (!pipes.containsKey(instance))
			throw new InterruptedException();

		System.out.println("Connecting " + instance);
		input.put(instance, new LinkedBlockingQueue<RawDraftContentState>(1));
		output.put(instance, new LinkedBlockingQueue<RawDraftContentState>(1));
		states.put(instance, new RawDraftContentState());
		parsers.put(instance, new Parser());
	}

	public static boolean connected(String instance) {
		return states.containsKey(instance);
	}

	public static void disconnect(String instance) {
		System.out.println("Disconnecting " + instance);
		input.remove(instance);
		output.remove(instance);
		parsers.remove(instance);
		pipes.remove(instance);
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

	public static Parser run(String instance, Parser parser) {
		Task task;

		switch (parser.getName()) {
		case "regex":
			parser.setError("");
			parser.setRunning(true);
			parser.setName("ebnf");
			break;

		case "ebnf":
			parser.setError("Not implemented");
			parser.setRunning(false);
			break;

		case "abnf":
		default:
			parser.setError("Not implemented");
			parser.setRunning(false);
		}

		parsers.put(instance, parser);
		return parser;
	}

}
