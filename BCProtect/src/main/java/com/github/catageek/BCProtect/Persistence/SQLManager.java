package com.github.catageek.BCProtect.Persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.catageek.BCProtect.BCProtect;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;

public class SQLManager {

	private final Database sql;

	public SQLManager(JavaPlugin plugin, String pluginname, Logger logger) {
		super();
		this.sql = new SQLite(logger, "["+pluginname+"]", plugin.getDataFolder().getAbsolutePath(),
				pluginname, ".sqlite");
		sql.open();
	}
	
	public ResultSet execute(String query) {
		ResultSet rep = null;
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " SQL query: " + query);
		try {
			rep = sql.query(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rep;
	}


	public ArrayList<Long> insert(String query) {
		ArrayList<Long> rep = null;
		if (BCProtect.debugQuadtree)
			BCProtect.log.info(BCProtect.logPrefix + " SQL query: " + query);
		try {
			rep = sql.insert(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rep;
	}
	
	public void close() {
		sql.close();
	}


}
