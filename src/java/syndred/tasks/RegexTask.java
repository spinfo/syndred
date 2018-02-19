package syndred.tasks;

import java.text.ParseException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;
import syndred.logic.DraftState;

public class RegexTask extends Task {

	Pattern pattern;

	public RegexTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) throws ExecutionException {
		super(input, output, parser);

		try {
			pattern = Pattern.compile(parser.getGrammar());
		} catch (Throwable thrown) {
			throw new ExecutionException(thrown);
		}
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		DraftState.del(state, "Success");
		Matcher matcher = pattern
				.matcher(state.getBlocks().stream().map(i -> i.getText()).collect(Collectors.joining()));

		try {
			while (matcher.find())
				DraftState.add(state, "Success", matcher.start(), matcher.end() - matcher.start());
		} catch (Throwable thrown) {
			throw new ParseException(thrown.getMessage(), matcher.start());
		}

		return state;
	}

}
