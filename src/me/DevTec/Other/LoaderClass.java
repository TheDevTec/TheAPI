package me.DevTec.Other;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.DevTec.ConfigAPI;
import me.DevTec.EconomyAPI;
import me.DevTec.MemoryAPI;
import me.DevTec.PluginManagerAPI;
import me.DevTec.ScoreboardAPI;
import me.DevTec.TheAPI;
import me.DevTec.BossBar.BossBar;
import me.DevTec.GUI.GUICreatorAPI;
import me.DevTec.NMS.NMSAPI;
import me.DevTec.NMS.Reflections;
import me.DevTec.NMS.PacketListeners.PacketHandler;
import me.DevTec.NMS.PacketListeners.PacketHandler_New;
import me.DevTec.NMS.PacketListeners.PacketHandler_Old;
import me.DevTec.Placeholders.ThePlaceholder;
import me.DevTec.Placeholders.ThePlaceholderAPI;
import me.DevTec.Scheduler.Scheduler;
import me.DevTec.Scheduler.Task;
import me.DevTec.Scheduler.Tasker;
import me.DevTec.TheVault.Bank;
import me.DevTec.TheVault.TheVault;
import me.DevTec.Utils.Events;
import me.DevTec.Utils.Tasks;
import me.DevTec.Utils.TheAPICommand;
import me.DevTec.WorldsManager.WorldsManager;
import net.milkbowl.vault.economy.Economy;

public class LoaderClass extends JavaPlugin {
	//Scoreboards
	public final HashMap<Integer, ScoreboardAPI> scoreboard = Maps.newHashMap();
	public final MultiMap<Integer, Integer, Object> map = new MultiMap<>();
	//Queue for updating blocks of BlocksAPI
	public final LinkedBlockingQueue<Object[]> refleshing = new LinkedBlockingQueue<Object[]>();
	//Scheduler
	public final HashMap<Integer, Task> scheduler = Maps.newHashMap();
	//GUIs
	public final HashMap<String, GUICreatorAPI> gui = Maps.newHashMap();
	//BossBars
	public final List<BossBar> bars = Lists.newArrayList();
	//TheAPI
	public static LoaderClass plugin;
	public static ConfigAPI unused= new ConfigAPI("TheAPI", "Cache"),
			config= new ConfigAPI("TheAPI", "Config")
			,data= new ConfigAPI("TheAPI", "Data");
	protected static boolean online = true;
	
	public String motd;
	public List<String> onlineText;
	public int max, fakeOnline;
	//EconomyAPI
	public boolean e, tve, tbank;
	public Economy economy;
	public me.DevTec.TheVault.Economy tveeconomy;
	public Bank bank;
	
	@Override
	public void onLoad() {
		plugin = this;
		createConfig();
		new Thread(new Runnable() {
			public void run() {
				while(online){
					if(refleshing.isEmpty())return;
					Object[] a = refleshing.poll();
					NMSAPI.refleshBlock(a[0], a[1], a[2], a[3]);
				}
			}
		}).start();
		if(TheAPI.isOlder1_9())
		new Thread(new Runnable() {
			public void run() {
				while(online){
			        for(BossBar s : bars)
			        	Reflections.invoke(s, Reflections.getMethod(s.getClass(), "move"));
			        try {
			            Thread.sleep(1000);
			        } catch (InterruptedException e) {
			        }
				}
			}
		}).start();		
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &6Loading plugin..", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &aEnabling plugin, creating config and registering economy..",
				TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		loadWorlds();
		loadPlaceholders();
		Tasks.load();
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		Bukkit.getPluginCommand("TheAPI").setExecutor(new TheAPICommand());
		if (PluginManagerAPI.getPlugin("TheVault") != null)
			TheVaultHooking();
		if (PluginManagerAPI.getPlugin("Vault") == null) {
			TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
			TheAPI.msg("&bTheAPI&7: &cPlugin not found Vault, EconomyAPI is disabled.", TheAPI.getConsole());
			TheAPI.msg("&bTheAPI&7: &cYou can enabled EconomyAPI by set custom Economy in EconomyAPI.",
					TheAPI.getConsole());
			TheAPI.msg("&bTheAPI&7: &c *TheAPI will still normally work without problems*", TheAPI.getConsole());
			TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		} else
			vaultHooking();
		new Tasker() {
			@Override
			public void run() {
				if (getTheAPIsPlugins().size() == 0)return;
				String end = getTheAPIsPlugins().size() != 1?"s":"";
				TheAPI.msg("&bTheAPI&7: &aTheAPI using " + getTheAPIsPlugins().size() + " plugin" + end,TheAPI.getConsole());
			}
		}.laterAsync(200);
		if(TheAPI.isNewerThan(7))
		handler = new PacketHandler_New();
		else
			handler = new PacketHandler_Old();
		for(Player s : TheAPI.getOnlinePlayers()) {
			if (!handler.hasInjected(handler.getChannel(s)))
				handler.injectPlayer(s);
		}
		}

	@SuppressWarnings("rawtypes")
	public PacketHandler handler;
	
	@Override
	public void onDisable() {
		if(TheAPI.isNewerThan(7))
		handler.close();
		online=false;
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &cDisabling plugin, saving configs and stopping runnables..",
				TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		Scheduler.cancelAll();
		for (String p : gui.keySet())
			gui.get(p).clear();
		gui.clear();
		unused.delete();
		data.reload();
		config.reload();
		ThePlaceholderAPI.unregister(main);
	}

	public List<Plugin> getTheAPIsPlugins() {
		List<Plugin> a = new ArrayList<Plugin>();
		for (Plugin all : PluginManagerAPI.getPlugins())
			if (PluginManagerAPI.getDepend(all.getName()).contains("TheAPI")
					|| PluginManagerAPI.getSoftDepend(all.getName()).contains("TheAPI"))
				a.add(all);
		return a;
	}
	
	private void createConfig() {
		data.create();
		config.setHeader("TNT, Action types: WAIT/DROP");
		config.addDefault("Options.ServerList.Enabled", false);
		config.addDefault("Options.ServerList.OnlinePlayersText", Arrays.asList("&eOnline: &6%server_online%","&eMax: &6%server_maxonline%"));
		config.addDefault("Options.ServerList.FakeOnline", -1);
		config.addDefault("Options.ServerList.FakeMax", 125);
		config.addDefault("Options.ServerList.Motd", "&eTheAPI's server MOTD\n&cServer version &6%server_version%");
		config.addDefault("Options.HideErrors", false); //hide only TheAPI errors
		config.addDefault("Options.AntiBot.Use", false);
		config.addDefault("Options.AntiBot.TimeBetweenPlayer", 10); //10 milis
		config.addDefault("Options.Optimize.TNT.Use", true);
		config.addDefault("Options.Optimize.TNT.Particles.Use", false);
		config.addDefault("Options.Optimize.TNT.Particles.Type", "EXPLOSION_LARGE");
		config.addDefault("Options.Optimize.TNT.LiquidCancelExplosion", true);
		config.addDefault("Options.Optimize.TNT.DestroyBlocks", true);
		config.addDefault("Options.Optimize.TNT.DamageEntities", true);
		config.addDefault("Options.Optimize.TNT.Power", 1);
		config.addDefault("Options.Optimize.TNT.Drops.Allowed", true);
		config.addDefault("Options.Optimize.TNT.Drops.InSingleLocation", true);
		config.addDefault("Options.Optimize.TNT.Drops.InFirstTNTLocation", false);
		config.addDefault("Options.Optimize.TNT.CollidingTNT.Disabled", false);
		config.addDefault("Options.Optimize.TNT.Action.LowMememory", "WAIT");
		config.addDefault("Options.Optimize.TNT.Action.LowTPS", "WAIT");
		config.addDefault("Options.Optimize.TNT.CollidingTNT.IgniteTime", 3); // 0 is ultra fast, but with ultra lag
		config.addDefault("Options.Optimize.TNT.SpawnTNT", false); //defaulty false, more friendly to server
		config.addDefault("Options.EntityMoveEvent.Reflesh", 3);
		config.addDefault("Options.EntityMoveEvent.Enabled", true); // set false to disable this event
		config.addDefault("Options.FakeEconomyAPI.Symbol", "$");
		config.addDefault("Options.FakeEconomyAPI.Format", "$%money%");
		config.addDefault("GameAPI.StartingIn", "&aStarting in %time%s");
		config.addDefault("GameAPI.Start", "&aStart");
		config.create();
		if(config.getBoolean("Options.ServerList.Enabled")) {
		motd=config.getString("Options.ServerList.Motd");
	    onlineText=config.getStringList("Options.ServerList.OnlinePlayersText");
	    max=config.getInt("Options.ServerList.FakeMax");
	    fakeOnline=config.getInt("Options.ServerList.FakeOnline");
		}else {
			fakeOnline=-1;
			max=Bukkit.getMaxPlayers();
			onlineText=null;
			motd=Bukkit.getMotd();
		}
		unused.setCustomEnd("dat");
		unused.create();
	}
	
	private static ThePlaceholder main;
	public void loadPlaceholders() {
		main= new ThePlaceholder("TheAPI") {
			@SuppressWarnings("deprecation")
			@Override
			public String onRequest(Player player, String placeholder) {
				if(player!=null) {
				if(placeholder.equalsIgnoreCase("player_money"))
					return ""+EconomyAPI.getBalance(player);
				if(placeholder.equalsIgnoreCase("player_formated_money"))
					return EconomyAPI.format(EconomyAPI.getBalance(player));
				if(placeholder.equalsIgnoreCase("player_displayname"))
					return player.getDisplayName();
				if(placeholder.equalsIgnoreCase("player_customname"))
					return player.getCustomName();
				if(placeholder.equalsIgnoreCase("player_name"))
					return player.getName();
				if(placeholder.equalsIgnoreCase("player_gamemode"))
					return player.getGameMode().name();
				if(placeholder.equalsIgnoreCase("player_uuid"))
					return player.getUniqueId().toString();
				if(placeholder.equalsIgnoreCase("player_health"))
					return ""+((Damageable)player).getHealth();
				if(placeholder.equalsIgnoreCase("player_food"))
					return ""+player.getFoodLevel();
				if(placeholder.equalsIgnoreCase("player_exp"))
					return ""+player.getExp();
				if(placeholder.equalsIgnoreCase("player_ping"))
					return ""+TheAPI.getPlayerPing(player);
				if(placeholder.equalsIgnoreCase("player_level"))
					return ""+player.getLevel();
				if(placeholder.equalsIgnoreCase("player_maxhealth"))
					return ""+((Damageable)player).getMaxHealth();
				if(placeholder.equalsIgnoreCase("player_world"))
					return ""+player.getWorld().getName();
				if(placeholder.equalsIgnoreCase("player_air"))
					return ""+player.getRemainingAir();
				if(placeholder.equalsIgnoreCase("player_statistic_play_one_tick"))
					return ""+player.getStatistic(Statistic.valueOf("PLAY_ONE_TICK"));
				if(placeholder.equalsIgnoreCase("player_statistic_play_one_minue"))
					return ""+player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE"));
				if(placeholder.equalsIgnoreCase("player_statistic_kills"))
					return ""+player.getStatistic(Statistic.PLAYER_KILLS);
				if(placeholder.equalsIgnoreCase("player_statistic_deaths"))
					return ""+player.getStatistic(Statistic.DEATHS);
				if(placeholder.equalsIgnoreCase("player_statistic_jump"))
					return ""+player.getStatistic(Statistic.JUMP);
				if(placeholder.equalsIgnoreCase("player_statistic_entity_kill"))
					return ""+player.getStatistic(Statistic.KILL_ENTITY);
				if(placeholder.equalsIgnoreCase("player_statistic_sneak_time"))
					return ""+player.getStatistic(Statistic.valueOf("SNEAK_TIME"));
			}
				if(placeholder.equalsIgnoreCase("server_time"))
					return ""+new SimpleDateFormat("HH:mm:ss").format(new Date());
				if(placeholder.equalsIgnoreCase("server_date"))
					return ""+new SimpleDateFormat("dd.MM.yyyy").format(new Date());
				if(placeholder.equalsIgnoreCase("server_online"))
					return ""+TheAPI.getOnlinePlayers().size();
				if(placeholder.equalsIgnoreCase("server_maxonline"))
					return ""+TheAPI.getMaxPlayers();
				if(placeholder.equalsIgnoreCase("server_version"))
					return Bukkit.getBukkitVersion();
				if(placeholder.equalsIgnoreCase("server_motd"))
					return motd!=null?motd:"";
				if(placeholder.equalsIgnoreCase("server_worlds"))
					return ""+Bukkit.getWorlds().size();
				if(placeholder.equalsIgnoreCase("server_tps"))
					return ""+TheAPI.getServerTPS();
				if(placeholder.equalsIgnoreCase("server_memory_max"))
					return ""+MemoryAPI.getMaxMemory();
				if(placeholder.equalsIgnoreCase("server_memory_used"))
					return ""+MemoryAPI.getUsedMemory(false);
				if(placeholder.equalsIgnoreCase("server_memory_free"))
					return ""+MemoryAPI.getFreeMemory(false);
				return null;
			}
		};
		ThePlaceholderAPI.register(main);
	}

	public void loadWorlds() {
		if (config.exist("Worlds")) {
			if (!config.getStringList("Worlds").isEmpty()) {
				TheAPI.msg("&bTheAPI&7: &8********************",TheAPI.getConsole());
				TheAPI.msg("&bTheAPI&7: &6Action: &6Loading worlds..",TheAPI.getConsole());
				TheAPI.msg("&bTheAPI&7: &8********************",TheAPI.getConsole());
				for (String s : config.getStringList("Worlds")) {
					String type = "Default";
					for (String w : Arrays.asList("Default", "Normal", "Nether", "The_End", "End", "The_Void", "Void",
							"Empty", "Flat")) {
						if (config.exist("WorldsSetting." + s)) {
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
					if (config.exist("WorldsSetting." + s + ".GenerateStructures"))
						f = config.getBoolean("WorldsSetting." + s + ".GenerateStructures");

					TheAPI.msg("&bTheAPI&7: &6Loading world with name '" + s + "'..", TheAPI.getConsole());
					WorldsManager.create(s, env, wt, f, 0);
					TheAPI.msg("&bTheAPI&7: &6World with name '" + s + "' loaded.", TheAPI.getConsole());
				}
				TheAPI.msg("&bTheAPI&7: &6All worlds loaded.", TheAPI.getConsole());
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
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &6Looking for Vault Economy..", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		new Tasker() {
			@Override
			public void run() {
				if (getVaultEconomy()) {
					e = true;
					TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
					TheAPI.msg("&bTheAPI&7: &6Found Vault Economy", TheAPI.getConsole());
					TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
					cancel();
				}
			}
		}.repeatingTimesAsync(0, 20, 15);
	}

	public void TheVaultHooking() {
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &6Looking for TheVault Economy and Bank system..", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		new Tasker() {
			boolean as = false, b = false;
			public void run() {
				if (TheVault.getEconomy() != null && !as) {
					as = true;
					tveeconomy = TheVault.getEconomy();
					tve = true;
					TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
					TheAPI.msg("&bTheAPI&7: &6Found TheVault Economy", TheAPI.getConsole());
					TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
				}
				if (TheVault.getBank() != null && !b) {
					b = true;
					bank = TheVault.getBank();
					tbank = true;
					TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
					TheAPI.msg("&bTheAPI&7: &6Found TheVault Bank system", TheAPI.getConsole());
					TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
				}
				if (as && b)
					cancel();
			}
		}.repeatingTimesAsync(0, 20, 15);
	}
}
