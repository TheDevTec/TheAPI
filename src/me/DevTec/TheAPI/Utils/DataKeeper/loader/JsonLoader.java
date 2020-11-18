package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.Json.Reader;

public class JsonLoader implements DataLoader {
	private boolean l;
	private Map<String, DataHolder> data = new HashMap<>();

	@Override
	public Map<String, DataHolder> get() {
		return data;
	}

	public Collection<String> getKeys() {
		return data.keySet();
	}

	public void set(String key, DataHolder holder) {
		if (key == null)
			return;
		if (holder == null) {
			data.remove(key);
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
		if(input==null) {
			l=false;
			return;
		}
		data.clear();
		try {
			Object read = Reader.read(input);
			if(read instanceof Map) {
					for (Entry<Object, Object> keyed : ((Map<Object, Object>)read).entrySet()) {
						data.put((String)keyed.getKey(), new DataHolder(keyed.getValue()));
					}
					l = true;
			}else {
			for(Object o : (Collection<Object>)read) {
			for (Entry<Object, Object> keyed : ((Map<Object, Object>) o).entrySet()) {
				data.put((String)keyed.getKey(), new DataHolder(keyed.getValue()));
			}
			}
			l=true;
			}
		} catch (Exception er) {
			l = false;
		}
	}

	@Override
	public List<String> getHeader() {
		// NOT SUPPORTED
		return null;
	}

	@Override
	public List<String> getFooter() {
		// NOT SUPPORTED
		return null;
	}

	@Override
	public boolean loaded() {
		return l;
	}

	@Override
	public String getDataName() {
		return "Data(JsonLoader:" + data.size() + ")";
	}
}
