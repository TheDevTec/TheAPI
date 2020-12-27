package me.devtec.theapi.utils;

import java.util.Iterator;

import org.bukkit.Location;

public class BlockMathIterator implements Iterable<int[]> {
	private final int sizeZ, sizeY, sizeX, baseZ, baseX, baseY;
	private int x, y, z;

	public BlockMathIterator(Position a, Position b) {
		baseX = Math.min(a.getBlockX(), b.getBlockX());
		baseY = Math.min(a.getBlockY(), b.getBlockY());
		baseZ = Math.min(a.getBlockZ(), b.getBlockZ());
		sizeX = Math.abs(Math.max(a.getBlockX(), b.getBlockX()) - baseX) + 1;
		sizeY = Math.abs(Math.max(a.getBlockY(), b.getBlockY()) - baseY) + 1;
		sizeZ = Math.abs(Math.max(a.getBlockZ(), b.getBlockZ()) - baseZ) + 1;
	}

	public BlockMathIterator(Location a, Location b) {
		baseX = Math.min(a.getBlockX(), b.getBlockX());
		baseY = Math.min(a.getBlockY(), b.getBlockY());
		baseZ = Math.min(a.getBlockZ(), b.getBlockZ());
		sizeX = Math.abs(Math.max(a.getBlockX(), b.getBlockX()) - baseX) + 1;
		sizeY = Math.abs(Math.max(a.getBlockY(), b.getBlockY()) - baseY) + 1;
		sizeZ = Math.abs(Math.max(a.getBlockZ(), b.getBlockZ()) - baseZ) + 1;
	}

	public BlockMathIterator(int posX, int posY, int posZ, int posX2, int posY2, int posZ2) {
		baseX = Math.min(posX, posX2);
		baseY = Math.min(posY, posY2);
		baseZ = Math.min(posZ, posZ2);
		sizeX = Math.abs(Math.max(posX, posX2) - baseX) + 1;
		sizeY = Math.abs(Math.max(posY, posY2) - baseY) + 1;
		sizeZ = Math.abs(Math.max(posZ, posZ2) - baseZ) + 1;
	}

	public void reset() {
		x = 0;
		y = 0;
		z = 0;
	}

	public boolean has() {
		return x < sizeX && y < sizeY && z < sizeZ;
	}

	public int[] get() {
		int[] b = new int[] {baseX + x, baseY + y, baseZ + z};
		if (!has())return b;
		if (++x >= sizeX) {
			x = 0;
			if (++y >= sizeY) {
				y = 0;
				++z;
			}
		}
		return b;
	}

	@Override
	public Iterator<int[]> iterator() {
		return new Iterator<int[]>() {
			public boolean hasNext() {
				return has();
			}
			public int[] next() {
				return get();
			}
		};
	}
}
