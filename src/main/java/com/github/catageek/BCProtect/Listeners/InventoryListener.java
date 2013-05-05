package com.github.catageek.BCProtect.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

	@EventHandler (ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {

		Entity entity;

		if ((entity = event.getRightClicked()) instanceof StorageMinecart) {

			if (! Util.checkPermission((Player)event.getPlayer(),
					((StorageMinecart) entity).getLocation(BCProtect.location),
					"openchest")) {
				event.setCancelled(true);
			}
		}
	}
}