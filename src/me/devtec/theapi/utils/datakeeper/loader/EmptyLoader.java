package me.devtec.theapi.utils.datakeeper.loader;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmptyLoader extends DataLoader {
	private Map<String, Object[]> data = new LinkedHashMap<>();
	private List<String> header = new LinkedList<>(), footer = new LinkedList<>();
	
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
		while(!isReady());
		data.put(key, holder);
	}

	public void remove(String key) {
		if (key == null)
			return;
		while(!isReady());
		data.remove(key);
	}

	public void reset() {
		while(!isReady());
		data.clear();
		header.clear();
		footer.clear();
	}
	
	public boolean isReady() {
		return !paused;
	}
	
	private boolean paused = false;
	public void setReady(boolean val) {
		paused=val;
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
}
