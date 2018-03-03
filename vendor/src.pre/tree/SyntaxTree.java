package tree;

public class SyntaxTree {

	public static void walker(Node iNode, TreeNode node, TreeNode mother) {
		if (node != null) {
			// entry
			iNode.enterNode(node, mother);

			// depth first
			TreeNode child = node.getChild();
			walker(iNode, child, node);

			// bredth second
			walker(iNode, node.suc, mother);

			// exit
			iNode.exitNode(node, mother);
		}
	}

}
