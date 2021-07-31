package me.devtec.theapi.utils.datakeeper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.theapiutils.Cache;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.Validator;

public class User implements me.devtec.theapi.utils.datakeeper.abstracts.Data {
	
	private UUID s;
	private String name;
	private Data a;
	private boolean autounload;

	public User(String name) {
		if (name == null)
			Validator.send("String cannot be null.");
		try {
			s = UUID.fromString(name);
			this.name=LoaderClass.cache.lookupNameById(s);
		} catch (Exception e) {
			if(LoaderClass.cache!=null) {
				this.name=LoaderClass.cache.lookupName(name);
				s=LoaderClass.cache.lookupId(name);
			}
		}
		prepareConfig();
	}

	public User(Player player) {
		if (player == null)
			Validator.send("Player cannot be null.");
		s = player.getUniqueId();
		name = player.getName();
		prepareConfig();
	}

	public User(UUID player) {
		if (player == null)
			Validator.send("UUID cannot be null.");
		s = player;
		this.name=LoaderClass.cache.lookupNameById(s);
		prepareConfig();
	}

	public User(String name, UUID player) {
		if (name == null||player == null)
			Validator.send("UUID cannot be null.");
		s = player;
		this.name = name;
		prepareConfig();
	}

	public User(Cache.Query query) {
		if (query == null)
			Validator.send("Query cannot be null.");
		s = query.uuid;
		this.name = query.name;
		prepareConfig();
	}
	
	public void setAutoUnload(boolean unload) {
		autounload=unload;
	}
	
	public boolean getAutoUnload() {
		return autounload;
	}

	private final void prepareConfig() {
		a = new Data("plugins/TheAPI/User/" + s + ".yml", true);
	}

	public void delete() {
		a.getFile().delete();
	}

	public boolean isOnline() {
		return TheAPI.getPlayerOrNull(name) != null;
	}

	public UUID getUUID() {
		return s;
	}

	public String getName() {
		return name;
	}

	public String getDataName() {
		return "User(" + name + ")";
	}

	public Data data() {
		return a;
	}

	public Data getData() {
		return a;
	}

	public void reload() {
		a.reload(a.getFile());
	}

	public boolean exists(String path) {
		if (path == null)
			return false;
		return a.exists(path);
	}

	public void remove(String path) {
		if (path == null)
			return;
		a.remove(path);
		if (!LoaderClass.config.getBoolean("Options.Cache.User.Use"))
			save();
	}

	public Set<String> getKeys(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return a.getKeys();
		return a.getKeys(path);
	}

	public Set<String> getKeys() {
		return a.getKeys();
	}

	public Set<String> getKeys(String path, boolean sub) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return a.getKeys(sub);
		return a.getKeys(path, sub);
	}

	public Set<String> getKeys(boolean sub) {
		return a.getKeys(sub);
	}

	public Object get(String path) {
		if (path == null)
			return null;
		return a.get(path);
	}

	public int getInt(String path) {
		if (path == null)
			return 0;
		return a.getInt(path);
	}

	public double getDouble(String path) {
		if (path == null)
			return 0.0;
		return a.getDouble(path);
	}

	public long getLong(String path) {
		if (path == null)
			return 0;
		return a.getLong(path);
	}

	public String getString(String path) {
		if (path == null)
			return null;
		return a.getString(path);
	}

	public boolean getBoolean(String path) {
		if (path == null)
			return false;
		return a.getBoolean(path);
	}

	public short getShort(String path) {
		if (path == null)
			return 0;
		return a.getShort(path);
	}

	public byte getByte(String path) {
		if (path == null)
			return 0;
		return a.getByte(path);
	}

	public float getFloat(String path) {
		if (path == null)
			return 0;
		return a.getFloat(path);
	}

	public Collection<Object> getList(String path) {
		if (path == null)
			return null;
		return a.getList(path);
	}

	public List<String> getStringList(String path) {
		if (path == null)
			return null;
		return a.getStringList(path);
	}

	public List<Boolean> getBooleanList(String path) {
		if (path == null)
			return null;
		return a.getBooleanList(path);
	}

	public List<Byte> getByteList(String path) {
		if (path == null)
			return null;
		return a.getByteList(path);
	}

	public List<Integer> getIntegerList(String path) {
		if (path == null)
			return null;
		return a.getIntegerList(path);
	}

	public List<Float> getFloatList(String path) {
		if (path == null)
			return null;
		return a.getFloatList(path);
	}

	public List<Long> getLongList(String path) {
		if (path == null)
			return null;
		return a.getLongList(path);
	}

	public List<Short> getShortList(String path) {
		if (path == null)
			return null;
		return a.getShortList(path);
	}

	public <K, V> List<Map<K, V>> getMapList(String path) {
		if (path == null)
			return null;
		return a.getMapList(path);
	}

	public boolean isString(String path) {
		return get(path) instanceof String;
	}

	public boolean isNumber(String path) {
		return get(path) instanceof Number;
	}

	public boolean isInt(String path) {
		return get(path) instanceof Integer;
	}

	public boolean isInteger(String path) {
		return get(path) instanceof Integer;
	}

	public boolean isDouble(String path) {
		return get(path) instanceof Double;
	}

	public boolean isFloat(String path) {
		return get(path) instanceof Float;
	}

	public boolean isLong(String path) {
		return get(path) instanceof Long;
	}

	public boolean isByte(String path) {
		return get(path) instanceof Byte;
	}

	public boolean isShort(String path) {
		return get(path) instanceof Short;
	}

	public boolean isList(String path) {
		return get(path) instanceof List;
	}

	public boolean isMap(String path) {
		return get(path) instanceof Map;
	}

	public boolean isBoolean(String path) {
		return get(path) instanceof Boolean;
	}

	public boolean isSection(String path) {
		return a.isKey(path);
	}

	public ItemStack getItemStack(String key) {
		return a.getAs(key, ItemStack.class);
	}

	public List<ItemStack> getItemStacks(String key) {
		return a.getListAs(key, ItemStack.class);
	}

	public void set(String key, Object o) {
		if (key == null)
			return;
		a.set(key, o);
		if (!LoaderClass.config.getBoolean("Options.Cache.User.Use"))
			save();
	}

	public void setSave(String key, Object o) {
		setAndSave(key, o);
	}

	public void setAndSave(String key, Object o) {
		set(key, o);
		if (LoaderClass.config.getBoolean("Options.Cache.User.Use"))
		save();
	}

	public void save() {
		try {
			a.save(DataType.valueOf(LoaderClass.config.getString("Options.User-SavingType").toUpperCase()));
		} catch (Exception r) {
			a.save(DataType.YAML);
		}
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

	/**
	 * @see see getBoolean(key) method
	 * @param key Path in config
	 * @return boolean
	 */
	public boolean has(String key) {
		return getBoolean(key);
	}
}
