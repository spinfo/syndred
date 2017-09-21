package syndred.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import syndred.Parser;
import syndred.draftjs.Block;
import syndred.draftjs.RawDraftContentState;
import texts.RichChar;

@Controller
public class TexteditorController {

	private Parser parser;

	private Integer length;

	private List<Block> blocks;

	public TexteditorController() {
		blocks = new ArrayList<Block>();
		parser = new Parser();
		parser.startThread();
	}

	@PostMapping(value = "/edit", produces = "application/json")
	public @ResponseBody RawDraftContentState edit(@RequestBody RawDraftContentState cs) throws Exception {
		length = 0;

		Block block;
		Iterator<Block> iter = cs.getBlocks().iterator();

		while (iter.hasNext() && (block = iter.next()) != null)
			block = parseBlock(block);

		blocks = cs.getBlocks();
		return cs;
	}

	private Block parseBlock(Block block) {
		return (blocks.stream().anyMatch(i -> i.getKey().equals(block.getKey()))) ? parseExistingBlock(block)
				: parseUnknownBlock(block);
	}

	private Block parseExistingBlock(Block block) {
		System.out.println(":::parseExistingBlock(" + block.getText() + ")");
		Optional<Block> first = blocks.stream().filter(i -> i.getKey().equals(block.getKey())).findFirst();
		Block existing = first.get();

		String s1 = existing.getText();
		String s2 = block.getText();

		if (s1.equals(s2))
			return block;

		length = 0;
		RichChar rch = new RichChar();
		List<Block> known = new ArrayList<Block>();

		for (Block b : blocks) {
			if (b.getKey().equals(existing.getKey()))
				break;

			known.add(b);
			length += b.getText().length();
		}

		System.out.println(":::BACKTRACK(" + length + ")");

		rch.ch = '$';
		blocks = known;
		parser.shared.getSharedText().setTextPos(length);
		parser.shared.setCharFromJson(rch);

		return parseUnknownBlock(block);
	}

	private Block parseUnknownBlock(Block block) {
		System.out.println(":::parseUnknownBlock(" + block.getText() + ")");
		int i = 0;
		RichChar rch = new RichChar();
		char[] text = block.getText().toCharArray();

		while (i < text.length && (rch.ch = text[i++]) != '0') {
			parser.shared.setCharFromJson(rch);
		}

		if (i == text.length && i > 0)
			block.setType("parsed");

		length += i;
		return block;
	}

}
