package me.devtec.theapi.utils.datakeeper.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.devtec.theapi.utils.json.Json;

public class JsonLoader extends DataLoader {
	private boolean l;
	private final Map<String, Object[]> data = new LinkedHashMap<>();

	@Override
	public Map<String, Object[]> get() {
		return data;
	}

	public Set<String> getKeys() {
		return data.keySet();
	}

	public void set(String key, Object[] holder) {
		if (key == null)
			return;
		if (holder == null) {
			remove(key);
			return;
		}
		data.put(key, holder);
	}

	public void remove(String key) {
		if (key == null)
			return;
		data.remove(key);
	}

	public void reset() {
		data.clear();
		l=false;
	}
	
	public void load(File file) {
		reset();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8), 8192);
			StringBuilder d = new StringBuilder(128);
			String s;
			while((s=r.readLine())!=null)d.append(s);
			r.close();
			load(d.toString());
		} catch (Exception e) {
			reset();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void load(String input) {
		if (input == null) {
			l = false;
			return;
		}
		reset();
		try {
			Object read = Json.reader().read(input);
			if (read instanceof Map) {
				for (Entry<Object, Object> keyed : ((Map<Object, Object>) read).entrySet()) {
					data.put((String) keyed.getKey(), new Object[] {Json.reader().read(keyed.getValue().toString()), null, null});
				}
			} else {
				for (Object o : (Collection<Object>) read) {
					for (Entry<Object, Object> keyed : ((Map<Object, Object>) o).entrySet()) {
						data.put((String) keyed.getKey(), new Object[] {Json.reader().read(keyed.getValue().toString()), null, null});
					}
				}
			}
			l = true;
		} catch (Exception er) {
			reset();
		}
	}

	@Override
	public Collection<String> getHeader() {
		// NOT SUPPORTED
		return null;
	}

	@Override
	public Collection<String> getFooter() {
		// NOT SUPPORTED
		return null;
	}

	@Override
	public boolean isLoaded() {
		return l;
	}
}
