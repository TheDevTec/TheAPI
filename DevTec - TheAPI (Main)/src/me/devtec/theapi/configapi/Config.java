package me.devtec.theapi.configapi;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.plugin.Plugin;

import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;
import me.devtec.theapi.utils.datakeeper.loader.ByteLoader;
import me.devtec.theapi.utils.datakeeper.loader.DataLoader;
import me.devtec.theapi.utils.datakeeper.loader.JsonLoader;

public class Config implements me.devtec.theapi.utils.datakeeper.abstracts.Data {
	
	public static class Node {
		public Node(Object value, List<String> comments){
			this.value=value;
			this.comments=comments;
		}
		
		public Node(Object value, String...strings){
			this.value=value;
			if(strings!=null && strings.length!=0)
			this.comments=Arrays.asList(strings);
		}
		
		private Object value;
		private List<String> comments;
		
		public Object getValue() {
			return value;
		}
		
		public Object setValue(Object value) {
			Object old = this.value;
			this.value=value;
			return old;
		}
		
		public List<String> getComments() {
			return comments;
		}
		
		public List<String> setComments(List<String> comments) {
			List<String> old = this.comments;
			this.comments=comments;
			return old;
		}
	}
	
	public static Config loadConfig(Plugin plugin, String pathToConfig, String outputFile) { 
		Config c = new Config(outputFile);
		Data load = new Data();
		load.reload(StreamUtils.fromStream(plugin.getResource(pathToConfig)));
		DataLoader loader = load.getDataLoader();
		if(loader instanceof JsonLoader)c.t=DataType.JSON;
		else if(loader instanceof ByteLoader)c.t=DataType.BYTE;
		else c.t=DataType.YAML;
		if(c.getData().merge(load, true, true))c.save();
		return c;
	}
	
	public static Config loadConfig(Plugin plugin, String pathToConfig, String outputFile, DataType type) {
		Config c = new Config(outputFile, type);
		Data load = new Data();
		load.reload(StreamUtils.fromStream(plugin.getResource(pathToConfig)));
		if(c.getData().merge(load, true, true))c.save();
		return c;
	}
	
	public static final String folderName = "plugins/";
	
	private final Map<String, Node> defaults = new HashMap<>();
	private final Data f;
	private DataType t;

	// merge
	public Config(String path, DataType type, Config c, MergeType... merge) {
		this(path, type);
		merge(c, merge);
	}

	public void merge(Config c, MergeType... merge) {
		List<MergeType> todo = Arrays.asList(merge);
		if (todo.contains(MergeType.REMOVE_DATA))
			f.clear();
		if (todo.contains(MergeType.REMOVE_DEFAULTS))
			defaults.clear();
		if (todo.contains(MergeType.ADD_DEFAULTS))
			defaults.putAll(c.defaults);
		if (todo.contains(MergeType.ADD_DATA))
			f.merge(c.f, true, true);
	}

	public void merge(Config c, boolean addHeader, boolean addFooter, MergeType... merge) {
		List<MergeType> todo = Arrays.asList(merge);
		if (todo.contains(MergeType.REMOVE_DATA))
			f.clear();
		if (todo.contains(MergeType.REMOVE_DEFAULTS))
			defaults.clear();
		if (todo.contains(MergeType.ADD_DEFAULTS))
			defaults.putAll(c.defaults);
		if (todo.contains(MergeType.ADD_DATA))
			f.merge(c.f, addHeader, addFooter);
	}

	public Config(String path) {
		this(path, DataType.YAML);
	}

	public Config(String path, DataType type) {
		File file = new File(folderName + path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			try {
				file.createNewFile();
			} catch (Exception e) {
			}
		}
		f = new Data(file, true);
		t = type;
	}

	public DataType getType() {
		return t;
	}

	public void setType(DataType type) {
		t = type;
	}

	public void save() {
		f.save(t);
	}

	public void setHeader(Collection<String> texts) {
		f.setHeader(texts);
	}

	public Collection<String> getHeader() {
		return f.getHeader();
	}

	public void addHeader(String text) {
		f.getHeader().add(text);
	}

	public void removeHeader(String text) {
		f.getHeader().remove(text);
	}

	public void setFooter(Collection<String> texts) {
		f.setFooter(texts);
	}

	public Collection<String> getFooter() {
		return f.getFooter();
	}

	public void addFooter(String text) {
		f.getFooter().add(text);
	}

	public void removeFooter(String text) {
		f.getFooter().remove(text);
	}

	public void addDefaults(Map<String, Node> values) {
		for (String key : values.keySet())
			addDefault(key, values.get(key));
	}

	public void addDefault(String key, Object value) {
		if(key==null||value==null)return;
		if(value instanceof Node)addDefault(key, (Node)value);
		defaults.put(key, new Node(value));
		f.setIfAbsent(key, value);
	}

	public void addDefault(String key, Node value) {
		if(key==null||value==null)return;
		defaults.put(key, value);
		f.setIfAbsent(key, value.getValue(), value.getComments());
	}

	public Map<String, Node> getDefaults() {
		return defaults;
	}

	public void reload() {
		f.reload(f.getFile());
		int change = 0;
		for(Entry<String, Node> def : defaults.entrySet()) {
			if (f.setIfAbsent(def.getKey(), def.getValue().getValue(), def.getValue().getComments()))
				change=1;
		}
		if(change==1)save();
	}

	public String getName() {
		return f.getFile().getName();
	}

	public boolean exists(String path) {
		if (path == null)
			return false;
		return f.exists(path);
	}

	public Section getSection(String path) {
		if (exists(path))
			return new Section(this, path);
		return null;
	}

	public void remove(String path) {
		if (path == null)
			return;
		f.remove(path);
	}

	public void set(String path, Object value) {
		if (path == null)
			return;
		f.set(path, value);
	}

	public Set<String> getKeys(String path) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return f.getKeys();
		return f.getKeys(path);
	}

	public Set<String> getKeys() {
		return f.getKeys();
	}

	public Set<String> getKeys(String path, boolean sub) {
		if (path == null)
			return null;
		if (path.trim().isEmpty())
			return f.getKeys(sub);
		return f.getKeys(path, sub);
	}

	public Set<String> getKeys(boolean sub) {
		return f.getKeys(sub);
	}

	public List<String> getComments(String path) {
		if (path == null)
			return null;
		return f.getComments(path);
	}

	public void setComments(String path, List<String> value) {
		if (path == null)
			return;
		f.setComments(path, value);
	}

	public void addComment(String path, String... value) {
		if (path != null && value != null)
			f.addComments(path, Arrays.asList(value));
	}

	public void addComments(String path, List<String> value) {
		if (path != null && value != null)
			f.addComments(path, value);
	}

	public void removeComment(String path, String... value) {
		if (path != null && value != null)
			f.removeComments(path, Arrays.asList(value));
	}

	public void removeComment(String path, List<String> value) {
		if (path != null && value != null)
			f.removeComments(path, value);
	}

	public Object get(String path) {
		if (path == null)
			return null;
		return f.get(path);
	}

	public int getInt(String path) {
		if (path == null)
			return 0;
		return f.getInt(path);
	}

	public double getDouble(String path) {
		if (path == null)
			return 0.0;
		return f.getDouble(path);
	}

	public long getLong(String path) {
		if (path == null)
			return 0;
		return f.getLong(path);
	}

	public String getString(String path) {
		if (path == null)
			return null;
		return f.getString(path);
	}

	public boolean getBoolean(String path) {
		if (path == null)
			return false;
		return f.getBoolean(path);
	}

	public short getShort(String path) {
		if (path == null)
			return 0;
		return f.getShort(path);
	}

	public byte getByte(String path) {
		if (path == null)
			return 0;
		return f.getByte(path);
	}

	public float getFloat(String path) {
		if (path == null)
			return 0;
		return f.getFloat(path);
	}

	public Collection<Object> getList(String path) {
		if (path == null)
			return null;
		return f.getList(path);
	}

	public List<String> getStringList(String path) {
		if (path == null)
			return null;
		return f.getStringList(path);
	}

	public List<Boolean> getBooleanList(String path) {
		if (path == null)
			return null;
		return f.getBooleanList(path);
	}

	public List<Byte> getByteList(String path) {
		if (path == null)
			return null;
		return f.getByteList(path);
	}

	public List<Integer> getIntegerList(String path) {
		if (path == null)
			return null;
		return f.getIntegerList(path);
	}

	public List<Float> getFloatList(String path) {
		if (path == null)
			return null;
		return f.getFloatList(path);
	}

	public List<Long> getLongList(String path) {
		if (path == null)
			return null;
		return f.getLongList(path);
	}

	public List<Short> getShortList(String path) {
		if (path == null)
			return null;
		return f.getShortList(path);
	}

	public <K, V> List<Map<K, V>> getMapList(String path) {
		if (path == null)
			return null;
		return f.getMapList(path);
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
		return f.isKey(path);
	}

	public Data getData() {
		return f;
	}

	public String toString() {
		return "Data(Config:" + getName() + "/" + t.name() + ")";
	}

	@Override
	public String getDataName() {
		return toString();
	}
}
