package com.github.catageek.BCProtect.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.catageek.BCProtect.BCProtect;
import com.github.catageek.BCProtect.Util;

public final class CanBuildListener implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (! Util.checkPermission(event.getPlayer(), event.getBlock().getLocation(BCProtect.location), "canbuild")) {
			event.setCancelled(true);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (! Util.checkPermission(event.getPlayer(), event.getBlock().getLocation(BCProtect.location), "canbuild")) {
			event.setCancelled(true);
			return;
		}
	}
	
}
