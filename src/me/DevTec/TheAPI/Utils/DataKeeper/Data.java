package me.DevTec.TheAPI.Utils.DataKeeper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.loader.DataLoader;
import me.DevTec.TheAPI.Utils.DataKeeper.loader.EmptyLoader;
import me.DevTec.TheAPI.Utils.Json.jsonmaker.Maker;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

public class Data implements me.DevTec.TheAPI.Utils.DataKeeper.Abstract.Data {
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
	private File a;
	public Data() {
	}
	
	public Data(String filePath) {
		this(filePath, true);
	}
	
	public Data(String filePath, boolean load) {
		a=new File(filePath);
		if (load)
		reload(a);
	}

	public Data(File f) {
		this(f,true);
	}
	
	public Data(File f, boolean load) {
		a=f;
		if(load)
		reload(a);
	}
	
	public boolean exists(String path) {
		int a = 0;
			for (String k : loader.get().keySet()) {
				if (k.startsWith(path)) {
					a=1;
					break;
				}}
		return a==1;
	}
	
	public void setFile(File f) {
		a=f;
	}
	
	private DataHolder getOrCreateData(String key) {
		if (key==null) return null;
		DataHolder h = loader.get().getOrDefault(key, null);
		if (h==null) {
			h = new DataHolder(null);
			
			if (!aw.contains(key.split("\\.")[0]))
				aw.add(key.split("\\.")[0]);
			
			loader.get().put(key, h);
		}
		return h;
	}
	
	public void set(String key, Object value) {
		if (key==null) return;
		if (value==null) {
			if (key.split("\\.").length<=1)
				aw.remove(key.split("\\.")[0]);
			
			loader.get().remove(key);
			return;
		}
		
		getOrCreateData(key).o=value;
	}
	
	public void remove(String key) {
		if (key==null) return;
		if (key.split("\\.").length<=1)
			aw.remove(key.split("\\.")[0]);
		loader.get().remove(key);
		for (String keys : getKeys(key))
			loader.get().remove(key+"."+keys);
	}

	public List<String> getLines(String key) {
		if (key==null)return null;
		return getOrCreateData(key).lines;
	}
	
	public void setLines(String key, List<String> value) {
		if (value==null||key==null)return;
		getOrCreateData(key).lines=value;
	}
	
	public void addLines(String key, List<String> value) {
		if (value==null||key==null)return;
		DataHolder h = getOrCreateData(key);
		if (h.lines==null)h.lines=value;
		else
		h.lines.addAll(value);
	}
	
	public void addLine(String key, String value) {
		if (value==null||key==null)return;
		DataHolder h = getOrCreateData(key);
		if (h.lines==null)h.lines=new ArrayList<>(3);
		h.lines.add(value);
	}
	
	public void removeLine(String key, String value) {
		if (value==null||key==null)return;
		DataHolder h = getOrCreateData(key);
		if (h.lines!=null)
		h.lines.remove(value);
	}
	
	public void removeLine(String key, int line) {
		if (line<=-1||key==null)
			return;
		DataHolder h = getOrCreateData(key);
		if (h.lines!=null)
		h.lines.remove(line);
	}
	
	public File getFile() {
		return a;
	}
	
	public void setHeader(List<String> lines) {
		loader.getHeader().clear();
		loader.getHeader().addAll(lines);
	}
	
	public void setFooter(List<String> lines) {
		loader.getFooter().clear();
		loader.getFooter().addAll(lines);
	}
	
	public List<String> getHeader() {
		return loader.getHeader();
	}
	
	public List<String> getFooter() {
		return loader.getFooter();
	}

	public void reload(String input) {
		aw.clear();
		loader = DataLoader.findLoaderFor(input); //get & load
		for (String k : loader.get().keySet())
			if (!aw.contains(k.split("\\.")[0]))
			aw.add(k.split("\\.")[0]);
	}
	
	public void reload(File f) {
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (Exception e) {
			}
		}
		aw.clear();
		loader = DataLoader.findLoaderFor(f); //get & load
		for (String k : loader.get().keySet())
			if (!aw.contains(k.split("\\.")[0]))
			aw.add(k.split("\\.")[0]);
	}
		
	@SuppressWarnings("unchecked")
	public <E> E getVariable(String key) {
		try {
			return (E)(loader.get().get(key).o);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Object get(String key) {
		try {
			return loader.get().get(key).o;
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getString(String key) {
		return get(key)!=null?String.valueOf(get(key)):null;
	}
	
	public int getInt(String key) {
		try {
		return (int)getVariable(key);
		} catch (Exception notNumber) {
			return StringUtils.getInt(getString(key));
		}
	}
	
	public double getDouble(String key) {
		try {
		return (double)getVariable(key);
		} catch (Exception notNumber) {
			return StringUtils.getDouble(getString(key));
		}
	}
	
	public long getLong(String key) {
		try {
		return (long)getVariable(key);
		} catch (Exception notNumber) {
			return StringUtils.getLong(getString(key));
		}
	}
	
	public float getFloat(String key) {
		try {
		return (float)getVariable(key);
		} catch (Exception notNumber) {
			return StringUtils.getFloat(getString(key));
		}
	}
	
	public byte getByte(String key) {
		try {
		return (byte)getVariable(key);
		} catch (Exception notNumber) {
			return StringUtils.getByte(getString(key));
		}
	}

	public boolean getBoolean(String key) {
		try {
		return (boolean)getVariable(key);
		} catch (Exception notNumber) {
			return StringUtils.getBoolean(getString(key));
		}
	}
	
	public short getShort(String key) {
		try {
		return (short)getVariable(key);
		} catch (Exception notNumber) {
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
		synchronized (loader) {
		try {
			FileWriter w = new FileWriter(a);
		    if (type==DataType.DATA||type==DataType.BYTE) {
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream(loader.get().size());
					GZIPOutputStream tos = new GZIPOutputStream(bos);
					BufferedOutputStream buf = new BufferedOutputStream(tos);
					DataOutputStream ous = new DataOutputStream(buf);
					for (Entry<String, DataHolder> key: loader.get().entrySet())
						try {
							Object o = key.getValue();
							ous.writeUTF(key.toString());
							ous.writeUTF(o==null?"null":Maker.objectToJson(o));
						} catch (Exception er) {}
					ous.flush();
					bos.flush();
					buf.flush();
					tos.finish();
					w.write(type==DataType.DATA?bos.toString():Base64.getEncoder().encodeToString(bos.toByteArray()));
					w.close();
				} catch (Exception e) {
					w.write("");
					w.close();
				}
				return;
			}
			if (type==DataType.JSON) {
				Maker maker = new Maker();
				for (String key : aw)
					addKeys(maker, key);
				w.write(maker.toString());
				w.close();
				return;
			}
			for (String h : loader.getHeader())
				w.write(h+System.lineSeparator());
			for (String key : aw)
				preparePath(key, key+":",0, w);
			for (String h : loader.getFooter())
				w.write(h+System.lineSeparator());
		    w.close();
		} catch (Exception er) {
			Validator.send("Saving Data to File", er);
		}
	}}
	
	public void save() {
		save(DataType.YAML);
	}

	public Set<String> getKeys() {
		return new HashSet<>(aw);
	}

	private List<String> aw = new ArrayList<>();
	public Set<String> getKeys(boolean subkeys) {
		if (subkeys)return loader.get().keySet();
		return new HashSet<>(aw);
	}

	public Set<String> getKeys(String key) {
		return getKeys(key, false);
	}
	
	public boolean isKey(String key) {
		boolean is = false;
		for (String k : loader.get().keySet()) {
			if (k.startsWith(key)) {
				String r = k.replaceFirst(key, "");
				if (r.startsWith(".")||r.trim().isEmpty()) {
					is=true;
					break;
				}
			}
		}

		return is;
	}
	
	public Set<String> getKeys(String key, boolean subkeys) {
		HashSet<String> a = new HashSet<>();
		for (String d : loader.get().keySet())
			if (d.startsWith(key)) {
				String c = d.replaceFirst(key, "").replaceFirst("\\.", "");
				if (c.trim().isEmpty())continue;
				c = subkeys ? c : c.split("\\.")[0];
				if (!a.contains(c))a.add(c);
			}
		return a;
	}

	public String toString() {
		return toString(DataType.DATA);
	}
	
	private void addKeys(Maker main, String key) {
		Object o = get(key);
		if (o!=null)
			main.add(main.create().put(key, Maker.objectToJson(o)));
		for (String keyer : getKeys(key))
			addKeys(main, key+"."+keyer);
	}
	
	private void preparePath(String path, String pathName, int spaces, Writer b) {
		synchronized (loader) {
		try {
		Object o = getOrCreateData(path).o;
		String space = cs(spaces,1);
		pathName=space+pathName;
		for (String s : getOrCreateData(path).lines)
			b.write(space+s+System.lineSeparator());
		if (o==null) {
			b.write(pathName+System.lineSeparator());
		} else {
			if (o instanceof Map<?,?>) {
				b.write(pathName+System.lineSeparator());
				String splitted = space+"- ";
				for (Entry<?,?> a : ((Map<?,?>)o).entrySet())
					b.write(splitted+addQuotes(true, Maker.objectToJson(a.getKey())+"="+Maker.objectToJson(a.getValue()))+System.lineSeparator());
			}
			if (o instanceof List || o instanceof Object[]) {
				b.write(pathName+System.lineSeparator());
				String splitted = space+"- ";
				for (Object a : (List<?>)o)
					b.write(splitted+addQuotes(a instanceof String, Maker.objectToJson(a))+System.lineSeparator());
			} else
				b.write(pathName+" "+addQuotes(o instanceof String, Maker.objectToJson(o))+System.lineSeparator());
		}
		for (String key : getKeys(path, false))
			preparePath(path+"."+key, key+":", spaces+1, b);
		} catch (Exception er) {}
	}}
	
	public String toString(DataType type) {
		synchronized (loader) {
		if (type == DataType.DATA || type == DataType.BYTE) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream(loader.get().size());
				GZIPOutputStream tos = new GZIPOutputStream(bos);
				BufferedOutputStream buf = new BufferedOutputStream(tos);
				DataOutputStream ous = new DataOutputStream(buf);
				for (Entry<String, DataHolder> key: loader.get().entrySet())
					try {
						Object o = key.getValue();
						ous.writeUTF(key.toString());
						ous.writeUTF(o==null?"null":Maker.objectToJson(o));
					} catch (Exception er) {}
				ous.flush();
				bos.flush();
				buf.flush();
				tos.finish();

				return type == DataType.DATA ? bos.toString() : Base64.getEncoder().encodeToString(bos.toByteArray());
			} catch (Exception e) {}

			return "";
		}
		if (type == DataType.JSON) {
			Maker main = new Maker();
			for (String key : aw)
				addKeys(main, key);
			return main.toString();
		}

		StringWriter d = new StringWriter();
		for (String h : loader.getHeader())
			d.write(h+System.lineSeparator());
		for (String key : aw)
			preparePath(key, key+":",0, d);
		for (String h : loader.getFooter())
			d.write(h+System.lineSeparator());
		return d.toString();
	}}
	
	/**
	 *	Wraps string in double quotes, unless the string is already in double
	 *	or single quotes. 
	 *
	 *	New-line characters are removed from the returned string.
	 *
	 *	Example input -> output:
	 *	 : 5 -> "5"
	 *	 : '5' -> '5'
	 *	 : "5" -> "5"
	 *   : 5" -> "5""
	 */
	private String addQuotes(boolean raw, String text) {
		if (text == null) return null;
		
		boolean quotedString = (text.startsWith("'") && text.endsWith("'")) || (text.startsWith("\"") && text.endsWith("\""));
		if (raw && quotedString) {
			return ("\"" + text + "\"").replace(System.lineSeparator(), "");
		}
		
		return text.replace(System.lineSeparator(), "");
	}
	
	private static String cs(int s, int doubleSpace) {
		StringWriter i = new StringWriter();
		String space = doubleSpace==1?"  ":" ";
		for (int c = 0; c < s; ++c)
			i.write(space);
		return i.toString();
	}

	@Override
	public String getDataName() {
		return "Data("+(a!=null?"'"+a.getName()+"'":"")+")";
	}
}