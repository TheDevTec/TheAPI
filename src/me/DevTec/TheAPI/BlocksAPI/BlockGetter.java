package me.DevTec.TheAPI.BlocksAPI;

import me.DevTec.TheAPI.Utils.Position;

public class BlockGetter {
	private final int sizeZ, sizeY, sizeX, baseZ, baseX, baseY;
	private int x, y, z;
	private final String w;

	public BlockGetter(Position a, Position b) {
		w = a.getWorld().getName();
		baseX = Math.min(a.getBlockX(), b.getBlockX());
		baseY = Math.min(a.getBlockY(), b.getBlockY());
		baseZ = Math.min(a.getBlockZ(), b.getBlockZ());
		sizeX = Math.abs(Math.max(a.getBlockX(), b.getBlockX()) - baseX) + 1;
		sizeY = Math.abs(Math.max(a.getBlockY(), b.getBlockY()) - baseY) + 1;
		sizeZ = Math.abs(Math.max(a.getBlockZ(), b.getBlockZ()) - baseZ) + 1;
	}

	public void reset() {
		x = 0;
		y = 0;
		z = 0;
	}

	public boolean has() {
		return x < sizeX && y < sizeY && z < sizeZ;
	}

	public Position get() {
		if (!has())
			return new Position(w, baseX + x, baseY + y, baseZ + z);
		Position b = new Position(w, baseX + x, baseY + y, baseZ + z);
		if (++x >= sizeX) {
			x = 0;
			if (++y >= sizeY) {
				y = 0;
				++z;
			}
		}
		return b;
	}
}
