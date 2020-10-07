package me.DevTec.TheAPI.Utils.DataKeeper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DevTec.TheAPI.Utils.TheCoder;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

public class User implements me.DevTec.TheAPI.Utils.DataKeeper.Abstract.Data {
	private UUID s;
	private String name;
	private Data a;

	@SuppressWarnings("deprecation")
	public User(String name) {
		if(name==null)
			new Exception("String cannot be null.").printStackTrace();
		try {
			s = UUID.fromString(name);
			name = Bukkit.getOfflinePlayer(s).getName();
		} catch (Exception e) {
			s = Bukkit.getOfflinePlayer(name).getUniqueId();
			this.name = name;
		}
		prepareConfig();
	}

	public User(Player player) {
		if(player==null)
			new Exception("Player cannot be null.").printStackTrace();
		s = player.getUniqueId();
		name = player.getName();
		prepareConfig();
	}

	public User(UUID player) {
		if(player==null)
			new Exception("UUID cannot be null.").printStackTrace();
		s = player;
		name = Bukkit.getOfflinePlayer(s).getName();
		prepareConfig();
	}
	
	private final void prepareConfig() {
		File file = new File("plugins/TheAPI/User/"+s.toString()+".yml");
    	if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
				file.createNewFile();
			} catch (Exception e) {
			}
        }
    	a=new Data(file, true);
	}
	
	public void delete() {
		a.getFile().delete();
		s = null;
		a = null;
	}

	public UUID getUUID() {
		return s;
	}

	public String getName() {
		return name;
	}

	public String getDataName() {
		return "User("+name+")";
	}

	public Data data() {
		return a;
	}

	public Data getData() {
		return a;
	}

	public Object get(String key) {
		return a.get(key);
	}
	
	public boolean isSection(String key) {
		return a.isKey(key);
	}

	public ItemStack getItemStack(String key) {
		try {
			ItemStack stack = TheCoder.fromString(getString(key));
			if(stack==null)return (ItemStack)get(key);
			return stack;
		} catch (Exception e) {
			return (ItemStack) get(key);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ItemStack> getItemStacks(String key) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		try {
			List<String> inSet = a.getStringList(key);
			for (String o : inSet)
				list.add((ItemStack) TheCoder.fromStringToList(o));
			if(list.contains(null))return (List<ItemStack>) getList(key);
			return list;
		} catch (Exception e) {
			list = (List<ItemStack>) getList(key);
		}
		return list;
	}
	
	public void set(String key, Object o) {
		a.set(key, o);
		if(!LoaderClass.config.getBoolean("Options.Cache.User.Use"))
			save();
	}

	public void setSave(String key, Object o) {
		setAndSave(key, o);
	}

	public void setAndSave(String key, Object o) {
		set(key, o);
		save();
	}

	public void save() {
		try {
			a.save(DataType.valueOf(LoaderClass.config.getString("Options.User-SavingType").toUpperCase()));
		}catch(Exception r) {
			a.save(DataType.YAML);
		}
	}

	public Set<String> getKeys(String key) {
		return a.getKeys(key);
	}

	public Set<String> getKeys() {
		return a.getKeys();
	}

	public Set<String> getKeys(String key, boolean sub) {
		return a.getKeys(key,sub);
	}

	public Set<String> getKeys(boolean sub) {
		return a.getKeys(sub);
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
		return a.exists(key);
	}
	
	private static Pattern isD = Pattern.compile("[0-9.-]+"),isN = Pattern.compile("[0-9-]+");

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
		return a.get(key) instanceof String;
	}

	public boolean isDouble(String key) {
		return a.get(key) instanceof Double || isD.matcher(getString(key)).find();
	}

	public boolean isInt(String key) {
		return a.get(key) instanceof Long || isN.matcher(getString(key)).find();
	}

	public boolean isList(String key) {
		return a.get(key) instanceof List;
	}

	public boolean isLong(String key) {
		return a.get(key) instanceof Long || isN.matcher(getString(key)).find();
	}

	public boolean isFloat(String key) {
		return a.get(key) instanceof Float || a.get(key) instanceof Long || a.get(key) instanceof Double || isD.matcher(getString(key)).find();
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

	public float getFloat(String key) {
		return a.getFloat(key);
	}
}
