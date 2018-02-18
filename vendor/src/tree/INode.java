package tree;

import CP.SyntaxTree.SyntaxTree_TreeNode;

public interface INode {

	public void enterNode(SyntaxTree_TreeNode node, SyntaxTree_TreeNode mother);

	public void exitNode(SyntaxTree_TreeNode node, SyntaxTree_TreeNode mother);

}
