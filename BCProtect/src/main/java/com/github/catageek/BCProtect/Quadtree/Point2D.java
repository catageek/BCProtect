package com.github.catageek.BCProtect.Quadtree;


/**
 * an interface that represents a 2D point
 */
public interface Point2D {
	public int getX();
	public int getZ();
	void setX(int x);
	void setZ(int z);
	public Point2D clone();
}
