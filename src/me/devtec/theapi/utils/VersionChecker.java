package me.devtec.theapi.utils;

public class VersionChecker {
	public enum Version {
		OLD, NEW, SAME, UKNOWN
	}
	
	public static Version getVersion(String currentVersion, String version) {
		if(currentVersion==null || version==null || (currentVersion=currentVersion.replaceAll("[^0-9.]+", "").trim()).isEmpty()||(version=version.replaceAll("[^0-9.]+", "").trim()).isEmpty())return Version.UKNOWN;
		Version is = Version.UKNOWN;
		String[] s = currentVersion.split("\\."), splitted = version.split("\\.");
		int pos = 0;
		for(String split : splitted) {
			int id = StringUtils.getInt(split), bi = StringUtils.getInt(s[pos++]);
			if(id == bi) {
				is=Version.SAME;
				continue;
			}
			return id > bi?Version.NEW:Version.OLD;
		}
		return is;
	}
}
