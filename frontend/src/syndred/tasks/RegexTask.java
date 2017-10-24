package syndred.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import syndred.entities.InlineStyleRange;
import texts.RichChar;

public class RegexTask extends Task {

	public RegexTask(String gramma) {
		super(gramma);
	}
	
	private int offset;

	@Override
	public void run() {
		String s = richCharListToString(characters);
		int offset = position;
		position = position + s.length();
		
		// Grammatik für die Fehler einfach umkehren? geht das? sinnvoll
		Pattern pattern  = Pattern.compile(gramma);
		
		Matcher matcher = pattern.matcher(s);
		List<InlineStyleRange> findings = new ArrayList<InlineStyleRange>();
		while(matcher.find()){
			findings.add(new InlineStyleRange(offset + matcher.start(), matcher.end()-matcher.start(),"success"));
		}
		errors = invertRanges(findings);
	}
	
	private String richCharListToString(List<RichChar> rcList){
		StringBuilder sb = new StringBuilder();
		for(RichChar rc : rcList){
			sb.append(rc.getCh());
		}
		return sb.toString();
	}
	
	private List<InlineStyleRange> invertRanges(List<InlineStyleRange> ranges){
		List<InlineStyleRange> invers = new ArrayList<InlineStyleRange>();
		Iterator<InlineStyleRange> iter = ranges.iterator();
		if(iter.hasNext()){
			InlineStyleRange range1 = iter.next();
			int off = range1.getOffset();
			if(off > offset){
				invers.add(new InlineStyleRange(offset, off-offset, "error"));
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
