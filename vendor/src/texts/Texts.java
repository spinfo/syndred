package texts;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class Texts {

	public static boolean ok = !false;

	public boolean ebnfEot, eot;

	private static final ThreadLocal<List<Character>> grammar = new ThreadLocal<List<Character>>();

	private List<RichChar> characters = new LinkedList<RichChar>();

	private int maximum;

	private int position;

	public static char[] convertLongIntToCharArray(long l) {
		return null;
	}

	public static char convertUnicode(String s) {
		return s.startsWith("\\u") ? (char) new BigInteger(s.substring(2), 16).intValue() : 0;
	}

	public int getParsePos() {
		return position;
	}

	public void setParsePos(int pos) {
		position = pos;
	}

	public int getMaxPos() {
		return maximum;
	}

	public void setMaxPos(int pos) {
		maximum = pos;
	}

	public RichChar getRichChar() {
		return characters.get(position++);
	}

	public List<RichChar> getRichChars() {
		return characters;
	}

	public void setRichChars(List<RichChar> chars) {
		characters = chars;
	}

	public int getTextLen() {
		return characters.size();
	}

	public void setGrammar(List<Character> list) {
		grammar.set(list);
	}

	public boolean idle() {
		return maximum < 0 && position < characters.size();
	}

	public void open(String s) {
	}

	public void openEbnf() {
	}

	public char readCharFromFile() {
		return '\0';
	}

	public char readEbnfChar() {
		ebnfEot = grammar.get().isEmpty();
		return ebnfEot ? ' ' : grammar.get().remove(0);
	}

}
