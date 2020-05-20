package me.Straiker123;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class User {
	private UUID s;
	private String name;
	private ConfigAPI a;

	@SuppressWarnings("deprecation")
	public User(String name) {
		if(name==null)
			new Exception("String cannot be null.").printStackTrace();
		try {
			s = UUID.fromString(name);
			name = Bukkit.getOfflinePlayer(s).getName();
		} catch (Exception e) {
			s = Bukkit.getOfflinePlayer(name).getUniqueId();
			name = this.name;
		}
		a = new ConfigAPI("TheAPI/User", s.toString());
		a.create();
	}

	public User(Player player) {
		if(player==null)
			new Exception("Player cannot be null.").printStackTrace();
		s = player.getUniqueId();
		name = player.getName();
		a = new ConfigAPI("TheAPI/User", s.toString());
		a.create();
	}

	public User(UUID player) {
		if(player==null)
			new Exception("UUID cannot be null.").printStackTrace();
		s = player;
		name = Bukkit.getOfflinePlayer(s).getName();
		a = new ConfigAPI("TheAPI/User", s.toString());
		a.create();
	}

	public void delete() {
		s = null;
		a.delete();
	}

	public UUID getUUID() {
		return s;
	}

	public String getName() {
		return name;
	}

	public ConfigAPI config() {
		return a;
	}

	public Object get(String key) {
		return a.get(key);
	}

	public ItemStack getItemStack(String key) {
		try {
			String inSet = getString(key);
			return (ItemStack) TheAPI.getStringUtils().getTheCoder().getObjectFromString(inSet);
		} catch (Exception e) {
			return (ItemStack) get(key);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ItemStack> getItemStacks(String key) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		try {
			List<String> inSet = (List<String>) getList(key);
			for (String o : inSet) {
				list.add((ItemStack) TheAPI.getStringUtils().getTheCoder().getObjectFromString(o));
			}
		} catch (Exception e) {
			list = (List<ItemStack>) getList(key);
		}
		return list;
	}

	public ConfigAPI getConfig() {
		return a;
	}

	public void set(String key, Object o) {
		if (o instanceof ItemStack) {
			o = TheAPI.getStringUtils().getTheCoder().toString(o);
		}
		if (o instanceof List && ((List<?>) o).get(0) instanceof ItemStack) {
			@SuppressWarnings("unchecked")
			List<ItemStack> list = (List<ItemStack>) o;
			List<String> newList = new ArrayList<String>();
			for (ItemStack os : list)
				newList.add(TheAPI.getStringUtils().getTheCoder().toString(os));
			o = newList;
		}
		a.set(key, o);
	}

	public void setSave(String key, Object o) {
		setAndSave(key, o);
	}

	public void setAndSave(String key, Object o) {
		set(key, o);
		a.save();
	}

	public void save() {
		a.save();
	}

	public ConfigurationSection getConfigurationSection(String key) {
		return a.getConfigurationSection(key);
	}

	public Set<String> getConfigurationSection(String key, boolean getKeys) {
		return a.getConfigurationSection(key, getKeys);
	}

	public Set<String> getKeys(String key) {
		return a.getConfigurationSection(key, false);
	}

	public Set<String> getKeys() {
		return a.getKeys(false);
	}

	public List<String> getStringList(String key) {
		return a.getStringList(key);
	}

	public List<?> getList(String key) {
		return a.getList(key);
	}

	public boolean exist(String key) {
		return exists(key);
	}

	public boolean existsPath(String key) {
		return exists(key);
	}

	public boolean existPath(String key) {
		return exists(key);
	}

	public boolean isNull(String key) {
		return !exists(key);
	}

	public boolean exists(String key) {
		return getString(key) != null;
	}

	/**
	 * @see see getBoolean(key) method
	 * @param key Path in config
	 * @return boolean
	 */
	public boolean has(String key) {
		return getBoolean(key);
	}

	public boolean getBoolean(String key) {
		return a.getBoolean(key);
	}

	public boolean isString(String key) {
		return a.isString(key);
	}

	public boolean isDouble(String key) {
		return a.isDouble(key);
	}

	public boolean isInt(String key) {
		return a.isInt(key);
	}

	public boolean isLong(String key) {
		return a.isLong(key);
	}

	public boolean isConfigurationSection(String key) {
		return a.isConfigurationSection(key);
	}

	public String getString(String key) {
		return a.getString(key);
	}

	public int getInt(String key) {
		return a.getInt(key);
	}

	public double getDouble(String key) {
		return a.getDouble(key);
	}

	public long getLong(String key) {
		return a.getLong(key);
	}

}
