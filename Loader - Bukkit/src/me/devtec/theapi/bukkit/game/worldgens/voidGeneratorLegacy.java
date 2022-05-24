package me.devtec.theapi.bukkit.game.worldgens;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

//Void generator 1.7.10 - 1.8.8
public class voidGeneratorLegacy extends ChunkGenerator {
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ,
			ChunkGenerator.BiomeGrid biomeGrid) {
		return new byte[world.getMaxHeight() / 16][];
	}

	public boolean canSpawn(World world, int x, int z) {
		return true;
	}

	public Location getFixedSpawnLocation(World world, Random random) {
		if (new Location(world, 0, 63, 0).getBlock().getType() == Material.AIR)
			new Location(world, 0, 63, 0).getBlock().setType(Material.GLASS);
		return new Location(world, 0, 64, 0);
	}
}