package tree;

public class Node {

	public static final int ROOT = 0;

	public static final int ENTER = 1;

	public static final int EXIT = 2;

	private int previousOp = ROOT;

	private StringBuffer sb = new StringBuffer();

	public void enterNode(TreeNode node, TreeNode parent) {
		switch (previousOp) {
		case ROOT:
			sb = new StringBuffer();
			break;
		case EXIT:
			sb.append(",");
			break;
		case ENTER:
			break;
		}

		sb.append("{\"name\":\"" + node.nodeName() + "\",");

		if (parent != null)
			sb.append("\"parent\":\"" + parent.nodeName() + "\",");

		if (node instanceof TerminalTreeNode)
			sb.append("\"match\":\"" + getParsedToken((TerminalTreeNode) node) + "\",");

		sb.append("\"children\":[");

//		node.setParseTree(sb.toString());
		previousOp = ENTER;
	}

	public void exitNode(TreeNode node, TreeNode mother) {
		switch (previousOp) {
		case ROOT:
		case EXIT:
			sb.append("]}");
			break;
		case ENTER:
			sb.delete(sb.length() - 13, sb.length());
			sb.append("}");
			break;
		}

//		node.setParseTree(sb.toString());
		previousOp = EXIT;
	}

	private String getParsedToken(TerminalTreeNode node) {
		char ch;
		String res = "";

		for (int i = 0; i < node.getEndPos() - node.getStartPos(); i++) {
			ch = node.getSharedText().getRichChars().get(i + node.getStartPos()).ch;
			res = res + ch;
		}

		return res;
	}

}
