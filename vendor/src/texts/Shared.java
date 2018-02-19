package texts;

import java.util.List;

import CP.Ebnf.Ebnf;
import tree.Node;
import tree.SyntaxTree;

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

	public void setRegex(List<Character> regex) {
		sharedText.setRegex(regex);
	}

	public Texts getSharedText() {
		return sharedText;
	}

	public synchronized RichChar getSym() {
		try {
			while (sharedText.getParsePos() == sharedText.getTextLen())
				Thread.sleep(100);
		} catch (InterruptedException e) {
			return new RichChar('\0');
		}

		maxPosInParse = -1;
		SyntaxTree.walker(new Node(), Ebnf.root, null);
		return sharedText.getRichChar();
	}

	public boolean errorCase(int position) {
		maxPosInParse = Math.max(position, sharedText.getParsePos());

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		return false;
	}

}