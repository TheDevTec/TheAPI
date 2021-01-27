package me.devtec.theapi.apis;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;

import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.thapiutils.LoaderClass;

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
		List<Plugin> a = new ArrayList<>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (p.isEnabled())
				a.add(p);
		}
		return a;
	}

	public static List<Plugin> getDisabledPlugins() {
		List<Plugin> a = new ArrayList<>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (!p.isEnabled())
				a.add(p);
		}
		return a;
	}

	public static List<String> getEnabledPluginsNames() {
		List<String> a = new ArrayList<>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (p.isEnabled())
				a.add(p.getName());
		}
		return a;
	}

	public static List<String> getDisabledPluginsNames() {
		List<String> a = new ArrayList<>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (!p.isEnabled())
				a.add(p.getName());
		}
		return a;
	}

	public static List<Plugin> getPlugins() {
		List<Plugin> a = new ArrayList<>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins())
			a.add(p);
		return a;
	}

	public static List<String> getPluginsNames() {
		List<String> a = new ArrayList<>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins())
			a.add(p.getName());
		return a;
	}

	public static Plugin getPlugin(String plugin) {
		for (Plugin s : spm.getPlugins())
			if (s.getName().equalsIgnoreCase(plugin))return s;
		return null;
	}

	public static List<String> getDepend(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null)
			if (p.getDescription().getDepend() != null)
				return p.getDescription().getDepend();
		return new ArrayList<>();
	}

	public static List<String> getSoftDepend(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null)
			if (p.getDescription().getSoftDepend() != null)
				return p.getDescription().getSoftDepend();
		return new ArrayList<>();
	}

	public static List<String> getAuthor(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null)
			return p.getDescription().getAuthors();
		return new ArrayList<>();
	}

	public static String getAPIVersion(String plugin) {
		Plugin p = getPlugin(plugin);
		try {
			if (p != null)
				return p.getDescription().getAPIVersion();
		} catch (Exception | NoSuchMethodError e) {
		}
		return null;
	}

	public static String getVersion(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null)
			return p.getDescription().getVersion();
		return null;
	}

	public static String getWebsite(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null)
			return p.getDescription().getWebsite();
		return null;
	}

	public static String getMainClass(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null)
			return p.getDescription().getMain();
		return null;
	}

	public static List<String> getCommands(String plugin) {
		return new ArrayList<>(getPlugin(plugin).getDescription().getCommands().keySet());
	}

	public static List<String> getCommands(Plugin plugin) {
		return new ArrayList<>(plugin.getDescription().getCommands().keySet());
	}

	public static List<Permission> getPermissions(String plugin) {
		return getPlugin(plugin).getDescription().getPermissions();
	}

	public static List<Permission> getPermissions(Plugin plugin) {
		return plugin.getDescription().getPermissions();
	}

	public static boolean isEnabledPlugin(String plugin) {
		if (plugin == null || getPlugin(plugin) == null)return false;
		return getPlugin(plugin).isEnabled();
	}

	public static boolean isEnabledPlugin(Plugin plugin) {
		if (plugin == null)return false;
		return isEnabledPlugin(plugin.getName());
	}

	public static boolean isDisabledPlugin(String plugin) {
		if (plugin == null || getPlugin(plugin) == null)return true;
		return !getPlugin(plugin).isEnabled();
	}

	public static boolean isDisabledPlugin(Plugin plugin) {
		if (plugin == null)return true;
		return !isEnabledPlugin(plugin.getName());
	}
	
	public static enum SearchType {
		PLUGIN_NAME,
		FILE_NAME
	}
	
	public static List<String> getPluginsToLoad(SearchType type) {
		List<String> list = new ArrayList<>();
		File ff = new File("plugins");
		Data data = new Data();
		if (ff.exists() && ff.isDirectory()) // is folder
			for (File f : ff.listFiles()) {
				if (!f.isDirectory() && f.getName().endsWith(".jar")) {
					try {
						JarFile jar = new JarFile(f);
						data.reload(StreamUtils.fromStream(jar.getInputStream(jar.getEntry("plugin.yml"))));
						jar.close();
						if(getPlugin(data.getString("name"))==null) {
							list.add(type==SearchType.FILE_NAME?f.getName():data.getString("name"));
						}
					}catch(Exception err) {}
					data.reset();
				}
			}
		return list;
	}
	
	public static String getPluginName(String name, SearchType type) {
		File ff = new File("plugins");
		Data data = new Data();
		if (ff.exists() && ff.isDirectory()) // is folder
			for (File f : ff.listFiles()) {
				if (!f.isDirectory() && f.getName().endsWith(".jar")) {
					try {
						JarFile jar = new JarFile(f);
						data.reload(StreamUtils.fromStream(jar.getInputStream(jar.getEntry("plugin.yml"))));
						jar.close();
						if(type==SearchType.PLUGIN_NAME?f.getName().equals(name):name.equalsIgnoreCase(data.getString("name")))return type==SearchType.PLUGIN_NAME?data.getString("name"):f.getName();
					}catch(Exception err) {}
					data.reset();
				}
			}
		return null;
	}
	
	public static String getPluginName(File name) {
		File ff = new File("plugins");
		Data data = new Data();
		if (ff.exists() && ff.isDirectory()) // is folder
			for (File f : ff.listFiles()) {
				if (!f.isDirectory() && f.getName().endsWith(".jar") && f == name) {
					try {
						JarFile jar = new JarFile(f);
						data.reload(StreamUtils.fromStream(jar.getInputStream(jar.getEntry("plugin.yml"))));
						jar.close();
						return data.getString("name");
					}catch(Exception err) {}
					data.reset();
				}
			}
		return null;
	}
	
	public static File getPluginFile(String name, SearchType type) {
		File ff = new File("plugins");
		Data data = new Data();
		if (ff.exists() && ff.isDirectory()) // is folder
			for (File f : ff.listFiles()) {
				if (!f.isDirectory() && f.getName().endsWith(".jar")) {
					try {
						JarFile jar = new JarFile(f);
						data.reload(StreamUtils.fromStream(jar.getInputStream(jar.getEntry("plugin.yml"))));
						jar.close();
						if(type==SearchType.PLUGIN_NAME?data.getString("name").equalsIgnoreCase(name):f.getName().equalsIgnoreCase(name))return f;
					}catch(Exception err) {}
					data.reset();
				}
			}
		return null;
	}
	
	public static File getPluginFile(Plugin p) {
		return new File("plugins/" + new File(Ref.getClass(p.getDescription().getMain()).getProtectionDomain()
				.getCodeSource().getLocation().getPath()).getName());
	}

	public static Map<String, File> getPluginsToLoadWithNames() {
		HashMap<String, File> nameFile = new HashMap<>();
		HashMap<String, String> copies = new HashMap<>();
		File ff = new File("plugins");
		Data data = new Data();
		if (ff.exists() && ff.isDirectory()) // is folder
			for (File f : ff.listFiles()) {
				if (!f.isDirectory() && f.getName().endsWith(".jar")) {
					try {
						JarFile jar = new JarFile(f);
						data.reload(StreamUtils.fromStream(jar.getInputStream(jar.getEntry("plugin.yml"))));
						jar.close();
						if(getPlugin(data.getString("name"))==null) {
							if(data.getString("name").contains(data.getString("name").toLowerCase())) {
								Bukkit.getLogger().warning("Found two or more plugins with similiar name! ("+copies.get(data.getString("name").toLowerCase())+" = "+f.getName()+")");
							}
							nameFile.put(data.getString("name"), f);
							copies.put(data.getString("name").toLowerCase(), f.getName());
						}
					}catch(Exception err) {}
					data.reset();
				}
			}
		return nameFile;
	}

	public static void reloadPlugin(Plugin plugin, Runnable onReload) {
		reloadPlugin(plugin.getName(), onReload);
	}

	public static void unloadPlugins() {
		NMSAPI.postToMainThread(new Runnable() {
			public void run() {
				List<Plugin> pl = getPlugins();
				pl.remove(LoaderClass.plugin);
				for(Plugin p : new ArrayList<>(pl))
					unloadPlugin(p, new Runnable() {
						public void run() {
							pl.remove(p);
							if(pl.isEmpty()) {
								unloadPlugin(LoaderClass.plugin);
							}
						}
					});
			}});
	}

	public static void reloadPlugins(Runnable onReload) {
		NMSAPI.postToMainThread(new Runnable() {
			public void run() {
				List<Plugin> pl = getPlugins();
				pl.remove(LoaderClass.plugin);
				for(Plugin p : new ArrayList<>(pl))
					unloadPlugin(p, new Runnable() {
						public void run() {
							pl.remove(p);
							if(pl.isEmpty()) {
								unloadPlugin(LoaderClass.plugin, new Runnable() {
									public void run() {
										for(Plugin p : spm.loadPlugins(new File("plugins"))) {
											p.onLoad();
											spm.enablePlugin(p);
										}
										if(onReload!=null)
										onReload.run();
									}
								});
							}
						}
					});
			}});
	}

	public static void reloadPlugin(String plugin, Runnable onReload) {
		NMSAPI.postToMainThread(new Runnable() {
			public void run() {
				String pl = plugin;
				unloadPlugin(getPlugin(pl), new Runnable() {
					public void run() {
						loadPlugin(pl, onReload);
					}
				});
			}});
	}

	public static void reloadPlugin(Plugin plugin) {
		reloadPlugin(plugin.getName());
	}

	public static void reloadPlugin(String plugin) {
		NMSAPI.postToMainThread(new Runnable() {
			public void run() {
				String pl = plugin;
				unloadPlugin(getPlugin(pl), new Runnable() {
					public void run() {
						loadPlugin(pl);
					}
				});
			}});
	}

	public static void unloadPlugin(Plugin plugin, Runnable onUnload) {
		if (plugin == null)return;
		unloadPlugin(plugin.getName(), onUnload);
	}

	public static void unloadPlugin(Plugin plugin) {
		if (plugin == null)return;
		unloadPlugin(plugin.getName(), null);
	}
	
	public static void unloadPlugin(String pluginName) {
		unloadPlugin(pluginName, null);
	}
	
	private static Field fPls = Ref.field(SimplePluginManager.class, "plugins"), lNms = Ref.field(SimplePluginManager.class, "lookupNames");
	private static SimpleCommandMap commandMap = (SimpleCommandMap)Ref.get(spm, "commandMap");
	@SuppressWarnings("unchecked")
	private static Map<String, Command> knownCommands = (Map<String, Command>) Ref.get(commandMap, "knownCommands");

	@SuppressWarnings("unchecked")
	public static void unloadPlugin(String pluginName, Runnable onUnload) {
		if (pluginName == null)
			return;
		Plugin pl = getPlugin(pluginName);
		if(pl==null)return;
		try {
			disablePlugin(pl);
		}catch(Exception er) {
			er.printStackTrace();
		}
		new Thread() {
			public void run() {
				List<Plugin> plugins = (List<Plugin>) Ref.get(((SimplePluginManager) spm),fPls);
				Map<String, Plugin> lookupNames = (Map<String, Plugin>) Ref.get(((SimplePluginManager) spm),lNms);
				if (plugins != null && plugins.contains(pl))
					plugins.remove(pl);
				if (lookupNames != null && lookupNames.containsKey(pluginName))
					lookupNames.remove(pluginName);
				if (commandMap != null)
					for (Iterator<Map.Entry<String, Command>> it = knownCommands.entrySet().iterator(); it.hasNext();) {
						Map.Entry<String, Command> entry = it.next();
						if (entry.getValue() instanceof PluginCommand) {
							PluginCommand c = (PluginCommand) entry.getValue();
							if (c.getPlugin().getName().equalsIgnoreCase(pl.getName())) {
								c.unregister(commandMap);
								it.remove();
							}
						}
					}
				try {
					List<Permission> permissionlist = pl.getDescription().getPermissions();
					Iterator<Permission> p = permissionlist.iterator();
					while (p.hasNext())
						spm.removePermission(p.next().toString());
				} catch (Exception | NoSuchMethodError e) {
				}
				if(onUnload!=null)
					onUnload.run();
			}
		}.start();
	}

	public static void loadPlugin(String n, Runnable onLoad) {
		if (n == null)
			return;
		new Thread() {
			public void run() {
				File plugin = getPluginFile(n, SearchType.PLUGIN_NAME);
				if (plugin == null)return;
				try {
					Plugin p = spm.loadPlugin(plugin);
					NMSAPI.postToMainThread(new Runnable() {
						public void run() {
							p.onLoad();
							spm.enablePlugin(p);
							try {
							if(p.getDescription().getCommands()!=null)
								for (String command : p.getDescription().getCommands().keySet()) {
									command = command.toLowerCase(Locale.ENGLISH).trim();
									commandMap.register(command, null);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							if(onLoad!=null)
							onLoad.run();
						}});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}}.start();
	}

	public static void loadPlugin(String n) {
		loadPlugin(n, null);
	}

	public static boolean disablePlugin(String plugin) {
		if (plugin == null)return false;
		if (isEnabledPlugin(plugin)) {
			spm.disablePlugin(getPlugin(plugin));
			return true;
		}
		return false;
	}

	public static boolean disablePlugin(Plugin plugin) {
		if (plugin == null)return false;
		return disablePlugin(plugin.getName());
	}
}

