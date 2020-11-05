package me.DevTec.TheAPI.Utils.DataKeeper.Maps;

import java.util.HashMap;
import java.util.Map;

import me.DevTec.TheAPI.Utils.DataKeeper.TheArrays;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.Data;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheCollection;
import me.DevTec.TheAPI.Utils.DataKeeper.Lists.TheArrayList;

public class MultiMap<K, T, V> implements Data {
	private Map<K, Map<T, V>> data = new HashMap<>();

	public MultiMap() {
	}

	public MultiMap(MultiMap<K, T, V> map) {
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

	public boolean containsKey(K key) {
		return data.containsKey(key);
	}

	public boolean containsThread(K key, T thread) {
		if (containsKey(key))
			return data.get(key).containsKey(thread);
		return false;
	}

	public boolean containsValue(K key, V value) {
		if (containsKey(key))
			return data.get(key).containsValue(value);
		return false;
	}

	public V replace(K key, T thread, V value) {
		return put(key, thread, value);
	}

	public V put(K key, T thread, V value) {
		Map<T, V> map = data.getOrDefault(key, null);
		if (map == null) {
			map = new HashMap<>();
			data.put(key, map);
		}
		map.put(thread, value);
		return value;
	}

	public boolean isEmpty() {
		return data.isEmpty();
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
		V get = get(key, thread);
		return get == null ? def : get;
	}

	public void putAll(MultiMap<K, T, V> map) {
		map.entrySet().forEach(Entry -> put(Entry.k, Entry.t, Entry.v));
	}

	public TheCollection<K> keySet() {
		return new TheArrayList<>(data.keySet());
	}

	public TheCollection<T> threadSet(K key) {
		return data.containsKey(key) ? new TheArrayList<>(data.get(key).keySet()) : TheArrays.newList();
	}

	public TheCollection<V> values(K key, T thread) {
		return data.containsKey(key) ? new TheArrayList<>(data.get(key).values()) : TheArrays.newList();
	}

	public TheCollection<Entry<K, T, V>> entrySet() {
		TheArrayList<Entry<K, T, V>> entries = new TheArrayList<>(data.size());
		for (K key : keySet())
			for (T thread : threadSet(key))
				entries.add(new Entry<>(key, thread, get(key, thread)));
		return entries;
	}

	public String toString() {
		String builder = "";
		for (Entry<K, T, V> e : entrySet()) {
			builder += (builder.isEmpty() ? "" : ", ") + e.toString();
		}
		return "MultiMap:[" + builder + "]";
	}

	public static class Entry<K, T, V> {
		private K k;
		private T t;
		private V v;

		public Entry(K key, T thread, V value) {
			k = key;
			t = thread;
			v = value;
		}

		public K getKey() {
			return k;
		}

		public T getThread() {
			return t;
		}

		public V getValue() {
			return v;
		}

		public void setKey(K key) {
			k = key;
		}

		public void setThread(T thread) {
			t = thread;
		}

		public void setValue(V value) {
			v = value;
		}

		public String toString() {
			return k.toString() + ":" + t.toString() + ":" + v.toString();
		}
	}

	@Override
	public String getDataName() {
		return "MultiMap(" + toString() + ")";
	}
}
