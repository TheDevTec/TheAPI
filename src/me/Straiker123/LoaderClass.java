package me.Straiker123;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.DevTec.TheVault.Bank;
import me.DevTec.TheVault.TheVault;
import me.Straiker123.Scheduler.Scheduler;
import me.Straiker123.Scheduler.Task;
import me.Straiker123.Scheduler.Tasker;
import me.Straiker123.Utils.Events;
import me.Straiker123.Utils.GUIID;
import me.Straiker123.Utils.Tasks;
import me.Straiker123.Utils.TheAPICommand;
import net.milkbowl.vault.economy.Economy;

public class LoaderClass extends JavaPlugin {
	public static LoaderClass plugin;
	public HashMap<Integer, Task> scheduler = Maps.newHashMap();
	public static ArrayList<ConfigAPI> list = Lists.newArrayList();
	public static HashMap<Player, GUIID> gui = Maps.newHashMap();
	public static HashMap<String, Integer> GameAPI_Arenas = Maps.newHashMap();
	public static HashMap<String, Runnable> win_rewards = Maps.newHashMap();
	public static ArrayList<Integer> tasks = Lists.newArrayList();
	public static ConfigAPI unused;
	public static ConfigAPI config;
	public static ConfigAPI gameapi;
	public static ConfigAPI data;

	@Override
	public void onLoad() {
		unused = new ConfigAPI(this, "UnusedData");
		config = new ConfigAPI(this, "Config");
		gameapi = new ConfigAPI(this, "GameAPI");
		data = new ConfigAPI(this, "Data");
		plugin = this;
		createConfig();
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
		}.repeatingTimesAsync(0, 20, 30);
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
		}.repeatingTimesAsync(0, 20, 30);
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
	@SuppressWarnings("unchecked")
	public void onEnable() {
		Tasks.load();
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		Bukkit.getPluginCommand("TheAPI").setExecutor(new TheAPICommand());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &6Action: &aEnabling plugin, creating config and registering economy..",
				TheAPI.getConsole());
		TheAPI.msg("&bTheAPI&7: &8********************", TheAPI.getConsole());
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
		try {
			Method m = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
			Object o = m.invoke(null);
			for (Player p : o instanceof Collection ? (Collection<Player>) o : Arrays.asList((Player[]) o))
				a.add(p);
		} catch (Exception ex) {
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

	public static HashMap<Player, String> chatformat = new HashMap<Player, String>();

	private void createConfig() {
		data.create();
		config.setHeader("ChunkMobLimit -> OnLimitExceeded types: KILL/WARN\n"
				+ "TNT -> Action -> LowMememory types: WAIT/DROP\n" + "TNT -> Action -> LowTPS types: WAIT/DROP");
		config.addDefault("Options.HideErrors", false);
		config.addDefault("Options.AntiBot.Use", true);
		config.addDefault("Options.AntiBot.TimeBetweenPlayer", 3);

		config.addDefault("Options.PacketsEnabled.Read", true);
		config.addDefault("Options.PacketsEnabled.Receive", true);
		config.addDefault("Options.LagChecker.Enabled", true);
		config.addDefault("Options.LagChecker.Log", false);

		config.addDefault("Options.LagChecker.TNT.Use", true);

		config.addDefault("Options.LagChecker.TNT.Particles.Disable", true);
		config.addDefault("Options.LagChecker.TNT.Particles.Type", "EXPLOSION_LARGE");

		config.addDefault("Options.LagChecker.TNT.Drops.Allowed", true);
		config.addDefault("Options.LagChecker.TNT.Drops.InSingleLocation", true);
		config.addDefault("Options.LagChecker.TNT.Drops.InFirstTNTLocation", false);

		config.addDefault("Options.LagChecker.TNT.CollidingTNT.Disabled", false);
		config.addDefault("Options.LagChecker.TNT.Action.LowMememory", "WAIT");
		config.addDefault("Options.LagChecker.TNT.Action.LowTPS", "WAIT");
		config.addDefault("Options.LagChecker.TNT.CollidingTNT.IgniteTime", 5); // 0 is ultra fast, but with ultra lag
		config.addDefault("Options.LagChecker.TNT.SpawnTNT", false); // more friendly to server

		config.addDefault("Options.LagChecker.ChunkMobLimit.Use", true);
		config.addDefault("Options.LagChecker.ChunkMobLimit.Limit", 120);
		config.addDefault("Options.LagChecker.ChunkMobLimit.OnLimitExceeded", "WARN");
		config.addDefault("Options.LagChecker.ChunkMobLimit.Bypass", Arrays.asList("BEE", "ITEM_FRAME", "ARMOR_STAND",
				"VILLAGER", "TAMED_WOLF", "TAMED_CAT", "OCELOT", "PARROT", "DROPPED_ITEM"));
		config.addDefault("Options.LagChecker.ClearMemIfPercentIsFree", 25);
		config.addDefault("Options.LagChecker.Reflesh", "5min"); // 100÷20 = 5 -> reflesh every 5s
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

	public static List<Player> a = new ArrayList<Player>();
	public static int task = 1;
	public static boolean created;

	public static List<Player> getOnline() {
		return a;
	}
}
