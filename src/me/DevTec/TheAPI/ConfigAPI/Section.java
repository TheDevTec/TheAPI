package me.DevTec.TheAPI.ConfigAPI;

import java.util.List;
import java.util.Map;
import java.util.Set;

import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.Data;

public class Section implements Data {
	private final Config c;
	private final String s;

	public Section(Config config, String path) {
		c = config;
		s = path;
	}

	public boolean exists(String path) {
		if (path.trim().isEmpty())
			return c.exists(s);
		return c.exists(s + "." + path);
	}

	public Config getConfig() {
		return c;
	}

	public Section getSection(String path) {
		if (c.exists(s + "." + path))
			return new Section(c, s + "." + path);
		return null;
	}

	public boolean isSection(String path) {
		if (path.trim().isEmpty())
			return c.isSection(s);
		return c.isSection(s + "." + path);
	}

	public void remove(String path) {
		if (path.trim().isEmpty())
			c.remove(s);
		else
			c.remove(s + "." + path);
	}

	public void set(String path, Object value) {
		if (path == null)
			return;
		if (path.trim().isEmpty())
			c.set(s, value);
		else
			c.set(s + "." + path, value);
	}

	public Set<String> getKeys(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getKeys(s);
		return c.getKeys(s + "." + path);
	}

	public Set<String> getKeys(String path, boolean sub) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getKeys(s, sub);
		return c.getKeys(s + "." + path, sub);
	}

	public List<String> getComments(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getComments(s);
		return c.getComments(s + "." + path);
	}

	public void setComments(String path, List<String> value) {
		if (path == null)
			return;
		if (path.trim().isEmpty())
			c.setComments(s, value);
		else
			c.setComments(s + "." + path, value);
	}

	public void addComment(String path, String... value) {
		if (path != null && value != null)
			for (String s : value)
				getComments(path).add(s);
	}

	public void addComments(String path, List<String> value) {
		if (path != null && value != null)
			getComments(path).addAll(value);
	}

	public void removeComment(String path, String... value) {
		if (path != null && value != null)
			for (String s : value)
				getComments(path).remove(s);
	}

	public void removeComment(String path, List<String> value) {
		if (path != null && value != null)
			getComments(path).removeAll(value);
	}

	public Object get(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.get(s);
		return c.get(s + "." + path);
	}

	public int getInt(String path) {
		if (path == null)
			return 0;
		if (path.trim().isEmpty())
			return c.getInt(s);
		return c.getInt(s + "." + path);
	}

	public double getDouble(String path) {
		if (path == null)
			return 0.0;
		if (path.trim().isEmpty())
			return c.getDouble(s);
		return c.getDouble(s + "." + path);
	}

	public long getLong(String path) {
		if (path == null)
			return 0;
		if (path.trim().isEmpty())
			return c.getLong(s);
		return c.getLong(s + "." + path);
	}

	public float getFloat(String path) {
		if (path == null)
			return 0;
		if (path.trim().isEmpty())
			return c.getFloat(s);
		return c.getFloat(s + "." + path);
	}

	public String getString(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getString(s);
		return c.getString(s + "." + path);
	}

	public boolean getBoolean(String path) {
		if (path == null)
			return false;
		if (path.trim().isEmpty())
			return c.getBoolean(s);
		return c.getBoolean(s + "." + path);
	}

	public short getShort(String path) {
		if (path == null)
			return 0;
		if (path.trim().isEmpty())
			return c.getShort(s);
		return c.getShort(s + "." + path);
	}

	public byte getByte(String path) {
		if (path == null)
			return 0;
		if (path.trim().isEmpty())
			return c.getByte(s);
		return c.getByte(s + "." + path);
	}

	public List<Object> getList(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getList(s);
		return c.getList(s + "." + path);
	}

	public List<String> getStringList(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getStringList(s);
		return c.getStringList(s + "." + path);
	}

	public List<Boolean> getBooleanList(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getBooleanList(s);
		return c.getBooleanList(path);
	}

	public List<Byte> getByteList(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getByteList(s);
		return c.getByteList(s + "." + path);
	}

	public List<Integer> getIntegerList(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getIntegerList(s);
		return c.getIntegerList(s + "." + path);
	}

	public List<Float> getFloatList(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getFloatList(s);
		return c.getFloatList(s + "." + path);
	}

	public List<Short> getShortList(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getShortList(s);
		return c.getShortList(s + "." + path);
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

	public List<Map<?, ?>> getMapList(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return c.getMapList(s);
		return c.getMapList(s + "." + path);
	}

	@Override
	public String getDataName() {
		return "Data(Section:" + s + "/" + c.getName() + ")";
	}

}