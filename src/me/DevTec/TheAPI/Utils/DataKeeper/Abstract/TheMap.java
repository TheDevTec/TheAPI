package me.DevTec.TheAPI.Utils.DataKeeper.Abstract;

import java.util.Map;
import java.util.function.BiConsumer;

public interface TheMap<K, V> extends Iterable<K> {
	public V put(K key, V item);
	public default V replace(K key, V item) {
		return put(key, item);
	}
	public default void addAll(TheMap<K, V> item) {
		item.forEach((k, v) -> put(k, v));
	}
	public default void addAll(Map<K, V> item) {
		item.forEach((k, v) -> put(k, v));
	}
	
	public void remove(K key);
	public default void removeAll(TheMap<K, V> item) {
		item.forEach((k, v) -> remove(k));
	}
	public default void removeAll(Map<K, V> item) {
		item.forEach((k, v) -> remove(k));
	}

	public boolean containsKey(K item);
	public boolean containsValue(V item);

	public TheCollection<Entry<K, V>> entrySet();
	public TheCollection<K> keySet();
	public TheCollection<V> values();
	
	public int size();
	
	public default void forEach(BiConsumer<K, V> item) {
		for(Entry<K, V> e : entrySet())
		item.accept(e.getKey(), e.getValue());
	}
	
	public static class Entry<K, V> {
		private K k;
		private V t;
		public Entry(K k, V v) {
			this.k=k;
			this.t=v;
		}

		public K getKey() {
			return k;
		}

		public V getValue() {
			return t;
		}

		public void setKey(K k) {
			this.k=k;
		}

		public void setValue(V v) {
			t=v;
		}
	}
}
