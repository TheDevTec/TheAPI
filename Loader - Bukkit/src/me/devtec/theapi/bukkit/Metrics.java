package me.devtec.theapi.bukkit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.scheduler.Tasker;

public class Metrics {
	// The version of this bStats class
	public static final int B_STATS_VERSION = 1;

	// The url to which the data is sent
	private static final String URL = "https://bStats.org/submitData/bukkit";

	// Is bStats enabled on this server?
	private static final boolean enabled;

	// Should failed requests be logged?
	private static final boolean logFailedRequests;

	// Should the sent data be logged?
	private static final boolean logSentData;

	// Should the response text be logged?
	private static final boolean logResponseStatusText;

	// The uuid of the server
	private static final String serverUUID;
	static {
		Config c = new Config("plugins/bStats/config.yml");
		enabled = c.getBoolean("enabled");
		serverUUID = c.getString("serverUuid");
		logFailedRequests = c.getBoolean("logFailedRequests");
		logSentData = c.getBoolean("logSentData");
		logResponseStatusText = c.getBoolean("logResponseStatusText");
	}

	// The plugin
	private final Plugin plugin;

	// The plugin id
	private final int pluginId;

	public Metrics(Plugin plugin, int pluginId) {
		this.plugin = plugin;
		this.pluginId = pluginId;
		if (Metrics.enabled)
			new Tasker() {

				@Override
				public void run() {
					submitData();
				}
			}.runRepeating(15 * 60 * 20, 15 * 60 * 20);
	}

	public boolean isEnabled() {
		return Metrics.enabled;
	}

	public Map<String, Object> getPluginData() {
		Map<String, Object> data = new HashMap<>();
		data.put("pluginName", plugin.getDescription().getName());
		data.put("id", pluginId);
		data.put("pluginVersion", plugin.getDescription().getVersion());
		data.put("customCharts", new ArrayList<>());
		return data;
	}

	private Map<String, Object> getServerData() {
		// Minecraft specific data
		int playerAmount = BukkitLoader.getOnlinePlayers().size();
		int onlineMode = Bukkit.getOnlineMode() ? 1 : 0;
		String bukkitVersion = Bukkit.getVersion();
		String bukkitName = Bukkit.getName();

		// OS/Java specific data
		String javaVersion = System.getProperty("java.version");
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");
		String osVersion = System.getProperty("os.version");
		int coreCount = Runtime.getRuntime().availableProcessors();

		Map<String, Object> data = new HashMap<>();
		data.put("serverUUID", Metrics.serverUUID);
		data.put("playerAmount", playerAmount);
		data.put("onlineMode", onlineMode);
		data.put("bukkitVersion", bukkitVersion);
		data.put("bukkitName", bukkitName);
		data.put("javaVersion", javaVersion);
		data.put("osName", osName);
		data.put("osArch", osArch);
		data.put("osVersion", osVersion);
		data.put("coreCount", coreCount);
		return data;
	}

	private void submitData() {
		final Map<String, Object> data = getServerData();
		data.put("plugins", Arrays.asList("TheAPI"));
		try {
			Metrics.sendData(plugin, data);
		} catch (Exception e) {
			if (Metrics.logFailedRequests)
				plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of " + plugin.getName(), e);
		}
	}

	private static void sendData(Plugin plugin, Map<String, Object> data) throws Exception {
		if (Metrics.logSentData)
			plugin.getLogger().info("Sending data to bStats: " + data);
		HttpsURLConnection connection = (HttpsURLConnection) new URL(Metrics.URL).openConnection();
		byte[] compressedData = Metrics.compress(data.toString());
		connection.setRequestMethod("POST");
		connection.addRequestProperty("Accept", "application/json");
		connection.addRequestProperty("Connection", "close");
		connection.addRequestProperty("Content-Encoding", "gzip");
		connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("User-Agent", "MC-Server/" + Metrics.B_STATS_VERSION);
		connection.setDoOutput(true);
		try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
			outputStream.write(compressedData);
		}
		if (Metrics.logResponseStatusText) {
			StringBuilder builder = new StringBuilder();
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line;
				while ((line = bufferedReader.readLine()) != null)
					builder.append(line);
			}
			plugin.getLogger().info("Sent data to bStats and received response: " + builder);
		}
	}

	private static byte[] compress(final String str) throws IOException {
		if (str == null)
			return null;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
			gzip.write(str.getBytes(StandardCharsets.UTF_8));
		}
		return outputStream.toByteArray();
	}
}