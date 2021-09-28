package me.devtec.theapi.utils.theapiutils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.MemoryAPI;
import me.devtec.theapi.apis.PluginManagerAPI;
import me.devtec.theapi.apis.ResourcePackAPI;
import me.devtec.theapi.apis.ResourcePackAPI.ResourcePackResult;
import me.devtec.theapi.bossbar.BossBar;
import me.devtec.theapi.configapi.Config;
import me.devtec.theapi.configapi.Config.Node;
import me.devtec.theapi.economyapi.EconomyAPI;
import me.devtec.theapi.guiapi.GUI.ClickType;
import me.devtec.theapi.guiapi.HolderGUI;
import me.devtec.theapi.guiapi.ItemGUI;
import me.devtec.theapi.placeholderapi.PlaceholderAPI;
import me.devtec.theapi.placeholderapi.ThePlaceholder;
import me.devtec.theapi.placeholderapi.ThePlaceholderAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.scoreboardapi.ScoreboardAPI;
import me.devtec.theapi.scoreboardapi.SimpleScore;
import me.devtec.theapi.sockets.Client;
import me.devtec.theapi.sockets.Server;
import me.devtec.theapi.sockets.ServerClient;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.SpigotUpdateChecker;
import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.listener.events.ClientReceiveMessageEvent;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.packetlistenerapi.PacketHandler;
import me.devtec.theapi.utils.packetlistenerapi.PacketHandler_New;
import me.devtec.theapi.utils.packetlistenerapi.PacketHandler_Old;
import me.devtec.theapi.utils.packetlistenerapi.PacketListener;
import me.devtec.theapi.utils.packetlistenerapi.PacketManager;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.Cache.Query;
import me.devtec.theapi.utils.theapiutils.LoggerManager.BukkitLogger;
import me.devtec.theapi.utils.theapiutils.LoggerManager.ConsoleLogger;
import me.devtec.theapi.utils.theapiutils.command.TheAPICommand;
import me.devtec.theapi.utils.theapiutils.metrics.Metrics;
import me.devtec.theapi.worldsapi.WorldsAPI;
import me.devtec.theapi.worldsapi.voidGenerator;
import me.devtec.theapi.worldsapi.voidGenerator_1_8;
import net.milkbowl.vault.economy.Economy;

public class LoaderClass extends JavaPlugin {
	public final static Map<String, String> colorMap = new HashMap<>();
	// GUIs
	public final Map<String, HolderGUI> gui = new HashMap<>();
	// BossBars
	public final Set<BossBar> bars = new HashSet<>();
	// TheAPI
	public static LoaderClass plugin;
	public static final Config config = new Config("TheAPI/Config.yml");
	public static final Config sockets = new Config("TheAPI/Sockets.yml");
	public static Config tags;
	public static Config data;
	public static Cache cache;
	
	public String motd;
	public static String ss;
	public static String gradientTag, tagG;
	public int max;
	// EconomyAPI
	public boolean e, tve, tbank;
	public Economy economy;
	public static Object air = Ref.invoke(Ref.getNulled(Ref.field(Ref.nmsOrOld("world.level.block.Block","Block"), "AIR")), "getBlockData");
	static {
		if(air==null)
			air=Ref.getNulled(Ref.field(Ref.nmsOrOld("world.level.block.Blocks","Blocks"), "AIR"));
		if(air==null && TheAPI.isNewVersion())
			air=Ref.invoke(Ref.get(Ref.cast(Ref.craft("block.data.CraftBlockData"), Bukkit.createBlockData(Material.AIR)), "state"),"getBlock");
	}
	public Map<String, Client> servers;
	public Server server;
	
	static int airR = 0;
	static Field shift,item,slotR,button,a,quickMove;
	static final Method getSlot = Ref.method(Ref.nmsOrOld("world.inventory.Container","Container"), "getSlot", int.class);
	static final Method getItem= Ref.method(Ref.nmsOrOld("world.inventory.Slot","Slot"), "getItem");
	static Constructor<?> setSlotR = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutSetSlot","PacketPlayOutSetSlot"), int.class, int.class, Ref.nmsOrOld("world.item.ItemStack","ItemStack"));
	static final Class<?> resource = Ref.nmsOrOld("network.protocol.game.PacketPlayInResourcePackStatus","PacketPlayInResourcePackStatus");
	static final Class<?> close = Ref.nmsOrOld("network.protocol.game.PacketPlayInCloseWindow","PacketPlayInCloseWindow");
	static final Class<?> click = Ref.nmsOrOld("network.protocol.game.PacketPlayInWindowClick","PacketPlayInWindowClick");
	static {
		if(setSlotR==null) {
			++airR;
			setSlotR=Ref.findConstructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutSetSlot","PacketPlayOutSetSlot"), int.class, int.class, Ref.nmsOrOld("world.item.ItemStack","ItemStack"));
		}
		if(airR==1) {
			a=Ref.field(click, "b");
			shift=Ref.field(click, TheAPI.isNewerThan(16)?"f":"shift");
			item=Ref.field(click, TheAPI.isNewerThan(16)?"g":"item");
			slotR=Ref.field(click, TheAPI.isNewerThan(16)?"d":"slot");
			button=Ref.field(click, TheAPI.isNewerThan(16)?"e":"button");
			quickMove=Ref.field(click, "h");
		}else {
			a=Ref.field(click, "a");
			shift=Ref.field(click, TheAPI.isNewerThan(16)?"d":"shift");
			item=Ref.field(click, TheAPI.isNewerThan(16)?"e":"item");
			slotR=Ref.field(click, TheAPI.isNewerThan(16)?"b":"slot");
			button=Ref.field(click, TheAPI.isNewerThan(16)?"c":"button");
			if(TheAPI.isNewerThan(16))quickMove=Ref.field(click, "f");
		}
	}
	static final Constructor<?> setSlot = setSlotR;
	static final int airplane = airR;
	
	  static boolean useItem(Player player, ItemStack stack, HolderGUI g, int slot, ClickType mouse) {
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

	private String generate() {
		String d = "abcdefghijklmnopqrstuvwxyz0123456789";
		int len = d.length();
		char[] a = d.toCharArray();
		Random r = new Random();
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < 16; ++i)
			b.append(r.nextBoolean() ? a[r.nextInt(len)] : Character.toUpperCase(a[r.nextInt(len)]));
		return b.toString();
	}

	public enum InventoryClickType {
		PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL
	}
	
	@Override
	public void onLoad() {
		plugin = this;
		data = new Config("TheAPI/Data.dat", DataType.YAML);
		//CONFIG
		createConfig();
		
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eLoading plugin..", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		//SOCKETS
		boolean ops = sockets.exists("Options");
		sockets.addDefault("Options.Enabled", false);
		sockets.addDefault("Options.Name", "serverName");
		sockets.addDefault("Options.Password", generate());
		sockets.addDefault("Options.Port", 25569);
		if(!sockets.exists("Server") && !ops) {
			sockets.set("Server.Bungee.IP", "localhost");
			sockets.set("Server.Bungee.Password", "INSERT PASSWORD HERE");
			sockets.set("Server.Bungee.Port", 25567);
			sockets.set("Server.AnotherSpigotServer.IP", "localhost");
			sockets.set("Server.AnotherSpigotServer.Password", 25568);
			sockets.set("Server.AnotherSpigotServer.Password", "INSERT PASSWORD HERE");
			sockets.set("Server.AnotherSpigotServer.Port", 25568);
		}
		sockets.save();
		if(sockets.getBoolean("Options.Enabled")) {
			servers = new HashMap<>();
			server=new Server(sockets.getString("Options.Password"), sockets.getInt("Options.Port"));
			server.register(new me.devtec.theapi.sockets.Reader() {
				public void read(ServerClient client, Data data) {
					TheAPI.callEvent(new ClientReceiveMessageEvent(client, data));
				}
			});
			for(String s : sockets.getKeys("Server")) {
				servers.put(s, new Client(sockets.getString("Options.Name"), sockets.getString("Server."+s+".Password"), sockets.getString("Server."+s+".IP"), sockets.getInt("Server."+s+".Port")) {
					public void read(Data data) {
						TheAPI.callEvent(new ClientReceiveMessageEvent(this, data));
					}
				});
			}
		}
		
		//CONSOLE LOG EVENT
		if(config.getBoolean("Options.ConsoleLogEvent")) {
			try {
				Class.forName("org.apache.logging.log4j.core.filter.AbstractFilter");
				org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger)org.apache.logging.log4j.LogManager.getRootLogger();
				logger.addFilter(new ConsoleLogger());
			} catch (ClassNotFoundException e) {
			}
			BukkitLogger filter = new BukkitLogger();
			getLogger().setFilter(filter);
			Bukkit.getLogger().setFilter(filter);
			java.util.logging.Logger.getLogger("Minecraft").setFilter(filter);
		}
		//TAGS - 1.16+
		if (TheAPI.isNewerThan(15)) {
			tags = new Config("TheAPI/Tags.yml");
			tags.addDefault("TagPrefix", "!");
			tags.addDefault("GradientPrefix", "!");
			if (!tags.exists("Tags")) {
				tags.addDefault("Tags.baby_blue", "0fd2f6");
				tags.addDefault("Tags.beige", "ffc8a9");
				tags.addDefault("Tags.blush", "e69296");
				tags.addDefault("Tags.amaranth", "e52b50");
				tags.addDefault("Tags.brown", "964b00");
				tags.addDefault("Tags.crimson", "dc143c");
				tags.addDefault("Tags.dandelion", "ffc31c");
				tags.addDefault("Tags.eggshell", "f0ecc7");
				tags.addDefault("Tags.fire", "ff0000");
				tags.addDefault("Tags.ice", "bddeec");
				tags.addDefault("Tags.indigo", "726eff");
				tags.addDefault("Tags.lavender", "4b0082");
				tags.addDefault("Tags.leaf", "618a3d");
				tags.addDefault("Tags.lilac", "c8a2c8");
				tags.addDefault("Tags.lime", "b7ff00");
				tags.addDefault("Tags.midnight", "007bff");
				tags.addDefault("Tags.mint", "50c878");
				tags.addDefault("Tags.olive", "929d40");
				tags.addDefault("Tags.royal_purple", "7851a9");
				tags.addDefault("Tags.rust", "b45019");
				tags.addDefault("Tags.sky", "00c8ff");
				tags.addDefault("Tags.smoke", "708c98");
				tags.addDefault("Tags.tangerine", "ef8e38");
				tags.addDefault("Tags.violet", "9c6eff");
			}
			tags.save();
			tagG = tags.getString("TagPrefix");
			gradientTag = tags.getString("GradientPrefix");
			for (String tag : tags.getKeys("Tags"))
				colorMap.put(tag.toLowerCase(), "#"+tags.getString("Tags." + tag));
			StringUtils.gradientFinder=Pattern.compile(LoaderClass.gradientTag+"(#[A-Fa-f0-9]{6})(.*?)"+LoaderClass.gradientTag+"(#[A-Fa-f0-9]{6})");
		}
		PluginCommand ca = TheAPI.createCommand("theapi", this);
		if(Ref.field(Command.class, "timings")!=null && TheAPI.isOlderThan(9)) {
			Ref.set(Bukkit.getServer(), "commandMap", new Old1_8SimpleCommandMap(Bukkit.getServer(), TheAPI.knownCommands));
			ca = TheAPI.createCommand("theapi", this);
		}
		ca.setExecutor(new TheAPICommand());
		TheAPI.registerCommand(ca);
		StringUtils.sec=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Seconds.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.min=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Minutes.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.hour=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Hours.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.day=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Days.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.week=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Weeks.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.mon=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Months.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.year=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Years.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		
		//METRICS
		new Metrics(this, 10581);
		
		//BOSSBAR - 1.7.10 - 1.8.8
		if (TheAPI.isOlderThan(9))
			new Tasker() {
				public void run() {
					for (BossBar s : bars)
						s.move();
				}
			}.runRepeating(0, 20);
	}

	public void onEnable() {
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eEnabling plugin, creating config and registering economy..",
				TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		if(new Data("spigot.yml").getBoolean("settings.late-bind")) {
			new Tasker() {
				public void run() {
					if (TheAPI.isNewerThan(7))
						handler = new PacketHandler_New(true);
					else
						handler = new PacketHandler_Old(true);
				}
			}.runTask();
		}else {
			if (TheAPI.isNewerThan(7))
				handler = new PacketHandler_New(false);
			else
				handler = new PacketHandler_Old(false);
		}

		loadPlaceholders();
		
		new Tasker() {
			public void run() {
				Tasks.load();
				if (PluginManagerAPI.getPlugin("Vault") == null) {
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &ePlugin not found Vault, EconomyAPI is disabled.", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &eYou can enabled EconomyAPI by set custom Economy in EconomyAPI.",
							TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &e *TheAPI will still normally work without problems*",
							TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
				} else
					vaultHooking();
				new Tasker() {
					@Override
					public void run() {
						if (getTheAPIsPlugins().size() == 0)
							return;
						String end = getTheAPIsPlugins().size() != 1 ? "s" : "";
						TheAPI.msg("&cTheAPI&7: &eTheAPI using &6" + getTheAPIsPlugins().size() + " &eplugin" + end,
								TheAPI.getConsole());
					}
				}.runLater(200);
				new Tasker() {
					public void run() {
						for(User u : TheAPI.getCachedUsers()) {
							if(u.getAutoUnload() && TheAPI.getPlayerOrNull(u.getName())==null) {
								TheAPI.removeCachedUser(u.getUUID());
								u.clearCache();
							}
						}
					}
				}.runRepeating(40, 20*300);
				checker = new SpigotUpdateChecker(getDescription().getVersion(), 72679);
				switch (checker.checkForUpdates()) {
				case UKNOWN:
					TheAPI.msg("&cTheAPI&7: &8*********************************************", TheAPI.getConsole());
					TheAPI.msg(
							"&cTheAPI&7: &eUpdate checker: &7Unable to connect to spigot, check internet connection.",
							TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &8*********************************************", TheAPI.getConsole());
					checker = null; // close updater
					break;
				case NEW:
					TheAPI.msg("&cTheAPI&7: &8*********************************************", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &eUpdate checker: &7Found new version of TheAPI.", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7:        https://www.spigotmc.org/resources/72679/", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &8*********************************************", TheAPI.getConsole());
					break;
				case OLD:
					TheAPI.msg("&cTheAPI&7: &8*********************************************", TheAPI.getConsole());
					TheAPI.msg(
							"&cTheAPI&7: &eUpdate checker: &7You are using the BETA version of TheAPI, report bugs to our Discord.",
							TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7:        https://discord.io/spigotdevtec", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &8*********************************************", TheAPI.getConsole());
					break;
				default:
					break;
				}
				if (checker != null)
					new Tasker() {
						public void run() {
							switch (checker.checkForUpdates()) {
							case UKNOWN:
								TheAPI.msg("&cTheAPI&7: &8*********************************************",
										TheAPI.getConsole());
								TheAPI.msg(
										"&cTheAPI&7: &eUpdate checker: &7Unable to connect to spigot, check internet connection.",
										TheAPI.getConsole());
								TheAPI.msg("&cTheAPI&7: &8*********************************************",
										TheAPI.getConsole());
								checker = null; // close updater
								cancel(); // destroy task
								break;
							case NEW:
								TheAPI.msg("&cTheAPI&7: &8*********************************************",
										TheAPI.getConsole());
								TheAPI.msg("&cTheAPI&7: &eUpdate checker: &7Found new version of TheAPI.",
										TheAPI.getConsole());
								TheAPI.msg("&cTheAPI&7:        https://www.spigotmc.org/resources/72679/",
										TheAPI.getConsole());
								TheAPI.msg("&cTheAPI&7: &8*********************************************",
										TheAPI.getConsole());
								break;
							case OLD:
								TheAPI.msg("&cTheAPI&7: &8*********************************************",
										TheAPI.getConsole());
								TheAPI.msg(
										"&cTheAPI&7: &eUpdate checker: &7You are using the BETA version of TheAPI, report bugs to our Discord.",
										TheAPI.getConsole());
								TheAPI.msg("&cTheAPI&7:        https://discord.io/spigotdevtec", TheAPI.getConsole());
								TheAPI.msg("&cTheAPI&7: &8*********************************************",
										TheAPI.getConsole());
								break;
							default:
								break;
							}
						}
					}.runRepeating(144000, 144000);
			}
		}.runTask();
		if(config.getBoolean("Options.AntiFakeBlocks"))
		new PacketListener() {
			@Override
			public boolean PacketPlayIn(String player, Object packet, Object channel) {
				return false;
			}
			
			final Class<?> c = Ref.nmsOrOld("network.protocol.game.PacketPlayOutBlockChange", "PacketPlayOutBlockChange");
			
			@Override
			public boolean PacketPlayOut(String player, Object packet, Object channel) {
				if(packet.getClass()==c) {
					if(TheAPI.isNewerThan(7)) {
						Player a = TheAPI.getPlayer(player);
						Position c = new Position(a.getWorld(),(int)Ref.invoke(Ref.get(packet,"a"), "getX"),(int)Ref.invoke(Ref.get(packet,"a"), "getY"),(int)Ref.invoke(Ref.get(packet,"a"), "getZ"));
						if(!c.getIBlockData().equals(Ref.get(packet, TheAPI.isNewerThan(16)?"b":"block")))
							Ref.set(packet, TheAPI.isNewerThan(16)?"b":"block", LoaderClass.air);
					}else {
						Player a = TheAPI.getPlayer(player);
						Position c = new Position(a.getWorld(),(int)Ref.get(packet,"a"),(int)Ref.get(packet,"b"),(int)Ref.get(packet,"c"));
						if(!c.getIBlockData().equals(Ref.get(packet, "block")) || !Ref.get(packet, "data").equals(c.getData())) {
							Ref.set(packet, "block", LoaderClass.air);
							Ref.set(packet, "data", 0);
						}
					}
				}
				return false;
			}
		}.register();
		
		new PacketListener() {
			
			@Override
			public boolean PacketPlayOut(String player, Object packet, Object channel) {
				return false;
			}
			final boolean installedModificationPlugin = PluginManagerAPI.getPlugin("ViaVersion")!=null||PluginManagerAPI.getPlugin("ProtocolSupport")!=null;
			final ItemStack empty = new ItemStack(Material.AIR);
			public boolean PacketPlayIn(String player, Object packet, Object channel) {
				if(player==null)return false; //NPC
				//ResourcePackAPI
				if(resource!=null && packet.getClass()==resource) {
					Player s = TheAPI.getPlayer(player);
					if(s==null)return false;
					if(ResourcePackAPI.getResourcePack(s)==null||ResourcePackAPI.getHandlingPlayer(s)==null)return false;
					ResourcePackAPI.getHandlingPlayer(s).onHandle(s, ResourcePackAPI.getResourcePack(s), ResourcePackResult.valueOf(Ref.get(packet, TheAPI.isNewerThan(16)?"a":"status").toString()));
					return false;
				}
				//GUIS
				if(packet.getClass()==close) {
					Player p = TheAPI.getPlayer(player);
					if(p==null)return false;
					HolderGUI d = LoaderClass.plugin.gui.remove(p.getName());
					if (d == null)
						return false;
					d.closeWithoutPacket(p);
					return true;
				}
				if(packet.getClass()==click) {
					Player p = TheAPI.getPlayer(player);
					if(p==null)return false;
					HolderGUI d = LoaderClass.plugin.gui.get(p.getName());
					if (d == null)return false;
					int id = (int) Ref.get(packet, a);
					int slot = (int) Ref.get(packet, slotR);
					int mouseClick = (int) Ref.get(packet, button);
					Object aw = Ref.get(packet, shift);
					InventoryClickType type;
					if(aw instanceof Integer) {
						type=InventoryClickType.values()[(int)aw];
					}else {
						type=InventoryClickType.valueOf(aw.toString());
					}
					if(slot==-999)return false;
					Object g = d.getContainer(p);
					ItemStack i = NMSAPI.asBukkitItem(Ref.get(packet, item));
					if((type==InventoryClickType.QUICK_MOVE||type==InventoryClickType.CLONE||type==InventoryClickType.THROW||i.getType()==Material.AIR) && i.getType()==Material.AIR)
						i=NMSAPI.asBukkitItem(Ref.invoke(Ref.invoke(g, getSlot, slot),getItem));
					if(InventoryClickType.SWAP==type) {
						i=p.getInventory().getItem(mouseClick);
						mouseClick=0;
					}
					ItemStack before = p.getItemOnCursor();
					if(i==null)i=new ItemStack(Material.AIR);
					ClickType w = buildClick(i, type, slot, mouseClick);
					boolean cancel = useItem(p, i, d, slot, w);
					if(!d.isInsertable())cancel=true;
					if(!cancel) {
						cancel=d.onIteractItem(p, i, w, slot>d.size()?slot-d.size()+27:slot, slot<d.size());
					}
					if(type==InventoryClickType.QUICK_MOVE && TheAPI.isOlderThan(9))
						cancel=true;
					if(cancel) {
						//MOUSE
						Ref.sendPacket(p,airplane==0?Ref.newInstance(setSlot,-1, -1, NMSAPI.asNMSItem(before)):
							Ref.newInstance(setSlot,-1,-1, -1, NMSAPI.asNMSItem(before)));
						switch(type) {
						case CLONE:
							return true;
						case SWAP:
						case QUICK_MOVE:
							//1.17+ = simplier packet & need viaversion & protocolsupport installed check
							if(TheAPI.isNewerThan(16)) {
								if(installedModificationPlugin) {
									Ref.sendPacket(p,airplane==0?Ref.newInstance(setSlot,id, slot, Ref.invoke(Ref.invoke(g, getSlot, slot),getItem)):
										Ref.newInstance(setSlot,id,(int)Ref.invoke(Ref.get(Ref.player(p), "bU"),"incrementStateId"), slot, Ref.invoke(Ref.invoke(g, getSlot, slot),getItem)));
									if(airplane == 0) {
										//TOP
										int ic = 0;
										for(ItemStack o : d.getInventory().getContents()) {
											if(o==null)o=empty;
											Ref.sendPacket(p,Ref.newInstance(setSlot,id, ic++, NMSAPI.asNMSItem(o)));
										}
										//BUTTON
										for(ItemStack o : p.getInventory().getContents()) {
											if(o==null)o=empty;
											Ref.sendPacket(p,Ref.newInstance(setSlot,id, p.getInventory().getSize()-ic++, NMSAPI.asNMSItem(o)));
										}
									}else {
										int slotId = (int)Ref.invoke(Ref.get(Ref.player(p), "bU"),"incrementStateId");
										//TOP
										int ic = 0;
										for(ItemStack o : d.getInventory().getContents()) {
											if(o==null)o=empty;
											Ref.sendPacket(p,Ref.newInstance(setSlot,id,slotId, ic++, NMSAPI.asNMSItem(o)));
										}
										//BUTTON
										for(ItemStack o : p.getInventory().getContents()) {
											if(o==null)o=empty;
											Ref.sendPacket(p,Ref.newInstance(setSlot,id,slotId, p.getInventory().getSize()-ic++, NMSAPI.asNMSItem(o)));
										}
									}
								}else { //without check
									org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap<?> f = (org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap<?>) Ref.get(packet, quickMove);
									if(airplane == 0) {
										//TOP
										for(int o : f.keySet()) {
											Ref.sendPacket(p,Ref.newInstance(setSlot,id, o, Ref.invoke(Ref.invoke(g, getSlot, o),getItem)));
										}
									}else {
										int slotId = (int)Ref.invoke(Ref.get(Ref.player(p), "bU"),"incrementStateId");
										//TOP
										for(int o : f.keySet()) {
											Ref.sendPacket(p,Ref.newInstance(setSlot,id,slotId,o, Ref.invoke(Ref.invoke(g, getSlot, o),getItem)));
										}
									}
								}
								Ref.invoke(Ref.get(Ref.player(p), TheAPI.isNewerThan(16)?"bU":"inventory"),TheAPI.isNewerThan(16)?"updateInventory":"update");
								return true;
							}else {
								//TOP
								int ic = 0;
								for(ItemStack o : d.getInventory().getContents()) {
									if(o==null)o=empty;
									Ref.sendPacket(p,Ref.newInstance(setSlot,id, ic++, NMSAPI.asNMSItem(o)));
								}
								//BUTTON
								for(ItemStack o : p.getInventory().getContents()) {
									if(o==null)o=empty;
									Ref.sendPacket(p,Ref.newInstance(setSlot,id, p.getInventory().getSize()-ic++, NMSAPI.asNMSItem(o)));
								}
							}
							return true;
						case PICKUP_ALL:
							//IF PICKUP IN TOP
							if(TheAPI.isNewerThan(16) && !installedModificationPlugin) {
								org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap<?> f = (org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap<?>) Ref.get(packet, quickMove);
								if(airplane == 0) {
									//TOP
									for(int o : f.keySet()) {
										Ref.sendPacket(p,Ref.newInstance(setSlot,id, o, Ref.invoke(Ref.invoke(g, getSlot, o),getItem)));
									}
								}else {
									int slotId = (int)Ref.invoke(Ref.get(Ref.player(p), "bU"),"incrementStateId");
									//TOP
									for(int o : f.keySet()) {
										Ref.sendPacket(p,Ref.newInstance(setSlot,id,slotId,o, Ref.invoke(Ref.invoke(g, getSlot, o),getItem)));
									}
								}
								return true;
							}
							if(TheAPI.isNewerThan(16)) {
								if(airplane == 0) {
									//TOP
									int ic = 0;
									for(ItemStack o : d.getInventory().getContents()) {
										if(o==null)o=empty;
										Ref.sendPacket(p,Ref.newInstance(setSlot,id, ic++, NMSAPI.asNMSItem(o)));
									}
									//BUTTON
									for(ItemStack o : p.getInventory().getContents()) {
										if(o==null)o=empty;
										Ref.sendPacket(p,Ref.newInstance(setSlot,id, p.getInventory().getSize()-ic++, NMSAPI.asNMSItem(o)));
									}
								}else {
									int slotId = (int)Ref.invoke(Ref.get(Ref.player(p), "bU"),"incrementStateId");
									//TOP
									int ic = 0;
									for(ItemStack o : d.getInventory().getContents()) {
										if(o==null)o=empty;
										Ref.sendPacket(p,Ref.newInstance(setSlot,id,slotId, ic++, NMSAPI.asNMSItem(o)));
									}
									//BUTTON
									for(ItemStack o : p.getInventory().getContents()) {
										if(o==null)o=empty;
										Ref.sendPacket(p,Ref.newInstance(setSlot,id,slotId, p.getInventory().getSize()-ic++, NMSAPI.asNMSItem(o)));
									}
								}
							}else {
								//TOP
								int ic = 0;
								for(ItemStack o : d.getInventory().getContents()) {
									if(o==null)o=empty;
									Ref.sendPacket(p,Ref.newInstance(setSlot,id, ic++, NMSAPI.asNMSItem(o)));
								}
								//BUTTON
								for(ItemStack o : p.getInventory().getContents()) {
									if(o==null)o=empty;
									Ref.sendPacket(p,Ref.newInstance(setSlot,id, p.getInventory().getSize()-ic++, NMSAPI.asNMSItem(o)));
								}
							}
							return true;
						default:
							Ref.sendPacket(p,airplane==0?Ref.newInstance(setSlot,id, slot, Ref.invoke(Ref.invoke(g, getSlot, slot),getItem)):
								Ref.newInstance(setSlot,id,(int)Ref.invoke(Ref.get(Ref.player(p), "bU"),"incrementStateId"), slot, Ref.invoke(Ref.invoke(g, getSlot, slot),getItem)));
							return true;
						}
					}
				}
				return false;
			}
		}.register();
		Bukkit.getPluginManager().registerEvents(new Events(), LoaderClass.this);
		if(config.getBoolean("Options.ItemUnbreakable"))
		Bukkit.getPluginManager().registerEvents(new ItemBreakEvent(), LoaderClass.this);

		Data plugin = new Data();
		for(Plugin e : Bukkit.getPluginManager().getPlugins()) {
			plugin.reload(StreamUtils.fromStream(e.getResource("plugin.yml")));
			if(plugin.exists("configs")) {
				String folder = plugin.exists("configsFolder")?(plugin.getString("configsFolder").trim().isEmpty()?e.getName():plugin.getString("configsFolder")):e.getName();
				if(plugin.get("configs") instanceof Collection) {
					for(String config : plugin.getStringList("configs"))
						Config.loadConfig(e, config, folder+"/"+config);
				}else
					Config.loadConfig(e, plugin.getString("configs"), folder+"/"+plugin.getString("configs"));
			}
		}
		new Tasker() {
			public void run() {
				loadWorlds();
			}
		}.runLaterSync(20);
		if (PlaceholderAPI.isEnabledPlaceholderAPI()) {
			/*
			 * TheAPI placeholder extension for PAPI BRIDGE:
			 * 
			 * PAPI -> THEAPI : %papi_placeholder_here% PAPI <- THEAPI :
			 * %theapi_{theapi_placeholder_here}%
			 */
			new PlaceholderExpansion() {
				final Pattern math = Pattern.compile("math\\{((?:\\{??[^A-Za-z\\{][ 0-9+*/^%()~.-]*))\\}");
				final Pattern limiter = Pattern.compile("limiter\\{((?:\\{??[^A-Za-z\\{][ 0-9+*/^%()~.-]*)),[ ]*((?:\\{??[^A-Za-z\\{][ 0-9+*/^%()~.-]*)),[ ]*((?:\\{??[^A-Za-z\\{][ 0-9+*/^%()~.-]*))\\}");
				
				@Override
				public String getVersion() {
					return LoaderClass.this.getDescription().getVersion();
				}
				
				@Override
				public String getIdentifier() {
					return "theapi";
				}
				
				@Override
				public String getAuthor() {
					return "DevTec";
				}
				
				public String onRequest(OfflinePlayer player, String params) {
					String text = params;
					Matcher m = math.matcher(text);
					while (m.find()) {
						text = text.replace(m.group(), StringUtils.calculate(m.group(1))+"");
						m = math.matcher(text);
					}
					m = limiter.matcher(text);
					while (m.find()) {
						text = text.replace(m.group(), limit(StringUtils.getDouble(m.group(1)),StringUtils.getDouble(m.group(2)),StringUtils.getDouble(m.group(3)))+"");
						m = limiter.matcher(text);
					}
					for (ThePlaceholder get : ThePlaceholderAPI.getPlaceholders()) {
						String toReplace = get.onPlaceholderRequest(player == null ? null : player.isOnline() ? player.getPlayer() : null, params);
						if (toReplace != null)
							text = text.replace(params, toReplace);
					}
					m = math.matcher(text);
					while (m.find()) {
						text = text.replace(m.group(), StringUtils.calculate(m.group(1))+"");
						m = math.matcher(text);
					}
					m = limiter.matcher(text);
					while (m.find()) {
						text = text.replace(m.group(), limit(StringUtils.getDouble(m.group(1)),StringUtils.getDouble(m.group(2)),StringUtils.getDouble(m.group(3)))+"");
						m = limiter.matcher(text);
					}
					return text.equals(params) ? null : text;
				}
				
				private double limit(double val, double min, double max) {
					if(val<min)val=min;
					if(val>max)val=max;
					return val;
				}
			}.register();
		}
	}

	private SpigotUpdateChecker checker;

	@SuppressWarnings("rawtypes")
	public PacketHandler handler;
	public boolean enabled=true;

	@SuppressWarnings("unchecked")
	@Override
	public void onDisable() {
		enabled=false;
		//GUI
		for (Entry<String, HolderGUI> p : gui.entrySet()) {
			p.getValue().clear();
			p.getValue().close();
		}
		gui.clear();
		
		//Scheduler
		Scheduler.cancelAll();
		
		//Sockets
		if(server!=null)
			server.exit();
		
		//Placeholders
		main.unregister();
		timeUtils.unregister();
		
		//Bans
		data.save();
		
		//SCOREBOARD
		for(ScoreboardAPI sb : ((Map<String,ScoreboardAPI>)Ref.getStatic(SimpleScore.class, "scores")).values())
			sb.destroy();
		
		//BOSSBAR, ACTION BAR & TITLE
		for(Player p : TheAPI.getOnlinePlayers()) {
			TheAPI.removeBossBar(p);
			TheAPI.removeActionBar(p);
			TheAPI.sendTitle(p, "","");
		}
		
		//PacketListener
		PacketManager.unregisterAll();
		handler.close();
		
		//users cache
		if(cache!=null) {
			Data data = cache.saveToData();
			File f = new File("plugins/TheAPI/Cache.dat");
			if (!f.exists()) {
				try {
					if(f.getParentFile()!=null)
						f.getParentFile().mkdirs();
				} catch (Exception e) {
				}
				try {
					f.createNewFile();
				} catch (Exception e) {
				}
			}
			data.setFile(f);
			data.save(DataType.YAML);
		}
		
		//Users
		for(User u : TheAPI.getCachedUsers())
			u.save();
		
		TheAPI.clearCache();
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eDisabling plugin, saving configs and stopping runnables..", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
	}

	public List<Plugin> getTheAPIsPlugins() {
		List<Plugin> a = new ArrayList<>();
		for (Plugin all : PluginManagerAPI.getPlugins())
			if (PluginManagerAPI.getDepend(all.getName()).contains("TheAPI")
					|| PluginManagerAPI.getSoftDepend(all.getName()).contains("TheAPI"))
				a.add(all);
		return a;
	}
	
	public void reload() {
		LoaderClass.data.reload();
		LoaderClass.config.reload();
		boolean wasEnabled = LoaderClass.sockets.getBoolean("Options.Enabled");
		LoaderClass.sockets.reload();
		if(!wasEnabled && LoaderClass.sockets.getBoolean("Options.Enabled")) {
			LoaderClass.plugin.servers = new HashMap<>();
			LoaderClass.plugin.server=new Server(LoaderClass.sockets.getString("Options.Password"), LoaderClass.sockets.getInt("Options.Port"));
			LoaderClass.plugin.server.register(new me.devtec.theapi.sockets.Reader() {
				public void read(ServerClient client, Data data) {
					TheAPI.callEvent(new ClientReceiveMessageEvent(client, data));
				}
			});
			for(String ds : LoaderClass.sockets.getKeys("Server")) {
				LoaderClass.plugin.servers.put(ds, new Client(LoaderClass.sockets.getString("Options.Name"),LoaderClass. sockets.getString("Server."+ds+".Password"), LoaderClass.sockets.getString("Server."+ds+".IP"), LoaderClass.sockets.getInt("Server."+ds+".Port")) {
					public void read(Data data) {
						TheAPI.callEvent(new ClientReceiveMessageEvent(this, data));
					}
				});
			}
		}else {
			if(wasEnabled) {
				LoaderClass.plugin.server.exit();
				for(Client e : LoaderClass.plugin.servers.values())
					e.exit();
				LoaderClass.plugin.servers.clear();
				LoaderClass.plugin.server=null;
			}
		}
		if (TheAPI.isNewerThan(15)) {
			LoaderClass.tags.reload();
			LoaderClass.tagG = LoaderClass.tags.getString("TagPrefix");
			LoaderClass.gradientTag = LoaderClass.tags.getString("GradientPrefix");
			LoaderClass.colorMap.clear();
			for (String tag : LoaderClass.tags.getKeys("Tags"))
				LoaderClass.colorMap.put(tag.toLowerCase(), "#" + LoaderClass.tags.getString("Tags." + tag));
			StringUtils.gradientFinder=Pattern.compile(LoaderClass.gradientTag+"(#[A-Fa-f0-9]{6})(.*?)"+LoaderClass.gradientTag+"(#[A-Fa-f0-9]{6})|.*?(?=(?:"+LoaderClass.gradientTag+"#[A-Fa-f0-9]{6}.*?"+LoaderClass.gradientTag+"#[A-Fa-f0-9]{6}))");
		}
		for (User u : TheAPI.getCachedUsers())
			u.getData().reload(u.getData().getFile());
		Tasks.unload();
		Tasks.load();

		StringUtils.timeFormat=config.getString("Options.TimeConvertor.Format");
		
		List<String> sec = new ArrayList<>();
		StringUtils.actions.put("Seconds",sec);
		sec.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Seconds.Convertor"));
		if(sec.isEmpty())
			sec.addAll(Arrays.asList("=,1,sec",">,1,secs"));
		
		List<String> min = new ArrayList<>();
		StringUtils.actions.put("Minutes",min);
		min.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Minutes.Convertor"));
		if(min.isEmpty())
			min.addAll(Arrays.asList("=,1,min",">,1,s"));
		
		List<String> hours = new ArrayList<>();
		StringUtils.actions.put("Hours",hours);
		hours.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Hours.Convertor"));
		if(hours.isEmpty())
			hours.addAll(Arrays.asList("=,1,hour",">,1,hours"));
		
		List<String> days = new ArrayList<>();
		StringUtils.actions.put("Days",days);
		days.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Days.Convertor"));
		if(days.isEmpty())
			days.addAll(Arrays.asList("=,1,day",">,1,days"));
		
		List<String> weeks = new ArrayList<>();
		StringUtils.actions.put("Weeks",weeks);
		weeks.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Weeks.Convertor"));
		if(weeks.isEmpty())
			weeks.addAll(Arrays.asList("=,1,week",">,1,weeks"));
		
		List<String> years = new ArrayList<>();
		StringUtils.actions.put("Years",years);
		years.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Years.Convertor"));
		if(years.isEmpty())
			years.addAll(Arrays.asList("=,1,year",">,1,years"));

		StringUtils.sec=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Seconds.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.min=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Minutes.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.hour=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Hours.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.day=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Days.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.week=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Weeks.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.mon=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Months.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.year=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(LoaderClass.config.getStringList("Options.TimeConvertor.Years.Lookup"), "|")+")",Pattern.CASE_INSENSITIVE);
		
	}
	
	public void createConfig() {
		config.addDefault("Options.HideErrors", new Node(false, "",
				"# If you enable this option, errors from TheAPI will disappear", "# default: false")); // hide only TheAPI errors
		config.addDefault("Options.ConsoleLogEvent", false);
		config.addDefault("Options.ItemUnbreakable", true);
		config.addDefault("Options.ServerListPingEvent", true);
		config.addDefault("Options.AntiFakeBlocks", new Node(false, "# This function can solve problems with \"ghost blocks\", but make anti-xray no longer working", "# default: false"));
		config.addDefault("Options.Cache.User.Use", new Node(true, "# Cache Users to memory for faster loading", "# default: true")); // Require memory, but loading of User.class is faster (only
		config.setComments("Options.Cache", Collections.singletonList(""));
		config.addDefault("Options.Cache.User.RemoveOnQuit", new Node(true, "# Remove cache of User from memory", "# default: true")); // Remove cached player from cache on
		config.addDefault("Options.Cache.User.DisableSaving.IP", false);
		config.addDefault("Options.Cache.User.OfflineNames.Use", true); // Cache offline-names of players
		config.addDefault("Options.Cache.User.OfflineNames.AutoSave", "15min"); // Period in which offline-names saves
		config.addDefault("Options.Cache.User.OfflineNames.AutoClear.Use", false); // Enable automatic clearing of cache
		config.addDefault("Options.Cache.User.OfflineNames.AutoClear.OfflineTime", "1mon"); // Automatic clear cache after 1mon
		config.addDefault("Options.Cache.User.OfflineNames.AutoClear.Period", "0"); // 0 means on startup or type time period
		config.addDefault("Options.User-SavingType", new Node("YAML","", "# Saving type of User data", "# Types: YAML, JSON, BYTE", "# default: YAML"));
		config.addDefault("Options.FakeEconomyAPI.Symbol", new Node("$", "# Economy symbol of FakeEconomyAPI", "# default: $"));
		config.setComments("Options.FakeEconomyAPI", Collections.singletonList(""));
		config.addDefault("Options.FakeEconomyAPI.Format", new Node("$%money%", "# Economy format of FakeEconomyAPI", "# default: $%money%"));
		
		//TRANSLATABLE TIME CONVERTOR
		config.setComments("Options.TimeConvertor", Arrays.asList("","# Convertor Actions:","# action, amount, translation","# = (equals)","# < (lower than)","# > (more than)"));
		config.addDefault("Options.TimeConvertor.Split", " ");
		config.addDefault("Options.TimeConvertor.Format", "%time% %format%");
		config.addDefault("Options.TimeConvertor.Seconds.Convertor", Arrays.asList("=,1,sec",">,1,secs"));
		if(!(config.get("Options.TimeConvertor.Seconds.Convertor") instanceof Collection))
			config.set("Options.TimeConvertor.Seconds.Convertor", Arrays.asList("=,1,sec",">,1,secs"));
		config.addDefault("Options.TimeConvertor.Seconds.Lookup", Arrays.asList("s","sec","second","seconds"));
		
		config.addDefault("Options.TimeConvertor.Minutes.Convertor", Arrays.asList("=,1,min",">,1,mins"));
		if(!(config.get("Options.TimeConvertor.Minutes.Convertor") instanceof Collection))
			config.set("Options.TimeConvertor.Minutes.Convertor", Arrays.asList("=,1,min",">,1,mins"));
		config.addDefault("Options.TimeConvertor.Minutes.Lookup", Arrays.asList("m","mi","min","minu","minut","minute","minutes"));
		
		if(!(config.get("Options.TimeConvertor.Hours.Convertor") instanceof Collection))
			config.set("Options.TimeConvertor.Hours.Convertor", Arrays.asList("=,1,hour",">,1,hours"));
		config.addDefault("Options.TimeConvertor.Hours.Convertor", Arrays.asList("=,1,hour",">,1,hours"));
		config.addDefault("Options.TimeConvertor.Hours.Lookup", Arrays.asList("h","ho","hou","hour","hours"));
		
		config.addDefault("Options.TimeConvertor.Days.Convertor", Arrays.asList("=,1,day",">,1,days"));
		if(!(config.get("Options.TimeConvertor.Days.Convertor") instanceof Collection))
			config.set("Options.TimeConvertor.Days.Convertor", Arrays.asList("=,1,day",">,1,days"));
		config.addDefault("Options.TimeConvertor.Days.Lookup", Arrays.asList("d","da","day","days"));
		
		config.addDefault("Options.TimeConvertor.Weeks.Lookup", Arrays.asList("w","we","wee","week","weeks"));
		
		config.addDefault("Options.TimeConvertor.Months.Convertor", Arrays.asList("=,1,month",">,1,months"));
		if(!(config.get("Options.TimeConvertor.Months.Convertor") instanceof Collection))
			config.set("Options.TimeConvertor.Months.Convertor", Arrays.asList("=,1,month",">,1,months"));
		config.addDefault("Options.TimeConvertor.Months.Lookup", Arrays.asList("mo","mon","mont","month","months"));
		
		config.addDefault("Options.TimeConvertor.Years.Convertor", Arrays.asList("=,1,year",">,1,years"));
		if(!(config.get("Options.TimeConvertor.Years.Convertor") instanceof Collection))
			config.set("Options.TimeConvertor.Years.Convertor", Arrays.asList("=,1,year",">,1,years"));
		config.addDefault("Options.TimeConvertor.Years.Lookup", Arrays.asList("y","ye","yea","year","years"));
		config.save();

		StringUtils.timeFormat=config.getString("Options.TimeConvertor.Format");
		
		List<String> sec = new ArrayList<>();
		StringUtils.actions.put("Seconds",sec);
		sec.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Seconds.Convertor"));
		if(sec.isEmpty())
			sec.addAll(Arrays.asList("=,1,sec",">,1,secs"));
		
		List<String> min = new ArrayList<>();
		StringUtils.actions.put("Minutes",min);
		min.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Minutes.Convertor"));
		if(min.isEmpty())
			min.addAll(Arrays.asList("=,1,min",">,1,s"));
		
		List<String> hours = new ArrayList<>();
		StringUtils.actions.put("Hours",hours);
		hours.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Hours.Convertor"));
		if(hours.isEmpty())
			hours.addAll(Arrays.asList("=,1,hour",">,1,hours"));
		
		List<String> days = new ArrayList<>();
		StringUtils.actions.put("Days",days);
		days.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Days.Convertor"));
		if(days.isEmpty())
			days.addAll(Arrays.asList("=,1,day",">,1,days"));
		
		List<String> weeks = new ArrayList<>();
		StringUtils.actions.put("Weeks",weeks);
		weeks.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Weeks.Convertor"));
		if(weeks.isEmpty())
			weeks.addAll(Arrays.asList("=,1,week",">,1,weeks"));
		
		List<String> years = new ArrayList<>();
		StringUtils.actions.put("Years",years);
		years.addAll(LoaderClass.config.getStringList("Options.TimeConvertor.Years.Convertor"));
		if(years.isEmpty())
			years.addAll(Arrays.asList("=,1,year",">,1,years"));
		
		StringUtils.sec=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(LoaderClass.config.getStringList("Options.TimeConvertor.Seconds.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.min=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(LoaderClass.config.getStringList("Options.TimeConvertor.Minutes.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.hour=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(LoaderClass.config.getStringList("Options.TimeConvertor.Hours.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.day=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(LoaderClass.config.getStringList("Options.TimeConvertor.Days.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.week=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(LoaderClass.config.getStringList("Options.TimeConvertor.Weeks.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.mon=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(LoaderClass.config.getStringList("Options.TimeConvertor.Months.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
		StringUtils.year=Pattern.compile("([+-]?[0-9]+)("+StringUtils.join(Lists.reverse(LoaderClass.config.getStringList("Options.TimeConvertor.Years.Lookup")), "|")+")",Pattern.CASE_INSENSITIVE);
		
		if(config.getBoolean("Options.Cache.User.OfflineNames.Use")) {
			if(cache==null) {
				cache=new Cache();
				Data data = new Data("plugins/TheAPI/Cache.dat");
				for(String s : data.getKeys())
					try {
					cache.setLookup(UUID.fromString(s), data.getString(s));
					}catch(Exception err) {}
				new Tasker() {
					public void run() {
						Data data = cache.saveToData();
						File f = new File("plugins/TheAPI/Cache.dat");
						if (!f.exists()) {
							try {
								if(f.getParentFile()!=null)
									f.getParentFile().mkdirs();
							} catch (Exception e) {
							}
							try {
								f.createNewFile();
							} catch (Exception e) {
							}
						}
						data.setFile(f);
						data.save(DataType.YAML);
					}
				}.runRepeating(0, 20*StringUtils.timeFromString(config.getString("Options.Cache.User.OfflineNames.AutoSave")));
				if(config.getBoolean("Options.Cache.User.OfflineNames.AutoClear.Use")) {
					long removeAfter = StringUtils.timeFromString(config.getString("Options.Cache.User.OfflineNames.AutoClear.OfflineTime"));
					if(removeAfter > 0 && StringUtils.timeFromString(config.getString("Options.Cache.User.OfflineNames.AutoClear.Period"))<=0) {
						int removed = 0;
						for(String s : data.getKeys()) {
							try {
							User user = new User(data.getString(s), UUID.fromString(s));
							if(user.getData().getKeys().size()==0) { //empty user
								cache.values.remove(user.getName().toLowerCase());
								TheAPI.removeCachedUser(user.getUUID());
								user.delete(); //delete file
								user.clearCache();
								++removed;
								continue;
							}
							if(user.getLong("quit")==0) {
								TheAPI.removeCachedUser(user.getUUID());
								user.data().clear(); //clear cache from memory
								user.clearCache();
								continue; //fake user?
							}
							if(user.getLong("quit") - System.currentTimeMillis()/1000 + removeAfter <= 0) {
								cache.values.remove(user.getName().toLowerCase());
								TheAPI.removeCachedUser(user.getUUID());
								user.delete(); //delete file
								user.clearCache();
								++removed;
							}
							}catch(Exception err) {}
						}
						data.clear();
						if (removed != 0)
							TheAPI.msg("&cTheAPI&7: &eTheAPI deleted &6" + removed + " &eunused user files",
									TheAPI.getConsole());
					}else {
						new Tasker() {
							public void run() {
								int removed = 0;
								for(Query s : Collections.unmodifiableCollection(cache.values.values())) {
									User user = new User(s);
									if(user.getData().getKeys().size()==0) { //empty user
										cache.values.remove(user.getName().toLowerCase());
										TheAPI.removeCachedUser(user.getUUID());
										user.delete(); //delete file
										user.clearCache();
										++removed;
										continue;
									}
									if(removeAfter==0)continue;
									if(user.getLong("quit")==0) {
										user.data().clear(); //clear cache from memory
										continue; //fake user?
									}
									if(user.getLong("quit") - System.currentTimeMillis()/1000 + removeAfter <= 0) {
										cache.values.remove(user.getName().toLowerCase());
										TheAPI.removeCachedUser(user.getUUID());
										user.delete(); //delete file
										user.clearCache();
										++removed;
									}
								}
								if (removed != 0)
									TheAPI.msg("&cTheAPI&7: &eTheAPI deleted &6" + removed + " &eunused user files",
											TheAPI.getConsole());
							}
						}.runRepeating(0, 20*StringUtils.timeFromString(config.getString("Options.Cache.User.OfflineNames.AutoClear.Period")));
					}
				}
			}
		}else
			cache=new Cache();
		max = Bukkit.getMaxPlayers();
		motd = Bukkit.getMotd();
	}

	private static ThePlaceholder main, timeUtils;

	public void loadPlaceholders() {
		main = new ThePlaceholder("TheAPI") {
			@Override
			public String onRequest(Player player, String placeholder) {
				if (player != null) {
					if (placeholder.equalsIgnoreCase("player_money"))
						return "" + EconomyAPI.getBalance(player);
					if (placeholder.equalsIgnoreCase("player_balance"))
						return "" + EconomyAPI.getBalance(player);
					if (placeholder.equalsIgnoreCase("player_formated_money"))
						return EconomyAPI.format(EconomyAPI.getBalance(player));
					if (placeholder.equalsIgnoreCase("player_formated_balance"))
						return EconomyAPI.format(EconomyAPI.getBalance(player));
					if (placeholder.equalsIgnoreCase("player_displayname"))
						return player.getDisplayName();
					if (placeholder.equalsIgnoreCase("player_customname"))
						return player.getCustomName();
					if (placeholder.equalsIgnoreCase("player_name")||placeholder.equalsIgnoreCase("player"))
						return player.getName();
					if (placeholder.equalsIgnoreCase("player_gamemode"))
						return player.getGameMode().name();
					if (placeholder.equalsIgnoreCase("player_uuid"))
						return player.getUniqueId().toString();
					if (placeholder.equalsIgnoreCase("player_health"))
						return "" + ((Damageable) player).getHealth();
					if (placeholder.equalsIgnoreCase("player_food"))
						return "" + player.getFoodLevel();
					if (placeholder.equalsIgnoreCase("player_exp"))
						return "" + Ref.get(Ref.player(player), "exp");
					if (placeholder.equalsIgnoreCase("player_ping"))
						return "" + TheAPI.getPlayerPing(player);
					if (placeholder.equalsIgnoreCase("player_level"))
						return "" + player.getLevel();
					if (placeholder.equalsIgnoreCase("player_maxhealth"))
						return "" + ((Damageable) player).getMaxHealth();
					if (placeholder.equalsIgnoreCase("player_world"))
						return "" + player.getWorld().getName();
					if (placeholder.equalsIgnoreCase("player_air"))
						return "" + player.getRemainingAir();
					if (placeholder.equalsIgnoreCase("player_statistic_play_one_tick"))
						return "" + player.getStatistic(Statistic.valueOf("PLAY_ONE_TICK"));
					if (placeholder.equalsIgnoreCase("player_statistic_play_one_minue"))
						return "" + player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"));
					if (placeholder.equalsIgnoreCase("player_statistic_kills"))
						return "" + player.getStatistic(Statistic.PLAYER_KILLS);
					if (placeholder.equalsIgnoreCase("player_statistic_deaths"))
						return "" + player.getStatistic(Statistic.DEATHS);
					if (placeholder.equalsIgnoreCase("player_statistic_jump"))
						return "" + player.getStatistic(Statistic.JUMP);
					if (placeholder.equalsIgnoreCase("player_statistic_entity_kill"))
						return "" + player.getStatistic(Statistic.KILL_ENTITY);
					if (placeholder.equalsIgnoreCase("player_statistic_sneak_time"))
						return "" + player.getStatistic(Statistic.valueOf("SNEAK_TIME"));
					if (placeholder.startsWith("user:")) {
						placeholder=placeholder.substring(5);
						return TheAPI.getUser(player).getString(placeholder);
					}
				}
				if (placeholder.startsWith("user_other:")) {
					placeholder=placeholder.substring(11);
					return TheAPI.getUser(placeholder.split(":")[0]).getString(placeholder.substring((placeholder.split(":")[0]+":").length()));
				}
				if (placeholder.equalsIgnoreCase("server_time"))
					return "" + new SimpleDateFormat("HH:mm:ss").format(new Date());
				if (placeholder.equalsIgnoreCase("server_date"))
					return "" + new SimpleDateFormat("dd.MM.yyyy").format(new Date());
				if (placeholder.equalsIgnoreCase("server_online"))
					return "" + TheAPI.getOnlinePlayers().size();
				if (placeholder.equalsIgnoreCase("server_maxonline"))
					return "" + TheAPI.getMaxPlayers();
				if (placeholder.equalsIgnoreCase("server_max_online"))
					return "" + TheAPI.getMaxPlayers();
				if (placeholder.equalsIgnoreCase("server_version"))
					return Bukkit.getBukkitVersion();
				if (placeholder.equalsIgnoreCase("server_motd"))
					return motd != null ? motd : "";
				if (placeholder.equalsIgnoreCase("server_worlds"))
					return "" + Bukkit.getWorlds().size();
				if (placeholder.equalsIgnoreCase("server_tps"))
					return "" + TheAPI.getServerTPS();
				if (placeholder.equalsIgnoreCase("server_memory_max"))
					return "" + MemoryAPI.getMaxMemory();
				if (placeholder.equalsIgnoreCase("server_memory_used"))
					return "" + MemoryAPI.getUsedMemory(false);
				if (placeholder.equalsIgnoreCase("server_memory_free"))
					return "" + MemoryAPI.getFreeMemory(false);
				return null;
			}
		};
		timeUtils = new ThePlaceholder("TheAPI_TimeUtils") {
			Pattern fromTime = Pattern.compile("fromTime:\\{(.*?)\\}"),  toTime = Pattern.compile("toTime:\\{(.*?)\\}");
			public String onRequest(Player player, String placeholder) {
				if (placeholder.startsWith("fromTime:{")) {
					Matcher m = fromTime.matcher(placeholder);
					if(m.find())
						return ""+StringUtils.timeFromString(m.group(1));
				}
				if (placeholder.startsWith("toTime:{")) {
					Matcher m = toTime.matcher(placeholder);
					if(m.find())
						return StringUtils.timeToString((long)StringUtils.calculate(m.group(1)));
				}
				return null;
			}
		};
		timeUtils.register();
		main.register();
	}

	public void loadWorlds() {
		if (config.exists("Worlds"))
			if (!config.getStringList("Worlds").isEmpty()) {
				TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
				TheAPI.msg("&cTheAPI&7: &6Action: &eLoading worlds..", TheAPI.getConsole());
				TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
				for (String s : config.getStringList("Worlds")) {
					String type = null;
					for (String w : Arrays.asList("Default", "Normal", "Nether", "The_End", "End", "The_Void", "Void",
							"Empty", "Flat")) {
						if (config.exists("WorldsSetting." + s)) {
							if(config.exists("WorldsSetting." + s + ".Generator"))
							if (config.getString("WorldsSetting." + s + ".Generator").equalsIgnoreCase(w)) {
								if (w.equalsIgnoreCase("Normal")|| w.equalsIgnoreCase("0"))
									type = "Normal";
								if (w.equalsIgnoreCase("Flat")|| w.equalsIgnoreCase("3"))
									type = "Flat";
								if (w.equalsIgnoreCase("Nether")|| w.equalsIgnoreCase("1"))
									type = "Nether";
								if (w.equalsIgnoreCase("The_End") || w.equalsIgnoreCase("End")|| w.equalsIgnoreCase("2"))
									type = "The_End";
								if (w.equalsIgnoreCase("The_Void") || w.equalsIgnoreCase("Void")
										|| w.equalsIgnoreCase("Empty")|| w.equalsIgnoreCase("4"))
									type = "The_Void";
								break;
							}
						} else
							break;
					}
					Environment env = Environment.NORMAL;
					WorldType wt = WorldType.NORMAL;
					if(type==null) {
						String gen = config.getString("WorldsSetting." + s + ".Generator");
						ChunkGenerator g = null;
						if(gen.contains(":")) {
							g=PluginManagerAPI.getPlugin(gen.split(":")[0]).getDefaultWorldGenerator(s, gen.split(":")[1]);
						}else
							for(Plugin p : Bukkit.getPluginManager().getPlugins())
								g=p.getDefaultWorldGenerator(s, gen);
						if(g!=null) {
							boolean f = true;
							if (config.exists("WorldsSetting." + s + ".GenerateStructures"))
								f = config.getBoolean("WorldsSetting." + s + ".GenerateStructures");
							WorldsAPI.create(s, env, wt, g, f, 0);
							TheAPI.msg("&bTheAPI&7: &eWorld with name '&6" + s + "&e' loaded.", TheAPI.getConsole());
							continue;
						}
					}
					if (type.equals("Flat"))
						wt = WorldType.FLAT;
					if (type.equals("The_Void"))
						wt = null;
					if (type.equals("The_End")) {
						try {
							env = Environment.valueOf("THE_END");
						} catch (Exception e) {
							env = Environment.valueOf("END");
						}
					}
					if (type.equals("Nether"))
						env = Environment.NETHER;
					boolean f = true;
					if (config.exists("WorldsSetting." + s + ".GenerateStructures"))
						f = config.getBoolean("WorldsSetting." + s + ".GenerateStructures");
					WorldsAPI.create(s, env, wt, f, 0);
					TheAPI.msg("&bTheAPI&7: &eWorld with name '&6" + s + "&e' loaded.", TheAPI.getConsole());
				}
			}
	}

	private boolean getVaultEconomy() {
		try {
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null)
				economy = economyProvider.getProvider();
			return economy != null;
		} catch (Exception e) {
			return false;
		}
	}

	public void vaultHooking() {
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eLooking for Vault Economy..", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		new Tasker() {
			@Override
			public void run() {
				if (getVaultEconomy()) {
					e = true;
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &eFound Vault Economy", TheAPI.getConsole());
					TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
					cancel();
				}
			}
		}.runTimer(0, 20, 15);
	}
	
	public ChunkGenerator getDefaultWorldGenerator(String world, String id) {
		if(id.equalsIgnoreCase("void")) {
			return TheAPI.isNewerThan(8)?new voidGenerator():new voidGenerator_1_8();
		}
		return null;
	}
}
