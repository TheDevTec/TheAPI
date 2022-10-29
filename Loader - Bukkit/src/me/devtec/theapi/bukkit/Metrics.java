package me.devtec.theapi.bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.DataType;
import me.devtec.shared.json.Json;
import me.devtec.shared.scheduler.Tasker;

public class Metrics {

	private static List<String> plugins = new ArrayList<>();

	// The version of this bStats class
	public static final int B_STATS_VERSION = 1;

	// The url to which the data is sent
	private static final String URL = "https://bStats.org/submitData/bukkit";

	// The uuid of the server
	private static final String serverUUID;
	static {
		Config c = new Config("plugins/bStats/config.yml");
		serverUUID = c.getString("serverUuid", UUID.randomUUID().toString());
		c.set("serverUuid", serverUUID).save(DataType.YAML);
	}

	// The plugin
	private final Plugin plugin;

	// The plugin id
	private final int pluginId;

	public Metrics(Plugin plugin, int pluginId) {
		plugins.add(plugin.getName());
		this.plugin = plugin;
		this.pluginId = pluginId;
		if (plugins.size() == 1)
			new Tasker() {

				@Override
				public void run() {
					submitData();
				}
			}.runRepeating(15 * 60 * 20, 15 * 60 * 20);
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
		data.put("plugins", plugins);
		try {
			Metrics.sendData(plugin, data);
		} catch (Exception e) {
		}
	}

	private static void sendData(Plugin plugin, Map<String, Object> data) throws Exception {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(Metrics.URL).openConnection();
		byte[] compressedData = Metrics.compress(Json.writer().simpleWrite(data));
		connection.setRequestMethod("POST");
		connection.addRequestProperty("Accept", "application/json");
		connection.addRequestProperty("Connection", "close");
		connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
		connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
		connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
		connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);
		connection.setDoOutput(true);
		try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
			outputStream.write(compressedData);
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