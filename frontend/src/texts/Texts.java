package texts;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Texts {

	public boolean eot;

	private char[] charr;

	private int length;

	private int position;

	private BufferedReader reader;

	public Texts() {
		charr = new char[10000];
		length = 0;
		position = 0;
	}

	public void incTextLen() {
		length++;
	}

	public int getTextLen() {
		return length;
	}

	public int getTextPos() {
		return position;
	}

	public void setTextPos(int pos) {
		position = pos;
	}

	public void incTextPos() {
		position++;
	}

	public char getTextChar() {
		return charr[position++];
	}

	public char getTextCharAtPos(int pos) {
		return charr[pos];
	}

	public void setTextCharAtPos(char ch, int pos) {
		charr[pos] = ch;
	}

	public void open(String fileName) {
		eot = false;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		try {
			reader = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(fileName)));
		} catch (Exception e) {
		}
	}

	public char readCharFromFile() {
		int val;

		try {
			val = reader.read();
			if (val != -1)
				return (char) val;
		} catch (Exception e) {
		}

		eot = true;
		return ' ';
	}

	public void readTextFromFile(String filename) {
		this.open(filename);
		while (true) {
			char ch = readCharFromFile();

			if (eot)
				break;

			charr[length] = ch;
			length++;
		}
	}

	public static char[] convertLongIntToCharArray(long l) {
		return null;
	}

}
