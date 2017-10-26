package syndred.tasks;

import java.text.ParseException;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

public class EchoTask extends Task {

	public EchoTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) {
		super(input, output, parser);
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		return state;
	}

}
