package me.Straiker123;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.bukkit.event.Event;
import org.bukkit.permissions.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.SimplePluginManager;

import me.Straiker123.Utils.Error;

public class PluginManagerAPI {
	private PluginManager manager = Bukkit.getPluginManager();

	public boolean enablePlugin(String plugin) {
		if (!manager.isPluginEnabled(plugin)) {
			manager.enablePlugin(getPlugin(plugin));
			return true;
		}
		return false;
	}

	public List<Plugin> getEnabledPlugins() {
		List<Plugin> a = new ArrayList<Plugin>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (p.isEnabled())
				a.add(p);
		}
		return a;
	}

	public List<Plugin> getDisabledPlugins() {
		List<Plugin> a = new ArrayList<Plugin>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			if (!p.isEnabled())
				a.add(p);
		}
		return a;
	}

	public List<Plugin> getPlugins() {
		List<Plugin> a = new ArrayList<Plugin>();
		for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
			a.add(p);
		}
		return a;
	}

	public Plugin getPlugin(String plugin) {
		Plugin p = null;
		for (Plugin s : manager.getPlugins()) {
			if (s.getName().equalsIgnoreCase(plugin))
				p = s;
		}
		return p;
	}

	public List<String> getDepend(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled())
			if (p.getDescription().getDepend() != null && p.getDescription().getDepend().isEmpty() == false)
				return p.getDescription().getDepend();
		return new ArrayList<String>();
	}

	public List<String> getSoftDepend(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled())
			if (p.getDescription().getSoftDepend() != null && p.getDescription().getSoftDepend().isEmpty() == false)
				return p.getDescription().getSoftDepend();
		return new ArrayList<String>();
	}

	public List<String> getAuthor(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled())
			return p.getDescription().getAuthors();
		return new ArrayList<String>();
	}

	public String getAPIVersion(String plugin) {
		Plugin p = getPlugin(plugin);
		try {
			if (p != null && p.isEnabled() && p.getDescription().getAPIVersion() != null)
				return p.getDescription().getAPIVersion();
		} catch (Exception e) {
		}
		return null;
	}

	public String getVersion(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled() && p.getDescription().getVersion() != null)
			return p.getDescription().getVersion();
		return null;
	}

	public String getWebsite(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.isEnabled() && p.getDescription().getWebsite() != null)
			return p.getDescription().getWebsite();
		return null;
	}

	public String getMainClass(String plugin) {
		Plugin p = getPlugin(plugin);
		if (p != null && p.getDescription().getMain() != null)
			return p.getDescription().getMain();
		return null;
	}

	public List<String> getCommands(String plugin) {
		List<String> list = new ArrayList<String>();
		for (String s : getPlugin(plugin).getDescription().getCommands().keySet())
			list.add(s);
		return list;
	}

	public List<String> getCommands(Plugin plugin) {
		return getCommands(plugin.getName());
	}

	public List<Permission> getPermissions(String plugin) {
		return getPlugin(plugin).getDescription().getPermissions();
	}

	public List<Permission> getPermissions(Plugin plugin) {
		return getPermissions(plugin.getName());
	}

	public boolean isEnabledPlugin(String plugin) {
		if (plugin == null || getPlugin(plugin) == null)
			return false;
		return getPlugin(plugin).isEnabled();
	}

	public boolean isEnabledPlugin(Plugin plugin) {
		if (plugin == null)
			return false;
		return isEnabledPlugin(plugin.getName());
	}

	public boolean isDisabledPlugin(String plugin) {
		if (plugin == null || getPlugin(plugin) == null)
			return true;
		return !getPlugin(plugin).isEnabled();
	}

	public boolean isDisabledPlugin(Plugin plugin) {
		if (plugin == null)
			return true;
		return !isEnabledPlugin(plugin.getName());
	}

	public List<String> getPluginsToLoad() {
		List<String> list = new ArrayList<String>();
		if (new File("plugins").isDirectory()) // is folder
			for (File f : new File("plugins").listFiles()) {
				if (f.getName().endsWith(".jar")) {
					if (new File("plugins/" + f + "/plugin.yml").exists()) {
						list.add(f.getName());
					}
				}
			}
		return list;
	}

	public void reloadPlugin(Plugin plugin) {
		try {
			unloadPlugin(plugin);
			loadPlugin(plugin.getDataFolder().getName());
		} catch (Exception e) {
			Error.err("reloading plugin " + plugin.getName(), "Uknown");
		}
	}

	public void unloadPlugin(Plugin plugin) {
		if (plugin == null)
			return;
		unloadPlugin(plugin.getName());
	}

	@SuppressWarnings("unchecked")
	public void unloadPlugin(String pluginName) {
		if (pluginName == null)
			return;
		SimplePluginManager spm = (SimplePluginManager) manager;

		List<Plugin> plugins = null;
		Map<String, Plugin> lookupNames = null;
		Map<Event, SortedSet<RegisteredListener>> listeners = null;
		SimpleCommandMap commandMap = null;
		Map<String, Command> knownCommands = null;

		if (spm != null) {
			try {
				Field pluginsField;
				pluginsField = spm.getClass().getDeclaredField("plugins");

				Field lookupNamesField = spm.getClass().getDeclaredField("lookupNames");
				Field listenersField = spm.getClass().getDeclaredField("listeners");
				Field commandMapField = spm.getClass().getDeclaredField("commandMap");

				pluginsField.setAccessible(true);
				lookupNamesField.setAccessible(true);
				listenersField.setAccessible(true);
				commandMapField.setAccessible(true);

				plugins = (List<Plugin>) pluginsField.get(spm);
				lookupNames = (Map<String, Plugin>) lookupNamesField.get(spm);
				listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(spm);
				commandMap = (SimpleCommandMap) commandMapField.get(spm);

				Field knownCommandsField = commandMap.getClass().getDeclaredField("knownCommands");

				knownCommandsField.setAccessible(true);

				knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			}
		}
		for (Plugin pl : manager.getPlugins()) {
			if (pl.getDescription().getName().equalsIgnoreCase(pluginName)) {
				disablePlugin(pl);
				if (plugins != null && plugins.contains(pl)) {
					plugins.remove(pl);
				}
				if (lookupNames != null && lookupNames.containsKey(pluginName)) {
					lookupNames.remove(pluginName);
				}
				if (listeners != null) {
					for (SortedSet<RegisteredListener> set : listeners.values()) {
						for (Iterator<RegisteredListener> it = set.iterator(); it.hasNext();) {
							RegisteredListener value = it.next();

							if (value.getPlugin() == pl) {
								it.remove();
							}
						}
					}
				}
				if (commandMap != null) {
					for (Iterator<Map.Entry<String, Command>> it = knownCommands.entrySet().iterator(); it.hasNext();) {
						Map.Entry<String, Command> entry = it.next();

						if (entry.getValue() instanceof PluginCommand) {
							PluginCommand c = (PluginCommand) entry.getValue();

							if (c.getPlugin() == pl) {
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
						manager.removePermission(p.next().toString());
					}
				} catch (NoSuchMethodError e) {
				}
			}
		}
	}

	public String loadPlugin(String pluginName) {

		if (pluginName == null)
			return TheAPI.colorize("&4Invalid plugin");
		if (pluginName.endsWith(".jar"))
			pluginName = pluginName.substring(0, pluginName.length() - 4);
		try {
			Plugin p = Bukkit.getPluginManager().loadPlugin(new File("plugins/" + pluginName + ".jar"));
			p.onLoad();
			Bukkit.getPluginManager().enablePlugin(p);
			try {
				final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

				bukkitCommandMap.setAccessible(true);
				CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
				for (String s : p.getDescription().getCommands().keySet())
					commandMap.register(s, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return TheAPI.colorize("&aPlugin loaded");
		} catch (InvalidPluginException e) {
			return TheAPI.colorize("&4Invalid plugin");
		} catch (InvalidDescriptionException e) {
			return TheAPI.colorize("&cInvalid description");
		}
	}

	public boolean disablePlugin(String plugin) {
		if (plugin == null)
			return false;
		if (isEnabledPlugin(plugin)) {
			manager.disablePlugin(getPlugin(plugin));
			return true;
		}
		return false;
	}

	public boolean disablePlugin(Plugin plugin) {
		if (plugin == null)
			return false;
		return disablePlugin(plugin.getName());
	}
}
