package me.devtec.theapi.utils.datakeeper;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.loader.DataLoader;
import me.devtec.theapi.utils.datakeeper.loader.EmptyLoader;
import me.devtec.theapi.utils.datakeeper.loader.YamlLoader;
import me.devtec.theapi.utils.json.Json;
import me.devtec.theapi.utils.json.Maker;
import me.devtec.theapi.utils.theapiutils.Validator;

public class Data implements me.devtec.theapi.utils.datakeeper.abstracts.Data {
	protected DataLoader loader;
	protected List<String> aw;
	protected File a;
	
	public Data() {
		loader = new EmptyLoader();
		aw = new LinkedList<>();
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
		aw = new LinkedList<>();
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
		for (String k : loader.getKeys())
			if (k.startsWith(path))
				return true;
		return false;
	}

	public synchronized boolean existsKey(String path) {
		return loader.get().containsKey(path);
	}

	public synchronized Data setFile(File f) {
		a = f;
		return this;
	}

	public synchronized Object[] getOrCreateData(String key) {
		Object[] h = loader.get().get(key);
		if (h == null) {
			String ss = key.split("\\.")[0];
			if (!aw.contains(ss))
				aw.add(ss);
			loader.get().put(key, h = new Object[2]);
		}
		return h;
	}
	
	public synchronized boolean setIfAbsent(String key, Object value) {
		if (key == null || value==null)
			return false;
		if(!existsKey(key)) {
			requireSave=true;
			Object[] data = getOrCreateData(key);
			data[0]=value;
			return true;
		}
		return false;
	}

	public synchronized boolean setIfAbsent(String key, Object value, List<String> comments) {
		if (key == null || value==null)
			return false;
		if(!existsKey(key)) {
			requireSave=true;
			Object[] data = getOrCreateData(key);
			data[0]=value;
			data[1]=comments;
			return true;
		}
		Object[] data = getOrCreateData(key);
		if(data[1]==null && comments!=null && !comments.isEmpty() || data[1]!=null && ((List<?>) data[1]).isEmpty() && comments!=null && !comments.isEmpty()) {
			data[1]=comments;
			requireSave=true;
			return true;
		}
		return false;
	}

	public Data set(String key, Object value) {
		if (key == null)
			return this;
		requireSave=true;
		if (value == null) {
			String[] sf = key.split("\\.");
			if (sf.length <= 1)
				aw.remove(sf[0]);
			loader.remove(key);
			return this;
		}
		Object[] o = getOrCreateData(key);
		o[0]=value;
		switch(o.length) {
		case 4:
			o[2]=null;
			o[3]=null;
			break;
		case 3:
			o[2]=null;
			break;
		}
		return this;
	}

	public synchronized Data remove(String key) {
		if (key == null)
			return this;
		requireSave=true;
		if (key.split("\\.").length<=1)
			aw.remove(key.split("\\.")[0]);
		loader.remove(key);
		for(String d : new ArrayList<>(loader.get().keySet()))
			if (d.startsWith(key) && d.substring(key.length()).startsWith("."))
				loader.get().remove(d);
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized List<String> getComments(String key) {
		if (key == null)
			return null;
		requireSave=true;
		Object obj = getOrCreateData(key)[1];
		return obj==null?null:(List<String>)obj;
	}

	public synchronized Data setComments(String key, List<String> value) {
		if (key == null)
			return this;
		requireSave=true;
		getOrCreateData(key)[1]=value;
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized Data addComments(String key, List<String> value) {
		if (value == null || key == null)
			return this;
		requireSave=true;
		Object[] g = getOrCreateData(key);
		if(g[1]==null)g[1]=value;
		else
		((List<String>) g[1]).addAll(value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized Data addComment(String key, String value) {
		if (value == null || key == null)
			return this;
		requireSave=true;
		Object[] g = getOrCreateData(key);
		if(g[0]==null)g[1]=new ArrayList<>();
		((List<String>) g[1]).add(value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized Data removeComments(String key, List<String> value) {
		if (value == null || key == null)
			return this;
		requireSave=true;
		Object[] g = getOrCreateData(key);
		if(g[0]!=null)
			((List<String>) g[1]).removeAll(value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized Data removeComment(String key, String value) {
		if (value == null || key == null)
			return this;
		requireSave=true;
		Object[] g = getOrCreateData(key);
		if(g[0]!=null)
			((List<String>) g[1]).remove(value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized Data removeComment(String key, int line) {
		if (line < 0 || key == null)
			return this;
		requireSave=true;
		Object[] h = getOrCreateData(key);
		if (h[1] != null)
			((List<String>) h[1]).remove(line);
		return this;
	}

	public synchronized File getFile() {
		return a;
	}

	public synchronized Data setHeader(Collection<String> lines) {
		requireSave=true;
		loader.getHeader().clear();
		loader.getHeader().addAll(lines);
		return this;
	}

	public synchronized Data setFooter(Collection<String> lines) {
		requireSave=true;
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
		requireSave=true;
		aw.clear();
		loader = DataLoader.findLoaderFor(input); // get & load
		for (String k : loader.getKeys()) {
			String g = k.split("\\.")[0];
			if (!aw.contains(g))
				aw.add(g);
		}
		return this;
	}

	public synchronized Data reload(File f) {
		if (!f.exists()) {
			requireSave=true;
			loader=new YamlLoader();
			aw.clear();
			return this;
		}
		requireSave=true;
		aw.clear();
		loader = DataLoader.findLoaderFor(f); // get & load
		for (String k : loader.getKeys()) {
			String g = k.split("\\.")[0];
			if (!aw.contains(g))
				aw.add(g);
		}
		return this;
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
		if(a.length>=3) {
			Object s = a[2];
			if(s!=null)return s+"";
		}
		return a[0] instanceof String ? (String)a[0] : (a[0]==null?null:a[0]+"");
	}

	public synchronized int getInt(String key) {
		try {
			return ((Number)get(key)).intValue();
		} catch (Exception notNumber) {
			return StringUtils.getInt(getString(key));
		}
	}

	public synchronized double getDouble(String key) {
		try {
			return ((Number)get(key)).doubleValue();
		} catch (Exception notNumber) {
			return StringUtils.getDouble(getString(key));
		}
	}

	public synchronized long getLong(String key) {
		try {
			return ((Number)get(key)).longValue();
		} catch (Exception notNumber) {
			return StringUtils.getLong(getString(key));
		}
	}

	public synchronized float getFloat(String key) {
		try {
			return ((Number)get(key)).floatValue();
		} catch (Exception notNumber) {
			return StringUtils.getFloat(getString(key));
		}
	}

	public synchronized byte getByte(String key) {
		try {
			return ((Number)get(key)).byteValue();
		} catch (Exception notNumber) {
			return StringUtils.getByte(getString(key));
		}
	}

	public synchronized boolean getBoolean(String key) {
		try {
			return (boolean)get(key);
		} catch (Exception notNumber) {
			return StringUtils.getBoolean(getString(key));
		}
	}

	public synchronized short getShort(String key) {
		try {
			return ((Number)get(key)).shortValue();
		} catch (Exception notNumber) {
			return StringUtils.getShort(getString(key));
		}
	}
	
	public synchronized Collection<Object> getList(String key) {
		Object g = get(key);
		return g != null && g instanceof Collection ? new ArrayList<>((Collection<?>) g) : new ArrayList<>();
	}

	public synchronized <E> List<E> getListAs(String key, Class<? extends E> clazz) {
		List<E> list = new ArrayList<>();
		for (Object o : getList(key))
			try {
				list.add(o == null ? null : clazz.cast(o));
			} catch (Exception er) {
			}
		return list;
	}

	public synchronized List<String> getStringList(String key) {
		Collection<Object> items = getList(key);
		List<String> list=new ArrayList<>(items.size());
		for (Object o : items)
			if (o != null)
				list.add("" + o);
			else
				list.add(null);
		return list;
	}

	public synchronized List<Boolean> getBooleanList(String key) {
		Collection<Object> items = getList(key);
		List<Boolean> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o != null && (o instanceof Boolean ? (Boolean) o : StringUtils.getBoolean(o.toString())));
		return list;
	}

	public synchronized List<Integer> getIntegerList(String key) {
		Collection<Object> items = getList(key);
		List<Integer> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).intValue():StringUtils.getInt(o.toString()));
		return list;
	}

	public synchronized List<Double> getDoubleList(String key) {
		Collection<Object> items = getList(key);
		List<Double> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0.0 : o instanceof Number ? ((Number)o).doubleValue():StringUtils.getDouble(o.toString()));
		return list;
	}

	public synchronized List<Short> getShortList(String key) {
		Collection<Object> items = getList(key);
		List<Short> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).shortValue():StringUtils.getShort(o.toString()));
		return list;
	}

	public synchronized List<Byte> getByteList(String key) {
		Collection<Object> items = getList(key);
		List<Byte> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).byteValue():StringUtils.getByte(o.toString()));
		return list;
	}

	public synchronized List<Float> getFloatList(String key) {
		Collection<Object> items = getList(key);
		List<Float> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : o instanceof Number ? ((Number)o).floatValue():StringUtils.getFloat(o.toString()));
		return list;
	}

	public synchronized List<Long> getLongList(String key) {
		Collection<Object> items = getList(key);
		List<Long> list=new ArrayList<>(items.size());
		for (Object o : items)
			list.add(o == null ? 0 : StringUtils.getLong(o.toString()));
		return list;
	}

	@SuppressWarnings("unchecked")
	public synchronized <K, V> List<Map<K, V>> getMapList(String key) {
		Collection<Object> items = getList(key);
		List<Map<K, V>> list=new ArrayList<>(items.size());
		for (Object o : items) {
			if(o==null)
				list.add(null);
			else {
				Object re = Json.reader().read(o.toString());
				list.add(re instanceof Map ? (Map<K, V>) re : new HashMap<>());
			}
		}
		return list;
	}

	protected boolean isSaving, requireSave;
	public synchronized Data save(DataType type) {
		if (a == null)
			return this;
		if(isSaving)return this;
		if(!requireSave)return this;
		if (!a.exists()) {
			try {
				a.getParentFile().mkdirs();
			} catch (Exception e) {
			}
			try {
				a.createNewFile();
			} catch (Exception e) {
			}
		}
		isSaving=true;
		requireSave=false;
		try {
			new Thread(() -> {
				try {
					OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(a), StandardCharsets.UTF_8);
					w.write(toString(type));
					w.close();
				} catch (Exception e1) {
				}
				isSaving=false;
			}).start();
		} catch (Exception e1) {
		}
		return this;
	}

	protected synchronized void addQuotesSplit(StringBuilder b, CharSequence split, String aw) {
		b.append(split);
		b.append('"');
		b.append(aw);
		b.append('"');
		b.append(System.lineSeparator());
	}
	
	protected synchronized void addQuotesSplit(StringBuilder b, CharSequence split, Object aw) {
		b.append(split);
		b.append(Json.writer().write(aw));
		b.append(System.lineSeparator());
	}
	
	protected synchronized void addQuotes(StringBuilder b, CharSequence pathName, String aw, boolean add) {
		b.append(pathName).append(' ');
		if(add) {
			b.append(aw);
		}else {
			b.append('"');
			b.append(aw);
			b.append('"');
		}
		b.append(System.lineSeparator());
	}
	
	protected synchronized void addQuotes(StringBuilder b, CharSequence pathName, Object aw) {
		b.append(pathName).append(' ');
		b.append(Json.writer().write(aw));
		b.append(System.lineSeparator());
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
		for (String k : loader.getKeys()) {
			if (k.startsWith(key)) {
				String r = k.substring(key.length());
				if (r.startsWith(".") || r.trim().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	public synchronized Set<String> getKeys(String key, boolean subkeys) {
		Set<String> a = new LinkedHashSet<>();
		for (String d : loader.getKeys())
			if (d.startsWith(key)) {
				String c = d.substring(key.length());
				if (!c.startsWith("."))
					continue;
				c = c.substring(1);
				if(!subkeys)
				c =  c.split("\\.")[0];
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

	protected synchronized void addKeys(Maker main, String key) {
		Object o = get(key);
		if (o != null)
			main.add(main.create().put(key, o));
		for (String keyer : getKeys(key))
			addKeys(main, key + "." + keyer);
	}

	@SuppressWarnings("unchecked")
	protected synchronized void preparePath(List<String> done, String path, String pathName, String space, StringBuilder b) {
		StringBuilder bab = new StringBuilder(pathName.length()+space.length()+2);
		bab.append(space);
		String split = pathName.substring(0, pathName.length()-1);
		if(split.length()==1) { //char
			bab.append('"').append(split).append('"').append(':');
		}else
		if(split.contains(":")) {
			bab.append('"').append(split).append('"').append(':');
		}else
		if(split.startsWith("#")) { //starts with comment
			bab.append('"').append(split).append('"').append(':');
		}else bab.append(pathName);
		try {
		Object[] aw = loader.get().get(path);
		if(aw==null) {
			b.append(bab).append(System.lineSeparator());
			for (String d : loader.get().keySet()) {
				if (d.startsWith(path)) {
					String c = d.substring(path.length());
					if (!c.startsWith("."))
						continue;
					c = c.substring(1);
					c =  c.split("\\.")[0];
					if (c.trim().isEmpty())
						continue;
					String dd = path+"."+c;
					if(!done.contains(dd)) {
						done.add(dd);
						preparePath(done, dd, c+":", space+"  ", b);
					}
				}
			}
			return;
		}
		Collection<String> list = (Collection<String>)aw[1];
		Object o = aw[0];
		if(list != null)
			for (String s : list)
				b.append(space).append(s).append(System.lineSeparator());
		if(o==null)b.append(bab).append(System.lineSeparator());
		else {
			if (o instanceof Collection || o instanceof Object[]) {
				String splitted = space+"- ";
				if (o instanceof Collection) {
					if(!((Collection<?>) o).isEmpty()) {
						try {
							if(aw[3]!=null && (int)aw[3]==1) {
								addQuotes(b,bab,(String)aw[2], o instanceof Comparable && !(o instanceof String));
							}else {
								b.append(bab).append(System.lineSeparator());
								for (Object a : (Collection<?>) o) {
									if(a instanceof String)
										addQuotesSplit(b,splitted,(String)a);
									else
										addQuotesSplit(b,splitted,a);
								}
							}
						}catch(Exception er) {
							b.append(bab).append(System.lineSeparator());
							for (Object a : (Collection<?>) o) {
								if(a instanceof String)
									addQuotesSplit(b,splitted,(String)a);
								else
									addQuotesSplit(b,splitted,a);
							}
					}}else
						b.append(bab).append(" []").append(System.lineSeparator());
				} else {
					if(((Object[]) o).length!=0) {
						try {
							if(aw[3]!=null && (int)aw[3]==1) {
								addQuotes(b,bab,(String)aw[2], o instanceof Comparable && !(o instanceof String));
							}else {
								b.append(bab).append(System.lineSeparator());
								for (Object a : (Object[]) o) {
									if(a instanceof String)
										addQuotesSplit(b,splitted,(String)a);
									else
										addQuotesSplit(b,splitted,a);
								}
							}
						}catch(Exception er) {
							b.append(bab).append(System.lineSeparator());
							for (Object a : (Object[]) o) {
								if(a instanceof String)
									addQuotesSplit(b,splitted,(String)a);
								else
									addQuotesSplit(b,splitted,a);
							}
					}}else
						b.append(bab).append(" []").append(System.lineSeparator());
				}
			} else {
				try {
					if(aw[3]!=null && (int)aw[3]==1) {
						addQuotes(b,bab,(String)aw[2], o instanceof Comparable && !(o instanceof String));
					}else {
						if(o instanceof String)
							addQuotes(b,bab,(String)o, false);
						else
							addQuotes(b,bab,o);
					}
				}catch(Exception er) {
					if(o instanceof String)
						addQuotes(b,bab,(String)o, false);
					else
						addQuotes(b,bab,o);
				}
			}
		}
		for (String d : loader.getKeys())
			if (d.startsWith(path)) {
				String c = d.substring(path.length());
				if (!c.startsWith("."))
					continue;
				c = c.substring(1);
				c = c.split("\\.")[0];
				if (c.trim().isEmpty())
					continue;
				String dd = path+"."+c;
				if(!done.contains(dd)) {
					done.add(dd);
					preparePath(done, dd, c+":", space+"  ", b);
				}
			}
		}catch(Exception err) {}
	}

	public synchronized String toString(DataType type) {
		switch(type) {
		case BYTE:
			try {
				ByteArrayDataOutput bos = ByteStreams.newDataOutput(loader.get().size());
				bos.writeInt(2);
				bos.writeUTF("1");
				for (Entry<String, Object[]> key : loader.get().entrySet())
					try {
						bos.writeUTF(key.getKey());
						if(key.getValue()[0]==null) {
							bos.writeUTF("null");
							bos.writeUTF("0");
						}else {
							if(key.getValue().length >2)
								if(key.getValue()[2]!=null && key.getValue()[2] instanceof String) {
									String write = (String)key.getValue()[2];
									if(write==null) {
										bos.writeUTF("null");
										bos.writeUTF("0");
										continue;
									}
									while(write.length()>40000) {
										String wr = write.substring(0, 39999);
										bos.writeUTF("0"+wr);
										write=write.substring(39999);
									}
									bos.writeUTF("0"+write);
									bos.writeUTF("0");
									continue;
								}
							Object val = key.getValue()[0];
							String write = val instanceof String ? (String)val:Json.writer().write(val);
							if(write==null) {
								bos.writeUTF("null");
								bos.writeUTF("0");
								continue;
							}
							while(write.length()>40000) {
								String wr = write.substring(0, 39999);
								bos.writeUTF("0"+wr);
								write=write.substring(39999);
							}
							bos.writeUTF("0"+write);
							bos.writeUTF("0");
							continue;
						}
					} catch (Exception er) {
						er.printStackTrace();
					}
				return Base64.getEncoder().encodeToString(bos.toByteArray());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		case JSON:
			Maker main = new Maker();
			for (String key : Collections.unmodifiableList(aw))
				addKeys(main, key);
			return main.toString();
		case YAML:
			int size = loader.get().size();
			StringBuilder d = new StringBuilder(size*8);
			try {
				for (String h : loader.getHeader())
					d.append(h).append(System.lineSeparator());
			} catch (Exception er) {
			}
			List<String> done = new ArrayList<>(size);
			for (String key : Collections.unmodifiableList(aw))
				preparePath(done,key, key + ":", "", d);
			try {
				for (String h : loader.getFooter())
					d.append(h).append(System.lineSeparator());
			} catch (Exception er) {
				Validator.send("Saving Data to YAML", er);
			}
			return d.toString();
		}
		return null;
	}
	
	@Override
	public String getDataName() {
		HashMap<String, Object> s = new HashMap<>();
		if(a!=null)s.put("file", a.getPath()+"/"+a.getName());
		s.put("loader", loader.getDataName());
		return Json.writer().write(s);
	}

	public synchronized Data clear() {
		aw.clear();
		loader.get().clear();
		return this;
	}

	public synchronized Data reset() {
		aw.clear();
		loader.reset();
		return this;
	}

	@SuppressWarnings("unchecked")
	public synchronized boolean merge(Data f, boolean addHeader, boolean addFooter) {
		boolean change = false;
		try {
		if(addHeader)
			if(f.loader.getHeader()==null || f.loader.getHeader()!=null && !f.loader.getHeader().isEmpty() && (loader.getHeader().isEmpty()||!f.loader.getHeader().containsAll(loader.getHeader()))) {
				loader.getHeader().clear();
				loader.getHeader().addAll(f.loader.getHeader());
				change = true;
			}
		if(addFooter)
			if(f.loader.getFooter()==null || f.loader.getFooter()!=null && !f.loader.getFooter().isEmpty() && (loader.getFooter().isEmpty()||!f.loader.getFooter().containsAll(loader.getFooter()))) {
				loader.getFooter().clear();
				loader.getFooter().addAll(f.loader.getFooter());
				change = true;
			}
		}catch(Exception nope) {}
		try {
		for(Entry<String, Object[]> s : f.loader.get().entrySet()) {
			Object[] o = getOrCreateData(s.getKey());
			if(o[0]==null && s.getValue()[0]!=null) {
				o[0]=s.getValue()[0];
				try {
				o[2]=s.getValue()[2];
				}catch(Exception outOfBoud) {}
				change = true;
			}
			if(s.getValue()[1]!=null && !((List<String>) s.getValue()[1]).isEmpty()) {
				List<String> cc = (List<String>)o[1];
	    		if(cc==null || cc.isEmpty()) {
	    			if(getHeader()!=null && !getHeader().isEmpty() && ((List<String>)s.getValue()[1]).containsAll(getHeader())
	    					|| getFooter()!=null && !getFooter().isEmpty() && ((List<String>) s.getValue()[1]).containsAll(getFooter()))continue;
	    			o[1]=(List<String>)s.getValue()[1];
	    			change = true;
	    		}
			}
		}
		}catch(Exception err) {}
		if(change)
			requireSave=true;
		return change;
	}

	public DataLoader getDataLoader() {
		return loader;
	}
}