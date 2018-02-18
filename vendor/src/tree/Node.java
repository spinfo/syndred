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

	public void clearString() {
		sb = new StringBuffer();
	}

	public String getString() {
		return sb.toString();
	}

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
//		System.out.println("in EnterNode");

		switch (previousOp) {
		case ROOT:
			sb = new StringBuffer();
			break;
		case EXIT:
			// previous node is a sibling or a superior node
			sb.append("\n,");
			break;
		case ENTER:
			// previous node is a parent node
			break;
		}

		sb.append("{" + "\n" + "\"" + "name" + "\"" + ": " + node.nodename() + "),");
//		System.out.println("{" + "\n" + "\"" + "name" + "\"" + ": " + node.nodename() + "),");

		if (parent != null) {

			sb.append("\n" + "\"" + "parent" + "\"" + ": " + parent.nodename() + "),");
//			System.out.println("\n" + "\"" + "parent" + "\"" + ": " + parent.nodename() + "),");

		}
		if (node instanceof SyntaxTree_TerminalTreeNode) {
			tNode = (SyntaxTree_TerminalTreeNode) node;
			String parsedToken = getParsedToken(tNode);
			sb.append("\n" + "\"" + "match" + "\"" + ": " + parsedToken + "),");
//			System.out.println("\n" + "\"" + "match" + "\"" + ": " + parsedToken + "),");
		}

		sb.append("\n" + "\"" + "children" + "\"" + ": [");
//		System.out.println("\n" + "\"" + "children" + "\"" + ": [");

		previousOp = Status.ENTER;
		resultString = getString();
	}

	public void exitNode(SyntaxTree_TreeNode node, SyntaxTree_TreeNode mother) {
//		System.out.println("in ExitNode");

		switch (previousOp) {
		case ROOT:
		case EXIT:
			// node had children
			sb.append("\n" + "]" + "\n" + "}");
//			System.out.println("]" + "\n" + "}");
			break;
		case ENTER:
			// node has no children
			// delete children's field
			sb.delete(sb.length() - 14, sb.length());
			sb.append("\n" + "}");
//			System.out.println("\n" + "}");
			break;
		}

		previousOp = Status.EXIT;
		resultString = getString();
	}

}
