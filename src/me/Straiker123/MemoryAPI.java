package me.Straiker123;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class MemoryAPI {
	private static double mb = 1024*1024;
	private static double max = Runtime.getRuntime().maxMemory() /mb;
	
	public String clearMemory() {
		double mem = getRawUsedMemory(false);
		synchronized(this) {
			for(World w:Bukkit.getWorlds()) {
				for(Chunk c : w.getLoadedChunks()) {
				c.unload(true);
			}}
        System.gc();
		}
        return String.format("%2.02f", mem - getRawUsedMemory(false));
	}
	
	public double getFreeMemory(boolean inPercentage) {
		if(!inPercentage)
			return TheAPI.getNumbersAPI(String.format("%2.02f", (getMaxMemory() - getRawUsedMemory(false)))).getDouble();
			else
			return TheAPI.getNumbersAPI(String.format("%2.02f", ((getMaxMemory()-getRawUsedMemory(false))/getMaxMemory())*100)).getDouble();
	}
	
	public double getMaxMemory() {
		return max;
	}
	
	public double getUsedMemory(boolean inPercentage) {
		if(!inPercentage)
			return TheAPI.getNumbersAPI(String.format("%2.02f", getRawUsedMemory(false))).getDouble();
			else
			return TheAPI.getNumbersAPI(String.format("%2.02f", (getRawUsedMemory(false)/ getMaxMemory())*100)).getDouble();
	}
	
	public double getRawUsedMemory(boolean inPercentage) {
		if(!inPercentage)
			return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) /mb;
			else
			return (getRawUsedMemory(false)/ getMaxMemory())*100;
	}

	public double getRawFreeMemory(boolean inPercentage) {
		if(!inPercentage)
			return getMaxMemory()-getRawUsedMemory(false);
			else
			return ((getMaxMemory()-getRawUsedMemory(false))/getMaxMemory())*100;
	}
}
