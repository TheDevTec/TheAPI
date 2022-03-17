package me.devtec.shared.utils;

import me.devtec.shared.utility.StringUtils;

public class MemoryAPI {
	private static double mb = 1048576;
	private static double max = Runtime.getRuntime().maxMemory() / mb;

	public static double getFreeMemory(boolean inPercentage) {
		if (!inPercentage)
			return StringUtils.getDouble(
					String.format("%2.02f", (getMaxMemory() - getRawUsedMemory(false))).replaceFirst("\\.00", ""));
		else
			return StringUtils.getDouble(
					String.format("%2.02f", ((getMaxMemory() - getRawUsedMemory(false)) / getMaxMemory()) * 100)
							.replaceFirst("\\.00", ""));
	}

	public static double getMaxMemory() {
		return max;
	}

	public static double getUsedMemory(boolean inPercents) {
		if (!inPercents)
			return StringUtils.getDouble(String.format("%2.02f", getRawUsedMemory(false)).replaceFirst("\\.00", ""));
		else
			return StringUtils.getDouble(String.format("%2.02f", (getRawUsedMemory(false) / getMaxMemory()) * 100)
					.replaceFirst("\\.00", ""));
	}

	public static double getRawUsedMemory(boolean inPercents) {
		if (!inPercents)
			return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / mb;
		else
			return (getRawUsedMemory(false) / getMaxMemory()) * 100;
	}

	public static double getRawFreeMemory(boolean inPercents) {
		if (!inPercents)
			return getMaxMemory() - getRawUsedMemory(false);
		else
			return ((getMaxMemory() - getRawUsedMemory(false)) / getMaxMemory()) * 100;
	}
}
