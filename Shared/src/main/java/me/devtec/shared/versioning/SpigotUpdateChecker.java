package me.devtec.shared.versioning;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class SpigotUpdateChecker {

	private final String pluginVersion;
	private final int id;
	private URL checkURL;

	public SpigotUpdateChecker(String pluginVersion, int id) {
		this.id = id;
		this.pluginVersion = pluginVersion;
	}

	public static SpigotUpdateChecker createUpdateChecker(String pluginVersion, int id) {
		return new SpigotUpdateChecker(pluginVersion, id);
	}

	public int getId() {
		return this.id;
	}

	public SpigotUpdateChecker reconnect() {
		try {
			this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.id);
		} catch (Exception e) {
		}
		return this;
	}

	// 0 == SAME VERSION
	// 1 == NEW VERSION
	// 2 == BETA VERSION
	public VersionUtils.Version checkForUpdates() {
		if (this.checkURL == null)
			this.reconnect();
		String[] readerr = null;
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(this.checkURL.openConnection().getInputStream()));
			ArrayList<String> s = new ArrayList<>();
			String read;
			while ((read = reader.readLine()) != null)
				s.add(read);
			readerr = s.toArray(new String[s.size()]);
		} catch (Exception e) {
		}
		if (readerr == null)
			return VersionUtils.Version.UKNOWN;
		return VersionUtils.getVersion(this.pluginVersion, readerr[0]);
	}
}
