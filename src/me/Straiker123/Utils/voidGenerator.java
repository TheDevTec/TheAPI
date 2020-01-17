package me.Straiker123.Utils;

import java.util.Random;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import me.Straiker123.TheAPI;

public class voidGenerator extends ChunkGenerator {
	 String the_void;
	    public ChunkGenerator.ChunkData generateChunkData(World world, Random random, int chunkx, int chunkz, ChunkGenerator.BiomeGrid biome) {
	        ChunkGenerator.ChunkData data = this.createChunkData(world);
	        for (int x = 0; x < 16; ++x) {
	            for (int z = 0; z < 16; ++z) {
	            	if(TheAPI.getServerVersion().contains("glowstone")||TheAPI.getServerVersion().contains("1_14"))the_void="VOID";
	            	if(TheAPI.getServerVersion().contains("1_13")||TheAPI.getServerVersion().contains("1_14"))the_void="THE_VOID";
	            	if(TheAPI.getServerVersion().contains("1_12")||TheAPI.getServerVersion().contains("1_11")
	            			||TheAPI.getServerVersion().contains("1_10")||TheAPI.getServerVersion().contains("1_9"))the_void="VOID";
	                biome.setBiome(x, z, Biome.valueOf(the_void));
	                
	            }
	        }
	        return data;
	    }
}
