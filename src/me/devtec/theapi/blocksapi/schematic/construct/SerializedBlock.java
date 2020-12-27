package me.devtec.theapi.blocksapi.schematic.construct;

import java.io.Serializable;

import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.TheMaterial;

public interface SerializedBlock extends Serializable {
	public default SerializedBlock serialize(Block block) {
		return serialize(new TheMaterial(block.getState().getData().toItemStack(1)),block.getBiome(), block.getData());
	}

	public default SerializedBlock serialize(TheMaterial material) {
		return serialize(material, Biome.PLAINS, (byte)0);
	}

	public default SerializedBlock serialize(TheMaterial material, Biome biome) {
		return serialize(material, biome, (byte)0);
	}

	public default SerializedBlock serialize(ItemStack material) {
		return serialize(new TheMaterial(material), Biome.PLAINS, (byte)0);
	}

	public default SerializedBlock serialize(ItemStack material, Biome biome) {
		return serialize(new TheMaterial(material), biome, (byte)0);
	}

	public default SerializedBlock serialize(ItemStack material, Biome biome, byte data) {
		return serialize(new TheMaterial(material), biome, data);
	}

	public SerializedBlock serialize(TheMaterial material, Biome biome, byte data);
	
	public String getAsString();
	
	public SerializedBlock fromString(String string);
}
