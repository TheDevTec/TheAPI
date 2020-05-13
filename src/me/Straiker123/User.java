package me.Straiker123;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class User {
	private UUID s;
	private ConfigAPI a;
	@SuppressWarnings("deprecation")
	public User(String name) {
		try {
			s=UUID.fromString(name);
		}catch(Exception e) {
			s=Bukkit.getOfflinePlayer(name).getUniqueId();
		}
		a=new ConfigAPI("TheAPI/User",s.toString());
		a.create();
	}
	
	public User(Player player) {
		s=player.getUniqueId();
		a=new ConfigAPI("TheAPI/User",s.toString());
		a.create();
	}
	
	public User(UUID player) {
		s=player;
		a=new ConfigAPI("TheAPI/User",s.toString());
		a.create();
	}
	
	public void delete() {
		s=null;
		a.delete();
	}
	
	public UUID getUUID() {
		return s;
	}
	
	public String getName() {
		return Bukkit.getOfflinePlayer(s).getName();
	}
	
	public ConfigAPI config() {
		return a;
	}
	
	public ConfigAPI getConfig() {
		return a;
	}
	
	public void set(String key, Object o) {
		a.set(key, o);
	}
	
	public void setAndSave(String key, Object o) {
		a.set(key, o);
		a.save();
	}
	
	public void save() {
		a.save();
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
		return getString(key)!=null;
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
