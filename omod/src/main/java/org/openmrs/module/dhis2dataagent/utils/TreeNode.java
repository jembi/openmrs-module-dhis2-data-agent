package org.openmrs.module.dhis2dataagent.utils;

import java.util.ArrayList;

public class TreeNode {
	
	public String key;
	
	public Object value;
	
	public TreeNode parent;
	
	ArrayList<TreeNode> children;
	
	public TreeNode(String key, Object value, TreeNode parent) {
		this.key = key;
		this.value = value;
		this.parent = parent;
		this.children = new ArrayList<TreeNode>();
	}
	
	public boolean isRoot() {
		return this.parent == null;
	}
	
	public boolean isLeaf() {
		return this.children.size() == 0;
	}
	
	public boolean hasChildren() {
		return !isLeaf();
	}
	
	public void addChild(String key, Object value) {
		TreeNode newChild = new TreeNode(key, value, this);
		this.children.add(newChild);
	}
	
	public void addChild(TreeNode newChild) {
		this.children.add(newChild);
	}
}
