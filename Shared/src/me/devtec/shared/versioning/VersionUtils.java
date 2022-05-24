package me.devtec.shared.versioning;

import me.devtec.shared.utility.StringUtils;

public class VersionUtils {
	public static enum Version {
		OLDER_VERSION, NEWER_VERSION, SAME_VERSION, UKNOWN;
	}

	public static Version getVersion(String currentVersion, String version) {
		if (currentVersion == null || version == null || currentVersion.replaceAll("[^0-9.]+", "").trim().isEmpty()
				|| version.replaceAll("[^0-9.]+", "").trim().isEmpty())
			return Version.UKNOWN;
		int count = 0;
		String[] cver = currentVersion.replaceAll("[^0-9.]+", "").split("\\.");
		for (String ver : version.replaceAll("[^0-9.]+", "").split("\\.")) {
			int next = StringUtils.getInt(ver);
			int current = StringUtils.getInt(cver[count++]);
			if (next == current)
				continue;
			if (next > current)
				return Version.NEWER_VERSION;
			return Version.OLDER_VERSION;
		}
		return Version.SAME_VERSION;
	}
}
