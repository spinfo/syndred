package tree;

public class NonTerminalTreeNode extends TreeNode {

	public TreeNode child;

	public static NonTerminalTreeNode NonTerminalTreeNodeFactory(String name) {
		NonTerminalTreeNode node = new NonTerminalTreeNode();
		node.child = null;
		node.name = name;
		node.suc = null;

		return node;
	}

}
