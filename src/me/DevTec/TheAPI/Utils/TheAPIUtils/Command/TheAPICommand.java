package me.DevTec.TheAPI.Utils.TheAPIUtils.Command;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
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
import org.bukkit.util.StringUtil;

import com.google.common.collect.Lists;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.TheAPI.TPSType;
import me.DevTec.TheAPI.APIs.MemoryAPI;
import me.DevTec.TheAPI.APIs.PluginManagerAPI;
import me.DevTec.TheAPI.Scheduler.Tasker;
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
			if (perm(s,"User"))
				return true;
			new TAC_User(s, args);
			return true;
		}
		if (args[0].equalsIgnoreCase("test")) {
			if (perm(s,"Test"))
				return true;
			new TAC_Test(s, args);
			return true;
		}
		if (args[0].equalsIgnoreCase("PluginManager") || args[0].equalsIgnoreCase("pm") 
				|| args[0].equalsIgnoreCase("plugin") ||args[0].equalsIgnoreCase("pluginm")||args[0].equalsIgnoreCase("pmanager")) {
			if (perm(s,"PluginManager"))return true;
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
						TheAPI.msg("&7╔═════════════════════════════", s);
						TheAPI.msg("&7║ Memory:", s);
						TheAPI.msg("&7║  Max: &e"+MemoryAPI.getMaxMemory(), s);
						TheAPI.msg("&7║  Used: &e"+MemoryAPI.getUsedMemory(false)+" &7(&e"+MemoryAPI.getUsedMemory(true)+"%&7)", s);
						TheAPI.msg("&7║  Free: &e"+MemoryAPI.getFreeMemory(false)+" &7(&e"+MemoryAPI.getFreeMemory(true)+"%&7)", s);
						TheAPI.msg("&7║ Worlds:", s);
						for(World w : Bukkit.getWorlds())
							TheAPI.msg("&7║  - &e"+w.getName()+" &7(Ent:&e"+w.getEntities().size()+"&7, Players:&e"+w.getPlayers().size()+"&7, Chunks:&e"+w.getLoadedChunks().length+"&7)", s);
						TheAPI.msg("&7║ Players:", s);
						TheAPI.msg("&7║  Max: &e"+TheAPI.getMaxPlayers(), s);
						TheAPI.msg("&7║  Online: &e"+TheAPI.getOnlinePlayers().size()+" &7(&e"+(TheAPI.getOnlinePlayers().size()/((double)TheAPI.getMaxPlayers()/100))+"%&7)", s);
						OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
						TheAPI.msg("&7║ System:", s);
						TheAPI.msg("&7║  CPU: &e"+String.format("%2.02f",TheAPI.getProcessCpuLoad()).replaceFirst(",", ".").replaceFirst("\\.00", "")+"%", s);
						TheAPI.msg("&7║  Name: &e"+osBean.getName(), s);
						TheAPI.msg("&7║  Procesors: &e"+osBean.getAvailableProcessors(), s);
						TheAPI.msg("&7║ TPS: &e"+TheAPI.getServerTPS(TPSType.ONE_MINUTE)+", "+TheAPI.getServerTPS(TPSType.FIVE_MINUTES)+", "+TheAPI.getServerTPS(TPSType.FIFTEEN_MINUTES), s);
						TheAPI.msg("&7║ Version: &ev" + LoaderClass.plugin.getDescription().getVersion(), s);
						if (LoaderClass.plugin.getTheAPIsPlugins().size() != 0) {
							TheAPI.msg("&7║ Plugins using TheAPI:", s);
							for (Plugin a : LoaderClass.plugin.getTheAPIsPlugins())
								TheAPI.msg("&7║  - &e" + TAC_PluginManager.getPlugin(a), s);
						}
						TheAPI.msg("&7╚═════════════════════════════", s);
					}
				}.runAsync();
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("worldsmanager") || args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds") || args[0].equalsIgnoreCase("wm") || args[0].equalsIgnoreCase( "mw") || args[0].equalsIgnoreCase( "worldmanager")) {
			if (perm(s,"WorldsManager"))
				return true;
			new TAC_Worlds(s, args);
		return true;
		}
		return true;
	}

	List<String> getWorlds() {
		List<String> list = Lists.newArrayList();
		for (World w : Bukkit.getWorlds())
			list.add(w.getName());
		return list;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
		List<String> c = Lists.newArrayList();
		if (args.length == 1) {
			if (s.hasPermission("TheAPI.Command.Info"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Info"), Lists.newArrayList()));
			if (s.hasPermission("TheAPI.Command.Reload"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Reload"), Lists.newArrayList()));
			if (s.hasPermission("TheAPI.Command.ClearCache"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("ClearCache"), Lists.newArrayList()));
			if (s.hasPermission("TheAPI.Command.WorldsManager"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("WorldsManager"), Lists.newArrayList()));
			if (s.hasPermission("TheAPI.Command.Invsee"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Invsee"), Lists.newArrayList()));
			if (s.hasPermission("TheAPI.Command.PluginManager"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("PluginManager"), Lists.newArrayList()));
			if (s.hasPermission("theapi.command.test"))
				c.addAll(StringUtil.copyPartialMatches(args[0], Arrays.asList("Test"), Lists.newArrayList()));
		}
		if (args[0].equalsIgnoreCase("Test") && s.hasPermission("theapi.command.test")) {
			if (args.length == 2) {
				c.addAll(StringUtil.copyPartialMatches(args[1],
						Arrays.asList("SortedMap", "GUI", "Other"),
						Lists.newArrayList()));
			}
		}
		if (args[0].equalsIgnoreCase("Invsee") && s.hasPermission("TheAPI.Command.Invsee")) {
			if (args.length == 2)
			return null;
		}
		if (s.hasPermission("TheAPI.Command.PluginManager"))
		if (args[0].equalsIgnoreCase("PluginManager") || args[0].equalsIgnoreCase("pm")) {
			if (args.length == 2) {
				c.addAll(StringUtil.copyPartialMatches(args[1],
						Arrays.asList("Load", "Unload", "Reload", "Enable", "Disable", "Info", "Files", "DisableAll", "EnableAll", "ReloadAll", "UnloadAll", "LoadAll"),
						Lists.newArrayList()));
			}
			if (args.length == 3) {
				if (args[1].equalsIgnoreCase("Load")) {
					c.addAll(StringUtil.copyPartialMatches(args[2],
							PluginManagerAPI.getPluginsToLoad(), Lists.newArrayList()));
				}
				if (args[1].equalsIgnoreCase("Unload")||args[1].equalsIgnoreCase("Enable") || args[1].equalsIgnoreCase("Disable")
						 || args[1].equalsIgnoreCase("Info") || args[1].equalsIgnoreCase("Reload")) {
						c.addAll(StringUtil.copyPartialMatches(args[2],
								PluginManagerAPI.getPluginsNames(), Lists.newArrayList()));
				}
			}}
		if (s.hasPermission("TheAPI.Command.WorldsManager"))
		if (args[0].equalsIgnoreCase("WorldsManager") || args[0].equalsIgnoreCase("wm")) {
			if (args.length == 2) {
				c.addAll(StringUtil.copyPartialMatches(args[1],
						Arrays.asList("Create", "Delete", "Load", "Teleport", "Unload", "Save", "SaveAll"),
						Lists.newArrayList()));
			}
			if (args.length >= 3) {
				if (args[1].equalsIgnoreCase("Create") || args[1].equalsIgnoreCase("Load")) {
					if (args.length == 3)
						return Arrays.asList("?");
					if (args.length == 4)
						c.addAll(StringUtil.copyPartialMatches(args[3],
								Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat"), Lists.newArrayList()));
				}
				if (args[1].equalsIgnoreCase("Teleport")) {
					if (args.length == 3)
						c.addAll(StringUtil.copyPartialMatches(args[1], getWorlds(), Lists.newArrayList()));
					if (args.length == 4)
						return null;
				}
				if (args[1].equalsIgnoreCase("Unload") || args[1].equalsIgnoreCase("Delete")
						|| args[1].equalsIgnoreCase("Save")) {
					if (args.length == 3)
						c.addAll(StringUtil.copyPartialMatches(args[1], getWorlds(), Lists.newArrayList()));
				}
			}
		}
		return c;
	}

}
