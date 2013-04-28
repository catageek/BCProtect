package com.github.catageek.BCProtect;


import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.github.catageek.BCProtect.Regions.RegionBuilder;
import com.github.catageek.ByteCart.Event.UpdaterMoveEvent;
import com.github.catageek.ByteCart.Event.UpdaterPassRouterEvent;

final class BCProtectListener implements Listener {


	@EventHandler
	public void onUpdaterPassRouter(UpdaterPassRouterEvent event) {
		BCProtect.getRegionBuilder().onPassRouter(event.getBlock().getLocation(BCProtect.location),
				event.getFrom(), event.getTo(), event.getName());
	}

	@EventHandler
	public void onUpdaterMove(UpdaterMoveEvent event) {
		Location from = event.getEvent().getFrom();
		Location to = event.getEvent().getTo();
		BCProtect.getRegionBuilder().onMove(from, to, this.getDirection(from, to));
	}


	@EventHandler (ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (! checkPermission(event.getPlayer(), event.getBlock().getLocation(BCProtect.location), "canbuild")) {
			event.setCancelled(true);
		}
	}

	@EventHandler (ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (! checkPermission(event.getPlayer(), event.getBlock().getLocation(BCProtect.location), "canbuild")) {
			event.setCancelled(true);
			return;
		}
		BCProtect.tree.remove(RegionBuilder.getPoint(BCProtect.location));
	}

	private BlockFace getDirection(Location from, Location to) {
		if (from.getBlockX() == to.getBlockX())
			if (from.getBlockZ() < to.getBlockZ())
				return BlockFace.SOUTH;
			else
				return BlockFace.NORTH;
		else
			if (from.getBlockX() < to.getBlockX())
				return BlockFace.EAST;
			else
				return BlockFace.WEST;
	}

	private boolean checkPermission(Player p, Location loc, String permission) {
		Set<Object> set = BCProtect.tree.get(loc.getX(), loc.getY(), loc.getZ());
		Iterator<Object> it = set.iterator();

		while (it.hasNext()) {
			StringBuilder sb = new StringBuilder(BCProtect.permprefix);
			sb.append((String) it.next()).append(".").append(permission);
			if (! p.hasPermission(sb.toString())) {
				sendError(p, "You don't have " + sb.toString() + " permission.");
				return false;
			}
		}
		return true;
	}

	private void sendError(Player player, String message) {
		if (player.isOnline())
			player.sendMessage(ChatColor.DARK_GREEN+"[BCProtect] " + ChatColor.RED + message);
	}
}
