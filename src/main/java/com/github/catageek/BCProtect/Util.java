package com.github.catageek.BCProtect;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.catageek.BCProtect.Persistence.PersistentQuadtree;

public abstract class Util {
	public static PersistentQuadtree getQuadtree(Block b) {
		return BCProtect.getQuadtreeManager().getQuadtree(b.getWorld().getName());
	}

	public static PersistentQuadtree getQuadtree(Location loc) {
		return BCProtect.getQuadtreeManager().getQuadtree(loc.getWorld().getName());
	}

	public static boolean checkPermission(Player p, Location loc, String permission) {
		Set<Object> set = Util.getQuadtree(loc).get(loc.getX(), loc.getY(), loc.getZ());
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

	public static void sendError(Player player, String message) {
		if (player.isOnline())
			player.sendMessage(ChatColor.DARK_GREEN+"[BCProtect] " + ChatColor.RED + message);
	}

}
