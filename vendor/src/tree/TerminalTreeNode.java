package tree;

import texts.Texts;

public class TerminalTreeNode extends TreeNode {

	public int startPos;

	public int endPos;

	public Texts sharedText;

	public static TerminalTreeNode TerminalTreeNodeFactory(String name, Texts sharedText, int start, int end) {
		TerminalTreeNode node = new TerminalTreeNode();
		node.suc = null;
		node.name = name;
		node.startPos = start;
		node.endPos = end;
		node.sharedText = sharedText;

		return node;
	}

	public int getEndPos() {
		return endPos;
	}

	public Texts getSharedText() {
		return sharedText;
	}

	public int getStartPos() {
		return startPos;
	}

}
