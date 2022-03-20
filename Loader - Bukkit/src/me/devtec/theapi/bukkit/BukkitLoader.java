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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.devtec.shared.API;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.Adventure;
import me.devtec.shared.components.Bungee;
import me.devtec.shared.components.ComponentAPI;
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
import me.devtec.theapi.bukkit.game.ResourcePackAPI;
import me.devtec.theapi.bukkit.game.ResourcePackAPI.ResourcePackResult;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;
import me.devtec.theapi.bukkit.nms.NmsProvider;
import me.devtec.theapi.bukkit.packetlistener.PacketHandler;
import me.devtec.theapi.bukkit.packetlistener.PacketHandler_New;
import me.devtec.theapi.bukkit.packetlistener.PacketListener;

public class BukkitLoader extends JavaPlugin {
	private static Method addUrl;
	private static NmsProvider nmsProvider;
	
	public static Map<UUID, HolderGUI> gui = new HashMap<>();

	private static PacketHandler<?> handler;
	public static Object airBlock;

	static Class<?> resource, close, click, itemname;
	public static List<BossBar> bossbars = new ArrayList<>();
	private me.devtec.shared.placeholders.PlaceholderExpansion placeholders;
	
	public void onLoad() {
		initTheAPI(this);
		new Metrics(this, 10581);
		boolean mohist = false;
		try {
			if(Class.forName("com.mohistmc.MohistMC", true, getClassLoader())!=null)mohist=true;
		}catch(Exception | NoClassDefFoundError err) {
			
		}
		try {
			nmsProvider=(NmsProvider) Class.forName("me.devtec.theapi.bukkit.nms."+Ref.serverVersion()+(mohist?"_Mohist":""),true,getClassLoader()).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(nmsProvider!=null)
			nmsProvider.loadParticles();
		if(new File("spigot.yml").exists() && new Config("spigot.yml").getBoolean("settings.late-bind")) {
			new Thread(() -> { //ASYNC
				if (Ref.isNewerThan(7))
					handler = new PacketHandler_New(true);
				else
					handler = (PacketHandler<?>)Ref.newInstanceByClass("me.devtec.theapi.bukkit.packetlistener.PacketHandler_Legacy", true);
			}).start();
		}else {
			if (Ref.isNewerThan(7))
				handler = new PacketHandler_New(false);
			else
				handler = (PacketHandler<?>)Ref.newInstanceByClass("me.devtec.theapi.bukkit.packetlistener.PacketHandler_Legacy", false);
		}
		resource = Ref.nmsOrOld("network.protocol.game.PacketPlayInResourcePackStatus","PacketPlayInResourcePackStatus");
		close = Ref.nmsOrOld("network.protocol.game.PacketPlayInCloseWindow","PacketPlayInCloseWindow");
		click = Ref.nmsOrOld("network.protocol.game.PacketPlayInWindowClick","PacketPlayInWindowClick");
		itemname = Ref.nmsOrOld("network.protocol.game.PacketPlayInItemName", "PacketPlayInItemName");
		airBlock = Ref.invoke(Ref.getNulled(Ref.field(Ref.nmsOrOld("world.level.block.Block","Block"), "AIR")), "getBlockData");
		if(airBlock==null)
			airBlock=Ref.getNulled(Ref.field(Ref.nmsOrOld("world.level.block.Blocks","Blocks"), "AIR"));
		if(airBlock==null && Ref.isNewerThan(12))
			airBlock=Ref.invoke(Ref.get(Ref.cast(Ref.craft("block.data.CraftBlockData"), Bukkit.createBlockData(Material.AIR)), "state"),"getBlock");
		
		//BOSSBAR API: 1.7.10 - 1.8.8
		if (Ref.isOlderThan(9)) {
			new Tasker() {
				public void run() {
					for (BossBar s : bossbars)
						s.move();
				}
			}.runRepeating(0, 20);
		}else bossbars = null;
		new PacketListener() {
			
			@Override
			public boolean PacketPlayOut(String player, Object packet, Object channel) {
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
					if (isAllowedChatCharacter(var5)) {
						var1.append(var5);
					}
				}

				return var1.toString();
			}
			
			public boolean PacketPlayIn(String nick, Object packet, Object channel) {
				if(nick==null)return false; //NPC
				//ResourcePackAPI
				if(resource!=null && packet.getClass()==resource) {
					Player player = Bukkit.getPlayer(nick);
					if(player==null)return false;
					if(ResourcePackAPI.getResourcePack(player)==null||ResourcePackAPI.getHandlingPlayer(player)==null)return false;
					ResourcePackAPI.getHandlingPlayer(player).onHandle(player, ResourcePackAPI.getResourcePack(player), ResourcePackResult.valueOf(Ref.isNewerThan(16)?getLegacyNameOf(Ref.get(packet, Ref.isNewerThan(16)?"a":"status").toString()):Ref.get(packet, Ref.isNewerThan(16)?"a":"status").toString()));
					return false;
				}
				//GUIS
				if(packet.getClass()==itemname) {
					Player player = Bukkit.getPlayer(nick);
					if(player==null)return false;
					HolderGUI gui = BukkitLoader.gui.get(player.getUniqueId());
					if(gui!=null && gui instanceof AnvilGUI) {
					    nmsProvider.postToMainThread(() -> {
					    	((AnvilGUI)gui).setRepairText(buildText(Ref.get(packet, "a")+""));
					    });
						return true;
					}
				}
				if(packet.getClass()==close) {
					Player player = Bukkit.getPlayer(nick);
					if(player==null)return false;
					HolderGUI gui = BukkitLoader.gui.remove(player.getUniqueId());
					if(gui==null)return false;
					gui.closeWithoutPacket(player);
					return true;
				}
				if(packet.getClass()==click) {
					Player player = Bukkit.getPlayer(nick);
					if(player==null)return false;
					HolderGUI gui = BukkitLoader.gui.get(player.getUniqueId());
					return gui==null?false:nmsProvider.processInvClickPacket(player, gui, packet);
				}
				return false;
			}

			private String getLegacyNameOf(String string) {
				switch(string.charAt(0)) {
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
	
	public void onEnable() {
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!=null) {
			placeholders = new me.devtec.shared.placeholders.PlaceholderExpansion("PAPI Support") {
				public String apply(String text, UUID player) {
					return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(player), text);
				}
			}.register();
			new PlaceholderExpansion() {
				@Override
				public String onRequest(OfflinePlayer player, String params) {
					return PlaceholderAPI.apply("%"+params+"%", player.getUniqueId());
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
	
	public void onDisable() {
		API.setEnabled(false);
		Scheduler.cancelAll();
		handler.close();
		if(placeholders!=null)
			placeholders.unregister();
		if(bossbars!=null)
			for(BossBar bar : new ArrayList<>(bossbars))bar.remove();
	}
	
	/**
	 * @apiNote Support for 1.7.10 - Latest
	 * {@link NmsProvider#getOnlinePlayers()}
	 */
	public static Collection<? extends Player> getOnlinePlayers() {
		return nmsProvider.getOnlinePlayers();
	}
	
	public static NmsProvider getNmsProvider() {
		return nmsProvider;
	}
	
	public static PacketHandler<?> getPacketHandler() {
		return handler;
	}
	
	public static boolean useItem(Player player, ItemStack stack, HolderGUI g, int slot, ClickType mouse) {
		ItemGUI d = g.getItemGUI(slot);
		boolean stolen = d==null||!d.isUnstealable();
		if(d!=null) {
			d.onClick(player, g, mouse);
		}
		return !stolen;
	}

	public static ClickType buildClick(ItemStack stack, InventoryClickType type, int button, int mouse) {
		String action = stack.getType()==Material.AIR && (type==InventoryClickType.PICKUP||type==InventoryClickType.QUICK_CRAFT)?"DROP":"PICKUP";
		action=(type==InventoryClickType.CLONE?"MIDDLE_":(mouse==0?"LEFT_":mouse==1?"RIGHT_":"MIDDLE_"))+action;
		if(type==InventoryClickType.QUICK_MOVE)
			action="SHIFT_"+action;
		return ClickType.valueOf(action);
	}
	
	public enum InventoryClickType {
		PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL;
	}
	
	private static class SimpleClassLoader extends URLClassLoader {
	
		public SimpleClassLoader(URL[] urls) {
			super(urls);
		}
		
		public void addURL(URL url) {
			super.addURL(url);
		}
	}
	
	private static class ImplementableJar extends JarFile {
		public List<JarFile> file = new ArrayList<>();
		
		public ImplementableJar(File file) throws IOException {
			super(file);
		}
		
		public JarEntry getJarEntry(String name) {
			JarEntry find = super.getJarEntry(name);
			if(find==null) {
				for(JarFile search : file) {
					find=search.getJarEntry(name);
					if(find!=null)return find;
				}
			}
			return null;
		}
		
		public InputStream getInputStream(ZipEntry name) throws IOException {
			InputStream find = super.getInputStream(name);
			if(find==null) {
				for(JarFile search : file) {
					find=search.getInputStream(name);
					if(find!=null)return find;
				}
			}
			return null;
		}
		
		public void close() throws IOException {
			super.close();
			for(JarFile f : file)f.close();
			file.clear();
		}
		
	}
	
	private static int getJavaVersion() {
	    String version = System.getProperty("java.version");
	    if(version.startsWith("1.")) {
	        version = version.substring(2, 3);
	    } else {
	        int dot = version.indexOf(".");
	        if(dot != -1)version = version.substring(0, dot);
	    } return StringUtils.getInt(version);
	}
	
	private static void initTheAPI(JavaPlugin plugin) {
		Ref.init(Ref.getClass("net.md_5.bungee.api.ChatColor")!=null?
				(Ref.getClass("net.kyori.adventure.Adventure")!=null?ServerType.PAPER:ServerType.SPIGOT)
				:ServerType.BUKKIT, Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]); //Server version
		if(Ref.serverType()!=ServerType.BUKKIT)
			ComponentAPI.init((Bungee<?>)Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.components.BungeeComponentAPI")), 
					Ref.serverType()==ServerType.PAPER?(Adventure<?>)Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.components.AdventureComponentAPI")):null);
		if(Ref.isNewerThan(7))
			Json.init(new ModernJsonReader(), new ModernJsonWriter()); //Modern version of Guava
		else
			Json.init((JReader)Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.json.legacy.LegacyJsonReader")), (JWriter)Ref.newInstanceByClass(Ref.getClass("me.devtec.shared.json.legacy.LegacyJsonWriter"))); //Old version of Guava
		API.library = new LibraryLoader() {
			List<File> loaded = new ArrayList<>();
			ImplementableJar jar;
			SimpleClassLoader lloader;
			
			@Override
			public void load(File file) {
				if(isLoaded(file) || !file.exists())return;
				loaded.add(file);
				ClassLoader loader = plugin.getClass().getClassLoader();
				if(getJavaVersion() <= 15) {
					if(addUrl==null)
						addUrl=Ref.method(URLClassLoader.class, "addURL", URL.class);
					try {
						Ref.invoke(loader, addUrl, file.toURI().toURL()); //Simple!
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}else {
					if(Ref.isNewerThan(16) || Ref.isNewerThan(15) && Ref.serverType()==ServerType.PAPER) { //1.16.X with Paper or 1.17 and newer
						if(lloader==null) {
							try {
								lloader=new SimpleClassLoader(new URL[] {file.toURI().toURL()});
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
					}
					try { //Just small hack for modern Java.. - Does not working for files inside jar
						if(jar==null) {
							jar = new ImplementableJar((File)Ref.get(loader, "file"));
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
		StringUtils.rainbowSplit = Pattern.compile("(#[A-Fa-f0-9]{6}([&§][K-Ok-oRr])*|[&§][Xx]([&§][A-Fa-f0-9]){6}([&§][K-Ok-oRr])*|[&§][A-Fa-f0-9K-ORrk-oUuXx]([&§][K-Ok-oRr])*)");
		StringUtils.color = new ColormaticFactory() {
			char[] characters = "abcdef0123456789".toCharArray();
			Random random = new Random();
			Pattern getLast = Pattern.compile("(#[A-Fa-f0-9k-oK-ORrXxUu]{6}|§[Xx](§[A-Fa-f0-9k-oK-ORrXxUu]){6}|§[A-Fa-f0-9k-oK-ORrXxUu]|&[Uu])"), hex = Pattern.compile("(#[a-fA-F0-9]{6})");
			
			@Override
			public String gradient(String msg, String fromHex, String toHex) {
				return API.basics().gradient(msg, fromHex, toHex);
			}
	
			@Override
			public String generateColor() {
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
			public String replaceHex(String msg) {
				Matcher match = hex.matcher(msg);
				while (match.find()) {
					String color = match.group();
					String hex = "§x";
					for(char c : color.substring(1).toCharArray())
						hex+="§"+c;
					msg = msg.replace(color, hex);
				}
				return msg;
			}
		};
	}
}
