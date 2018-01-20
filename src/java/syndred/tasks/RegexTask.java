package syndred.tasks;

import java.text.ParseException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

public class RegexTask extends Task {

	public RegexTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) throws ExecutionException {
		super(input, output, parser);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

}
