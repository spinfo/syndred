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

import com.fasterxml.jackson.databind.ObjectMapper;

import syndred.entities.Block;
import syndred.entities.InlineStyleRange;
import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;

public class RegexTask extends Task {

	public RegexTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) {
		super(input, output, parser);
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		System.out.println("RegexParse");
		List<Block> blocks = state.getBlocks();
		
	Iterator<Block> iterator = blocks.iterator();
		Pattern pattern = Pattern.compile(parser.getGramma());
		while(iterator.hasNext()){
			Block b = iterator.next();
//			b.setInlineStyleRanges(new ArrayList<InlineStyleRange>());
			b.setInlineStyleRanges(removeParseRanges(b.getInlineStyleRanges()));
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
//			InlineStyleRange styleBuffer = new InlineStyleRange();
//			styleBuffer.setOffset(b.getText().length());
//			styleBuffer.setLength(1);
//			styleBuffer.setStyle("unstyled");
//			b.addInlineStyleRange(styleBuffer);
		}
//		state.setBlocks(blocks);
		
		// Kontrollausgabe in json-Datei
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File("test/regexReturn.json"), state);
//			mapper.writeValue(new File("test/regexReturn.json"), state);
		} catch (IOException e) {
			System.out.println("JSON-Generator-Error");
			e.printStackTrace();
		}
		return state;
	}


}
