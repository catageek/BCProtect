package com.github.catageek.BCProtect;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.catageek.BCProtect.Listeners.CanBuildListener;
import com.github.catageek.BCProtect.Listeners.InventoryListener;
import com.github.catageek.BCProtect.Listeners.MobListener;
import com.github.catageek.BCProtect.Listeners.PistonListener;
import com.github.catageek.BCProtect.Listeners.PlayerAccessListener;
import com.github.catageek.BCProtect.Persistence.QuadtreeManager;
import com.github.catageek.BCProtect.Regions.RegionBuilder;

public final class BCProtect extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");
	public static BCProtect myPlugin;
	public static boolean debugQuadtree;
	public static boolean debugRegions;
	public static String logPrefix = "BCProtect.";

	public static int minLeaf = 16;
	public static int initLeaf = 512;
	public static int MaxListSize = 16;
	private static RegionBuilder rb;
	private static QuadtreeManager qm;

	private static boolean canBuild;
	public static String permprefix = "bytecart.";
	public static Location location = new Location(null, 0, 0, 0);
	private static boolean enablePistons;
	private static boolean enableMobs;
	private static boolean openInventory;
	private static boolean canAccess;

	public void onEnable(){
		log.info("BCProtect plugin has been enabled.");

		myPlugin = this;

		this.saveDefaultConfig();

		this.loadConfig();
		
		setQm(new QuadtreeManager());

		rb = new RegionBuilder();

		getServer().getPluginManager().registerEvents(new BCProtectListener(), this);

		if (! BCProtect.canBuild)
			getServer().getPluginManager().registerEvents(new CanBuildListener(), this);

		if (! BCProtect.enablePistons)
			getServer().getPluginManager().registerEvents(new PistonListener(), this);

		if (! BCProtect.enableMobs)
			getServer().getPluginManager().registerEvents(new MobListener(), this);

		if (! BCProtect.openInventory)
			getServer().getPluginManager().registerEvents(new InventoryListener(), this);

		if (! BCProtect.canAccess)
			getServer().getPluginManager().registerEvents(new PlayerAccessListener(), this);

	}

	public void onDisable(){ 
		log.info("BCProtect plugin has been disabled.");

		qm.closeAll();
		qm = null;
		myPlugin = null;
		log = null;

	}

	public static RegionBuilder getRegionBuilder() {
		return rb;

	}

	protected final void loadConfig() {
		debugQuadtree = BCProtect.myPlugin.getConfig().getBoolean("debugQuadtree");

		if(debugQuadtree){
			log.info("ByteCart : debug mode on quadtree is on.");
		}

		debugRegions = BCProtect.myPlugin.getConfig().getBoolean("debugRegions");

		if(debugQuadtree){
			log.info("ByteCart : debug mode on regions is on.");
		}

		canBuild = BCProtect.myPlugin.getConfig().getBoolean("canBuild");
		enablePistons = BCProtect.myPlugin.getConfig().getBoolean("enablePistons");
		enableMobs = BCProtect.myPlugin.getConfig().getBoolean("enableMobs");
		openInventory = BCProtect.myPlugin.getConfig().getBoolean("canOpenInventory");
		canAccess = BCProtect.myPlugin.getConfig().getBoolean("canAccess");
	}

	/**
	 * @return the qm
	 */
	public static QuadtreeManager getQuadtreeManager() {
		return qm;
	}

	/**
	 * @param qm the qm to set
	 */
	private static void setQm(QuadtreeManager qm) {
		BCProtect.qm = qm;
	}


}
