package me.devtec.theapi.utils.theapiutils.command;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.MemoryAPI;
import me.devtec.theapi.apis.PluginManagerAPI;
import me.devtec.theapi.apis.PluginManagerAPI.SearchType;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.HoverMessage;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.Tasks;

public class TheAPICommand implements CommandExecutor, TabCompleter {
	  String realVersion = (String)Ref.invoke(Ref.get(Bukkit.getServer(), "console"), "getVersion");
	  OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
	  RuntimeMXBean rr = ManagementFactory.getRuntimeMXBean();
	  File d = new File("plugins");
	  
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
			if (s.hasPermission("theapi.command.test"))
				TheAPI.msg("&e/TheAPI Test", s);
			TheAPI.msg("&0[&6TheAPI&0] &7&eCreated by DevTec, StraikerinaCZ", s);
			return true;
		}
		if (args[0].equalsIgnoreCase("user")) {
			if (!perm(s, "User"))
				return true;
			new TAC_User(s, args);
			return true;
		}
		if (args[0].equalsIgnoreCase("test")) {
			if (!perm(s, "Test"))
				return true;
			new TAC_Test(s, args);
			return true;
		}
		if (args[0].equalsIgnoreCase("PluginManager") || args[0].equalsIgnoreCase("pm")
				|| args[0].equalsIgnoreCase("plugin") || args[0].equalsIgnoreCase("pluginm")
				|| args[0].equalsIgnoreCase("pmanager")) {
			if (!perm(s, "PluginManager"))
				return true;
			new TAC_PluginManager(s, args);
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
		if (args[0].equalsIgnoreCase("invsee")) {
			if (!s.hasPermission("TheAPI.Command.Invsee"))
				return true;
			if (args.length == 1) {
				TheAPI.msg("&e/TheAPI Invsee <player>", s);
				return true;
			}
			Player p = TheAPI.getPlayer(args[1]);
			if (p != null) {
				TheAPI.msg("&eOpening inventory of player " + p.getName() + "..", s);
				((Player) s).openInventory(p.getInventory());
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (perm(s, "Reload")) {
				TheAPI.msg("&eReloading configs..", s);
				LoaderClass.data.reload();
				LoaderClass.config.reload();
				if (TheAPI.isNewerThan(15)) {
					LoaderClass.tags.reload();
					LoaderClass.tags.addDefault("TagPrefix", "!");
					LoaderClass.tags.addDefault("GradientPrefix", "!");
					if (!LoaderClass.tags.exists("Tags")) {
						LoaderClass.tags.addDefault("Tags.baby_blue", "0fd2f6");
						LoaderClass.tags.addDefault("Tags.beige", "ffc8a9");
						LoaderClass.tags.addDefault("Tags.blush", "e69296");
						LoaderClass.tags.addDefault("Tags.amaranth", "e52b50");
						LoaderClass.tags.addDefault("Tags.brown", "964b00");
						LoaderClass.tags.addDefault("Tags.crimson", "dc143c");
						LoaderClass.tags.addDefault("Tags.dandelion", "ffc31c");
						LoaderClass.tags.addDefault("Tags.eggshell", "f0ecc7");
						LoaderClass.tags.addDefault("Tags.fire", "ff0000");
						LoaderClass.tags.addDefault("Tags.ice", "bddeec");
						LoaderClass.tags.addDefault("Tags.indigo", "726eff");
						LoaderClass.tags.addDefault("Tags.lavender", "4b0082");
						LoaderClass.tags.addDefault("Tags.leaf", "618a3d");
						LoaderClass.tags.addDefault("Tags.lilac", "c8a2c8");
						LoaderClass.tags.addDefault("Tags.lime", "b7ff00");
						LoaderClass.tags.addDefault("Tags.midnight", "007bff");
						LoaderClass.tags.addDefault("Tags.mint", "50c878");
						LoaderClass.tags.addDefault("Tags.olive", "929d40");
						LoaderClass.tags.addDefault("Tags.royal_purple", "7851a9");
						LoaderClass.tags.addDefault("Tags.rust", "b45019");
						LoaderClass.tags.addDefault("Tags.sky", "00c8ff");
						LoaderClass.tags.addDefault("Tags.smoke", "708c98");
						LoaderClass.tags.addDefault("Tags.tangerine", "ef8e38");
						LoaderClass.tags.addDefault("Tags.violet", "9c6eff");
					}
					LoaderClass.tags.save();
					LoaderClass.tagG = LoaderClass.tags.getString("TagPrefix");
					LoaderClass.gradientTag = LoaderClass.tags.getString("GradientPrefix");
					LoaderClass.colorMap.clear();
					for (String tag : LoaderClass.tags.getKeys("Tags"))
						LoaderClass.colorMap.put(tag.toLowerCase(), "#" + LoaderClass.tags.getString("Tags." + tag));
					StringUtils.gradientFinder=Pattern.compile(LoaderClass.gradientTag+"(#[A-Fa-f0-9]{6})(.*?)"+LoaderClass.gradientTag+"(#[A-Fa-f0-9]{6})|.*?(?=(?:"+LoaderClass.gradientTag+"#[A-Fa-f0-9]{6}.*?"+LoaderClass.gradientTag+"#[A-Fa-f0-9]{6}))");
				}
				for (User u : TheAPI.getCachedUsers())
					u.getData().reload(u.getData().getFile());
				Tasks.unload();
				Tasks.load();
				TheAPI.msg("&eConfigs reloaded.", s);
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("informations") || args[0].equalsIgnoreCase("info")) {
			if (perm(s, "Info")) {
				
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
		              List<Plugin> pl = LoaderClass.plugin.getTheAPIsPlugins();
		              if (!pl.isEmpty())
		                if (s instanceof Player) {
		                  int c = 0;
		                  HoverMessage sdf = new HoverMessage(StringUtils.colorize(color + "» &7Plugins using TheAPI (" + pl.size() + "): "));
		                  for (Plugin w : pl) {
		                	  if(c++ != 0)
		                		  sdf.addText(", ").setColor("GRAY");
		                    sdf.addText(StringUtils.colorize(TAC_PluginManager.getPlugin(w))).setHoverEvent(StringUtils.colorize(
		                        "&7Author: &e"+ StringUtils.join(w.getDescription().getAuthors(), ", ") + "\n" + 
		                        		(w.getDescription().getWebsite() != null ?
		                        "&7Website: &e"+ w.getDescription().getWebsite()+ "\n":"")
		                        +"&7Version: &e"+ w.getDescription().getVersion()));
		                    if (w.getDescription().getWebsite() != null)
		                      sdf.setClickEvent(HoverMessage.ClickAction.OPEN_URL, w.getDescription().getWebsite()); 
		                  }
		                  sdf.send((Player)s);
		                } else {
		                  String sdf = "";
		                  for (Plugin w : pl)
		                    sdf += (sdf.equals("") ? "" : "&7, ") + TAC_PluginManager.getPlugin(w) + " &ev" + 
		                      w.getDescription().getVersion(); 
		                  TheAPI.msg(color+"» &7Plugins using TheAPI (" + pl.size() + "): " + sdf, s);
		                }
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
				for (Player p : TheAPI.getOnlinePlayers())
					a.add(p.getName());
				c.addAll(StringUtils.copyPartialMatches(args[1], a));
			}
			if (args.length == 3) {
				c.addAll(StringUtils.copyPartialMatches(args[2],
						Arrays.asList("Set", "Get", "Keys", "Exists", "Reload")));
			}
			if (args.length == 4) {
				if (args[2].equalsIgnoreCase("set") || args[2].equalsIgnoreCase("get")
						|| args[2].equalsIgnoreCase("keys") || args[2].equalsIgnoreCase("exists")) {
					c.addAll(StringUtils.copyPartialMatches(args[3], TheAPI.getUser(args[1]).getKeys(true)));
				}
			}
			if (args.length >= 5) {
				if (args[2].equalsIgnoreCase("set")) {
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
							Arrays.asList("Load", "Unload", "Reload", "Enable", "Disable", "Info", "Files",
									"DisableAll", "EnableAll", "ReloadAll", "UnloadAll", "LoadAll")));
				}
				if (args.length == 3) {
					if (args[1].equalsIgnoreCase("Load")) {
						c.addAll(StringUtils.copyPartialMatches(args[2], PluginManagerAPI.getPluginsToLoad(SearchType.PLUGIN_NAME)));
					}
					if (args[1].equalsIgnoreCase("Unload") || args[1].equalsIgnoreCase("Enable")
							|| args[1].equalsIgnoreCase("Disable") || args[1].equalsIgnoreCase("Info")
							|| args[1].equalsIgnoreCase("Reload")) {
						c.addAll(StringUtils.copyPartialMatches(args[2], PluginManagerAPI.getPluginsNames()));
					}
				}
			}
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
