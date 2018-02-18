package texts;

import java.util.Arrays;

public class RichChar {

	public char ch;

	public char color[], size[], style[], typeface[], weight[];

	public RichChar() {
	}

	public RichChar(char c) {
		ch = c;
	}

	@Override
	public boolean equals(Object obj) {
		boolean equals = true;
		RichChar rch = (RichChar) obj;

		equals = equals && ch == rch.ch;
		equals = equals && Arrays.equals(color, rch.color);
		equals = equals && Arrays.equals(size, rch.size);
		equals = equals && Arrays.equals(style, rch.style);
		equals = equals && Arrays.equals(typeface, rch.typeface);
		equals = equals && Arrays.equals(weight, rch.weight);

		return equals;
	}

	public boolean color(char[] test) {
		return match(color, test);
	}

	public boolean size(char[] test) {
		return match(size, test);
	}

	public boolean style(char[] test) {
		return match(style, test);
	}

	public boolean typeface(char[] test) {
		return match(typeface, test);
	}

	public boolean weight(char[] test) {
		return match(weight, test);
	}

	private boolean match(char[] java, char[] cp) {
		return java == null || java.length == 0 || cp == null || cp.length == 0 ? false
				: Arrays.equals(Arrays.copyOfRange(cp, 0, cp.length - 1), java);
	}

}
