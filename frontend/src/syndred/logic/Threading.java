package syndred.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;
import texts.RichChar;

public class Threading {

	private static Map<String, Parser> parsers = new HashMap<String, Parser>();
	private static Map<String, BlockingQueue> queues = new HashMap<String, BlockingQueue>();
	private static Map<String, RawDraftContentState> states = new HashMap<String, RawDraftContentState>();

	public static void connect(String instance) {
		if (!states.containsKey(instance)) {
			parsers.put(instance, new Parser());
			queues.put(instance, new LinkedBlockingQueue<RichChar>());
			states.put(instance, new RawDraftContentState());
		}
	}

	public static void disconnect(String instance) {
		parsers.remove(instance);
		queues.remove(instance);
		states.remove(instance);
	}

	public static Parser getParser(String instance) {
		return parsers.get(instance);
	}

	public static void setParser(String instance, Parser parser) {
		parsers.put(instance, parser);
	}

	public static RawDraftContentState getState(String instance) {
		return states.get(instance);
	}

	public static void setState(String instance, RawDraftContentState state) {
		states.put(instance, state);
	}

}
