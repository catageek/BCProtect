package com.github.catageek.BCProtect;


import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import com.github.catageek.BCProtect.Regions.RegionBuilder;
import com.github.catageek.ByteCart.Event.UpdaterMoveEvent;
import com.github.catageek.ByteCart.Event.UpdaterPassRouterEvent;
import com.github.catageek.ByteCart.Event.UpdaterPassStationEvent;

final class BCProtectListener implements Listener {


	@EventHandler
	public void onUpdaterPassRouter(UpdaterPassRouterEvent event) {
		BCProtect.getRegionBuilder().onPassRouter(event.getBlock().getLocation(BCProtect.location),
				event.getFrom(), event.getTo(), event.getName(), event.getUpdaterLevel());
	}

	@EventHandler
	public void onUpdaterMove(UpdaterMoveEvent event) {
		Location from = event.getEvent().getFrom();
		Location to = event.getEvent().getTo();
		BCProtect.getRegionBuilder().onMove(from, to, this.getDirection(from, to));
	}

	@EventHandler
	public void onUpdaterPassStation(UpdaterPassStationEvent event) {
		BCProtect.getRegionBuilder().onPassStation(event.getBlock().getLocation(BCProtect.location),
				event.getDirection(), event.getName(), event.getUpdaterLevel());
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
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

	static boolean checkPermission(Player p, Location loc, String permission) {
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

	static void sendError(Player player, String message) {
		if (player.isOnline())
			player.sendMessage(ChatColor.DARK_GREEN+"[BCProtect] " + ChatColor.RED + message);
	}
}
