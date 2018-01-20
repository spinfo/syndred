package texts;

import java.util.LinkedList;
import java.util.List;

public class Texts {

	public boolean eot;

	private static List<Character> grammar;

	private static List<RichChar> characters;

	private static int position;

	public Texts() {
		characters = new LinkedList<RichChar>();
		position = 0;
	}

	public int getParsePos() {
		return position;
	}

	public void setParsePos(int pos) {
		position = pos;
	}

	public RichChar getRichChar() {
		return characters.get(position++);
	}

	public void setRichChars(List<RichChar> chars) {
		characters = chars;
	}

	public List<RichChar> getRichChars() {
		return characters;
	}

	public int getTextLen() {
		return characters.size();
	}

	public char[] convertLongIntToCharArray(long l) {
		return null;
	}

	public void open(String s) {
	}

	public char readCharFromFile() {
		eot = grammar.isEmpty();
		return eot ? ' ' : grammar.remove(0);
	}

	public void setGrammar(List<Character> value) {
		grammar = value;
	}

}
