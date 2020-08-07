package me.DevTec;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.DevTec.Bans.PunishmentAPI;
import me.DevTec.Bans.ReportSystem;
import me.DevTec.Blocks.BlocksAPI;
import me.DevTec.BossBar.BarColor;
import me.DevTec.BossBar.BarStyle;
import me.DevTec.BossBar.BossBar;
import me.DevTec.Events.PlayerVanishEvent;
import me.DevTec.NMS.NMSAPI;
import me.DevTec.NMS.NMSAPI.TitleAction;
import me.DevTec.Other.LoaderClass;
import me.DevTec.Other.MultiMap;
import me.DevTec.Other.ScoreboardType;
import me.DevTec.Other.SlowLoop;
import me.DevTec.Other.Storage;
import me.DevTec.Other.StringUtils;
import me.DevTec.Other.User;
import me.DevTec.Placeholders.PlaceholderAPIUtils;
import me.DevTec.Placeholders.ThePlaceholderAPI;
import me.DevTec.Scheduler.Tasker;
import me.DevTec.Utils.Error;
import me.DevTec.WorldsManager.WorldBorderAPI;
import me.DevTec.WorldsManager.WorldsManager;
import net.glowstone.entity.GlowPlayer;

public class TheAPI {
	private static final HashMap<String, BossBar> bars = Maps.newHashMap();
	private static final HashMap<String, Integer> task = Maps.newHashMap();
	private static final HashMap<UUID, User> cache = Maps.newHashMap();

	public static void clearCache() {
		cache.clear();
	}

	public static boolean generateChance(double chance) {
		return generateRandomDouble(100) <= chance;
	}

	public static NMSAPI getNMSAPI() {
		return new NMSAPI();
	}

	public static double getProcessCpuLoad() {
		try {
	    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
	    ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
	    AttributeList list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });
	    if (list.isEmpty())return 0.0;
	    Attribute att = (Attribute)list.get(0);
	    Double value  = (Double)att.getValue();
	    if (value == -1.0)return 0;
	    return ((value * 1000.0) / 10.0);
		}catch(Exception e) {
			return 0;
		}
	}

	/**
	 * @see see Colorize string with colors
	 * @param string
	 * @return Colored String
	 */
	public static String colorize(String string) {
		return getStringUtils().colorize(string);
	}

	/**
	 * @see see Create sign on location with actions on click
	 * @return SignAPI
	 */
	public static SignAPI getSignAPI() {
		return new SignAPI();
	}

	/**
	 * @see see Create Inventory that can be used as Storage for Items or Sorter
	 * @return Storage
	 */
	public static Storage getStorage() {
		return new Storage();
	}

	/**
	 * @see see Get all blocks in radius 20 blocks
	 * @return BlocksAPI
	 */
	public static BlocksAPI getBlocksAPI() {
		return new BlocksAPI();
	}

	/**
	 * @see see This is HashMap with easier manupulation
	 * @return MultiMap<T>
	 */
	public static <K, T, V> MultiMap<K, T, V> getMultiMap() {
		return new MultiMap<K, T, V>();
	}

	/**
	 * @see see StringUtils (Get int/double/long from String, convertLong to String
	 *      time or visa verse and more
	 * @return StringUtils
	 */
	public static StringUtils getStringUtils() {
		return new StringUtils();
	}

	/**
	 * @see see Create or delete config
	 * @param localization
	 * @param name
	 * @return ConfigAPI
	 */
	public static ConfigAPI getConfig(String localization, String name) {
		return new ConfigAPI(localization, name);
	}

	/**
	 * @see see Create or delete config
	 * @param plugin
	 * @param name
	 * @return ConfigAPI
	 */
	public static ConfigAPI getConfig(Plugin plugin, String name) {
		return new ConfigAPI(plugin.getName(), name);
	}

	/**
	 * @see see Return is server version 1.13+
	 * @return boolean
	 */
	public static boolean isNewVersion() {
		return !getServerVersion().equalsIgnoreCase("glowstone")
				&& getStringUtils().getInt(getServerVersion().split("_")[1]) > 12;
	}

	/**
	 * @see see Return is server version older than 1.9 ? (1.5 up to 1.8.9)
	 * @return boolean
	 */
	public static boolean isOlder1_9() {
		return getStringUtils().getInt(getServerVersion().split("_")[1]) < 9;
	}

	/**
	 * @see see Return time in which server start
	 * @return long
	 */
	public static long getServerStartTime() {
		return ManagementFactory.getRuntimeMXBean().getStartTime();
	}

	/**
	 * @see see Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(String[] args) {
		return getStringUtils().buildString(args);
	}

	public static SQLAPI getSQLAPI(String host, String database, String username, String password, int port) {
		return new SQLAPI(host, database, username, password, port);
	}

	public static SQLAPI getSQLAPI(String host, String database, String username, String password) {
		return getSQLAPI(host, database, username, password, 3306); // 3306 is default.
	}

	/**
	 * @see see Create your own entity with damage, health and more
	 * @param type
	 * @return EntityCreatorAPI
	 */
	public static EntityCreatorAPI getEntityCreatorAPI(EntityType type) {
		return new EntityCreatorAPI(type);
	}

	/**
	 * @see see Return random object from list
	 * @param list
	 * @return Object
	 */
	public static Object getRandomFromList(List<?> list) {
		return getStringUtils().getRandomFromList(list);
	}

	/**
	 * @see see Set world border size, center and more
	 * @param world
	 * @return WorldBorderAPI
	 */
	public static WorldBorderAPI getWorldBorder(World world) {
		return new WorldBorderAPI(world);
	}

	/**
	 * @see see Generate random int with limit
	 * @param maxInt
	 * @return int
	 */
	public static int generateRandomInt(int maxInt) {
		boolean inMinus = false;
		if (maxInt < 0) {
			maxInt = -1 * maxInt;
			inMinus = true;
		}
		if (maxInt == 0) {
			return 0;
		}
		int i = new Random().nextInt(maxInt);
		if (inMinus)
			i = -1 * i;
		return i;
	}

	/**
	 * @see see Replace in String/List<String> placeholders from PlaceholderAPI
	 *      without depend or register placeholders
	 * @return PlaceholderAPIUtils
	 */
	public static PlaceholderAPIUtils getPlaceholderAPI() {
		return new PlaceholderAPIUtils();
	}

	/**
	 * @see see Replace in String/List<String> placeholders from TheAPI
	 * @return ThePlaceholderAPI
	 */
	public static ThePlaceholderAPI getThePlaceholderAPI() {
		return new ThePlaceholderAPI();
	}

	/**
	 * @see see Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static double generateRandomDouble(double maxDouble) {
		boolean inMinus = false;
		if (maxDouble < 0) {
			maxDouble = -1 * maxDouble;
			inMinus = true;
		}
		if (maxDouble == 0.0) {
			return 0.0;
		}
		double i = new Random().nextInt((int) maxDouble) + new Random().nextDouble();
		if (i <= 0)
			i = 1;
		if (i > maxDouble)
			i = maxDouble;
		if (inMinus)
			i = -1 * i;
		return i;
	}

	/**
	 * @see see GameAPI in which you can create your own minigame
	 * @param MiniGameName
	 * @return GameAPI
	 */
	public static GameAPI getGameAPI(String MiniGameName) {
		return new GameAPI(MiniGameName);
	}

	/**
	 * @see see Server up time in long
	 * @return long
	 */
	public static long getServerUpTime() {
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}

	/**
	 * @see see Get player from List<Player>
	 * @return Player
	 */
	public static Player getPlayer(int i) {
		return getOnlinePlayers().size()-1 <= i ? null : getOnlinePlayers().get(i);
	}

	/**
	 * @see see Get player by name
	 * @return Player
	 */
	public static Player getPlayer(String name) {
		return Bukkit.getPlayer(name);
	}

	/**
	 * @see see Get random player from List<Player>
	 * @return Player
	 */
	public static Player getRandomPlayer() {
		Player r = (Player) getRandomFromList(getOnlinePlayers());
		return r == null ? null : r;
	}

	/**
	 * @see see Get random player from List<Player>
	 * @return List<Player>
	 */
	@SuppressWarnings("unchecked")
	public static List<Player> getOnlinePlayers() {
		ArrayList<Player> a = Lists.newArrayList();
		try {
			Method m = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
			Object o = m.invoke(null);
			for (Player p : o instanceof Collection ? (Collection<Player>) o : Arrays.asList((Player[]) o))
				a.add(p);
		} catch (Exception ex) {
		}
		return a;
	}

	/**
	 * @see see Get random player from List<Player>
	 * @return List<Player>
	 */
	public static List<Player> getPlayers() {
		return getOnlinePlayers();
	}

	public static void sendMessage(String message, CommandSender sender) {
		if (sender == null) {
			Error.err("sending message", "CommandSender is null");
			return;
		}
		if (message == null) {
			Error.err("sending message", "Message is null");
			return;
		}
		ChatColor old = ChatColor.RESET;
		for (String s : message.replace("\\n", "\n").split("\n")) {
			sender.sendMessage(old + colorize(s));
			old = getStringUtils().getColor(s);
		}
	}

	public static void sendMsg(String message, CommandSender sender) {
		sendMessage(message, sender);
	}

	public static void msg(String message, CommandSender sender) {
		sendMessage(message, sender);
	}

	/**
	 * @see see Send player bossbar
	 * @param p
	 * @param text
	 * @param progress
	 * @param timeToExpire
	 */
	public static BossBar sendBossBar(Player p, String text, double progress) {
		if (p == null) {
			Error.err("sending bossbar", "Player is null");
			return null;
		}
		if(task.containsKey(p.getName())) {
			Tasker.cancelTask(task.get(p.getName()));
			task.remove(p.getName());
		}
		BossBar a = bars.containsKey(p.getName())?bars.get(p.getName()) : new BossBar(p, text, progress, BarColor.GREEN, BarStyle.PROGRESS);
		if (progress < 0)
			progress = 0;
		if (progress > 1)
			progress = 1;
		a.setProgress(progress);
		a.setTitle(colorize(text));
		if(a.isHidden())a.show();
		if(!bars.containsKey(p.getName()))
		bars.put(p.getName(), a);
		return a;
	}

	/**
	 * @see see Send player bossbar on time
	 * @param p
	 * @param text
	 * @param progress
	 * @param timeToExpire
	 */
	public static void sendBossBar(Player p, String text, double progress, int timeToExpire) {
		if (p == null) {
			Error.err("sending bossbar", "Player is null");
			return;
		}
		BossBar a = sendBossBar(p, text, progress);
		task.put(p.getName(), new Tasker() {
			@Override
			public void run() {
				a.hide();
			}
		}.laterAsync(timeToExpire));
	}

	/**
	 * @see see Remove player from all bossbars in which player is in
	 * @param p
	 */
	public static void removeBossBar(Player p) {
		if (p == null) {
			Error.err("removing bossbars", "Player is null");
			return;
		}
		if(task.containsKey(p.getName())) {
		Tasker.cancelTask(task.get(p.getName()));
		task.remove(p.getName());
		}
		if(bars.containsKey(p.getName()))bars.get(p.getName()).remove();
	}

	/**
	 * @see see Return list with bossbars in which player is in
	 * @param p
	 * @return BossBar
	 */
	public static BossBar getBossBar(Player p) {
		if (p == null) {
			Error.err("getting bossbars", "Player is null");
			return null;
		}
		return bars.containsKey(p.getName()) ? bars.get(p.getName()) : null;

	}

	/**
	 * @see see Remove player action bar instanceof sendActionBar(player, "")
	 * @param p
	 */
	public static void removeActionBar(Player p) {
		if (p == null) {
			Error.err("sending ActionBar", "Player is null");
			return;
		}
		sendActionBar(p, "");
	}

	/**
	 * @see see Send player action bar
	 * @param p
	 * @param text
	 */
	public static void sendActionBar(Player p, String text) {
		sendActionBar(p,text,10,20,10);
	}

	/**
	 * @see see Send player action bar
	 * @param p
	 * @param text
	 */
	public static void sendActionBar(Player p, String text, int fadeIn, int stay, int fadeOut) {
		if (p == null) {
			Error.err("sending ActionBar", "Player is null");
			return;
		}
		if (TheAPI.getServerVersion().equals("glowstone")) {
			try {
				((GlowPlayer) p).sendActionBar(TheAPI.colorize(text));
				return;
			} catch (Exception e) {
				Error.err("sending ActionBar to " + p.getName(), "Text is null");
			}
		}
		getNMSAPI().sendPacket(p,
				getNMSAPI().getPacketPlayOutTitle(TitleAction.ACTIONBAR, colorize(text), fadeIn, stay, fadeOut));
	}

	/**
	 * @see see Get int, double or calculate string
	 * @param string
	 * @return NumbersAPI
	 */
	@Deprecated
	public static NumbersAPI getNumbersAPI(String string) {
		return new NumbersAPI(string);
	}

	/**
	 * @see see Set player max health, air, teleport to location and more
	 * @param p
	 * @return PlayerAPI
	 */
	public static PlayerAPI getPlayerAPI(Player p) {
		return new PlayerAPI(p);
	}

	public static enum SudoType {
		CHAT, COMMAND
	}

	/**
	 * @see see Send value as player
	 * @param target
	 * @param type
	 * @param value
	 */
	public static void sudo(Player target, SudoType type, String value) {
		switch (type) {
		case CHAT:
			target.chat(value);
			break;
		case COMMAND:
			target.performCommand(value);
			break;
		}
	}

	/**
	 * @see see Send value as console
	 * @param type
	 * @param value
	 */
	public static void sudoConsole(SudoType type, String value) {
		switch (type) {
		case CHAT:
			Bukkit.dispatchCommand(getConsole(), "say " + value);
			break;
		case COMMAND:
			Bukkit.dispatchCommand(getConsole(), value);
			break;
		}
	}

	private static void giveItems(Player p, ItemStack item) {
		if (p == null) {
			Error.err("giving Item", "Player is null");
			return;
		}
		try {
			if (item == null)
				return;
			if (p.getInventory().firstEmpty() == -1) {
				p.getWorld().dropItem(p.getLocation(), item);
			} else {
				p.getInventory().addItem(item);
			}
		} catch (Exception e) {
			Error.err("giving Item to player " + p.getName(), "Item is null");
			return;
		}
	}

	/**
	 * @see see If player have full inventory, item will be dropped on ground or
	 *      item will be added to player inventory
	 * @param p
	 * @param item
	 */
	public static void giveItem(Player p, List<ItemStack> item) {
		for (ItemStack i : item)
			giveItems(p, i);
	}

	/**
	 * @see see If player have full inventory, item will be dropped on ground or
	 *      item will be added to player inventory
	 * @param p
	 * @param item
	 */
	public static void giveItem(Player p, ItemStack... item) {
		for (ItemStack i : item)
			giveItems(p, i);
	}

	/**
	 * @see see If player have full inventory, item will be dropped on ground or
	 *      item will be added to player inventory
	 * @param p
	 * @param item
	 * @param amount
	 */
	public static void giveItem(Player p, Material item, int amount) {
		giveItems(p, new ItemStack(item, amount));
	}

	/**
	 * @see see Send player title
	 * @param p
	 * @param firstLine
	 * @param nextLine
	 */
	public static void sendTitle(Player p, String firstLine, String nextLine) {
		if (p == null) {
			Error.err("sending Title", "Player is null");
			return;
		}
		getPlayerAPI(p).sendTitle(firstLine, nextLine);
	}
	
	/**
	 * @see see Send message to all online players with interval
	 * @param message
	 * @param time
	 */
	public static void slowBroadcast(List<String> messages, long interval) {
		SlowLoop<String> t = new SlowLoop<String>() {
			@Override
			public void toRun(String t) {
				broadcastMessage(t);
			}
		};
		t.addToLoop(messages);
		t.start(interval);
	}

	/**
	 * @see see Send message to all online players with interval
	 * @param message
	 * @param time
	 */
	public static void slowBroadcast(Collection<String> messages, long interval) {
		SlowLoop<String> t = new SlowLoop<String>() {
			@Override
			public void toRun(String t) {
				broadcastMessage(t);
			}
		};
		t.addToLoop(messages);
		t.start(interval);
	}

	/**
	 * @see see Send message to all online players
	 * @param message
	 */
	public static void broadcastMessage(String message) {
		for (Player p : TheAPI.getOnlinePlayers()) {
			for (String s : message.replace("\\n", "\n").split("\n")) {
				p.sendMessage(colorize(s));
			}
		}
		for (String s : message.replace("\\n", "\n").split("\n")) {
			getConsole().sendMessage(colorize(s));
		}
	}

	/**
	 * @see see Send message to all online players
	 * @param message
	 */
	public static void broadcastMessage(Object object) {
		broadcastMessage(String.valueOf(object));
	}

	/**
	 * @see see Send message to all online players with specified permission
	 * @param message
	 * @param permission
	 */
	public static void broadcast(Object object, String permission) {
		broadcast(String.valueOf(object), permission);
	}

	/**
	 * @see see Send message to all online players with specified permission
	 * @param message
	 * @param permission
	 */
	public static void broadcast(String message, String permission) {
		for (Player p : TheAPI.getOnlinePlayers()) {
			if (p.hasPermission(permission))
				for (String s : message.replace("\\n", "\n").split("\n")) {
					msg(s, p);
				}
		}
		ChatColor old = ChatColor.RESET;
		for (String s : message.replace("\\n", "\n").split("\n")) {
			getConsole().sendMessage(old + colorize(s));
			old = getStringUtils().getColor(s);
		}
	}

	/**
	 * @see see Ban, Ban-Ip or mute player with reason and more
	 * @return PunishmentAPI
	 */
	public static PunishmentAPI getPunishmentAPI() {
		return new PunishmentAPI();
	}

	/**
	 * @see see Constructor for own report system
	 * @return ReportSystem
	 */
	public static ReportSystem getReportSystem() {
		return new ReportSystem();
	}

	/**
	 * @see see Set max players on server
	 * @param int
	 */
	public static void setMaxPlayers(int max) {
		LoaderClass.plugin.max = max;
	}

	/**
	 * @see see Set online players on server
	 * @param int
	 */
	public static void setFakeOnlinePlayers(int online) {
		LoaderClass.plugin.fakeOnline = online;
	}

	/**
	 * @see see Return fake amount of online players on server
	 * @return int
	 */
	public static int getFakeOnlinePlayers() {
		return LoaderClass.plugin.fakeOnline;
	}

	/**
	 * @see see Return server motd
	 * @return String
	 */
	public static String getMotd() {
		return LoaderClass.plugin.motd;
	}

	/**
	 * @see see Set new server motd
	 * @param neww Motd text
	 */
	public static void setMotd(String neww) {
		LoaderClass.plugin.motd=neww;
	}

	/**
	 * @see see Return server list players text
	 * @return String
	 */
	public static List<String> getServerListPlayers() {
		return LoaderClass.plugin.onlineText;
	}

	/**
	 * @see see Set new server list players text
	 * @param neww List<String>
	 */
	public static void setServerListPlayers(List<String> neww) {
		LoaderClass.plugin.onlineText=neww;
	}

	/**
	 * @see see Get max players on server
	 * @return int
	 */
	public static int getMaxPlayers() {
		return LoaderClass.plugin.max;
	}

	/**
	 * @see see Hide or show player to players on server
	 * @param p
	 * @param permission To see player
	 * @param vanish
	 */
	public static void vanish(Player p, String permission, boolean vanish) {
		v(p, vanish, permission);
	}

	private static boolean isV(Player player) {
		if (player.hasMetadata("vanished"))
			for (MetadataValue meta : player.getMetadata("vanished")) {
				if (meta.asBoolean())
					return true;
			}
		return false;
	}

	/**
	 * @see see Return is player in vanish mode
	 * @param p
	 * @return boolean
	 */
	public static boolean isVanished(Player p) {
		if (isV(p))
			return true;
		return getUser(p).getString("vanish") != null;
	}

	/**
	 * @see see Return is player in vanish mode
	 * @param p
	 * @return boolean
	 */
	public static boolean isVanished(String p) {
		return getUser(p).getString("vanish") != null;
	}

	/**
	 * @see see Hide or show player to players on server
	 * @param p
	 * @param vanish
	 */
	public static void vanish(Player p, boolean vanish) {
		v(p, vanish, null);
	}

	/**
	 * @see see Hide or show player to players on server
	 * @param p
	 * @param vanish
	 */
	public static void vanish(String p, boolean vanish) {
		vanish(p, vanish, null);
	}

	/**
	 * @see see Hide or show player to players on server
	 * @param p
	 * @param vanish
	 */
	public static void vanish(String p, boolean vanish, String perm) {
		if (vanish) {
			if (perm != null)
				getUser(p).setAndSave("vanish", perm);
			List<String> v = LoaderClass.data.getConfig().getStringList("vanished");
			v.add(p);
			LoaderClass.data.getConfig().set("vanished", v);
		} else {
			getUser(p).setAndSave("vanish", null);
			List<String> v = LoaderClass.data.getConfig().getStringList("vanished");
			v.remove(p);
			LoaderClass.data.getConfig().set("vanished", v);
		}
		LoaderClass.data.save();
	}

	private static boolean has(Player s, Player d) {
		if (getUser(d).getString("data." + d.getName() + ".vanish") != null)
			return s.hasPermission(getUser(d).getString("data." + d.getName() + ".vanish"));
		else
			return false;
	}

	@SuppressWarnings("deprecation")
	private static void hide(Player p) {
		if (isVanished(p)) {
			for (Player s : getOnlinePlayers()) {
				if (s != p && !has(s, p))
					s.hidePlayer(p);
			}
		} else {
			for (Player s : getOnlinePlayers()) {
				s.showPlayer(p);
			}
		}
	}

	private static void v(Player p, boolean vanish, String perm) {

		PlayerVanishEvent d = new PlayerVanishEvent(p, (perm == null ? null : perm), vanish,
				(perm == null ? false : true));
		Bukkit.getPluginManager().callEvent(d);
		if (!d.isCancelled()) {
			vanish = d.vanish();
			perm = d.getPermission();
			if (vanish) {
				if (perm != null)
					getUser(p).setAndSave("vanish", perm);
				List<String> v = LoaderClass.data.getConfig().getStringList("vanished");
				v.add(p.getName());
				LoaderClass.data.getConfig().set("vanished", v);
			} else {
				getUser(p).setAndSave("vanish", null);
				List<String> v = LoaderClass.data.getConfig().getStringList("vanished");
				v.remove(p.getName());
				LoaderClass.data.getConfig().set("vanished", v);
			}
			LoaderClass.data.save();
			hide(p);
		}
	}

	/**
	 * @see see Return console
	 * @return CommandSender
	 */
	public static CommandSender getConsole() {
		return Bukkit.getConsoleSender();
	}

	/**
	 * @see see Create, delete, unload or load world
	 * @return WorldsManager
	 */
	public static WorldsManager getWorldsManager() {
		return new WorldsManager();
	}

	/**
	 * @see see With VaultAPI deposit, withdraw from player money and more
	 * @return EconomyAPI
	 */
	public static EconomyAPI getEconomyAPI() {
		return new EconomyAPI();
	}

	/**
	 * @see see Set player Header and Footer in tablist
	 * @return TabListAPI
	 */
	public static TabListAPI getTabListAPI() {
		return new TabListAPI();
	}

	/**
	 * @see see Send formated message to all online players with specified
	 *      permission
	 * @param s
	 * @param message
	 */
	public static void sendHelpOp(CommandSender s, String message) {
		for (String ss : message.replace("\\n", "\n").split("\n")) {
			broadcast(
					LoaderClass.config.getConfig().getString("Format.HelpOp").replace("%message%", ss)
							.replace("%sender%", s.getName()),
					LoaderClass.config.getConfig().getString("Format.HelpOp-Permission"));
		}
		if (!s.hasPermission(LoaderClass.config.getConfig().getString("Format.HelpOp-Permission")))
			for (String ss : message.replace("\\n", "\n").split("\n")) {
				s.sendMessage(colorize(LoaderClass.config.getConfig().getString("Format.HelpOp")
						.replace("%message%", ss).replace("%sender%", s.getName())));
			}
	}

	/**
	 * @see see Set player name tag
	 * @param p
	 * @param prefix
	 * @param suffix
	 * @return NameTagAPI
	 */
	public static NameTagAPI getNameTagAPI(Player p, String prefix, String suffix) {
		return new NameTagAPI(p, prefix, suffix);
	}

	/**
	 * @see see Manager of player's cooldowns
	 * @param cooldown
	 * @return CooldownAPI
	 */
	public static CooldownAPI getCooldownAPI(String player) {
		return getCooldownAPI(getUser(player));
	}

	/**
	 * @see see Manager of player's cooldowns
	 * @param cooldown
	 * @return CooldownAPI
	 */
	public static CooldownAPI getCooldownAPI(UUID player) {
		return getCooldownAPI(getUser(player));
	}

	/**
	 * @see see Manager of player's cooldowns
	 * @param cooldown
	 * @return CooldownAPI
	 */
	public static CooldownAPI getCooldownAPI(Player player) {
		return getCooldownAPI(getUser(player));
	}

	/**
	 * @see see Manager of player's cooldowns
	 * @param cooldown
	 * @return CooldownAPI
	 */
	public static CooldownAPI getCooldownAPI(User player) {
		return new CooldownAPI(player);
	}

	/**
	 * @see see Get used memory, free memory and max memory
	 * @return MemoryAPI
	 */
	public static MemoryAPI getMemoryAPI() {
		return new MemoryAPI();
	}

	/**
	 * @see see Load, unload, enable or disable plugins
	 * @return PluginManagerAPI
	 */
	public static PluginManagerAPI getPluginsManagerAPI() {
		return new PluginManagerAPI();
	}

	/**
	 * @see see Get bukkit name of enchantment from string for ex. Sharpness ->
	 *      DAMAGE_ALL
	 * @return Enchantment
	 */
	public static EnchantmentAPI getEnchantmentAPI(String enchantment) {
		return EnchantmentAPI.byName(enchantment);
	}

	/**
	 * @see see Send player scoreboard (Fuctions: Per player scoreboard, Packets)
	 * @param p Player
	 * @param usePackets If this is set to true, scoreboard will use packets, not bukkit methods
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p, boolean usePackets) {
		return new ScoreboardAPI(p, usePackets, false);
	}

	/**
	 * @see see Send player scoreboard (Fuctions: Per player scoreboard, Teams -> Non-Flashing)
	 * @param p Player
	 * @param usePackets If this is set to true, scoreboard will use packets, not bukkit methods
	 * @param useTeams If this is set to true & usePackets is set to false, scoreboard will use teams (Bukkit methods)
	 * Or if useTeams & usePackets are set to false, scoreboard will use OfflinePlayers (Bukkit methods, not teams)
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p, boolean usePackets, boolean useTeams) {
		return new ScoreboardAPI(p,usePackets,useTeams);
	}

	/**
	 * @see see Send player scoreboard (Fuctions: Per player scoreboard, Teams -> Non-Flashing)
	 * @param p Player
	 * @param type ScoreboardType
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p, ScoreboardType type) {
		return new ScoreboardAPI(p,type);
	}

	/**
	 * @see see Send player scoreboard (Fuctions: Per player scoreboard)
	 * @param p
	 * @param board
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p) {
		return new ScoreboardAPI(p,false,false);
	}

	/**
	 * @see see Send player sound or get sound name from String
	 * @return SoundAPI
	 */
	public static SoundAPI getSoundAPI() {
		return new SoundAPI();
	}

	/**
	 * @see see Convert long to String time or String time to long
	 * @return TimeConventorAPI
	 */
	@Deprecated
	public static TimeConventorAPI getTimeConventorAPI() {
		return new TimeConventorAPI();
	}

	/**
	 * @see see Create ItemStack with custom lore, model, displayname and more
	 * @param material
	 * @return ItemCreatorAPI
	 */
	public static ItemCreatorAPI getItemCreatorAPI(Material material) {
		return new ItemCreatorAPI(new ItemStack(material));
	}

	/**
	 * @see see Create ItemStack with custom lore, model, displayname and more
	 * @param itemstack
	 * @return ItemCreatorAPI
	 */
	public static ItemCreatorAPI getItemCreatorAPI(ItemStack itemstack) {
		return new ItemCreatorAPI(itemstack);
	}

	/**
	 * @see see Return server version, for ex. v1_14_R1
	 * @return String
	 */
	public static String getServerVersion() {
		String version = null;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} catch (Exception e) {
			try {
				version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[1];
			} catch (Exception ss) {
			}
		}
		return version;
	}

	/**
	 * @see see Return current server TPS
	 * @return double
	 */
	public static double getServerTPS() {
		return getServerTPS(TPSType.ONE_MINUTE);
	}

	public static enum TPSType {
		ONE_MINUTE, FIVE_MINUTES, FIFTEEN_MINUTES
	}

	/**
	 * @see see Return server TPS from 1, 5 or 15 minutes
	 * @return double
	 */
	public static double getServerTPS(TPSType type) {
		try {
			double tps = getNMSAPI().getServerTPS()[type == TPSType.ONE_MINUTE ? 0
					: type == TPSType.FIVE_MINUTES ? 1 : 2];
			if (tps > 20)
				tps = 20;
			return getStringUtils().getDouble(String.format("%2.02f", tps));
		} catch (Exception e) {
			return 20.0;
		}
	}

	/**
	 * @see see Return player ping
	 * @param p
	 * @return int
	 */
	public static int getPlayerPing(Player p) {
		if (getServerVersion().equals("glowstone")) {
			try {
				return ((GlowPlayer) p).getUserListEntry().getPing();
			} catch (Exception e) {
				return -1;
			}
		}
		try {
			return getNMSAPI().getNMSPlayerAPI(p).getPing();
		} catch (Exception e) {
			return -1;
		}
	}

	public static void showEntity(Player to, UUID uuid) {
		if (LoaderClass.data.getConfig().getString("hiden." + uuid.toString()) == null)
			return; // not hiden or isn't in config
		LoaderClass.data.getConfig().set("hiden." + uuid.toString(), null);
		try {
			Entity entity = Bukkit.getEntity(uuid);
			if (entity == null)
				return;
			if (entity instanceof LivingEntity)
				getNMSAPI().sendPacket(to, getNMSAPI()
						.getPacketPlayOutSpawnEntityLiving(getNMSAPI().getEntityLiving((LivingEntity) entity)));
			else
				getNMSAPI().sendPacket(to, getNMSAPI().getPacketPlayOutSpawnEntity(getNMSAPI().getEntity(entity), 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showEntity(Player to, LivingEntity entity) {
		if (LoaderClass.data.getConfig().getString("hiden." + entity.getUniqueId().toString()) != null) {
			LoaderClass.data.getConfig().set("hiden." + entity.getUniqueId().toString(), null);
		}
		try {
			getNMSAPI().sendPacket(to,
					getNMSAPI().getPacketPlayOutSpawnEntityLiving(getNMSAPI().getEntityLiving(entity)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showEntity(Player to, Entity entity) {
		if (LoaderClass.data.getConfig().getString("hiden." + entity.getUniqueId().toString()) != null) {
			LoaderClass.data.getConfig().set("hiden." + entity.getUniqueId().toString(), null);
		}
		try {
			if (entity instanceof LivingEntity)
				getNMSAPI().sendPacket(to, getNMSAPI()
						.getPacketPlayOutSpawnEntityLiving(getNMSAPI().getEntityLiving((LivingEntity) entity)));
			else
				getNMSAPI().sendPacket(to, getNMSAPI().getPacketPlayOutSpawnEntity(getNMSAPI().getEntity(entity), 0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void hideEntity(Player from, UUID uuid) {
		try {
			Entity entity = Bukkit.getEntity(uuid);
			if (entity == null)
				return; // not exists
			getNMSAPI().sendPacket(from, getNMSAPI().getPacketPlayOutEntityDestroy(entity.getEntityId()));
			LoaderClass.data.getConfig().set("hiden." + uuid.toString(), true);
		} catch (Exception e) {
		}
	}

	public static void hideEntity(Player from, LivingEntity entity) {
		try {
			getNMSAPI().sendPacket(from, getNMSAPI().getPacketPlayOutEntityDestroy(entity.getEntityId()));
			LoaderClass.data.getConfig().set("hiden." + entity.getUniqueId().toString(), true);
		} catch (Exception e) {
		}
	}

	public static void hideEntity(Player from, Entity entity) {
		try {
			getNMSAPI().sendPacket(from, getNMSAPI().getPacketPlayOutEntityDestroy(entity.getEntityId()));
			LoaderClass.data.getConfig().set("hiden." + entity.getUniqueId().toString(), true);
		} catch (Exception e) {
		}
	}

	/**
	 * @see see Return boolean exists User's file
	 * @return boolean
	 */
	@SuppressWarnings("deprecation")
	public static boolean existsUser(String name) {
		String s = null;
		try {
			s = UUID.fromString(name).toString();
		} catch (Exception e) {
			s = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
		}
		return new File("plugins/TheAPI/User/" + s + ".yml").exists();
	}

	/**
	 * @see see Return boolean exists User's file
	 * @return boolean
	 */
	public static boolean existsUser(UUID uuid) {
		return new File("plugins/TheAPI/User/" + uuid.toString() + ".yml").exists();
	}

	/**
	 * @see see Return boolean exists User's file
	 * @return boolean
	 */
	public static boolean existsUser(Player player) {
		return new File("plugins/TheAPI/User/" + player.getUniqueId().toString() + ".yml").exists();
	}

	/**
	 * @see see Return List of users (For ex. e74d98f0-a807-3b3d-b53f-93ea27908936)
	 * @return List<UUID>
	 */
	public static List<UUID> getUsers() {
		List<UUID> a = Lists.newArrayList();
		for (File f : new File("plugins/TheAPI/User").listFiles()) {
			try {
				a.add(UUID.fromString(f.getName().replaceFirst(".yml", "")));
			} catch (Exception e) {
				// hide error.
			}
		}
		return a;
	}

	/**
	 * @see see Return List of users names (For ex. Straikerina)
	 * @return List<String>
	 */
	public static List<String> getUsersNames() {
		List<String> a = Lists.newArrayList();
		for (File f : new File("plugins/TheAPI/User").listFiles()) {
			try {
				a.add(Bukkit.getOfflinePlayer(UUID.fromString(f.getName().replaceFirst(".yml", ""))).getName());
			} catch (Exception e) {
				// hide error.
			}
		}
		return a;
	}

	/**
	 * @see see If the user doesn't exist, his data file is created automatically.
	 * @param nameOrUUID Name of player or UUID in String
	 * @return User
	 */
	@SuppressWarnings("deprecation")
	public static User getUser(String nameOrUUID) {
		if(nameOrUUID==null)return null;
		UUID s = null;
		try {
			s = UUID.fromString(nameOrUUID);
		} catch (Exception e) {
			s = Bukkit.getOfflinePlayer(nameOrUUID).getUniqueId();
		}
		return getUser(s);
	}

	/**
	 * @see see If the user doesn't exist, his data file is created automatically.
	 * @param player Player
	 * @return User
	 */
	public static User getUser(Player player) {
		if(player==null)return null;
		return getUser(player.getUniqueId());
	}

	/**
	 * @see see If the user doesn't exist, his data file is created automatically.
	 * @param uuid UUID of player
	 * @return User
	 */
	public static User getUser(UUID uuid) {
		if(uuid==null)return null;
		User c = cache.containsKey(uuid) ? cache.get(uuid) : null;
		if(c==null) {
			c=new User(uuid);
			cache.put(uuid, c);
		}
		return c;
	}
}
