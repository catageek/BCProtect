package com.github.catageek.BCProtect.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.github.catageek.BCProtect.BCProtect;
import com.github.catageek.BCProtect.Util;

public class InventoryListener implements Listener {

	@EventHandler (ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Material m = event.getClickedBlock().getType();

			switch (m) {
			case CHEST:
			case FURNACE:
			case DROPPER:
			case HOPPER:
				if (! Util.checkPermission((Player)event.getPlayer(),
						event.getClickedBlock().getLocation(BCProtect.location),
						"openchest")) {
					event.setCancelled(true);
					return;
				}
			default:
				break;
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onInventoryOpen(InventoryOpenEvent event) {
		
		if (!(event.getInventory().getHolder() instanceof Vehicle)) {
			return;
		}
		if (! Util.checkPermission((Player)event.getPlayer(),
				((Vehicle)event.getInventory().getHolder()).getLocation(BCProtect.location),
				"openchest")) {
			event.setCancelled(true);
		}
	}
}
