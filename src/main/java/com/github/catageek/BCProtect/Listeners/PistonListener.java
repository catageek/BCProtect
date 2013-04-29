package com.github.catageek.BCProtect.Listeners;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.github.catageek.BCProtect.Util;
import com.github.catageek.BCProtect.Persistence.PersistentQuadtree;

public class PistonListener implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		Iterator<Block> it = event.getBlocks().iterator();
		PersistentQuadtree pq = Util.getQuadtree(event.getBlock());
		Block block = event.getBlock();
		while (it.hasNext()) {
			block = it.next();
			if (! block.isEmpty() && pq.contains(block.getX(), block.getY(), block.getZ())) {
				event.setCancelled(true);
				return;
			}
		}
		// last empty space
		block = block.getRelative(event.getDirection());
		if (pq.contains(block.getX(), block.getY(), block.getZ())) {
			event.setCancelled(true);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (event.isSticky()) {
			Location loc = event.getRetractLocation();
			if (! loc.getBlock().isEmpty() && Util.getQuadtree(loc).contains(loc)) {
				event.setCancelled(true);
				return;
			}
		}
	}

}
