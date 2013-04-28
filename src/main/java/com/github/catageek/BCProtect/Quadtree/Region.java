package com.github.catageek.BCProtect.Quadtree;

import java.util.Iterator;


/**
 * An interface that represents a region
 */
public interface Region {
	
	/**
	 * Check if a point is inside a region
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 * @return true if the point is inside the region
	 */

	boolean isInsideRegion(double px, double py, double pz);
	
	/**
	 * Tell if the shape is crossing a specific vertical axis
	 *
	 * @param z the position of the axis
	 * @return true is the shape is crossing this axis
	 */
	public boolean isCollidingXAxis(int z);

	/**
	 * Tell if the shape is crossing a specific horizontal axis
	 *
	 * @param x the position of the axis
	 * @return true is the shape is crossing this axis
	 */
	public boolean isCollidingZAxis(int x);
	
	
	/**
	 * Returns an iterator on the points of the shape
	 *
	 * @return the iterator
	 */
	public Iterator<Point> getPointIterator();
	
	/**
	 * Retrieve the data we want to associate with this region 
	 *
	 *
	 * @return the data
	 */
	public Object getData();
	
	/**
	 * The weight used to compute the center of the tree. Pratically, it is the volume
	 *
	 *
	 * @return
	 */
	public int getWeight();
	
	/**
	 * Nullify the data
	 *
	 *
	 */
	public void nullData();
	
	/**
	 * Clone the region
	 *
	 *
	 * @return
	 */
	public Region clone();
}
