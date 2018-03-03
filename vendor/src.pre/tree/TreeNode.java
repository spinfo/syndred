package tree;

public abstract class TreeNode {

	public String name;

	public TreeNode suc;

	public String nodeName() {
		return name;
	}

	public TreeNode getChild() {
		return this instanceof NonTerminalTreeNode ? ((NonTerminalTreeNode) this).child : null;
	}

	public boolean isTerminalTreeNode() {
		return this instanceof NonTerminalTreeNode;
	}

}
