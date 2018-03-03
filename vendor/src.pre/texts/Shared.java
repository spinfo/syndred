package texts;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Shared {

	public final boolean backTrack = false;

	private static final ConcurrentMap<Thread, Texts> texts = new ConcurrentHashMap<Thread, Texts>();

	public Shared() {
	}

	public Shared(Thread thread) {
		System.out.println("~~~~~~~~~~ Shared(): " + Thread.currentThread().getName());
		texts.put(thread, new Texts());
	}

	public Texts getSharedText() {
		System.out.println("~~~~~~~~~~ getSharedText(): " + Thread.currentThread().getName());
		return texts.get(Thread.currentThread());
	}

	public Map<Thread, Texts> getSharedTexts() {
		return texts;
	}

	public synchronized RichChar getSym() {
		System.out.println("~~~~~~~~~~ getSym(): " + Thread.currentThread().getName());
		Texts text = getSharedText();

		try {
			while (text.getParsePos() == text.getTextLen())
				Thread.sleep(100);
		} catch (InterruptedException e) {
			return new RichChar('\0');
		}

		text.setMaxPos(-1);
		return text.getRichChar();
	}

	public boolean errorCase(int position) {
		// maxPosInParse = Math.max(position, texts.getParsePos());
		System.out.println("~~~~~~~~~~ errorCase(): " + Thread.currentThread().getName());

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		return false;
	}

}