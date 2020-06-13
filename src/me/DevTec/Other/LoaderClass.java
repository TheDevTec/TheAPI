package me.DevTec.Other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import me.DevTec.ConfigAPI;
import me.DevTec.ScoreboardAPI;
import me.DevTec.ScoreboardAPI.Team;
import me.DevTec.TheAPI;
import me.DevTec.Events.PacketReadEvent;
import me.DevTec.Events.PacketReceiveEvent;
import me.DevTec.GUI.GUIID;
import me.DevTec.NMS.NMSPlayer;
import me.DevTec.Scheduler.Scheduler;
import me.DevTec.Scheduler.Task;
import me.DevTec.Scheduler.Tasker;
import me.DevTec.TheVault.Bank;
import me.DevTec.TheVault.TheVault;
import me.DevTec.Utils.Events;
import me.DevTec.Utils.Tasks;
import me.DevTec.Utils.TheAPICommand;
import net.milkbowl.vault.economy.Economy;

public class LoaderClass extends JavaPlugin {
	public static final HashMap<String, ScoreboardAPI> scoreboard = Maps.newHashMap();
	public static LoaderClass plugin;
	public static final LinkedBlockingQueue<Object[]> refleshing = Queues.newLinkedBlockingQueue();
	public static final MultiMap<String, Integer, Team> map = new MultiMap<String, Integer, Team>();
	public final HashMap<Integer, Task> scheduler = Maps.newHashMap();
	public final HashMap<Player, GUIID> gui = Maps.newHashMap();
	public final HashMap<String, Integer> GameAPI_Arenas = Maps.newHashMap();
	public final HashMap<String, Runnable> win_rewards = Maps.newHashMap();
	public final List<Integer> tasks = Lists.newArrayList();
	public static ConfigAPI unused,config,gameapi,data;
	public static boolean online = true;
	public static int task = 1;
	//public static boolean created;
	@Override
	public void onLoad() {
		unused = new ConfigAPI(this, "UnusedData");
		config = new ConfigAPI(this, "Config");
		gameapi = new ConfigAPI(this, "GameAPI");
		data = new ConfigAPI(this, "Data");
		plugin = this;
		createConfig();
		new Thread(new Runnable() {
			public void run() {
				while(online){
					if(refleshing.isEmpty())return;
					Object[] a = refleshing.poll();
					TheAPI.getNMSAPI().refleshBlock(a[0], a[1], a[2], a[3]);
			}}
		}).start();
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &6Loading plugin..", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
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

	private boolean as = false, b = false;

	public void TheVaultHooking() {
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &6Looking for TheVault Economy and Bank system..", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		new Tasker() {
			@Override
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

	public boolean e, tve, tbank;
	public String motd;
	public int max;

	public List<Plugin> getTheAPIsPlugins() {
		List<Plugin> a = new ArrayList<Plugin>();
		for (Plugin all : TheAPI.getPluginsManagerAPI().getPlugins())
			if (TheAPI.getPluginsManagerAPI().getDepend(all.getName()).contains("TheAPI")
					|| TheAPI.getPluginsManagerAPI().getSoftDepend(all.getName()).contains("TheAPI"))
				a.add(all);
		return a;
	}

	@Override
	public void onEnable() {
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &aEnabling plugin, creating config and registering economy..",
				TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		for(String s : config.getStringList("Worlds")) {
			Environment env = Environment.NORMAL;
			WorldType wt = WorldType.NORMAL;
		switch (config.getInt("WorldsSetting."+s+".Generator")) {
		case 1:
			wt = WorldType.FLAT;
			break;
		case 2:
			env = Environment.NETHER;
			break;
		case 3:
			try {
				env = Environment.valueOf("THE_END");
			} catch (Exception e) {
				env = Environment.valueOf("END");
			}
			break;
		case 4:
			wt = null;
			break;
		}
		if(!TheAPI.getWorldsManager().create(s, env, wt,config.getBoolean("WorldsSetting."+s+".GenerateStructures"),0)) {
			TheAPI.msg("&cError when loading world '"+s+"' with settings GenerateStructures:"+config.getBoolean("WorldsSetting."+s+".GenerateStructures")+", Generator:"+config.getInt("WorldsSetting."+s+".Generator")+".", TheAPI.getConsole());
		}
		}
		Tasks.load();
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		Bukkit.getPluginCommand("TheAPI").setExecutor(new TheAPICommand());
		if (TheAPI.getPluginsManagerAPI().getPlugin("TheVault") != null)
			TheVaultHooking();
		if (TheAPI.getPluginsManagerAPI().getPlugin("Vault") == null) {
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
				if (getTheAPIsPlugins().size() == 0)
					return;
				String end = "";
				if (getTheAPIsPlugins().size() != 1)
					end = "s";
				TheAPI.msg("&bTheAPI&7: &aTheAPI using " + getTheAPIsPlugins().size() + " plugin" + end,
						TheAPI.getConsole());
			}
		}.laterAsync(200);
		for(Player s : TheAPI.getOnlinePlayers()) {
			if (LoaderClass.config.getBoolean("Options.PacketListener")) {
				ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
		            @Override
		            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
		            	PacketReceiveEvent e = new PacketReceiveEvent(s,packet);
		            	Bukkit.getPluginManager().callEvent(e);
		            	if(e.isCancelled())return;
		                super.channelRead(channelHandlerContext, e.getPacket());
		            }
		            @Override
		            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
		            	PacketReadEvent e = new PacketReadEvent(s,packet);
		            	Bukkit.getPluginManager().callEvent(e);
		            	if(e.isCancelled())return;
		                super.write(channelHandlerContext, e.getPacket(), channelPromise);
		            }
		        };
		        ChannelPipeline pipeline = new NMSPlayer(s).getPlayerConnection().getNetworkManager().getChannel().pipeline();
		        pipeline.addBefore("packet_handler", s.getName(), channelDuplexHandler);
			}
		}
	}

	public static Economy economy;
	public static me.DevTec.TheVault.Economy tveeconomy;
	public static Bank bank;

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

	private void createConfig() {
		data.create();
		config.setHeader("TNT, Action types: WAIT/DROP");
		config.addDefault("Options.HideErrors", false); //hide only TheAPI errors
		config.addDefault("Options.AntiBot.Use", true);
		config.addDefault("Options.AntiBot.TimeBetweenPlayer", 20); //20 milis
		config.addDefault("Options.PacketListener", true);
		config.addDefault("Options.PacketsEnabled.Read", true);
		config.addDefault("Options.PacketsEnabled.Receive", true);
		config.addDefault("Options.Optimize.TNT.Use", true);
		config.addDefault("Options.Optimize.TNT.Particles.Use", false);
		config.addDefault("Options.Optimize.TNT.Particles.Type", "EXPLOSION_LARGE");
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
		config.addDefault("Words.Second", "s");
		config.addDefault("Words.Minute", "min");
		config.addDefault("Words.Hour", "h");
		config.addDefault("Words.Day", "d");
		config.addDefault("Words.Week", "w");
		config.addDefault("Words.Month", "mon");
		config.addDefault("Words.Year", "y");
		config.addDefault("Words.Century", "cen");
		config.addDefault("Words.Millenium", "mil");
		config.addDefault("Format.Mute", "&6You are muted for &c%reason%");
		config.addDefault("Format.TempMute", "&6You are muted for &c%reason% &6on &c%time%");
		config.addDefault("Format.Ban", "&6You are banned for &c%reason%");
		config.addDefault("Format.TempBan", "&6You are banned for &c%reason% &6on &c%time%");
		config.addDefault("Format.BanIP", "&6You are ip-banned for &c%reason%");
		config.addDefault("Format.TempBanIP", "&6You are temp ip-banned for &c%reason% &6on &c%time%");
		config.addDefault("Format.Broadcast.Mute", "&6Player &c%player% &6muted for &c%reason%");
		config.addDefault("Format.Broadcast.TempMute", "&6Player &c%player% &6muted for &c%reason% &6on &c%time%");
		config.addDefault("Format.Broadcast.Ban", "&6Player &c%player% &6banned for &c%reason%");
		config.addDefault("Format.Broadcast.TempBan", "&6Player &c%player% &6banned for &c%reason% &6on &c%time%");
		config.addDefault("Format.Broadcast.BanIP", "&6Player &c%target% &6ip-banned for &c%reason%");
		config.addDefault("Format.Broadcast.TempBanIP",
				"&6Player &c%target% &6temp ip-banned for &c%reason% &6on &c%time%");
		config.addDefault("Format.Broadcast.Mute-Permission", "TheAPI.Mute");
		config.addDefault("Format.Broadcast.TempMute-Permission", "TheAPI.TempMute");
		config.addDefault("Format.Broadcast.Ban-Permission", "TheAPI.Ban");
		config.addDefault("Format.Broadcast.TempBan-Permission", "TheAPI.TempBan");
		config.addDefault("Format.Broadcast.BanIP-Permission", "TheAPI.BanIP");
		config.addDefault("Format.Broadcast.TempBanIP-Permission", "TheAPI.TempBanIP");
		config.addDefault("Format.HelpOp", "&0[&4HelpOp&0] &c%sender%&8: &c%message%");
		config.addDefault("Format.HelpOp-Permission", "TheAPI.HelpOp");
		config.addDefault("Format.Report", "&0[&4Report&0] &c%sender% &6reported &c%reported% &6for &c%message%");
		config.addDefault("Format.Report-Permission", "TheAPI.Report");
		config.addDefault("GameAPI.StartingIn", "&aStarting in %time%s");
		config.addDefault("GameAPI.Start", "&aStart");
		config.create();
		gameapi.setCustomEnd("dat");
		gameapi.create();
		unused.setCustomEnd("dat");
		unused.create();
	}

	public void loadWorlds() {
		if (config.getConfig().getString("Worlds") != null) {
			if (config.getConfig().getStringList("Worlds").isEmpty() == false) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Action: &6Loading worlds.."));
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
				for (String s : config.getConfig().getStringList("Worlds")) {
					String type = "Default";
					for (String w : Arrays.asList("Default", "Normal", "Nether", "The_End", "End", "The_Void", "Void",
							"Empty", "Flat")) {
						if (config.getConfig().getString("WorldsSetting." + s) != null) {
							if (config.getConfig().getString("WorldsSetting." + s + ".Generator").equalsIgnoreCase(w)) {
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
					if (config.getConfig().getString("WorldsSetting." + s + ".GenerateStructures") != null)
						f = config.getConfig().getBoolean("WorldsSetting." + s + ".GenerateStructures");

					TheAPI.msg("&bTheAPI&7: &6Loading world with name '" + s + "'..", TheAPI.getConsole());
					TheAPI.getWorldsManager().create(s, env, wt, f, 0);
					TheAPI.msg("&bTheAPI&7: &6World with name '" + s + "' loaded.", TheAPI.getConsole());
				}
				TheAPI.msg("&bTheAPI&7: &6All worlds loaded.", TheAPI.getConsole());
			}
		}
	}

	@Override
	public void onDisable() {
		for(Player s : TheAPI.getOnlinePlayers()) {
			if (LoaderClass.config.getBoolean("Options.PacketListener")) {
				Channel channel = new NMSPlayer(s).getPlayerConnection()
						.getNetworkManager().getChannel();
				channel.eventLoop().submit(() -> {
					channel.pipeline().remove(s.getName());
					return null;
				});
			}
		}
		online=false;
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &cDisabling plugin, saving configs and stopping runnables..",
				TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		Scheduler.cancelAll();
		for (Player p : gui.keySet()) {
			gui.get(p).closeAndClear();
		}
		unused.delete();
		data.reload();
		config.reload();
		gameapi.reload();
	}
}
