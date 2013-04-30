package com.github.catageek.BCProtect.Listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import com.github.catageek.BCProtect.BCProtect;
import com.github.catageek.BCProtect.Util;

public final class VehicleDestroyListener implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		Location loc = event.getVehicle().getLocation(BCProtect.location);
		if (Util.getQuadtree(loc).contains(loc)) {
			event.setCancelled(true);
		}
	}
}
