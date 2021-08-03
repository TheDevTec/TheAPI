package me.devtec.theapi.worldsapi;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class voidGenerator_1_8 extends ChunkGenerator {
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ,
			ChunkGenerator.BiomeGrid biomeGrid) {
		return new byte[world.getMaxHeight() / 16][];
	}

    public boolean canSpawn(World world, int x, int z) {
    	return true;
    }
    
    public Location getFixedSpawnLocation(World world, Random random) {
    	Location spawnLocation = new Location(world, 0, 64, 0);
    	Location blockLocation = spawnLocation.clone().subtract(0, 1, 0);
    	if(blockLocation.getBlock().getType()==Material.AIR)
    		blockLocation.getBlock().setType(Material.GLASS);
    	return spawnLocation;
    }
}