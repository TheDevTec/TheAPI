package me.DevTec.TheAPI.Utils.DataKeeper.Maps;

import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheCollection;
import me.DevTec.TheAPI.Utils.DataKeeper.Lists.TheArrayList;

public class MultiMap<K, T, V> implements Cloneable {
	private TheHashMap<K, TheHashMap<T, V>> data;
	public MultiMap() {
		data=new TheHashMap<>(0);
	}

	public MultiMap(int initialSize) {
		data=new TheHashMap<>(initialSize);
	}
	
	public MultiMap(MultiMap<K, T, V> map) {
		this(map, map.size());
	}

	public MultiMap(MultiMap<K, T, V> map, int initialSize) {
		this(initialSize);
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
	}
	
	public void remove(K key) {
		data.remove(key);
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
		if(data.containsKey(key)) {
			TheHashMap<T, V> map = data.get(key);
			map.put(thread, value);
			data.put(key, map);
		}else {
			TheHashMap<T, V> map = new TheHashMap<>();
			map.put(thread, value);
			data.put(key, map);
		}
		return value;
	}
	
	public V get(K key, T thread) {
		return data.containsKey(key)?data.get(key).getOrDefault(thread, null):null;
	}
	
	public void putAll(MultiMap<K, T, V> map) {
		map.entrySet().forEach(Entry -> put(Entry.k, Entry.t, Entry.v));
	}

	public TheCollection<K> keySet(){
		return data.keySet();
	}

	public TheCollection<T> threadSet(K key){
		return data.containsKey(key)?data.get(key).keySet():new TheArrayList<>();
	}

	public TheCollection<V> values(K key, T thread){
		return data.containsKey(key)?data.get(key).values():new TheArrayList<>();
	}
	
	public TheCollection<Entry<K, T, V>> entrySet(){
		TheArrayList<Entry<K, T, V>> entries = new TheArrayList<Entry<K, T, V>>(data.size());
		for(K key : keySet())
			for(T thread : threadSet(key))
				entries.add(new Entry<>(key, thread, get(key, thread)));
		return entries;
	}
	
	public static class Entry<K, T, V> {
		private K k;
		private T t;
		private V v;
		public Entry(K key, T thread, V value) {
			k=key;
			t=thread;
			v=value;
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
			k=key;
		}
		
		public void setThread(T thread) {
			t=thread;
		}
		
		public void setValue(V value) {
			v=value;
		}
	}
}
