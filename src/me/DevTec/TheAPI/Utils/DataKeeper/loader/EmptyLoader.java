package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;

public class EmptyLoader implements DataLoader {
	private Map<String, DataHolder> data = new UnsortedMap<>();
	private List<String> header = new ArrayList<>(), footer = new ArrayList<>();

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
		data.clear();
		header.clear();
		footer.clear();
	}

	@Override
	public List<String> getHeader() {
		return header;
	}

	@Override
	public List<String> getFooter() {
		return footer;
	}

	@Override
	public boolean loaded() {
		return true;
	}

	@Override
	public String getDataName() {
		return "Data(EmptyLoader:" + data.size() + ")";
	}
}
