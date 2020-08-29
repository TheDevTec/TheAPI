package me.DevTec.TheAPI.Utils.TheAPIUtils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;

import com.google.common.collect.Lists;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.TheAPI.TPSType;
import me.DevTec.TheAPI.APIs.ItemCreatorAPI;
import me.DevTec.TheAPI.APIs.MemoryAPI;
import me.DevTec.TheAPI.APIs.PluginManagerAPI;
import me.DevTec.TheAPI.GUIAPI.GUI;
import me.DevTec.TheAPI.GUIAPI.ItemGUI;
import me.DevTec.TheAPI.PlaceholderAPI.ThePlaceholderAPI;
import me.DevTec.TheAPI.Scheduler.Tasker;
import me.DevTec.TheAPI.SortedMap.RankingAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.WorldsAPI.WorldsAPI;

public class TheAPICommand implements CommandExecutor, TabCompleter {

	private String getPlugin(Plugin a) {
		if (a.isEnabled())
			return "&a" + a.getName();
		return "&c" + a.getName();
	}

	private boolean perm(CommandSender s, String p) {
		if (s.hasPermission("TheAPI.Command." + p))
			return true;
		TheAPI.msg("&6You do not have permission '&eTheAPI.Command." + p + "&6' to do that!", s);
		return false;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			TheAPI.msg("&7-----------------", s);
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
			if (s.hasPermission("TheAPI.Command.PluginManager"))
				TheAPI.msg("&e/TheAPI PluginManager", s);
			if (!s.hasPermission("theapi.command.test"))
				TheAPI.msg("&e/TheAPI Test", s);
			TheAPI.msg("&0[&6TheAPI&0] &7&eCreated by DevTec, StraikerinaCZ", s);
			TheAPI.msg("&7-----------------", s);
			return true;
		}
		if (args[0].equalsIgnoreCase("test")) {
			if (!s.hasPermission("theapi.command.test"))
				return true;
			if (args.length == 1) {
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&e/TheAPI Test SortedMap", s);
				TheAPI.msg("&e/TheAPI Test GUI", s);
				TheAPI.msg("&e/TheAPI Test Other - DevTec currently testing", s);
				TheAPI.msg("&7-----------------", s);
				return true;
			}
			if (args[1].equalsIgnoreCase("Other")) {
			TheAPI.msg("&eThePlaceholderAPII:", s);
			TheAPI.msg("&6- %player_health% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%player_health%"), s);
			TheAPI.msg("&6- %player_statistic_kills% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%player_statistic_kills%"), s);
			TheAPI.msg("&6- %server_time% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%server_time%"), s);
			TheAPI.msg("&6- %server_online% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%server_online%"), s);
			TheAPI.msg(" &eMath:", s);
			TheAPI.msg("&6- %math{5*4+6}% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%math{5*4+6}%"), s);
			TheAPI.msg("&6- %math{5.9*4.4+6.448}% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%math{5.9*4.4+6.448}%"), s);
			TheAPI.msg("&6- %math{3*%math{2*4}%+1}% -> "+ThePlaceholderAPI.setPlaceholders((Player)s, "%math{3*%math{2*4}%+1}%"), s);
			return true;
			}
			if(s instanceof Player) {
			Player p = (Player) s;
			if (args[1].equalsIgnoreCase("GUI")) {
				GUI gui = new GUI("&eTheAPI v" + PluginManagerAPI.getVersion("TheAPI"), 54, p) {
			
				@Override
				public void onClose(Player player) {
					TheAPI.msg("&0[&cTheAPI&0] &eClosed example gui!", player);
				}
				
			};
			Material a = Material.matchMaterial("BLACK_STAINED_GLASS_PANE")!=null?Material.matchMaterial("BLACK_STAINED_GLASS_PANE"):Material.matchMaterial("STAINED_GLASS_PANE");
			ItemGUI item = new ItemGUI(a.name().equals("BLACK_STAINED_GLASS_PANE")?ItemCreatorAPI.create(a, 1, "&7"):ItemCreatorAPI.create(a, 1, "&7", 15)) {
				
				@Override
				public void onClick(Player player, GUI gui, ClickType click) {
					
				}
			};
			for (int i = 0; i < 10; ++i)
				gui.setItem(i, item);
			gui.setItem(17, item);
			gui.setItem(18, item);
			gui.setItem(26, item);
			gui.setItem(27, item);
			gui.setItem(35, item);
			gui.setItem(36, item);
			for (int i = 44; i < 54; ++i)
				gui.setItem(i, item);
			
			gui.setItem(20, new ItemGUI(ItemCreatorAPI.create(Material.DIAMOND, 1, "&eWho created TheAPI?", 
					Arrays.asList("", "  &e» &7Creator of TheAPI is StraikerinaCZ", "  &e» &7Owner of TheAPI is DevTec"))) {
				
				@Override
				public void onClick(Player player, GUI gui, ClickType click) {
					TheAPI.msg("&0[&cTheAPI&0] &eWho created TheAPI?", player);
					TheAPI.msg("  &e» &7Creator of TheAPI is StraikerinaCZ", player);
					TheAPI.msg("  &e» &7Owner of TheAPI is DevTec", player);
				}
			});
			
			gui.setItem(22, new ItemGUI(ItemCreatorAPI.create(Material.EMERALD, 1, "&eWhere report bug?", 
					Arrays.asList("", "  &e» &7On our discord or github", "  &e» &7Discord: https://discord.gg/z4kK66g", "  &e» &7Github: https://github.com/TheDevTec/TheAPI"))) {
				
				@Override
				public void onClick(Player player, GUI gui, ClickType click) {
					TheAPI.msg("&0[&cTheAPI&0] &eWhere report bug?", player);
					TheAPI.msg("  &e» &7On our discord or github", player);
					TheAPI.msg("  &e» &7Discord: https://discord.gg/z4kK66g", player);
					TheAPI.msg("  &e» &7Github: https://github.com/TheDevTec/TheAPI", player);
				}
			});
			
			gui.setItem(24, new ItemGUI(ItemCreatorAPI.create(Material.GOLD_INGOT, 1, "&eAre somewhere examples of GUIs?", 
					Arrays.asList("", "  &e» &7GUI Slots: https://i.imgur.com/f43qxux.png", "  &e» &7GUI #1: https://pastebin.com/PGPwKxRz"))) {
				
				@Override
				public void onClick(Player player, GUI gui, ClickType click) {
					TheAPI.msg("&0[&cTheAPI&0] &eAre somewhere examples of GUIs?", player);
					TheAPI.msg("  &e» &7GUI Slots: https://i.imgur.com/f43qxux.png", player);
					TheAPI.msg("  &e» &7GUI #1: https://pastebin.com/PGPwKxRz", player);
				}
			});
			
			gui.setItem(49, new ItemGUI(ItemCreatorAPI.create(Material.getMaterial("BARRIER")==null?Material.getMaterial("BEDROCK"):Material.getMaterial("BARRIER"), 1, "&cClose")) {
				
				@Override
				public void onClick(Player player, GUI gui, ClickType click) {
					gui.close(player);
				}
			});
				return true;
			}}
			if (args[1].equalsIgnoreCase("SortedMap")) {
				HashMap<String, Double> Comparable = new HashMap<>();
				TheAPI.msg("&eInput:", s);
				TheAPI.msg("&6- A, 50.0", s);
				TheAPI.msg("&6- D, 5431.6", s);
				TheAPI.msg("&6- C, 886.5", s);
				TheAPI.msg("&6- G, 53.11", s);
				Comparable.put("A", 50.0);
				Comparable.put("D", 5431.6);
				Comparable.put("C", 886.5);
				Comparable.put("G", 53.11);
				RankingAPI<String, Double> map = new RankingAPI<>(Comparable);
				TheAPI.msg("&eResult:", s);
				for (Entry<String, Double> entry : map.entrySet())
					TheAPI.msg("&6" + map.getPosition(entry.getKey()) + ". " + entry.getKey() + ", "
							+entry.getValue(), s);
				HashMap<String, String> tops = new HashMap<>();
				TheAPI.msg("&eInput:", s);
				TheAPI.msg("&6- A, ABD", s); //1
				TheAPI.msg("&6- B, VGR", s); //4
				TheAPI.msg("&6- C, BTW", s); //2
				TheAPI.msg("&6- D, OAW", s); //3
				tops.put("A", "ABD");
				tops.put("B", "VGR");
				tops.put("C", "BTW");
				tops.put("D", "OAW");
				RankingAPI<String, String> maps = new RankingAPI<>(tops);
				TheAPI.msg("&eResult:", s);
				for (Entry<String, String> entry : maps.entrySet())
					TheAPI.msg("&6" + maps.getPosition(entry.getKey()) + ". " + entry.getKey() + ", "
							+entry.getValue(), s);
				return true;
			}
		}
		if (args[0].equalsIgnoreCase("PluginManager") || args[0].equalsIgnoreCase("pm") 
				|| args[0].equalsIgnoreCase("plugin") ||args[0].equalsIgnoreCase("pluginm")||args[0].equalsIgnoreCase("pmanager")) {
			if (perm(s,"PluginManager")) {
				if (args.length == 1) {
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&e/TheAPI PluginManager Enable <plugin>", s);
					TheAPI.msg("&e/TheAPI PluginManager Disable <plugin>", s);
					TheAPI.msg("&e/TheAPI PluginManager Load <plugin>", s);
					TheAPI.msg("&e/TheAPI PluginManager Unload <plugin>", s);
					TheAPI.msg("&e/TheAPI PluginManager Reload <plugin>", s);
					TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
					TheAPI.msg("&e/TheAPI PluginManager EnableAll", s);
					TheAPI.msg("&e/TheAPI PluginManager DisableAll", s);
					TheAPI.msg("&e/TheAPI PluginManager LoadAll", s);
					TheAPI.msg("&e/TheAPI PluginManager UnloadAll", s);
					TheAPI.msg("&e/TheAPI PluginManager ReloadAll", s);
					TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
					TheAPI.msg("&e/TheAPI PluginManager Files", s);
					List<Plugin> pl =  PluginManagerAPI.getPlugins();
					if(!pl.isEmpty()) {
					TheAPI.msg("&7Plugins:", s);
					for (Plugin w :pl)
						TheAPI.msg("&7 - &e" + getPlugin(w), s);
					}
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("Files")||args[1].equalsIgnoreCase("notloaded")||args[1].equalsIgnoreCase( "toload")||args[1].equalsIgnoreCase( "unloaded")) {
					HashMap<String, String> d = PluginManagerAPI.getPluginsToLoadWithNames();
					if(d.isEmpty()) {
						TheAPI.msg("&eNo plugin to load.", s);
						return true;
					}
					TheAPI.msg("&ePlugins to load:", s);
					for(String a : d.keySet()) {
						String text = " &7- &e"+d.get(a)+" &7(&e"+a+"&7)";
					TheAPI.msg(text, s);
					}
					return true;
				}
				if (args[1].equalsIgnoreCase("EnableAll")) {
					List<Plugin> ad = PluginManagerAPI.getPlugins();
					ad.remove(PluginManagerAPI.getPlugin(LoaderClass.plugin.getName()));
					if(ad.isEmpty()) {
						TheAPI.msg("&eNo plugin to enable.", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					for(Plugin a : ad) {
						TheAPI.msg("&eEnabling plugin "+a.getName()+"..", s);
						PluginManagerAPI.enablePlugin(a);
						TheAPI.msg("&ePlugin "+a.getName()+" enabled.", s);
					}
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("DisableAll")) {
					List<Plugin> ad = PluginManagerAPI.getPlugins();
					ad.remove(PluginManagerAPI.getPlugin(LoaderClass.plugin.getName()));
					if(ad.isEmpty()) {
						TheAPI.msg("&eNo plugin to disable.", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					for(Plugin a : ad) {
						TheAPI.msg("&eDisabling plugin "+a.getName()+"..", s);
						PluginManagerAPI.disablePlugin(a);
						TheAPI.msg("&ePlugin "+a.getName()+" disabled.", s);
					}
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("ReloadAll")) {
					if(PluginManagerAPI.getPlugins().isEmpty()) {
						TheAPI.msg("&eNo plugin to reload.", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					for(Plugin a : PluginManagerAPI.getPlugins()) {
						TheAPI.msg("&eReloading plugin "+a.getName()+"..", s);
						PluginManagerAPI.reloadPlugin(a);
						TheAPI.msg("&ePlugin "+a.getName()+" reloaded.", s);
					}
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("LoadAll")) {
					if(PluginManagerAPI.getPluginsToLoad().isEmpty()) {
						TheAPI.msg("&eNo plugin to load.", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					for(String a : PluginManagerAPI.getPluginsToLoad()) {
						if(a.equals("TheAPI"))continue;
						TheAPI.msg("&eLoading plugin "+a+"..", s);
						PluginManagerAPI.loadPlugin(a);
						TheAPI.msg("&ePlugin "+a+" loaded.", s);
					}
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("UnloadAll")) {
					List<Plugin> ad = PluginManagerAPI.getPlugins();
					ad.remove(PluginManagerAPI.getPlugin(LoaderClass.plugin.getName()));
					if(ad.isEmpty()) {
						TheAPI.msg("&eNo plugin to unload.", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					for(Plugin a : ad) {
						TheAPI.msg("&eUnloading plugin "+a.getName()+"..", s);
						PluginManagerAPI.unloadPlugin(a);
						TheAPI.msg("&ePlugin "+a.getName()+" unloaded.", s);
					}
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("Enable")) {
					if (args.length == 2) {
						TheAPI.msg("&e/TheAPI PluginManager Enable <plugin>", s);
						return true;
					}
					if(args[2].equals("TheAPI")) {
						TheAPI.msg("&eYou can't enable TheAPI.", s);
						return true;
					}
					int f=0;
					for(Plugin a : PluginManagerAPI.getPlugins())
						if(a.getName().equals(args[2])) {
							if(a.isEnabled())f=1;
							else f = 2;
							break;
						}
					if(f==1) {
						TheAPI.msg("&7Plugin "+args[2]+" is already enabled.", s);
						return true;
					}
					if(f==0) {
						TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eEnabling plugin "+args[2]+"..", s);
					PluginManagerAPI.enablePlugin(args[2]);
					TheAPI.msg("&ePlugin "+args[2]+" enabled.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("Disable")) {
					if (args.length == 2) {
						TheAPI.msg("&e/TheAPI PluginManager Disable <plugin>", s);
						return true;
					}
					if(args[2].equals("TheAPI")) {
						TheAPI.msg("&eYou can't disable TheAPI.", s);
						return true;
					}
					int f=0;
					for(Plugin a : PluginManagerAPI.getPlugins())
						if(a.getName().equals(args[2])) {
							if(a.isEnabled())f=1;
							else f = 2;
							break;
						}
					if(f==2) {
						TheAPI.msg("&7Plugin "+args[2]+" is already disabled.", s);
						return true;
					}
					if(f==0) {
						TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eDisabling plugin "+args[2]+"..", s);
					PluginManagerAPI.disablePlugin(args[2]);
					TheAPI.msg("&ePlugin "+args[2]+" disabled.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("Load")) {
					if (args.length == 2) {
						TheAPI.msg("&e/TheAPI PluginManager Load <plugin>", s);
						return true;
					}
					String pluginName = args[2];
					if(PluginManagerAPI.getRawPluginsToLoad().contains(pluginName)||PluginManagerAPI.getRawPluginsToLoad().contains(pluginName+".jar")
							||PluginManagerAPI.getPluginsToLoad().contains(pluginName)||PluginManagerAPI.getPluginsToLoad().contains(pluginName+".jar")) {
					if(pluginName.equals("TheAPI")) {
						TheAPI.msg("&eYou can't load TheAPI.", s);
						return true;
					}
					int f=0;
					String real = null;
					for(Plugin a : PluginManagerAPI.getPlugins())
						if(a.getName().equals(pluginName)||PluginManagerAPI.getFileOfPlugin(a).getName().equals(pluginName)
								||PluginManagerAPI.getFileOfPlugin(a).getName().equals(pluginName+".jar")) {
							if(a.isEnabled())f=1;
							else f = 2;
							real=a.getName();
							break;
						}
					if(f==2) {
						TheAPI.msg("&7Plugin "+real+" is already loaded, but disabled.", s);
						return true;
					}
					if(f==1) {
						TheAPI.msg("&7Plugin "+real+" is already loaded.", s);
						return true;
					}
					real = PluginManagerAPI.getPluginNameByFile(pluginName);
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eLoading plugin "+real+" ("+pluginName+")..", s);
					PluginManagerAPI.loadPlugin(pluginName);
					TheAPI.msg("&ePlugin "+real+" ("+pluginName+") loaded & enabled.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
					}
					if (pluginName.endsWith(".jar"))
						pluginName = pluginName.substring(0, pluginName.length() - 4);
					TheAPI.msg("&7Plugin "+pluginName+" not found.", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("Unload")) {
					if (args.length == 2) {
						TheAPI.msg("&e/TheAPI PluginManager Unload <plugin>", s);
						return true;
					}
					if(args[2].equals("TheAPI")) {
						TheAPI.msg("&eYou can't unload TheAPI.", s);
						return true;
					}
					int f=0;
					for(Plugin a : PluginManagerAPI.getPlugins()) {
						if(a.getName().equals(args[2])) {
							if(a.isEnabled())f=1;
							else f = 2;
							break;
						}}
					if(f==0) {
						TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eUnloading plugin "+args[2]+"..", s);
					PluginManagerAPI.unloadPlugin(args[2]);
					TheAPI.msg("&ePlugin "+args[2]+" unloaded.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("Reload")) {
					if (args.length == 2) {
						TheAPI.msg("&e/TheAPI PluginManager Reload <plugin>", s);
						return true;
					}
					int i = 0;
					for(Plugin a : PluginManagerAPI.getPlugins())
						if(a.getName().equals(args[2])) {
							if(a.isEnabled())i=1;
							else i = 2;
							break;
						}
					if(i==0) {
						TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eReloading plugin "+args[2]+"..", s);
					PluginManagerAPI.reloadPlugin(args[2]);
					TheAPI.msg("&ePlugin "+args[2]+" reloaded.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("Info")||args[1].equalsIgnoreCase("information")||args[1].equalsIgnoreCase("informations")) {
					if (args.length == 2) {
						TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
						return true;
					}
					int i = 0;
					for(Plugin a : PluginManagerAPI.getPlugins())
						if(a.getName().equals(args[2])) {
							if(a.isEnabled())i=1;
							else i = 2;
							break;
						}
					if(i==0) {
						TheAPI.msg("&7Plugin "+args[2]+" isn't loaded.", s);
						return true;
					}
					TheAPI.msg("&7╔═════════════════════════════", s);
					TheAPI.msg("&7║ Name: &e"+args[2], s);
					TheAPI.msg("&7║ State: &e"+(i==1?"Enabled":"Disabled"), s);
					if (PluginManagerAPI.getCommands(args[2]).size() != 0) {
						TheAPI.msg("&7║ Commands:", s);
						for (String a : PluginManagerAPI.getCommands(args[2]))
							TheAPI.msg("&7║  - &e" + a, s);
					}
					if (PluginManagerAPI.getPermissions(args[2]).size() != 0) {
						TheAPI.msg("&7║ Permissions:", s);
						for (Permission a : PluginManagerAPI.getPermissions(args[2])) {
							TheAPI.msg("&7║  » &e" + a.getName()+"&7:", s);
							Map<String, Boolean> c = a.getChildren();
							if(c.isEmpty()==false)
								for(String d : c.keySet())
						TheAPI.msg("&7║    - "+(c.get(d) ? "&a" : "&c") + d, s);
						}
					}
					if (PluginManagerAPI.getVersion(args[2]) != null)
						TheAPI.msg("&7║ Version: &e"+PluginManagerAPI.getVersion(args[2]), s);
					if (PluginManagerAPI.getWebsite(args[2]) != null)
						TheAPI.msg("&7║ Website: &e"+PluginManagerAPI.getWebsite(args[2]), s);
					if (PluginManagerAPI.getMainClass(args[2]) != null)
						TheAPI.msg("&7║ MainClass: &e"+PluginManagerAPI.getMainClass(args[2]), s);
					if (!PluginManagerAPI.getAuthor(args[2]).isEmpty())
						TheAPI.msg("&7║ Author(s): &e"+StringUtils.join(PluginManagerAPI.getAuthor(args[2]),", "), s);
					if (!PluginManagerAPI.getSoftDepend(args[2]).isEmpty())
						TheAPI.msg("&7║ SoftDepend(s): &e"+StringUtils.join(PluginManagerAPI.getSoftDepend(args[2]),", "), s);
					if (!PluginManagerAPI.getDepend(args[2]).isEmpty())
						TheAPI.msg("&7║ Depend(s): &e"+StringUtils.join(PluginManagerAPI.getDepend(args[2]),", "), s);
					TheAPI.msg("&7╚═════════════════════════════", s);
					return true;
				}
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&e/TheAPI PluginManager Enable <plugin>", s);
				TheAPI.msg("&e/TheAPI PluginManager Disable <plugin>", s);
				TheAPI.msg("&e/TheAPI PluginManager Load <plugin>", s);
				TheAPI.msg("&e/TheAPI PluginManager Unload <plugin>", s);
				TheAPI.msg("&e/TheAPI PluginManager Reload <plugin>", s);
				TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
				TheAPI.msg("&e/TheAPI PluginManager EnableAll", s);
				TheAPI.msg("&e/TheAPI PluginManager DisableAll", s);
				TheAPI.msg("&e/TheAPI PluginManager LoadAll", s);
				TheAPI.msg("&e/TheAPI PluginManager UnloadAll", s);
				TheAPI.msg("&e/TheAPI PluginManager ReloadAll", s);
				TheAPI.msg("&e/TheAPI PluginManager Info <plugin>", s);
				TheAPI.msg("&e/TheAPI PluginManager Files", s);
				List<Plugin> pl =  PluginManagerAPI.getPlugins();
				if(!pl.isEmpty()) {
				TheAPI.msg("&7Plugins:", s);
				for (Plugin w :pl)
					TheAPI.msg("&7 - &e" + getPlugin(w), s);
				}
				TheAPI.msg("&7-----------------", s);
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("cc") || args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("clearcache")) {
			if (perm(s,"ClearCache")) {
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&eClearing cache..", s);
				for (String p : LoaderClass.plugin.gui.keySet())
					LoaderClass.plugin.gui.get(p).clear();
				LoaderClass.plugin.gui.clear();
				TheAPI.clearCache();
				for(World w : Bukkit.getWorlds())
					for(Chunk c: w.getLoadedChunks())c.unload(true);
				TheAPI.msg("&eCache cleared.", s);
				TheAPI.msg("&7-----------------", s);
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
			TheAPI.msg("&7Opening inventory of player "+p.getName()+"..", s);
			((Player)s).openInventory(p.getInventory());
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
			if (perm(s,"Reload")) {
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&eReloading configs..", s);
				LoaderClass.data.reload();
				LoaderClass.config.reload();
				Tasks.unload();
				Tasks.load();
				TheAPI.msg("&eConfigs reloaded.", s);
				TheAPI.msg("&7-----------------", s);
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
								TheAPI.msg("&7║  - &e" + getPlugin(a), s);
						}
						TheAPI.msg("&7╚═════════════════════════════", s);
					}
				}.runAsync();
				return true;
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("worldsmanager") || args[0].equalsIgnoreCase("world") || args[0].equalsIgnoreCase("worlds") || args[0].equalsIgnoreCase("wm") || args[0].equalsIgnoreCase( "mw") || args[0].equalsIgnoreCase( "worldmanager")) {
			if (perm(s,"WorldsManager")) {
				if (args.length == 1) {
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&e/TheAPI WorldsManager Create <world> <generator>", s);
					TheAPI.msg("&e/TheAPI WorldsManager Delete <world>", s);
					TheAPI.msg("&e/TheAPI WorldsManager Unload <world>", s);
					TheAPI.msg("&e/TheAPI WorldsManager Load <world> <generator>", s);
					TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> [player]", s);
					TheAPI.msg("&e/TheAPI WorldsManager Save <world>", s);
					TheAPI.msg("&e/TheAPI WorldsManager SaveAll", s);
					TheAPI.msg("&7Worlds:", s);
					for (World w : Bukkit.getWorlds())
						TheAPI.msg("&7 - &e" + w.getName(), s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("Teleport") || args[1].equalsIgnoreCase("tp")) {
					if (args.length == 2) {
						if (s instanceof Player)
							TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> [player]", s);
						else
							TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> <player>", s);
						TheAPI.msg("&7Worlds:", s);
						for (World w : Bukkit.getWorlds())
							TheAPI.msg("&7 - &e" + w.getName(), s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) == null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&7World with name '" + args[2] + "' doesn't exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (args.length == 3) {
						if (s instanceof Player) {
							try {
								((Player) s).teleport(Bukkit.getWorld(args[2]).getSpawnLocation());
							} catch (Exception e) {
								((Player) s).teleport(new Location(Bukkit.getWorld(args[2]), 60, 60, 60));
							}
							TheAPI.msg("&eTeleporting to the world " + args[2] + "..", s);
							return true;
						} else
							TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> <player>", s);
						return true;
					}
					if (args.length == 4) {
						Player p = Bukkit.getPlayer(args[3]);
						if (p == null) {
							TheAPI.msg("&ePlayer " + args[3] + " isn't online", p);
							return true;
						}
						TheAPI.msg("&eTeleporting to the world " + args[2] + "..", p);
						TheAPI.msg("&eTeleporting player " + p.getName() + " to the world " + args[2] + "..", s);
						return true;
					}
				}
				if (args[1].equalsIgnoreCase("saveall")) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&eTheAPI WorldsManager saving " + (Bukkit.getWorlds().size()) + " world(s)..", s);
						for (World w : Bukkit.getWorlds())
							w.save();
						TheAPI.msg("&eWorlds saved..", s);
						TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("save")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&e/TheAPI WorldsManager Save <world>", s);
						TheAPI.msg("&7Worlds:", s);
						for (World w : Bukkit.getWorlds())
							TheAPI.msg("&7 - &e" + w.getName(), s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}

					if (Bukkit.getWorld(args[2]) == null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&eWorld with name '" + args[2] + "' doesn't exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}

					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eTheAPI WorldsManager saving world with name '" + args[2] + "'..", s);
					Bukkit.getWorld(args[2]).save();
					TheAPI.msg("&eWorld with name '" + args[2] + "' saved.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("unload")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&e/TheAPI WorldsManager Unload <world>", s);
						TheAPI.msg("&7Worlds:", s);
						for (World w : Bukkit.getWorlds())
							TheAPI.msg("&7 - &e" + w.getName(), s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) == null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&eWorld with name '" + args[2] + "' doesn't exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eTheAPI WorldsManager unloading world with name '" + args[2] + "'..", s);
					WorldsAPI.unloadWorld(args[2], true);

					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					a.remove(args[2]);
					LoaderClass.config.getConfig().set("Worlds", a);
					TheAPI.msg("&eWorld with name '" + args[2] + "' unloaded.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("load")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&e/TheAPI WorldsManager Load <world> <generator>", s);
						TheAPI.msg("&7Generators:", s);
						for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat"))
							TheAPI.msg("&7 - &e" + w, s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) != null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&eWorld with name '" + args[2] + "' already exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (args.length == 3) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&e/TheAPI WorldsManager Load <world> <generator>", s);
						TheAPI.msg("&7Generators:", s);
						for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat"))
							TheAPI.msg("&7 - &e" + w, s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (new File(Bukkit.getWorldContainer().getPath() + "/" + args[2] + "/session.lock").exists()) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&eTheAPI WorldsManager loading world with name '" + args[2] + "'..", s);
						int generator = 0;
						if (args[3].equalsIgnoreCase("Flat"))
							generator = 1;
						if (args[3].equalsIgnoreCase("Nether"))
							generator = 2;
						if (args[3].equalsIgnoreCase("The_End") || args[3].equalsIgnoreCase("End"))
							generator = 3;
						if (args[3].equalsIgnoreCase("The_Void") || args[3].equalsIgnoreCase("Void")
								|| args[3].equalsIgnoreCase("Empty"))
							generator = 4;
						Environment env = Environment.NORMAL;
						WorldType wt = WorldType.NORMAL;
						switch (generator) {
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
						WorldsAPI.load(args[2], env, wt);
						List<String> a = LoaderClass.config.getStringList("Worlds");
						a.add(args[2]);
						LoaderClass.config.set("Worlds", a);
						LoaderClass.config.set("WorldsSetting." + args[2] + ".Generator", generator);
						LoaderClass.config.set("WorldsSetting." + args[2] + ".GenerateStructures", true);
						LoaderClass.config.save();
						TheAPI.msg("&eWorld with name '" + args[2] + "' loaded.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eWorld with name '" + args[2] + "' doesn't exists.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("delete")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&e/TheAPI WorldsManager Delete <world>", s);
						TheAPI.msg("&7Worlds:", s);
						for (World w : Bukkit.getWorlds())
							TheAPI.msg("&7 - &e" + w.getName(), s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) == null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&eWorld with name '" + args[2] + "' doesn't exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eTheAPI WorldsManager deleting world with name '" + args[2] + "'..", s);
					WorldsAPI.delete(Bukkit.getWorld(args[2]), true);
					List<String> a = LoaderClass.config.getConfig().getStringList("Worlds");
					if(a.contains(args[2])) {
					a.remove(args[2]);
					LoaderClass.config.set("Worlds", a);
					}
					LoaderClass.config.set("WorldsSetting." + args[2], null);
					LoaderClass.config.save();
					TheAPI.msg("&eWorld with name '" + args[2] + "' deleted.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				if (args[1].equalsIgnoreCase("create")) {
					if (args.length == 2) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&e/TheAPI WorldsManager Create <world> <generator>", s);
						TheAPI.msg("&7Generators:", s);
						for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void"))
							TheAPI.msg("&7 - &e" + w, s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					if (Bukkit.getWorld(args[2]) != null) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&eWorld with name '" + args[2] + "' already exists.", s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}

					if (args.length == 3) {
						TheAPI.msg("&7-----------------", s);
						TheAPI.msg("&e/TheAPI WorldsManager Create " + args[2] + " <generator>", s);
						TheAPI.msg("&7Generators:", s);
						for (String w : Arrays.asList("Default", "Nether", "The_End", "The_Void", "Flat"))
							TheAPI.msg("&7 - &e" + w, s);
						TheAPI.msg("&7-----------------", s);
						return true;
					}
					int generator = 0;
					if (args[3].equalsIgnoreCase("Flat"))
						generator = 1;
					if (args[3].equalsIgnoreCase("Nether"))
						generator = 2;
					if (args[3].equalsIgnoreCase("The_End") || args[3].equalsIgnoreCase("End"))
						generator = 3;
					if (args[3].equalsIgnoreCase("The_Void") || args[3].equalsIgnoreCase("Void")
							|| args[3].equalsIgnoreCase("Empty"))
						generator = 4;
					TheAPI.msg("&7-----------------", s);
					TheAPI.msg("&eTheAPI WorldsManager creating new world with name '" + args[2] + "' using generator '"
							+ args[3] + "'..", s);
					Environment env = Environment.NORMAL;
					WorldType wt = WorldType.NORMAL;
					switch (generator) {
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
					List<String> a = LoaderClass.config.getStringList("Worlds");
					if(!a.contains(args[2])) {
					a.add(args[2]);
					LoaderClass.config.set("Worlds", a);
				    }
					LoaderClass.config.set("WorldsSetting." + args[2] + ".Generator", generator);
					LoaderClass.config.set("WorldsSetting." + args[2] + ".GenerateStructures", true);
					LoaderClass.config.save();
					WorldsAPI.create(args[2], env, wt, true, 0);
					TheAPI.msg("&eWorld with name '" + args[2] + "' created.", s);
					TheAPI.msg("&7-----------------", s);
					return true;
				}
				TheAPI.msg("&7-----------------", s);
				TheAPI.msg("&e/TheAPI WorldsManager Create <world> <generator>", s);
				TheAPI.msg("&e/TheAPI WorldsManager Delete <world>", s);
				TheAPI.msg("&e/TheAPI WorldsManager Unload <world>", s);
				TheAPI.msg("&e/TheAPI WorldsManager Load <world> <generator>", s);
				TheAPI.msg("&e/TheAPI WorldsManager Teleport <world> [player]", s);
				TheAPI.msg("&e/TheAPI WorldsManager Save <world>", s);
				TheAPI.msg("&e/TheAPI WorldsManager SaveAll", s);
				TheAPI.msg("&7Worlds:", s);
				for (World w : Bukkit.getWorlds())
					TheAPI.msg("&7 - &e" + w.getName(), s);
				TheAPI.msg("&7-----------------", s);
				return true;

			}
			return true;
		}
		return false;
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
