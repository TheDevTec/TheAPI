package me.devtec.theapi.utils.theapiutils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.bukkit.Statistic;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.MemoryAPI;
import me.devtec.theapi.apis.PluginManagerAPI;
import me.devtec.theapi.apis.ResourcePackAPI;
import me.devtec.theapi.apis.ResourcePackAPI.ResourcePackResult;
import me.devtec.theapi.bossbar.BossBar;
import me.devtec.theapi.configapi.Config;
import me.devtec.theapi.economyapi.EconomyAPI;
import me.devtec.theapi.guiapi.AnvilGUI;
import me.devtec.theapi.guiapi.HolderGUI;
import me.devtec.theapi.guiapi.ItemGUI;
import me.devtec.theapi.placeholderapi.PlaceholderAPI;
import me.devtec.theapi.placeholderapi.PlaceholderPreRegister;
import me.devtec.theapi.placeholderapi.ThePlaceholder;
import me.devtec.theapi.placeholderapi.ThePlaceholderAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.sockets.Client;
import me.devtec.theapi.sockets.Server;
import me.devtec.theapi.sockets.ServerClient;
import me.devtec.theapi.utils.SpigotUpdateChecker;
import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.listener.events.ClientReceiveMessageEvent;
import me.devtec.theapi.utils.listener.events.ServerReceiveMessageEvent;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.packetlistenerapi.PacketHandler;
import me.devtec.theapi.utils.packetlistenerapi.PacketHandler_New;
import me.devtec.theapi.utils.packetlistenerapi.PacketHandler_Old;
import me.devtec.theapi.utils.packetlistenerapi.PacketListener;
import me.devtec.theapi.utils.packetlistenerapi.PacketManager;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoggerManager.BukkitLogger;
import me.devtec.theapi.utils.theapiutils.LoggerManager.ConsoleLogger;
import me.devtec.theapi.utils.theapiutils.command.TheAPICommand;
import me.devtec.theapi.utils.theapiutils.metrics.Metrics;
import me.devtec.theapi.worldsapi.WorldsAPI;
import net.milkbowl.vault.economy.Economy;

public class LoaderClass extends JavaPlugin {
	public final static Map<String, String> colorMap = new HashMap<>();
	// GUIs
	public final Map<String, HolderGUI> gui = new HashMap<>();
	// BossBars
	public final Set<BossBar> bars = new HashSet<>();
	// TheAPI
	public static LoaderClass plugin;
	public static Config config = new Config("TheAPI/Config.yml"), sockets = new Config("TheAPI/Sockets.yml"), tags,
			data = new Config("TheAPI/Data.dat", DataType.BYTE);
	public static Cache cache;
	
	public String motd;
	public static String ss;
	public static String gradientTag, tagG;
	public int max;
	// EconomyAPI
	public boolean e, tve, tbank;
	public Economy economy;
	public Object air = Ref.invoke(Ref.getNulled(Ref.field(Ref.nms("Block"), "AIR")), "getBlockData");
	public Map<String, Client> servers;
	public Server server;

	private String generate() {
		String d = "abcdefghijklmnopqrstuvwxyz0123456789";
		int len = d.length();
		char[] a = d.toCharArray();
		Random r = new Random();
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < 16; ++i)
			b.append(r.nextBoolean() ? (a[TheAPI.generateRandomInt(len)]) : (Character.toUpperCase(a[TheAPI.generateRandomInt(len)])));
		return b.toString();
	}

	public static enum InventoryClickType {
		PICKUP, QUICK_MOVE, SWAP, CLONE, THROW, QUICK_CRAFT, PICKUP_ALL;
	}
	
	@Override
	public void onLoad() {
		plugin = this;
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
		new PacketListener() {
			
			@Override
			public boolean PacketPlayOut(String player, Object packet, Object channel) {
				return false;
			}

			Method getSlot = Ref.method(Ref.nms("Container"), "getSlot", int.class), updateInv=Ref.method(Ref.nms("EntityPlayer"), "updateInventory", Ref.nms("Container"));
			Constructor<?> setSlot = Ref.constructor(Ref.nms("PacketPlayOutSetSlot"), int.class, int.class, Ref.nms("ItemStack"))
					,equipment = Ref.constructor(Ref.nms("PacketPlayOutEntityEquipment"), int.class, List.class);
			Object OFFHAND = Ref.getStatic(Ref.nms("EnumItemSlot"),"OFFHAND");
			@Override
			public boolean PacketPlayIn(String player, Object packet, Object channel) {
				//ResourcePackAPI
				if(packet.toString().contains("PacketPlayInResourcePackStatus")) {
					Player s = TheAPI.getPlayer(player);
					if(ResourcePackAPI.getResourcePack(s)==null||ResourcePackAPI.getHandlingPlayer(s)==null)return false;
					ResourcePackAPI.getHandlingPlayer(s).onHandle(s, ResourcePackAPI.getResourcePack(s), ResourcePackResult.valueOf(Ref.get(packet, "status").toString()));
					return false;
				}
				//GUIS
				if(packet.toString().contains("PacketPlayInCloseWindow")) {
					Player p = (Player) TheAPI.getPlayer(player);
					HolderGUI d = LoaderClass.plugin.gui.getOrDefault(p.getName(), null);
					if (d == null)
						return false;
					LoaderClass.plugin.gui.remove(p.getName());
					d.closeWithoutPacket(p);
					return true;
				}
				if(packet.toString().contains("PacketPlayInWindowClick")) {
					int id = (int) Ref.get(packet, "a");
					int mouseClick = (int) Ref.get(packet, "button");
					int slot = (int) Ref.get(packet, "slot");
					Player p = (Player) TheAPI.getPlayer(player);
					InventoryClickType type = null;
					if(Ref.get(packet, "shift") instanceof Integer) {
						type=InventoryClickType.values()[(int)Ref.get(packet, "shift")];
					}else {
						type=InventoryClickType.valueOf(Ref.get(packet, "shift").toString());
					}
					HolderGUI d = LoaderClass.plugin.gui.getOrDefault(p.getName(), null);
					if (d == null)
						return false;
					if(InventoryClickType.THROW==type||InventoryClickType.PICKUP==type && slot==-999)return false;
					if(InventoryClickType.SWAP==type) {
						Ref.sendPacket(p,Ref.newInstance(setSlot,id, slot, Ref.invoke(Ref.invoke(d.getContainer(p), getSlot, slot),"getItem")));
						if(mouseClick == 40 && TheAPI.isNewerThan(8)) {
							final List<com.mojang.datafixers.util.Pair<?,?>> equipmentList = new ArrayList<>();
							equipmentList.add(new com.mojang.datafixers.util.Pair<>(OFFHAND, NMSAPI.asNMSItem(p.getEquipment().getItemInOffHand())));
							Ref.sendPacket(p,Ref.newInstance(equipment, p.getEntityId(), equipmentList));
						}else
						Ref.sendPacket(p,Ref.newInstance(setSlot,id, (36+d.size())-9+mouseClick, NMSAPI.asNMSItem(p.getInventory().getItem(mouseClick))));
						Ref.sendPacket(p,Ref.newInstance(setSlot,-1, -1, NMSAPI.asNMSItem(p.getItemOnCursor())));
						if (slot < d.size()) {
							ItemGUI a = d.getItemGUI(slot);
							if (a != null) {
								a.onClick(p, d, me.devtec.theapi.guiapi.GUI.ClickType.LEFT_PICKUP);
							}
						}
						return true;
					}
					ItemStack i = NMSAPI.asBukkitItem(Ref.get(packet, "item"));
					if((type==InventoryClickType.QUICK_MOVE||type==InventoryClickType.CLONE) && i.getType()==Material.AIR)
						i=NMSAPI.asBukkitItem(Ref.invoke(Ref.invoke(d.getContainer(p), getSlot, slot),"getItem"));
					ItemStack before = p.getItemOnCursor();
					if(type!=InventoryClickType.PICKUP_ALL)
					if(before.getType()==Material.AIR&&i.getType()==Material.AIR || type!=InventoryClickType.CLONE && type!=InventoryClickType.QUICK_MOVE && type!=InventoryClickType.QUICK_CRAFT && type!=InventoryClickType.PICKUP) {
						Ref.sendPacket(p,Ref.newInstance(setSlot,id, slot, Ref.invoke(Ref.invoke(d.getContainer(p), getSlot, slot),"getItem")));
						Ref.sendPacket(p,Ref.newInstance(setSlot,-1, -1, NMSAPI.asNMSItem(before)));
						return true;
					}
					String action = i.getType()==Material.AIR && (type==InventoryClickType.PICKUP||type==InventoryClickType.QUICK_CRAFT)?"DROP":"PICKUP";
					action=(type==InventoryClickType.CLONE?"MIDDLE_":(mouseClick==0?"LEFT_":mouseClick==1?"RIGHT_":"MIDDLE_"))+action;
					if(type==InventoryClickType.QUICK_MOVE)
					action="SHIFT_"+action;
					boolean cancel = false;
					if (slot < d.size()) {
						if(action.contains("DROP"))
							cancel = d.onPutItem(p, i, slot%d.size());
						else
							cancel = d.onTakeItem(p, i, slot%d.size());
						ItemGUI a = d.getItemGUI(slot);
						if (a != null) {
							if (a.isUnstealable())
								cancel=true;
							a.onClick(p, d, me.devtec.theapi.guiapi.GUI.ClickType.valueOf(action));
						}
						if(!cancel && d instanceof AnvilGUI) {
							if(action.contains("DROP") && slot < 2) {
							if(d.getItemGUI(slot)==null) 
								((AnvilGUI) d).getInventory().put(slot, i);
							}else if(action.contains("PICKUP")) {
								if(slot==2) {
									((AnvilGUI) d).getInventory().remove(0);
									((AnvilGUI) d).getItemGUIs().remove(0);
									((AnvilGUI) d).getInventory().remove(1);
									((AnvilGUI) d).getItemGUIs().remove(1);
								}
								((AnvilGUI) d).getInventory().remove(slot);
								((AnvilGUI) d).getItemGUIs().remove(slot);
							}
						}
					}else
						if(!d.isInsertable())
							cancel=true;
					if(cancel) {
						if(type==InventoryClickType.QUICK_MOVE||type==InventoryClickType.PICKUP_ALL) {
							Ref.invoke(Ref.player(p), updateInv, d.getContainer(p));
						}else {
							Ref.sendPacket(p,Ref.newInstance(setSlot,id, slot, Ref.invoke(Ref.invoke(d.getContainer(p), getSlot, slot),"getItem")));
							Ref.sendPacket(p,Ref.newInstance(setSlot,-1, -1, NMSAPI.asNMSItem(before)));
						}
						return true;
					}
					if(type!=InventoryClickType.CLONE && slot%d.size()!=2 && d instanceof AnvilGUI)((AnvilGUI) d).getInventory().put(slot%d.size(), i);
				}
				return false;
			}
		}.register();
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eLoading plugin..", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		
		//CONFIG
		createConfig();
		
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
					TheAPI.callEvent(new ServerReceiveMessageEvent(client, data));
				}
			});
			for(String s : sockets.getKeys("Server")) {
				servers.put(s, new Client(sockets.getString("Options.Name"), sockets.getString("Server."+s+".Password"), sockets.getString("Server."+s+".IP"), sockets.getInt("Server."+s+".Port")) {
					public void read(Data data) {
						TheAPI.callEvent(new ClientReceiveMessageEvent(this, data));
					}
				});
			}
		}else sockets.getData().clear();
		
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
				colorMap.put(tag.toLowerCase(), "#" + tags.getString("Tags." + tag));
			StringUtils.gradientFinder=Pattern.compile(LoaderClass.gradientTag+"(#[A-Fa-f0-9]{6})(.*?)"+LoaderClass.gradientTag+"(#[A-Fa-f0-9]{6})|.*?(?=(?:"+LoaderClass.gradientTag+"#[A-Fa-f0-9]{6}.*?"+LoaderClass.gradientTag+"#[A-Fa-f0-9]{6}))");
		}
		PluginCommand ca = TheAPI.createCommand("theapi", this);
		if(Ref.field(Command.class, "timings")!=null && TheAPI.isOlderThan(9)) {
			Ref.set(Bukkit.getServer(), "commandMap", new Old1_8SimpleCommandMap(Bukkit.getServer(), TheAPI.knownCommands));
			ca = TheAPI.createCommand("theapi", this);
		}
		ca.setExecutor(new TheAPICommand());
		TheAPI.registerCommand(ca);
	}

	public void onEnable() {
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eEnabling plugin, creating config and registering economy..",
				TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		
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
		Bukkit.getPluginManager().registerEvents(new Events(), LoaderClass.this);

		if(config.getBoolean("Options.ItemUnbreakable"))
		Bukkit.getPluginManager().registerEvents(new ItemBreakEvent(), LoaderClass.this);
		if (TheAPI.isNewerThan(7))
			handler = new PacketHandler_New();
		else
			handler = new PacketHandler_Old();
		loadWorlds();
		loadPlaceholders();
		if (PlaceholderAPI.isEnabledPlaceholderAPI()) {
			/*
			 * TheAPI placeholder extension for PAPI BRIDGE:
			 * 
			 * PAPI -> THEAPI : %papi_placeholder_here% PAPI <- THEAPI :
			 * %theapi_{theapi_placeholder_here}%
			 */
			new PlaceholderPreRegister("TheAPI", "DevTec", getDescription().getVersion()) {
				Pattern finder = Pattern.compile("\\{(.*?)\\}"),
						math = Pattern.compile("\\{math\\{((?:\\{??[^A-Za-z\\{][ 0-9+*/^%()~.-]*))\\}\\}");

				@Override
				public String onRequest(Player player, String params) {
					String text = params;
					while (true) {
						Matcher m = math.matcher(text);
						int v = 0;
						while (m.find()) {
							++v;
							String w = m.group();
							text = text.replace(w, StringUtils.calculate(w.substring(6, w.length() - 2)).toString());
						}
						if (v != 0)
							continue;
						else
							break;
					}
					Matcher found = finder.matcher(text);
					while (found.find()) {
						String g = found.group();
						String find = g.substring(1, g.length() - 1);
						int v = 0;
						for (Iterator<ThePlaceholder> r = ThePlaceholderAPI.getPlaceholders().iterator(); r
								.hasNext();) {
							++v;
							ThePlaceholder get = r.next();
							String toReplace = get.onPlaceholderRequest(player, find);
							if (toReplace != null)
								text = text.replace("{" + find + "}", toReplace);
						}
						if (v != 0)
							continue;
						else
							break;
					}
					return text.equals(params) ? null : text;
				}
			}.register();
		}
		
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
				int removed = 0;
				if (new File("plugins/TheAPI/User").exists())
					for (File f : Arrays.asList(new File("plugins/TheAPI/User").listFiles())) {
						if (StreamUtils.fromStream(f).trim().isEmpty()) {
							f.delete();
							++removed;
						}
					}
				if (removed != 0)
					TheAPI.msg("&cTheAPI&7: &eTheAPI deleted &6" + removed + " &eunused user files",
							TheAPI.getConsole());
				new Tasker() {
					@Override
					public void run() {
						for(User u : TheAPI.getCachedUsers())
							u.save();
					}
				}.runRepeating(20*300, 20*300);
				TheAPI.clearCache();
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
	}

	private SpigotUpdateChecker checker;

	@SuppressWarnings("rawtypes")
	public PacketHandler handler;

	@Override
	public void onDisable() {
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &6Action: &eDisabling plugin, saving configs and stopping runnables..",
				TheAPI.getConsole());
		TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
		
		//GUI
		for (Entry<String, HolderGUI> p : gui.entrySet()) {
			p.getValue().clear();
			p.getValue().close();
		}
		gui.clear();
		
		//Scheulder
		Scheduler.cancelAll();
		
		//Sockets
		if(server!=null)
			server.exit();
		
		//PacketListener
		handler.close();
		PacketManager.unregisterAll();
		
		//Placeholders
		main.unregister();
		
		//Users
		for(User u : TheAPI.getCachedUsers())
			u.save();
		
		//Bans
		data.save();
		
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
			data.save(DataType.BYTE);
		}
	}

	public List<Plugin> getTheAPIsPlugins() {
		List<Plugin> a = new ArrayList<>();
		for (Plugin all : PluginManagerAPI.getPlugins())
			if (PluginManagerAPI.getDepend(all.getName()).contains("TheAPI")
					|| PluginManagerAPI.getSoftDepend(all.getName()).contains("TheAPI"))
				a.add(all);
		return a;
	}

	public int receive_speed = 500;
	public long relog = 10000;
	
	private void createConfig() {
		config.addDefault("Options.HideErrors", false); // hide only TheAPI errors
		config.setComments("Options.HideErrors", Arrays.asList("",
				"# If you enable this option, errors from TheAPI will dissapear", "# defaulty: false"));
		config.addDefault("Options.ConsoleLogEvent", false);
		config.addDefault("Options.ItemUnbreakable", true);
		config.addDefault("Options.Cache.User.Use", true); // Require memory, but loading of User.class is faster (only
		// from TheAPI.class)
		config.addDefault("Options.SocketsSpeed", "PERFORMANCE");
		config.setComments("Options.SocketsSpeed",
				Arrays.asList("# Speed types: FAST, PERFORMANCE, SLOW", "# defaulty: PERFORMANCE"));
		config.setComments("Options.Cache", Arrays.asList(""));
		config.setComments("Options.Cache.User.Use",
				Arrays.asList("# Cache Users to memory for faster loading", "# defaulty: true"));
		config.addDefault("Options.Cache.User.RemoveOnQuit", true); // Remove cached player from cache on
																	// PlayerQuitEvent
		config.setComments("Options.Cache.User.RemoveOnQuit",
				Arrays.asList("# Remove cache of User from memory", "# defaulty: true"));

		config.addDefault("Options.Cache.User.OfflineNames.Use", true); // Cache offline-names of players
		config.addDefault("Options.Cache.User.OfflineNames.AutoSave", "15min"); // Period in which offline-names saves
		config.addDefault("Options.Cache.User.OfflineNames.AutoClear.Use", false); // Enable automatic clearing of cache
		config.addDefault("Options.Cache.User.OfflineNames.AutoClear.OfflineTime", "1mon"); // Automatic clear cache after 1mon
		config.addDefault("Options.Cache.User.OfflineNames.AutoClear.Period", "0"); // 0 means on startup or type time period
		config.addDefault("Options.User-SavingType", "YAML");
		config.setComments("Options.User-SavingType",
				Arrays.asList("", "# Saving type of User data", "# Types: YAML, JSON, BYTE", "# defaulty: YAML"));
		config.addDefault("Options.AntiBot.Use", false);
		config.setComments("Options.AntiBot", Arrays.asList(""));
		config.setComments("Options.AntiBot.Use",
				Arrays.asList("# If you enable this, TheAPI will set time between player can't connect to the server",
						"# defaulty: false"));
		config.addDefault("Options.AntiBot.TimeBetweenPlayer", 10); // 10 milis
		config.setComments("Options.AntiBot.TimeBetweenPlayer",
				Arrays.asList("# Time between player can't connect to the server", "# defaulty: 10"));
		config.addDefault("Options.EntityMoveEvent.Enabled", true);
		config.setComments("Options.EntityMoveEvent.Enabled",
				Arrays.asList("# Enable EntityMoveEvent event", "# defaulty: true"));
		config.addDefault("Options.EntityMoveEvent.Reflesh", 3);
		config.setComments("Options.EntityMoveEvent.Reflesh",
				Arrays.asList("# Ticks to look for entity move action", "# defaulty: 3"));
		config.addDefault("Options.FakeEconomyAPI.Symbol", "$");
		config.setComments("Options.FakeEconomyAPI", Arrays.asList(""));
		config.setComments("Options.FakeEconomyAPI.Symbol",
				Arrays.asList("# Economy symbol of FakeEconomyAPI", "# defaulty: $"));
		config.addDefault("Options.FakeEconomyAPI.Format", "$%money%");
		config.setComments("Options.FakeEconomyAPI.Format",
				Arrays.asList("# Economy format of FakeEconomyAPI", "# defaulty: $%money%"));
		config.save();	
		if(config.getString("Options.SocketsSpeed").equalsIgnoreCase("fast")) {
			receive_speed=100;
			relog=5000;
		}
		if(config.getString("Options.SocketsSpeed").equalsIgnoreCase("performance")) {
			receive_speed=500;
			relog=10000;
		}
		if(config.getString("Options.SocketsSpeed").equalsIgnoreCase("slow")) {
			receive_speed=1000;
			relog=15000;
		}
		if(config.getBoolean("Options.Cache.User.OfflineNames.Use")) {
			if(cache==null) {
				cache=new Cache();
				Data data = new Data("plugins/TheAPI/Cache.dat");
				for(String s : data.getKeys())
					cache.setLookup(UUID.fromString(s), data.getString(s));
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
						data.save(DataType.BYTE);
					}
				}.runRepeating(0, 20*StringUtils.timeFromString(config.getString("Options.Cache.User.OfflineNames.AutoSave")));
				if(config.getBoolean("Options.Cache.User.OfflineNames.AutoClear.Use")) {
					long removeAfter = StringUtils.timeFromString(config.getString("Options.Cache.User.OfflineNames.AutoClear.OfflineTime"));
					if(removeAfter > 0 && StringUtils.timeFromString(config.getString("Options.Cache.User.OfflineNames.AutoClear.Period"))<=0) {
						int removed = 0;
						for(String s : data.getKeys()) {
							User user = new User(data.getString(s), UUID.fromString(s));
							if(user.getLong("quit")==0) {
								user.data().clear(); //clear cache from memory
								continue; //fake user?
							}
							if(user.getLong("quit") - System.currentTimeMillis()/1000 + removeAfter <= 0) {
								cache.nameLookup.remove(user.getName().toLowerCase());
								cache.uuidLookup.remove(user.getName().toLowerCase());
								TheAPI.removeCachedUser(user.getUUID());
								user.delete(); //delete file
								++removed;
							}else
								user.data().clear(); //clear cache from memory
						}
						data.clear();
						if (removed != 0)
							TheAPI.msg("&cTheAPI&7: &eTheAPI deleted &6" + removed + " &eunused user files",
									TheAPI.getConsole());
					}else {
						new Tasker() {
							public void run() {
								int removed = 0;
								for(Entry<String, UUID> s : cache.uuidLookup.entrySet()) {
									User user = new User(cache.nameLookup.get(s.getKey()), s.getValue());
									if(user.getLong("quit")==0) {
										user.data().clear(); //clear cache from memory
										continue; //fake user?
									}
									if(user.getLong("quit") - System.currentTimeMillis()/1000 + removeAfter <= 0) {
										cache.nameLookup.remove(user.getName().toLowerCase());
										cache.uuidLookup.remove(user.getName().toLowerCase());
										TheAPI.removeCachedUser(user.getUUID());
										user.delete(); //delete file
										++removed;
									}else
										user.data().clear(); //clear cache from memory
								}
								if (removed != 0)
									TheAPI.msg("&cTheAPI&7: &eTheAPI deleted &6" + removed + " &eunused user files",
											TheAPI.getConsole());
							}
						}.runRepeating(0, 20*StringUtils.timeFromString(config.getString("Options.Cache.User.OfflineNames.AutoClear.Period")));
					}
				}
			}
		}
		max = Bukkit.getMaxPlayers();
		motd = Bukkit.getMotd();
	}

	private static ThePlaceholder main;

	public void loadPlaceholders() {
		main = new ThePlaceholder("TheAPI") {
			@Override
			public String onRequest(Player player, String placeholder) {
				if (player != null) {
					if (placeholder.equalsIgnoreCase("player_money"))
						return "" + EconomyAPI.getBalance(player);
					if (placeholder.equalsIgnoreCase("player_formated_money"))
						return EconomyAPI.format(EconomyAPI.getBalance(player));
					if (placeholder.equalsIgnoreCase("player_displayname"))
						return player.getDisplayName();
					if (placeholder.equalsIgnoreCase("player_customname"))
						return player.getCustomName();
					if (placeholder.equalsIgnoreCase("player_name"))
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
		main.register();
	}

	public void loadWorlds() {
		if (config.exists("Worlds"))
			if (!config.getStringList("Worlds").isEmpty()) {
				TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
				TheAPI.msg("&cTheAPI&7: &6Action: &eLoading worlds..", TheAPI.getConsole());
				TheAPI.msg("&cTheAPI&7: &8********************", TheAPI.getConsole());
				for (String s : config.getStringList("Worlds")) {
					String type = "Default";
					for (String w : Arrays.asList("Default", "Normal", "Nether", "The_End", "End", "The_Void", "Void",
							"Empty", "Flat")) {
						if (config.exists("WorldsSetting." + s)) {
							if(config.exists("WorldsSetting." + s + ".Generator"))
							if (config.getString("WorldsSetting." + s + ".Generator").equalsIgnoreCase(w)) {
								if (w.equalsIgnoreCase("Flat"))
									type = "Flat";
								if (w.equalsIgnoreCase("Nether"))
									type = "Nether";
								if (w.equalsIgnoreCase("The_End") || w.equalsIgnoreCase("End"))
									type = "The_End";
								if (w.equalsIgnoreCase("The_Void") || w.equalsIgnoreCase("Void")
										|| w.equalsIgnoreCase("Empty"))
									type = "The_Void";
								break;
							}
						} else
							break;
					}
					Environment env = Environment.NORMAL;
					WorldType wt = WorldType.NORMAL;
					if (type.equals("Flat"))
						wt = WorldType.FLAT;
					if (type.equals("The_Void"))
						env = null;
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
			RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
					.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();

			}
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
}
