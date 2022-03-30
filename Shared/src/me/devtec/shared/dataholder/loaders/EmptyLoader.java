package me.devtec.shared.dataholder.loaders;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmptyLoader extends DataLoader {
	protected final Map<String, Object[]> data = new LinkedHashMap<>();
	protected final List<String> header = new LinkedList<>();
	protected final List<String> footer = new LinkedList<>();
	protected boolean loaded = true;
	
	public boolean loadingFromFile() {
		return false;
	}
	
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
		header.clear();
		footer.clear();
	}

	@Override
	public void load(String input) {
		reset();
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
