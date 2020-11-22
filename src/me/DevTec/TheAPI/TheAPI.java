package me.DevTec.TheAPI;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import me.DevTec.TheAPI.APIs.EnchantmentAPI;
import me.DevTec.TheAPI.APIs.EntityCreatorAPI;
import me.DevTec.TheAPI.APIs.NameTagAPI;
import me.DevTec.TheAPI.BossBar.BarColor;
import me.DevTec.TheAPI.BossBar.BarStyle;
import me.DevTec.TheAPI.BossBar.BossBar;
import me.DevTec.TheAPI.ConfigAPI.ConfigAPI;
import me.DevTec.TheAPI.CooldownAPI.CooldownAPI;
import me.DevTec.TheAPI.SQLAPI.SQLAPI;
import me.DevTec.TheAPI.Scheduler.Scheduler;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.ScoreboardAPI.ScoreboardAPI;
import me.DevTec.TheAPI.ScoreboardAPI.ScoreboardType;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.Data;
import me.DevTec.TheAPI.Utils.DataKeeper.Storage;
import me.DevTec.TheAPI.Utils.DataKeeper.User;
import me.DevTec.TheAPI.Utils.Listener.Event;
import me.DevTec.TheAPI.Utils.Listener.HandlerList;
import me.DevTec.TheAPI.Utils.Listener.Listener;
import me.DevTec.TheAPI.Utils.Listener.Events.PlayerVanishEvent;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI.ChatType;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI.TitleAction;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;
import me.DevTec.TheAPI.WorldsAPI.WorldBorderAPI;

public class TheAPI {
	private static final HashMap<String, BossBar> bars = new HashMap<>();
	private static final HashMap<String, Integer> task = new HashMap<>();
	private static final HashMap<UUID, User> cache = new HashMap<>();
	private static Constructor<?> constructor = Ref.constructor(PluginCommand.class, String.class, Plugin.class);
	private static Method m = Ref.method(Bukkit.class, "getOnlinePlayers");
	private static Random random = new Random();
	private static int ver;
	public static void register(Listener listener) {
		HandlerList.register(listener);
	}

	public static void unregister(Listener listener) {
		HandlerList.unregister(listener);
	}

	public static void callEvent(Event e) {
		HandlerList.callEvent(e);
	}

	public static void createAndRegisterCommand(String commandName, String permission, CommandExecutor commandExecutor,
			String... aliases) {
		createAndRegisterCommand(commandName, permission, commandExecutor,
				aliases != null ? Arrays.asList(aliases) : null);
	}

	public static void createAndRegisterCommand(String commandName, String permission, CommandExecutor commandExecutor,
			List<String> aliases) {
		PluginCommand cmd = TheAPI.createCommand(commandName.toLowerCase(), LoaderClass.plugin);
		if (permission != null)
			Ref.set(cmd, "permission", permission.toLowerCase());
		List<String> lowerCase = new ArrayList<>();
		if (aliases != null)
			for (String s : aliases)
				lowerCase.add(s.toLowerCase());
		cmd.setAliases(lowerCase);
		Ref.set(cmd, "executor", commandExecutor);
		registerCommand(cmd);
	}

	public static PluginCommand createCommand(String name, Plugin plugin) {
		return (PluginCommand) Ref.newInstance(constructor, name, plugin);
	}

	private static Object cmdMap = Ref.get(Bukkit.getPluginManager(), "commandMap");
	@SuppressWarnings("unchecked")
	private static HashMap<String, Command> knownCommands = (HashMap<String, Command>) Ref.get(cmdMap, "knownCommands");
	public static void registerCommand(PluginCommand command) {
		String label = command.getName().toLowerCase(Locale.ENGLISH).trim();
		String sd = command.getPlugin().getName().toLowerCase(Locale.ENGLISH).trim();
		command.setLabel(sd+":"+label);
		if(command.getTabCompleter()==null) {
			if(command.getExecutor() instanceof TabCompleter) {
				command.setTabCompleter((TabCompleter)command.getExecutor());
			}else
				command.setTabCompleter(new TabCompleter() {
					public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
						return null;
					}
				});
		}
		if(command.getExecutor()==null) {
			if(command.getTabCompleter() instanceof CommandExecutor) {
				command.setExecutor((CommandExecutor)command.getTabCompleter());
			}else return; //exectutor can't be null
		}
		List<String> low = new ArrayList<>();
		for(String s : command.getAliases()) {
			s=s.toLowerCase(Locale.ENGLISH).trim();
			low.add(s);
		}
		if(!low.contains(label))low.add(label);
	    for(String s : low)
	    	knownCommands.put(s, command);
	    command.register((CommandMap) cmdMap);
	}
	
	public static boolean unregisterCommand(PluginCommand command) {
		return unregisterCommand(command.getName());
	}
	
	public static boolean unregisterCommand(String name) {
		boolean is = false;
	    for(Entry<String, Command> e : new HashMap<>(knownCommands).entrySet())
	    	if(e.getValue().getName().equals("name")) {
	    		knownCommands.remove(e.getKey());
	    		is=true;
	    	}
		return is;
	}

	public static void clearCache() {
		cache.clear();
	}

	public static boolean generateChance(double chance) {
		return generateRandomDouble(100) <= chance;
	}

	public static double getProcessCpuLoad() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
			AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });
			if (list.isEmpty())
				return 0.0;
			Attribute att = (Attribute) list.get(0);
			Double value = (Double) att.getValue();
			if (value == -1.0)
				return 0;
			return ((value * 1000.0) / 10.0);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * @see see Colorize string with colors
	 * @param string
	 * @return Colored String
	 */
	public static String colorize(String string) {
		return StringUtils.colorize(string);
	}

	/**
	 * @see see Create Inventory that can be used as Storage for Items or Sorter
	 * @return Storage
	 */
	public static Storage getStorage() {
		return new Storage();
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
		return isNewerThan(12);
	}

	public static boolean isOlderThan(int version) {
		if (ver == 0) {
			ver = StringUtils.getInt(getServerVersion().split("_")[1]);
		}
		return ver < version;
	}

	public static boolean isNewerThan(int version) {
		if (ver == 0) {
			ver = StringUtils.getInt(getServerVersion().split("_")[1]);
		}
		return ver > version;
	}

	/**
	 * @see see Return is server version older than 1.9 ? (1.0 - 1.8.9)
	 * @return boolean
	 */
	public static boolean isOlder1_9() {
		return isOlderThan(9);
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
		return StringUtils.buildString(args);
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
	public static <T> T getRandomFromList(List<T> list) {
		return StringUtils.getRandomFromList(list);
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
		return generateRandomInt(0, maxInt);
	}

	/**
	 * @see see Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static double generateRandomDouble(double maxDouble) {
		return generateRandomDouble(0, maxDouble);
	}

	/**
	 * @see see Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static double generateRandomDouble(double min, double maxDouble) {
		boolean a = maxDouble < 0;
		if (a)
			maxDouble = -1 * maxDouble;
		if ((min < 0 ? min * -1 : min) >= maxDouble)
			return min;
		if (maxDouble == 0)
			return maxDouble;
		double i = random.nextInt((int) maxDouble) + random.nextDouble();
		if (i < min)
			i = min;
		if (i > maxDouble)
			i = maxDouble;
		return a ? -1 * i : i;
	}

	/**
	 * @see see Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static int generateRandomInt(int min, int maxInt) {
		boolean a = maxInt < 0;
		if (a)
			maxInt = -1 * maxInt;
		if ((min < 0 ? min * -1 : min) >= maxInt)
			return min;
		if (maxInt == 0)
			return maxInt;
		int i = random.nextInt(maxInt);
		if (i < min)
			i = min;
		if (i > maxInt)
			i = maxInt;
		return a ? -1 * i : i;
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
		try {
			return getOnlinePlayers().get(i);
		}catch(Exception er) {
			return null;
		}
	}

	/**
	 * @see see Get player by name
	 * @return Player
	 */
	public static Player getPlayer(String name) {
		return Bukkit.getPlayer(name);
	}

	/**
	 * @see see Get player by name (Not matching)
	 * @return Player
	 */
	public static Player getPlayerOrNull(String name) {
		Player found = Bukkit.getPlayer(name);
		return found != null && found.getName().equals(name) ? found : null;
	}

	/**
	 * @see see Get random player from List<Player>
	 * @return Player
	 */
	public static Player getRandomPlayer() {
		return getRandomFromList(getOnlinePlayers());
	}

	/**
	 * @see see Get random player from List<Player>
	 * @return List<Player>
	 */
	@SuppressWarnings("unchecked")
	public static List<Player> getOnlinePlayers() {
		Object o = Ref.invokeNulled(m);
		return new ArrayList<>(o instanceof Collection ? (Collection<Player>) o : Arrays.asList((Player[]) o));
	}

	/**
	 * @see see Get random player from List<Player>
	 * @return List<Player>
	 */
	public static List<Player> getPlayers() {
		return getOnlinePlayers();
	}

	public static void sendMessage(String message, CommandSender sender) {
		Validator.validate(sender == null, "CommandSender is null");
		Validator.validate(message == null, "Message is null");
		ChatColor old = ChatColor.RESET;
		for (String s : message.replace("\\n", "\n").split("\n")) {
			sender.sendMessage(old + colorize(s));
			old = StringUtils.getColor(s);
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
		Validator.validate(p == null, "Player is null");
		Validator.validate(text == null, "Text is null");
		Validator.validate(progress < 0, "Progress is lower than zero");
		if (task.containsKey(p.getName())) {
			Scheduler.cancelTask(task.get(p.getName()));
			task.remove(p.getName());
		}
		BossBar a = bars.containsKey(p.getName()) ? bars.get(p.getName())
				: new BossBar(p, TheAPI.colorize(text), progress, BarColor.GREEN, BarStyle.PROGRESS);
		if (progress < 0)
			progress = 0;
		if (progress > 1)
			progress = 1;
		a.setProgress(progress);
		a.setTitle(colorize(text));
		if (a.isHidden())
			a.show();
		if (!bars.containsKey(p.getName()))
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
		Validator.validate(p == null, "Player is null");
		Validator.validate(text == null, "Text is null");
		Validator.validate(progress < 0, "Progress is lower than zero");
		Validator.validate(timeToExpire < 0, "Time to expire is lower than zero");
		BossBar a = sendBossBar(p, text, progress);
		task.put(p.getName(), new Tasker() {
			@Override
			public void run() {
				a.hide();
			}
		}.runLater(timeToExpire));
	}

	/**
	 * @see see Remove player from all bossbars in which player is in
	 * @param p
	 */
	public static void removeBossBar(Player p) {
		Validator.validate(p == null, "Player is null");
		if (task.containsKey(p.getName())) {
			Scheduler.cancelTask(task.get(p.getName()));
			task.remove(p.getName());
		}
		if (bars.containsKey(p.getName()))
			bars.get(p.getName()).remove();
	}

	/**
	 * @see see Return list with bossbars in which player is in
	 * @param p
	 * @return BossBar
	 */
	public static BossBar getBossBar(Player p) {
		Validator.validate(p == null, "Player is null");
		return bars.containsKey(p.getName()) ? bars.get(p.getName()) : null;

	}

	/**
	 * @see see Remove player action bar instanceof sendActionBar(player, "")
	 * @param p
	 */
	public static void removeActionBar(Player p) {
		Validator.validate(p == null, "Player is null");
		sendActionBar(p, "");
	}

	/**
	 * @see see Send player action bar
	 * @param p
	 * @param text
	 */
	public static void sendActionBar(Player p, String text) {
		sendActionBar(p, text, 10, 20, 10);
	}

	/**
	 * @see see Send player action bar
	 * @param p
	 * @param text
	 */
	public static void sendActionBar(Player p, String text, int fadeIn, int stay, int fadeOut) {
		Validator.validate(p == null, "Player is null");
		Validator.validate(text == null, "Text is null");
		Validator.validate(fadeIn < 0, "FadeIn time is lower than zero");
		Validator.validate(stay < 0, "Stay time is lower than zero");
		Validator.validate(fadeOut < 0, "FadeOut time is lower than zero");
		Object packet = NMSAPI.getPacketPlayOutTitle(TitleAction.ACTIONBAR, colorize(text), fadeIn, stay, fadeOut);
		if (packet == null)
			packet = NMSAPI.getPacketPlayOutChat(ChatType.CHAT, colorize(text));
		NMSAPI.sendPacket(p, packet);
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

	/**
	 * @see see Send command as console
	 * @param type
	 * @param value
	 */
	public static void sudoConsole(String value) {
		Bukkit.dispatchCommand(getConsole(), value);
	}

	private static void giveItems(Player p, ItemStack item) {
		Validator.validate(p == null, "Player is null");
		Validator.validate(item == null, "ItemStack is null");
		if (p.getInventory().firstEmpty() == -1) {
			p.getWorld().dropItem(p.getLocation(), item);
		} else {
			p.getInventory().addItem(item);
		}
	}

	/**
	 * @see see If player have full inventory, item will be dropped on ground or
	 *      item will be added to player inventory
	 * @param p
	 * @param item
	 */
	public static void giveItem(Player p, List<ItemStack> item) {
		Validator.validate(item == null, "List of ItemStacks is null");
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
		Validator.validate(item == null, "ItemStacks are null");
		for (ItemStack i : item)
			if(i!=null)
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
		Validator.validate(p == null, "Player is null");
		Validator.validate(item == null, "Material is null");
		Validator.validate(amount <= 0, "Amount of items is lower than one");
		giveItems(p, new ItemStack(item, amount));
	}

	/**
	 * @see see Send player title
	 * @param p
	 * @param firstLine
	 * @param nextLine
	 */
	public static void sendTitle(Player p, String firstLine, String nextLine) {
		Validator.validate(p == null, "Player is null");
		Validator.validate(firstLine == null, "FirstLine is null");
		Validator.validate(nextLine == null, "NextLine is null");
		NMSAPI.sendPacket(p,
				NMSAPI.getPacketPlayOutTitle(TitleAction.TITLE, Ref.IChatBaseComponent(TheAPI.colorize(firstLine))));
		NMSAPI.sendPacket(p,
				NMSAPI.getPacketPlayOutTitle(TitleAction.SUBTITLE, Ref.IChatBaseComponent(TheAPI.colorize(nextLine))));
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
	public static void bcMsg(String message) {
		broadcastMessage(message);
	}

	/**
	 * @see see Send message to all online players
	 * @param message
	 */
	public static void broadcastMessage(Object object) {
		broadcastMessage(object + "");
	}

	/**
	 * @see see Send message to all online players
	 * @param message
	 */
	public static void bcMsg(Object object) {
		broadcastMessage(object + "");
	}

	/**
	 * @see see Send message to all online players with specified permission
	 * @param message
	 * @param permission
	 */
	public static void broadcast(Object object, String permission) {
		broadcast(object + "", permission);
	}

	/**
	 * @see see Send message to all online players with specified permission
	 * @param message
	 * @param permission
	 */
	public static void bc(Object object, String permission) {
		broadcast(object + "", permission);
	}

	/**
	 * @see see Send message to all online players with specified permission
	 * @param message
	 * @param permission
	 */
	public static void bc(String message, String permission) {
		broadcast(message, permission);
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
			old = StringUtils.getColor(s);
		}
	}

	/**
	 * @see see Set max players on server
	 * @param int
	 */
	public static void setMaxPlayers(int max) {
		LoaderClass.plugin.max = max;
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
		LoaderClass.plugin.motd = TheAPI.colorize(neww);
	}

	/**
	 * @see see Get max players on server
	 * @return int
	 */
	public static int getMaxPlayers() {
		return LoaderClass.plugin.max;
	}
	
	public static void setVanish(String playerName, String permission, boolean value) {
		getUser(playerName).set("vanish", value);
		getUser(playerName).setAndSave("vanish.perm", permission);
		Player s = getPlayerOrNull(playerName);
		if(s!=null)applyVanish(s, permission, value);
	}
	
	private static void applyVanish(Player s, String perm, boolean var) {
		PlayerVanishEvent da = new PlayerVanishEvent(s, perm, var);
		callEvent(da);
		if (!da.isCancelled()) {
			var = da.vanish();
			perm = da.getPermission();
			if(var) {
				getUser(s).set("vanish", var);
				getUser(s).set("vanish.perm", perm);
				for(Player d : getOnlinePlayers())
					if(s!=d && !canSee(d, s.getName()) && d.canSee(s))
						d.hidePlayer(s);
				return;
			}
			getUser(s).remove("vanish");
			for(Player d : getOnlinePlayers())
				if(s!=d && canSee(d, s.getName()) && !d.canSee(s))
					d.showPlayer(s);
		}
	}

	private static boolean hasSuperVanish(Player player) {
		if (player.hasMetadata("vanished"))
			for (MetadataValue meta : player.getMetadata("vanished"))
				return meta.asBoolean();
		return false;
	}
	
	public static boolean hasVanish(String playerName) {
		Player s = getPlayerOrNull(playerName);
		return s!=null?(hasSuperVanish(s) || getUser(s).getBoolean("vanish")) : getUser(playerName).getBoolean("vanish");
	}
	
	public static String getVanishPermission(String playerName) {
		return getUser(playerName).getString("vanish.perm");
	}
	
	public static boolean canSee(Player player, String target) {
		return hasVanish(target) ? player.hasPermission(getVanishPermission(target)) : true;
	}
	
	public static boolean canSee(String player, String target) {
		return hasVanish(target);
	}

	/**
	 * @see see Return console
	 * @return CommandSender
	 */
	public static CommandSender getConsole() {
		return Bukkit.getConsoleSender();
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
	 * @see see Get bukkit name of enchantment from string for ex. Sharpness ->
	 *      DAMAGE_ALL
	 * @return Enchantment
	 */
	public static EnchantmentAPI getEnchantmentAPI(String enchantment) {
		return EnchantmentAPI.byName(enchantment);
	}

	/**
	 * @see see Send player scoreboard (Fuctions: Per player scoreboard, Packets)
	 * @param p          Player
	 * @param usePackets If this is set to true, scoreboard will use packets, not
	 *                   bukkit methods
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p, boolean usePackets) {
		return new ScoreboardAPI(p, usePackets, false);
	}

	/**
	 * @see see Send player scoreboard (Fuctions: Per player scoreboard, Teams ->
	 *      Non-Flashing)
	 * @param p          Player
	 * @param usePackets If this is set to true, scoreboard will use packets, not
	 *                   bukkit methods
	 * @param useTeams   If this is set to true & usePackets is set to false,
	 *                   scoreboard will use teams (Bukkit methods) Or if useTeams &
	 *                   usePackets are set to false, scoreboard will use
	 *                   OfflinePlayers (Bukkit methods, not teams)
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p, boolean usePackets, boolean useTeams) {
		return new ScoreboardAPI(p, usePackets, useTeams);
	}

	/**
	 * @see see Send player scoreboard (Fuctions: Per player scoreboard, Teams ->
	 *      Non-Flashing)
	 * @param p    Player
	 * @param type ScoreboardType
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p, ScoreboardType type) {
		return new ScoreboardAPI(p, type);
	}

	/**
	 * @see see Send player scoreboard (Fuctions: Per player scoreboard)
	 * @param p
	 * @param board
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p) {
		return new ScoreboardAPI(p, false, false);
	}

	private static String version;

	/**
	 * @see see Return server version, for ex. v1_14_R1
	 * @return String
	 */
	public static String getServerVersion() {
		if (version == null) {
			try {
				version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			} catch (Exception e) {
				try {
					version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[1];
				} catch (Exception ss) {
				}
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
			double tps = NMSAPI.getServerTPS()[type == TPSType.ONE_MINUTE ? 0 : type == TPSType.FIVE_MINUTES ? 1 : 2];
			if (tps > 20)
				tps = 20;
			return StringUtils.getDouble(String.format("%2.02f", tps));
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
		try {
			return (int)Ref.get(Ref.player(p), "ping");
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * @see see Return boolean exists User's file
	 * @return boolean
	 */
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
		List<UUID> a = new ArrayList<>();
		if (new File("plugins/TheAPI/User").exists())
			for (File f : new File("plugins/TheAPI/User").listFiles()) {
				try {
					a.add(UUID.fromString(f.getName().replaceFirst(".yml", "")));
				} catch (Exception e) {
					// hide error.
				}
			}
		return a;
	}

	public static Collection<User> getCachedUsers() {
		return cache.values();
	}

	/**
	 * @see see Return List of users names (For ex. Straikerina)
	 * @return List<String>
	 */
	public static List<String> getUsersNames() {
		List<String> a = new ArrayList<>();
		if (new File("plugins/TheAPI/User").exists())
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
	public static User getUser(String nameOrUUID) {
		if (nameOrUUID == null)
			return null;
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
		if (player == null)
			return null;
		return getUser(player.getUniqueId());
	}

	/**
	 * @see see If the user doesn't exist, his data file is created automatically.
	 * @param uuid UUID of player
	 * @return User
	 */
	public static User getUser(UUID uuid) {
		if (uuid == null)
			return null;
		if (LoaderClass.config.getBoolean("Options.Cache.User.Use")) {
			User c = cache.containsKey(uuid) ? cache.get(uuid) : null;
			if (c == null) {
				c = new User(uuid);
				cache.put(uuid, c);
			}
			return c;
		}
		return new User(uuid);
	}

	public static void removeCachedUser(UUID uuid) {
		if (uuid == null)
			return;
		cache.remove(uuid);
	}

	public static void removeCachedUser(Player player) {
		if (player == null)
			return;
		cache.remove(player.getUniqueId());
	}

	public static List<UUID> getUsersByIP(String ip) {
		if (ip.startsWith("/"))
			ip = ip.substring(1);
		List<UUID> a = new ArrayList<>();
		for (UUID s : getUsers()) {
			if (new Data("TheAPI/User/" + s + ".yml", true).getString("ip").equals(ip.replace(".", "_"))) {
				a.add(s);
			}
		}
		return a;
	}

	// Bit of LastLoginAPI
	public static User getLastLoggedUserByIP(String ip, boolean canBeOnline) {
		User a = null;
		long last = 0;
		List<UUID> ips = getUsersByIP(ip);
		if (canBeOnline) {
			for (Player s : TheAPI.getOnlinePlayers()) {
				if (ips.contains(s.getUniqueId())) {
					long quit = getUser(s).getLong("quit");
					if (quit <= last) {
						last = quit;
						a = getUser(s);
					}
				}
			}
			if (a != null)
				return a;
		}
		for (UUID s : ips) {
			long quit = new User(s).getLong("quit");
			if (quit <= last) {
				last = quit;
				a = getUser(s);
			}
		}
		return a;
	}

	public static void removeCachedUser(String nameOrUUID) {
		if (nameOrUUID == null)
			return;
		UUID s = null;
		try {
			s = UUID.fromString(nameOrUUID);
		} catch (Exception e) {
			s = Bukkit.getOfflinePlayer(nameOrUUID).getUniqueId();
		}
		cache.remove(s);
	}
}
