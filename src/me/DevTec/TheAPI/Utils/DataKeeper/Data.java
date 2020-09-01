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

import me.DevTec.TheAPI.MultiHashMap.MultiMap;
import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.File.Reader;
import me.DevTec.TheAPI.Utils.File.Writer;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class Data extends MultiMap<Integer, String, DataHolder> {
	public static class DataHolder {
		private String key;
		private Object o;
		private List<String> lines = Lists.newArrayList();
		private int i;
		public DataHolder(String key, Object object, List<String> unusedLines) {
			this.key=key;
			o=object;
			lines=unusedLines;
		}
		
		public String getKey() {
			return key;
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
		for(Integer ii : keySet())
			for(String k : getThreads(ii))
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
		DataHolder f = null;
		for(int i : this.keySet()) {
			if(getThreads(i).get(0).equals(key)) {
				f=get(i, key);
				break;
			}
		}
		return f;
	}
	
	public DataHolder getOrCreateData(String key) {
		DataHolder h = getData(key);
		if(h==null) {
			h = new DataHolder(key, null, Lists.newArrayList());
			for(int i = 0; i > -1; ++i) {
				if(!keySet().contains(i)) {
					h.i=i;
					put(i, key, h);
					break;
				}
			}
		}
		return h;
	}
	
	public void set(String key, Object value) {
		if(value==null) {
			remove(key);
			return;
		}
		DataHolder h = getOrCreateData(key);
		h.setValue(value);
		put(h.i, key, h);
	}
	
	public void remove(String key) {
		for(int i : this.keySet()) {
			if(getThreads(i).get(0).equals(key)) {
				removeThread(i, key);
				break;
			}
		}
	}

	public List<String> getLines(String key) {
		if(key==null)return Lists.newArrayList();
		return getOrCreateData(key).lines;
	}
	
	public void setLines(String key, List<String> value) {
		if(value==null||key==null)
			return;
		DataHolder h = getOrCreateData(key);
		h.lines=value;
		put(h.i, key, h);
	}
	
	public void addLine(String key, String value) {
		if(value==null||key==null)
			return;
		DataHolder h = getOrCreateData(key);
		h.lines.add(value);
		put(h.i, key, h);
	}
	
	public void removeLine(String key, String value) {
		if(value==null||key==null)
			return;
		DataHolder h = getOrCreateData(key);
		h.lines.remove(value);
		put(h.i, key, h);
	}
	
	public void removeLine(String key, int line) {
		if(line<=-1||key==null)
			return;
		DataHolder h = getOrCreateData(key);
		h.lines.remove(line);
		put(h.i, key, h);
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
	
	@SuppressWarnings("unchecked")
	public void reload(File a) {
		clear();
		try {
			ByteArrayInputStream bos = new ByteArrayInputStream(Base64.getDecoder().decode(Reader.read(a, false)));
			GZIPInputStream zos = new GZIPInputStream(bos);
			ObjectInputStream ous = new ObjectInputStream(zos);
			try {
			header=(List<String>)ous.readObject();
			footer=(List<String>)ous.readObject();
			}catch(Exception e) {}
			while(true)
				try {
					String key = ous.readUTF();
					set(key, ous.readObject());
					try {
					setLines(key, (List<String>)ous.readObject());
					}catch(Exception er) {}
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
					if(c==1)
						set(key, readObject(v.toString()));
						if(c==2)
						set(key, items);
						c=0;
				}
				if(f==0)
					header.add(text.replaceFirst(cd(c(text)), ""));
				else
				lines.add(text.replaceFirst(cd(c(text)), ""));
				continue;
			}
			if(c!=0 && text.contains(":") && text.matches("([A-Za-z0-9]|[^A-Za-z0-9])+:*")) {
				if(c==1)
				set(key, readObject(v.toString()));
				if(c==2)
				set(key, items);
				c=0;
			}
			if(c==2 || text.replaceFirst(cd(c(text)), "").startsWith("- ") && !key.equals("")) {
				items.add(readObject(c!=2?text.replaceFirst(text.split("- ")[0]+"- ", ""):text.replaceFirst(cd(c(text)),"")));
			}else {
				if(!items.isEmpty()) {
					set(key, items);
					items=Lists.newArrayList();
				}
				if(c==1) {
					v.append(text.replaceFirst(cd(c(text)),""));
					continue;
				}
				if(c(text.split(":")[0]) < last) {
					if(!text.startsWith(" "))key="";
					else
					for(int i = 0; i < last-c(text.split(":")[0]); ++i) {
						String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
						int remove = key.length()-lastr.length();
						if(remove < 0)remove=key.length();
						key=key.substring(0, remove);
					}
				}else
					if(c(text.split(":")[0]) == last) {
						String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
						int remove = key.length()-lastr.length();
						if(remove < 0)remove=key.length();
						key=key.substring(0, remove);
					}
				key+=(key.equals("")?"":".")+text.split(":")[0].trim();
				f=1;
				last=c(text.split(":")[0]);
				if(!lines.isEmpty()) {
				getOrCreateData(key).lines=lines;
				lines=Lists.newArrayList();
				}
				if(!text.replaceFirst(text.split(":")[0]+":", "").trim().isEmpty()) {
					if(text.replaceFirst(text.split(":")[0]+": ", "").trim().equals("-")) {
						c=1;
						continue;
					}
					if(text.replaceFirst(text.split(":")[0]+": ", "").trim().equals("|-")) {
						c=2;
						continue;
					}
					set(key, readObject(text.replaceFirst(text.split(":")[0]+": ", "")));
				}
			}
		}
		if(!items.isEmpty()) {
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
		for(int i : keySet())
			if(getThreads(i).contains(key))
				return (T)get(i, key).getValue();
		return null;
	}
	
	public String getString(String key) {
		return get(key);
	}
	
	public int getInt(String key) {
		return get(key);
	}
	
	public double getDouble(String key) {
		return get(key);
	}
	
	public long getLong(String key) {
		return get(key);
	}
	
	public float getFloat(String key) {
		return get(key);
	}
	
	public byte getByte(String key) {
		return get(key);
	}

	public boolean getBoolean(String key) {
		return get(key);
	}
	
	public short getShort(String key) {
		return get(key);
	}
	
	public <T> List<T> getList(String key) {
		return get(key);
	}
	
	public List<String> getStringList(String key){
		return get(key);
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
		for(Integer ii : keySet())
			for(String d : getThreads(ii))
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
		for(Integer ii : keySet())
			for(String d : getThreads(ii))
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
				ous.writeObject(header);
				ous.writeObject(footer);
				for(Integer ii : keySet())
					for(String key : getThreads(ii)) {
					try {
						ous.writeUTF(key);
						ous.writeObject(get(key));
						try {
							ous.writeObject(getLines(key));
						}catch(Exception er) {
							ous.writeObject(Lists.newArrayList());
						}
					}catch(Exception er) {}
				}
				zos.finish();
				return Base64.getEncoder().encodeToString(bos.toByteArray());
			}catch(Exception e) {}
			return Base64.getEncoder().encodeToString(new byte[0]);
		}
		StringBuilder d = new StringBuilder();
		List<String> created = Lists.newArrayList();
		for(String h : header)
			d.append(h+System.lineSeparator());
		for(Integer ii : keySet()) {
			String keyr = "";
			int ir = -1;
			for(String k : getThreads(ii).get(0).split("\\.")) {
				++ir;
				keyr+=(keyr.equals("")?"":".")+k;
				if(created.contains(keyr))continue;
				if(get(keyr)==null) {
					created.add(keyr);
					d.append(cs(ir)+k+":"+System.lineSeparator());
				}else {
					created.add(keyr);
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
		}
		for(String h : footer)
			d.append(h+System.lineSeparator());
		return d.toString();
	}

	private Object readObject(String o) {
		String obj = o;
		if(obj.startsWith("'") && obj.endsWith("'"))
			obj=obj.substring(1,obj.length()-1);
		else if(obj.startsWith("\"") && obj.endsWith("\""))
		obj=obj.substring(1,obj.length()-1); 
		try {
		return g.fromJson(obj.replaceFirst(Pattern.quote(obj.split(":")[0])+":", ""), Ref.getClass(obj.split(":")[0]));
		}catch(Exception er) {
				try {
					if(obj.equals("yes")||obj.equals("on"))
						obj="true";
					if(obj.equals("no")||obj.equals("off"))
						obj="false";
					return parser.parse(obj);
				} catch (Exception e) {
				}
			return obj;
		}
	}
	
	private String writeObject(Object obj) {
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