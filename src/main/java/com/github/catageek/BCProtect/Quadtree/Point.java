package com.github.catageek.BCProtect.Quadtree;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * A class that implements a 3D point
 */

public class Point implements Cloneable{
	private int y;
	private Point2D point2d;

	public final Point2D getPoint2d() {
		return point2d;
	}

	public Point(int x, int y, int z) {
		this.point2d = new BCPoint2D(x, z);
		this.y = y;
	}
	
	public Point() {
		this.point2d = new BCPoint2D(0,0);
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return point2d.getX();
	}

	/**
	 * @return the z
	 */
	public int getZ() {
		return point2d.getZ();
	}

	public Point setX(int x) {
		this.point2d.setX(x);
		return this;
	}

	public Point setZ(int z) {
		this.point2d.setZ(z);
		return this;
	}

	public int getY() {
		return y;
	}

	public Point setY(int y) {
		this.y = y;
		return this;
	}
	
	public String toString() {
		return "(" + this.getX() + ", " + this.y + ", " + this.getZ() + ")";
	}
	
	public Point clone() {
		Point p = null;
		try {
			p = (Point) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.point2d = point2d.clone();
		return p;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(29,43).append(getX()).append(getY()).append(getZ()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof Point))
			return false;
		
		Point rhs = (Point) o;
		
		return new EqualsBuilder().append(getX(),rhs.getX()).append(getY(),rhs.getY()).append(getZ(),rhs.getZ()).isEquals();
	}

}
