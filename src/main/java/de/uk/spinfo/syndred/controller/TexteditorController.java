package de.uk.spinfo.syndred.controller;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import de.uk.spinfo.syndred.draftjs.Block;
import de.uk.spinfo.syndred.draftjs.InlineStyleRange;
import de.uk.spinfo.syndred.draftjs.RawDraftContentState;

@Controller
public class TexteditorController {

	@PostMapping(value = "/edit", produces = "application/json")
	public @ResponseBody RawDraftContentState edit(@RequestBody RawDraftContentState cs) throws Exception {
		Block block;
		Iterator<Block> iter = cs.getBlocks().iterator();

		while (iter.hasNext() && (block = iter.next()) != null) {
			List<InlineStyleRange> styles = block.getInlineStyleRanges().stream()
					.filter(i -> i.getStyle().equals("PARSED")).collect(Collectors.toList());

			InlineStyleRange style = new InlineStyleRange();
			style.setOffset(0);
			style.setLength(block.getText().length() - 1);
			style.setStyle("PARSED");

			styles.add(style);
			block.setInlineStyleRanges(styles);
		}

		return cs;
	}

}
