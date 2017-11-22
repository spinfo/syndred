package syndred.tasks;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import syndred.entities.Block;
import syndred.entities.InlineStyleRange;
import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;
import texts.RichChar;

public class RegexTask extends Task {

	public RegexTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) {
		super(input, output, parser);
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		List<Block> blocks = state.getBlocks();
		Pattern pattern = Pattern.compile(parser.getGramma());
		for(Block b : blocks){
			Matcher matcher = pattern.matcher(b.getText());
			List<InlineStyleRange> findings = new ArrayList<InlineStyleRange>();
			while(matcher.find()){
				findings.add(new InlineStyleRange(matcher.start(), matcher.end()-matcher.start(),"success"));
			}
			List<InlineStyleRange> errors = invertRanges(findings);
			
			b.addInlineStyleRanges(errors);
			b.addInlineStyleRanges(findings);
		}
		state.setBlocks(blocks);
		
		
		// _____ KOMPLEXE IMPlEMENTIERUNG UMFASST FOLGENDES:
		// aufteilen in ContentBlocks
		// Abgleich mit vorausgehenden Blocks (wo ist der gespeichert?)
		// RChar - List <- nicht mehr Teil des eigentlichen Interfaces, d. h. auch weglassbar
		// eigentl. Parsing
		// zurück in einen content-State
		return state;
	}

	private List<InlineStyleRange> invertRanges(List<InlineStyleRange> ranges){
		List<InlineStyleRange> invers = new ArrayList<InlineStyleRange>();
		Iterator<InlineStyleRange> iter = ranges.iterator();
		if(iter.hasNext()){
			InlineStyleRange range1 = iter.next();
			int off = range1.getOffset();
			if(off > 0){
				invers.add(new InlineStyleRange(0, off, "error"));
			}
			while(iter.hasNext()){
				InlineStyleRange range2 = iter.next();
				int pos = off+range1.getLength();
				if(pos < range2.getOffset()){
					invers.add(new InlineStyleRange(pos, range2.getOffset()-pos, "error"));
				}
				range1 = range2;
			}
		}
		return invers;
	}

}
