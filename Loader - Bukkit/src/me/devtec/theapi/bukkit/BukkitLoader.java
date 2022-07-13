package me.devtec.theapi.bukkit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.components.ComponentTransformer;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.json.JReader;
import me.devtec.shared.json.JWriter;
import me.devtec.shared.json.Json;
import me.devtec.shared.json.modern.ModernJsonReader;
import me.devtec.shared.json.modern.ModernJsonWriter;
import me.devtec.shared.placeholders.PlaceholderAPI;
import me.devtec.shared.scheduler.Scheduler;
import me.devtec.shared.scheduler.Tasker;
import me.devtec.shared.utility.LibraryLoader;
import me.devtec.shared.utility.StringUtils;
import me.devtec.shared.utility.StringUtils.ColormaticFactory;
import me.devtec.theapi.bukkit.bossbar.BossBar;
import me.devtec.theapi.bukkit.commands.hooker.BukkitCommandManager;
import me.devtec.theapi.bukkit.commands.hooker.SpigotSimpleCommandMap;
import me.devtec.theapi.bukkit.commands.selectors.BukkitSelectorUtils;
import me.devtec.theapi.bukkit.game.ResourcePackAPI;
import me.devtec.theapi.bukkit.game.ResourcePackAPI.ResourcePackResult;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;
import me.devtec.theapi.bukkit.nms.NmsProvider;
import me.devtec.theapi.bukkit.packetlistener.PacketHandler;
import me.devtec.theapi.bukkit.packetlistener.PacketHandlerModern;
import me.devtec.theapi.bukkit.packetlistener.PacketListener;

public class BukkitLoader extends JavaPlugin implements Listener {
	private static Method addUrl;
	private static NmsProvider nmsProvider;

	public static Map<UUID, HolderGUI> gui = new ConcurrentHashMap<>();

	private static PacketHandler<?> handler;
	public static Object airBlock;

	static Class<?> resource;
	static Class<?> close;
	static Class<?> click;
	static Class<?> itemname;
	public static List<BossBar> bossbars = new ArrayList<>();

	@Override
	@SuppressWarnings("unchecked")
	public void onLoad() {
		BukkitLoader.initTheAPI(this);
		new Metrics(this, 10581);
		try {
			BukkitLoader.nmsProvider = (NmsProvider) Class
					.forName("me.devtec.theapi.bukkit.nms." + Ref.serverVersion(), true, getClassLoader())
					.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (BukkitLoader.nmsProvider != null)
			BukkitLoader.nmsProvider.loadParticles();

		if (Ref.field(Command.class, "timings") != null && Ref.isOlderThan(9))
			Ref.set(Bukkit.getServer(), "commandMap", new SpigotSimpleCommandMap(Bukkit.getServer(),
					(Map<String, Command>) Ref.get(Ref.get(Bukkit.getPluginManager(), "commandMap"), "knownCommands")));

		if (new File("spigot.yml").exists() && new Config("spigot.yml").getBoolean("settings.late-bind"))
			new Thread(() -> { // ASYNC
				if (Ref.isNewerThan(7))
					BukkitLoader.handler = new PacketHandlerModern(true);
				else
					BukkitLoader.handler = (PacketHandler<?>) Ref
					.newInstanceByClass("me.devtec.theapi.bukkit.packetlistener.PacketHandlerLegacy", true);
			}).start();
		else if (Ref.isNewerThan(7))
			BukkitLoader.handler = new PacketHandlerModern(false);
		else
			BukkitLoader.handler = (PacketHandler<?>) Ref
			.newInstanceByClass("me.devtec.theapi.bukkit.packetlistener.PacketHandlerLegacy", false);
		BukkitLoader.resource = Ref.nmsOrOld("network.protocol.game.PacketPlayInResourcePackStatus",
				"PacketPlayInResourcePackStatus");
		BukkitLoader.close = Ref.nmsOrOld("network.protocol.game.PacketPlayInCloseWindow", "PacketPlayInCloseWindow");
		BukkitLoader.click = Ref.nmsOrOld("network.protocol.game.PacketPlayInWindowClick", "PacketPlayInWindowClick");
		BukkitLoader.itemname = Ref.nmsOrOld("network.protocol.game.PacketPlayInItemName", "PacketPlayInItemName");
		BukkitLoader.airBlock = Ref.invoke(
				Ref.getNulled(Ref.field(Ref.nmsOrOld("world.level.block.Block", "Block"), "AIR")), "getBlockData");
		if (BukkitLoader.airBlock == null)
			BukkitLoader.airBlock = Ref.getNulled(Ref.field(Ref.nmsOrOld("world.level.block.Blocks", "Blocks"), "AIR"));
		if (BukkitLoader.airBlock == null && Ref.isNewerThan(12))
			BukkitLoader.airBlock = Ref.invoke(
					Ref.get(Ref.cast(Ref.craft("block.data.CraftBlockData"), Bukkit.createBlockData(Material.AIR)),
							"state"),
					"getBlock");

		// BOSSBAR API: 1.7.10 - 1.8.8
		if (Ref.isOlderThan(9))
			new Tasker() {
			@Override
			public void run() {
				for (BossBar s : BukkitLoader.bossbars)
					s.move();
			}
		}.runRepeating(0, 20);
		else
			BukkitLoader.bossbars = null;
		new PacketListener() {

			@Override
			public boolean playOut(String player, Object packet, Object channel) {
				return false;
			}

			public boolean isAllowedChatCharacter(char var0) {
				return var0 != 167 && var0 >= ' ' && var0 != 127;
			}

			public String buildText(String var0) {
				StringBuilder var1 = new StringBuilder();
				char[] var2 = var0.toCharArray();
				int var3 = var2.length;

				for (int var4 = 0; var4 < var3; ++var4) {
					char var5 = var2[var4];
					if (isAllowedChatCharacter(var5))
						var1.append(var5);
				}

				return var1.toString();
			}

			@Override
			public boolean playIn(String nick, Object packet, Object channel) {
				if (nick == null)
					return false; // NPC
				// ResourcePackAPI
				if (BukkitLoader.resource != null && packet.getClass() == BukkitLoader.resource) {
					Player player = Bukkit.getPlayer(nick);
					if (player == null || ResourcePackAPI.getResourcePack(player) == null
							|| ResourcePackAPI.getHandlingPlayer(player) == null)
						return false;
					ResourcePackAPI.getHandlingPlayer(player).onHandle(player, ResourcePackAPI.getResourcePack(player),
							ResourcePackResult.valueOf(Ref.isNewerThan(16)
									? getLegacyNameOf(
											Ref.get(packet, Ref.isNewerThan(16) ? "a" : "status").toString())
											: Ref.get(packet, Ref.isNewerThan(16) ? "a" : "status").toString()));
					return false;
				}
				// GUIS
				if (packet.getClass() == BukkitLoader.itemname) {
					Player player = Bukkit.getPlayer(nick);
					if (player == null)
						return false;
					HolderGUI gui = BukkitLoader.gui.get(player.getUniqueId());
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
					HolderGUI gui = BukkitLoader.gui.remove(player.getUniqueId());
					if (gui == null)
						return false;
					gui.closeWithoutPacket(player);
					return true;
				}
				if (packet.getClass() == BukkitLoader.click) {
					Player player = Bukkit.getPlayer(nick);
					if (player == null)
						return false;
					HolderGUI gui = BukkitLoader.gui.get(player.getUniqueId());
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
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			PlaceholderAPI.PAPI_BRIDGE = new me.devtec.shared.placeholders.PlaceholderExpansion("PAPI Support") {
				@Override
				public String apply(String text, UUID player) {
					return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player==null?null:Bukkit.getOfflinePlayer(player), "%"+text+"%");
				}
			};
			new PlaceholderExpansion() {
				@Override
				public String onRequest(OfflinePlayer player, String params) {
					return PlaceholderAPI.apply("%" + params + "%", player==null?null:player.getUniqueId());
				}

				@Override
				public String getIdentifier() {
					return "theapi";
				}

				@Override
				public String getAuthor() {
					return "DevTec & StraikerinaCZ";
				}

				@Override
				public String getVersion() {
					return BukkitLoader.this.getDescription().getVersion();
				}
			}.register();
		}
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
		PlaceholderAPI.PAPI_BRIDGE=null;
		if (BukkitLoader.bossbars != null)
			for (BossBar bar : new ArrayList<>(BukkitLoader.bossbars))
				bar.remove();

		// OfflineCache support!
		API.offlineCache().saveToConfig().setFile(new File("plugins/TheAPI/Cache.dat")).save();
	}

	/**
	 * @apiNote Support for 1.7.10 - Latest {@link NmsProvider#getOnlinePlayers()}
	 */
	public static Collection<? extends Player> getOnlinePlayers() {
		return BukkitLoader.nmsProvider.getOnlinePlayers();
	}

	public static NmsProvider getNmsProvider() {
		return BukkitLoader.nmsProvider;
	}

	public static PacketHandler<?> getPacketHandler() {
		return BukkitLoader.handler;
	}

	public static boolean useItem(Player player, ItemStack stack, HolderGUI g, int slot, ClickType mouse) {
		ItemGUI d = g.getItemGUI(slot);
		boolean stolen = d == null || !d.isUnstealable();
		if (d != null)
			d.onClick(player, g, mouse);
		return !stolen;
	}

	public static ClickType buildClick(ItemStack stack, InventoryClickType type, int button, int mouse) {
		String action = stack.getType() == Material.AIR
				&& (type == InventoryClickType.PICKUP || type == InventoryClickType.QUICK_CRAFT) ? "DROP" : "PICKUP";
		action = (type == InventoryClickType.CLONE ? "MIDDLE_"
				: mouse == 0 ? "LEFT_" : mouse == 1 ? "RIGHT_" : "MIDDLE_") + action;
		if (type == InventoryClickType.QUICK_MOVE)
			action = "SHIFT_" + action;
		return ClickType.valueOf(action);
	}

	public enum InventoryClickType {
		PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL;
	}

	private static class SimpleClassLoader extends URLClassLoader {

		public SimpleClassLoader(URL[] urls) {
			super(urls);
		}

		@Override
		public void addURL(URL url) {
			super.addURL(url);
		}
	}

	private static class ImplementableJar extends JarFile {
		public List<JarFile> file = new ArrayList<>();

		public ImplementableJar(File file) throws IOException {
			super(file);
		}

		@Override
		public JarEntry getJarEntry(String name) {
			JarEntry find = super.getJarEntry(name);
			if (find == null)
				for (JarFile search : file) {
					find = search.getJarEntry(name);
					if (find != null)
						return find;
				}
			return null;
		}

		@Override
		public InputStream getInputStream(ZipEntry name) throws IOException {
			InputStream find = super.getInputStream(name);
			if (find == null)
				for (JarFile search : file) {
					find = search.getInputStream(name);
					if (find != null)
						return find;
				}
			return null;
		}

		@Override
		public void close() throws IOException {
			super.close();
			for (JarFile f : file)
				f.close();
			file.clear();
		}

	}

	private static int getJavaVersion() {
		String version = System.getProperty("java.version");
		if (version.startsWith("1."))
			version = version.substring(2, 3);
		else {
			int dot = version.indexOf(".");
			if (dot != -1)
				version = version.substring(0, dot);
		}
		return StringUtils.getInt(version);
	}

	private static void initTheAPI(JavaPlugin plugin) {
		Ref.init(Ref.getClass("net.md_5.bungee.api.ChatColor") != null
				? Ref.getClass("net.kyori.adventure.Adventure") != null ? ServerType.PAPER : ServerType.SPIGOT
						: ServerType.BUKKIT, Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]); // Server version
		// version
		if (Ref.serverType() != ServerType.BUKKIT) {
			ComponentAPI.registerTransformer("BUNGEECORD", (ComponentTransformer<?>) Ref
					.newInstanceByClass(Ref.getClass("me.devtec.shared.components.BungeeComponentAPI")));
			if (Ref.serverType() == ServerType.PAPER)
				ComponentAPI.registerTransformer("ADVENTURE", (ComponentTransformer<?>) Ref
						.newInstanceByClass(Ref.getClass("me.devtec.shared.components.AdventureComponentAPI")));
		}
		if (Ref.isNewerThan(7))
			Json.init(new ModernJsonReader(), new ModernJsonWriter()); // Modern version of Guava
		else
			Json.init((JReader) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.json.legacy.LegacyJsonReader")),
					(JWriter) Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.json.legacy.LegacyJsonWriter"))); // 1.7.10

		// Commands api
		API.commandsRegister = new BukkitCommandManager();
		API.selectorUtils = new BukkitSelectorUtils();

		// OfflineCache support!
		API.initOfflineCache(Bukkit.getOnlineMode(), new Config("plugins/TheAPI/Cache.dat"));

		API.library = new LibraryLoader() {
			List<File> loaded = new ArrayList<>();
			ImplementableJar jar;
			SimpleClassLoader lloader;

			@Override
			public void load(File file) {
				if (isLoaded(file) || !file.exists())
					return;
				loaded.add(file);
				ClassLoader loader = plugin.getClass().getClassLoader();
				if (BukkitLoader.getJavaVersion() <= 15) {
					if (BukkitLoader.addUrl == null)
						BukkitLoader.addUrl = Ref.method(URLClassLoader.class, "addURL", URL.class);
					try {
						Ref.invoke(loader, BukkitLoader.addUrl, file.toURI().toURL()); // Simple!
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				} else {
					if (Ref.isNewerThan(16) || Ref.isNewerThan(15) && Ref.serverType() == ServerType.PAPER)
						if (lloader == null) {
							try {
								lloader = new SimpleClassLoader(new URL[] { file.toURI().toURL() });
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
							Ref.set(loader, "library", lloader);
						} else
							try {
								lloader.addURL(file.toURI().toURL());
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
					try { // Just small hack for modern Java.. - Does not working for files inside jar
						if (jar == null) {
							jar = new ImplementableJar((File) Ref.get(loader, "file"));
							Ref.set(loader, "manifest", jar);
							Ref.set(loader, "jar", jar);
						}
						jar.file.add(new JarFile(file));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public boolean isLoaded(File file) {
				return loaded.contains(file);
			}
		};
		API.basics().load();
		StringUtils.rainbowSplit = Pattern.compile(
				"(#[A-Fa-f0-9]{6}([&§][K-Ok-oRr])*|[&§][Xx]([&§][A-Fa-f0-9]){6}([&§][K-Ok-oRr])*|[&§][A-Fa-f0-9K-ORrk-oUuXx]([&§][K-Ok-oRr])*)");
		StringUtils.color = new ColormaticFactory() {
			char[] characters = "abcdef0123456789".toCharArray();
			Random random = new Random();
			Pattern getLast = Pattern.compile(
					"(#[A-Fa-f0-9k-oK-ORrXxUu]{6}|§[Xx](§[A-Fa-f0-9k-oK-ORrXxUu]){6}|§[A-Fa-f0-9k-oK-ORrXxUu]|&[Uu])");
			Pattern hex = Pattern.compile("(#[a-fA-F0-9]{6})");
			String rainbow = "c6ea9b5";

			char[] chars = rainbow.toCharArray();
			AtomicInteger position = new AtomicInteger(0);

			@Override
			public String gradient(String msg, String fromHex, String toHex) {
				if (Ref.isNewerThan(15)) // Hex
					return API.basics().gradient(msg, fromHex, toHex);
				String split = msg.replace("", "<>");

				StringBuilder builder = new StringBuilder();
				boolean inRainbow = false;
				char prev = 0;
				String formats = "";

				for (String s : split.split("<>")) {
					if (s.isEmpty())
						continue;
					char c = s.charAt(0);
					if (prev == '&' || prev == '§') {
						if (prev == '&' && s.charAt(0) == 'u') {
							builder.deleteCharAt(builder.length() - 1); // remove & char
							inRainbow = true;
							prev = c;
							continue;
						}
						if (inRainbow && prev == '§' && (isColor(s.charAt(0)) || isFormat(s.charAt(0)))) {
							if (isFormat(s.charAt(0))) {
								if (s.charAt(0) == 'r')
									formats = "§r";
								else
									formats += "§" + s.charAt(0);
								prev = c;
								continue;
							}
							builder.delete(builder.length() - 14, builder.length()); // remove &<random color> string
							inRainbow = false;
						}
					}
					if (c != ' ' && inRainbow)
						if (formats.equals("§r")) {
							builder.append(formats); // add formats
							builder.append(generateColor()); // add random color
							formats = "";
						} else {
							builder.append(generateColor()); // add random color
							builder.append(formats); // add formats
						}
					builder.append(c);
					prev = c;
				}
				return builder.toString();
			}

			private boolean isColor(int charAt) {
				return charAt >= 97 && charAt <= 102 || charAt >= 65 && charAt <= 70 || charAt >= 48 && charAt <= 57;
			}

			private boolean isFormat(int charAt) {
				return charAt >= 107 && charAt <= 111 || charAt == 114;
			}

			@Override
			public String generateColor() {
				if (!Ref.isNewerThan(15)) {
					if (position.get() == chars.length)
						position.set(0);
					return "§" + chars[position.getAndIncrement()];
				}
				StringBuilder b = new StringBuilder("#");
				for (int i = 0; i < 6; ++i)
					b.append(characters[random.nextInt(16)]);
				return b.toString();
			}

			@Override
			public String[] getLastColors(String text) {
				return API.basics().getLastColors(getLast, text);
			}

			@Override
			public String replaceHex(String text) {
				if (!Ref.isNewerThan(15))
					return text;
				String msg = text;
				Matcher match = hex.matcher(msg);
				while (match.find()) {
					String color = match.group();
					String hex = "§x";
					for (char c : color.substring(1).toCharArray())
						hex += "§" + c;
					msg = msg.replace(color, hex);
				}
				return msg;
			}

			@Override
			public String rainbow(String msg, String fromHex, String toHex) {
				if (Ref.isNewerThan(15)) // Hex
					return API.basics().rainbow(msg, fromHex, toHex);
				return gradient(msg, null, null);
			}
		};
	}
}
