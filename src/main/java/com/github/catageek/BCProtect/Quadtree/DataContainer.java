package com.github.catageek.BCProtect.Quadtree;


/**
 * Class implementing an object we can pass to the tree for storage
 */
final public class DataContainer {

	private Region region;

	public final DataContainer setRegion(Region region) {
		this.region = region;
		return this;
	}



	public final DataContainer setPoint(Point point) {
		this.point = point;
		return this;
	}

	private Point point;

	/**
	 * Initialize the cuboid with the first point
	 * 
	 * @param a first point
	 * @param link point to link with the cuboid
	 */
	public DataContainer(Region o, Point attach) {
		this.region = o.clone();
		this.point = attach.clone();
	}
	
	public DataContainer() {
	}



	public final Point getAttachedPoint() {
		return point;
	}

	/**
	 * @return the object
	 */
	public Region getRegion() {
		return region;
	}

	public Object getData() {
		return region.getData();
	}
}
