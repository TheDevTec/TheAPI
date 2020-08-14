package me.DevTec;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import me.DevTec.Other.StringUtils;

public class MemoryAPI {
	private static double mb = 1048576;
	private static double max = Runtime.getRuntime().maxMemory() / 1048576;

	public static String clearMemory() {
		double mem = getRawUsedMemory(false);
		for (World w : Bukkit.getWorlds())
			for (Chunk c : w.getLoadedChunks())
				c.unload(true);
		System.gc();
		return String.format("%2.02f", mem - getRawUsedMemory(false)).replaceFirst("\\.00", "");
	}

	public static double getFreeMemory(boolean inPercentage) {
		if (!inPercentage)
			return StringUtils
					.getDouble(String.format("%2.02f", (getMaxMemory() - getRawUsedMemory(false))).replaceFirst("\\.00", ""));
		else
			return StringUtils.getDouble(
					String.format("%2.02f", ((getMaxMemory() - getRawUsedMemory(false)) / getMaxMemory()) * 100).replaceFirst("\\.00", ""));
	}

	public static double getMaxMemory() {
		return max;
	}

	public static double getUsedMemory(boolean inPercentage) {
		if (!inPercentage)
			return StringUtils.getDouble(String.format("%2.02f", getRawUsedMemory(false)).replaceFirst("\\.00", ""));
		else
			return StringUtils
					.getDouble(String.format("%2.02f", (getRawUsedMemory(false) / getMaxMemory()) * 100).replaceFirst("\\.00", ""));
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
