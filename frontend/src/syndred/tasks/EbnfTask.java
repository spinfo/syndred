package syndred.tasks;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

import syndred.entities.Block;
import syndred.entities.InlineStyleRange;
import syndred.entities.Parser;
import syndred.entities.RawDraftContentState;
import texts.RichChar;
import texts.Shared;

import CP.Ebnf.Ebnf;
import ebnf.EbnfParser;

public class EbnfTask extends Task {
	
	private Shared shared;
	private EbnfParser ebnfParser;
	private List<Block> prevBlocks;
	private Integer length;
	

	public EbnfTask(BlockingQueue<RawDraftContentState> input, Function<RawDraftContentState, Exception> output,
			Parser parser) {
		super(input, output, parser);
		
		this.shared = new Shared();
		
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		
		// _____ KOMPLEXE IMPlEMENTIERUNG UMFASST FOLGENDES:
		// aufteilen in ContentBlocks
		// Abgleich mit vorausgehenden Blocks (wo ist der gespeichert?)
		// RChar - List <- nicht mehr Teil des eigentlichen Interfaces, d. h. auch weglassbar
		// eigentl. Parsing
		// zurück in einen content-State
		
	
		
//		List<RichChar> rchList = new ArrayList<RichChar>();
		length = 0;
		
		List<Block> currBlocks = state.getBlocks();
		Iterator<Block> iter = currBlocks.iterator();
		Block block;	
				
		while (iter.hasNext() && (block = iter.next()) != null) {
			block = parseBlock(block);
			
			prevBlocks = state.getBlocks();

		}
		
		// Kontrollausgabe in json-Datei
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(new File("test/regexReturn.json"), state);
		} catch (IOException e) {
			System.out.println("JSON-Generator-Error");
			e.printStackTrace();
		}
		return state;
	}

private Block parseBlock(Block block) {
	if (prevBlocks.stream().anyMatch(i -> i.getKey().equals(block.getKey()))) {
		return parseExistingBlock(block);
	} else {
		return parseUnknownBlock(block);
	}
}

private Block parseExistingBlock(Block block) {
	System.out.println("====================> parseExistingBlock(" + block.getText() + ")");
	Optional<Block> first = prevBlocks.stream().filter(i -> i.getKey().equals(block.getKey())).findFirst();
	Block existing = first.get();

	String s1 = existing.getText();
	String s2 = block.getText();

	if (s1.equals(s2))
		return block;

	if (s1.isEmpty() && !s2.isEmpty())
		return parseUnknownBlock(block);

	block.setType(null);
	
	length = 0;
	RichChar rch = new RichChar();
	List<Block> known = new ArrayList<Block>();

	for (Block b : prevBlocks) {
		if (b.getKey().equals(existing.getKey()))
			break;

		known.add(b);
		length += b.getText().length();
	}

	System.out.println("====================> BACKTRACK(" + length + ")");

	rch.ch = '$';
	prevBlocks = known;
	ebnfParser.shared.getSharedText().setTextPos(length);
	ebnfParser.shared.setCharFromJson(rch);

	return parseUnknownBlock(block);
}

private Block parseUnknownBlock(Block block) {
	System.out.println("====================> parseUnknownBlock(" + block.getText() + ")");
	int i = 0;
	RichChar rch = new RichChar();
	char[] text = block.getText().toCharArray();
	ebnfParser.parsed = false;

	while (i < text.length && (rch.ch = text[i++]) != '0') {
		ebnfParser.shared.setCharFromJson(rch);
		try {
			while (ebnfParser.shared.available)
				Thread.sleep(100);
		} catch (Exception e) {
		}
	}

	if (i == text.length && i > 0 && ebnfParser.parsed)
		block.setType("parsed");

	length += i;
	return block;
}

}
