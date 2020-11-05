package me.DevTec.TheAPI.ConfigAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

/*
 * @see see Prefer Config.class (Data.class)
 */
@Deprecated
public class ConfigAPI {
	private String name, h, loc, end = "yml";
	private FileConfiguration a;
	private File f, folder;
	private Map<String, Object> c = new HashMap<>();

	public ConfigAPI(String localization, String configName) {
		name = configName;
		loc = localization;
	}

	public ConfigAPI(Plugin plugin, String configName) {
		name = configName;
		loc = plugin.getName();
	}

	public String getName() {
		return name;
	}

	public String getFolderName() {
		return loc;
	}

	public File getFolder() {
		if (folder == null)
			folder = new File("plugins/" + loc);
		if (!folder.exists())
			folder.mkdir();
		return folder;
	}

	public boolean existsPath(String string) {
		return getString(string) != null;
	}

	public boolean exists(String string) {
		return existsPath(string);
	}

	public boolean exist(String string) {
		return existsPath(string);
	}

	public boolean isNull(String string) {
		return !existsPath(string);
	}

	public boolean existPath(String string) {
		return existsPath(string);
	}

	public void addDefault(String path, Object value) {
		c.put(path, value);
		if (a != null)
			a.addDefault(path, value);
	}

	public void addDefaults(Map<String, Object> defaults) {
		c = defaults;
		if (a != null)
			a.addDefaults(defaults);
	}

	public void addDefaults(HashMap<String, Object> defaults) {
		c = defaults;
		if (a != null)
			a.addDefaults(defaults);
	}

	public boolean existFile() {
		return new File(getFolder(), name + "." + end) != null;
	}

	public File getFile() {
		if (f == null) {
			File ff = new File(getFolder(), name + "." + end);
			try {
				if (ff.exists()) { // file exists
					f = ff;
				} else {
					try {
						ff.createNewFile();
						f = ff;
					} catch (Exception e) {
						Validator.send("Creating file exception", e);
					}
					f = ff;
				}
			} catch (Exception er) {
				ff = new File(getFolder(), name + "-fixed." + end);
				try {
					ff.createNewFile();
					f = ff;
				} catch (IOException e) {
					Validator.send("Creating file exception", e);
				}
			}
		}
		return f;
	}

	public void setHeader(String header) {
		h = header;
	}

	public FileConfiguration getConfig() {
		return a;
	}

	public void setCustomEnd(String customEnd) {
		if (customEnd != null)
			end = customEnd;
	}

	public boolean remove(String path) {
		return removePath(path);
	}

	public boolean removePath(String path) {
		if (getString(path) != null) {
			set(path, null);
			return true;
		}
		return false;
	}

	public boolean createPath(String path) {
		if (getString(path) == null) {
			createSection(path);
			return true;
		}
		return false;
	}

	public void createSection(String path) {
		if (!existFile())
			return;
		if (a == null)
			YamlConfiguration.loadConfiguration(getFile()).createSection(path);
		a.createSection(path);
	}

	private boolean check() {
		if (a == null) {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cFileConfiguration is null"));
			return false;
		} else if (!existFile()) {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cFile is null"));
			return false;
		} else
			return true;
	}

	public boolean save() {
		try {
			if (check()) {
				a.save(f);
				return true;
			}
			return false;
		} catch (Exception e) {
			Validator.send("Saving config exception", e);
			return false;
		}
	}

	public boolean reload() {
		try {
			f = null;
			a = null;
			f = getFile();
			Reader reader = new InputStreamReader(new FileInputStream(f), "UTF-8");
			a = YamlConfiguration.loadConfiguration(reader);
			if (h != null)
				a.options().header(h);
			if (c != null && !c.isEmpty()) {
				a.addDefaults(c);
			}
			a.options().copyDefaults(true).copyHeader(true);
			save();
			return true;
		} catch (Exception e) {
			Validator.send("Saving file exception", e);
			return false;
		}
	}

	public int getInt(String path) {
		if (!existFile())
			return 0;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getInt(path);
		return a.getInt(path);
	}

	public double getDouble(String path) {
		if (!existFile())
			return 0;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getDouble(path);
		return a.getDouble(path);
	}

	public String getString(String path) {
		if (!existFile())
			return null;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getString(path);
		return a.getString(path);
	}

	public long getLong(String path) {
		if (!existFile())
			return 0;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getLong(path);
		return a.getLong(path);
	}

	public boolean getBoolean(String path) {
		if (!existFile())
			return false;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getBoolean(path);
		return a.getBoolean(path);
	}

	public ConfigurationSection getConfigurationSection(String path) {
		if (!existFile())
			return null;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getConfigurationSection(path);
		return a.getConfigurationSection(path);
	}

	public Set<String> getKeys(boolean keys) {
		if (!existFile())
			return null;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getKeys(keys);
		return a.getKeys(keys);
	}

	public Set<String> getKeys(String key) {
		return getConfigurationSection(key, false);
	}

	public Set<String> getConfigurationSection(String path, boolean keys) {
		if (!existFile())
			return null;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getConfigurationSection(path).getKeys(keys);
		return a.getConfigurationSection(path).getKeys(keys);
	}

	public void set(String path, Object value) {
		if (!existFile())
			return;
		if (a == null)
			YamlConfiguration.loadConfiguration(getFile()).set(path, value);
		a.set(path, value);
	}

	public boolean isString(String path) {
		if (!existFile())
			return false;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).isString(path);
		return a.isString(path);
	}

	public boolean isLong(String path) {
		if (!existFile())
			return false;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).isLong(path);
		return a.isLong(path);
	}

	public boolean isList(String path) {
		if (!existFile())
			return false;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).isList(path);
		return a.isList(path);
	}

	public boolean isDouble(String path) {
		if (!existFile())
			return false;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).isDouble(path);
		return a.isDouble(path);
	}

	public boolean isInt(String path) {
		if (!existFile())
			return false;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).isBoolean(path);
		return a.isInt(path);
	}

	public boolean isBoolean(String path) {
		if (!existFile())
			return false;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).isBoolean(path);
		return a.isBoolean(path);
	}

	public boolean isConfigurationSection(String path) {
		if (!existFile())
			return false;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).isConfigurationSection(path);
		return a.isConfigurationSection(path);
	}

	public Object get(String path) {
		if (!existFile())
			return null;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).get(path);
		return a.get(path);
	}

	public List<String> getStringList(String path) {
		if (!existFile())
			return null;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getStringList(path);
		return a.getStringList(path);
	}

	public List<?> getList(String path) {
		if (!existFile())
			return null;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getList(path);
		return a.getList(path);
	}

	public Color getColor(String path) {
		if (!existFile())
			return null;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getColor(path);
		return a.getColor(path);
	}

	public ItemStack getItemStack(String path) {
		if (!existFile())
			return null;
		if (a == null)
			return YamlConfiguration.loadConfiguration(getFile()).getItemStack(path);
		return a.getItemStack(path);
	}

	public boolean create() {
		try {
			if (f == null)
				f = getFile();
			try {
				if (a == null) {
					Reader reader = new InputStreamReader(new FileInputStream(f), "UTF-8");
					a = YamlConfiguration.loadConfiguration(reader);
				}
			} catch (Exception repeat) {
				a = YamlConfiguration.loadConfiguration(f);
			}
			if (h != null)
				a.options().header(h);
			if (c != null && !c.isEmpty()) {
				a.addDefaults(c);
			}
			a.options().copyDefaults(true).copyHeader(true);
			save();
			return true;
		} catch (Exception e) {
			Validator.send("Creating config exception", e);
			return false;
		}
	}

	public Map<String, Object> getDefaults() {
		return c;
	}

	public boolean delete() {
		if (f == null)
			f = getFile();
		if (f.exists()) {
			f.delete();
			c.clear();
			return true;
		}
		return false;
	}

}
