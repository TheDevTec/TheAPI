package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.Json.Reader;

public class YamlLoader implements DataLoader {
	private static final Pattern pattern = Pattern.compile("[ ]*(['\"][^'\"]+['\"]|[^\"']?\\w+[^\"']?|.*):[ ]*(.*)");
	private Map<String, DataHolder> data = new HashMap<>();
	private Map<Integer, String> items = new HashMap<>();
	private boolean l;
	private int c;
	private List<String> header = new ArrayList<>(0), footer = new ArrayList<>(0);
	
	public Collection<String> getKeys() {
		return items.values();
	}
	
	public void set(String key, DataHolder holder) {
		if(key==null)return;
		if(holder==null) {
			remove(key);
			return;
		}
		if(data.containsKey(key)) {
			data.put(key, holder);
		}else {
			data.put(key, holder);
			items.put(c++, key);
		}
	}
	
	public void remove(String key) {
		if(key==null)return;
		data.remove(key);
		for(Entry<Integer, String> entry : new ArrayList<>(items.entrySet())) {
			if(entry.getValue().equals(key))
			items.remove(entry.getKey());
		}
	}
	
	public void reset() {
		data.clear();
		items.clear();
		header.clear();
		footer.clear();
		c=0;
	}
	
	@Override
	public Map<String, DataHolder> get() {
		return data;
	}
	
	@Override
	public void load(String input) {
		reset();
		try {
		List<Object> items = new ArrayList<>(0);
		List<String> lines = new ArrayList<>(0);
		String key = "";
		StringBuilder v = null;
		int last = 0, f = 0, c = 0;
		for(String text : input.split(System.lineSeparator())) {
			if(text.trim().startsWith("#") || text.trim().isEmpty()) {
				if(!items.isEmpty()) {
					set(key, items, lines);
					items=new ArrayList<>(0);
				}
				if(c!=0) {
					if(c==1) {
						String object = v.toString();
						if((object.startsWith("'")||object.startsWith("\"")) && (object.endsWith("'")||object.endsWith("\"")) && object.length()>1)
							object=object.substring(1, object.length()-1);
					set(key, Reader.object(object), lines);
						v=null;
						}else
						if(c==2) {
							set(key, items, lines);
							items=new ArrayList<>(1);
						}
						c=0;
				}
				if(f==0) {
					if(!input.equals(""))
					header.add(text.substring(c(text)));
				}else
				lines.add(text.substring(c(text)));
				continue;
			}
			Matcher sec = pattern.matcher(text);
			boolean find = sec.find();
			if(c!=0 && find) {
				if(c==1) {
					String object = v.toString();
					if((object.startsWith("'")||object.startsWith("\"")) && (object.endsWith("'")||object.endsWith("\"")) && object.length()>1)
						object=object.substring(1, object.length()-1);
				set(key, Reader.object(object), lines);
				v=null;
				}
				if(c==2) {
					set(key, items, lines);
					items=new ArrayList<>(1);
				}
				c=0;
			}
			if(c==2 || text.substring(c(text)).startsWith("- ") && !key.equals("")) {
				String object = c!=2?text.replaceFirst(text.split("- ")[0]+"- ", ""):text.substring(c(text));
				if((object.startsWith("'")||object.startsWith("\"")) && (object.endsWith("'")||object.endsWith("\"")) && object.length()>1)
					object=object.substring(1, object.length()-1);
				items.add(Reader.object(object));
				continue;
			}
			if(find) {
				if(!items.isEmpty()) {
					set(key, items, lines);
					items=new ArrayList<>(1);
				}
				if(c==1) {
					v.append(text.substring(c(text)));
					continue;
				}
				if(c(text) <= last) {
					if(!text.startsWith(" "))key="";
					if(c(text.split(":")[0]) == last) {
						String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
						int remove = key.length()-lastr.length();
						if(remove > 0)
						key=key.substring(0, remove);
					}else {
					for(int i = 0; i < Math.abs(last-c(text))/2+1; ++i) {
					String lastr = key.split("\\.")[key.split("\\.").length-1]+1;
					int remove = key.length()-lastr.length();
					if(remove < 0)break;
					key=key.substring(0, remove);
				}}}
				String split = sec.group(1);
				if((split.startsWith("'")||split.startsWith("\"")) && (split.endsWith("'")||split.endsWith("\"")) && split.length()>1)
					split=split.substring(1, split.length()-1);
				String object = null;
				try{
					object=sec.group(2);
					if((object.startsWith("'")||object.startsWith("\"")) && (object.endsWith("'")||object.endsWith("\"")) && object.length()>1)
						object=object.substring(1, object.length()-1);
				}catch(Exception er) {}
				if((key.startsWith("\"") && key.endsWith("\"")||key.startsWith("'") && key.endsWith("'")) && key.length()>1)key=key.substring(1, key.length()-1);
				key+=(key.equals("")?"":".")+split.trim();
				f=1;
				last=c(text);
				if(!object.isEmpty()) {
					if(object.trim().equals("|")) {
						c=1;
						v = new StringBuilder();
						continue;
					}
					if(object.trim().equals("|-")) {
						c=2;
						v = new StringBuilder();
						continue;
					}
					set(key, Reader.object(object), lines);
				}else
				if(lines.isEmpty()==false)
					set(key, null, lines);
			}}
		if(!items.isEmpty()||c==2) {
			set(key, items, lines);
		}else
		if(c==1) {
			String done = v.toString();
			if((done.startsWith("\"") && done.endsWith("\"")||done.startsWith("'") && done.endsWith("'")) && done.length()>1)done=done.substring(1, done.length()-1);
			set(key, Reader.object(done), lines);
		}else
		if(!lines.isEmpty())
			footer=lines;
		l=true;
		}catch(Exception er) {
			l=false;
		}
	}

	@Override
	public List<String> getHeader() {
		return header;
	}

	@Override
	public List<String> getFooter() {
		return footer;
	}
	
	private final int c(String s) {
		int i = 0;
		for(char c : s.toCharArray())
			if(c==' ')++i;
			else break;
		return i;
	}
	
	private final void set(String key, Object o, List<String> lines) {
		if((key.startsWith("\"") && key.endsWith("\"")||key.startsWith("'") && key.endsWith("'")) && key.length()>1)key=key.substring(1, key.length()-1);
		if(get().containsKey(key)) {
			get().get(key).setValue(o);
		}else
		set(key, new DataHolder(o, new ArrayList<>(lines)));
		lines.clear();
	}

	@Override
	public boolean loaded() {
		return l;
	}

	@Override
	public String getDataName() {
		return "Data(YamlLoader:"+data.size()+")";
	}
}
