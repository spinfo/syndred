package syndred.tasks;

import java.text.ParseException;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

public class EbnfTask extends Task {

	public EbnfTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) {
		super(input, output, parser);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

}
