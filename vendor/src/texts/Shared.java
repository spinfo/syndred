package texts;

import java.util.List;

public class Shared {

	public boolean backTrack;

	public static int maxPosInParse;

	public Texts sharedText;

	public Shared() {
		backTrack = false;
		maxPosInParse = -1;
		sharedText = new Texts();
	}

	public void setGrammar(List<Character> grammar) {
		sharedText.setGrammar(grammar);
	}

	public Texts getSharedText() {
		return sharedText;
	}

	public synchronized RichChar getSym() throws InterruptedException {
		while (sharedText.getParsePos() == sharedText.getTextLen())
			Thread.sleep(100);

		maxPosInParse = -1;
		return sharedText.getRichChar();
	}

	public boolean errorCase(int position) throws InterruptedException {
		List<RichChar> chars = sharedText.getRichChars();
		maxPosInParse = Math.max(position, sharedText.getParsePos());

		while (chars.equals(sharedText.getRichChars()))
			Thread.sleep(100);

		return false;
	}

}