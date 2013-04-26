package com.github.catageek.BCProtect.Quadtree;


/**
 * A class that implements a 2D point
 */

class BCPoint2D implements Point2D, Cloneable {
	private int x, z;

	BCPoint2D(int x, int z) {
		this.x = x;
		this.z = z;
	}

	BCPoint2D(Point p) {
		this.x = p.getX();
		this.z = p.getZ();
	}

	/**
	 * @return the x
	 */
	@Override
	public int getX() {
		return x;
	}

	/**
	 * @return the z
	 */
	@Override
	public int getZ() {
		return z;
	}

	@Override
	public void setX(int x) {
		this.x = x;		
	}

	@Override
	public void setZ(int z) {
		this.z = z;
	}
	
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.z + ")";
	}
	
	@Override
	public Point2D clone() {
		Point2D p = null;
		try {
			p = (Point2D) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}

}
