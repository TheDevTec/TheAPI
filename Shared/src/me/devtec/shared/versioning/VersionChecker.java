package me.devtec.shared.versioning;

import me.devtec.shared.utility.StringUtils;

public class VersionChecker {
	public static enum Version {
		OLDER_VERSION, NEWER_VERSION, SAME_VERSION, UKNOWN;
	}
	
	public static Version getVersion(String currentVersion, String version) {
		if(currentVersion==null || version==null || currentVersion.replaceAll("[^0-9.]+", "").trim().isEmpty()||version.replaceAll("[^0-9.]+", "").trim().isEmpty())return Version.UKNOWN;
		Version is = Version.UKNOWN;
		int d = 0;
    	String[] s = currentVersion.replaceAll("[^0-9.]+", "").split("\\.");
    	for(String f : version.replaceAll("[^0-9.]+", "").split("\\.")) {
    		int id = StringUtils.getInt(f), bi = StringUtils.getInt(s[d++]);
    		if(id == bi) {
    			is = Version.SAME_VERSION;
    			continue;
    		}
    		return id > bi? Version.NEWER_VERSION : Version.OLDER_VERSION;
    	}
    	return is;
	}
}
