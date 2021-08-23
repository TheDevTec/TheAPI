package me.devtec.theapi.utils.datakeeper;

import java.util.Collection;
import java.util.HashMap;
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
	protected static final Map<UUID, Data> datasByUUID = new HashMap<>();
	
	private UUID s;
	private String name;
	private Data data;
	private boolean autounload;

	public User(String name) {
		if (name == null) {
			Validator.send("PlayerName cannot be null.");
			return;
		}
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
		if (player == null) {
			Validator.send("Player cannot be null.");
			return;
		}
		s = player.getUniqueId();
		name = player.getName();
		prepareConfig();
	}

	public User(UUID player) {
		if (player == null) {
			Validator.send("UUID cannot be null.");
			return;
		}
		s = player;
		this.name=LoaderClass.cache.lookupNameById(s);
		prepareConfig();
	}

	public User(String name, UUID player) {
		if (player == null||name==null) {
			Validator.send("UUID & PlayerName cannot be null.");
			return;
		}
		s = player;
		this.name = LoaderClass.cache.lookupName(name);
		prepareConfig();
	}

	public User(Cache.Query query) {
		if (query == null || query.name == null || query.uuid == null) {
			Validator.send("Query cannot be null.");
			return;
		}
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

	private void prepareConfig() {
		Data d = datasByUUID.get(s);
		if(d==null)
			datasByUUID.put(s, d=new Data("plugins/TheAPI/User/" + s + ".yml", true));
		data = d;
	}
	
	public Data clearCache() {
		return datasByUUID.remove(s);
	}

	public void delete() {
	 data.clear();
	 data.getFile().delete();
		datasByUUID.remove(s);
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
		return data;
	}

	public Data getData() {
		return data;
	}

	public void reload() {
	 data.reload(data.getFile());
	}

	public boolean exists(String path) {
		if (path == null)
			return false;
		return data.exists(path);
	}

	public void remove(String path) {
		if (path == null)
			return;
	 data.remove(path);
		if (!LoaderClass.config.getBoolean("Options.Cache.User.Use"))
			save();
	}

	public Set<String> getKeys(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return data.getKeys();
		return data.getKeys(path);
	}

	public Set<String> getKeys() {
		return data.getKeys();
	}

	public Set<String> getKeys(String path, boolean sub) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return data.getKeys(sub);
		return data.getKeys(path, sub);
	}

	public Set<String> getKeys(boolean sub) {
		return data.getKeys(sub);
	}

	public Object get(String path) {
		if (path == null)
			return null;
		return data.get(path);
	}

	public int getInt(String path) {
		if (path == null)
			return 0;
		return data.getInt(path);
	}

	public double getDouble(String path) {
		if (path == null)
			return 0.0;
		return data.getDouble(path);
	}

	public long getLong(String path) {
		if (path == null)
			return 0;
		return data.getLong(path);
	}

	public String getString(String path) {
		if (path == null)
			return null;
		return data.getString(path);
	}

	public boolean getBoolean(String path) {
		if (path == null)
			return false;
		return data.getBoolean(path);
	}

	public short getShort(String path) {
		if (path == null)
			return 0;
		return data.getShort(path);
	}

	public byte getByte(String path) {
		if (path == null)
			return 0;
		return data.getByte(path);
	}

	public float getFloat(String path) {
		if (path == null)
			return 0;
		return data.getFloat(path);
	}

	public Collection<Object> getList(String path) {
		if (path == null)
			return null;
		return data.getList(path);
	}

	public List<String> getStringList(String path) {
		if (path == null)
			return null;
		return data.getStringList(path);
	}

	public List<Boolean> getBooleanList(String path) {
		if (path == null)
			return null;
		return data.getBooleanList(path);
	}

	public List<Byte> getByteList(String path) {
		if (path == null)
			return null;
		return data.getByteList(path);
	}

	public List<Integer> getIntegerList(String path) {
		if (path == null)
			return null;
		return data.getIntegerList(path);
	}

	public List<Float> getFloatList(String path) {
		if (path == null)
			return null;
		return data.getFloatList(path);
	}

	public List<Long> getLongList(String path) {
		if (path == null)
			return null;
		return data.getLongList(path);
	}

	public List<Short> getShortList(String path) {
		if (path == null)
			return null;
		return data.getShortList(path);
	}

	public <K, V> List<Map<K, V>> getMapList(String path) {
		if (path == null)
			return null;
		return data.getMapList(path);
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
		return data.isKey(path);
	}

	public ItemStack getItemStack(String key) {
		return data.getAs(key, ItemStack.class);
	}

	public List<ItemStack> getItemStacks(String key) {
		return data.getListAs(key, ItemStack.class);
	}

	public void set(String key, Object o) {
		if (key == null)
			return;
	 data.set(key, o);
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
		 data.save(DataType.valueOf(LoaderClass.config.getString("Options.User-SavingType").toUpperCase()));
		} catch (Exception r) {
		 data.save(DataType.YAML);
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
	 * @see User#getBoolean
	 * @param key Path in config
	 * @return boolean
	 */
	public boolean has(String key) {
		return getBoolean(key);
	}
}
