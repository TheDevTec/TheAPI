package me.devtec.theapi.utils.datakeeper.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertiesLoader extends DataLoader {
	private final Pattern pattern = Pattern.compile("(.*?)=(.*)");
	private final Map<String, Object[]> map = new HashMap<>();
	private List<String> header=new LinkedList<>(), footer = new LinkedList<>();
	private boolean loaded;
	
	public void load(File file) {
		reset();
		List<String> comments = new LinkedList<>();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), 8192);
			String s;
			while((s=r.readLine())!=null) {
				String f = s.trim();
				if(!f.isEmpty() && !f.startsWith("#")) {
					Matcher m = pattern.matcher(s);
					if(m.find()) {
						map.put(m.group(1), new Object[] {m.group(2), comments.isEmpty()?null:new LinkedList<>(comments)});
						comments.clear();
						continue;
					}
				}
				comments.add(s.substring(YamlLoader.removeSpaces(s)));
			}
			r.close();
			if(!comments.isEmpty()) {
				if(map.isEmpty())header=comments;
				else
				footer=comments;
			}
			loaded=!map.isEmpty();
		} catch (Exception e) {
			reset();
		}
	}
	
	public void load(String d) {
		reset();
		List<String> comments = new LinkedList<>();
		for(String s : d.split(System.lineSeparator())) {
			String f = s.trim();
			if(!f.isEmpty() && !f.startsWith("#")) {
				Matcher m = pattern.matcher(s);
				if(m.find()) {
					map.put(m.group(1), new Object[] {m.group(2), comments.isEmpty()?null:new LinkedList<>(comments)});
					comments.clear();
					continue;
				}
				//comment
			}
			comments.add(s.substring(YamlLoader.removeSpaces(s)));
		}
		if(!comments.isEmpty()) {
			if(map.isEmpty())header=comments;
			else
			footer=comments;
		}
		loaded=!map.isEmpty();
	}

	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public Collection<String> getHeader() {
		return header;
	}

	@Override
	public Collection<String> getFooter() {
		return footer;
	}

	@Override
	public Map<String, Object[]> get() {
		return map;
	}

	@Override
	public void set(String key, Object[] value) {
		if(value==null)remove(key);
		else
		map.put(key, value);
	}

	@Override
	public void remove(String key) {
		map.remove(key);
	}

	@Override
	public Set<String> getKeys() {
		return map.keySet();
	}

	@Override
	public void reset() {
		map.clear();
		header.clear();
		footer.clear();
		loaded=false;
	}
}
