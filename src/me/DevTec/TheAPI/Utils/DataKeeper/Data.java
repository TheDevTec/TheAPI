package me.DevTec.TheAPI.Utils.DataKeeper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.Collections.UnsortedList;
import me.DevTec.TheAPI.Utils.DataKeeper.Collections.UnsortedSet;
import me.DevTec.TheAPI.Utils.DataKeeper.loader.DataLoader;
import me.DevTec.TheAPI.Utils.DataKeeper.loader.EmptyLoader;
import me.DevTec.TheAPI.Utils.Json.Maker;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

public class Data implements me.DevTec.TheAPI.Utils.DataKeeper.Abstract.Data {
	public static class DataHolder {
		private Object o;
		private List<String> lines = new UnsortedList<>();

		public DataHolder() {
		}

		public DataHolder(Object object) {
			o = object;
		}

		public DataHolder(Object object, List<String> unusedLines) {
			this(object);
			lines = unusedLines;
		}

		public Object getValue() {
			return o;
		}

		public void setValue(Object o) {
			this.o = o;
		}

		public List<String> getComments() {
			return lines;
		}
	}

	private DataLoader loader;
	private Set<String> aw = new UnsortedSet<>();
	private File a;

	public Data() {
		loader = new EmptyLoader();
	}

	public Data(String filePath) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), true);
	}

	public Data(String filePath, boolean load) {
		this(new File(filePath.startsWith("/") ? filePath.substring(1) : filePath), load);
	}

	public Data(File f) {
		this(f, true);
	}

	public Data(File f, boolean load) {
		a = f;
		if (load)
			reload(a);
		else
			loader = new EmptyLoader();
	}

	public boolean exists(String path) {
		int a = 0;
		for (String k : loader.get().keySet()) {
			if (k.startsWith(path)) {
				a = 1;
				break;
			}
		}
		return a == 1;
	}

	public Data setFile(File f) {
		a = f;
		return this;
	}

	private DataHolder getOrCreateData(String key) {
		DataHolder h = loader.get().getOrDefault(key, null);
		if (h == null) {
			h = new DataHolder(null);
			if (!aw.contains(key.split("\\.")[0]))
				aw.add(key.split("\\.")[0]);
			loader.set(key, h);
		}
		return h;
	}

	public Data set(String key, Object value) {
		if (key == null)
			return this;
		if (value == null) {
			if (key.split("\\.").length <= 1)
				aw.remove(key.split("\\.")[0]);
			loader.remove(key);
			return this;
		}
		getOrCreateData(key).setValue(value);
		return this;
	}

	public Data remove(String key) {
		if (key == null)
			return this;
		if (key.split("\\.").length <= 1)
			aw.remove(key.split("\\.")[0]);
		loader.remove(key);
		for (String keys : getKeys(key))
			loader.remove(key + "." + keys);
		return this;
	}

	public List<String> getComments(String key) {
		if (key == null)
			return null;
		return getOrCreateData(key).lines;
	}

	public Data setComments(String key, List<String> value) {
		if (key == null)
			return this;
		getOrCreateData(key).lines.clear();
		if (value == null)
			return this;
		getOrCreateData(key).lines.addAll(value);
		return this;
	}

	public Data addComments(String key, List<String> value) {
		if (value == null || key == null)
			return this;
		getOrCreateData(key).lines.addAll(value);
		return this;
	}

	public Data addComment(String key, String value) {
		if (value == null || key == null)
			return this;
		DataHolder h = getOrCreateData(key);
		h.lines.add(value);
		return this;
	}

	public Data removeComments(String key, List<String> value) {
		if (value == null || key == null)
			return this;
		getOrCreateData(key).lines.removeAll(value);
		return this;
	}

	public Data removeComment(String key, String value) {
		if (value == null || key == null)
			return this;
		DataHolder h = getOrCreateData(key);
		if (h.lines != null)
			h.lines.remove(value);
		return this;
	}

	public Data removeComment(String key, int line) {
		if (line < 0 || key == null)
			return this;
		DataHolder h = getOrCreateData(key);
		if (h.lines != null)
			h.lines.remove(line);
		return this;
	}

	public File getFile() {
		return a;
	}

	public Data setHeader(List<String> lines) {
		loader.getHeader().clear();
		loader.getHeader().addAll(lines);
		return this;
	}

	public Data setFooter(List<String> lines) {
		loader.getFooter().clear();
		loader.getFooter().addAll(lines);
		return this;
	}

	public List<String> getHeader() {
		return loader.getHeader();
	}

	public List<String> getFooter() {
		return loader.getFooter();
	}

	public Data reload(String input) {
		aw.clear();
		if (loader != null)
			loader.reset();
		loader = DataLoader.findLoaderFor(input); // get & load
		for (String k : loader.getKeys())
			if (!aw.contains(k.split("\\.")[0]))
				aw.add(k.split("\\.")[0]);
		return this;
	}

	public Data reload(File f) {
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (Exception e) {
			}
		}
		aw.clear();
		if (loader != null)
			loader.reset();
		loader = DataLoader.findLoaderFor(f); // get & load
		for (String k : loader.getKeys())
			if (!aw.contains(k.split("\\.")[0]))
				aw.add(k.split("\\.")[0]);
		return this;
	}

	public Object get(String key) {
		try {
			return loader.get().get(key).o;
		} catch (Exception e) {
			return null;
		}
	}

	public <E> E getAs(String key, Class<? extends E> clazz) {
		try {
			return clazz.cast(loader.get().get(key).o);
		} catch (Exception e) {
			throw e;
		}
	}

	public String getString(String key) {
		return get(key) != null ? String.valueOf(get(key)) : null;
	}

	public int getInt(String key) {
		try {
			return getAs(key, int.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Integer.class);
			} catch (Exception notNumber2) {
				return StringUtils.getInt(getString(key));
			}
		}
	}

	public double getDouble(String key) {
		try {
			return getAs(key, double.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Double.class);
			} catch (Exception notNumber2) {
				return StringUtils.getDouble(getString(key));
			}
		}
	}

	public long getLong(String key) {
		try {
			return getAs(key, long.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Long.class);
			} catch (Exception notNumber2) {
				return StringUtils.getLong(getString(key));
			}
		}
	}

	public float getFloat(String key) {
		try {
			return getAs(key, float.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Float.class);
			} catch (Exception notNumber2) {
				return StringUtils.getFloat(getString(key));
			}
		}
	}

	public byte getByte(String key) {
		try {
			return getAs(key, byte.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Byte.class);
			} catch (Exception notNumber2) {
				return StringUtils.getByte(getString(key));
			}
		}
	}

	public boolean getBoolean(String key) {
		try {
			return getAs(key, boolean.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Boolean.class);
			} catch (Exception notNumber2) {
				return StringUtils.getBoolean(getString(key));
			}
		}
	}

	public short getShort(String key) {
		try {
			return getAs(key, short.class);
		} catch (Exception notNumber) {
			try {
				return getAs(key, Short.class);
			} catch (Exception notNumber2) {
				return StringUtils.getShort(getString(key));
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Object> getList(String key) {
		return get(key) != null && get(key) instanceof Collection ? (List) get(key) : new ArrayList<>(3);
	}

	public <E> List<E> getListAs(String key, Class<? extends E> clazz) {
		// Cast everything to <E>
		List<Object> items = getList(key);
		List<E> list = new ArrayList<>(items.size());
		for (Object o : items)
			try {
				if (o != null)
					list.add(o == null ? null : clazz.cast(o));
				else
					list.add(null);
			} catch (Exception er) {
			}
		return list;
	}

	public List<String> getStringList(String key) {
		// Cast everything to String
		List<Object> items = getList(key);
		List<String> list = new ArrayList<>(items.size());
		for (Object o : items)
			if (o != null)
				list.add("" + o);
			else
				list.add(null);
		return list;
	}

	public List<Boolean> getBooleanList(String key) {
		// Cast everything to Boolean
		List<Object> items = getList(key);
		List<Boolean> list = new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? false : StringUtils.getBoolean(o.toString()));
		return list;
	}

	public List<Integer> getIntegerList(String key) {
		// Cast everything to Integer
		List<Object> items = getList(key);
		List<Integer> list = new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getInt(o.toString()));
		return list;
	}

	public List<Double> getDoubleList(String key) {
		// Cast everything to Double
		List<Object> items = getList(key);
		List<Double> list = new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0.0 : StringUtils.getDouble(o.toString()));
		return list;
	}

	public List<Short> getShortList(String key) {
		// Cast everything to Short
		List<Object> items = getList(key);
		List<Short> list = new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getShort(o.toString()));
		return list;
	}

	public List<Byte> getByteList(String key) {
		// Cast everything to Byte
		List<Object> items = getList(key);
		List<Byte> list = new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getByte(o.toString()));
		return list;
	}

	public List<Float> getFloatList(String key) {
		// Cast everything to Float
		List<Object> items = getList(key);
		List<Float> list = new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getFloat(o.toString()));
		return list;
	}

	public List<Long> getLongList(String key) {
		// Cast everything to Byte
		List<Object> items = getList(key);
		List<Long> list = new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getLong(o.toString()));
		return list;
	}

	public List<Map<?, ?>> getMapList(String key) {
		return getListAs(key, Map.class);
	}

	public Data save(DataType type) {
		synchronized (loader) {
			try {
				if (a == null)
					return this;
				OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(a), StandardCharsets.UTF_8);
				if (type == DataType.DATA || type == DataType.BYTE) {
					try {
						ByteArrayOutputStream bos = new ByteArrayOutputStream(loader.getKeys().size());
						GZIPOutputStream tos = new GZIPOutputStream(bos);
						BufferedOutputStream buf = new BufferedOutputStream(tos);
						DataOutputStream ous = new DataOutputStream(buf);
						for (Entry<String, DataHolder> key : loader.get().entrySet())
							try {
								Object o = key.getValue();
								ous.writeUTF(key.getKey());
								ous.writeUTF("" + me.DevTec.TheAPI.Utils.Json.Writer.write(o));
							} catch (Exception er) {
							}
						ous.flush();
						bos.flush();
						buf.flush();
						tos.finish();
						w.write(type == DataType.DATA ? bos.toString()
								: Base64.getEncoder().encodeToString(bos.toByteArray()));
						w.close();
					} catch (Exception e) {
						w.write("");
						w.close();
					}
					return this;
				}
				if (type == DataType.JSON) {
					Maker maker = new Maker();
					for (String key : aw)
						addKeys(maker, key);
					w.write(maker.toString(false).replace("\\n", System.lineSeparator()));
					w.close();
					return this;
				}
				try {
					for (String h : loader.getHeader())
						w.write(h + System.lineSeparator());
				} catch (Exception er) {
				}
				for (String key : aw)
					preparePath(key, key + ":", 0, w);
				try {
					for (String h : loader.getFooter())
						w.write(h + System.lineSeparator());
				} catch (Exception er) {
				}
				w.close();
			} catch (Exception er) {
				Validator.send("Saving Data to File", er);
			}
		}
		return this;
	}

	/**
	 * Wraps string in double quotes, unless the string is already in double or
	 * single quotes.
	 *
	 * New-line characters are removed from the returned string.
	 *
	 * Example input -> output: : 5 -> "5" : '5' -> '5' : "5" -> "5" : 5" -> "5""
	 */
	private String addQuotes(boolean raw, String text) {
		if (text == null)
			return null;
		boolean quotedString = (text.trim().startsWith("'") && text.trim().endsWith("'"))
				|| (text.trim().startsWith("\"") && text.trim().endsWith("\""));
		if (raw && !quotedString) {
			return StringUtils.isBoolean(text)?text:StringUtils.isNumber(text)?"'"+text+"'":"\"" + text + "\"";
		}
		return text;
	}
	
	public void save() {
		save(DataType.YAML);
	}

	public Set<String> getKeys() {
		return new UnsortedSet<>(aw);
	}

	public Set<String> getKeys(boolean subkeys) {
		if (subkeys)
			return loader.getKeys();
		return new UnsortedSet<>(aw);
	}

	public Set<String> getKeys(String key) {
		return getKeys(key, false);
	}

	public boolean isKey(String key) {
		boolean is = false;
		for (String k : loader.getKeys()) {
			if (k.startsWith(key)) {
				String r = k.replaceFirst(key, "");
				if (r.startsWith(".") || r.trim().isEmpty()) {
					is = true;
					break;
				}
			}
		}

		return is;
	}

	public Set<String> getKeys(String key, boolean subkeys) {
		Set<String> a = new UnsortedSet<>();
		for (String d : loader.getKeys())
			if (d.startsWith(key)) {
				String c = d.replaceFirst(Pattern.quote(key), "");
				if (!c.startsWith("."))
					continue;
				c = subkeys ? c : c.replaceFirst("\\.", "").split("\\.")[0];
				if (c.trim().isEmpty())
					continue;
				if (!a.contains(c))
					a.add(c);
			}
		return a;
	}

	public String toString() {
		return toString(DataType.DATA);
	}

	private void addKeys(Maker main, String key) {
		Object o = get(key);
		if (o != null)
			main.add(main.create().put(key, o));
		for (String keyer : getKeys(key))
			addKeys(main, key + "." + keyer);
	}

	private void preparePath(String path, String pathName, int spaces, java.io.Writer b) {
		synchronized (loader) {
			try {
				DataHolder aw = loader.get().get(path);
				Object o = aw != null ? aw.o : null;
				String space = cs(spaces, 1);
				pathName = space + pathName;
				if (aw != null)
					for (String s : aw.getComments())
						b.write(space + s + System.lineSeparator());
				if (o == null) {
					b.write(pathName + System.lineSeparator());
				} else {
					if (o instanceof Collection || o instanceof Object[]) {
						b.write(pathName + System.lineSeparator());
						String splitted = space + "- ";
						if (o instanceof Collection) {
							for (Object a : (Collection<?>) o) {
								b.write(splitted+addQuotes(a instanceof String, me.DevTec.TheAPI.Utils.Json.Writer.write(a))
										+ System.lineSeparator());
							}
						} else {
							for (Object a : (Object[]) o)
								b.write(splitted+addQuotes(a instanceof String, me.DevTec.TheAPI.Utils.Json.Writer.write(a))
										+ System.lineSeparator());
						}
					} else
						b.write(pathName + " "
								+ addQuotes(o instanceof String, me.DevTec.TheAPI.Utils.Json.Writer.write(o))
								+ System.lineSeparator());
				}
				for (String key : getKeys(path, false))
					preparePath(path + "." + key, key + ":", spaces + 1, b);
			} catch (Exception er) {
				Validator.send("Saving Data to YAML", er);
			}
		}
	}

	public String toString(DataType type) {
		synchronized (loader) {
			if (type == DataType.DATA || type == DataType.BYTE) {
				try {
					ByteArrayDataOutput bos = ByteStreams.newDataOutput(loader.get().size());
					for (Entry<String, DataHolder> key : loader.get().entrySet())
						try {
							bos.writeUTF(key.getKey());
							bos.writeUTF(me.DevTec.TheAPI.Utils.Json.Writer.write(key.getValue().o));
						} catch (Exception er) {
						}
					return type == DataType.DATA ? bos.toString()
							: Base64.getEncoder().encodeToString(bos.toByteArray());
				} catch (Exception e) {
				}
				return "";
			}
			if (type == DataType.JSON) {
				Maker main = new Maker();
				for (String key : aw)
					addKeys(main, key);
				return main.toString();
			}

			StringWriter d = new StringWriter();
			try {
				for (String h : loader.getHeader())
					d.write(h + System.lineSeparator());
			} catch (Exception er) {
			}
			for (String key : aw)
				preparePath(key, key + ":", 0, d);
			try {
				for (String h : loader.getFooter())
					d.write(h + System.lineSeparator());
			} catch (Exception er) {
			}
			return d.toString();
		}
	}

	private static String cs(int s, int doubleSpace) {
		StringWriter i = new StringWriter();
		String space = doubleSpace == 1 ? "  " : " ";
		for (int c = 0; c < s; ++c)
			i.write(space);
		return i.toString();
	}

	@Override
	public String getDataName() {
		return "Data(" + (a != null ? "'" + a.getName() + "'" : "") + ")";
	}

	public Data clear() {
		loader.get().clear();
		return this;
	}

	public Data reset() {
		loader.reset();
		return this;
	}

	public Data merge(Data f, boolean addHeader, boolean addFooter) {
		loader.get().putAll(f.loader.get());
		for (String sw : f.aw)
			if (!aw.contains(sw))
				aw.add(sw);
		if (addHeader)
			loader.getHeader().addAll(f.loader.getHeader());
		if (addFooter)
			loader.getFooter().addAll(f.loader.getFooter());
		return this;
	}
}