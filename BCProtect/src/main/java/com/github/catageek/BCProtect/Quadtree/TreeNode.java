package com.github.catageek.BCProtect.Quadtree;

/**
 * Interface representing a node of the tree
 */
interface TreeNode extends TreeChild {

	/**
	 * Add a branch
	 * 
	 * The leaf is replaced by a node
	 *
	 * @param leaf the leaf to replace
	 */
	void addBranch(Leaf leaf);

}
