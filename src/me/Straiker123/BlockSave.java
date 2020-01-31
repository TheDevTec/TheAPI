package me.Straiker123;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockSave {
	BlockData b;
	Biome s;
	Material d;
	Location l;
	public BlockSave(Block b) {
		this.b=b.getBlockData();
		s=b.getBiome();
		d=b.getType();
		l=b.getLocation();
	}

	public Location getLocation() {
		return l;
	}
	
	public World getWorld() {
		return l.getWorld();
	}
	
	public BlockData getBlockData() {
		return b;
	}
	public Biome getBiome() {
		return s;
	}
	public Material getMaterial() {
		return d;
	}
}
