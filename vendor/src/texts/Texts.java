package texts;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class Texts {

	public static String grammar = "1";

	public static String regex = "0";

	public static boolean ok = false;

	public boolean eot;

	private static List<RichChar> characters;

	private static List<Character> list;

	private static List<Character> listGrammar;

	private static List<Character> listRegex;

	private static int position;

	public static char[] convertLongIntToCharArray(long l) {
		return null;
	}

	public static char convertUnicode(String s) {
		return s.startsWith("\\u") ? (char) new BigInteger(s.substring(2), 16).intValue() : 0;
	}

	public Texts() {
		characters = new LinkedList<RichChar>();
		position = 0;
	}

	public int getParsePos() {
		return position;
	}

	public RichChar getRichChar() {
		return characters.get(position++);
	}

	public List<RichChar> getRichChars() {
		return characters;
	}

	public int getTextLen() {
		return characters.size();
	}

	public void setParsePos(int pos) {
		position = pos;
	}

	public void setRichChars(List<RichChar> chars) {
		characters = chars;
	}

	public void setGrammar(List<Character> grammar) {
		listGrammar = grammar;
	}

	public void setRegex(List<Character> regex) {
		listRegex = regex;
	}

	public void open(String s) {
		if (s == grammar)
			list = listGrammar;

		if (s == regex)
			list = listRegex;
	}

	public char readCharFromFile() {
		eot = list == null || list.isEmpty();
		return eot ? ' ' : list.remove(0);
	}

}
