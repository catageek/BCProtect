package com.github.catageek.BCProtect.Quadtree;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.github.catageek.BCProtect.BCProtect;

public final class RegionList {

	private final List<Region> list = Collections.synchronizedList(new LinkedList<Region>());


	public boolean isInRegionList(double x, double y, double z) {
		ListIterator<Region> iterator = list.listIterator();
		if (getFirstData(x, y, z, iterator) != null)
			return true;
		return false;
	}

	public Set<Object> getDataSet(double x, double y, double z) {
		Set<Object> set = new HashSet<Object>();
		Object o;

		ListIterator<Region> iterator = list.listIterator();
		while((o = getFirstData(x, y ,z, iterator)) != null)
			set.add(o);
		return set;
	}

	private Object getFirstData(double x, double y, double z, Iterator<Region> iterator) {
		Region r = null;
		while (iterator.hasNext()) {
			r = iterator.next();
			if (r.getData() == null)
				// the region has been marked to be removed
				iterator.remove();
			else if (r.isInsideRegion(x, y, z)) {
				return r.getData();
			}
		}
		return null;
	}

	public List<Region> getContent() {
		return list;
	}

	@Override
	public String toString() {
		Iterator<Region> iterator = list.iterator();
		StringBuilder str = new StringBuilder(BCProtect.logPrefix);
		str.append(" Content of the list:\n");
		BCProtect.log.info(str.toString());
		while (iterator.hasNext()) {
			str.append(" ").append(iterator.next().toString()).append("\n");
		}
		return str.toString();
	}
}
