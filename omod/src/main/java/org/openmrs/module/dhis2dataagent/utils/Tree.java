package org.openmrs.module.dhis2dataagent.utils;

import java.util.ArrayList;

public class Tree {
	
	public TreeNode root;
	
	public Tree(String key, Object value) {
		this.root = new TreeNode(key, value, null);
	}
	
	public void getPath(TreeNode node, ArrayList<TreeNode> path) {
		
		if (node.isRoot()) {
			path.add(node);
			return;
		} else {
			getPath(node.parent, path);
			path.add(node);
			
		}
	}
	
	public ArrayList<TreeNode> getLeaves() {
		ArrayList<TreeNode> allNodes = new ArrayList<TreeNode>();
		preOrderTraversal(this.root, allNodes);
		
		ArrayList<TreeNode> leaves = new ArrayList<TreeNode>();
		for (TreeNode node : allNodes) {
			if (node.isLeaf()) {
				leaves.add(node);
			}
		}
		
		return leaves;
	}
	
	void preOrderTraversal(TreeNode node, ArrayList<TreeNode> nodes) {
		nodes.add(node);
		
		if (node.hasChildren()) {
			for (TreeNode child : node.children) {
				preOrderTraversal(child, nodes);
			}
		}
	}
	
	void postOrderTraversal(TreeNode node, ArrayList<TreeNode> nodes) {
		if (node.hasChildren()) {
			for (TreeNode child : node.children) {
				preOrderTraversal(child, nodes);
			}
		}
		
		nodes.add(node);
	}
	
	public void insert(String parentNodeKey, String key, Object value) {
		// if (value == null)
		// 	value = key;
		
		ArrayList<TreeNode> allNodes = new ArrayList<TreeNode>();
		preOrderTraversal(this.root, allNodes);
		
		for (TreeNode parentNode : allNodes) {
			if (parentNode.key.equals(parentNodeKey)) {
				TreeNode newChild = new TreeNode(key, value, parentNode);
				parentNode.children.add(newChild);
				return;
			}
		}
	}
	
	public TreeNode find(String key) {
		
		ArrayList<TreeNode> allNodes = new ArrayList<TreeNode>();
		preOrderTraversal(this.root, allNodes);
		
		for (TreeNode node : allNodes) {
			if (node.key.equals(key)) {
				return node;
			}
		}
		
		return null;
	}
}
