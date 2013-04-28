package com.github.catageek.BCProtect.Quadtree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.github.catageek.BCProtect.BCProtect;

/**
 * Class implementing a Quadtree
 */
/**
 * 
 */
public class Quadtree implements Parent {

	private Node head;
	private final Map<Point,Set<Region>> map = new HashMap<Point,Set<Region>>();

	/**
	 * Constructor of the tree.
	 * 
	 * @param p the center of the tree
	 */
	public Quadtree(Point p) {
		head = new Node(p.getPoint2d(), BCProtect.initLeaf, this);
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + "Creating QuadTree");
	}

	/**
	 * Returns a list of objects stored in the leaf containing this point
	 *
	 * @param x x coordinate of the point
	 * @param z z coordinate of this point
	 * @return a list of objects previously stored
	 */
	private RegionList getRegionList(double x, double z) {
		return head.getContent(x, z);
	}

	/**
	 * Store an object in the tree
	 *
	 * @param content to store
	 */
	public void put(DataContainer content) {
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + "put: prepare to add cuboid " + content.getRegion().toString());
		expandIfNecessary(content.getRegion());
		head.setContent(content.getRegion());
		addRegionToMap(content.getRegion(), content.getAttachedPoint());
	}
	
	public Set<Object> get(double x, double y, double z) {
		RegionList rl;
		if ((rl = getRegionList(x, z)) != null)
			return rl.getDataSet(x, y, z);
		return new HashSet<Object>();
	}

	public boolean contains(double x, double y, double z) {
		RegionList rl;
		if ((rl = getRegionList(x, z)) != null)
			return rl.isInRegionList(x, y, z);
		return false;
	}

	/**
	 * Remove the data associated with a point
	 *
	 * @param p the point
	 */
	public void remove(Point p) {
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + "Quadtree:remove: check if point hash " + p.hashCode() + " is in map");
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + "Quadtree:remove: content of map " + ((Point)map.keySet().toArray()[0]).hashCode());
		if (! mapContainKey(p))
			return;
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + "Quadtree:remove: prepare to delete cuboids of " + p);
		Iterator<Region> it = mapGet(p).iterator();
		while (it.hasNext()) {
			it.next().nullData();
		}
		mapRemove(p);
	}

	/**
	 * Expand if needed, i.e if a point is outside
	 * the border of the tree.
	 *
	 * @param content to store
	 */
	private void expandIfNecessary(Region content) {
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " Checking if expansion is needed at node " + head);
		Node node = head;

		while ((node = node.expand(content)) != null) {
			head = node;
			if (BCProtect.debugQuadtree)
				BCProtect.log.info(BCProtect.logPrefix + " Setting root to " + head + " with size = " + head.getSize());
		}
	}

	private void addRegionToMap(Region r, Point p) {
		Set<Region> set = mapGet(p);
		if (set == null) {
			set = getSet(r);
			map.put(p, set);
			if (BCProtect.debugQuadtree)
				BCProtect.log.info(BCProtect.logPrefix + "Quadtree:addRegionToMap: point hash " + p.hashCode());
		}
		else {
			set.add(r);
		}
	}

	private void mapRemove(Point p) {
		map.remove(p);
	}

	private Set<Region> mapGet(Point p) {
		return map.get(p);
	}

	private boolean mapContainKey(Point p) {
		return map.containsKey(p);
	}

	private Set<Region> getSet(Region r) {
		Set<Region> set = new HashSet<Region>();
		set.add(r);
		return set;
	}
}
