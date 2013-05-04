package com.github.catageek.BCProtect;


import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.github.catageek.ByteCart.Event.SignCreateEvent;
import com.github.catageek.ByteCart.Event.UpdaterMoveEvent;
import com.github.catageek.ByteCart.Event.UpdaterPassRouterEvent;
import com.github.catageek.ByteCart.Event.UpdaterPassStationEvent;

final class BCProtectListener implements Listener {


	@EventHandler
	public void onUpdaterPassRouter(UpdaterPassRouterEvent event) {
		BCProtect.getRegionBuilder().onPassRouter(event.getIc().getBlock().getLocation(BCProtect.location),
				event.getFrom(), event.getTo(), event.getIc().getName(), event.getUpdaterLevel());
	}

	@EventHandler
	public void onUpdaterMove(UpdaterMoveEvent event) {
		Location from = event.getEvent().getFrom();
		Location to = event.getEvent().getTo();
		BCProtect.getRegionBuilder().onMove(from, to, this.getDirection(from, to));
	}

	@EventHandler
	public void onUpdaterPassStation(UpdaterPassStationEvent event) {
		BCProtect.getRegionBuilder().onPassStation(event.getIc().getBlock().getLocation(BCProtect.location),
				event.getIc().getCardinal().getOppositeFace(), event.getIc().getName(), event.getUpdaterLevel());
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		Util.getQuadtree(event.getBlock()).remove(Util.getPoint(BCProtect.location));
	}

	@EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onSignCreate(SignCreateEvent event) {
		String name;
		if ((name = event.getStrings()[1]).equals("BC9001"))
			BCProtect.getRegionBuilder().onCreateStation(event.getIc().getBlock().getLocation(BCProtect.location),
					event.getIc().getCardinal().getOppositeFace(), name);
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

}
