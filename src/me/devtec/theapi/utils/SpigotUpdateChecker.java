package me.devtec.theapi.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class SpigotUpdateChecker {
	public static SpigotUpdateChecker createUpdateChecker(String pluginVersion, int id) {
		return new SpigotUpdateChecker(pluginVersion, id);
	}
	
	private final String pluginVersion;
	private final int id;
    private URL checkURL;
	public SpigotUpdateChecker(String pluginVersion, int id) {
		this.id=id;
		this.pluginVersion=pluginVersion;
	}
	
	public int getId() {
		return id;
	}
    
    public SpigotUpdateChecker reconnect() {
    	try {
			checkURL=new URL("https://api.spigotmc.org/legacy/update.php?resource="+id);
		} catch (Exception e) {}
        return this;
    }

    //0 == SAME VERSION
    //1 == NEW VERSION
    //2 == BETA VERSION
    public VersionChecker.Version checkForUpdates() {
    	if(checkURL==null)
    		reconnect();
    	String[] readerr = null;
    	try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(checkURL.openConnection().getInputStream()));
			ArrayList<String> s = new ArrayList<>();
			String read;
			while((read=reader.readLine()) != null)
				s.add(read);
			readerr=s.toArray(new String[s.size()]);
		} catch (Exception e) {
		}
    	if(readerr==null)return VersionChecker.Version.UKNOWN;
        return VersionChecker.getVersion(pluginVersion, readerr[0]);
    }
}
