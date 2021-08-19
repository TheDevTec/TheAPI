package me.devtec.theapi.apis;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.StringUtils;

public class MemoryAPI {
	private static final double mb = 1048576;

	public static String clearMemory() {
		double mem = getRawUsedMemory(false);
		for (World w : Bukkit.getWorlds())
			for (Chunk c : w.getLoadedChunks())
				c.unload(true);
		new Tasker() {
			public void run() {
				System.gc();
			}
		}.runTask();
		return StringUtils.fixedFormatDouble(mem - getRawUsedMemory(false));
	}

	public static double getFreeMemory(boolean inPercentage) {
		if (!inPercentage)
			return StringUtils.getDouble(
					StringUtils.fixedFormatDouble((getMaxMemory() - getRawUsedMemory(false))));
		else
			return StringUtils.getDouble(
					StringUtils.fixedFormatDouble(((getMaxMemory() - getRawUsedMemory(false)) / getMaxMemory()) * 100));
	}

	public static double getMaxMemory() {
		return Runtime.getRuntime().maxMemory() / mb;
	}

	public static double getUsedMemory(boolean inPercentage) {
		if (!inPercentage)
			return StringUtils.getDouble(StringUtils.fixedFormatDouble(getRawUsedMemory(false)));
		else
			return StringUtils.getDouble(StringUtils.fixedFormatDouble((getRawUsedMemory(false) / getMaxMemory()) * 100));
	}

	public static double getRawUsedMemory(boolean inPercentage) {
		if (!inPercentage)
			return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb;
		else
			return (getRawUsedMemory(false) / getMaxMemory()) * 100;
	}

	public static double getRawFreeMemory(boolean inPercentage) {
		if (!inPercentage)
			return getMaxMemory() - getRawUsedMemory(false);
		else
			return ((getMaxMemory() - getRawUsedMemory(false)) / getMaxMemory()) * 100;
	}
}
