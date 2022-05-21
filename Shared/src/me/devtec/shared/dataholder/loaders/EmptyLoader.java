package me.devtec.shared.dataholder.loaders;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.devtec.shared.dataholder.loaders.constructor.DataValue;

public class EmptyLoader extends DataLoader {
	protected final Map<String, DataValue> data = new LinkedHashMap<>();
	protected final List<String> header = new LinkedList<>();
	protected final List<String> footer = new LinkedList<>();
	protected boolean loaded = true;

	@Override
	public boolean loadingFromFile() {
		return false;
	}

	@Override
	public Map<String, DataValue> get() {
		return data;
	}

	@Override
	public Set<String> getKeys() {
		return data.keySet();
	}

	@Override
	public void set(String key, DataValue holder) {
		if (key == null)
			return;
		if (holder == null) {
			data.remove(key);
			return;
		}
		data.put(key, holder);
	}

	@Override
	public void remove(String key) {
		if (key == null)
			return;
		data.remove(key);
	}

	@Override
	public void reset() {
		data.clear();
		header.clear();
		footer.clear();
		loaded = false;
	}

	@Override
	public void load(String input) {
		reset();
		loaded = true;
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
	public boolean isLoaded() {
		return loaded;
	}
}
