package com.github.catageek.BCProtect.Regions;

import java.util.HashMap;
import java.util.Map;

public final class RegionBuilderFactory {
	
	private final static Map<Integer,RegionBuilder> map = new HashMap<Integer,RegionBuilder>();
	
	public static RegionBuilder getRegionBuilder(int id) {
		if (map.containsKey(id))
			return map.get(id);
		RegionBuilder rb = new RegionBuilder();
		map.put(id, rb);
		return rb;
	}
	
	public static RegionBuilder getTempRegionBuilder() {
		return new RegionBuilder();
	}
}
