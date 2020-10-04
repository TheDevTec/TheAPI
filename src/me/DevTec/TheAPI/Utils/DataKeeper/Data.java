package me.DevTec.TheAPI.Utils.DataKeeper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheList;
import me.DevTec.TheAPI.Utils.DataKeeper.Lists.TheArrayList;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap.Entry;
import me.DevTec.TheAPI.Utils.DataKeeper.loader.DataLoader;
import me.DevTec.TheAPI.Utils.DataKeeper.loader.EmptyLoader;
import me.DevTec.TheAPI.Utils.Json.jsonmaker.Maker;
import me.DevTec.TheAPI.Utils.Json.jsonmaker.Maker.MakerObject;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

public class Data implements me.DevTec.TheAPI.Utils.DataKeeper.Abstract.Data, Iterable<java.util.Map.Entry<String, Object>> {
	private static final long serialVersionUID = 1L;

	public static class DataHolder {
		private Object o;
		private List<String> lines = new ArrayList<>(0);
		public DataHolder(Object object) {
			o=object;
		}
		public DataHolder(Object object, List<String> unusedLines) {
			o=object;
			lines=unusedLines;
		}
		
		public Object getValue() {
			return o;
		}
		
		public void setValue(Object o) {
			this.o=o;
		}
		
		public List<String> getLines() {
			return lines;
		}
	}
	
	private DataLoader loader = new EmptyLoader();
	private List<String> header, footer;
	private File a;
	public Data() {
	}
	
	public Data(String filePath, boolean load) {
		this();
		File f = new File(filePath);
		if(!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (Exception e) {
			}
		}
		a=f;
		if(load)
		reload(a);
	}
	
	public boolean exists(String path) {
		int a = 0;
		if(loader.get().containsKey(path.split("\\.")[0]))
			for(String ka : loader.get().keySet())
				for(String k : loader.get().threadSet(ka))
				if(k.startsWith(path)) {
					a=1;
					break;
				}
		return a==1;
	}

	public Data(File f) {
		this(f,true);
	}
	
	public Data(File f, boolean load) {
		a=f;
		if(load)
			reload(a);
	}
	
	public void setFile(File f) {
		a=f;
	}

	private DataHolder getData(String key) {
		if(key==null)return null;
		return loader.get().containsThread(key.split("\\.")[0], key)?loader.get().get(key.split("\\.")[0], key):null;
	}
	
	private DataHolder getOrCreateData(String key) {
		if(key==null)return null;
		DataHolder h = getData(key);
		if(h==null) {
			h = new DataHolder(null);
			loader.get().put(key.split("\\.")[0], key, h);
		}
		return h;
	}
	
	public void set(String key, Object value) {
		if(key==null)return;
		if(value==null) {
			if(loader.get().containsThread(key.split("\\.")[0], key))
				loader.get().remove(key.split("\\.")[0], key);
			return;
		}
		getOrCreateData(key).o=value;
	}
	
	public void remove(String key) {
		if(key==null)return;
		if(loader.get().containsThread(key.split("\\.")[0], key))
			loader.get().remove(key.split("\\.")[0], key);
	}

	public List<String> getLines(String key) {
		if(key==null)return null;
		return getOrCreateData(key).lines;
	}
	
	public void setLines(String key, List<String> value) {
		if(value==null||key==null)return;
		getOrCreateData(key).lines=value;
	}
	
	public void addLines(String key, List<String> value) {
		if(value==null||key==null)return;
		DataHolder h = getOrCreateData(key);
		if(h.lines==null)h.lines=value;
		else
		h.lines.addAll(value);
	}
	
	public void addLine(String key, String value) {
		if(value==null||key==null)return;
		DataHolder h = getOrCreateData(key);
		if(h.lines==null)h.lines=new ArrayList<>(3);
		h.lines.add(value);
	}
	
	public void removeLine(String key, String value) {
		if(value==null||key==null)return;
		DataHolder h = getOrCreateData(key);
		if(h.lines!=null)
		h.lines.remove(value);
	}
	
	public void removeLine(String key, int line) {
		if(line<=-1||key==null)
			return;
		DataHolder h = getOrCreateData(key);
		if(h.lines!=null)
		h.lines.remove(line);
	}
	
	public File getFile() {
		return a;
	}
	
	public void setHeader(List<String> lines) {
		header=lines;
	}
	
	public void setFooter(List<String> lines) {
		footer=lines;
	}
	
	public List<String> getHeader() {
		return header;
	}
	
	public List<String> getFooter() {
		return footer;
	}

	public void reload(String input) {
		loader = DataLoader.findLoaderFor(input); //get & load
	}
	
	public void reload(File a) {
		loader = DataLoader.findLoaderFor(a); //get & load
	}
		
	@SuppressWarnings("unchecked")
	public <E> E getVariable(String key) {
		try {
			return (E)(getData(key).o);
		}catch(Exception e) {
			return null;
		}
	}
	
	public Object get(String key) {
		try {
			return getData(key).o;
		}catch(Exception e) {
			return null;
		}
	}
	
	public String getString(String key) {
		return get(key)!=null?String.valueOf(get(key)):null;
	}
	
	public int getInt(String key) {
		try {
		return getVariable(key);
		}catch(Exception notNumber) {
			return StringUtils.getInt(getString(key));
		}
	}
	
	public double getDouble(String key) {
		try {
		return getVariable(key);
		}catch(Exception notNumber) {
			return StringUtils.getDouble(getString(key));
		}
	}
	
	public long getLong(String key) {
		try {
		return getVariable(key);
		}catch(Exception notNumber) {
			return StringUtils.getLong(getString(key));
		}
	}
	
	public float getFloat(String key) {
		try {
		return getVariable(key);
		}catch(Exception notNumber) {
			return StringUtils.getFloat(getString(key));
		}
	}
	
	public byte getByte(String key) {
		try {
		return getVariable(key);
		}catch(Exception notNumber) {
			return StringUtils.getByte(getString(key));
		}
	}

	public boolean getBoolean(String key) {
		try {
		return getVariable(key);
		}catch(Exception notNumber) {
			return StringUtils.getBoolean(getString(key));
		}
	}
	
	public short getShort(String key) {
		try {
		return getVariable(key);
		}catch(Exception notNumber) {
			return StringUtils.getShort(getString(key));
		}
	}
	
	public <E> List<E> getList(String key) {
		return get(key)!=null && get(key) instanceof List ? getVariable(key) :  new ArrayList<>(3);
	}
	
	public List<String> getStringList(String key){
		return getList(key);
	}
	
	public void save(DataType type) {
		Validator.validate(a==null, "File is null");
		try {
			Writer w = new OutputStreamWriter(new FileOutputStream(a), "UTF_8");
			if(type==DataType.DATA||type==DataType.BYTE) {
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream(getKeys().size());
					GZIPOutputStream tos = new GZIPOutputStream(bos);
					BufferedOutputStream buf = new BufferedOutputStream(tos);
					DataOutputStream ous = new DataOutputStream(buf);
					for(String keyThread : loader.get().keySet())
						for(String key : loader.get().threadSet(keyThread))
						try {
							Object o = get(key);
							ous.writeUTF(key);
							ous.writeUTF(o==null?"null":Maker.objectToJson(o));
						}catch(Exception er) {}
					ous.flush();
					bos.flush();
					buf.flush();
					tos.finish();
					w.write(type==DataType.DATA?bos.toString():Base64.getEncoder().encodeToString(bos.toByteArray()));
				}catch(Exception e) {}
				w.write("");
			}
			if(type==DataType.JSON) {
				Maker maker = new Maker();
				MakerObject main = maker.create();
				for(String key : getKeys())
					addKeys(main, key);
				w.write(maker.add(main).toString());
			}
			if(header!=null)
			for(String h : header)
				w.write(h+System.lineSeparator());
			for(String key : getKeys(false))
				preparePath(key, w);
			if(footer!=null)
			for(String h : footer)
				w.write(h+System.lineSeparator());
			w.close();
		}catch(Exception er) {}
	}
	
	public void save() {
		save(DataType.YAML);
	}

	public Set<String> getKeys() {
		return getKeys(false);
	}

	public Set<String> getKeys(boolean subkeys) {
		HashSet<String> a = new HashSet<>();
		for(String ka : loader.get().keySet())
			for(String d : loader.get().threadSet(ka))
				if(subkeys) {
					if(!a.contains(d)) 
					a.add(d);
				}else
					if(!a.contains(d.contains(".")?d.split("\\.")[0]:d))
					a.add(d.contains(".")?d.split("\\.")[0]:d);
		return a;
	}

	public Set<String> getKeys(String key) {
		return getKeys(key, false);
	}
	
	public boolean isKey(String key) {
		boolean is = false;
		for(String ka : loader.get().keySet())
			for(String k : loader.get().threadSet(ka)) {
			if(k.startsWith(key)) {
				String r = k.replaceFirst(key, "");
				if(r.startsWith(".")||r.trim().isEmpty()) {
					is=true;
					break;
				}
			}
		}
		return is;
	}
	
	public Set<String> getKeys(String key, boolean subkeys) {
		String keyw = key.endsWith(".")?key.substring(0, key.length()-1):key;
		HashSet<String> a = new HashSet<>();
		if(isKey(keyw))
		for(String ka : loader.get().keySet())
			for(String d : loader.get().threadSet(ka))
			if(d.startsWith(keyw)) {
				String c = d.replaceFirst(keyw, "");
				if(c.trim().isEmpty())continue;
				c=c.substring(1);
				if(subkeys) {
					if(!a.contains(c))
					a.add(c);
				}else
					if(!a.contains(c.split("\\.")[0]))
				a.add(c.split("\\.")[0]);
			}
		return a;
	}

	public String toString() {
		return toString(DataType.DATA);
	}
	
	private void addKeys(MakerObject main, String key) {
		Object o = get(key);
		if(o!=null)
			main.put(key, Maker.objectToJson(o));
		for(String keyer : getKeys(key))
			addKeys(main, key+"."+keyer);
	}
	
	private void preparePath(String path, Writer b) {
		try {
		Object o = get(path);
		String space = cs(path.split("\\.").length-1,1);
		String pathName = space+(path.split("\\.")[path.split("\\.").length-1])+":";
		for(String s : getLines(path))
			b.write(space+s+System.lineSeparator());
		if(o==null)
		b.write(pathName+System.lineSeparator());
		else {
			if(o instanceof List) {
				b.write(pathName+System.lineSeparator());
				for(Object a : (List<?>)o)
				b.write(space+"- "+addQuotes(a, Maker.objectToJson(a))+System.lineSeparator());
			}else
			b.write(pathName+" "+addQuotes(o, Maker.objectToJson(o))+System.lineSeparator());
		}
		for(String key : getKeys(path, false))
			preparePath(path+"."+key, b);
		}catch(Exception er) {}
	}
	
	public String toString(DataType type) {
		if(type==DataType.DATA||type==DataType.BYTE) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(getKeys().size());
				GZIPOutputStream tos = new GZIPOutputStream(bos);
				BufferedOutputStream buf = new BufferedOutputStream(tos);
				DataOutputStream ous = new DataOutputStream(buf);
				for(String keyThread : loader.get().keySet())
					for(String key : loader.get().threadSet(keyThread))
					try {
						Object o = get(key);
						ous.writeUTF(key);
						ous.writeUTF(o==null?"null":Maker.objectToJson(o));
					}catch(Exception er) {}
				ous.flush();
				bos.flush();
				buf.flush();
				tos.finish();
				return type==DataType.DATA?bos.toString():Base64.getEncoder().encodeToString(bos.toByteArray());
			}catch(Exception e) {}
			return "";
		}
		if(type==DataType.JSON) {
			Maker maker = new Maker();
			MakerObject main = maker.create();
			for(String key : getKeys())
				addKeys(main, key);
			return maker.add(main).toString();
		}
		StringWriter d = new StringWriter();
		if(header!=null)
		for(String h : header)
			d.write(h+System.lineSeparator());
		for(String key : getKeys(false))
			preparePath(key, d);
		if(footer!=null)
		for(String h : footer)
			d.write(h+System.lineSeparator());
		return d.toString();
	}
	
	private String addQuotes(Object s, String text) {
		if(text==null || s==null)return null;
		if(s instanceof String && !(text.startsWith("'") && text.endsWith("'") || text.startsWith("\"") && text.endsWith("\"")))
			return "\""+text+"\"".replace(System.lineSeparator(), "");
		return text.replace(System.lineSeparator(), "");
	}
	
	private static String cs(int s, int doubleSpace) {
		StringWriter i = new StringWriter();
		String space = doubleSpace==1?"  ":" ";
		for(int c = 0; c < s; ++c)
			i.write(space);
		return i.toString();
	}

	@Override
	public Iterator<java.util.Map.Entry<String, Object>> iterator() {
		return new Iterator<java.util.Map.Entry<String, Object>>() {
			TheList<Entry<String, String, DataHolder>> list = new TheArrayList<>(loader.get().entrySet());
			int i = 0;
			@Override
			public boolean hasNext() {
				return i<list.size()-1;
			}
			@Override
			public java.util.Map.Entry<String, Object> next() {
				Entry<String, String, DataHolder> a = list.get(i++);
				return new java.util.Map.Entry<String, Object>() {
					Object o = a.getValue().getValue();
					@Override
					public String getKey() {
						return a.getThread();
					}
					@Override
					public Object getValue() {
						return o;
					}
					@Override
					public Object setValue(Object value) {
						Object old = o;
						o=value;
						return old;
					}
				};
			}
		};
	}

	@Override
	public String getDataName() {
		return "Data("+(a!=null?"'"+a.getName()+"'":"")+")";
	}
}