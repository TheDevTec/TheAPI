package me.devtec.theapi.utils;

public class VersionChecker {
	public enum Version {
		OLD, NEW, SAME, UKNOWN
	}
	
	public static Version getVersion(String currentVersion, String version) {
		if(currentVersion==null || version==null || currentVersion.replaceAll("[^0-9.]+", "").trim().isEmpty()||version.replaceAll("[^0-9.]+", "").trim().isEmpty())return Version.UKNOWN;
		Version is = Version.UKNOWN;
		int d = 0;
    	String[] s = currentVersion.replaceAll("[^0-9.]+", "").split("\\.");
    	for(String f : version.replaceAll("[^0-9.]+", "").split("\\.")) {
    		int id = StringUtils.getInt(f), bi = StringUtils.getInt(s[d++]);
    		if(id == bi) {
    			is=Version.SAME;
    			continue;
    		}
    		is=id > bi?Version.NEW:Version.OLD;
    		break;
    	}
    	return is;
	}
}
