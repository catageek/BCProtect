package com.github.catageek.BCProtect;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

final class CanBuildListener implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (! BCProtectListener.checkPermission(event.getPlayer(), event.getBlock().getLocation(BCProtect.location), "canbuild")) {
			event.setCancelled(true);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (! BCProtectListener.checkPermission(event.getPlayer(), event.getBlock().getLocation(BCProtect.location), "canbuild")) {
			event.setCancelled(true);
			return;
		}
	}
}
