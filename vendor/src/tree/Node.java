package tree;

import CP.SyntaxTree.SyntaxTree_TerminalTreeNode;
import CP.SyntaxTree.SyntaxTree_TreeNode;

public class Node implements INode {

	public enum Status {
		ROOT, ENTER, EXIT
	};

	public static String resultString;

	private StringBuffer sb = new StringBuffer();

	private Status previousOp = Status.ROOT;

	private String getParsedToken(SyntaxTree_TerminalTreeNode node) {
		String res = "";
		char ch;
		for (int i = 0; i < (node.getEndPos() - node.getStartPos()); i++) {
			ch = node.getSharedText().getRichChars().get(i + node.getStartPos()).ch;
			res = res + ch;
		}
		return res;
	}

	public void enterNode(SyntaxTree_TreeNode node, SyntaxTree_TreeNode parent) {
		SyntaxTree_TerminalTreeNode tNode = null;

		switch (previousOp) {
		case ROOT:
			sb = new StringBuffer();
			break;
		case EXIT:
			// previous node is a sibling or a superior node
			sb.append(",");
			break;
		case ENTER:
			// previous node is a parent node
			break;
		}

		sb.append("{\"name\":\"" + node.nodename() + "\",");

		if (parent != null) {
			sb.append("\"parent\":\"" + parent.nodename() + "\",");
		}
		if (node instanceof SyntaxTree_TerminalTreeNode) {
			tNode = (SyntaxTree_TerminalTreeNode) node;
			String parsedToken = getParsedToken(tNode);
			sb.append("\"match\":\"" + parsedToken + "\",");
		}

		sb.append("\"children\":[");

		previousOp = Status.ENTER;
		resultString = sb.toString();
	}

	public void exitNode(SyntaxTree_TreeNode node, SyntaxTree_TreeNode mother) {
		switch (previousOp) {
		case ROOT:
		case EXIT:
			// node had children
			sb.append("]}");
			break;
		case ENTER:
			// node has no children, delete children's field
			sb.delete(sb.length() - 13, sb.length());
			sb.append("}");
			break;
		}

		previousOp = Status.EXIT;
		resultString = sb.toString();
	}

}
