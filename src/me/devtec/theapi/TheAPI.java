package me.devtec.theapi;

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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.bukkit.Bukkit;
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
import org.bukkit.plugin.Plugin;
import org.spigotmc.SpigotConfig;

import me.devtec.theapi.apis.EnchantmentAPI;
import me.devtec.theapi.apis.EntityCreatorAPI;
import me.devtec.theapi.apis.NameTagAPI;
import me.devtec.theapi.bossbar.BarColor;
import me.devtec.theapi.bossbar.BarStyle;
import me.devtec.theapi.bossbar.BossBar;
import me.devtec.theapi.cooldownapi.CooldownAPI;
import me.devtec.theapi.punishmentapi.PunishmentAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.sockets.Client;
import me.devtec.theapi.sockets.Server;
import me.devtec.theapi.sqlapi.SQLAPI;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.listener.Event;
import me.devtec.theapi.utils.listener.HandlerList;
import me.devtec.theapi.utils.listener.Listener;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.nms.NMSAPI.TitleAction;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.Validator;
import me.devtec.theapi.worldsapi.WorldBorderAPI;

public class TheAPI {
	private static final HashMap<String, BossBar> bars = new HashMap<>();
	private static final HashMap<String, Integer> task = new HashMap<>();
	private static final HashMap<UUID, User> cache = new HashMap<>();
	private static Constructor<?> constructor = Ref.constructor(PluginCommand.class, String.class, Plugin.class);
	private static Method m = Ref.method(Bukkit.class, "getOnlinePlayers");
	private static Random random = new Random();
	private static int ver;
	
	public static Client getSocketClient(String server) {
		return LoaderClass.plugin.servers.get(server);
	}
	
	public static Server getSocketServer() {
		return LoaderClass.plugin.server;
	}
	
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
		cmd.setPermissionMessage("");
		List<String> lowerCase = new ArrayList<>();
		if (aliases != null)
			for (String s : aliases)
				lowerCase.add(s.toLowerCase());
		cmd.setAliases(lowerCase);
		cmd.setUsage("");
		Ref.set(cmd, "executor", commandExecutor);
		registerCommand(cmd);
	}
	
	public static void dispatchCommandAsync(CommandSender sender, String cmd) {
		if(!cmdMap.dispatch(sender, cmd)) {
			if(Ref.getClass("org.spigotmc.SpigotConfig")!=null) { //spigot+
				if (!SpigotConfig.unknownCommandMessage.isEmpty())
					sender.sendMessage(SpigotConfig.unknownCommandMessage);
			}else { //craftbukkit
				if (sender instanceof Player) {
					sender.sendMessage("Unknown command. Type \"/help\" for help.");
				} else {
					sender.sendMessage("Unknown command. Type \"help\" for help.");
				}
			}
		}
	}

	public static PluginCommand createCommand(String name, Plugin plugin) {
		return (PluginCommand) Ref.newInstance(constructor, name, plugin);
	}

	public static CommandMap cmdMap = (CommandMap)Ref.get(Bukkit.getPluginManager(), "commandMap");
	@SuppressWarnings("unchecked")
	public static Map<String, Command> knownCommands = (Map<String, Command>) Ref.get(cmdMap, "knownCommands");

	public static void registerCommand(PluginCommand command) {
		String label = command.getName().toLowerCase(Locale.ENGLISH).trim();
		String sd = command.getPlugin().getName().toLowerCase(Locale.ENGLISH).trim();
		command.setLabel(sd + ":" + label);
		command.register(cmdMap);
		if (command.getTabCompleter() == null) {
			if (command.getExecutor() instanceof TabCompleter) {
				command.setTabCompleter((TabCompleter) command.getExecutor());
			} else
				command.setTabCompleter(new TabCompleter() {
					public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
						return null;
					}
				});
		}
		if (command.getExecutor() == null) {
			if (command.getTabCompleter() instanceof CommandExecutor) {
				command.setExecutor((CommandExecutor) command.getTabCompleter());
			} else
				return; // exectutor can't be null
		}
		List<String> low = new ArrayList<>();
		for (String s : command.getAliases()) {
			s = s.toLowerCase(Locale.ENGLISH).trim();
			low.add(s);
		}
		command.setAliases(low);
		command.setPermission("");
		if (!low.contains(label))
			low.add(label);
		for (String s : low)
			knownCommands.put(s, command);
	}

	public static boolean unregisterCommand(PluginCommand command) {
		return unregisterCommand(command.getName());
	}

	public static boolean unregisterCommand(String name) {
		boolean is = false;
		for (Entry<String, Command> e : new HashMap<>(knownCommands).entrySet())
			if (e.getValue().getName().equals("name")) {
				knownCommands.remove(e.getKey());
				is = true;
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
	 */
	public static <T> T getRandomFromList(List<T> list) {
		return StringUtils.getRandomFromList(list);
	}

	/**
	 * @see see Return random object from collection
	 */
	public static <T> T getRandomFromCollection(Collection<T> list) {
		return StringUtils.getRandomFromCollection(list);
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
		if (maxDouble == 0)
			return maxDouble;
		boolean a = maxDouble < 0;
		if (a)
			maxDouble *= -1;
		double i = random.nextInt((int) maxDouble) + random.nextDouble();
		if (i < (min < 0 ? min * -1 : min))
			return min;
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
		if (maxInt == 0)
			return maxInt;
		boolean a = maxInt < 0;
		if (a)
			maxInt *= -1;
		int i = random.nextInt(maxInt);
		if (i < (min < 0 ? min * -1 : min))
			return min;
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
		} catch (Exception er) {
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
		return found != null && found.getName().equalsIgnoreCase(name) ? found : null;
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
	public static List<Player> getOnlinePlayers() {
		if(isNewerThan(7))return new ArrayList<>(Bukkit.getOnlinePlayers());
		Object o = Ref.invokeNulled(m);
		Player[] f = (Player[]) o;
		List<Player> p = new ArrayList<>(f.length);
		for(Player s : f)p.add(s);
		return p;
	}

	/**
	 * @see see Get random player from List<Player>
	 * @return List<Player>
	 */
	public static int getOnlineCount() {
		if(isNewerThan(7))return Bukkit.getOnlinePlayers().size();
		Object o = Ref.invokeNulled(m);
		return ((Player[]) o).length;
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
		if(message == null)return;
		String old = "";
		for (String s : colorize(message).replace("\\n", "\n").split("\n")) {
			s=old+s;
			sender.sendMessage(s);
			old = StringUtils.getLastColors(s);
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
		BossBar a = bars.get(p.getName());
		if(a==null) {
			a=new BossBar(p, TheAPI.colorize(text), progress, BarColor.GREEN, BarStyle.PROGRESS);
			bars.put(p.getName(), a);
			return a;
		}
		if (progress < 0)
			progress = 0;
		if (progress > 1)
			progress = 1;
		a.setProgress(progress);
		a.setTitle(colorize(text));
		if (a.isHidden())
			a.show();
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
		if (task.containsKey(p.getName()))
			Scheduler.cancelTask(task.remove(p.getName()));
		if (bars.containsKey(p.getName()))
			bars.remove(p.getName()).remove();
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
		Validator.validate(p == null, "Player is null");
		Validator.validate(text == null, "Text is null");
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutTitle(TitleAction.ACTIONBAR, colorize(text), 10, 20, 10));
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
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutTitle(TitleAction.TIMES, "", fadeIn, stay, fadeOut));
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutTitle(TitleAction.ACTIONBAR, colorize(text), fadeIn, stay, fadeOut));
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
			dispatchCommandAsync(target, value);
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
			dispatchCommandAsync(getConsole(), "say " + value);
			break;
		case COMMAND:
			dispatchCommandAsync(getConsole(), value);
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
			if (i != null)
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
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutTitle(TitleAction.TITLE, Ref.IChatBaseComponent(TheAPI.colorize(firstLine))));
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutTitle(TitleAction.SUBTITLE, Ref.IChatBaseComponent(TheAPI.colorize(nextLine))));
	}

	/**
	 * @see see Send player title
	 * @param p
	 * @param firstLine
	 * @param nextLine
	 */
	public static void sendTitle(Player p, String firstLine, String nextLine, int fadeIn, int stay, int fadeOut) {
		Validator.validate(p == null, "Player is null");
		Validator.validate(firstLine == null, "FirstLine is null");
		Validator.validate(nextLine == null, "NextLine is null");
		Validator.validate(fadeIn < 0, "FadeIn time is lower than zero");
		Validator.validate(stay < 0, "Stay time is lower than zero");
		Validator.validate(fadeOut < 0, "FadeOut time is lower than zero");
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutTitle(TitleAction.TIMES, "", fadeIn, stay, fadeOut));
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutTitle(TitleAction.TITLE, Ref.IChatBaseComponent(TheAPI.colorize(firstLine)),fadeIn,stay,fadeOut));
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutTitle(TitleAction.SUBTITLE, Ref.IChatBaseComponent(TheAPI.colorize(nextLine)),fadeIn,stay,fadeOut));
	}

	/**
	 * @see see Send message to all online players
	 * @param message
	 */
	public static void broadcastMessage(String message) {
		String old = "";
		for (String s : colorize(message).replace("\\n", "\n").split("\n")) {
			s=old+s;
			for(Player p : TheAPI.getOnlinePlayers())
				p.sendMessage(s);
			getConsole().sendMessage(colorize(s));
			old = StringUtils.getLastColors(s);
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
		String old = "";
		for (String s : colorize(message).replace("\\n", "\n").split("\n")) {
			s=old+s;
			for(Player p : TheAPI.getOnlinePlayers())
				if (p.hasPermission(permission))
					p.sendMessage(s);
			if (getConsole().hasPermission(permission))
				getConsole().sendMessage(colorize(s));
			old = StringUtils.getLastColors(s);
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
			return (int) Ref.get(Ref.player(p), isNewerThan(16)?"e":"ping");
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
			Player p = getPlayerOrNull(name);
			if(p!=null)
				s=p.getUniqueId().toString();
			else {
				if(LoaderClass.cache!=null) {
					s=LoaderClass.cache.lookupId(name).toString();
				}else
				s=UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes()).toString();
			}
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
		return new ArrayList<>(cache.values());
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
					String name = Bukkit.getOfflinePlayer(UUID.fromString(f.getName().replaceFirst(".yml", ""))).getName();
					if(name!=null)
						a.add(name);
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
		try {
			return getUser(UUID.fromString(nameOrUUID));
		} catch (Exception e) {
			String name = nameOrUUID;
			Player p = getPlayerOrNull(nameOrUUID);
			if(p!=null)name=p.getName();
			if(LoaderClass.cache!=null) {
				name=LoaderClass.cache.lookupName(name);
				return getUser(name,LoaderClass.cache.lookupId(name));
			}else {
				return getUser(name,UUID.nameUUIDFromBytes(("OfflinePlayer:"+name).getBytes()));
			}
		}
	}

	/**
	 * @see see If the user doesn't exist, his data file is created automatically.
	 * @param player Player
	 * @return User
	 */
	public static User getUser(Player player) {
		if (player == null)
			return null;
		return getUser(player.getName(),player.getUniqueId());
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
			User c = cache.get(uuid);
			if (c == null) {
				c = new User(uuid);
				cache.put(uuid, c);
			}
			return c;
		}
		return new User(uuid);
	}

	/**
	 * @see see If the user doesn't exist, his data file is created automatically.
	 * @param uuid UUID of player
	 * @return User
	 */
	public static User getUser(String name, UUID uuid) {
		if (uuid == null)
			return null;
		if (LoaderClass.config.getBoolean("Options.Cache.User.Use")) {
			User c = cache.get(uuid);
			if (c == null) {
				c = new User(name,uuid);
				cache.put(uuid, c);
			}
			return c;
		}
		return new User(name,uuid);
	}

	public static void removeCachedUser(UUID uuid) {
		if (uuid == null)
			return;
		User u = cache.remove(uuid);
		if(u!=null)u.save();
	}

	public static void removeCachedUser(Player player) {
		if (player == null)
			return;
		User u = cache.remove(player.getUniqueId());
		if(u!=null)u.save();
	}

	public static List<UUID> getUsersByIP(String ip) {
		List<UUID> uuid = new ArrayList<>();
		for(String a : PunishmentAPI.getPlayersOnIP(ip))
			uuid.add(getUser(a).getUUID());
		return uuid;
	}

	// Bit of LastLoginAPI
	public static User getLastLoggedUserByIP(String ip) {
		User a = null;
		long last = 0;
		for (UUID s : getUsersByIP(ip)) {
			long quit = new User(s).getLong("quit");
			if (quit <= last) {
				last = quit;
				a = getUser(s);
			}
		}
		return a;
	}

	public static void removeCachedUser(String nameOrUUID) {
		if (nameOrUUID == null)return;
		UUID s = null;
		try {
			s = UUID.fromString(nameOrUUID);
		} catch (Exception e) {
			s = LoaderClass.cache!=null?LoaderClass.cache.lookupId(nameOrUUID):Bukkit.getOfflinePlayer(nameOrUUID).getUniqueId();
		}
		User u = cache.remove(s);
		if(u!=null)u.save();
	}
}
