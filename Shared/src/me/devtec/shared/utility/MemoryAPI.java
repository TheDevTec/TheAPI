package me.devtec.shared.utility;

public class MemoryAPI {
	private static double mb = 1048576;
	private static double max = Runtime.getRuntime().maxMemory() / MemoryAPI.mb;

	public static double getFreeMemory(boolean inPercentage) {
		if (!inPercentage)
			return StringUtils
					.getDouble(String.format("%2.02f", MemoryAPI.getMaxMemory() - MemoryAPI.getRawUsedMemory(false))
							.replaceFirst("\\.00", ""));
		return StringUtils.getDouble(String
				.format("%2.02f",
						(MemoryAPI.getMaxMemory() - MemoryAPI.getRawUsedMemory(false)) / MemoryAPI.getMaxMemory() * 100)
				.replaceFirst("\\.00", ""));
	}

	public static double getMaxMemory() {
		return MemoryAPI.max;
	}

	public static double getUsedMemory(boolean inPercents) {
		if (!inPercents)
			return StringUtils
					.getDouble(String.format("%2.02f", MemoryAPI.getRawUsedMemory(false)).replaceFirst("\\.00", ""));
		return StringUtils
				.getDouble(String.format("%2.02f", MemoryAPI.getRawUsedMemory(false) / MemoryAPI.getMaxMemory() * 100)
						.replaceFirst("\\.00", ""));
	}

	public static double getRawUsedMemory(boolean inPercents) {
		if (!inPercents)
			return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / MemoryAPI.mb;
		return MemoryAPI.getRawUsedMemory(false) / MemoryAPI.getMaxMemory() * 100;
	}

	public static double getRawFreeMemory(boolean inPercents) {
		if (!inPercents)
			return MemoryAPI.getMaxMemory() - MemoryAPI.getRawUsedMemory(false);
		return (MemoryAPI.getMaxMemory() - MemoryAPI.getRawUsedMemory(false)) / MemoryAPI.getMaxMemory() * 100;
	}
}
