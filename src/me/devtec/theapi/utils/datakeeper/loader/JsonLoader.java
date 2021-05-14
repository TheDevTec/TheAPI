package me.devtec.theapi.utils.datakeeper.loader;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.devtec.theapi.utils.json.Reader;

public class JsonLoader extends DataLoader {
	private boolean l;
	private Map<String, Object[]> data = new LinkedHashMap<>();

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
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void load(String input) {
		if (input == null) {
			l = false;
			return;
		}
		data.clear();
		try {
			Object read = Reader.read(input);
			if (read instanceof Map) {
				for (Entry<Object, Object> keyed : ((Map<Object, Object>) read).entrySet()) {
					data.put((String) keyed.getKey(), new Object[] {keyed.getValue(), null, null});
				}
				l = true;
			} else {
				for (Object o : (Collection<Object>) read) {
					for (Entry<Object, Object> keyed : ((Map<Object, Object>) o).entrySet()) {
						data.put((String) keyed.getKey(), new Object[] {keyed.getValue(), null, null});
					}
				}
				l = true;
			}
		} catch (Exception er) {
			l = false;
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
