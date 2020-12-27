package me.devtec.theapi.utils.datakeeper.loader;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.devtec.theapi.utils.datakeeper.Data.DataHolder;
import me.devtec.theapi.utils.datakeeper.collections.UnsortedList;
import me.devtec.theapi.utils.datakeeper.maps.UnsortedMap;

public class EmptyLoader extends DataLoader {
	private Map<String, DataHolder> data = new UnsortedMap<>();
	private List<String> header = new UnsortedList<>(), footer = new UnsortedList<>();

	@Override
	public Map<String, DataHolder> get() {
		return data;
	}

	public Set<String> getKeys() {
		return data.keySet();
	}

	public void set(String key, DataHolder holder) {
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
		return true;
	}
	
	public String toString() {
		return getDataName();
	}

	@Override
	public String getDataName() {
		return "Data(EmptyLoader:" + data.size() + ")";
	}
}
