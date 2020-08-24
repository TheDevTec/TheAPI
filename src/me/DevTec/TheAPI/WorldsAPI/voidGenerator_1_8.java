package me.DevTec.TheAPI.WorldsAPI;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class voidGenerator_1_8 extends ChunkGenerator {
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ,ChunkGenerator.BiomeGrid biomeGrid) {
		return new byte[world.getMaxHeight() / 16][];
	}
}