package me.devtec.theapi.utils.theapiutils.command;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.reflections.Ref;

public class TAC_Pm {

	public TAC_Pm(CommandSender s, String[] args) {
		if(args.length==1) {
			TheAPI.msg("&e/TheAPI Pm Load [pluginName]", s);
			TheAPI.msg("&e/TheAPI Pm Unload [plugin]", s);
			TheAPI.msg("&e/TheAPI Pm Reload [plugin]", s);
			TheAPI.msg("&e/TheAPI Pm Info [plugin]", s);
			return;
		}
		if(args[1].equalsIgnoreCase("load")) {
			if(args.length==2) {
				TheAPI.msg("&e/TheAPI Pm Load [pluginName]", s);
				return;
			}
			Object[] toLoad = getPlugin(StringUtils.buildString(2, args));
			if(toLoad==null) {
				TheAPI.msg("&7Plugin not found.", s);
				return;
			}
			String name = (String) toLoad[1];
			if(Bukkit.getPluginManager().getPlugin(name)!=null) {
				TheAPI.msg("&7Plugin is already loaded.", s);
				return;
			}
			TheAPI.msg("&7Loading plugin...", s);
			Plugin c = load((File)toLoad[0]);
			if(c==null) {
				TheAPI.msg("&7Failed to load plugin.", s);
				return;
			}
			TheAPI.msg("&7Plugin &e"+c.getName()+" loaded & enabled.", s);
			return;
		}
		if(args[1].equalsIgnoreCase("unload")) {
			if(args.length==2) {
				TheAPI.msg("&e/TheAPI Pm Unload [plugin]", s);
				return;
			}
			Plugin pl = Bukkit.getPluginManager().getPlugin(StringUtils.buildString(2, args));
			if(pl==null) {
				TheAPI.msg("&7Plugin not found.", s);
				return;
			}
			unload(pl);
			TheAPI.msg("&7Plugin &e"+pl.getName()+" unloaded ", s);
			return;
		}
		if(args[1].equalsIgnoreCase("reload")) {
			if(args.length==2) {
				TheAPI.msg("&e/TheAPI Pm Reload [plugin]", s);
				return;
			}
			Plugin pl = Bukkit.getPluginManager().getPlugin(StringUtils.buildString(2, args));
			if(pl==null) {
				TheAPI.msg("&7Plugin not found.", s);
				return;
			}
			unload(pl);
			TheAPI.msg("&7Plugin &e"+pl.getName()+" unloaded ", s);
			
			Object[] toLoad = getPlugin(pl.getName());
			if(toLoad==null) {
				TheAPI.msg("&7Plugin not found.", s);
				return;
			}
			String name = (String) toLoad[1];
			if(Bukkit.getPluginManager().getPlugin(name)!=null) {
				TheAPI.msg("&7Plugin is already loaded.", s);
				return;
			}
			TheAPI.msg("&7Loading plugin...", s);
			Plugin c = load((File)toLoad[0]);
			if(c==null) {
				TheAPI.msg("&7Failed to load plugin.", s);
				return;
			}
			TheAPI.msg("&7Plugin &e"+c.getName()+" loaded & enabled.", s);
			return;
		}
		if(args[1].equalsIgnoreCase("info")) {
			if(args.length==2) {
				TheAPI.msg("&e/TheAPI Pm Info [plugin]", s);
				return;
			}
			Plugin pl = Bukkit.getPluginManager().getPlugin(StringUtils.buildString(2, args));
			if(pl==null) {
				TheAPI.msg("&7Plugin not found.", s);
				return;
			}
			TheAPI.msg("&7Informations about plugin &e"+pl.getName()+"&7:", s);
			TheAPI.msg("&7State: &e" + (!pl.isNaggable() ? "Enabled" : "Disabled"), s);
			if (pl.getDescription().getCommands().size() != 0) {
			    TheAPI.msg("&7Commands:", s);
			    for (String a : pl.getDescription().getCommands().keySet())
			        TheAPI.msg("&7 - &e" + a, s);
			}
			if(TheAPI.isNewerThan(12))
				if (pl.getDescription().getAPIVersion() != null)
				    TheAPI.msg("&7Version: &e" + pl.getDescription().getAPIVersion(), s);
			if (pl.getDescription().getWebsite() != null)
			    TheAPI.msg("&7Website: &e" + pl.getDescription().getWebsite(), s);
			TheAPI.msg("&7MainClass: &e" + pl.getDescription().getMain(), s);
			TheAPI.msg("&7Author(s): &e" + StringUtils.join(pl.getDescription().getAuthors(), ", "), s);
			if (!pl.getDescription().getSoftDepend().isEmpty())
			    TheAPI.msg("&7SoftDepend(s): &e" + StringUtils.join(pl.getDescription().getSoftDepend(), ", "), s);
			if (!pl.getDescription().getDepend().isEmpty())
			    TheAPI.msg("&7Depend(s): &e" + StringUtils.join(pl.getDescription().getDepend(), ", "), s);
			return;
		}
	}

	public static Object[] getPlugin(String name) {
		if(name.endsWith(".jar"))name=name.substring(0, name.length()-4);
		File ff = new File("plugins");
		Data data = new Data();
		if (ff.exists() && ff.isDirectory()) // is folder
			for (File f : ff.listFiles()) {
				if (!f.isDirectory() && f.getName().endsWith(".jar")) {
					try {
						JarFile jar = new JarFile(f);
						data.reload(StreamUtils.fromStream(jar.getInputStream(jar.getEntry("plugin.yml"))));
						jar.close();
						if(data.getString("name").equalsIgnoreCase(name) || f.getName().substring(0, f.getName().length()-4).equalsIgnoreCase(name))return new Object[] {f,data.getString("name")};
					}catch(Exception err) {}
					data.reset();
				}
			}
		return null;
	}

	public static Map<String, File> getPluginsToLoad() {
		File ff = new File("plugins");
		Data data = new Data();
		Map<String, File> m = new HashMap<>();
		if (ff.exists() && ff.isDirectory()) // is folder
			for (File f : ff.listFiles()) {
				if (!f.isDirectory() && f.getName().endsWith(".jar")) {
					try {
						JarFile jar = new JarFile(f);
						data.reload(StreamUtils.fromStream(jar.getInputStream(jar.getEntry("plugin.yml"))));
						jar.close();
						if(Bukkit.getPluginManager().getPlugin(data.getString("name"))==null)
							m.put(data.getString("name"), f);
					}catch(Exception err) {}
					data.reset();
				}
			}
		return m;
	}
	
	public static Plugin load(File file) {
		try {
			Plugin load = Bukkit.getPluginManager().loadPlugin(file);
			load.onLoad();
			Bukkit.getPluginManager().enablePlugin(load);
			return load;
		}catch(Exception err) {
			err.printStackTrace();
			return null;
		}
	}
	
	private static final Field fPls = Ref.field(SimplePluginManager.class, "plugins");
	private static final Field lNms = Ref.field(SimplePluginManager.class, "lookupNames");
	private static final SimpleCommandMap commandMap = (SimpleCommandMap)Ref.get(Bukkit.getPluginManager(), "commandMap");
	@SuppressWarnings("unchecked")
	private static final Map<String, Command> knownCommands = (Map<String, Command>) Ref.get(commandMap, "knownCommands");
	
	@SuppressWarnings("unchecked")
	public static void unload(Plugin plugin) {
		//disable plugin
		try {
			Bukkit.getPluginManager().disablePlugin(plugin);
		}catch(Exception err) {
			err.printStackTrace();
		}
		//unload plugin
		List<Plugin> plugins = (List<Plugin>) Ref.get(((SimplePluginManager) Bukkit.getPluginManager()),fPls);
		Map<String, Plugin> lookupNames = (Map<String, Plugin>) Ref.get(((SimplePluginManager) Bukkit.getPluginManager()),lNms);
		if (plugins != null)
			plugins.remove(plugin);
		if (lookupNames != null)
			lookupNames.remove(plugin.getName().replace(' ', '_').toLowerCase(Locale.ENGLISH));
		if (commandMap != null)
			for (Iterator<Map.Entry<String, Command>> it = knownCommands.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Command> entry = it.next();
				if (entry.getValue() instanceof PluginCommand) {
					PluginCommand c = (PluginCommand) entry.getValue();
					if (c.getPlugin().equals(plugin)) {
						c.unregister(commandMap);
						it.remove();
					}
				}
			}
		try {
			List<Permission> permissionlist = plugin.getDescription().getPermissions();
			for (Permission permission : permissionlist) Bukkit.getPluginManager().removePermission(permission.toString());
		} catch (Exception | NoSuchMethodError e) {
		}
	}
	

}
