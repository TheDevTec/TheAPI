package me.Straiker123;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.Straiker123.Events.EntityMoveEvent;
import me.Straiker123.Utils.TheAPICommand;
import me.Straiker123.Utils.Events;
import me.Straiker123.Utils.GUIID;
import net.milkbowl.vault.economy.Economy;

public class LoaderClass extends JavaPlugin {
	public static LoaderClass plugin;
	public static List<ConfigAPI> list = new ArrayList<ConfigAPI>();

	public static HashMap<Player, GUIID> gui = new HashMap<Player, GUIID>();
	//arena, int - runnable
	public static HashMap<String, Integer> GameAPI_Arenas = new HashMap<String, Integer>();
	public static HashMap<String, Integer> gameapi_timer = new HashMap<String, Integer>();
	public static HashMap<String, Runnable> win_rewards = new HashMap<String, Runnable>();

	public static ConfigAPI data;
	public static ConfigAPI config;
	public static ConfigAPI gameapi;
	
	public void onLoad() {
		plugin=this;
		createConfig();
		new TheAPI();
		new TimeConventorAPI();
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Action: &6Loading plugin.."));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
	}
	boolean hooked;
	int vaulthook;
	int times;
	public void vaultHooking() {
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Action: &6Looking for Vault Economy.."));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		vaulthook=Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if(getVaultEconomy()) {
					e=true;
					Bukkit.getScheduler().cancelTask(vaulthook);
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Found Vault Economy"));
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
					return;
				}
				++times;
				if(times==30) {
					Bukkit.getScheduler().cancelTask(vaulthook);
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cPlugin not found Vault Economy, disabling EconomyAPI.."));
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cYou can enable EconomyAPI by set Economy in EconomyAPI."));
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &c *TheAPI still works normally without any problems*"));
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
					return;
				}
			}
		}, 20, 40);
	}
	
	public void runnable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if(!config.getConfig().getBoolean("Options.EntityMoveEvent.Enabled"))return;
				for(World w: Bukkit.getWorlds()) {
					for(Entity e :w.getEntities()) {
						if(e.getType()==EntityType.PLAYER)continue;
						if(data.getConfig().getString("entities."+e)!=null) {
							Location old = (Location)data.getConfig().get("entities."+e);
							if(move(e,e.getLocation())) {
								EntityMoveEvent event = new EntityMoveEvent(e,old,e.getLocation());
								Bukkit.getPluginManager().callEvent(event);
								if(event.isCancelled())
									e.teleport(old);
							}
					}else
						data.getConfig().set("entities."+e,e.getLocation());
					}
				}
			}
		}, 3, config.getConfig().getInt("Options.EntityMoveEvent.Reflesh"));
	}
	private boolean move(Entity e,Location a) {
		if((Location)data.getConfig().get("entities."+e)==a) {
			return false;
		}
		data.getConfig().set("entities."+e,a);
			return true;
	}
	public boolean e;
	public String motd;
	public int max;
	public void onEnable() {
		createConfig();
		new TheAPI();
		new TimeConventorAPI();
		Bukkit.getPluginManager().registerEvents(new Events(), this);
		Bukkit.getPluginCommand("TheAPI").setExecutor(new TheAPICommand());
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Action: &aEnabling plugin, creating config and registering economy.."));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		
		if(TheAPI.getPluginsManagerAPI().getPlugin("Vault") == null) {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cPlugin not found Vault, EconomyAPI is disabled."));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cYou can enabled EconomyAPI by set custom Economy in EconomyAPI."));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &c *TheAPI will still normally work without problems*"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
			e=false;
		}else {
			vaultHooking();
		}
		new EconomyAPI();
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {

			@Override
			public void run() {
				if(TheAPI.getCountingAPI().getPluginsUsingTheAPI().size()==0)return;
				String end = "";
				if(TheAPI.getCountingAPI().getPluginsUsingTheAPI().size() !=1)end="s";
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &aTheAPI using "+TheAPI.getCountingAPI().getPluginsUsingTheAPI().size()+" plugin"+end));
			}
			
		}, 200);
		if(config.getConfig().getBoolean("Options.EntityMoveEvent.Enabled"))
		runnable();
		else {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6EntityMoveEvent is disabled."));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6You can enable EntityMoveEvent in Config.yml"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6 *TheAPI will still normally work without problems*"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));

		}
		if(!TheAPI.isNewVersion() && !TheAPI.getServerVersion().startsWith("v1_12")
				&& !TheAPI.getServerVersion().startsWith("v1_11")
				&& !TheAPI.getServerVersion().startsWith("v1_10")
				&& !TheAPI.getServerVersion().startsWith("v1_9")
				&& !TheAPI.getServerVersion().startsWith("v1_8")
				&& !TheAPI.getServerVersion().equals("v1_7_R4")) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

				@Override
				public void run() {
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Info: &cYour server version isn't supported!"));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
	}}, 20, 20*60*3600);
		}
	}
	
	public static Economy economy;
	private boolean getVaultEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	    if (economyProvider != null) {
	    	economy = economyProvider.getProvider();
	    	
	    }
		return economy != null;
	}
	
	public static HashMap<Player, String> chatformat = new HashMap<Player, String>();
	private void createConfig() {
		config = TheAPI.getConfig("TheAPI", "Config");
		config.addDefault("Options.HideErrors", false);
		config.addDefault("Options.EntityMoveEvent.Reflesh", 3);
		config.addDefault("Options.EntityMoveEvent.Enabled", true); //set false to disable this event
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
		config.addDefault("Format.Broadcast.TempBanIP", "&6Player &c%target% &6temp ip-banned for &c%reason% &6on &c%time%");
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
		gameapi=TheAPI.getConfig("TheAPI", "GameAPI");
		gameapi.setCustomEnd("dat");
		gameapi.create();
		data = TheAPI.getConfig("TheAPI", "Data");
		data.setCustomEnd("dat");
		data.create();
	}
	
	public void loadWorlds() {
		if(config.getConfig().getString("Worlds")!=null) {
			if(config.getConfig().getStringList("Worlds").isEmpty()==false) {
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Action: &6Loading worlds.."));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
				for(String s:config.getConfig().getStringList("Worlds")){
					String type="Default";
					for(String w:Arrays.asList("Default","Normal","Nether","The_End"
							,"End","The_Void","Void","Empty","Flat")) {
						if(config.getConfig().getString("WorldsSetting."+s)!=null) {
					if(config.getConfig().getString("WorldsSetting."+s+".Generator").equalsIgnoreCase(w)) {
						if(w.equals("Flat"))type="Flat";
						if(w.equals("Nether"))type="Nether";
						if(w.equals("The_End")||w.equals("End"))type="The_End";
						if(w.equals("The_Void")||w.equals("Void")||w.equals("Empty"))type="The_Void";
						break;
					}}else
					break;
					}
					Environment env = Environment.NORMAL;
					WorldType wt= WorldType.NORMAL;
					if(type.equals("Flat"))wt=WorldType.FLAT;
					if(type.equals("The_Void"))env=null;
					if(type.equals("The_End")) {
						try {
						env=Environment.valueOf("THE_END");
						}catch(Exception e) {
							env=Environment.valueOf("END");
						}
					}
					if(type.equals("Nether"))env=Environment.NETHER;
					boolean f=true;
					if(config.getConfig().getString("WorldsSetting."+s+".GenerateStructures")!=null)
						f=config.getConfig().getBoolean("WorldsSetting."+s+".GenerateStructures");

					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Loading world with name '"+s+"'.."));
					TheAPI.getWorldsManager().create(s, env, wt,f,0);
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6World with name '"+s+"' loaded."));
				}
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6All worlds loaded."));
			}
		}
	}
	
	public void onDisable() {
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &6Action: &cDisabling plugin and saving configs.."));
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &8********************"));
		for(Player p : gui.keySet()) {
			p.getOpenInventory().close();
			gui.get(p).clear();
		}
		data.getConfig().set("guis", null);
		data.getConfig().set("entities", null);
		for(ConfigAPI s:list) {
			s.save();
		}
	}
}
