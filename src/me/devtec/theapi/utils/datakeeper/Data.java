package me.devtec.theapi.utils.datakeeper;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.loader.DataLoader;
import me.devtec.theapi.utils.datakeeper.loader.EmptyLoader;
import me.devtec.theapi.utils.json.Maker;
import me.devtec.theapi.utils.json.Reader;
import me.devtec.theapi.utils.json.Writer;
import me.devtec.theapi.utils.theapiutils.Validator;

public class Data implements me.devtec.theapi.utils.datakeeper.abstracts.Data {
	
	protected DataLoader loader;
	protected Set<String> aw;
	protected File a;
	
	public Data() {
		loader = new EmptyLoader();
		aw = new LinkedHashSet<>();
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
		aw = new LinkedHashSet<>();
		if (load)
			reload(a);
	}
	
	// CLONE
	public Data(Data data) {
		a = data.a;
		aw = data.aw;
		loader=data.loader;
	}

	public synchronized boolean exists(String path) {
		int a = 0;
		for (String k : loader.get().keySet()) {
			if (k.startsWith(path)) {
				a = 1;
				break;
			}
		}
		return a == 1;
	}

	public synchronized boolean existsKey(String path) {
		return loader.get().containsKey(path);
	}

	public synchronized Data setFile(File f) {
		a = f;
		return this;
	}

	private synchronized Object[] getOrCreateData(String key) {
		Object[] h = loader.get().getOrDefault(key, null);
		if (h == null) {
			h = new Object[4];
			String ss = key.split("\\.")[0];
			if (!aw.contains(ss))
				aw.add(ss);
			loader.set(key, h);
		}
		return h;
	}

	public synchronized boolean setIfAbsent(String key, Object value) {
		if (key == null || value==null)
			return false;
		if(!existsKey(key)) {
			Object[] data = getOrCreateData(key);
			data[0]=value;
			data[2]=value+"";
			return true;
		}
		return false;
	}

	public synchronized boolean setIfAbsent(String key, Object value, List<String> comments) {
		if (key == null || value==null)
			return false;
		if(!existsKey(key)) {
			Object[] data = getOrCreateData(key);
			data[0]=value;
			data[1]=comments;
			data[2]=value+"";
			return true;
		}
		Object[] data = getOrCreateData(key);
		if(data[1]==null || ((List<?>) data[1]).isEmpty()) {
			data[1]=comments;
			return true;
		}
		return false;
	}

	private static final int set = 1;
	public synchronized Data set(String key, Object value) {
		if (key == null)
			return this;
		if (value == null) {
			String[] sf = key.split("\\.");
			if (sf.length <= 1)
				aw.remove(sf[0]);
			loader.remove(key);
			return this;
		}
		Object[] o = getOrCreateData(key);
		o[0]=value;
		try {
			o[2]=value+"";
			o[3]=set;
		}catch(Exception outOfBound) {
			Object[] h = new Object[4];
			h[0]=value;
			h[1]=o[1];
			h[2]=value+"";
			h[3]=set;
			loader.set(key, h);
		}
		return this;
	}

	public synchronized Data remove(String key) {
		if (key == null)
			return this;
		if (key.split("\\.").length <= 1)
			aw.remove(key.split("\\.")[0]);
		loader.remove(key);
		Iterator<String> s = loader.getKeys().iterator();
		while(s.hasNext()) {
			String d = s.next();
			if (d.startsWith(key)) {
				if(d.substring(key.length()).trim().startsWith(".")) {
					s.remove();
					loader.remove(d);
				}
			}
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized List<String> getComments(String key) {
		if (key == null)
			return null;
		return (List<String>) getOrCreateData(key)[1];
	}

	public synchronized Data setComments(String key, List<String> value) {
		if (key == null)
			return this;
		getOrCreateData(key)[1]=value;
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized Data addComments(String key, List<String> value) {
		if (value == null || key == null)
			return this;
		Object[] g = getOrCreateData(key);
		if(g[1]==null)g[1]=value;
		else
		((List<String>) g[1]).addAll(value);
		return this;
	}

	public synchronized Data addComment(String key, String value) {
		if (value == null || key == null)
			return this;
		return addComments(key, Arrays.asList(value));
	}

	@SuppressWarnings("unchecked")
	public synchronized Data removeComments(String key, List<String> value) {
		if (value == null || key == null)
			return this;
		Object[] g = getOrCreateData(key);
		if(g[0]!=null)
			((List<String>) g[1]).removeAll(value);
		return this;
	}

	public synchronized Data removeComment(String key, String value) {
		if (value == null || key == null)
			return this;
		return removeComments(key, Arrays.asList(value));
	}

	@SuppressWarnings("unchecked")
	public synchronized Data removeComment(String key, int line) {
		if (line < 0 || key == null)
			return this;
		Object[] h = getOrCreateData(key);
		if (h[1] != null)
			((List<String>) h[1]).remove(line);
		return this;
	}

	public synchronized File getFile() {
		return a;
	}

	public synchronized Data setHeader(Collection<String> lines) {
		loader.getHeader().clear();
		loader.getHeader().addAll(lines);
		return this;
	}

	public synchronized Data setFooter(Collection<String> lines) {
		loader.getFooter().clear();
		loader.getFooter().addAll(lines);
		return this;
	}

	public synchronized Collection<String> getHeader() {
		return loader.getHeader();
	}

	public synchronized Collection<String> getFooter() {
		return loader.getFooter();
	}

	public synchronized Data reload(String input) {
		aw.clear();
		loader = DataLoader.findLoaderFor(input); // get & load
		for (String k : loader.getKeys())
			if (!aw.contains(k.split("\\.")[0]))
				aw.add(k.split("\\.")[0]);
		return this;
	}

	public synchronized Data reload(File f) {
		if (!f.exists()) {
			try {
				if(f.getParentFile()!=null)
					f.getParentFile().mkdirs();
			} catch (Exception e) {
			}
			try {
				f.createNewFile();
			} catch (Exception e) {
			}
		}
		return reload(StreamUtils.fromStream(f));
	}

	public synchronized Object get(String key) {
		try {
			return loader.get().get(key)[0];
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized <E> E getAs(String key, Class<? extends E> clazz) {
		try {
		if(clazz==String.class||clazz==CharSequence.class)return clazz.cast(getString(key));
		} catch (Exception e) {
		}
		try {
			return clazz.cast(loader.get().get(key)[0]);
		} catch (Exception e) {
		}
		return null;
	}

	public synchronized String getString(String key) {
		Object[] a = loader.get().get(key);
		if(a==null)return null;
		try {
			return (String)a[2];
		}catch(Exception outOfBound) {
			return a[0] instanceof String ? (String)a[0] : (a[0]==null?null:a[0]+"");
		}
	}

	public synchronized int getInt(String key) {
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

	public synchronized double getDouble(String key) {
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

	public synchronized long getLong(String key) {
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

	public synchronized float getFloat(String key) {
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

	public synchronized byte getByte(String key) {
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

	public synchronized boolean getBoolean(String key) {
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

	public synchronized short getShort(String key) {
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
	public synchronized Collection<Object> getList(String key) {
		return get(key) != null && get(key) instanceof Collection ? (Collection) get(key) : new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public synchronized <E> List<E> getListAs(String key, Class<? extends E> clazz) {
		// Cast everything to <E>
		try {
			//try to cast List<?> to List<E>
			return (List<E>)getList(key);
		}catch(Exception err) {
			List<E> list = new ArrayList<>();
			for (Object o : getList(key))
				try {
					list.add(o == null ? null : clazz.cast(o));
				} catch (Exception er) {
				}
			return list;
		}
	}

	public synchronized List<String> getStringList(String key) {
		List<String> list = getListAs(key, String.class);
		// Cast everything to String
		Collection<Object> items = getList(key);
		if(list!=null && list.containsAll(items))return list;
		list=new ArrayList<>(items.size());
		for (Object o : items)
			if (o != null)
				list.add("" + o);
			else
				list.add(null);
		return list;
	}

	public synchronized List<Boolean> getBooleanList(String key) {
		List<Boolean> list = getListAs(key, Boolean.class);
		// Cast everything to Boolean
		Collection<Object> items = getList(key);
		if(list!=null && list.containsAll(items))return list;
		list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? false : o instanceof Boolean ? (Boolean)o: StringUtils.getBoolean(o.toString()));
		return list;
	}

	public synchronized List<Integer> getIntegerList(String key) {
		List<Integer> list = getListAs(key, Integer.class);
		// Cast everything to Integer
		Collection<Object> items = getList(key);
		if(list!=null && list.containsAll(items))return list;
		list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).intValue():StringUtils.getInt(o.toString()));
		return list;
	}

	public synchronized List<Double> getDoubleList(String key) {
		List<Double> list = getListAs(key, Double.class);
		// Cast everything to Double
		Collection<Object> items = getList(key);
		if(list!=null && list.containsAll(items))return list;
		list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0.0 : o instanceof Number ? ((Number)o).doubleValue():StringUtils.getDouble(o.toString()));
		return list;
	}

	public synchronized List<Short> getShortList(String key) {
		List<Short> list = getListAs(key, Short.class);
		// Cast everything to Short
		Collection<Object> items = getList(key);
		if(list!=null && list.containsAll(items))return list;
		list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).shortValue():StringUtils.getShort(o.toString()));
		return list;
	}

	public synchronized List<Byte> getByteList(String key) {
		List<Byte> list = getListAs(key, Byte.class);
		// Cast everything to Byte
		Collection<Object> items = getList(key);
		if(list!=null && list.containsAll(items))return list;
		list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).byteValue():StringUtils.getByte(o.toString()));
		return list;
	}

	public synchronized List<Float> getFloatList(String key) {
		List<Float> list = getListAs(key, Float.class);
		// Cast everything to Float
		Collection<Object> items = getList(key);
		if(list!=null && list.containsAll(items))return list;
		list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).floatValue():StringUtils.getFloat(o.toString()));
		return list;
	}

	public synchronized List<Long> getLongList(String key) {
		List<Long> list = getListAs(key, Long.class);
		// Cast everything to Long
		Collection<Object> items = getList(key);
		if(list!=null && list.containsAll(items))return list;
		list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getLong(o.toString()));
		return list;
	}

	@SuppressWarnings("unchecked")
	public synchronized <K, V> List<Map<K, V>> getMapList(String key) {
		List<Map<K, V>> list = getListAs(key, Map.class);
		// Cast everything to Long
		Collection<Object> items = getList(key);
		if(list!=null && list.containsAll(items))return list;
		list=new ArrayList<>(items.size());
		for (Object o : items) {
			Object re = Reader.read(o.toString());
			list.add(o == null ? null : re instanceof Map ? (Map<K, V>)re : new HashMap<>());
		}
		return list;
	}

	public synchronized Data save(DataType type) {
		if (a == null)
			return this;
		try {
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(a), StandardCharsets.UTF_8);
			if (type == DataType.BYTE) {
				try {
					ByteArrayDataOutput bos = ByteStreams.newDataOutput(loader.get().size());
					bos.writeInt(1);
					for (Entry<String, Object[]> key : loader.get().entrySet())
						try {
							bos.writeUTF(key.getKey());
							if(key.getValue()[0]==null) {
								bos.writeUTF(null);
							}else {
								String write = Writer.write(key.getValue()[0]);
								while(write.length()>35000) {
									String wr = write.substring(0, 34999);
									bos.writeUTF("0"+wr);
									write=write.substring(34999);
								}
								bos.writeUTF("0"+write);
							}
							bos.writeUTF("0");
						} catch (Exception er) {
						}
					w.write(Base64.getEncoder().encodeToString(bos.toByteArray()));
					w.close();
				} catch (Exception e) {
					w.close();
				}
				return this;
			}
			if (type == DataType.JSON) {
				Maker maker = new Maker();
				for (String key : new LinkedHashSet<>(aw))
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
			for (String key : new LinkedHashSet<>(aw))
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
			return StringUtils.isBoolean(text)?text:StringUtils.isNumber(text)?"'"+text+"'":"\"" + text.replace(System.lineSeparator(), "\\n") + "\"";
		}
		return text;
	}
	
	public synchronized void save() {
		save(DataType.YAML);
	}

	public synchronized Set<String> getKeys() {
		return new LinkedHashSet<>(aw);
	}

	public synchronized Set<String> getKeys(boolean subkeys) {
		if (subkeys)
			return loader.getKeys();
		return new LinkedHashSet<>(aw);
	}

	public synchronized Set<String> getKeys(String key) {
		return getKeys(key, false);
	}

	public synchronized boolean isKey(String key) {
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

	public synchronized Set<String> getKeys(String key, boolean subkeys) {
		Set<String> a = new LinkedHashSet<>();
		for (String d : loader.getKeys())
			if (d.startsWith(key)) {
				String c = d.substring(key.length());
				if (!c.startsWith("."))
					continue;
				c = c.startsWith(".")?c.substring(1):c;
				if(!subkeys)
				c = c.split("\\.")[0];
				if (c.trim().isEmpty())
					continue;
				if (!a.contains(c))
					a.add(c);
			}
		return a;
	}

	public synchronized String toString() {
		return toString(DataType.BYTE);
	}

	private synchronized void addKeys(Maker main, String key) {
		Object o = get(key);
		if (o != null)
			main.add(main.create().put(key, o));
		for (String keyer : getKeys(key))
			addKeys(main, key + "." + keyer);
	}

	@SuppressWarnings("unchecked")
	private synchronized void preparePath(String path, String pathName, int spaces, java.io.Writer b) {
		try {
			Object[] aw = loader.get().get(path);
			Collection<String> list = aw != null ? (Collection<String>)aw[1] : null;
			Object o = aw != null ? aw[0] : null;
			String space = cs(spaces);
			pathName = space + pathName;
			if(list != null && !list.isEmpty()) {
				for (String s : list)
					b.write(space + s + System.lineSeparator());
			}
				if(o==null)b.write(pathName + System.lineSeparator());
				else {
					if (o instanceof Collection || o instanceof Object[]) {
						String splitted = space + "- ";
						boolean add = false;
						if (o instanceof Collection) {
							if(!((Collection<?>) o).isEmpty()) {
								try {
									if((int)aw[3]==set) {
										b.write(pathName + " "+addQuotes(true, aw[2]+"") + System.lineSeparator());
									}else {
										if(Reader.read(aw[2]+"") instanceof Map||Reader.read(aw[2]+"") instanceof Collection) { //json
											b.write(pathName + " "+addQuotes(true, aw[2]+"") + System.lineSeparator());
										}else {
											add=true;
											b.write(pathName + System.lineSeparator());
											for (Object a : (Collection<?>) o) {
												b.write(splitted+addQuotes(a instanceof String, Writer.write(a)) + System.lineSeparator());
										}}
									}
								}catch(Exception er) {
									try {
									if(Reader.read(aw[2]+"") instanceof Map||Reader.read(aw[2]+"") instanceof Collection) { //json
										b.write(pathName + " "+addQuotes(true, aw[2]+"") + System.lineSeparator());
									}else {
										if(!add) {
											add=true;
											b.write(pathName + System.lineSeparator());
										}
									for (Object a : (Collection<?>) o) {
										b.write(splitted+addQuotes(a instanceof String, Writer.write(a)) + System.lineSeparator());
									}}
									}catch(Exception unsuported) {
										if(!add) {
											add=true;
											b.write(pathName + System.lineSeparator());
										}
										for (Object a : (Collection<?>) o) {
											b.write(splitted+addQuotes(a instanceof String, Writer.write(a)) + System.lineSeparator());
										}
									}
								}
							}else
								b.write(pathName + " []" + System.lineSeparator());
						} else {
							if(((Object[]) o).length!=0) {
								b.write(pathName + System.lineSeparator());
								try {
									if((int)aw[3]==set) {
										b.write(pathName + " "+addQuotes(true, aw[2]+"") + System.lineSeparator());
									}else {
										if(Reader.read(aw[2]+"") instanceof Map||Reader.read(aw[2]+"") instanceof Collection) { //json
											b.write(pathName + " "+addQuotes(true, aw[2]+"") + System.lineSeparator());
										}else {
											add=true;
											b.write(pathName + System.lineSeparator());
										for (Object a : (Collection<?>) o) {
											b.write(splitted+addQuotes(a instanceof String, Writer.write(a)) + System.lineSeparator());
										}}
									}
								}catch(Exception er) {
									try {
									if(Reader.read(aw[2]+"") instanceof Map||Reader.read(aw[2]+"") instanceof Collection) { //json
										b.write(pathName + " "+addQuotes(true, aw[2]+"") + System.lineSeparator());
									}else {
										if(!add) {
											add=true;
											b.write(pathName + System.lineSeparator());
										}
									for (Object a : (Collection<?>) o) {
										b.write(splitted+addQuotes(a instanceof String, Writer.write(a)) + System.lineSeparator());
									}}
									}catch(Exception unsuported) {
										if(!add) {
											add=true;
											b.write(pathName + System.lineSeparator());
										}
										for (Object a : (Collection<?>) o) {
											b.write(splitted+addQuotes(a instanceof String, Writer.write(a)) + System.lineSeparator());
										}
									}
							}}else
								b.write(pathName + " []" + System.lineSeparator());
						}
					} else {
						try {
							if((int)aw[3]==set) {
								b.write(pathName + " "+addQuotes(true, aw[2]+"") + System.lineSeparator());
							}else {
								if(Reader.read(aw[2]+"") instanceof Map||Reader.read(aw[2]+"") instanceof Collection) { //json
									b.write(pathName + " "+addQuotes(true, aw[2]+"") + System.lineSeparator());
								}else
									b.write(pathName + " "+addQuotes(o instanceof String, Writer.write(o)) + System.lineSeparator());
							}
						}catch(Exception er) {
							try {
								if(Reader.read(aw[2]+"") instanceof Map||Reader.read(aw[2]+"") instanceof Collection) { //json
									b.write(pathName + " "+aw[2] + System.lineSeparator());
								}else
									b.write(pathName + " "+addQuotes(o instanceof String, Writer.write(o)) + System.lineSeparator());
							}catch(Exception unsuported) {
								b.write(pathName + " "+addQuotes(o instanceof String, Writer.write(o)) + System.lineSeparator());
							}
						}
					}
				}
			for (String key : getKeys(path, false))
				preparePath(path + "." + key, key + ":", spaces + 1, b);
		} catch (Exception er) {
		}
	}

	public synchronized String toString(DataType type) {
		if (type == DataType.BYTE) {
			try {
				ByteArrayDataOutput bos = ByteStreams.newDataOutput(loader.get().size());
				bos.writeInt(1);
				for (Entry<String, Object[]> key : loader.get().entrySet())
					try {
						bos.writeUTF(key.getKey());
						if(key.getValue()[0]==null) {
							bos.writeUTF(null);
						}else {
							String write = Writer.write(key.getValue()[0]);
							while(write.length()>35000) {
								String wr = write.substring(0, 34999);
								bos.writeUTF("0"+wr);
								write=write.substring(34999);
							}
							bos.writeUTF("0"+write);
						}
						bos.writeUTF("0");
					} catch (Exception er) {
					}
				return Base64.getEncoder().encodeToString(bos.toByteArray());
			} catch (Exception e) {
			}
			return "";
		}
		if (type == DataType.JSON) {
			Maker main = new Maker();
			for (String key : new LinkedHashSet<>(aw))
				addKeys(main, key);
			return main.toString();
		}

		StringWriter d = new StringWriter();
		try {
			for (String h : loader.getHeader())
				d.write(h + System.lineSeparator());
		} catch (Exception er) {
		}
		for (String key : new LinkedHashSet<>(aw))
			preparePath(key, key + ":", 0, d);
		try {
			for (String h : loader.getFooter())
				d.write(h + System.lineSeparator());
		} catch (Exception er) {
			Validator.send("Saving Data to YAML", er);
		}
		return d.toString();
	}

	private static final String space = "  ";
	private String cs(int s) {
		StringBuilder i = new StringBuilder();
		for (int c = 0; c < s; ++c)
			i.append(space);
		return i.toString();
	}

	@Override
	public synchronized String getDataName() {
		HashMap<String, Object> s = new HashMap<>();
		if(a!=null)s.put("file", a.getPath()+"/"+a.getName());
		s.put("loader", loader.getDataName());
		return Writer.write(s);
	}

	public synchronized Data clear() {
		loader.get().clear();
		return this;
	}

	public synchronized Data reset() {
		loader.reset();
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized boolean merge(Data f, boolean addHeader, boolean addFooter) {
		boolean change = false;
		try {
		for(Entry<String, Object[]> s : f.loader.get().entrySet()) {
			if(get(s.getKey())==null && s.getValue()[0]!=null) {
				Object[] o = getOrCreateData(s.getKey());
				o[0]=s.getValue()[0];
				try {
				o[2]=s.getValue()[2]==null?null:s.getValue()[2]+"";
				}catch(Exception outOfBoud) {
					try {
						o[2]=s.getValue()[0]==null?null:s.getValue()[0]+"";
					}catch(Exception outOfBoud2) {
						
					}
				}
				change = true;
			}
			try {
			if(addHeader)
				if(f.loader.getHeader()==null || f.loader.getHeader()!=null && !f.loader.getHeader().isEmpty() && (loader.getHeader().isEmpty()||!f.loader.getHeader().containsAll(loader.getHeader()))) {
					setHeader(f.loader.getHeader());
					change = true;
				}
			if(addFooter)
				if(f.loader.getFooter()==null || f.loader.getFooter()!=null && !f.loader.getFooter().isEmpty() && (loader.getFooter().isEmpty()||!f.loader.getFooter().containsAll(loader.getFooter()))) {
					setFooter(f.loader.getFooter());
					change = true;
				}
			}catch(Exception nope) {}
			if(s.getValue()[1]!=null && !((List<String>) s.getValue()[1]).isEmpty())
    		if(getComments(s.getKey())==null || getComments(s.getKey()).isEmpty()) {
    			if(getHeader()!=null && !getHeader().isEmpty() && ((List<String>)s.getValue()[1]).containsAll(getHeader())
    					|| getFooter()!=null && !getFooter().isEmpty() && ((List<String>) s.getValue()[1]).containsAll(getFooter()))continue;
    			setComments(s.getKey(), (List<String>)s.getValue()[1]);
    			change = true;
    		}
		}
		}catch(Exception err) {}
		return change;
	}
}