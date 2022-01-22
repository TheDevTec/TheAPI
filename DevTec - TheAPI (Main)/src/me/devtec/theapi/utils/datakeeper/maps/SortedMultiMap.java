package me.devtec.theapi.utils.datakeeper.maps;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

public class SortedMultiMap<K, T, V> extends MultiMap<K, T, V> {

	public SortedMultiMap() {
		data = new LinkedHashMap<>();
	}

	public SortedMultiMap(MultiMap<K, T, V> map) {
		this();
		putAll(map);
	}
	
	public V put(K key, T thread, V value) {
		Map<T, V> map = data.get(key);
		if (map == null)
			data.put(key, map = new TreeMap<>());
		map.put(thread, value);
		return value;
	}

	public Collection<Entry<K, T, V>> entrySet() {
		LinkedHashSet<Entry<K, T, V>> entries = new LinkedHashSet<>(keySet().size());
		for (K key : keySet())
			for (T thread : threadSet(key))
				entries.add(new Entry<>(key, thread, get(key, thread)));
		return entries;
	}
}
