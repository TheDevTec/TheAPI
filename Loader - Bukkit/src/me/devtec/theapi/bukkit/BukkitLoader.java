package me.devtec.theapi.bukkit;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.tools.ToolProvider;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.commands.structures.CommandStructure;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.DataType;
import me.devtec.shared.dataholder.StringContainer;
import me.devtec.shared.placeholders.PlaceholderAPI;
import me.devtec.shared.placeholders.PlaceholderExpansion;
import me.devtec.shared.scheduler.Scheduler;
import me.devtec.shared.utility.MemoryCompiler;
import me.devtec.shared.utility.StringUtils;
import me.devtec.shared.versioning.VersionUtils;
import me.devtec.shared.versioning.VersionUtils.Version;
import me.devtec.theapi.bukkit.bossbar.BossBar;
import me.devtec.theapi.bukkit.commands.hooker.SpigotSimpleCommandMap;
import me.devtec.theapi.bukkit.game.resourcepack.ResourcePackHandler;
import me.devtec.theapi.bukkit.game.resourcepack.ResourcePackResult;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.nms.NmsProvider;
import me.devtec.theapi.bukkit.packetlistener.PacketHandler;
import me.devtec.theapi.bukkit.packetlistener.PacketHandlerModern;
import me.devtec.theapi.bukkit.packetlistener.PacketListener;
import me.devtec.theapi.bukkit.tablist.Tablist;

public class BukkitLoader extends JavaPlugin implements Listener {
	// public APIs
	public static NmsProvider nmsProvider;
	public static PacketHandler<?> handler;

	// private fields
	private static Class<?> serverPing;
	private static Class<?> playerInfo;
	private static Class<?> resource;
	private static Class<?> close;
	private static Class<?> click;
	private static Class<?> itemname;
	private static double release;

	// public plugin fields
	public Map<UUID, HolderGUI> gui = new ConcurrentHashMap<>();
	public List<BossBar> bossbars = new ArrayList<>();
	public Map<UUID, ResourcePackHandler> resourcePackHandler = new ConcurrentHashMap<>();

	/**
	 * @apiNote Get online players on the server
	 *          {@link NmsProvider#getOnlinePlayers()}
	 */
	public static Collection<? extends Player> getOnlinePlayers() {
		return BukkitLoader.nmsProvider.getOnlinePlayers();
	}

	/**
	 * @apiNote Get NmsProvider @see {@link BukkitLoader#nmsProvider} - Can be null
	 *          if NmsProvider failed load or isn't loaded yet
	 */
	public static NmsProvider getNmsProvider() {
		return BukkitLoader.nmsProvider;
	}

	/**
	 * @apiNote Get PacketHandler @see {@link BukkitLoader#handler} - Can be null if
	 *          PacketHandler isn't loaded yet
	 */
	public static PacketHandler<?> getPacketHandler() {
		return BukkitLoader.handler;
	}

	/**
	 * @apiNote Get Api release version
	 */
	public static double getApiRelease() {
		return release;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onLoad() {
		BukkitLibInit.initTheAPI(this);

		release = Config.loadFromInput(getResource("release.yml")).getDouble("release");

		try {
			loadProvider();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Ref.field(Command.class, "timings") != null && Ref.isOlderThan(9))
			Ref.set(Bukkit.getServer(), "commandMap",
					new SpigotSimpleCommandMap(Bukkit.getServer(), (Map<String, Command>) Ref.get(Ref.get(Bukkit.getPluginManager(), "commandMap"), "knownCommands")));

		if (new File("spigot.yml").exists() && new Config("spigot.yml").getBoolean("settings.late-bind"))
			new Thread(() -> { // ASYNC
				if (Ref.isNewerThan(7))
					handler = new PacketHandlerModern(true);
				else
					handler = (PacketHandler<?>) Ref.newInstanceByClass("me.devtec.theapi.bukkit.packetlistener.PacketHandlerLegacy", true);
			}).start();

		else if (Ref.isNewerThan(7))
			handler = new PacketHandlerModern(false);
		else
			handler = (PacketHandler<?>) Ref.newInstanceByClass("me.devtec.theapi.bukkit.packetlistener.PacketHandlerLegacy", false);

		resource = Ref.nms("network.protocol.game", "PacketPlayInResourcePackStatus");
		close = Ref.nms("network.protocol.game", "PacketPlayInCloseWindow");
		serverPing = Ref.nms("network.protocol.status", "PacketStatusOutServerInfo");
		playerInfo = Ref.nms("network.protocol.status", "PacketPlayOutPlayerInfo");
		click = Ref.nms("network.protocol.game", "PacketPlayInWindowClick");
		itemname = Ref.nms("network.protocol.game", "PacketPlayInItemName");

		// BOSSBAR API: 1.7.10 - 1.8.8
		if (!Ref.isOlderThan(9))
			bossbars = null;

		new PacketListener() {

			@Override
			public boolean playOut(String nick, Object packet, Object channel) {
				if (packet.getClass() == serverPing)
					return nmsProvider.processServerListPing(nick, channel, packet);
				if (packet.getClass() == playerInfo) {
					Player player = Bukkit.getPlayer(nick);
					if (player != null)
						nmsProvider.processPlayerInfo(player, channel, packet, Tablist.of(player));
				}
				return false;
			}

			public boolean isAllowedChatCharacter(char var0) {
				return var0 != 167 && var0 >= ' ' && var0 != 127;
			}

			public String buildText(String text) {
				StringContainer builder = new StringContainer(text.length());
				for (int i = 0; i < text.length(); ++i) {
					char c = text.charAt(i);
					if (isAllowedChatCharacter(c))
						builder.append(c);
				}
				return builder.toString();
			}

			@Override
			public boolean playIn(String nick, Object packet, Object channel) {
				if (nick == null)
					return false; // NPC
				// ResourcePackAPI
				if (packet.getClass() == BukkitLoader.resource) {
					Player player = Bukkit.getPlayer(nick);
					ResourcePackHandler handler;
					if (player == null || (handler = resourcePackHandler.remove(player.getUniqueId())) == null)
						return false;
					handler.call(player, ResourcePackResult.valueOf(Ref.isNewerThan(16) ? getLegacyNameOf(Ref.get(packet, Ref.isNewerThan(16) ? "a" : "status").toString())
							: Ref.get(packet, Ref.isNewerThan(16) ? "a" : "status").toString()));
					return false;
				}
				// GUIS
				if (packet.getClass() == BukkitLoader.itemname) {
					Player player = Bukkit.getPlayer(nick);
					if (player == null)
						return false;
					HolderGUI gui = BukkitLoader.this.gui.get(player.getUniqueId());
					if (gui instanceof AnvilGUI) {
						BukkitLoader.nmsProvider.postToMainThread(() -> {
							((AnvilGUI) gui).setRepairText(buildText(Ref.get(packet, "a") + ""));
						});
						return true;
					}
				}
				if (packet.getClass() == BukkitLoader.close) {
					Player player = Bukkit.getPlayer(nick);
					if (player == null)
						return false;
					HolderGUI gui = BukkitLoader.this.gui.remove(player.getUniqueId());
					if (gui == null)
						return false;
					gui.closeWithoutPacket(player);
					return true;
				}
				if (packet.getClass() == BukkitLoader.click) {
					Player player = Bukkit.getPlayer(nick);
					if (player == null)
						return false;
					HolderGUI gui = BukkitLoader.this.gui.get(player.getUniqueId());
					return gui == null ? false : BukkitLoader.nmsProvider.processInvClickPacket(player, gui, packet);
				}
				return false;
			}

			private String getLegacyNameOf(String string) {
				switch (string.charAt(0)) {
				case 'a':
					return "SUCCESSFULLY_LOADED";
				case 'b':
					return "DECLINED";
				case 'c':
					return "FAILED_DOWNLOAD";
				case 'd':
					return "ACCEPTED";
				}
				return null;
			}
		}.register();

		new Metrics(this, 10581);
	}

	private void loadProvider() throws Exception {
		if (ToolProvider.getSystemJavaCompiler() != null) { // JDK
			getAllJarFiles();
			checkForUpdateAndDownload();
			nmsProvider = (NmsProvider) new MemoryCompiler("me.devtec.theapi.bukkit.nms." + Ref.serverVersion(), new File("plugins/TheAPI/NmsProviders/" + Ref.serverVersion() + ".java")).buildClass()
					.newInstance();
			if (nmsProvider != null)
				nmsProvider.loadParticles();
		} else { // JRE
			checkForUpdateAndDownloadCompiled();
			try (URLClassLoader cl = new URLClassLoader(new URL[] { new URL("jar:file:" + "plugins/TheAPI/NmsProviders/" + Ref.serverVersion() + ".jar" + "!/") }, getClassLoader())) {
				Class<?> c = cl.loadClass("me.devtec.theapi.bukkit.nms." + Ref.serverVersion());
				nmsProvider = (NmsProvider) c.newInstance();
				if (nmsProvider != null)
					nmsProvider.loadParticles();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			PlaceholderAPI.PAPI_BRIDGE = new PlaceholderExpansion("PAPI Support") {
				@Override
				public String apply(String text, UUID player) {
					return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player == null ? null : Bukkit.getOfflinePlayer(player), "%" + text + "%");
				}
			};
			for (PlaceholderExpansion exp : PlaceholderAPI.getPlaceholders())
				((me.clip.placeholderapi.expansion.PlaceholderExpansion) exp.setPapiInstance(new me.clip.placeholderapi.expansion.PlaceholderExpansion() {
					@Override
					public String onRequest(OfflinePlayer player, String params) {
						return exp.apply(params, player == null ? null : player.getUniqueId());
					}

					@Override
					public String getName() {
						return exp.getName();
					}

					@Override
					public String getIdentifier() {
						return exp.getName().toLowerCase();
					}

					@Override
					public String getAuthor() {
						return "(Unknown) TheAPI Provided Placeholder";
					}

					@Override
					public String getVersion() {
						return BukkitLoader.this.getDescription().getVersion();
					}
				}).getPapiInstance()).register();
			PlaceholderAPI.registerConsumer = exp -> ((me.clip.placeholderapi.expansion.PlaceholderExpansion) exp.setPapiInstance(new me.clip.placeholderapi.expansion.PlaceholderExpansion() {
				@Override
				public String onRequest(OfflinePlayer player, String params) {
					return exp.apply(params, player == null ? null : player.getUniqueId());
				}

				@Override
				public String getName() {
					return exp.getName();
				}

				@Override
				public String getIdentifier() {
					return exp.getName().toLowerCase();
				}

				@Override
				public String getAuthor() {
					return "(Unknown) TheAPI Provided Placeholder";
				}

				@Override
				public String getVersion() {
					return BukkitLoader.this.getDescription().getVersion();
				}
			}).getPapiInstance()).register();
			PlaceholderAPI.unregisterConsumer = exp -> ((me.clip.placeholderapi.expansion.PlaceholderExpansion) exp.getPapiInstance()).unregister();
		}

		// Command to reload NmsProvider
		CommandStructure.create(ConsoleCommandSender.class, (sender, perm, isTablist) -> sender.hasPermission(perm), (sender, structure, args) -> {
			try {
				loadProvider();
				sender.sendMessage(StringUtils.colorize("&5TheAPI &8» &7NmsProvider &asuccesfully &7reloaded."));
			} catch (Exception e) {
				sender.sendMessage(StringUtils.colorize("&5TheAPI &8» &7An &cerror &7occurred when reloading NmsProvider."));
				sender.sendMessage(StringUtils.colorize("&5TheAPI &8» &7&nDO NOT MODIFY THIS FILE IF YOU DON'T KNOW WHAT ARE YOU DOING!"));
			}
		}).permission("theapireload.command").build().register("theapireload");
	}

	@EventHandler
	public void onAsyncPreLoginEvent(AsyncPlayerPreLoginEvent e) {
		API.offlineCache().setLookup(e.getUniqueId(), e.getName());
	}

	@EventHandler
	public void onPreLoginEvent(PlayerPreLoginEvent e) { // fix uuid - premium login?
		API.offlineCache().setLookup(e.getUniqueId(), e.getName());
	}

	@EventHandler
	public void onLoginEvent(PlayerLoginEvent e) { // fix uuid - premium login?
		API.offlineCache().setLookup(e.getPlayer().getUniqueId(), e.getPlayer().getName());
		handler.add(e.getPlayer());
		Tablist tab = Tablist.of(e.getPlayer());
		for (Player player : BukkitLoader.getOnlinePlayers())
			if (!player.getUniqueId().equals(e.getPlayer().getUniqueId()))
				Tablist.of(player).addEntry(tab.asEntry(tab));
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		Config cache = API.removeCache(e.getPlayer().getUniqueId());
		if (cache != null)
			cache.save();
	}

	@Override
	public void onDisable() {
		API.setEnabled(false);
		Scheduler.cancelAll();
		BukkitLoader.handler.close();
		PlaceholderAPI.PAPI_BRIDGE = null;
		if (bossbars != null)
			for (BossBar bar : new ArrayList<>(bossbars))
				bar.remove();

		// OfflineCache support!
		API.offlineCache().saveToConfig().setFile(new File("plugins/TheAPI/Cache.dat")).save();
	}

	private void checkForUpdateAndDownloadCompiled() {
		try {
			Config gitVersion = Config.loadFromInput(
					new URL("https://raw.githubusercontent.com/TheDevTec/TheAPI/master/NmsProvider%20-%20" + Ref.serverVersion().substring(1).replace("_", ".") + "/version.yml").openStream());

			Config localVersion = new Config("plugins/TheAPI/version.yml");

			localVersion.setIfAbsent("build", 1);
			localVersion.setComments("build", Arrays.asList("# DO NOT MODIFY THIS VALUE"));

			Version ver = VersionUtils.getVersion(gitVersion.getString("release"), "" + release);

			if (ver != Version.OLDER_VERSION && ver != Version.SAME_VERSION && new File("plugins/TheAPI/NmsProviders/" + Ref.serverVersion() + ".jar").exists()) {
				Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §cERROR! Can't download new NmsProvider, please update TheAPI.");
				localVersion.save(DataType.YAML);
				return;
			}
			if (localVersion.getInt("build") < gitVersion.getInt("build") || !new File("plugins/TheAPI/NmsProviders/" + Ref.serverVersion() + ".jar").exists()) {
				localVersion.set("build", gitVersion.getInt("build"));
				localVersion.save(DataType.YAML);

				URL url = new URL(
						"https://raw.githubusercontent.com/TheDevTec/TheAPI/master/NmsProvider%20-%20" + Ref.serverVersion().substring(1).replace("_", ".") + "/" + Ref.serverVersion() + ".jar");
				Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §aDownloading update!");
				API.library.downloadFileFromUrl(url, new File("plugins/TheAPI/NmsProviders/" + Ref.serverVersion() + ".jar"));
			}
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §eNot found NmsProvider for your server version, do you have your own?");
		}
	}

	private void checkForUpdateAndDownload() {
		try {
			Config gitVersion = Config.loadFromInput(new URL("https://raw.githubusercontent.com/TheDevTec/TheAPI/master/version.yml").openStream());

			Config localVersion = new Config("plugins/TheAPI/version.yml");

			localVersion.setIfAbsent("build", 1);
			localVersion.setComments("build", Arrays.asList("# DO NOT MODIFY THIS VALUE"));

			Version ver = VersionUtils.getVersion(gitVersion.getString("release"), "" + release);

			if (ver != Version.OLDER_VERSION && ver != Version.SAME_VERSION && new File("plugins/TheAPI/NmsProviders/" + Ref.serverVersion() + ".java").exists()) {
				Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §cERROR! Can't download new NmsProvider, please update TheAPI.");
				Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §cERROR! Current release: " + release);
				Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §cERROR! Required release: " + gitVersion.getString("release"));
				localVersion.save(DataType.YAML);
				return;
			}
			if (localVersion.getInt("build") < gitVersion.getInt("build") || !new File("plugins/TheAPI/NmsProviders/" + Ref.serverVersion() + ".java").exists()) {
				localVersion.set("build", gitVersion.getInt("build"));
				localVersion.save(DataType.YAML);

				URL url = new URL("https://raw.githubusercontent.com/TheDevTec/TheAPI/master/NmsProvider%20-%20" + Ref.serverVersion().substring(1).replace("_", ".")
						+ "/src/me/devtec/theapi/bukkit/nms/" + Ref.serverVersion() + ".java");
				Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §aDownloading update!");
				API.library.downloadFileFromUrl(url, new File("plugins/TheAPI/NmsProviders/" + Ref.serverVersion() + ".java"));
			}
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("[TheAPI NmsProvider Updater] §eNot found NmsProvider for your server version, do you have your own?");
		}
	}

	private void getAllJarFiles() throws URISyntaxException {
		StringContainer args = new StringContainer(1024);
		File file = new File(Bukkit.getServer().getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		String fixedPath = file.getName();
		while (file.getParentFile() != null && !isInsidePath(file.getParentFile().toPath(), new File(System.getProperty("java.class.path")).toPath())) {
			fixedPath = file.getParentFile().getName() + "/" + fixedPath;
			file = file.getParentFile();
		}
		MemoryCompiler.allJars += (System.getProperty("os.name").toLowerCase().contains("win") ? ";" : ":") + "./" + fixedPath;
		addAllJarFiles(args, new File("plugins"), false); // Plugins
		addAllJarFiles(args, new File("libraries"), true); // Libraries
		MemoryCompiler.allJars += args.toString();
	}

	private boolean isInsidePath(Path current, Path file) {
		return current.equals(file.toAbsolutePath().getParent());
	}

	private void addAllJarFiles(StringContainer args, File folder, boolean sub) {
		if (!folder.exists())
			return;
		File[] files = folder.listFiles();
		if (files != null)
			for (File file : files)
				if (file.isDirectory() && sub)
					addAllJarFiles(args, file, sub);
				else if (file.getName().endsWith(".jar"))
					args.append(System.getProperty("os.name").toLowerCase().contains("win") ? ';' : ':').append('.').append('/').append(file.getPath());
	}
}
