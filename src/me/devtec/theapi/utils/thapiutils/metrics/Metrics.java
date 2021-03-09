package me.devtec.theapi.utils.thapiutils.metrics;

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

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.datakeeper.Data;

@SuppressWarnings("unchecked")
public class Metrics {

    // This ThreadFactory enforces the naming convention for our Threads
    private final ThreadFactory threadFactory = task -> new Thread(task, "bStats-Metrics");

    // Executor service for requests
    // We use an executor service because the Bukkit scheduler is affected by server lags
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, threadFactory);

    // The version of this bStats class
    public static final int B_STATS_VERSION = 1;

    // The url to which the data is sent
    private static final String URL = "https://bStats.org/submitData/bukkit";

    // Is bStats enabled on this server?
    private boolean enabled;

    // Should failed requests be logged?
    private static boolean logFailedRequests;

    // Should the sent data be logged?
    private static boolean logSentData;

    // Should the response text be logged?
    private static boolean logResponseStatusText;

    // The uuid of the server
    private static String serverUUID;

    // The plugin
    private final Plugin plugin;

    // The plugin id
    private final int pluginId;

	public Metrics(Plugin plugin, int pluginId) {
        this.plugin = plugin;
        this.pluginId = pluginId;
        Data c = new Data("plugins/bStats/config.yml");
        boolean found = false;
        enabled = c.getBoolean("enabled");
        serverUUID = c.getString("serverUuid");
        logFailedRequests = c.getBoolean("logFailedRequests");
        logSentData = c.getBoolean("logSentData");
        logResponseStatusText = c.getBoolean("logResponseStatusText");
        if (enabled) {
            for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
                try {
                    service.getField("B_STATS_VERSION");
                    found=true;
                } catch (Exception ignored) { }
            }
            Bukkit.getServicesManager().register(Metrics.class, this, plugin, ServicePriority.Normal);
            if(!found)
            startSubmitting();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    private void startSubmitting() {
        final Runnable submitTask = () -> {
            if (!plugin.isEnabled()) {
                scheduler.shutdown();
                return;
            }
            Bukkit.getScheduler().runTask(plugin, this::submitData);
        };
        scheduler.scheduleAtFixedRate(submitTask, 15, 15, TimeUnit.MINUTES);
    }

    public JSONObject getPluginData() {
    	JSONObject data = new JSONObject();
        data.put("pluginName", plugin.getDescription().getName());
        data.put("id", pluginId);
        data.put("pluginVersion", plugin.getDescription().getVersion());
        data.put("customCharts", new JSONArray());
        return data;
    }

	private JSONObject getServerData() {
        // Minecraft specific data
        int playerAmount = TheAPI.getOnlineCount();
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
        data.put("serverUUID", serverUUID);
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
        final JSONObject data = getServerData();
        JSONArray pluginData = new JSONArray();
        for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
            try {
                service.getField("B_STATS_VERSION"); // Our identifier :)
                for (RegisteredServiceProvider<?> provider : Bukkit.getServicesManager().getRegistrations(service)) {
                    try {
                        Object plugin = provider.getService().getMethod("getPluginData").invoke(provider.getProvider());
                        if (plugin instanceof JSONObject) {
                            pluginData.add((JSONObject) plugin);
                        } else {
                            try {
                            	JSONObject object = (JSONObject) new JSONParser().parse(plugin.toString());
                                pluginData.add(object);
                            } catch (Exception e) {
                                if (logFailedRequests) {
                                    this.plugin.getLogger().log(Level.SEVERE, "Encountered unexpected exception", e);
                                }
                            }
                        }
                    } catch (Exception | NoSuchFieldError ignored) { }
                }
            } catch (Exception | NoSuchFieldError ignored) { }
        }
        data.put("plugins", pluginData);
        new Thread(() -> {
            try {
                sendData(plugin, data);
            } catch (Exception e) {
                if (logFailedRequests) {
                    plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of " + plugin.getName(), e);
                }
            }
        }).start();
    }
    
    private static void sendData(Plugin plugin, JSONObject data) throws Exception {
        if (logSentData) {
            plugin.getLogger().info("Sending data to bStats: " + data);
        }
        HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();
        byte[] compressedData = compress(data.toString());
        connection.setRequestMethod("POST");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Connection", "close");
        connection.addRequestProperty("Content-Encoding", "gzip");
        connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);
        connection.setDoOutput(true);
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.write(compressedData);
        }
        if (logResponseStatusText) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        }
        plugin.getLogger().info("Sent data to bStats and received response: " + builder);
        }
    }

    private static byte[] compress(final String str) throws IOException {
        if (str == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(str.getBytes(StandardCharsets.UTF_8));
        }
        return outputStream.toByteArray();
    }
}