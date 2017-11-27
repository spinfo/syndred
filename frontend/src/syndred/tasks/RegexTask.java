package syndred.tasks;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		System.out.println("RegexParse");
	Iterator<Block> iterator = blocks.iterator();
		Pattern pattern = Pattern.compile(parser.getGramma());
		while(iterator.hasNext()){
			Block b = iterator.next();
			Matcher matcher = pattern.matcher(b.getText());
			List<InlineStyleRange> findings = new ArrayList<InlineStyleRange>();
			while(matcher.find()){
				System.out.println("match");
				InlineStyleRange range = new InlineStyleRange();
				range.setOffset(matcher.start());
				range.setLength(matcher.end()-matcher.start());
				range.setStyle("success");
				findings.add(range);
			}
			List<InlineStyleRange> errors = invertRanges(findings, b.getText().length());
			System.out.println("Errors: " + errors.size());
			b.addInlineStyleRanges(errors);
			b.addInlineStyleRanges(findings);
		}
//		state.setBlocks(blocks);
		
		
		// _____ KOMPLEXE IMPlEMENTIERUNG UMFASST FOLGENDES:
		// aufteilen in ContentBlocks
		// Abgleich mit vorausgehenden Blocks (wo ist der gespeichert?)
		// RChar - List <- nicht mehr Teil des eigentlichen Interfaces, d. h. auch weglassbar
		// eigentl. Parsing
		// zurück in einen content-State
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File("test/regexReturn.json"), state);
		} catch (IOException e) {
			System.out.println("JSON-Generator-Error");
			e.printStackTrace();
		}
		return state;
	}

	private List<InlineStyleRange> invertRanges(List<InlineStyleRange> ranges, int blockLength){
		List<InlineStyleRange> invers = new ArrayList<InlineStyleRange>();
		Iterator<InlineStyleRange> iter = ranges.iterator();
		if(iter.hasNext()){
			InlineStyleRange range1 = iter.next();
			int off = range1.getOffset();
			if(off > 0){
				InlineStyleRange range = new InlineStyleRange();
				range.setOffset(0);
				range.setLength(off);
				range.setStyle("error");
				invers.add(range);
			}
			while(iter.hasNext()){
				InlineStyleRange range2 = iter.next();
				int pos = off+range1.getLength();
				if(pos < range2.getOffset()){
					InlineStyleRange range = new InlineStyleRange();
					range.setOffset(pos);
					range.setLength(range2.getOffset()-pos);
					range.setStyle("error");
					invers.add(range);
				}
				range1 = range2;
			}
			int endOfLastRange = range1.getOffset()+range1.getLength();
			if(endOfLastRange < blockLength){
				InlineStyleRange range = new InlineStyleRange();
				range.setOffset(endOfLastRange+1);
				range.setLength(blockLength - endOfLastRange);
				range.setStyle("error");
				invers.add(range);
			}
		}
		return invers;
	}

}
