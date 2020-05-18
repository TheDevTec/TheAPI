package me.Straiker123.Utils;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import me.Straiker123.TheAPI;

public class voidGenerator extends ChunkGenerator {
	String the_void;

	@Override
	public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int chunkx, int chunkz,
			ChunkGenerator.BiomeGrid biome) {
		ChunkGenerator.ChunkData data = this.createChunkData(world);
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				if (!TheAPI.isNewVersion())
					the_void = "VOID";
				else
					the_void = "THE_VOID";
				biome.setBiome(x, z, Biome.valueOf(the_void));
			}
		}
		return data;
	}
}
