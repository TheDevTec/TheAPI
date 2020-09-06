package me.DevTec.TheAPI.Utils.DataKeeper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.json.simple.parser.JSONParser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.Lists.TheArrays;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap;
import me.DevTec.TheAPI.Utils.File.Reader;
import me.DevTec.TheAPI.Utils.File.Writer;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class Data {
	public static class DataHolder {
		private Object o;
		private List<String> lines = Lists.newArrayList();
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

	private MultiMap<String, String, DataHolder> map = TheArrays.newMultiMap();
	private List<String> header = Lists.newArrayList(), footer= Lists.newArrayList();
	private File a;
	public Data() {
	}
	
	public Data(String filePath, boolean load) {
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
		boolean a = false;
		for(String dd : map.keySet())
			for(String k : map.threadSet(dd))
				if(k.startsWith(path)) {
					a=true;
					break;
				}
		return a;
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

	public DataHolder getData(String key) {
		if(key==null)return null;
		return map.containsThread(key.split("\\.")[0],key)?map.get(key.split("\\.")[0],key):null;
	}
	
	public DataHolder getOrCreateData(String key) {
		if(key==null)return null;
		DataHolder h = getData(key);
		if(h==null) {
			h = new DataHolder(null, Lists.newArrayList());
			map.put(key.split("\\.")[0], key, h);
		}
		return h;
	}
	
	public void set(String key, Object value) {
		if(key==null)return;
		if(value==null) {
			if(map.containsThread(key.split("\\.")[0], key))
			map.remove(key.split("\\.")[0], key);
			return;
		}
		DataHolder h = getOrCreateData(key);
		h.setValue(value instanceof String ? ((String)value).replace(System.lineSeparator(), "") : value);
		map.put(key.split("\\.")[0], key, h);
	}
	
	public void remove(String key) {
		if(key==null)return;
		if(map.containsThread(key.split("\\.")[0], key))
		map.remove(key.split("\\.")[0], key);
	}

	public List<String> getLines(String key) {
		if(key==null)return Lists.newArrayList();
		return getOrCreateData(key).lines;
	}
	
	public void setLines(String key, List<String> value) {
		if(value==null||key==null)return;
		DataHolder h = getOrCreateData(key);
		h.lines=value;
		map.put(key.split("\\.")[0], key, h);
	}
	
	public void addLine(String key, String value) {
		if(value==null||key==null)
			return;
		DataHolder h = getOrCreateData(key);
		h.lines.add(value);
		map.put(key.split("\\.")[0], key, h);
	}
	
	public void removeLine(String key, String value) {
		if(value==null||key==null)
			return;
		DataHolder h = getOrCreateData(key);
		h.lines.remove(value);
		map.put(key.split("\\.")[0], key, h);
	}
	
	public void removeLine(String key, int line) {
		if(line<=-1||key==null)
			return;
		DataHolder h = getOrCreateData(key);
		h.lines.remove(line);
		map.put(key.split("\\.")[0], key, h);
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
		map.clear();
		try {
			byte[] bb = Base64.getDecoder().decode(input);
			ByteArrayInputStream bos = new ByteArrayInputStream(bb);
			GZIPInputStream zos = new GZIPInputStream(bos);
			ObjectInputStream ous = new ObjectInputStream(zos);
			while(true)
				try {
					String key = ous.readUTF();
					set(key, ous.readObject());
				}catch(Exception e) {
				break;
				}
			bos.close();
			zos.close();
			ous.close();
		}catch(Exception e) {
		if(input.trim().isEmpty())return;
		List<Object> items = Lists.newArrayList();
		List<String> lines = Lists.newArrayList();
		String key = "";
		StringBuffer v = new StringBuffer();
		int last = 0, f=0, c = 0;
		for(String text : input.split(System.lineSeparator())) {
			if(text.trim().startsWith("#")||c==0 && text.trim().isEmpty()) {
				if(c!=0) {
					if(c==1) {
						set(key, readObject(v.toString()));
						v=new StringBuffer();
						}
						if(c==2) {
							set(key, items);
							setLines(key, lines);
							items=Lists.newArrayList();
							lines=Lists.newArrayList();
						}
						c=0;
				}
				if(f==0)
					header.add(text.replaceFirst(cd(c(text)), ""));
				else
				lines.add(text.replaceFirst(cd(c(text)), ""));
				continue;
			}
			if(c!=0 && text.contains(":") && text.matches("([A-Za-z0-9]|[^A-Za-z0-9])+:*")) {
				if(c==1) {
				set(key, readObject(v.toString()));
				setLines(key, lines);
				lines=Lists.newArrayList();
				v=new StringBuffer();
				}
				if(c==2) {
					set(key, items);
					setLines(key, lines);
					items=Lists.newArrayList();
					lines=Lists.newArrayList();
				}
				c=0;
			}
			if(c==2 || text.replaceFirst(cd(c(text)), "").startsWith("- ") && !key.equals("")) {
				items.add(readObject(c!=2?text.replaceFirst(text.split("- ")[0]+"- ", ""):text.replaceFirst(cd(c(text)),"")));
			}else {
				if(!items.isEmpty()) {
					set(key, items);
					setLines(key, lines);
					items=Lists.newArrayList();
					lines=Lists.newArrayList();
				}
				if(c==1) {
					v.append(text.replaceFirst(cd(c(text)),""));
					continue;
				}
				if(c(text.split(":")[0]) <= last) {
					if(!text.startsWith(" "))key="";
					if(c(text.split(":")[0]) == last) {
						String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
						int remove = key.length()-lastr.length();
						if(remove > 0)
						key=key.substring(0, remove);
					}else {
					for(int i = 0; i < Math.abs(last-c(text.split(":")[0])); ++i) {
					String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
					int remove = key.length()-lastr.length();
					if(remove < 0)break;
					key=key.substring(0, remove);
				}}}
				key+=(key.equals("")?"":".")+text.split(":")[0].trim();
				f=1;
				last=c(text.split(":")[0]);
				if(!text.replaceFirst(text.split(":")[0]+":", "").trim().isEmpty()) {
					if(text.replaceFirst(text.split(":")[0]+": ", "").trim().equals("|")) {
						c=1;
						continue;
					}
					if(text.replaceFirst(text.split(":")[0]+": ", "").trim().equals("|-")) {
						c=2;
						continue;
					}
					set(key, readObject(text.replaceFirst(text.split(":")[0]+": ", "")));
					if(!lines.isEmpty()) {
						setLines(key, lines);
						lines=Lists.newArrayList();
					}
				}
			}
		}
		if(!items.isEmpty()||c==2) {
			set(key, items);
			items=Lists.newArrayList();
		}
		if(!lines.isEmpty())
			footer.addAll(lines);
		if(c==1)
			set(key, readObject(v.toString()));
	}
	}
	
	public void reload(File a) {
		map.clear();
		try {
			byte[] bb = Base64.getDecoder().decode(Reader.read(a, false));
			ByteArrayInputStream bos = new ByteArrayInputStream(bb);
			GZIPInputStream zos = new GZIPInputStream(bos);
			ObjectInputStream ous = new ObjectInputStream(zos);
			while(true)
				try {
					String key = ous.readUTF();
					set(key, ous.readObject());
				}catch(Exception e) {
				break;
				}
			bos.close();
			zos.close();
			ous.close();
		}catch(Exception e) {
		String input = Reader.read(a, true);
		if(input.trim().isEmpty())return;
		List<Object> items = Lists.newArrayList();
		List<String> lines = Lists.newArrayList();
		String key = "";
		StringBuffer v = new StringBuffer();
		int last = 0, f=0, c = 0;
		for(String text : input.split(System.lineSeparator())) {
			if(text.trim().startsWith("#")||c==0 && text.trim().isEmpty()) {
				if(c!=0) {
					if(c==1) {
						set(key, readObject(v.toString()));
						v=new StringBuffer();
						}
						if(c==2) {
							set(key, items);
							setLines(key, lines);
							items=Lists.newArrayList();
							lines=Lists.newArrayList();
						}
						c=0;
				}
				if(f==0)
					header.add(text.replaceFirst(cd(c(text)), ""));
				else
				lines.add(text.replaceFirst(cd(c(text)), ""));
				continue;
			}
			if(c!=0 && text.contains(":") && text.matches("([A-Za-z0-9]|[^A-Za-z0-9])+:*")) {
				if(c==1) {
				set(key, readObject(v.toString()));
				setLines(key, lines);
				lines=Lists.newArrayList();
				v=new StringBuffer();
				}
				if(c==2) {
					set(key, items);
					setLines(key, lines);
					items=Lists.newArrayList();
					lines=Lists.newArrayList();
				}
				c=0;
			}
			if(c==2 || text.replaceFirst(cd(c(text)), "").startsWith("- ") && !key.equals("")) {
				items.add(readObject(c!=2?text.replaceFirst(text.split("- ")[0]+"- ", ""):text.replaceFirst(cd(c(text)),"")));
			}else {
				if(!items.isEmpty()) {
					set(key, items);
					setLines(key, lines);
					items=Lists.newArrayList();
					lines=Lists.newArrayList();
				}
				if(c==1) {
					v.append(text.replaceFirst(cd(c(text)),""));
					continue;
				}
				if(c(text.split(":")[0]) <= last) {
					if(!text.startsWith(" "))key="";
					if(c(text.split(":")[0]) == last) {
						String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
						int remove = key.length()-lastr.length();
						if(remove > 0)
						key=key.substring(0, remove);
					}else {
					for(int i = 0; i < Math.abs(last-c(text.split(":")[0])); ++i) {
					String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
					int remove = key.length()-lastr.length();
					if(remove < 0)break;
					key=key.substring(0, remove);
				}}}
				key+=(key.equals("")?"":".")+text.split(":")[0].trim();
				f=1;
				last=c(text.split(":")[0]);
				if(!text.replaceFirst(text.split(":")[0]+":", "").trim().isEmpty()) {
					if(text.replaceFirst(text.split(":")[0]+": ", "").trim().equals("|")) {
						c=1;
						continue;
					}
					if(text.replaceFirst(text.split(":")[0]+": ", "").trim().equals("|-")) {
						c=2;
						continue;
					}
					set(key, readObject(text.replaceFirst(text.split(":")[0]+": ", "")));
					if(!lines.isEmpty()) {
						setLines(key, lines);
						lines=Lists.newArrayList();
					}
				}
			}
		}
		if(!items.isEmpty()||c==2) {
			set(key, items);
			items=Lists.newArrayList();
		}
		if(!lines.isEmpty())
			footer.addAll(lines);
		if(c==1)
			set(key, readObject(v.toString()));
	}}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return getData(key)!=null?(T)getData(key).getValue():null;
	}
	
	public String getString(String key) {
		return get(key)!=null?get(key)+"":null;
	}
	
	public int getInt(String key) {
		try {
		return get(key);
		}catch(Exception notNumber) {
			return StringUtils.getInt(getString(key));
		}
	}
	
	public double getDouble(String key) {
		try {
		return get(key);
		}catch(Exception notNumber) {
			return StringUtils.getDouble(getString(key));
		}
	}
	
	public long getLong(String key) {
		try {
		return get(key);
		}catch(Exception notNumber) {
			return StringUtils.getLong(getString(key));
		}
	}
	
	public float getFloat(String key) {
		try {
		return get(key);
		}catch(Exception notNumber) {
			return StringUtils.getFloat(getString(key));
		}
	}
	
	public byte getByte(String key) {
		try {
		return get(key);
		}catch(Exception notNumber) {
			return StringUtils.getByte(getString(key));
		}
	}

	public boolean getBoolean(String key) {
		try {
		return get(key);
		}catch(Exception notNumber) {
			return StringUtils.getBoolean(getString(key));
		}
	}
	
	public short getShort(String key) {
		try {
		return get(key);
		}catch(Exception notNumber) {
			return StringUtils.getShort(getString(key));
		}
	}
	
	public <T> List<T> getList(String key) {
		return get(key)!=null && get(key) instanceof List<?> ? get(key) : Lists.newArrayList();
	}
	
	public List<String> getStringList(String key){
		return getList(key);
	}
	
	public void save(DataType type) {
		Writer w = new Writer(a);
		w.append(toString(type));
		w.flush();
		w.close();
	}
	
	public void save() {
		save(DataType.YAML);
	}

	public Set<String> getKeys() {
		return getKeys(false);
	}

	public Set<String> getKeys(boolean subkeys) {
		HashSet<String> a = Sets.newHashSet();
		for(String dd : map.keySet())
			for(String d : map.threadSet(dd))
				if(subkeys)
					a.add(d);
				else
					a.add(d.split("\\.")[0]);
		return a;
	}

	public Set<String> getKeys(String key) {
		return getKeys(key, false);
	}
	
	public Set<String> getKeys(String key, boolean subkeys) {
		HashSet<String> a = Sets.newHashSet();
		for(String dd : map.keySet())
			for(String d : map.threadSet(dd))
			if(d.startsWith(key) && !d.replaceFirst(d.split(key)[0], "").startsWith("."))
				if(subkeys)
					a.add(d.replaceFirst(key+"\\.", ""));
				else
				a.add((d.replaceFirst(key+"\\.", "")).split("\\.")[0]);
		return a;
	}

	public String toString() {
		return toString(DataType.YAML);
	}
	
	public String toString(DataType type) {
		if(type==DataType.DATA) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				GZIPOutputStream zos = new GZIPOutputStream(bos);
				ObjectOutputStream ous = new ObjectOutputStream(zos);
				for(String key : map.keySet()) {
					try {
						ous.writeUTF(key);
						ous.writeObject(get(key));
					}catch(Exception er) {}
				}
				zos.finish();
				return Base64.getEncoder().encodeToString(bos.toByteArray());
			}catch(Exception e) {}
			return Base64.getEncoder().encodeToString(new byte[0]);
		}else {
		StringBuffer d = new StringBuffer();
		List<String> created = Lists.newArrayList();
		for(String h : header)
			d.append(h+System.lineSeparator());
		for(String keyy : map.keySet())
			for(String key : map.threadSet(keyy)) {
			String keyr = "";
			int ir = 0;
			for(String k : key.split("\\.")) {
				keyr+=(keyr.equals("")?"":".")+k;
				if(!created.contains(keyr)) {
				created.add(keyr);
				if(!map.containsThread(keyr.split("\\.")[0],keyr) && get(keyr)==null) {
					d.append(cs(ir)+k+":"+System.lineSeparator());
				}else {
					Object o = get(keyr);
					for(String h : getLines(keyr))
						d.append(cs(ir)+h+System.lineSeparator());
					if(o instanceof List == false)
						d.append(cs(ir)+k+": "+addQuetos(o,o instanceof Comparable<?> ? o.toString() : writeObject(o))+System.lineSeparator());
					else {
						StringBuffer f = new StringBuffer();
							for(Object os : (List<?>)o)
							f.append(cs(ir)+"- "+addQuetos(os,os instanceof Comparable<?> ? os.toString() : writeObject(os))+System.lineSeparator());
						d.append(cs(ir)+k+":"+System.lineSeparator()+f.toString());
					}
				}
				}
				++ir;
			}
		}
		for(String h : footer)
			d.append(h+System.lineSeparator());
		return d.toString();
	}}

	private Object readObject(String o) {
		String obj = o+"";
		if(obj.startsWith("'") && obj.endsWith("'") && obj.length()>1)
			obj=obj.substring(1,obj.length()-1);
		else if(obj.startsWith("\"") && obj.endsWith("\"") && obj.length()>1)
		obj=obj.substring(1,obj.length()-1); 
		try {
		return g.fromJson(obj.replaceFirst(Pattern.quote(obj.split(":")[0])+":", ""), Ref.getClass(obj.split(":")[0]));
		}catch(Exception er) {
				try {
					if(obj.equalsIgnoreCase("yes")||obj.equalsIgnoreCase("on"))
						obj="true";
					if(obj.equalsIgnoreCase("no")||obj.equalsIgnoreCase("off"))
						obj="false";
					return parser.parse(obj);
				} catch (Exception e) {
				}
			return obj;
		}
	}
	
	private String writeObject(Object obj) {
		if(obj==null)return null;
		return obj.getClass().getName()+":"+g.toJson(obj);
	}
	
	private String addQuetos(Object s, String text) {
		if(!(text.startsWith("'") && text.endsWith("'") && text.startsWith("\"") && text.endsWith("\""))) {
			if(s instanceof String)
				return "\""+text+"\"";
			return text;
		}
		return text;
	}
    
	private static JSONParser parser = new JSONParser();
	private static Gson g = new GsonBuilder().create();

	private static String cs(int s) {
		String i = "";
		for(int c = 0; c < s; ++c)
			i+="  ";
		return i;
	}

	private static String cd(int s) {
		String i = "";
		for(int c = 0; c < s; ++c)
			i+=" ";
		return i;
	}
	
	private static int c(String s) {
		int i = 0;
		for(char c : s.toCharArray())
			if(c==' ')++i;
			else break;
		return i;
	}
}