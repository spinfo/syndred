package texts;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Texts {

	public static final boolean ok = true;

	public boolean ebnfEot, eot;

	private static ThreadLocal<List<RichChar>> characters = new ThreadLocal<List<RichChar>>();

	private static ThreadLocal<List<Character>> grammar = new ThreadLocal<List<Character>>();

	private static ThreadLocal<Integer> maximum = new ThreadLocal<Integer>();

	private static ThreadLocal<Integer> position = new ThreadLocal<Integer>();

	public static char[] convertLongIntToCharArray(long l) {
		return null;
	}

	public static char convertUnicode(String s) {
		return s.startsWith("\\u") ? (char) new BigInteger(s.substring(2), 16).intValue() : 0;
	}

	public Texts() {
		characters.set("Hallo".chars().mapToObj(i -> {
			return new RichChar((char) i);
		}).collect(Collectors.toList()));
		System.out.println("~~~~~~~~~~ Texts(): " + Thread.currentThread().getName());
//		characters.set(new LinkedList<RichChar>());
		position.set(0);
	}

	public int getParsePos() {
		System.out.println("~~~~~~~~~~ getParsePos(): " + Thread.currentThread().getName());
		return position.get();
	}

	public void setParsePos(int pos) {
		// position = pos;
		System.out.println("~~~~~~~~~~ setParsePos(): " + Thread.currentThread().getName());
		position.set(pos);
	}

	public int getMaxPos() {
		System.out.println("~~~~~~~~~~ getMaxPos(): " + Thread.currentThread().getName());
		return maximum.get();
	}

	public void setMaxPos(int pos) {
		// maximum = pos;
		System.out.println("~~~~~~~~~~ setMaxPos(): " + Thread.currentThread().getName());
		maximum.set(pos);
	}

	public RichChar getRichChar() {
		// return characters.get(position++);
		System.out.println("~~~~~~~~~~ getRichChar(): " + Thread.currentThread().getName());
		int pos = position.get() + 1;
		position.set(pos);
		return characters.get().get(pos);
	}

	public List<RichChar> getRichChars() {
		System.out.println("~~~~~~~~~~ getRichChars(): " + Thread.currentThread().getName());
		return characters.get();
	}

	public void setRichChars(List<RichChar> chars) {
		// characters = chars;
		System.out.println("~~~~~~~~~~ setRichChars(): " + Thread.currentThread().getName());
		characters.set(chars);
	}

	public int getTextLen() {
		System.out.println("~~~~~~~~~~ getTextLen(): " + Thread.currentThread().getName());
		return characters.get().size();
	}

	public void setGrammar(List<Character> list) {
		System.out.println("~~~~~~~~~~ setGrammar(): " + Thread.currentThread().getName());
		// grammar = list;
		grammar.set(list);
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
