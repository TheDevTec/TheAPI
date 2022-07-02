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
		return this.data;
	}

	@Override
	public Set<String> getKeys() {
		return this.data.keySet();
	}

	@Override
	public void set(String key, DataValue holder) {
		if (key == null)
			return;
		if (holder == null) {
			this.data.remove(key);
			return;
		}
		this.data.put(key, holder);
	}

	@Override
	public boolean remove(String key) {
		if (key == null)
			return false;
		return this.data.remove(key) != null;
	}

	@Override
	public void reset() {
		this.data.clear();
		this.header.clear();
		this.footer.clear();
		this.loaded = false;
	}

	@Override
	public void load(String input) {
		this.reset();
		this.loaded = true;
	}

	@Override
	public Collection<String> getHeader() {
		return this.header;
	}

	@Override
	public Collection<String> getFooter() {
		return this.footer;
	}

	@Override
	public boolean isLoaded() {
		return this.loaded;
	}
}
