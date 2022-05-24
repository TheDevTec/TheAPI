package me.devtec.theapi.bukkit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import me.devtec.shared.dataholder.Config;

@SuppressWarnings("unchecked")
public class Metrics {

	// This ThreadFactory enforces the naming convention for our Threads
	private final ThreadFactory threadFactory = task -> new Thread(task, "bStats-Metrics");

	// Executor service for requests
	// We use an executor service because the Bukkit scheduler is affected by server
	// lags
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, this.threadFactory);

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
		boolean found = false;
		if (Metrics.enabled) {
			for (Class<?> service : Bukkit.getServicesManager().getKnownServices())
				try {
					service.getField("B_STATS_VERSION");
					found = true;
				} catch (Exception ignored) {
				}
			Bukkit.getServicesManager().register(Metrics.class, this, plugin, ServicePriority.Normal);
			if (!found)
				this.startSubmitting();
		}
	}

	public boolean isEnabled() {
		return Metrics.enabled;
	}

	private void startSubmitting() {
		final Runnable submitTask = () -> {
			if (!this.plugin.isEnabled()) {
				this.scheduler.shutdown();
				return;
			}
			Bukkit.getScheduler().runTask(this.plugin, this::submitData);
		};
		this.scheduler.scheduleAtFixedRate(submitTask, 15, 15, TimeUnit.MINUTES);
	}

	public JSONObject getPluginData() {
		JSONObject data = new JSONObject();
		data.put("pluginName", this.plugin.getDescription().getName());
		data.put("id", this.pluginId);
		data.put("pluginVersion", this.plugin.getDescription().getVersion());
		data.put("customCharts", new JSONArray());
		return data;
	}

	private JSONObject getServerData() {
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

		JSONObject data = new JSONObject();
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
		final JSONObject data = this.getServerData();
		JSONArray pluginData = new JSONArray();
		for (Class<?> service : Bukkit.getServicesManager().getKnownServices())
			try {
				service.getField("B_STATS_VERSION"); // Our identifier :)
				for (RegisteredServiceProvider<?> provider : Bukkit.getServicesManager().getRegistrations(service))
					try {
						Object plugin = provider.getService().getMethod("getPluginData").invoke(provider.getProvider());
						if (plugin instanceof JSONObject)
							pluginData.add(plugin);
						else
							try {
								JSONObject object = (JSONObject) new JSONParser().parse(plugin.toString());
								pluginData.add(object);
							} catch (Exception e) {
								if (Metrics.logFailedRequests)
									this.plugin.getLogger().log(Level.SEVERE, "Encountered unexpected exception", e);
							}
					} catch (Exception | NoSuchFieldError ignored) {
					}
			} catch (Exception | NoSuchFieldError ignored) {
			}
		data.put("plugins", pluginData);
		new Thread(() -> {
			try {
				Metrics.sendData(this.plugin, data);
			} catch (Exception e) {
				if (Metrics.logFailedRequests)
					this.plugin.getLogger().log(Level.WARNING,
							"Could not submit plugin stats of " + this.plugin.getName(), e);
			}
		}).start();
	}

	private static void sendData(Plugin plugin, JSONObject data) throws Exception {
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
			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()))) {
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