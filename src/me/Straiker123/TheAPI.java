package me.Straiker123;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import me.Straiker123.Events.PlayerVanishEvent;
import me.Straiker123.Utils.Error;
import me.Straiker123.Utils.Packets;
import net.glowstone.entity.GlowPlayer;

public class TheAPI {
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
	 * @see see Create repeating runnable more easier than before
	 * @return TheRunnable
	 */
	public static TheRunnable getTheRunnable() {
		return new TheRunnable();
	}

	/**
	 * @see see Create repeating runnable more easier than before
	 * @return TheRunnable
	 */
	public static TheRunnable getRunnable() {
		return getTheRunnable();
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
	public static <T> MultiMap<T> getMultiMap() {
		return new MultiMap<T>();
	}

	/**
	 * @see see StringUtils (Get int/double/long from String, convertLong to String time or visa verse and more
	 * @return StringUtils
	 */
	public static StringUtils getStringUtils() {
		return new StringUtils();
	}

	/**
	 * @see see Using this API you can sort HashMap
	 * @return RankingAPI
	 */
	public static RankingAPI getRankingAPI(HashMap<?, Double> map) {
		return new RankingAPI(map);
	}
	
	/**
	 * @see see Only for 1.8.X and older
	 * @return ParticleEffectAPI
	 */
	public static ParticleEffectAPI getParticleEffectAPI() {
		 return new ParticleEffectAPI();
	}
	
	/**
	 * @see see Create or delete config
	 * @param localization
	 * @param name
	 * @return ConfigAPI
	 */
	public static ConfigAPI getConfig(String localization,  String name) {
		return new ConfigAPI(localization,name);
	}
	/**
	 * @see see Return is server version 1.13+
	 * @return boolean
	 */
	public static boolean isNewVersion() {
		if(getServerVersion().equalsIgnoreCase("glowstone"))return false;
		return getStringUtils().getInt(getServerVersion().split("_")[1])>12;
	}
	/**
	 * @see see Return is server version older than 1.9 ? (1.5 up to 1.8.9)
	 * @return boolean
	 */
	public static boolean isOlder1_9() {
		return getStringUtils().getInt(getServerVersion().split("_")[1])<9;
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
		return new SQLAPI(host,database, username, password, port);
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
	 * @see see Generate random double with limit
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
	 * @see see Replace in String/List<String> placeholders from PlaceholderAPI without depend
	 * @return PlaceholderAPIUtils
	 */
	public static PlaceholderAPIUtils getPlaceholderAPI() {
		return new PlaceholderAPIUtils();
	}

	/**
	 * @see see int of maximmum players on the server
	 * @return int
	 */
	public static int getMaxPlayers() {
		return Bukkit.getMaxPlayers();
	}

	/**
	 * @see see Get player from List<Player>
	 * @return Player
	 */
	public static Player getPlayer(int i) {
		return getOnlinePlayers().size() <= i ? null : getOnlinePlayers().get(i);
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
		return getRandomFromList(getOnlinePlayers()) == null ? null : (Player)getRandomFromList(getOnlinePlayers());
	}

	/**
	 * @see see Get random player from List<Player>
	 * @return List<Player>
	 */
	public static List<Player> getOnlinePlayers(){
		return LoaderClass.getOnline();
	}

	/**
	 * @see see Get random player from List<Player>
	 * @return List<Player>
	 */
	public static List<Player> getPlayers(){
		return LoaderClass.getOnline();
	}

	
	public static void sendMessage(String message, CommandSender sender) {
		if(sender==null) {
	    	 Error.err("sending message", "CommandSender is null");
		   return;
		}
		if(message==null) {
	    	 Error.err("sending message", "Message is null");
		   return;
		}
		ChatColor old = ChatColor.RESET;
		for(String s : message.replace("\\n", "\n").split("\n")) {
		sender.sendMessage(old+colorize(s));
		old=getStringUtils().getColor(s);
		}
	}

	public static void sendMsg(String message, CommandSender sender) {
		sendMessage(message,sender);
	}
	public static void msg(String message, CommandSender sender) {
		sendMessage(message,sender);
	}
	
	private static HashMap<Player, BossBar> list = new HashMap<Player, BossBar>();
	private static HashMap<Player, BukkitTask> task = new HashMap<Player, BukkitTask>();
	/**
	 * @see see Send player bossbar on time
	 * @param p
	 * @param text
	 * @param progress
	 * @param timeToExpire
	 */
	public static void sendBossBar(Player p, String text, double progress, int timeToExpire) {
		 if(p == null) {
	    	 Error.err("sending bossbar", "Player is null");
		   return;
	   }
			if(getServerVersion().contains("v1_5") ||getServerVersion().contains("v1_6") 
					||getServerVersion().contains("v1_7")||getServerVersion().contains("v1_8")) {
			Error.err("sending bossbar to "+p.getName(), "Servers version older 1.9 doesn't have this method");
			return;
		}
	try {
		removeBossBar(p);
	BossBar a = Bukkit.createBossBar(TheAPI.colorize(text), BarColor.GREEN, BarStyle.SEGMENTED_20);
	if(progress<0)progress=0;
	if(progress>1)progress=1;
	a.setProgress(progress);
	a.addPlayer(p);
	list.put(p, a);
	task.put(p,Bukkit.getScheduler().runTaskLater(LoaderClass.plugin, new Runnable() {
		@Override
		public void run() {
			removeBossBar(p);
	}},timeToExpire));
	}catch(Exception e) {
		Error.err("sending bossbar to "+p.getName(), "Text is null");
	}}
	/**
	 * @see see Remove player from all bossbars in which player is in
	 * @param p
	 */
	@SuppressWarnings("deprecation")
	public static void removeBossBar(Player p) {
		 if(p == null) {
	    	 Error.err("removing bossbars", "Player is null");
		   return;
	   }
		 try {
			if(list.containsKey(p)) {
				task.get(p).cancel();
				BossBar b = list.get(p);
				b.hide();
				b.removePlayer(p);
				list.remove(p);
			}
		for(BossBar c : getBossBar(p)) {
			c.hide();
			c.removePlayer(p);
		}
		 }catch(Exception err) {
				if(getServerVersion().contains("v1_5") ||getServerVersion().contains("v1_6") 
						||getServerVersion().contains("v1_7")||getServerVersion().contains("v1_8"))
					Error.err("removing bossbars of player "+p.getName(), "Servers version older 1.9 doesn't have this method");
		 }
	}
	/**
	 * @see see Return list with bossbars in which player is in
	 * @param p
	 * @return List<BossBar>
	 */
	public static List<BossBar> getBossBar(Player p) {
		 if(p == null) {
	    	 Error.err("getting bossbars", "Player is null");
		   return null;
	   }
			if(getServerVersion().contains("v1_5") ||getServerVersion().contains("v1_6") 
					||getServerVersion().contains("v1_7")||getServerVersion().contains("v1_8")) {
				Error.err("getting bossbars of player "+p.getName(), "Servers version older 1.9 doesn't have this method");
			return null;
		}
		List<BossBar> bossBars = new ArrayList<BossBar>();
		Bukkit.getBossBars().forEachRemaining(BossBar -> {
		if(BossBar.getPlayers().contains(p)){
			bossBars.add(BossBar);
		}
		});
			return bossBars;
		
	}
	/**
	 * @see see Remove player action bar instanceof sendActionBar(player, "")
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
	 * @see see Send player action bar
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

			if(getServerVersion().contains("v1_5")||getServerVersion().contains("v1_6") 
					||getServerVersion().contains("v1_7") ||getServerVersion().contains("v1_8")) {
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
		CHAT,
		COMMAND
	}
	/**
	 * @see see Send value as player
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
	 * @see see Send value as console
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
	 * @see see If player have full inventory, item will be dropped on ground or item will be added to player inventory
	 * @param p
	 * @param item
	 */
	public static void giveItem(Player p, List<ItemStack> item) {
		for(ItemStack i:item)
		giveItems(p,i);
	}
	/**
	 * @see see If player have full inventory, item will be dropped on ground or item will be added to player inventory
	 * @param p
	 * @param item
	 */
	public static void giveItem(Player p, ItemStack... item) {
		for(ItemStack i:item)
		giveItems(p,i);
	}
	/**
	 * @see see If player have full inventory, item will be dropped on ground or item will be added to player inventory
	 * @param p
	 * @param item
	 * @param amount
	 */
	public static void giveItem(Player p, Material item, int amount) {
		giveItems(p,new ItemStack(item,amount));
	}
	
	/**
	 * @see see Send player title
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
	 * @see see Set player chat format
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
	 * @see see Send message to all online players with interval
	 * @param message
	 * @param time
	 */
	public void slowBroadcast(List<String> messages,long interval) {
		SlowLoop<String> t = new SlowLoop<String>() {
			void toRun(String t) {
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
	public void slowBroadcast(Collection<String> messages,long interval) {
		SlowLoop<String> t = new SlowLoop<String>() {
			void toRun(String t) {
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
		for(Player p:TheAPI.getOnlinePlayers()) {
			for(String s : message.replace("\\n", "\n").split("\n")) {
			p.sendMessage(colorize(s));
			}
		}
		for(String s : message.replace("\\n", "\n").split("\n")) {
		getConsole().sendMessage(colorize(s));
		}
	}
	/**
	 * @see see Send message to all online players with specified permission
	 * @param message
	 * @param permission
	 */
	public static void broadcast(String message, String permission) {
		for(Player p:TheAPI.getOnlinePlayers()) {
			if(p.hasPermission(permission))
				for(String s : message.replace("\\n", "\n").split("\n")) {
			msg(s,p);
				}
		}
		ChatColor old = ChatColor.RESET;
			for(String s : message.replace("\\n", "\n").split("\n")) {
		getConsole().sendMessage(old+colorize(s));
		old=getStringUtils().getColor(s);
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
	 * @see see Set server motd in server list
	 * @param motd
	 */
	public static void setServerMotd(String motd) {
		LoaderClass.plugin.motd=colorize(motd);
	}
	/**
	 * @see see Set server motd in server list
	 * @param firstLine
	 * @param secondLine
	 */
	public static void setServerMotd(String firstLine,String secondLine) {
		LoaderClass.plugin.motd=colorize(firstLine+"\n"+secondLine);
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
		LoaderClass.plugin.max=max;
	}
	/**
	 * @see see Hide or show player to players on server
	 * @param p
	 * @param permission To see player
	 * @param vanish
	 */
	public static void vanish(Player p, String permission, boolean vanish) {
		v(p,vanish,permission);
	}
	private static boolean isV(Player player) {
		if(player.hasMetadata("vanished"))
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
}
	/**
	 * @see see Return is player in vanish mode
	 * @param p
	 * @return boolean
	 */
	public static boolean isVanished(Player p) {
		if(isV(p))return true;
		return LoaderClass.data.getConfig().getString("data."+p.getName()+".vanish")!=null;
	}
	/**
	 * @see see Return is player in vanish mode
	 * @param p
	 * @return boolean
	 */
	public static boolean isVanished(String p) {
		return LoaderClass.data.getConfig().getString("data."+p+".vanish")!=null;
	}

	/**
	 * @see see Hide or show player to players on server
	 * @param p
	 * @param vanish
	 */
	public static void vanish(Player p, boolean vanish) {
		v(p,vanish,null);
	}
	/**
	 * @see see Hide or show player to players on server
	 * @param p
	 * @param vanish
	 */
	public static void vanish(String p, boolean vanish) {
		vanish(p,vanish,null);
	}
	/**
	 * @see see Hide or show player to players on server
	 * @param p
	 * @param vanish
	 */
	public static void vanish(String p, boolean vanish, String perm) {
		if(vanish) {
			if(perm!=null)
				LoaderClass.data.getConfig().set("data."+p+".vanish", perm);
			List<String> v= LoaderClass.data.getConfig().getStringList("vanished");
			v.add(p);
				LoaderClass.data.getConfig().set("vanished", v);
			}else {
				LoaderClass.data.getConfig().set("data."+p+".vanish", null);
				List<String> v= LoaderClass.data.getConfig().getStringList("vanished");
				v.remove(p);
					LoaderClass.data.getConfig().set("vanished", v);
			}
		LoaderClass.data.save();
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
			for (Player s : getOnlinePlayers()) {
				if(s!=p && !has(s,p))
	                s.hidePlayer(p);
	        }
		}else {
			for (Player s : getOnlinePlayers()) {
	                s.showPlayer(p);
	        }
		}
	}
	private static void v(Player p, boolean vanish, String perm) {
		 
		PlayerVanishEvent d = new PlayerVanishEvent(p,(perm == null ? null : perm),vanish,(perm== null ? false: true));
		Bukkit.getPluginManager().callEvent(d);
		if(!d.isCancelled()){
			vanish=d.vanish();
			perm=d.getPermission();
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
	 * @see see Send formated message to all online players with specified permission
	 * @param s
	 * @param message
	 */
	public static void sendHelpOp(CommandSender s, String message) {
		for(String ss : message.replace("\\n", "\n").split("\n")) {
		broadcast(LoaderClass.config.getConfig().getString("Format.HelpOp")
					.replace("%message%", ss).replace("%sender%", s.getName()),LoaderClass.config.getConfig().getString("Format.HelpOp-Permission"));
		}
		if(!s.hasPermission(LoaderClass.config.getConfig().getString("Format.HelpOp-Permission")))
			for(String ss : message.replace("\\n", "\n").split("\n")) {
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
	 * @see see Create cooldown or delete cooldown
	 * @param cooldown
	 * @return CooldownAPI
	 */
	public static CooldownAPI getCooldownAPI(String cooldown) {
		return new CooldownAPI(cooldown);
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
	 * @see see Get bukkit name of enchantment from string for ex. Sharpness -> DAMAGE_ALL
	 * @return EnchantmentAPI
	 */
	public static EnchantmentAPI getEnchantmentAPI() {
		return new EnchantmentAPI();
	}
	/**
	 * @see see Send player scoreboard with per player scoreboard function (Flashing, but overide other scoreboards)
	 * @param p
	 * @param board
	 * @return ScoreboardAPI
	 */
	@Deprecated
	public static ScoreboardAPI getScoreboardAPI(Player p, Scoreboard board) {
		return new ScoreboardAPI(p,board);
	}
	/**
	 * @see see Send player scoreboard with per player scoreboard function (Flashing)
	 * @param p
	 * @return ScoreboardAPI
	 */
	@Deprecated
	public static ScoreboardAPI getScoreboardAPI(Player p) {
		return new ScoreboardAPI(p,p.getServer().getScoreboardManager().getNewScoreboard());
	}

	/**
	 * @see see Send player scoreboard with per player scoreboard function (Non-flashing)
	 * @param p
	 * @return ScoreboardAPI
	 */
	public static ScoreboardAPIV2 getScoreboardAPIV2(Player p) {
		return new ScoreboardAPIV2(p);
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
	 * @see see Create GUI without events
	 * @param p
	 * @return GUICreatorAPI
	 */
	public static GUICreatorAPI getGUICreatorAPI(Player p) {
		return new GUICreatorAPI(p);
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
		String serverVer = null;
		try {
			 serverVer= Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	 }catch(Exception e) {
			 serverVer=Bukkit.getServer().getClass().getPackage().getName().split("\\.")[1];
	 }
		return serverVer;
	}
	/**
	 * @see see Return server TPS
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
	 * @see see Return player ping
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
	        Class<?> craftPlayer = Packets.getBukkitClass("entity.CraftPlayer");
	        Object handle = craftPlayer.getMethod("getHandle").invoke(p);
	        Integer ping = (Integer) handle.getClass().getDeclaredField("ping").get(handle);
	        return ping.intValue();
	    } catch (Exception e) {
	        return -1;
	    }
	}

	private static void sendActionBarOld(Player ps, String text) {
	        try {
	            Object ppoc;
	            Class<?> c2, c3,
	                    c4 = Packets.getNMSClass("PacketPlayOutChat");
	            Object o;
	                c2 = Packets.getNMSClass("ChatComponentText");
	                c3 = Packets.getNMSClass("IChatBaseComponent");
	                o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(TheAPI.colorize(text));
	           
	            ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
	            Packets.sendPacket(ps, ppoc);
	        } catch (Exception ex) {
		    	 Error.err("sending ActionBar to "+ps.getName(), "Text is null");
	        }
	}
}
