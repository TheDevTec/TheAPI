package me.Straiker123;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

@SuppressWarnings("deprecation")
public class BlockSave {
	BlockData blockdata;
	Biome biom;
	Material mat;
	Location loc;
	MaterialData data;
	ItemStack[] inv;
	String cmd;
	public BlockSave(Block b) {
		  if(b.getState() instanceof Chest) {
			  Chest c = (Chest) b.getState();
			  inv = c.getBlockInventory().getContents();
		  }
		  if(b.getType().name().contains("COMMAND")) {
			  CommandBlock c = (CommandBlock)b.getState();
			  cmd =c.getCommand();
		  }
		data=b.getState().getData();
		blockdata=b.getBlockData();
		biom=b.getBiome();
		mat=b.getType();
		loc=b.getLocation();
	}
	
	public ItemStack[] getBlockInventory() {
		return inv;
	}
	
	public String getCommand() {
		return cmd;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public World getWorld() {
		return loc.getWorld();
	}

	public BlockData getBlockData() {
		return blockdata;
	}
	public MaterialData getMaterialData() {
		return data;
	}
	public Biome getBiome() {
		return biom;
	}
	public Material getMaterial() {
		return mat;
	}
}
