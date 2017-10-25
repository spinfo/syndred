package syndred.tasks;

import java.text.ParseException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Function;

import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

public abstract class Task implements Callable<Parser> {

	private final BlockingQueue<RawDraftContentState> input;

	private final Function<RawDraftContentState, Exception> output;

	private final Parser parser;

	public Task(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) {
		this.input = input;
		this.output = output;
		this.parser = parser;
	}

	@Override
	public Parser call() {
		while (!Thread.interrupted()) {
			try {
				Exception error = output.apply(parse(input.take()));
				if (error != null)
					throw error;
			} catch (Exception error) {
				parser.setRunning(false);
				parser.setError(error.getClass().getSimpleName() + ": " + error.getMessage());
				break;
			}
		}

		return parser;
	};

	abstract protected RawDraftContentState parse(RawDraftContentState state) throws ParseException;

}
