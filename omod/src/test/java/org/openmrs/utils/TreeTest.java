package org.openmrs.utils;

import java.util.ArrayList;

import org.junit.Test;
import org.openmrs.module.dhis2dataagent.utils.Tree;
import org.openmrs.module.dhis2dataagent.utils.TreeNode;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TreeTest {
	
	@Test
	public void getPath_shouldReturnValidPath_test1() {
		// prepare
		Tree tree = new Tree("A", "A");
		
		String a = "this is. An example";
		String[] b = a.split("\\.");
		int c = b.length;
		if (!a.contains("\\.")) {
			System.out.print("good one");
		} else {
			System.out.print("oh oh!");
		}
		
		TreeNode childB = new TreeNode("B", "B", tree.root);
		TreeNode childC = new TreeNode("C", "C", tree.root);
		tree.root.addChild(childB);
		tree.root.addChild(childC);
		
		TreeNode childD = new TreeNode("D", "D", childB);
		childB.addChild(childD);
		
		// execute
		ArrayList<TreeNode> path = new ArrayList<TreeNode>();
		tree.getPath(childD, path);
		
		// assert
		assertThat(path.size(), is(3));
		assertThat(path.get(0).value.toString(), is("A"));
		assertThat(path.get(1).value.toString(), is("B"));
		assertThat(path.get(2).value.toString(), is("D"));
	}
	
	@Test
	public void getPath_shouldReturnValidPath_test2() {
		// prepare
		Tree tree = new Tree("A", "A");
		
		tree.insert("A", "B", "B");
		tree.insert("A", "C", "C");
		tree.insert("B", "D", "D");
		
		// execute
		ArrayList<TreeNode> path = new ArrayList<TreeNode>();
		tree.getPath(tree.find("D"), path);
		
		// assert
		assertThat(path.size(), is(3));
		assertThat(path.get(0).value.toString(), is("A"));
		assertThat(path.get(1).value.toString(), is("B"));
		assertThat(path.get(2).value.toString(), is("D"));
	}
	
	@Test
	public void getLeaves_shouldReturnValidLeaves() {
		// prepare
		Tree tree = new Tree("A", null);
		
		tree.insert("A", "B", "B");
		tree.insert("A", "C", "C");
		
		tree.insert("B", "1", "1");
		tree.insert("B", "2", "2");
		tree.insert("C", "3", "3");
		tree.insert("C", "4", "4");
		
		// execute
		ArrayList<TreeNode> leaves = tree.getLeaves();
		
		// assert
		assertThat(leaves.size(), is(4));
		assertThat(leaves.get(0).value.toString(), is("1"));
		assertThat(leaves.get(1).value.toString(), is("2"));
		assertThat(leaves.get(2).value.toString(), is("3"));
		assertThat(leaves.get(3).value.toString(), is("4"));
	}
}
