package texts;

import java.util.List;

public final class Shared {

	public boolean backTrack = false;

	private Texts text = new Texts();

	public synchronized Texts getSharedText() {
		// System.out.println("~~~~~~~~~~ getSharedText(" +
		// Thread.currentThread().getId() + "@" + text + ")");
		return text;
	}

	public synchronized RichChar getSym() {
		Texts text = getSharedText();

		try {
			while (!Thread.interrupted() && text.getParsePos() == text.getTextLen()) {
				System.out.println("~~~~~~~~~~ getSym(" + Thread.currentThread().getId() + ")");
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			return new RichChar('\0');
		}

		text.setMaxPos(-1);
		return text.getRichChar();
	}

	public synchronized boolean errorCase(int position) {
		// System.out.println("~~~~~~~~~~ errorCase(" + Thread.currentThread().getId() +
		// "@" + text + ")");
		Texts text = getSharedText();
		List<RichChar> chars = text.getRichChars();
		text.setMaxPos(Math.max(position, text.getParsePos()));

		try {
			Thread.sleep(500);
		} catch (InterruptedException expected) {
		}

		return chars.equals(text.getRichChars());
	}

}