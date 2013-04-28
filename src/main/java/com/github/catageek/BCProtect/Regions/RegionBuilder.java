package com.github.catageek.BCProtect.Regions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.github.catageek.BCProtect.BCProtect;
import com.github.catageek.BCProtect.Quadtree.DataContainer;
import com.github.catageek.BCProtect.Quadtree.Point;
import com.github.catageek.ByteCart.Util.MathUtil;

public final class RegionBuilder {

	private State state = State.UNDEFINED;
	private DataContainer currentContainer;
	private BlockFace currentDirection;

	private enum State {
		READY,
		WAIT,
		UNDEFINED;
	}

	private enum Delta {
		BC8010(6,1,6,6,7,6),
		BC900x(3,3,1,3,0,2),
		BC9000(2,2,1,3,0,4),
		BC9001(2,2,2,3,0,5),
		BC7001(2,2,1,3,1,0),
		OTHER(1,1,1,3,0,0),
		DEFAULT(0,1,0,0,1,0);

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

	public void onPassRouter(Location loc, BlockFace from, BlockFace to, String name) {
		Point p = getPoint(loc.getBlock().getRelative(BlockFace.UP, 5).getLocation());

		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " found router at pos " + loc.toString() + ", going to " + to.toString());

		if (getState().equals(State.WAIT)) {
			this.closeCuboid(loc.getBlock().getRelative(from, 7).getRelative(MathUtil.clockwise(from))
					.getRelative(BlockFace.UP, 2),
					from.getOppositeFace(), Delta.DEFAULT);
			BCProtect.tree.put(currentContainer);
			this.setState(State.READY);
		}
		
		if (BCProtect.tree.contains(loc.getX(), loc.getY(), loc.getZ())) {
			postPassRouter(loc, to);
			return;
		}
		if (getState().equals(State.UNDEFINED))
			currentContainer = new DataContainer(new Cuboid(p, name), p);

		this.openCuboid(loc.getBlock(), to, Delta.BC8010);
		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " closing router at pos " + loc.toString());
		this.closeCuboid(loc.getBlock(), to, Delta.BC8010);
		currentContainer.setPoint(p);

		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " Inserting router cuboid " + currentContainer.getRegion());
		BCProtect.tree.put(currentContainer);
		setState(State.READY);
		postPassRouter(loc, to);
	}
	
	private void postPassRouter(Location loc, BlockFace to) {
		currentContainer.setPoint(getPoint(loc.getBlock().getRelative(to, 6).
				getRelative(MathUtil.clockwise(to))
				.getLocation(BCProtect.location)));

		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " setting ref point to " + currentContainer.getAttachedPoint().toString());
	}

	public void onMove(Location locFrom, Location locTo, BlockFace to) {
		if (this.currentDirection != null && ! this.currentDirection.equals(to) && this.getState() == State.WAIT) {
			this.closeCuboid(locFrom.getBlock(), currentDirection, Delta.DEFAULT);
			BCProtect.tree.put(currentContainer);
			this.setState(State.READY);
		}
		if (BCProtect.tree.contains(locTo.getX(), locTo.getY(), locTo.getZ())) {
			this.setState(State.READY);
			return;
		}
		if (this.getState().equals(State.READY)) {
			this.openCuboid(locTo.getBlock(), to, Delta.DEFAULT);
			this.currentDirection = to;
			this.setState(State.WAIT);
		}
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

	public static Point getPoint(Location loc) {
		return new Point(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	private void closeCuboid(Block block1, BlockFace to, Delta delta) {
		Block block = block1;
		if (delta.px != 0)
			block = block.getRelative(MathUtil.clockwise(to), delta.px);
		if (delta.py != 0)
			block = block.getRelative(BlockFace.UP, delta.py);
		if (delta.pz != 0)
			block = block.getRelative(to.getOppositeFace(), delta.pz);
		Point p = getPoint(block.getLocation());
		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " Closing cuboid with point " + p.toString() + " and direction " + to.toString());
		((Cuboid) currentContainer.getRegion()).addPoint(p);
	}

	private void openCuboid(Block block1, BlockFace to, Delta delta) {
		Block block = block1;
		if (delta.nx != 0)
			block = block.getRelative(MathUtil.anticlockwise(to), delta.nx);
		if (delta.ny != 0)
			block = block.getRelative(BlockFace.DOWN, delta.ny);
		if (delta.nz != 0)
			block = block.getRelative(to, delta.nz);
		Point p = getPoint(block.getLocation());
		if (BCProtect.debugRegions)
			BCProtect.log.info(BCProtect.logPrefix + " Opening cuboid with point " + p.toString() + " and direction " + to.toString());
		Cuboid cub = new Cuboid(p, currentContainer.getData());
		currentContainer.setRegion(cub);
	}


}
