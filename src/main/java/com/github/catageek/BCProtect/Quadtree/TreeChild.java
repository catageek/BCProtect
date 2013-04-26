package com.github.catageek.BCProtect.Quadtree;


/**
 * Interface representing a member of the tree
 */
interface TreeChild {
	/**
	 * Get the content of the tree at point (x,z)
	 *
	 * @param x coordinate x of the point
	 * @param z coordinate z of the point
	 * @return the content
	 */
	RegionList getContent(double x, double z);
	
	/**
	 * Store an object in the tree
	 *
	 * @param content the content to store
	 */
	public void setContent(Region content);

	
}
