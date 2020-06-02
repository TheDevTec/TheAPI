package me.Straiker123.Blocks;

import java.util.List;

import org.bukkit.World;

import me.Straiker123.Position;
import me.Straiker123.TheMaterial;

public class BlockGetter {
	private final int sizeZ, sizeY, sizeX, baseZ, baseX, baseY;
	private int x, y, z;
	private final World w;

	public BlockGetter(Position a, Position b) {
		w = a.getWorld();
		baseX = Math.min(a.getBlockX(), b.getBlockX());
		baseY = Math.min(a.getBlockY(), b.getBlockY());
		baseZ = Math.min(a.getBlockZ(), b.getBlockZ());
		sizeX = Math.abs(Math.max(a.getBlockX(), b.getBlockX()) - baseX) + 1;
		sizeY = Math.abs(Math.max(a.getBlockY(), b.getBlockY()) - baseY) + 1;
		sizeZ = Math.abs(Math.max(a.getBlockZ(), b.getBlockZ()) - baseZ) + 1;
	}
	
	public boolean has() {
		return x < sizeX && y < sizeY && z < sizeZ;
	}

	public Position get() {
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
	
	public void replace(List<TheMaterial> block, TheMaterial with) {
		Position b = get();
		if (block.contains(b.getType()))
			b.setType(with);
	}

	public void replace(TheMaterial material, TheMaterial with) {
		Position b = get();
		if (b.getType().equals(material))
			b.setType(with);
	}
	
	public void replace(List<TheMaterial> block, TheMaterial with, boolean update) {
		Position b = get();
		if (block.contains(b.getType()))
			b.setType(with,update);
	}

	public void replace(TheMaterial material, TheMaterial with, boolean update) {
		Position b = get();
		if (b.getType().equals(material)) {
		get().setType(with,update);
		}
	}

	public void set(TheMaterial material, TheMaterial ignore) {
		Position b = get();
		if (!b.getType().equals(ignore))
			b.setType(material);
	}

	public void set(TheMaterial material, List<TheMaterial> ignore) {
		Position b = get();
		if (!ignore.contains(b.getType()))
			b.setType(material);
	}

	public void set(TheMaterial material) {
		get().setType(material);
	}

	public void set(TheMaterial material, TheMaterial ignore, boolean update) {
		Position b = get();
		if (!b.getType().equals(ignore))
			b.setType(material,update);
	}

	public void set(TheMaterial material, List<TheMaterial> ignore, boolean update) {
		Position b = get();
		if (!ignore.contains(b.getType()))
			b.setType(material,update);
	}

	public void set(TheMaterial material, boolean update) {
		get().setType(material,update);
	}
}
