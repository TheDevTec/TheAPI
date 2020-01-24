package me.Straiker123;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import me.Straiker123.Utils.Error;
import me.Straiker123.Utils.Packets;
import net.glowstone.entity.GlowPlayer;

public class TheAPI {
	/**
	 * Colorize string with colors
	 * @param string
	 * @return String
	 */
	public static String colorize(String string) {
		if(string == null)return null;
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	/**
	 * Get all blocks in radius 20 blocks
	 * @return BlocksAPI
	 */
	public static BlocksAPI getBlocksAPI() {
		return new BlocksAPI();
	}

	/**
	 * Create or delete config
	 * @param localization
	 * @param name
	 * @return ConfigAPI
	 */
	public static ConfigAPI getConfig(String localization,  String name) {
		return new ConfigAPI(name,localization);
	}
	/**
	 * Return is server version 1.13+
	 * @return boolean
	 */
	public static boolean isNewVersion() {
		return !getServerVersion().equalsIgnoreCase("glowstone") 
				&& !getServerVersion().contains("v1_8")
				&& !getServerVersion().contains("v1_9")
				&& !getServerVersion().contains("v1_10")
				&& !getServerVersion().contains("v1_11")
				&& !getServerVersion().contains("v1_12");
	}

	/**
	 * Return time in which server start
	 * @return long
	 */
	public static long getServerStartTime() {
		return ManagementFactory.getRuntimeMXBean().getStartTime();
	}
	/**
	 * Build string from String[]
	 * @param args
	 * @return String
	 * 
	 */
	public static String buildString(String[] args) {
		if(args.length>0) {
		String msg = "";
		for (String string : args) {
			msg=msg+" "+string;
		}
		msg = msg.replaceFirst(" ",	"");
		return msg;
	}
	return null;
	}
	
	
	public static SQLAPI getSQLAPI(String host, String database, String username, String password, int port) {
		return new SQLAPI(host,database, username, password, port);
	}
	
	/**
	 * Create your own entity with damage, health and more
	 * @param type
	 * @return EntityCreatorAPI
	 */
	public static EntityCreatorAPI getEntityCreatorAPI(EntityType type) {
		return new EntityCreatorAPI(type);
	}
	/**
	 * Return random object from list
	 * @param list
	 * @return Object
	 */
	public static Object getRandomFromList(List<?> list) {
		if(list.isEmpty()||list==null)return null;
		int r = new Random().nextInt(list.size());
		if(r<=0) {
			if(list.get(0)!=null) {
				return list.get(0);
			}
			return null;
		}else
		return list.get(r);
	}
	/**
	 * Set world border size, center and more
	 * @param world
	 * @return WorldBorderAPI
	 */
	public static WorldBorderAPI getWorldBorder(World world) {
		return new WorldBorderAPI(world);
	}

	/**
	 * Generate random int with limit
	 * @param maxInt
	 * @return int
	 */
	public static int generateRandomInt(int maxInt) {
		boolean inMinus = false;
		if(maxInt<0) {
			maxInt=-1*maxInt;
			inMinus=true;
		}
		if(maxInt==0){
			return 0;
		}
		int i = new Random().nextInt(maxInt);
		if(inMinus)maxInt=-1*maxInt;
		return i;
	}
	/**
	 * Generate random double with limit
	 * @param maxDouble
	 * @return double
	 */
	public static double generateRandomDouble(double maxDouble) {
		boolean inMinus = false;
		if(maxDouble<0) {
			maxDouble=-1*maxDouble;
			inMinus=true;
		}
		if(maxDouble==0.0){
			return 0.0;
		}
		double i = new Random().nextInt((int)maxDouble)+new Random().nextDouble();
		if(i<=0)i=1;
		if(i>maxDouble)i=maxDouble;
		if(inMinus)maxDouble=-1*maxDouble;
		return i;
	}
	/**
	 * Return GameAPI in which you can create your own minigame
	 * @param MiniGameName
	 * @return GameAPI
	 */
	public static GameAPI getGameAPI(String MiniGameName) {
		return new GameAPI(MiniGameName);
	}

	public static long getServerUpTime() {
		return ManagementFactory.getRuntimeMXBean().getUptime();
	}
	
	
	public static PlaceholderAPIUtils getPlaceholderAPI() {
		return new PlaceholderAPIUtils();
	}
	
	/**
	 * Send player bossbar on time
	 * @param p
	 * @param text
	 * @param progress
	 * @param timeToExpire
	 */
	public static void sendBossBar(Player p, String text, double progress, int timeToExpire) {
		 if(p == null) {
	    	 Error.err("sending ActionBar", "Player is null");
		   return;
	   }
		if(getServerVersion().contains("v1_8")) {
			Error.err("sending bossbar to "+p.getName(), "Servers version 1.8.X doesn't have this method");
			return;
		}
	try {
		if(timeToExpire<0)timeToExpire=0;
	BossBar a = Bukkit.createBossBar(TheAPI.colorize(text), BarColor.GREEN, BarStyle.SEGMENTED_20);
	if(progress<0)progress=0;
	if(progress>1)progress=1;
	a.setProgress(progress);
	a.addPlayer(p);
	removeBossBar(p);
	if(timeToExpire!=0)
	Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() {

		@Override
		public void run() {
			removeBossBar(p);
		}
		
	},(long) (20*timeToExpire));
	}catch(Exception e) {
		Error.err("sending bossbar to "+p.getName(), "Text is null");
	}}
	/**
	 * Remove player from all bossbars in which player is in
	 * @param p
	 */
	public static void removeBossBar(Player p) {
		 if(p == null) {
	    	 Error.err("sending ActionBar", "Player is null");
		   return;
	   }
		if(getServerVersion().contains("v1_8")) {
			Error.err("sending bossbar to "+p.getName(), "Servers version 1.8.X doesn't have this method");
			return;
		}
		Bukkit.getBossBars().forEachRemaining(KeyedBossBar -> {
		if(KeyedBossBar.getPlayers().contains(p)){
			KeyedBossBar.removePlayer(p);
		}
		});
	}
	/**
	 * Return list with bossbars in which player is in
	 * @param p
	 * @return List<BossBar>
	 */
	public static List<BossBar> getBossBar(Player p) {
		 if(p == null) {
	    	 Error.err("sending ActionBar", "Player is null");
		   return null;
	   }
		if(getServerVersion().contains("v1_8")) {
			Error.err("sending bossbar to "+p.getName(), "Servers version 1.8.X doesn't have this method");
			return null;
		}
		List<BossBar> bossBars = new ArrayList<BossBar>();
		Bukkit.getBossBars().forEachRemaining(KeyedBossBar -> {
		if(KeyedBossBar.getPlayers().contains(p)){
			bossBars.add(KeyedBossBar);
		}
		});
			return bossBars;
		
	}
	/**
	 * Remove player action bar instanceof sendActionBar(player, "")
	 * @param p
	 */
	public static void removeActionBar(Player p) {
		 if(p == null) {
	    	 Error.err("sending ActionBar", "Player is null");
		   return;
	   }
		 sendActionBar(p,"");
	}
	/**
	 * Send player action bar
	 * @param p
	 * @param text
	 */
	public static void sendActionBar(Player p, String text) {
		   if(p == null) {
		    	 Error.err("sending ActionBar", "Player is null");
			   return;
		   }
		   if(TheAPI.getServerVersion().equals("glowstone")) {
				try {
					   ((GlowPlayer) p).sendActionBar(TheAPI.colorize(text));
						return;
					}catch (Exception e) {
				    	 Error.err("sending ActionBar to "+p.getName(), "Text is null");}
					}
		   
		   if(getServerVersion().contains("v1_8")) {
			   sendActionBarOld(p,text);
			   return;
		   }
		   
		     Class<?> PACKET_PLAYER_CHAT_CLASS = null, ICHATCOMP = null, CHATMESSAGE = null,
	       CHAT_MESSAGE_TYPE_CLASS = null;
	     Constructor<?> PACKET_PLAYER_CHAT_CONSTRUCTOR = null, CHATMESSAGE_CONSTRUCTOR = null;
	     Object CHAT_MESSAGE_TYPE_ENUM_OBJECT = null;
		     try {
		       PACKET_PLAYER_CHAT_CLASS = Packets.getNMSClass("PacketPlayOutChat");
		       ICHATCOMP = Packets.getNMSClass("IChatBaseComponent");
		       try {
		         CHAT_MESSAGE_TYPE_CLASS = Packets.getNMSClass("ChatMessageType");
		         CHAT_MESSAGE_TYPE_ENUM_OBJECT = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];
		 
		         PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP,
		             CHAT_MESSAGE_TYPE_CLASS);
		       } catch (NoSuchMethodException e) {
		       }
		       CHATMESSAGE = Packets.getNMSClass("ChatMessage");
		       CHATMESSAGE_CONSTRUCTOR = CHATMESSAGE.getConstructor(String.class, Object[].class);
		     } catch (Exception e) {
		     }
		   
	     try {
	       Object icb = CHATMESSAGE_CONSTRUCTOR.newInstance(TheAPI.colorize(text), new Object[0]);
	       Object packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, CHAT_MESSAGE_TYPE_ENUM_OBJECT);
	       Packets.sendPacket(p, packet);
	     } catch (Exception e) {
	    	 Error.err("sending ActionBar to "+p.getName(), "Text is null");
		}
	   }
	/**
	 * Get int, double or calculate string
	 * @param string
	 * @return NumbersAPI
	 */
	public static NumbersAPI getNumbersAPI(String string) {
		return new NumbersAPI(string);
	}
	/**
	 * Set player max health, air, teleport to location and more
	 * @param p
	 * @return PlayerAPI
	 */
	public static PlayerAPI getPlayerAPI(Player p) {
		return new PlayerAPI(p);
	}
	public static enum SudoType {
		CHAT,
		COMMAND
	}
	/**
	 * Send value as player
	 * @param target
	 * @param type
	 * @param value
	 */
	public static void sudo(Player target, SudoType type, String value) {
		switch(type) {
		case CHAT:
			target.chat(value);
			break;
		case COMMAND:
			target.performCommand(value);
			break;
		}
	}
	
	/**
	 * Send value as console
	 * @param type
	 * @param value
	 */
	public static void sudoConsole(SudoType type, String value) {
		switch(type) {
		case CHAT:
			Bukkit.dispatchCommand(getConsole(), "say "+value);
			break;
		case COMMAND:
			Bukkit.dispatchCommand(getConsole(), value);
			break;
		}
	}
	
	private static void giveItems(Player p, ItemStack item) {
		 if(p == null) {
	    	 Error.err("giving Item", "Player is null");
		   return;
	   }
		try {
		if(item==null)return;
		 if (p.getInventory().firstEmpty() == -1) {
	            p.getWorld().dropItem(p.getLocation(), item);
      } else {
	            p.getInventory().addItem(item);
      }
		}catch(Exception e) {
	    	 Error.err("giving Item to player "+p.getName(), "Item is null");
		   return;
		}
	}
	/**
	 * If player have full inventory, item will be dropped on ground or item will be added to player inventory
	 * @param p
	 * @param item
	 */
	public static void giveItem(Player p, ItemStack... item) {
		for(ItemStack i:item)
		giveItems(p,i);
	}
	/**
	 * If player have full inventory, item will be dropped on ground or item will be added to player inventory
	 * @param p
	 * @param item
	 * @param amount
	 */
	public static void giveItem(Player p, Material item, int amount) {
		giveItems(p,new ItemStack(item,amount));
	}
	
	/**
	 * Send player title
	 * @param p
	 * @param firstLine
	 * @param nextLine
	 */
	public static void sendTitle(Player p, String firstLine, String nextLine) {
		 if(p == null) {
	    	 Error.err("sending Title", "Player is null");
		   return;
	   }
		getPlayerAPI(p).sendTitle(firstLine, nextLine);
	}
	
	/**
	 * Set player chat format
	 * @param format
	 * Set to null to reset chat format
	 */
	public void setChatFormat(Player p, String format) {
		if(format!=null)
		LoaderClass.chatformat.put(p,format);
		else
			LoaderClass.chatformat.remove(p);
	}
	/**
	 * Send message to all online players
	 * @param message
	 */
	public static void broadcastMessage(String message) {
		for(Player p:Bukkit.getOnlinePlayers()) {
			p.sendMessage(colorize(message));
		}
		getConsole().sendMessage(colorize(message));
	}
	/**
	 * Send message to all online players with specified permission
	 * @param message
	 * @param permission
	 */
	public static void broadcast(String message, String permission) {
		for(Player p:Bukkit.getOnlinePlayers()) {
			if(p.hasPermission(permission))
			p.sendMessage(colorize(message));
		}
		getConsole().sendMessage(colorize(message));
	}
	/**
	 * Ban, Ban-Ip or mute player with reason and more
	 * @return PunishmentAPI
	 */
	public static PunishmentAPI getPunishmentAPI() {
		return new PunishmentAPI();
	}
	/**
	 * Set server motd in server list
	 * @param motd
	 */
	public static void setServerMotd(String motd) {
		LoaderClass.plugin.motd=colorize(motd);
	}
	/**
	 * Set server motd in server list
	 * @param firstLine
	 * @param secondLine
	 */
	public static void setServerMotd(String firstLine,String secondLine) {
		LoaderClass.plugin.motd=colorize(firstLine+"\n"+secondLine);
	}
	/**
	 * Constructor for own report system
	 * @return ReportSystem
	 */
	public static ReportSystem getReportSystem() {
		return new ReportSystem();
	}
	/**
	 * Set max players on server
	 * @param int
	 */
	public static void setMaxPlayers(int max) {
		LoaderClass.plugin.max=max;
	}
	/**
	 * Hide or show player to players on server
	 * @param p
	 * @param permission To see player
	 * @param vanish
	 */
	public static void vanish(Player p, String permission, boolean vanish) {
		v(p,vanish,permission);
	}
	/**
	 * Return is player in vanish mode
	 * @param p
	 * @return boolean
	 */
	public static boolean isVanished(Player p) {
		return LoaderClass.data.getConfig().getString("data."+p.getName()+".vanish")!=null;
	}
	
	/**
	 * Hide or show player to players on server
	 * @param p
	 * @param vanish
	 */
	public static void vanish(Player p, boolean vanish) {
		v(p,vanish,null);
	}
	private static boolean has(Player s, Player d) {
		if(LoaderClass.data.getConfig().getString("data."+d.getName()+".vanish")!=null)
			return s.hasPermission(LoaderClass.data.getConfig().getString("data."+d.getName()+".vanish"));
		else
		return false;
	}
	@SuppressWarnings("deprecation")
	private static void hide(Player p) {
		if(isVanished(p)) {
			for (Player s : Bukkit.getOnlinePlayers()) {
				if(s!=p && !has(s,p))
	                s.hidePlayer(p);
	        }
		}else {
			for (Player s : Bukkit.getOnlinePlayers()) {
	                s.showPlayer(p);
	        }
		}
	}
	private static void v(Player p, boolean vanish, String perm) {
		if(vanish) {
		if(perm!=null)
			LoaderClass.data.getConfig().set("data."+p.getName()+".vanish", perm);
		List<String> v= LoaderClass.data.getConfig().getStringList("vanished");
		v.add(p.getName());
			LoaderClass.data.getConfig().set("vanished", v);
		}else {
			LoaderClass.data.getConfig().set("data."+p.getName()+".vanish", null);
			List<String> v= LoaderClass.data.getConfig().getStringList("vanished");
			v.remove(p.getName());
				LoaderClass.data.getConfig().set("vanished", v);
		}
		LoaderClass.data.save();
		hide(p);
	}
	
	/**
	 * Return console 
	 * @return CommandSender
	 */
	public static CommandSender getConsole() {
		return Bukkit.getConsoleSender();
	}
	
	/**
	 * Create, delete, unload or load world
	 * @return WorldsManager
	 */
	public static WorldsManager getWorldsManager() {
		return new WorldsManager();
	}
	
	/**
	 * Deposit, withdraw from player money and more
	 * @return EconomyAPI
	 */
	public static EconomyAPI getEconomyAPI() {
		return new EconomyAPI();
	}
	
	/**
	 * Set player Header and Footer in tablist
	 * @return TabListAPI
	 */
	public static TabListAPI getTabListAPI() {
		return new TabListAPI();
	}
	
	/**
	 * Count enabled plugins, players on server and more
	 * @return CountingAPI
	 */
	public static CountingAPI getCountingAPI() {
		return new CountingAPI();
	}
	
	/**
	 * Send formated message to all online players with specified permission
	 * @param s
	 * @param message
	 */
	public static void sendHelpOp(CommandSender s, String message) {
		broadcast(LoaderClass.config.getConfig().getString("Format.HelpOp")
					.replace("%message%", message).replace("%sender%", s.getName()),LoaderClass.config.getConfig().getString("Format.HelpOp-Permission"));
		
		if(!s.hasPermission(LoaderClass.config.getConfig().getString("Format.HelpOp-Permission")))
			s.sendMessage(colorize(LoaderClass.config.getConfig().getString("Format.HelpOp")
				.replace("%message%", message).replace("%sender%", s.getName())));
	}
	
	/**
	 * Set player name tag
	 * @param p
	 * @param prefix
	 * @param suffix
	 * @return NameTagAPI
	 */
	public static NameTagAPI getNameTagAPI(Player p, String prefix, String suffix) {
		return new NameTagAPI(p, prefix, suffix);
	}
	
	/**
	 * Create cooldown or delete cooldown
	 * @param cooldown
	 * @return CooldownAPI
	 */
	public static CooldownAPI getCooldownAPI(String cooldown) {
		return new CooldownAPI(cooldown);
	}
	
	/**
	 * Get used memory, free memory and max memory
	 * @return MemoryAPI
	 */
	public static MemoryAPI getMemoryAPI() {
		return new MemoryAPI();
	}
	/**
	 * Load, unload, enable or disable plugins
	 * @return PluginManagerAPI
	 */
	public static PluginManagerAPI getPluginsManagerAPI() {
		return new PluginManagerAPI();
	}
	/**
	 * Get bukkit name of enchantment from string for ex. Sharpness -> DAMAGE_ALL
	 * @return EnchantmentAPI
	 */
	public static EnchantmentAPI getEnchantmentAPI() {
		return new EnchantmentAPI();
	}
	/**
	 * Send player scoreboard with per player scoreboard function
	 * @param p
	 * @param board
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p, Scoreboard board) {
		return new ScoreboardAPI(p,board);
	}
	/**
	 * Send player scoreboard with per player scoreboard function
	 * @param p
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPI getScoreboardAPI(Player p) {
		return new ScoreboardAPI(p,p.getServer().getScoreboardManager().getNewScoreboard());
	}
	/**
	 * Send player sound or get sound name from String
	 * @return SoundAPI
	 */
	public static SoundAPI getSoundAPI() {
		return new SoundAPI();
	}
	/**
	 * Convert long to String time or String time to long
	 * @return TimeConventorAPI
	 */
	public static TimeConventorAPI getTimeConventorAPI() {
		return new TimeConventorAPI();
	}
	/**
	 * Create GUI without events
	 * @param p
	 * @return GUICreatorAPI
	 */
	public static GUICreatorAPI getGUICreatorAPI(Player p) {
		return new GUICreatorAPI(p);
	}
	/**
	 * Create ItemStack with custom lore, model, displayname and more
	 * @param material
	 * @return ItemCreatorAPI
	 */
	public static ItemCreatorAPI getItemCreatorAPI(Material material) {
		return new ItemCreatorAPI(new ItemStack(material));
	}
	/**
	 * Create ItemStack with custom lore, model, displayname and more
	 * @param itemstack
	 * @return ItemCreatorAPI
	 */
	public static ItemCreatorAPI getItemCreatorAPI(ItemStack itemstack) {
		return new ItemCreatorAPI(itemstack);
	}
	/**
	 * Return server version, for ex. v1_14_R1
	 * @return String
	 */
	public static String getServerVersion() {
		String serverVer = null;
		try {
			 serverVer= Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	 }catch(Exception e) {
			 serverVer=Bukkit.getServer().getClass().getPackage().getName().split("\\.")[1];
	 }
		return serverVer;
	}
	/**
	 * Return server TPS
	 * @return double
	 */
	public static double getServerTPS() {
		try {
	    	Object minecraftServer = null;
	    	Field recentTps = null;
	            Server server = LoaderClass.plugin.getServer();
	            Field consoleField = server.getClass().getDeclaredField("console");
	            consoleField.setAccessible(true);
	            minecraftServer = consoleField.get(server);
	     
	            recentTps = minecraftServer.getClass().getSuperclass().getDeclaredField("recentTps");
	            recentTps.setAccessible(true);
	        
	        double tps = ((double[]) recentTps.get(minecraftServer))[0];
	        if(tps>20)tps=20;
			return getNumbersAPI(String.format("%2.02f", tps)).getDouble();
	    	}catch(Throwable e) {
	    		return 20.0;
	    	}
	}
	/**
	 * Return player ping
	 * @param p
	 * @return int
	 */
	public static int getPlayerPing(Player p) {
		if(getServerVersion().equals("glowstone")) {
			try {
			return ((GlowPlayer)p).getUserListEntry().getPing();
			}catch(Exception e) {
				return -1;
			}
		}
		try {
	        Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit."
	                + getServerVersion() + ".entity.CraftPlayer");
	        Object handle = craftPlayer.getMethod("getHandle").invoke(p);
	        Integer ping = (Integer) handle.getClass().getDeclaredField("ping").get(handle);
	        return ping.intValue();
	    } catch (ClassNotFoundException | IllegalAccessException
	            | IllegalArgumentException | InvocationTargetException
	            | NoSuchMethodException | SecurityException
	            | NoSuchFieldException e) {
	        return -1;
	    }
	}

	private static void sendActionBarOld(Player ps, String text) {
	        try {
	            Object ppoc;
	            Class<?> c2, c3,
	                    c4 = Class.forName("net.minecraft.server."+getServerVersion()+".PacketPlayOutChat");
	            Object o;
	                c2 = Class.forName("net.minecraft.server."+getServerVersion()+".ChatComponentText");
	                c3 = Class.forName("net.minecraft.server."+getServerVersion()+".IChatBaseComponent");
	                o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(TheAPI.colorize(text));
	           
	            ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
	            Packets.sendPacket(ps, ppoc);
	        } catch (Exception ex) {
		    	 Error.err("sending ActionBar to "+ps.getName(), "Text is null");
	        }
	    }
	
}
