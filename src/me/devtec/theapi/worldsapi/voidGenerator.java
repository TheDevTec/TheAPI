package me.devtec.theapi.worldsapi;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import me.devtec.theapi.TheAPI;

public class voidGenerator extends ChunkGenerator {
	private static Biome the_void = TheAPI.isNewVersion() ? Biome.valueOf("THE_VOID") : Biome.valueOf("VOID");

	@Override
	public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int chunkx, int chunkz,
			ChunkGenerator.BiomeGrid biome) {
		ChunkGenerator.ChunkData data = this.createChunkData(world);
		biome.setBiome(chunkx, chunkz, the_void);
		return data;
	}
}
