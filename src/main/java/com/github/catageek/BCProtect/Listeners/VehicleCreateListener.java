package com.github.catageek.BCProtect.Listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import com.github.catageek.BCProtect.BCProtect;
import com.github.catageek.BCProtect.Util;

public class VehicleCreateListener implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onVehicleCreate(VehicleCreateEvent event) {
		Location loc = event.getVehicle().getLocation(BCProtect.location);
		if (Util.getQuadtree(loc).contains(loc)) {
			event.getVehicle().remove();
		}
	}

}
