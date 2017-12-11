package syndred.tasks;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.function.Function;

import syndred.entities.InlineStyleRange;
import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

public abstract class Task implements Callable<Parser> {

	private final BlockingQueue<RawDraftContentState> input;

	private final Function<RawDraftContentState, Exception> output;

	protected final Parser parser;

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

	public List<InlineStyleRange> invertRanges(List<InlineStyleRange> ranges, int blockLength){
		List<InlineStyleRange> invers = new ArrayList<InlineStyleRange>();
		Iterator<InlineStyleRange> iter = ranges.iterator();
		if(iter.hasNext()){
			InlineStyleRange range1 = iter.next();
			int off = range1.getOffset();
			if(off > 0){
				System.out.println("Anfang");
				InlineStyleRange range = new InlineStyleRange();
				range.setOffset(0);
				range.setLength(off);
				range.setStyle("error");
				invers.add(range);
				System.out.println(range);
			} else {
				while(iter.hasNext()){
					System.out.println("mitte");
					off = range1.getOffset();
					InlineStyleRange range2 = iter.next();
					int pos = off+range1.getLength();
//					System.out.println("pos: " + pos);
//					System.out.println("range2-Offset: " + range2.getOffset());
					if(pos < range2.getOffset()){
						InlineStyleRange range = new InlineStyleRange();
						range.setOffset(pos);
						range.setLength(range2.getOffset()-pos);
						range.setStyle("error");
						invers.add(range);
						System.out.println(range);	
					}
					range1 = range2;
				}
			}
			int endOfLastRange = range1.getOffset()+range1.getLength();
			if(endOfLastRange < blockLength){
				System.out.println("Ende");
				InlineStyleRange range = new InlineStyleRange();
				range.setOffset(endOfLastRange);
				range.setLength(blockLength - endOfLastRange);
				range.setStyle("error");
				invers.add(range);
				System.out.println(range);
			}
		}
		return invers;
	}
}
