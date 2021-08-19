package me.devtec.theapi.utils.datakeeper.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;

import me.devtec.theapi.utils.json.Writer;

public class SortedMultiMap<K, T, V> extends MultiMap<K, T, V> {
	private final Map<K, Map<T, V>> data = new LinkedHashMap<>();

	public SortedMultiMap() {
	}

	public SortedMultiMap(MultiMap<K, T, V> map) {
		putAll(map);
	}

	public int size() {
		return data.size();
	}

	public void clear() {
		data.clear();
	}

	public void remove(K key, T thread) {
		data.get(key).remove(thread);
		if (data.get(key).isEmpty())
			data.remove(key);
	}

	public void remove(K key) {
		data.remove(key);
	}

	public V get(K key, T thread) {
		try {
			Map<T, V> t = data.get(key);
			return t.get(thread);
		} catch (Exception er) {
			return null;
		}
	}
	
	public V getOrDefault(K key, T thread, V def) {
		return containsThread(key,thread)?get(key, thread):def;
	}

	public boolean containsKey(K key) {
		return data.containsKey(key);
	}

	public boolean containsThread(K key, T thread) {
		return containsKey(key) && data.get(key).containsKey(thread);
	}

	public boolean containsValue(K key, V value) {
		return containsKey(key) && data.get(key).containsValue(value);
	}

	public V replace(K key, T thread, V value) {
		return put(key, thread, value);
	}

	public V put(K key, T thread, V value) {
		Map<T, V> map = data.get(key);
		if (map == null)
			data.put(key, map = new TreeMap<>());
		map.put(thread, value);
		return value;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public void putAll(MultiMap<K, T, V> map) {
		map.entrySet().forEach(Entry -> put(Entry.getKey(), Entry.getThread(), Entry.getValue()));
	}

	public Collection<K> keySet() {
		return new ArrayList<>(data.keySet());
	}

	public Collection<T> threadSet(K key) {
		return data.containsKey(key) ? new ArrayList<>(data.get(key).keySet()) : new ArrayList<>();
	}

	public Collection<V> values(K key, T thread) {
		return data.containsKey(key) ? new ArrayList<>(data.get(key).values()) : new ArrayList<>();
	}

	public Collection<Entry<K, T, V>> entrySet() {
		LinkedHashSet<Entry<K, T, V>> entries = new LinkedHashSet<>(keySet().size());
		for (K key : keySet())
			for (T thread : threadSet(key))
				entries.add(new Entry<>(key, thread, get(key, thread)));
		return entries;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		for (Entry<K, T, V> e : entrySet()) {
			builder.append((builder.length() == 0) ? "" : ", ").append('(').append(e.toString()).append(')');
		}
		return builder.append('}').toString();
	}

	@Override
	public String getDataName() {
		Map<String, Object> ser = new HashMap<>();
		ser.put("name", "MultiMap");
		ser.put("sorted", true);
		ser.put("size", size());
		ser.put("values", toString());
		return Writer.write(ser);
	}
}
