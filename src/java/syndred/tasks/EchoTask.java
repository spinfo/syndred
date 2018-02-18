package syndred.tasks;

import java.text.ParseException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

public class EchoTask extends Task {

	public EchoTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) throws ExecutionException {
		super(input, output, parser);
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		return state;
	}

}
