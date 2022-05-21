package me.devtec.shared.dataholder;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.devtec.shared.dataholder.loaders.DataLoader;
import me.devtec.shared.dataholder.loaders.EmptyLoader;
import me.devtec.shared.dataholder.loaders.YamlLoader;
import me.devtec.shared.dataholder.loaders.constructor.DataValue;
import me.devtec.shared.json.Json;
import me.devtec.shared.utility.StreamUtils;
import me.devtec.shared.utility.StringUtils;

public class Config {
	protected DataLoader loader;
	protected List<String> keys;
	protected File file;
	protected boolean isSaving; //LOCK
	protected boolean requireSave;

	public static Config loadFromInput(InputStream input, String outputFile) {
		return Config.loadFromInput(input, new File(outputFile));
	}

	public static Config loadFromInput(InputStream input, File outputFile) {
		Config config = new Config(outputFile);
		Config insideJar = new Config();
		insideJar.reload(StreamUtils.fromStream(input));
		config.merge(insideJar, true, true, true, true);
		return config;
	}

	public static Config loadFromPlugin(Class<?> mainClass, String pathToFile, File outputFile) {
		return Config.loadFromInput(mainClass.getClassLoader().getResourceAsStream(pathToFile), outputFile);
	}

	public static Config loadFromPlugin(Class<?> mainClass, String pathToFile, String outputFile) {
		return Config.loadFromInput(mainClass.getClassLoader().getResourceAsStream(pathToFile), new File(outputFile));
	}

	public static Config loadFromFile(File file) {
		return new Config(file);
	}

	public static Config loadFromFile(String filePath) {
		return new Config(filePath);
	}

	public static Config loadFromString(String input) {
		return new Config().reload(input);
	}

	public Config() {
		loader = new EmptyLoader();
		keys = new LinkedList<>();
	}

	public Config(String filePath) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), true);
	}

	public Config(String filePath, boolean load) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), load);
	}

	public Config(File file) {
		this(file, true);
	}

	public Config(File file, boolean load) {
		if(!file.exists()) {
			try {
				if(file.getParentFile()!=null)
					file.getParentFile().mkdirs();
			}catch(Exception err) {
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.file = file;
		keys = new LinkedList<>();
		if (load)
			reload(file);
		markNonModified();
	}

	// CLONE
	public Config(Config data) {
		file = data.file;
		keys = data.keys;
		loader=data.loader;
	}

	public boolean isModified() {
		return requireSave;
	}

	public void markModified() {
		requireSave = true;
	}

	public void markNonModified() {
		requireSave = false;
	}

	public boolean exists(String path) {
		return isKey(path);
	}

	public boolean existsKey(String path) {
		return loader.get().containsKey(path);
	}

	public Config setFile(File file) {
		if(file == this.file)return this;
		markModified();
		if(!file.exists()) {
			try {
				if(file.getParentFile()!=null)
					file.getParentFile().mkdirs();
			}catch(Exception err) {
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.file = file;
		return this;
	}

	public DataValue getOrCreateData(String key) {
		DataValue h = loader.get().get(key);
		if (h == null) {
			String ss = Config.splitFirst(key);
			if (!keys.contains(ss))keys.add(ss);
			loader.get().put(key, h = DataValue.empty());
		}
		return h;
	}

	private static String splitFirst(String text) {
		int next = text.indexOf('.');
		return next!=-1 ? text.substring(0, next) : text;
	}

	public boolean setIfAbsent(String key, Object value) {
		if (key == null || value==null)
			return false;
		if(!existsKey(key)) {
			markModified();
			DataValue data = getOrCreateData(key);
			data.value=value;
			return true;
		}
		return false;
	}

	public boolean setIfAbsent(String key, Object value, List<String> comments) {
		if (key == null || value==null)
			return false;
		if(!existsKey(key)) {
			markModified();
			DataValue data = getOrCreateData(key);
			data.value=value;
			data.comments=comments;
			return true;
		}
		DataValue data = getOrCreateData(key);
		if(data.comments==null && comments!=null && !comments.isEmpty() || data.comments!=null && data.comments.isEmpty() && comments!=null && !comments.isEmpty()) {
			data.comments=comments;
			markModified();
			return true;
		}
		return false;
	}

	public Config set(String key, Object value) {
		if (key == null)
			return this;
		markModified();
		if (value == null) {
			String sf = Config.splitFirst(key);
			keys.remove(sf);
			loader.remove(key);
			return this;
		}
		DataValue o = getOrCreateData(key);
		o.value=value;
		o.writtenValue=null;
		return this;
	}

	public Config remove(String key) {
		if (key == null)
			return this;
		markModified();
		String sf = Config.splitFirst(key);
		keys.remove(sf);
		loader.remove(key);
		for(String d : new ArrayList<>(loader.get().keySet()))
			if (d.startsWith(key) && d.substring(key.length()).startsWith("."))
				loader.get().remove(d);
		return this;
	}

	public List<String> getComments(String key) {
		if (key == null)
			return null;
		DataValue h = loader.get().get(key);
		if(h!=null)return h.comments;
		return null;
	}

	public Config setComments(String key, List<String> value) {
		if (key == null)
			return this;
		markModified();
		getOrCreateData(key).comments=Config.simple(new ArrayList<>(value));
		return this;
	}

	public String getCommentAfterValue(String key) {
		if (key == null)
			return null;
		DataValue h = loader.get().get(key);
		if (h != null)
			return h.commentAfterValue;
		return null;
	}

	public Config setCommentAfterValue(String key, String comment) {
		if (key == null)
			return null;
		markModified();
		getOrCreateData(key).commentAfterValue=comment;
		return this;
	}

	public File getFile() {
		return file;
	}

	public Config setHeader(Collection<String> lines) {
		markModified();
		loader.getHeader().clear();
		loader.getHeader().addAll(Config.simple(lines));
		return this;
	}

	public Config setFooter(Collection<String> lines) {
		markModified();
		loader.getFooter().clear();
		loader.getFooter().addAll(Config.simple(lines));
		return this;
	}

	public Collection<String> getHeader() {
		return loader.getHeader();
	}

	public Collection<String> getFooter() {
		return loader.getFooter();
	}

	public Config reload(String input) {
		markModified();
		keys.clear();
		loader = DataLoader.findLoaderFor(input); // get & load
		for (String k : loader.getKeys()) {
			String g = Config.splitFirst(k);
			if (!keys.contains(g))
				keys.add(g);
		}
		return this;
	}

	public Config reload() {
		return reload(getFile());
	}

	public Config reload(File f) {
		if (!f.exists()) {
			markModified();
			loader=new EmptyLoader();
			keys.clear();
			return this;
		}
		markModified();
		keys.clear();
		loader = DataLoader.findLoaderFor(f); // get & load
		for (String k : loader.getKeys()) {
			String g = Config.splitFirst(k);
			if (!keys.contains(g))
				keys.add(g);
		}
		return this;
	}

	public Object get(String key) {
		try {
			return loader.get().get(key).value;
		} catch (Exception e) {
			return null;
		}
	}

	public <E> E getAs(String key, Class<? extends E> clazz) {
		try {
			if(clazz==String.class||clazz==CharSequence.class)return clazz.cast(getString(key));
		} catch (Exception e) {
		}
		try {
			return clazz.cast(loader.get().get(key).value);
		} catch (Exception e) {
		}
		return null;
	}

	public String getString(String key) {
		DataValue a = loader.get().get(key);
		if(a==null)return null;
		if(a.writtenValue != null)return a.writtenValue;
		return a.value instanceof String ? (String)a.value : a.value==null ? null : a.value+"";
	}

	public boolean isJson(String key) {
		try {
			DataValue a = loader.get().get(key);
			if(a.writtenValue != null)
				return a.writtenValue.charAt(0)=='[' && a.writtenValue.charAt(a.writtenValue.length()-1)==']' || a.writtenValue.charAt(0)=='{' && a.writtenValue.charAt(a.writtenValue.length()-1)=='}';
		} catch (Exception notNumber) {
		}
		return false;
	}

	public int getInt(String key) {
		try {
			return ((Number)get(key)).intValue();
		} catch (Exception notNumber) {
			return StringUtils.getInt(getString(key));
		}
	}

	public double getDouble(String key) {
		try {
			return ((Number)get(key)).doubleValue();
		} catch (Exception notNumber) {
			return StringUtils.getDouble(getString(key));
		}
	}

	public long getLong(String key) {
		try {
			return ((Number)get(key)).longValue();
		} catch (Exception notNumber) {
			return StringUtils.getLong(getString(key));
		}
	}

	public float getFloat(String key) {
		try {
			return ((Number)get(key)).floatValue();
		} catch (Exception notNumber) {
			return StringUtils.getFloat(getString(key));
		}
	}

	public byte getByte(String key) {
		try {
			return ((Number)get(key)).byteValue();
		} catch (Exception notNumber) {
			return StringUtils.getByte(getString(key));
		}
	}

	public boolean getBoolean(String key) {
		try {
			return (boolean)get(key);
		} catch (Exception notNumber) {
			return StringUtils.getBoolean(getString(key));
		}
	}

	public short getShort(String key) {
		try {
			return ((Number)get(key)).shortValue();
		} catch (Exception notNumber) {
			return StringUtils.getShort(getString(key));
		}
	}

	public Collection<Object> getList(String key) {
		Object g = get(key);
		return g instanceof Collection ? new ArrayList<>((Collection<?>) g) : new ArrayList<>();
	}

	public <E> List<E> getListAs(String key, Class<? extends E> clazz) {
		List<E> list = new ArrayList<>();
		for (Object o : getList(key))
			try {
				list.add(o == null ? null : clazz.cast(o));
			} catch (Exception er) {
			}
		return list;
	}

	public List<String> getStringList(String key) {
		Collection<Object> items = getList(key);
		List<String> list=new ArrayList<>(items.size());
		for (Object o : items)
			if (o != null)
				list.add("" + o);
			else
				list.add(null);
		return list;
	}

	public List<Boolean> getBooleanList(String key) {
		Collection<Object> items = getList(key);
		List<Boolean> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o != null && (o instanceof Boolean ? (Boolean) o : StringUtils.getBoolean(o.toString())));
		return list;
	}

	public List<Integer> getIntegerList(String key) {
		Collection<Object> items = getList(key);
		List<Integer> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).intValue():StringUtils.getInt(o.toString()));
		return list;
	}

	public List<Double> getDoubleList(String key) {
		Collection<Object> items = getList(key);
		List<Double> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0.0 : o instanceof Number ? ((Number)o).doubleValue():StringUtils.getDouble(o.toString()));
		return list;
	}

	public List<Short> getShortList(String key) {
		Collection<Object> items = getList(key);
		List<Short> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).shortValue():StringUtils.getShort(o.toString()));
		return list;
	}

	public List<Byte> getByteList(String key) {
		Collection<Object> items = getList(key);
		List<Byte> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).byteValue():StringUtils.getByte(o.toString()));
		return list;
	}

	public List<Float> getFloatList(String key) {
		Collection<Object> items = getList(key);
		List<Float> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).floatValue():StringUtils.getFloat(o.toString()));
		return list;
	}

	public List<Long> getLongList(String key) {
		Collection<Object> items = getList(key);
		List<Long> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getLong(o.toString()));
		return list;
	}

	@SuppressWarnings("unchecked")
	public <K, V> List<Map<K, V>> getMapList(String key) {
		Collection<Object> items = getList(key);
		List<Map<K, V>> list=new ArrayList<>(items.size());
		for (Object o : items)
			if(o==null)
				list.add(null);
			else {
				Object re = Json.reader().read(o.toString());
				list.add(re instanceof Map ? (Map<K, V>) re : new ConcurrentHashMap<>());
			}
		return list;
	}

	public Config save(DataType type) {
		if(file == null || isSaving || !isModified())return this;
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
			} catch (Exception e) {
			}
			try {
				file.createNewFile();
			} catch (Exception e) {
			}
		}
		isSaving=true;
		markNonModified();
		try {
			try {
				OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
				w.write(toString(type));
				w.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			isSaving=false;
		} catch (Exception e1) {
		}
		return this;
	}

	public void save() {
		save(DataType.YAML);
	}

	public Set<String> getKeys() {
		return new LinkedHashSet<>(keys);
	}

	public Set<String> getKeys(boolean subkeys) {
		if (subkeys)
			return loader.getKeys();
		return new LinkedHashSet<>(keys);
	}

	public Set<String> getKeys(String key) {
		return getKeys(key, false);
	}

	public boolean isKey(String key) {
		for (String k : loader.getKeys())
			if (k.startsWith(key)) {
				String r = k.substring(key.length());
				if (r.startsWith(".") || r.trim().isEmpty())
					return true;
			}
		return false;
	}

	public Set<String> getKeys(String key, boolean subkeys) {
		Set<String> a = new LinkedHashSet<>();
		for (String d : loader.getKeys())
			if (d.startsWith(key)) {
				String c = d.substring(key.length());
				if (!c.startsWith("."))
					continue;
				c = c.substring(1);
				if(!subkeys)
					c =  Config.splitFirst(c);
				if (c.trim().isEmpty())
					continue;
				if (!a.contains(c))
					a.add(c);
			}
		return a;
	}

	@Override
	public String toString() {
		return toString(DataType.YAML);
	}

	protected void addKeys(List<Map<String, String>> list, String key) {
		Object o = get(key);
		if (o != null) {
			Map<String, String> a = new ConcurrentHashMap<>();
			a.put(key, Json.writer().write(o));
			list.add(a);
		}
		for (String keyer : getKeys(key))
			addKeys(list, key + "." + keyer);
	}

	public String toString(DataType type) {
		switch(type) {
		case PROPERTIES: {
			int size = loader.get().size();
			StringBuilder builder = new StringBuilder(size*8);
			if(loader.getHeader()!=null)
				try {
					for (String h : loader.getHeader())
						builder.append(h).append(System.lineSeparator());
				} catch (Exception er) {
					er.printStackTrace();
				}
			for (Entry<String, DataValue> key : loader.get().entrySet()) {
				if(key.getValue()==null)continue;
				if(key.getValue().value==null) {
					if(key.getValue().commentAfterValue!=null)
						builder.append(key.getKey()+": "+key.getValue().commentAfterValue);
					continue;
				}
				builder.append(key.getKey()+": "+Json.writer().write(key.getValue().value));
			}
			if(loader.getFooter()!=null)
				try {
					for (String h : loader.getFooter())
						builder.append(h).append(System.lineSeparator());
				} catch (Exception er) {
					er.printStackTrace();
				}
			return builder.toString();
		}
		case BYTE:
			try {
				ByteArrayDataOutput in = ByteStreams.newDataOutput(loader.get().size());
				in.writeInt(3);
				for (Entry<String, DataValue> key : loader.get().entrySet())
					try {
						in.writeInt(0);
						in.writeUTF(key.getKey());
						if(key.getValue()==null||key.getValue().value==null) {
							in.writeInt(3);
							continue;
						}
						if(key.getValue().writtenValue!=null) {
							String write = key.getValue().writtenValue;
							if(write==null) {
								in.writeInt(3);
								continue;
							}
							while(write.length()>40000) {
								String wr = write.substring(0, 39999);
								in.writeInt(1);
								in.writeUTF(wr);
								write=write.substring(39999);
							}
							in.writeInt(1);
							in.writeUTF(write);
							continue;
						}
						String write = Json.writer().write(key.getValue().value);
						if(write==null) {
							in.writeInt(3);
							continue;
						}
						while(write.length()>40000) {
							String wr = write.substring(0, 39999);
							in.writeInt(1);
							in.writeUTF(wr);
							write=write.substring(39999);
						}
						in.writeInt(1);
						in.writeUTF(write);
					} catch (Exception er) {
						er.printStackTrace();
					}
				return Base64.getEncoder().encodeToString(in.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		case JSON:
			List<Map<String, String>> list = new ArrayList<>();
			for (String key : Collections.unmodifiableList(keys))
				addKeys(list, key);
			return Json.writer().simpleWrite(list);
		case YAML:
			int size = loader.get().size();
			StringBuilder builder = new StringBuilder(size*16);
			if(loader.getHeader()!=null)
				try {
					for (String h : loader.getHeader())
						builder.append(h).append(System.lineSeparator());
				} catch (Exception er) {
					er.printStackTrace();
				}

			//BUILD KEYS & SECTIONS
			SectionBuilder.write(builder, keys, loader.get());

			if(loader.getFooter()!=null)
				try {
					for (String h : loader.getFooter())
						builder.append(h).append(System.lineSeparator());
				} catch (Exception er) {
					er.printStackTrace();
				}
			return builder.toString();
		}
		return null;
	}

	public Config clear() {
		keys.clear();
		loader.get().clear();
		return this;
	}

	public Config reset() {
		keys.clear();
		loader.reset();
		return this;
	}

	public boolean merge(Config configToMergeWith, boolean addHeader, boolean addFooter, boolean addCommentsAfterValue, boolean addComments) {
		boolean change = false;

		//header & footer option
		try {
			if(addHeader)
				if(loader.getHeader() != null /** Is header supported? **/ && configToMergeWith.loader.getHeader()!=null && !configToMergeWith.loader.getHeader().isEmpty() && (loader.getHeader().isEmpty() || !loader.getHeader().containsAll(configToMergeWith.loader.getHeader()))) {
					loader.getHeader().clear();
					loader.getHeader().addAll(configToMergeWith.loader.getHeader());
					change = true;
				}
			if(addFooter)
				if(loader.getFooter() != null /** Is footer supported? **/ && configToMergeWith.loader.getFooter()!=null && !configToMergeWith.loader.getFooter().isEmpty() && (loader.getFooter().isEmpty() || !loader.getFooter().containsAll(configToMergeWith.loader.getFooter()))) {
					loader.getFooter().clear();
					loader.getFooter().addAll(configToMergeWith.loader.getFooter());
					change = true;
				}
		}catch(Exception error) {}

		try {
			boolean first = true;
			for(Entry<String, DataValue> s : configToMergeWith.loader.get().entrySet()) {
				DataValue value = getOrCreateData(s.getKey());
				if(value.value==null && s.getValue().value!=null) {
					value.value=s.getValue().value;
					value.writtenValue=s.getValue().writtenValue;
					change = true;
				}
				if(addCommentsAfterValue && value.commentAfterValue==null && s.getValue().commentAfterValue!=null) {
					value.commentAfterValue=s.getValue().commentAfterValue;
					change = true;
				}
				if(addComments && s.getValue().comments!=null && !s.getValue().comments.isEmpty()) {
					List<String> comments = value.comments;
					if(comments==null || comments.isEmpty()) {
						List<String> simple = Config.simple(s.getValue().comments);
						if(first && getHeader()!=null && !getHeader().isEmpty() && Config.simple(getHeader()).containsAll(simple))continue;
						value.comments=s.getValue().comments;
						change = true;
					}
				}
				first=false;
			}
		}catch(Exception err) {}
		if(change)
			markModified();
		return change;
	}

	private static List<String> simple(List<String> list) {
		ListIterator<String> s = list.listIterator();
		while(s.hasNext()) {
			String next = s.next();
			if(next.trim().isEmpty())s.set("");
			else s.set(next.substring(YamlLoader.removeSpaces(next)));
		}
		return list;
	}

	private static List<String> simple(Collection<String> list) {
		if(list instanceof ArrayList)return Config.simple((List<String>)list);
		List<String> fix = new ArrayList<>(list.size());
		Iterator<String> s = list.iterator();
		while(s.hasNext()) {
			String next = s.next();
			if(next.trim().isEmpty())fix.add("");
			else fix.add(next.substring(YamlLoader.removeSpaces(next)));
		}
		return fix;
	}

	public DataLoader getDataLoader() {
		return loader;
	}
}