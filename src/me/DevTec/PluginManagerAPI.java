package me.DevTec;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import com.google.common.collect.Maps;

import me.DevTec.NMS.Reflections;
import me.DevTec.Scheduler.Tasker;
import me.DevTec.Zip.JarReader;

public class PluginManagerAPI {
	private static PluginManager spm = Bukkit.getPluginManager();

	public static boolean enablePlugin(String plugin) {
		if (!spm.isPluginEnabled(plugin)) {
			spm.enablePlugin(getPlugin(plugin));
			return true;
		}
		return false;
	}

	public static boolean enablePlugin(Plugin plugin) {
		if (!spm.isPluginEnabled(plugin)) {
			spm.enablePlugin(plugin);
			return true;
		}
		return false;
	}

	public static List<Plugin> getEnabledPlugins() {
		List<Plugin> a = new ArrayList<Plugin>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (p.isEnabled())
				a.add(p);
		}
		return a;
	}

	public static List<Plugin> getDisabledPlugins() {
		List<Plugin> a = new ArrayList<Plugin>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (!p.isEnabled())
				a.add(p);
		}
		return a;
	}

	public static List<String> getEnabledPluginsNames() {
		List<String> a = new ArrayList<String>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (p.isEnabled())
				a.add(p.getName());
		}
		return a;
	}

	public static List<String> getDisabledPluginsNames() {
		List<String> a = new ArrayList<String>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (!p.isEnabled())
				a.add(p.getName());
		}
		return a;
	}

	public static List<Plugin> getPlugins() {
		List<Plugin> a = new ArrayList<Plugin>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			a.add(p);
		}
		return a;
	}

	public static List<String> getPluginsNames() {
		List<String> a = new ArrayList<String>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			a.add(p.getName());
		}
		return a;
	}

	public static Plugin getPlugin(String plugin) {
		Plugin p = null;
		for (Plugin s : spm.getPlugins()) {
			if (s.getName().equalsIgnoreCase(plugin))
				p = s;
		}
		return p;
	}

	public static List<String> getDepend(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled())
			if (p.getDescription().getDepend() != null && p.getDescription().getDepend().isEmpty() == false)
				return p.getDescription().getDepend();
		return new ArrayList<String>();
	}

	public static List<String> getSoftDepend(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled())
			if (p.getDescription().getSoftDepend() != null && p.getDescription().getSoftDepend().isEmpty() == false)
				return p.getDescription().getSoftDepend();
		return new ArrayList<String>();
	}

	public static List<String> getAuthor(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled())
			return p.getDescription().getAuthors();
		return new ArrayList<String>();
	}

	public static String getAPIVersion(String plugin) {
		Plugin p = getPlugin(plugin);
		try {
			if (p != null && p.isEnabled() && p.getDescription().getAPIVersion() != null)
				return p.getDescription().getAPIVersion();
		} catch (Exception e) {
		}
		return null;
	}

	public static String getVersion(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled() && p.getDescription().getVersion() != null)
			return p.getDescription().getVersion();
		return null;
	}

	public static String getWebsite(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled() && p.getDescription().getWebsite() != null)
			return p.getDescription().getWebsite();
		return null;
	}

	public static String getMainClass(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.getDescription().getMain() != null)
			return p.getDescription().getMain();
		return null;
	}

	public static List<String> getCommands(String plugin) {
		List<String> list = new ArrayList<String>();
		for (String s : getPlugin(plugin).getDescription().getCommands().keySet())
			list.add(s);
		return list;
	}

	public static List<String> getCommands(Plugin plugin) {
		return getCommands(plugin.getName());
	}

	public static List<Permission> getPermissions(String plugin) {
		return getPlugin(plugin).getDescription().getPermissions();
	}

	public static List<Permission> getPermissions(Plugin plugin) {
		return getPermissions(plugin.getName());
	}

	public static boolean isEnabledPlugin(String plugin) {
		if (plugin == null || getPlugin(plugin) == null)
			return false;
		return getPlugin(plugin).isEnabled();
	}

	public static boolean isEnabledPlugin(Plugin plugin) {
		if (plugin == null)
			return false;
		return isEnabledPlugin(plugin.getName());
	}

	public static boolean isDisabledPlugin(String plugin) {
		if (plugin == null || getPlugin(plugin) == null)
			return true;
		return !getPlugin(plugin).isEnabled();
	}

	public static boolean isDisabledPlugin(Plugin plugin) {
		if (plugin == null)
			return true;
		return !isEnabledPlugin(plugin.getName());
	}

	public static List<String> getPluginsToLoad() {
		List<String> list = new ArrayList<String>();
		if (new File("plugins").isDirectory()) // is folder
			for (File f : new File("plugins").listFiles()) {
				if(!f.isDirectory() && f.getName().endsWith(".jar")) {
					for(Plugin p : getPlugins()) {
						if(!getFileOfPlugin(p).getName().equals(f.getName()))
					if(!spm.isPluginEnabled(f.getName().substring(0,f.getName().length()-4)))
						list.add(f.getName());
					break;	
					}
				}
			}
		return list;
	}
	
	public static String getPluginFileByName(String pluginName) {
		String pl = null;
		HashMap<String, String> load = getPluginsToLoadWithNames();
		for(String s : load.keySet()) {
			if(s.equals(pluginName)) {
				pl=load.get(s);
				break;
			}
			if(load.get(s).equals(pluginName)) {
				pl=load.get(s);
				break;
			}
		}
		if (pl.endsWith(".jar"))
			pl = pl.substring(0, pl.length() - 4);
		return pl;
	}
	
	public static String getPluginNameByFile(String pluginFile) {
		String pluginName = null;
		HashMap<String, String> load = getPluginsToLoadWithNames();
		for(String s : load.keySet()) {
			if(s.equals(pluginFile)) {
				pluginName=pluginFile;
				break;
			}
			if(load.get(s).equals(pluginFile)) {
				pluginName=s;
				break;
			}
		}
		return pluginName;
	}
	
	public static File getFileOfPlugin(Plugin p) {
		return new File("plugins/"+new File(Reflections.getClass(p.getDescription().getMain()).getProtectionDomain()
				  .getCodeSource().getLocation().getPath()).getName());
	}
	
	/**
	 * @return HashMap<PluginName, FileName>
	 */
	public static HashMap<String,String> getPluginsToLoadWithNames() {
		HashMap<String,String> a = Maps.newHashMap();
		if (new File("plugins").isDirectory()) // is folder
			for (File f : new File("plugins").listFiles()) {
				if(!f.isDirectory() && f.getName().endsWith(".jar")) {
					Plugin loaded = null;
					for(Plugin p : getPlugins()) {
						if(getFileOfPlugin(p).getName().equals(f.getName())) {
							loaded=p;
							break;
						}
					}
					if(loaded==null) {
						String name = null;
						String[] text = new JarReader(f).read("plugin.yml");
						for(String find : text) {
			                 if(find.contains("name: ")) {
					         String[] str = find.split("name: ");
			                	 name=str[1];
			                	 break;
			                 }
					      }
					a.put(name,f.getName());
					}
					}
	
			}
		return a;
	}

	public static List<String> getRawPluginsToLoad() {
		List<String> list = new ArrayList<String>();
		if (new File("plugins").isDirectory()) // is folder
			for (File f : new File("plugins").listFiles()) {
				if(!f.isDirectory() && f.getName().endsWith(".jar")) {
					Plugin loaded = null;
					for(Plugin p : getPlugins()) {
						if(new java.io.File(Reflections.getClass(p.getDescription().getMain()).getProtectionDomain()
								  .getCodeSource()
								  .getLocation()
								  .getPath())
								.getName().equals(f.getName())) {
							loaded=p;
							break;
						}
					}
					if(loaded==null) {
						String name = null;
						String[] text = new JarReader(f).read("plugin.yml");
						for(String find : text) {
			                 if(find.contains("name: ")) {
					         String[] str = find.split("name: ");
			                	 name=str[1];
			                	 break;
			                 }
					      }
					list.add(name);
					}
				}
			}
		return list;
	}

	public static void reloadPlugin(Plugin plugin) {
		reloadPlugin(plugin.getName());
	}

	public static void reloadPlugin(String plugin) {
		new Tasker() {
			public void run() {
				String pl = plugin;
				unloadPlugin(getPlugin(pl));
				new Thread(new Runnable() {
					public void run() {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
						loadPlugin(pl);
					}
				}).start();
			}
		}.runAsync();
	}

	public static void unloadPlugin(Plugin plugin) {
		if (plugin == null)
			return;
		unloadPlugin(plugin.getName());
	}

	@SuppressWarnings("unchecked")
	public static void unloadPlugin(String pluginName) {
		if (pluginName == null)
			return;
		new Tasker() {
			public void run() {
		List<Plugin> plugins = (List<Plugin>)Reflections.get(Reflections.getField(((SimplePluginManager)spm).getClass(), "plugins"),((SimplePluginManager)spm));
		Map<String, Plugin> lookupNames = (Map<String, Plugin>)Reflections.get(Reflections.getField(((SimplePluginManager)spm).getClass(), "lookupNames"),((SimplePluginManager)spm));
		SimpleCommandMap commandMap = (SimpleCommandMap)Reflections.get(Reflections.getField(((SimplePluginManager)spm).getClass(), "commandMap"),((SimplePluginManager)spm));
		Map<String, Command> knownCommands = (Map<String, Command>)Reflections.get(Reflections.getField(commandMap.getClass(),"knownCommands"),commandMap);
			Plugin pl = getPlugin(pluginName);
			disablePlugin(pl);
			if(plugins != null && plugins.contains(pl))
				plugins.remove(pl);
			if(lookupNames != null && lookupNames.containsKey(pluginName))
				lookupNames.remove(pluginName);
			if(commandMap != null) {
				for(Iterator<Map.Entry<String, Command>> it = knownCommands.entrySet().iterator(); it.hasNext();) {
					Map.Entry<String, Command> entry = it.next();
					if(entry.getValue() instanceof PluginCommand) {
						PluginCommand c = (PluginCommand) entry.getValue();
						if(c.getPlugin() == pl) {
							c.unregister(commandMap);
							it.remove();
						}
					}
				}
			}
			try {
				List<Permission> permissionlist = pl.getDescription().getPermissions();
				Iterator<Permission> p = permissionlist.iterator();
				while (p.hasNext()) {
					spm.removePermission(p.next().toString());
				}
			} catch (NoSuchMethodError e) {
			}}}.runAsync();
		}

	public static void loadPlugin(String n) {
		if (n == null)return;
		new Tasker() {
			public void run() {
		String pluginName = getPluginFileByName(n);
		if(pluginName==null)pluginName=getPluginNameByFile(n);
		try {
			Plugin p = Bukkit.getPluginManager().loadPlugin(new File("plugins/" + pluginName + ".jar"));
			p.onLoad();
			Bukkit.getPluginManager().enablePlugin(p);
				CommandMap commandMap = (CommandMap)Reflections.get(Reflections.getField(Bukkit.getServer().getClass(), "commandMap"), Bukkit.getServer());
				for (String s : p.getDescription().getCommands().keySet())
					commandMap.register(s, null);
		} catch (Exception e) {}
			}}.runAsync();
	}

	public static boolean disablePlugin(String plugin) {
		if (plugin == null)
			return false;
		if (isEnabledPlugin(plugin)) {
			spm.disablePlugin(getPlugin(plugin));
			return true;
		}
		return false;
	}

	public static boolean disablePlugin(Plugin plugin) {
		if (plugin == null)
			return false;
		return disablePlugin(plugin.getName());
	}
}
