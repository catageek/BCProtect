package com.github.catageek.BCProtect.Quadtree;

import com.github.catageek.BCProtect.BCProtect;


/**
 * A class implementing a leaf of the tree
 */
final class Leaf implements TreeChild {
	
	private final RegionList contentlist;
	private TreeNode parent;
	
	Leaf(TreeNode n) {
		contentlist = new RegionList();
		parent = n;
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " Creating child leaf of " + n.toString());
//		count++;
	}
	
	/**
	 * @param contentlist the content to set
	 */
	@Override
	public void setContent(Region object) {
		if (! this.addObject(object)) {
			this.explodeLeaf();
		}
/*		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " count of leaves " + count);
*/	}
	
	private boolean addObject(Region object) {
		this.getContent().getContent().add(object);
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " Adding region " + object.toString());
		return (this.getContent().getContent().size() <= BCProtect.MaxListSize);
	}

	
	private void explodeLeaf() {
		parent.addBranch(this);
//		count--;
	}

	/**
	 * @return the list
	 */
	@Override
	public RegionList getContent(double x, double z) {
		return getContent();
	}
	
	RegionList getContent() {
		return contentlist;
	}

}
