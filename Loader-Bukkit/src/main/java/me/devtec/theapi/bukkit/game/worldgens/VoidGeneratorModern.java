package me.devtec.theapi.bukkit.game.worldgens;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import me.devtec.shared.Ref;

//Void generator 1.9+
public class VoidGeneratorModern extends ChunkGenerator {
    private static final Biome the_void = Ref.isNewerThan(12) ? Biome.valueOf("THE_VOID") : Biome.valueOf("VOID");

    @Override
    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int chunkx, int chunkz, ChunkGenerator.BiomeGrid biome) {
        ChunkGenerator.ChunkData data = this.createChunkData(world);
        biome.setBiome(chunkx, chunkz, the_void);
        return data;
    }

    @Override
	public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
	public Location getFixedSpawnLocation(World world, Random random) {
        if (new Location(world, 0, 63, 0).getBlock().getType() == Material.AIR) {
			new Location(world, 0, 63, 0).getBlock().setType(Material.GLASS);
		}
        return new Location(world, 0, 64, 0);
    }
}
