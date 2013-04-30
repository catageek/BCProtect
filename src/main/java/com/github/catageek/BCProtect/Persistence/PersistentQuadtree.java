package com.github.catageek.BCProtect.Persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;

import com.github.catageek.BCProtect.BCProtect;
import com.github.catageek.BCProtect.Quadtree.DataContainer;
import com.github.catageek.BCProtect.Quadtree.Point;
import com.github.catageek.BCProtect.Quadtree.Quadtree;
import com.github.catageek.BCProtect.Quadtree.Region;
import com.github.catageek.BCProtect.Regions.Cuboid;

/**
 * Saves and restore data of the Quadtree
 */
public final class PersistentQuadtree {

	private SQLManager sqlmanager;
	private Quadtree quadtree;


	public PersistentQuadtree(String world) {
		sqlmanager = new SQLManager(BCProtect.myPlugin, world, BCProtect.log);
		createTable();
		quadtree = this.loadTree();
	}

	/**
	 * Store a content.
	 *
	 *
	 * @param content content to store
	 */
	public void put(DataContainer content) {
		this.addRegionToSQL(content.getRegion(), content.getAttachedPoint());
		quadtree.put(content);
	}

	public Set<Object> get(Location loc) {
		return quadtree.get(loc.getX(), loc.getY(), loc.getZ());
	}

	public boolean contains(double x, double y, double z) {
		return quadtree.contains(x, y, z);
	}

	public boolean contains(Location loc) {
		return this.contains(loc.getX(), loc.getY(), loc.getZ());
	}

	public void remove(Point p) {
		quadtree.remove(p);
		this.removeSQL(p);
	}

	/**
	 * initialize tables
	 *
	 *
	 */
	private void createTable() {
		String query;
		query ="CREATE TABLE IF NOT EXISTS refpoint" +
				" (idx INTEGER PRIMARY KEY ASC AUTOINCREMENT,rx INT,ry INT,rz INT, UNIQUE(rx,ry,rz));";
		sqlmanager.execute(query.toString());
		query = "CREATE TABLE IF NOT EXISTS cuboids" +
				" (ax INT,ay INT,az INT,bx INT,by INT,bz INT,rp_index INT, perm VARCHAR(30));";
		sqlmanager.execute(query.toString());
	}

	private void removeSQL(Point p) {
		String query1 = "SELECT idx";
		StringBuilder query = new StringBuilder(" FROM refpoint WHERE rx=");
		query.append(p.getX()).append(" AND ry=").append(p.getY()).append(" AND rz=").append(p.getZ())
		.append(";");
		ResultSet rep = sqlmanager.execute(query1+query.toString());
		String query2 = "DELETE FROM cuboids WHERE rp_index=";
		int index;
		try {
			while (rep.next()) {
				index = rep.getInt("idx");
				sqlmanager.execute(query2+index);
				sqlmanager.execute("DELETE"+query);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * load the tree from the database
	 *
	 *
	 * @return
	 */
	private Quadtree loadTree() {
		StringBuilder query = new StringBuilder("SELECT idx,rx,ry,rz FROM refpoint;");
		ResultSet rep = sqlmanager.execute(query.toString());
		List<DataContainer> dclist = new ArrayList<DataContainer>();

		DataContainer dc = new DataContainer();
		Point a = new Point();
		Point b = new Point();
		ResultSet rep1;
		try {
			while (rep.next()) {
				Point r = new Point(rep.getInt("rx"),rep.getInt("ry"),rep.getInt("rz"));
				query = new StringBuilder("SELECT ax,ay,az,bx,by,bz,perm FROM cuboids WHERE rp_index="+rep.getInt("idx"));
				rep1 = sqlmanager.execute(query.toString());
				while (rep1.next()) {
					a.setX(rep1.getInt("ax")).setY(rep1.getInt("ay")).setZ(rep1.getInt("az"));
					Cuboid cub = new Cuboid(a, rep1.getString("perm"));
					cub.addPoint(b.setX(rep1.getInt("bx")).setY(rep1.getInt("by")).setZ(rep1.getInt("bz")), false);
					if (BCProtect.debugQuadtree)
						BCProtect.log.info(BCProtect.logPrefix + " cuboid " + cub.toString());
					dclist.add(new DataContainer(cub,r));
					//					quadtree.put(dc.setRegion(cub).setPoint(r));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// we compute the center of the tree
		Quadtree q = new Quadtree(this.computeCenter(dclist));

		// and we feed the beast
		Iterator<DataContainer> it = dclist.iterator();
		while(it.hasNext()) {
			dc = it.next();
			if (BCProtect.debugQuadtree)
				BCProtect.log.info(BCProtect.logPrefix + "loadTree: loading cuboid " + dc.getRegion().toString());
			q.put(dc);
		}
		return q;
	}

	protected boolean mapContainKey(Point p) {
		return (this.getIndex(p) != -1);
	}

	private void addRegionToSQL(Region r, Point p) {
		Iterator<Point> points = r.getPointIterator();
		String comma = ",";
		StringBuilder query;
		long index = this.getIndex(p);
		if (index == -1) {
			query = new StringBuilder();
			query.append("INSERT INTO refpoint (rx,ry,rz) VALUES(");
			query.append(p.getX()).append(comma).append(p.getY()).append(comma).append(p.getZ()).
			append(");");
			ArrayList<Long> rep = sqlmanager.insert(query.toString());
			index = rep.get(0);
		}
		query = new StringBuilder();
		query.append("INSERT INTO cuboids VALUES(");
		while(points.hasNext()) {
			Point point = points.next();
			query.append(point.getX()).append(comma).
			append(point.getY()).append(comma).
			append(point.getZ());
			query.append(comma);
		}
		query.append(index).append(",");
		query.append("'").append(r.getData().toString()).append("'").append(");");
		sqlmanager.execute(query.toString());
	}

	public void close() {
		sqlmanager.close();
	}

	// compute the median of x and z
	private Point computeCenter(List<DataContainer> list) {
		long x = 0, z = 0, wtotal = 0;
		Iterator<DataContainer> it = list.iterator();
		Region reg;
		Point p;

		while(it.hasNext()) {
			reg = it.next().getRegion();
			p = reg.getPointIterator().next();
			int weight = reg.getWeight();
			x += weight * p.getX();
			z += weight * p.getZ();
			wtotal += weight;
		}

		if (wtotal != 0) {
			x /= wtotal;
			z /= wtotal;
			return new Point((int)x, 0,(int) z);
		}
		else
			return new Point(0,0,0);

	}

	
	/**
	 * Get the unique index number given to a reference point
	 *
	 *
	 * @param p the reference point
	 * @return the index we can use on 'cuboids' table
	 */
	private long getIndex(Point p) {
		StringBuilder query = new StringBuilder("SELECT idx FROM refpoint WHERE rx=");
		query.append(p.getX()).append(" AND ry=").append(p.getY()).append(" AND rz=").append(p.getZ())
		.append(";");
		ResultSet rep = sqlmanager.execute(query.toString());
		try {
			while(rep.next()) {
				return rep.getInt("idx");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

}
