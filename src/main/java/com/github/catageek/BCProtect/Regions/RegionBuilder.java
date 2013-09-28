package com.github.catageek.BCProtect.Regions;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.github.catageek.BCProtect.BCProtect;
import com.github.catageek.BCProtect.Util;
import com.github.catageek.BCProtect.Quadtree.DataContainer;
import com.github.catageek.BCProtect.Quadtree.Point;
import com.github.catageek.ByteCartAPI.Util.MathUtil;
import com.github.catageek.ByteCartAPI.Wanderer.Wanderer.Level;


/**
 * The cuboids to store are built by this class
 */
public final class RegionBuilder {

	RegionBuilder() {
	}

	private State state = State.UNDEFINED;
	private DataContainer currentContainer;
	private BlockFace currentDirection;

	// stack for storing the stack of attached points of nested subnets
	private final Stack<Point> stackpoints = new Stack<Point>();

	private enum State {
		READY,	// ready to initialize cuboid
		WAIT,	// cuboid initialized, waiting to close
		UNDEFINED;	// waiting the conditions to initialize cuboid
	}

	// the boxes around components and rails
	// negative x,y,z, positive x,y,z
	private enum Delta {
		BC8010(6,1,6,6,7,6),  // from center of router
		BC900x(1,1,0,1,3,2),
		BC9000(1,1,0,1,3,2),
		BC9001(1,1,1,1,3,5),  // from sign
		BC7001(2,2,1,3,1,0),
		OTHER(1,1,1,3,0,0),
		DEFAULT(0,3,0,0,1,0); // from cart position

		private int nx,ny,nz,px,py,pz;

		Delta(int a,int b,int c,int d,int e,int f) {
			nx = a;
			ny = b;
			nz = c;
			px = d;
			py = e;
			pz = f;
		}
	}

	public void onPassRouter(Location loc, BlockFace from, BlockFace to, String name, Level level) {
		// reference point is the chest
		Point p = Util.getPoint(loc.getBlock().getRelative(BlockFace.UP, 5).getLocation());

		if (level.equals(Level.LOCAL)) {
			if (BCProtect.debugRegions)
				BCProtect.log.info(BCProtect.logPrefix + " found router at pos " + loc.toString() + ", going to " + to.toString());

			// we close the current cuboid if any
			if (getState().equals(State.WAIT)) {
				this.closeCuboid(loc.getBlock().getRelative(from, 7).getRelative(MathUtil.clockwise(from))
						.getRelative(BlockFace.UP, 2),
						from.getOppositeFace(), Delta.DEFAULT);
				Util.getQuadtree(loc).put(currentContainer);
			}
			String myname = name.equals("BC8010") ? "BC9256" : name;
			currentContainer = new DataContainer(new Cuboid(p, myname), p);
			this.setState(State.READY);
			// give a new reference point
			postPassRouter(loc, to);
			return;
		}

		if (level.equals(Level.REGION) || level.equals(Level.BACKBONE)) {
			// we initialize the variable here, especially reference point
			if (getState().equals(State.UNDEFINED)) {
				currentContainer = new DataContainer(new Cuboid(p, name), p);
			}


			// we already stored the router area ? give output sign as reference point and finish
			if (RegionBuilder.containsPermission(loc, name)) {
//				postPassRouter(loc, to);
				return;
			}

			// otherwise we store the box around the router
			this.storeBox(loc, to, p, Delta.BC8010);
			return;
		}
	}

	/**
	 * Set the reference point to a sign of the router
	 *
	 *
	 * @param loc the reference point of the router
	 * @param to the side of the sign
	 */
	private void postPassRouter(Location loc, BlockFace to) {
		Point point = Util.getPoint(loc.getBlock().getRelative(to, 6).
				getRelative(MathUtil.clockwise(to))
				.getLocation(BCProtect.location));
		currentContainer.setPoint(point);
		stackpoints.clear();
		stackpoints.push(point);

		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " setting ref point to " + currentContainer.getAttachedPoint().toString());
	}


	/**
	 * Here we put the default box around rails
	 *
	 *
	 * @param locFrom where we come from
	 * @param locTo where we go to
	 * @param to the direction we have
	 */
	public void onMove(Location locFrom, Location locTo, BlockFace to) {
		if (getState().equals(State.UNDEFINED))
			return;

		// check if we are in a curve
		if (this.currentDirection != null && ! this.currentDirection.equals(to) && this.getState() == State.WAIT) {
			// we close the current cuboid
			this.closeCuboid(locFrom.getBlock(), currentDirection, Delta.DEFAULT);
			Util.getQuadtree(locFrom).put(currentContainer);
			this.setState(State.READY);
		}

		// check if the next block is already protected
		if (Util.getQuadtree(locFrom).contains(locTo)) {
			return;
		}

		// otherwise we open a new cuboid
		if (this.getState().equals(State.READY)) {
			this.openCuboid(locTo.getBlock(), to, Delta.DEFAULT);
			this.currentDirection = to;
			this.setState(State.WAIT);
		}
	}

	/**
	 * Store the box around the BC9001 sign
	 *
	 *
	 * @param location the sign
	 * @param to the direction we aim
	 * @param name the name of the station sign (BC9001)
	 * @param level the level of the updater : local, region or backbone
	 */
	public void onPassStation(Location location, BlockFace to, String name) {

		if (getState().equals(State.UNDEFINED))
			return;


		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " found station at pos " + location.toString()
					+ ", going to " + to.toString() + " with name " + name);

		// check if we already stored this area for this permission
		if (RegionBuilder.containsPermission(location, name)) {
			return;
		}

		// Save the current variable
		DataContainer save = currentContainer;

		// put the box
		Point p = Util.getPoint(location);		

		currentContainer = new DataContainer(new Cuboid(p, name), p);

		storeBox(location, to, p, Delta.BC9001);
		// cuboid complete, next
		setState(State.READY);


		// restore variable
		currentContainer = save;
	}

	public void onEnterSubnet(Location location, BlockFace to, String name) {

		if (getState().equals(State.UNDEFINED))
			return;


		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " entering subnet at pos " + location.toString()
					+ ", going to " + to.toString() + " with name " + name);

		// put the box
		Point p = Util.getPoint(location);		

		stackpoints.push(p);

		// cuboid complete, next
		setState(State.READY);

		currentContainer = new DataContainer(new Cuboid(p, name), p);

		// check if we already stored this area for this permission
		if (RegionBuilder.containsPermission(location, name)) {
			return;
		}

		storeBox(location, to, p, Delta.BC900x);

	}

	public void onLeaveSubnet(Location location, BlockFace to, String name) {

		if (getState().equals(State.UNDEFINED))
			return;


		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " Leaving subnet at pos " + location.toString()
					+ ", going to " + to.toString() + " with next name " + name);

		// we close the current cuboid if any
		if (getState().equals(State.WAIT)) {
			this.closeCuboid(location.getBlock().getRelative(to.getOppositeFace()).getRelative(MathUtil.clockwise(to))
					.getRelative(BlockFace.UP, 2),
					to, Delta.DEFAULT);
			Util.getQuadtree(location).put(currentContainer);
		}

		// put the box
		//Point p = Util.getPoint(location);
		Point p = stackpoints.pop();

		// cuboid complete, next
		setState(State.READY);

		currentContainer.setPoint(p);

		// check if we already stored this area for this permission
		if (RegionBuilder.containsPermission(location, (String) currentContainer.getData())) {
			return;
		}

		storeBox(location, to, p, Delta.BC900x);

	}

	/**
	 * Called if a player creates a BC9001 sign
	 *
	 * @param location the location of the sign
	 * @param to the direction
	 * @param name
	 */
	public void onCreateStation(Location location, BlockFace to,
			String name) {
		this.onPassStation(location, to, name);
	}

	private void storeBox(Location location, BlockFace to, Point p, Delta delta) {
		this.openCuboid(location.getBlock(), to, delta);
		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " closing box at pos " + location.toString());
		this.closeCuboid(location.getBlock(), to, delta);
		currentContainer.setPoint(p);

		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " Inserting box cuboid " + currentContainer.getRegion());
		Util.getQuadtree(location).put(currentContainer);

	}

	/**
	 * @return the state
	 */
	State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	void setState(State state) {
		this.state = state;
	}


	private void closeCuboid(Block block1, BlockFace to, Delta delta) {
		Block block = block1;
		if (delta.px != 0)
			block = block.getRelative(MathUtil.clockwise(to), delta.px);
		if (delta.py != 0)
			block = block.getRelative(BlockFace.UP, delta.py);
		if (delta.pz != 0)
			block = block.getRelative(to, delta.pz);
		Point p = Util.getPoint(block.getLocation());
		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " Closing cuboid with point " + p.toString() + " and direction " + to.toString());
		((Cuboid) currentContainer.getRegion()).addPoint(p, true);
	}

	private void openCuboid(Block block1, BlockFace to, Delta delta) {
		Block block = block1;
		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " adding " + delta.nx + "," +delta.ny + "," + delta.nz);
		if (delta.nx != 0)
			block = block.getRelative(MathUtil.anticlockwise(to), delta.nx);
		if (delta.ny != 0)
			block = block.getRelative(BlockFace.DOWN, delta.ny);
		if (delta.nz != 0)
			block = block.getRelative(to.getOppositeFace(), delta.nz);
		Point p = Util.getPoint(block.getLocation());
		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " Opening cuboid " + (String) currentContainer.getData() + " with point " + p.toString() + " and direction " + to.toString());
		Cuboid cub = new Cuboid(p, currentContainer.getData());
		currentContainer.setRegion(cub);
	}

	/**
	 * Check if the tree contains a specific permission data for a given point
	 *
	 *
	 * @param loc the point to check
	 * @param permission the permission to check
	 * @return
	 */
	private static boolean containsPermission(Location loc, String permission) {
		Set<Object> set = Util.getQuadtree(loc).get(loc);
		Iterator<Object> it = set.iterator();

		while (it.hasNext()) {
			if (((String)it.next()).equals(permission)) {
				return true;
			}
		}
		return false;
	}
}
