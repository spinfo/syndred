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
		prevBlocks = new ArrayList<Block>();
		ebnfParser = new EbnfParser();
		ebnfParser.startThread();
		
	}

	@Override
	protected RawDraftContentState parse(RawDraftContentState state) throws ParseException {
		
		
		System.out.println("ebnf parse");
		// _____ KOMPLEXE IMPlEMENTIERUNG UMFASST FOLGENDES:
		// aufteilen in ContentBlocks
		// Abgleich mit vorausgehenden Blocks (wo ist der gespeichert?)
		// RChar - List <- nicht mehr Teil des eigentlichen Interfaces, d. h. auch weglassbar
		// eigentl. Parsing
		// zurück in einen content-State
		
		length = 0;
		
		List<Block> currBlocks = state.getBlocks();
		Iterator<Block> iter = currBlocks.iterator();
		Block block;	
				
		System.out.println("no. of blocks:" + currBlocks.size());
		if(currBlocks.size() >= 1 && currBlocks.get(0).getText().length() > 0){
			while (iter.hasNext() && (block = iter.next()) != null) {
				System.out.println("parse block: " + block.getText());
				block = parseBlock(block);
				
				prevBlocks = state.getBlocks();
			}
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
	System.out.println("parseBlock");
	if (prevBlocks.stream().anyMatch(i -> i.getKey().equals(block.getKey()))) {
		System.out.println("to parseExistingBlock");
		return parseExistingBlock(block);
	} else {
		System.out.println("to parseUnknownBlock");
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
	
	// Erfolg abfragen
	if (i == text.length && i > 0 && ebnfParser.parsed){
		InlineStyleRange range = new InlineStyleRange();
		range.setOffset(0);
		range.setLength(i);
		range.setStyle("success");
		block.addInlineStyleRange(range);
		// Fehler 
	} else if(i == text.length && !ebnfParser.parsed){
		InlineStyleRange successRange = new InlineStyleRange();
		successRange.setOffset(0);
		successRange.setLength(i);
		successRange.setStyle("success");
		block.addInlineStyleRange(successRange);
		InlineStyleRange errorRange = new InlineStyleRange();
		errorRange.setOffset(i+1);
		errorRange.setLength(text.length-i+1);
		errorRange.setStyle("error");
		block.addInlineStyleRange(errorRange);
	}
		

	length += i;
	return block;
}

}
