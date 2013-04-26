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
	
	public boolean isCollidingXAxis(int z);
	public boolean isCollidingZAxis(int x);
	public Iterator<Point> getPointIterator();
	public Object getData();
	public int getWeight();
	public void nullData();
	public Region clone();
}
