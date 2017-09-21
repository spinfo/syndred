package texts;

public class Shared {

	private Texts sharedText;

	public boolean available = false;

	public boolean backTrack = false;

	public Shared() {
		sharedText = new Texts();
	}

	public Texts getSharedText() {
		return sharedText;
	}

	public void setCharFromJson(RichChar richChar) {
		sharedText.setTextCharAtPos(richChar.ch, sharedText.getTextLen());
		sharedText.incTextLen();
		
		backTrack = (richChar.ch == '$');
		available = sharedText.getTextLen() > sharedText.getTextPos();
	}

	public synchronized char getSym() {
		try {
			while (!available)
				Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		available = false;

		char ch = sharedText.getTextCharAtPos(sharedText.getTextPos());
		sharedText.incTextPos();
		return ch;

	}

	public synchronized char getCharAtTextPos(int pos) {
		return (pos < sharedText.getTextLen()) ? sharedText.getTextCharAtPos(pos) : getSym();
	}
}