package me.DevTec.TheAPI.Utils.TheAPIUtils.Command;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.TheAPI.TPSType;
import me.DevTec.TheAPI.APIs.MemoryAPI;
import me.DevTec.TheAPI.APIs.PluginManagerAPI;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.User;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Tasks;

public class TheAPICommand implements CommandExecutor, TabCompleter {

	private boolean perm(CommandSender s, String p) {
		if (s.hasPermission("TheAPI.Command." + p))
			return true;
		TheAPI.msg("&6You do not have permission '&eTheAPI.Command." + p + "&6' to do that!", s);
		return false;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			if (s.hasPermission("TheAPI.Command.Info"))
				TheAPI.msg("&e/TheAPI Info", s);
			if (s.hasPermission("TheAPI.Command.Invsee"))
				TheAPI.msg("&e/TheAPI Invsee", s);
			if (s.hasPermission("TheAPI.Command.Reload"))
				TheAPI.msg("&e/TheAPI Reload", s);
			if (s.hasPermission("TheAPI.Command.WorldsManager"))
				TheAPI.msg("&e/TheAPI WorldsManager", s);
			if (s.hasPermission("TheAPI.Command.ClearCache"))
				TheAPI.msg("&e/TheAPI ClearCache", s);
			if (s.hasPermission("TheAPI.Command.User"))
				TheAPI.msg("&e/TheAPI User", s);
			if (s.hasPermission("TheAPI.Command.PluginManager"))
				TheAPI.msg("&e/TheAPI PluginManager", s);
			if (!s.hasPermission("theapi.command.test"))
				TheAPI.msg("&e/TheAPI Test", s);
			TheAPI.msg("&0[&6TheAPI&0] &7&eCreated by DevTec, StraikerinaCZ", s);
			return true;
		}
		if (args[0].equalsIgnoreCase("user")) {
			if (!perm(s,"User"))
				return true;
			new TAC_User(s, args);
			return true;
		}
		if (args[0].equalsIgnoreCase("test")) {
			if (!perm(s,"Test"))
				return true;
			new TAC_Test(s, args);
			return true;
		}
		if (args[0].equalsIgnoreCase("PluginManager") || args[0].equalsIgnoreCase("pm") 
				|| args[0].equalsIgnoreCase("plugin") ||args[0].equalsIgnoreCase("pluginm")||args[0].equalsIgnoreCase("pmanager")) {
			if (!perm(s,"PluginManager"))return true;
			new TAC_PluginManager(s, args);
			return true;
		}
		if (args[0].equalsIgnoreCase("cc") || args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("clearcache")) {
			if (perm(s,"ClearCache")) {
				TheAPI.msg("&eClearing cache..", s);
				for (String p : LoaderClass.plugin.gui.keySet())
					LoaderClass.plugin.gui.get(p).clear();
				LoaderClass.plugin.gui.clear();
				TheAPI.clearCache();
				for(World w : Bukkit.getWorlds())
					for(Chunk c: w.getLoadedChunks())c.unload(true);
				TheAPI.msg("&eCache cleared.", s);
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("invsee")) {
			if (!s.hasPermission("TheAPI.Command.Invsee"))return true;
			if(args.length==1) {
				TheAPI.msg("&e/TheAPI Invsee <player>", s);
				return true;
			}
			Player p = TheAPI.getPlayer(args[1]);
			if(p!=null) {
			TheAPI.msg("&eOpening inventory of player "+p.getName()+"..", s);
			((Player)s).openInventory(p.getInventory());
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (perm(s,"Reload")) {
				TheAPI.msg("&eReloading configs..", s);
				LoaderClass.data.reload();
				LoaderClass.config.reload();
				for(User u : TheAPI.getCachedUsers())
					u.getData().reload(u.getData().getFile());
				Tasks.unload();
				Tasks.load();
				TheAPI.msg("&eConfigs reloaded.", s);
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("informations") || args[0].equalsIgnoreCase("info")) {
			if (perm(s,"Info")) {
				new Tasker() {
					public void run() {
						OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
						TheAPI.msg("&7Memory:", s);
						TheAPI.msg("&7 Max: &e"+MemoryAPI.getMaxMemory(), s);
						TheAPI.msg("&7 Used: &e"+MemoryAPI.getUsedMemory(false)+" &7(&e"+MemoryAPI.getUsedMemory(true)+"%&7)", s);
						TheAPI.msg("&7 Free: &e"+MemoryAPI.getFreeMemory(false)+" &7(&e"+MemoryAPI.getFreeMemory(true)+"%&7)", s);
						TheAPI.msg("&7Worlds:", s);
						for(World w : Bukkit.getWorlds())
							TheAPI.msg("&7 - &e"+w.getName()+" &7(Ent:&e"+w.getEntities().size()+"&7, Players:&e"+w.getPlayers().size()+"&7, Chunks:&e"+w.getLoadedChunks().length+"&7)", s);
						TheAPI.msg("&7Players:", s);
						TheAPI.msg("&7 Max: &e"+TheAPI.getMaxPlayers(), s);
						TheAPI.msg("&7 Online: &e"+(TheAPI.getOnlinePlayers().size()+" &7(&e"+((int)(TheAPI.getOnlinePlayers().size()/((double)TheAPI.getMaxPlayers()/100))))+"%&7)", s);
						TheAPI.msg("&7System:", s);
						TheAPI.msg("&7 CPU: &e"+String.format("%2.02f",TheAPI.getProcessCpuLoad()).replaceFirst(",", ".").replaceFirst("\\.00", "")+"%", s);
						TheAPI.msg("&7 Arch: &e"+osBean.getArch(), s);
						TheAPI.msg("&7 Name: &e"+osBean.getName(), s);
						TheAPI.msg("&7 Version: &e"+osBean.getVersion(), s);
						TheAPI.msg("&7 Procesors: &e"+osBean.getAvailableProcessors(), s);
						TheAPI.msg("&7Server:", s);
						TheAPI.msg("&7 File: &e"+System.getProperty("java.class.path"), s);
						TheAPI.msg("&7 Path: &e"+System.getProperty("user.dir"), s);
						TheAPI.msg("&7 UpTime: &e"+StringUtils.timeToString(TheAPI.getServerUpTime()/1000), s);
						TheAPI.msg("&7 Version: &e"+TheAPI.getServerVersion(), s);
						TheAPI.msg("&7 Startup-Cmd: &e"+System.getProperty("sun.java.command"), s);
						TheAPI.msg("&7TPS: &e"+TheAPI.getServerTPS(TPSType.ONE_MINUTE)+", "+TheAPI.getServerTPS(TPSType.FIVE_MINUTES)+", "+TheAPI.getServerTPS(TPSType.FIFTEEN_MINUTES), s);
						TheAPI.msg("&7Version: &ev" + LoaderClass.plugin.getDescription().getVersion(), s);
						List<Plugin> pl = LoaderClass.plugin.getTheAPIsPlugins();
						if (!pl.isEmpty()) {
							String sd = "";
							for (Plugin w :pl)
								sd+=(sd.equals("")?"":"&7, ")+TAC_PluginManager.getPlugin(w)+" &6v"+w.getDescription().getVersion();
							TheAPI.msg("&7Plugins using TheAPI ("+pl.size()+"): "+sd, s);
						}
					}
				}.runTask();
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("worldsmanager") || args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds") || args[0].equalsIgnoreCase("wm") || args[0].equalsIgnoreCase( "mw") || args[0].equalsIgnoreCase( "worldmanager")) {
			if (!perm(s,"WorldsManager"))
				return true;
			new TAC_Worlds(s, args);
		return true;
		}
		return true;
	}

	List<String> getWorlds() {
		List<String> list = new ArrayList<>();
		for (World w : Bukkit.getWorlds())
			list.add(w.getName());
		return list;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
		List<String> c = new ArrayList<>();
		if (args.length == 1) {
			if (s.hasPermission("TheAPI.Command.Info"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Arrays.asList("Info")));
			if (s.hasPermission("TheAPI.Command.Reload"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Arrays.asList("Reload")));
			if (s.hasPermission("TheAPI.Command.ClearCache"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Arrays.asList("ClearCache")));
			if (s.hasPermission("TheAPI.Command.WorldsManager"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Arrays.asList("WorldsManager")));
			if (s.hasPermission("TheAPI.Command.Invsee"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Arrays.asList("Invsee")));
			if (s.hasPermission("TheAPI.Command.PluginManager"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Arrays.asList("PluginManager")));
			if (s.hasPermission("TheAPI.Command.User"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Arrays.asList("User")));
			if (s.hasPermission("theapi.command.test"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Arrays.asList("Test")));
		}
		if (args[0].equalsIgnoreCase("Test") && s.hasPermission("theapi.command.test")) {
			if (args.length == 2) {
				c.addAll(StringUtils.copyPartialMatches(args[1], Arrays.asList("SortedMap", "GUI", "Other")));
			}
		}
		if (args[0].equalsIgnoreCase("User") && s.hasPermission("theapi.command.user")) {
			if (args.length == 2) {
				List<String> a = new ArrayList<>();
				for(Player p : TheAPI.getOnlinePlayers())a.add(p.getName());
				for(User u : TheAPI.getCachedUsers())if(a.size()>=150)break;else if(!a.contains(u.getName()))a.add(u.getName());
				c.addAll(StringUtils.copyPartialMatches(args[1], a));
			}
			if (args.length == 3) {
				c.addAll(StringUtils.copyPartialMatches(args[2],
						Arrays.asList("Set","Get","Keys","Exists","Reload")));
			}
			if (args.length == 4) {
				if(args[2].equalsIgnoreCase("set")||args[2].equalsIgnoreCase("get")||args[2].equalsIgnoreCase("keys")||args[2].equalsIgnoreCase("exists")) {
					c.addAll(StringUtils.copyPartialMatches(args[3], TheAPI.getUser(args[1]).getKeys(true)));
				}
			}
			if (args.length >= 5) {
				if(args[2].equalsIgnoreCase("set")) {
					c.addAll(StringUtils.copyPartialMatches(args[3], Arrays.asList("?")));
				}
			}
		}
		if (args[0].equalsIgnoreCase("Invsee") && s.hasPermission("TheAPI.Command.Invsee")) {
			if (args.length == 2)
			return null;
		}
		if (s.hasPermission("TheAPI.Command.PluginManager"))
		if (args[0].equalsIgnoreCase("PluginManager") || args[0].equalsIgnoreCase("pm")) {
			if (args.length == 2) {
				c.addAll(StringUtils.copyPartialMatches(args[1],
						Arrays.asList("Load", "Unload", "Reload", "Enable", "Disable", "Info", "Files", "DisableAll", "EnableAll", "ReloadAll", "UnloadAll", "LoadAll")));
			}
			if (args.length == 3) {
				if (args[1].equalsIgnoreCase("Load")) {
					c.addAll(StringUtils.copyPartialMatches(args[2],
							PluginManagerAPI.getPluginsToLoad()));
				}
				if (args[1].equalsIgnoreCase("Unload")||args[1].equalsIgnoreCase("Enable") || args[1].equalsIgnoreCase("Disable")
						 || args[1].equalsIgnoreCase("Info") || args[1].equalsIgnoreCase("Reload")) {
						c.addAll(StringUtils.copyPartialMatches(args[2],
								PluginManagerAPI.getPluginsNames()));
				}
			}}
		if (s.hasPermission("TheAPI.Command.WorldsManager"))
		if (args[0].equalsIgnoreCase("WorldsManager") || args[0].equalsIgnoreCase("wm")) {
			if (args.length == 2) {
				c.addAll(StringUtils.copyPartialMatches(args[1],
						Arrays.asList("Create", "Delete", "Load", "Teleport", "Unload", "Save", "SaveAll")));
			}
			if (args.length >= 3) {
				if (args[1].equalsIgnoreCase("Create") || args[1].equalsIgnoreCase("Load")) {
					if (args.length == 3)
						return Arrays.asList("?");
					if (args.length == 4)
						c.addAll(StringUtils.copyPartialMatches(args[3],
								Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat")));
				}
				if (args[1].equalsIgnoreCase("Teleport")) {
					if (args.length == 3)
						c.addAll(StringUtils.copyPartialMatches(args[1], getWorlds()));
					if (args.length == 4)
						return null;
				}
				if (args[1].equalsIgnoreCase("Unload") || args[1].equalsIgnoreCase("Delete")
						|| args[1].equalsIgnoreCase("Save")) {
					if (args.length == 3)
						c.addAll(StringUtils.copyPartialMatches(args[1], getWorlds()));
				}
			}
		}
		Collections.sort(c);
		return c;
	}

}
