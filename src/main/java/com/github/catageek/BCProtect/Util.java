package com.github.catageek.BCProtect;

import org.bukkit.Location;
import org.bukkit.block.Block;

import com.github.catageek.BCProtect.Persistence.PersistentQuadtree;

public abstract class Util {
	public static PersistentQuadtree getQuadtree(Block b) {
		return BCProtect.getQuadtreeManager().getQuadtree(b.getWorld().getName());
	}

	public static PersistentQuadtree getQuadtree(Location loc) {
		return BCProtect.getQuadtreeManager().getQuadtree(loc.getWorld().getName());
	}

}
