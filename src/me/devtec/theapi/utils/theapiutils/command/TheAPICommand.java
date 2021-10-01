package me.devtec.theapi.utils.theapiutils.command;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
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

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.MemoryAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class TheAPICommand implements CommandExecutor, TabCompleter {
	  final String realVersion = (String)Ref.get(Bukkit.getServer(), "serverVersion");
	  final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
	  final RuntimeMXBean rr = ManagementFactory.getRuntimeMXBean();
	  final File d = new File("plugins");
	  
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
			if (s.hasPermission("TheAPI.Command.Reload"))
				TheAPI.msg("&e/TheAPI Reload", s);
			if (s.hasPermission("TheAPI.Command.WorldsManager"))
				TheAPI.msg("&e/TheAPI WorldsManager", s);
			if (s.hasPermission("TheAPI.Command.ClearCache"))
				TheAPI.msg("&e/TheAPI ClearCache", s);
			if (s.hasPermission("TheAPI.Command.User"))
				TheAPI.msg("&e/TheAPI User", s);
			TheAPI.msg("&0[&6TheAPI&0] &7&eCreated by DevTec, StraikerinaCZ", s);
			return true;
		}
		if (args[0].equalsIgnoreCase("user")) {
			if (!perm(s, "User"))
				return true;
			new TAC_User(s, args);
			return true;
		}
		if (args[0].equalsIgnoreCase("cc") || args[0].equalsIgnoreCase("clear")
				|| args[0].equalsIgnoreCase("clearcache")) {
			if (perm(s, "ClearCache")) {
				TheAPI.msg("&eClearing cache..", s);
				for (String p : LoaderClass.plugin.gui.keySet())
					LoaderClass.plugin.gui.get(p).clear();
				LoaderClass.plugin.gui.clear();
				TheAPI.clearCache();
				for (World w : Bukkit.getWorlds())
					for (Chunk c : w.getLoadedChunks())
						c.unload(true);
				TheAPI.msg("&eCache cleared.", s);
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (perm(s, "Reload")) {
				TheAPI.msg("&eReloading configs..", s);
				LoaderClass.plugin.reload();
				TheAPI.msg("&eConfigs reloaded.", s);
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("informations") || args[0].equalsIgnoreCase("info")) {
			if (!(s instanceof Player) || perm(s, "Info")) {
				new Tasker() {
		            public void run() {
		              String load = StringUtils.fixedFormatDouble(TheAPI.getProcessCpuLoad());
		              double[] tps = NMSAPI.getServerTPS();
		              double first = tps[0], second = tps[1], third = tps[2];
		              if (first > 20.0D)
		                first = 20.0D; 
		              if (second > 20.0D)
		                second = 20.0D; 
		              if (third > 20.0D)
		                third = 20.0D; 
		              String color = TheAPI.getRandomFromList(Arrays.asList("&b", "&a", "&c", "&d", "&5"));
		              String sd = " " + color + "* &7";
		              
		              TheAPI.msg(color + "» &7System Info:", s);
		              TheAPI.msg(sd + "Arch: &e"+osBean.getArch(), s);
		              TheAPI.msg(sd + "Name: &e"+osBean.getName(), s);
		              TheAPI.msg(sd + "Java: &e"+System.getProperty("java.version"), s);
		              TheAPI.msg(sd + "Version: &e"+osBean.getVersion(), s);
		              TheAPI.msg(sd + "Processors: &e"+osBean.getAvailableProcessors(), s);
		              TheAPI.msg(sd + "CPU load: &e"+load+"%", s);
		              TheAPI.msg(sd + "Memory:", s);
		              TheAPI.msg(sd + "  Free: &e"+ StringUtils.fixedFormatDouble(MemoryAPI.getFreeMemory(false))+"MB &7&o("+StringUtils.fixedFormatDouble(MemoryAPI.getFreeMemory(true))+"%)", s);
		              TheAPI.msg(sd + "  Used: &e"+ StringUtils.fixedFormatDouble(MemoryAPI.getUsedMemory(false))+"MB &7&o("+StringUtils.fixedFormatDouble(MemoryAPI.getUsedMemory(true))+"%)", s);
		              TheAPI.msg(sd + "  Total: &e"+StringUtils.fixedFormatDouble(MemoryAPI.getMaxMemory())+"MB", s);
		              TheAPI.msg(sd + "TPS:", s);
		              TheAPI.msg(sd + "  1 minute: &e"+StringUtils.fixedFormatDouble(first), s);
		              TheAPI.msg(sd + "  5 minutes: &e"+StringUtils.fixedFormatDouble(second), s);
		              TheAPI.msg(sd + "  15 minutes: &e"+StringUtils.fixedFormatDouble(third), s);
		              TheAPI.msg(sd + "Disk:", s);
		              TheAPI.msg(sd + "  Free: &e"+StringUtils.fixedFormatDouble((double)d.getTotalSpace() /1073741824)+"GB", s);
		              TheAPI.msg(sd + "  Used: &e"+StringUtils.fixedFormatDouble((double)(d.getTotalSpace()-d.getFreeSpace()) /1073741824)+"GB", s);
		              if(d.getFreeSpace()!=d.getUsableSpace())
		              TheAPI.msg(sd + "  Usable: &e"+StringUtils.fixedFormatDouble((double)d.getUsableSpace() /1073741824)+"GB", s);
		              TheAPI.msg(sd + "  Total: &e"+StringUtils.fixedFormatDouble((double)d.getTotalSpace() /1073741824)+"GB", s);
		              TheAPI.msg(color + "» &7Server Status:", s);
		              TheAPI.msg(sd + "  File: &e"+System.getProperty("java.class.path"), s);
		              TheAPI.msg(sd + "  Path: &e"+System.getProperty("user.dir"), s);
		              TheAPI.msg(sd + "  UpTime: &e"+StringUtils.timeToString(TheAPI.getServerUpTime() / 1000L), s);
		              TheAPI.msg(sd + "  Version: &e"+realVersion + " &7&o(" + TheAPI.getServerVersion() + ")", s);
		              TheAPI.msg(sd + "  Startup-Cmd: &e"+StringUtils.join(TheAPICommand.this.rr.getInputArguments(), " "), s);
		              TheAPI.msg(sd + "» &7Plugin Version: &e"+ LoaderClass.plugin.getDescription().getVersion(), s);
		            }
		          }.runTask();
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("worldsmanager") || args[0].equalsIgnoreCase("world")
				|| args[0].equalsIgnoreCase("worlds") || args[0].equalsIgnoreCase("wm")
				|| args[0].equalsIgnoreCase("mw") || args[0].equalsIgnoreCase("worldmanager")) {
			if (!perm(s, "WorldsManager"))
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
				c.addAll(StringUtils.copyPartialMatches(args[0], Collections.singletonList("Info")));
			if (s.hasPermission("TheAPI.Command.Reload"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Collections.singletonList("Reload")));
			if (s.hasPermission("TheAPI.Command.ClearCache"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Collections.singletonList("ClearCache")));
			if (s.hasPermission("TheAPI.Command.WorldsManager"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Collections.singletonList("WorldsManager")));
			if (s.hasPermission("TheAPI.Command.User"))
				c.addAll(StringUtils.copyPartialMatches(args[0], Collections.singletonList("User")));
		}
		if (args[0].equalsIgnoreCase("User") && s.hasPermission("theapi.command.user")) {
			if (args.length == 2) {
				return null;
			}
			if (args.length == 3) {
				c.addAll(StringUtils.copyPartialMatches(args[2],
						Arrays.asList("Set", "Get", "Keys", "Remove", "Exists", "Reload")));
			}
			if (args.length == 4) {
				if (args[2].equalsIgnoreCase("set") || args[2].equalsIgnoreCase("get")
						|| args[2].equalsIgnoreCase("keys") || args[2].equalsIgnoreCase("exists") || args[2].equalsIgnoreCase("remove")) {
					c.addAll(StringUtils.copyPartialMatches(args[3], TheAPI.getUser(args[1]).getKeys(true)));
				}
			}
			if (args.length >= 5) {
				if (args[2].equalsIgnoreCase("set")) {
					c.addAll(StringUtils.copyPartialMatches(args[3], Collections.singletonList("?")));
				}
			}
		}
		if (s.hasPermission("TheAPI.Command.WorldsManager"))
			if (args[0].equalsIgnoreCase("WorldsManager") || args[0].equalsIgnoreCase("mw") || args[0].equalsIgnoreCase("mv") || args[0].equalsIgnoreCase("wm")) {
				if (args.length == 2) {
					c.addAll(StringUtils.copyPartialMatches(args[1],
							Arrays.asList("Create", "Delete", "Load", "Teleport", "Unload", "Save", "SaveAll")));
				}
				if (args.length >= 3) {
					if (args[1].equalsIgnoreCase("Create") || args[1].equalsIgnoreCase("Load")) {
						if (args.length == 3)
							return Collections.singletonList("?");
						if (args.length == 4)
							c.addAll(StringUtils.copyPartialMatches(args[3],
									Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat")));
					}
					if (args[1].equalsIgnoreCase("Teleport")||args[1].equalsIgnoreCase("tp")) {
						if (args.length == 3)
							c.addAll(StringUtils.copyPartialMatches(args[2], getWorlds()));
						if (args.length == 4)
							return null;
					}
					if (args[1].equalsIgnoreCase("Unload") || args[1].equalsIgnoreCase("Delete")
							|| args[1].equalsIgnoreCase("Save")) {
						if (args.length == 3)
							c.addAll(StringUtils.copyPartialMatches(args[2], getWorlds()));
					}
				}
			}
		return c;
	}

}
