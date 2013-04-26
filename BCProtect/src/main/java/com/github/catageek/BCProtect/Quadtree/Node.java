package com.github.catageek.BCProtect.Quadtree;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.github.catageek.BCProtect.BCProtect;


/**
 * A class implementing a node of the tree
 */
final class Node implements Parent, TreeNode, TreeChild {

	enum Quadrant {
		NW(0, 1, -1),
		NE(1, -1, -1),
		SW(2, -1, 1),
		SE(3, 1, 1);

		final int index, dx, dz;

		Quadrant(int i, int deltax, int deltaz) {
			index = i;
			dx = deltax;
			dz = deltaz;
		}
	}

	/**
	 *  The head of the tree
	 */
	private Parent parent;

	/**
	 * The 4 branches of the node
	 */
	private final TreeChild[] quadrants = new TreeChild[4];

	/**
	 * The center of the node
	 */
	private final Point2D center;

	private final Point2D getCenter() {
		return center;
	}

	/**
	 * the size of each leaf
	 */
	private final int size;

	Node(Point2D p, int size, Parent parent) {
		center = p;
		this.size = size;
		this.parent = parent;
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " Creating " + this.toString());
	}

	/* (non-Javadoc)
	 * @see com.github.catageek.BCProtect.Quadtree.TreeChild#getContent(double, double)
	 */
	@Override
	public RegionList getContent(double x, double z) {
		Quadrant q = getQuadrant(x, z);
		int index = q.index;
/*
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " Found quadrant " + q + " of " + this.toString() + " for (" + x + "," + z + ")");
*/
		TreeChild tm = quadrants[index];
		if (tm == null)
			return null;
		return tm.getContent(x, z);
	}

	/* (non-Javadoc)
	 * @see com.github.catageek.BCProtect.Quadtree.TreeChild#setContent(com.github.catageek.BCProtect.Quadtree.DefaultContent)
	 */
	@Override
	public void setContent(Region content) {
		List<Quadrant> q;
		Iterator<Point> it = content.getPointIterator();

		if (content.isCollidingXAxis(getCenter().getZ()) && content.isCollidingZAxis(getCenter().getX())) {
			// 2 axis crossed, we update all quadrants
			q = Arrays.asList(Quadrant.values());
		}
		else {
			q = new LinkedList<Quadrant>();
			if (content.isCollidingXAxis(getCenter().getZ()) ^ content.isCollidingZAxis(getCenter().getX())){
				// only 1 axis is crossed so we add simply all points
				while (it.hasNext())
					q.add(getQuadrant(it.next().getPoint2d()));
			}
			else
				// no axis crossed, adding the first point is sufficient
				if (it.hasNext())
					q.add(getQuadrant(it.next().getPoint2d()));
		}

		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " Found " + q.size() + " quadrants to update at " + this.toString());

		Iterator<Quadrant> itq = q.iterator();
		while (itq.hasNext()) {
			Quadrant quad = itq.next();
			int index = quad.index;
			if (BCProtect.debugQuadtree)
				BCProtect.log.info(BCProtect.logPrefix + " Setting content " + quad + " at " + this.toString());

			if(quadrants[index] == null)
				quadrants[index] = new Leaf(this);

			synchronized(quadrants[index]) {
				TreeChild tm = quadrants[index];
				tm.setContent(content);
			}
		}
	}


	/**
	 * Get the quadrant containing the point
	 *
	 * @param point the point
	 * @return the quadrant
	 */
	private final Quadrant getQuadrant(Point2D point) {
		int x = point.getX();
		int z = point.getZ();
		return getQuadrant(x,z);
	}

	/**
	 * Get the quadrant containing the point with coordinates (x,z)
	 *
	 * @param x X coordinate of the point
	 * @param y Y coordinate of the point
	 * @return the quadrant
	 */
	private final Quadrant getQuadrant(double x, double z) {
		int pz = center.getZ();
		if (x < center.getX()) {
			if (z < pz)
				return Quadrant.NW;
			return Quadrant.SW;
		}
		if (z < pz)
			return Quadrant.NE;
		return Quadrant.SE;
	}

	/**
	 * Get the quadrant containing a specific member
	 *
	 * @param tm the member to search
	 * @return the quadrant
	 */
	private final Quadrant getQuadrant(TreeChild tm) {
		for (Quadrant q : Quadrant.values()) {
			if (tm == quadrants[q.index])
				return q;
		}

		return null;

	}


	/* (non-Javadoc)
	 * @see com.github.catageek.BCProtect.Quadtree.TreeNode#addBranch(com.github.catageek.BCProtect.Quadtree.Leaf)
	 */
	@Override
	public void addBranch(Leaf leaf) {
		Quadrant quadrant = getQuadrant(leaf);
		if (quadrant == null)
			return;
		int index = quadrant.index;
		int size = getSize() >> 1;
		// we do not make a leaf smaller than this limit
		if (size < BCProtect.minLeaf)
			return;

		// replace leaf by a node
		int cx = center.getX() + size*quadrant.dx;
		int cz = center.getZ() + size*quadrant.dz;
		Node node = new Node(new BCPoint2D(cx, cz), size, this);
		TreeChild tm = node.leafExplode(leaf);
		if (tm != node && tm instanceof Node)
			throw new IllegalArgumentException();

		quadrants[index] = tm;
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " " + this.toString() + " : replace leaf at " + quadrant + "with " + node.toString());
	}


	/**
	 * Set the branch of a quadrant
	 *
	 * @param branch branch to set
	 * @param quadrant quadrant where the branch must be set
	 */
	private void setBranch(TreeChild branch, Quadrant quadrant) {
		int index = quadrant.index;
		quadrants[index] = branch;
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " " + this.toString() + " : plug old branch to " + quadrant);
	}


	/**
	 * We expand to try to include the content.
	 * Expanding means to add one hierarchical level.
	 *
	 *
	 * @param content the polygon we try to contain
	 * @return A new parent for this node
	 */
	final Node expand(Region content) {
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " Expanding starting from " + this.toString());
		Point2D newcenter = isInBox(content.getPointIterator());
		if (newcenter == null)
			return null;
		// we instantiate the new node as root
		Node newroot = new Node(newcenter, getSize()<<1, parent);
		// we set the old root as a branch
		Quadrant branch = newroot.getQuadrant(getCenter());
		newroot.setBranch(this, branch);
		// we update our parent reference
		parent = newroot;

		return newroot;
	}

	/**
	 * Check that a polygon is contained in the box that this node is managing,
	 * i.e that that this node will answer for each point contained in the polygon.
	 * 
	 * This function returns the point which should become the root of the tree
	 * if points are missing and we want to try to include the polygon
	 *
	 * @param pointiterator An iterator on the points of the polygon
	 * @return The center of the node we should expand to, or null if the polygon is entirely contained
	 */
	private final Point2D isInBox(Iterator<Point> pointiterator) {
		Point point;
		while (pointiterator.hasNext()) {
			point = pointiterator.next();

			if (BCProtect.debugQuadtree)
				BCProtect.log.info(BCProtect.logPrefix + " Checking if point " + point.toString() + " is in node");

			int x = point.getX();
			int z = point.getZ();
			int cx = this.getCenter().getX();
			int cz = this.getCenter().getZ();
			int size = this.getSize();
			if (Math.abs(cx -x) >= size || Math.abs(z - cz) >= size) {
				if (BCProtect.debugQuadtree)
					BCProtect.log.info(BCProtect.logPrefix + " Point " + point.toString() + " is NOT in the node");
				// we compute the opposite angle
				Quadrant quadrant = this.getQuadrant(x, z);
				int ncx = cx + quadrant.dx * size;
				int ncz = cz + quadrant.dz * size;
				return new BCPoint2D(ncx, ncz);
			}
			if (BCProtect.debugQuadtree)
				BCProtect.log.info(BCProtect.logPrefix + " Point " + point.toString() + " is in the node");
		}
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " Expansion completed: All points are in the node");

		return null;

	}

	/**
	 * Transform a leaf to a node with center (x, z)
	 *
	 * @param leaf the leaf to transform
	 * @return A TreeMember that can replace the leaf
	 */
	private TreeChild leafExplode(Leaf leaf) {
		Iterator<Region> iterator = leaf.getContent().getContent().iterator();
		while (iterator.hasNext()) {
			this.setContent(iterator.next());
		}
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " Exploding leaf to " + this.toString());
		return this;
	}



	/**
	 * @return the size
	 */
	final int getSize() {
		return size;
	}


	@Override
	public String toString() {
		return "node center : " + this.getCenter() + ", leaf size = " + this.getSize();
	}
}
