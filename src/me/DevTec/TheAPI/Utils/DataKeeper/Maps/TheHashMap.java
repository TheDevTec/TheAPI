package me.DevTec.TheAPI.Utils.DataKeeper.Maps;

import java.util.Iterator;
import java.util.Map;

import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheCollection;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheList;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheMap;
import me.DevTec.TheAPI.Utils.DataKeeper.Lists.TheArrayList;

public class TheHashMap<K, V> implements TheMap<K, V> {
	private TheList<Entry<K, V>> data;
	public TheHashMap() {
		this(1);
	}
	
	public TheHashMap(TheHashMap<K, V> data) {
		this(data, data.size());
	}

	public TheHashMap(Map<K, V> data) {
		this(data, data.size());
	}

	public TheHashMap(int initialSize) {
		data = new TheArrayList<>(initialSize);
	}
	
	public TheHashMap(TheHashMap<K, V> data, int initialSize) {
		this(initialSize);
		putAll(data);
	}

	public TheHashMap(Map<K, V> data, int initialSize) {
		this(initialSize);
		putAll(data);
	}
	
	public void clear() {
		data.clear();
	}
	
	public V put(K key, V value) {
		if(key==null)return null;
		Entry<K, V> f = getOrNull(key);
		if(f==null) {
			data.add(new Entry<K, V>(key, value));
		}else {
			data.remove(f);
			f.setValue(value);
			data.add(f);
		}
		return value;
	}
	
	public void remove(K thread) {
		data.remove(getOrNull(thread));
	}
	
	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	public boolean containsKey(K key) {
		return data.contains(getOrNull(key));
	}
	
	public boolean containsValue(V value) {
		boolean c = false;
		for(Entry<K, V> e : data) {
			if(e.getValue().equals(value)) {
				c=true;
				break;
			}
		}
		return c;
	}
	
	public void putAll(TheHashMap<K, V> map) {
		if(map==null)return;
		map.entrySet().forEach(Entry -> put(Entry.getKey(), Entry.getValue()));
	}
	
	public void putAll(Map<K, V> map) {
		if(map==null)return;
		map.entrySet().forEach(Entry -> put(Entry.getKey(), Entry.getValue()));
	}
	
	public V replace(K key, V value) {
		return put(key, value);
	}
	
	public int size() {
		return data.size();
	}
	
	public TheCollection<Entry<K, V>> entrySet(){
		return data;
	}
	
	public TheCollection<K> keySet(){
		return new TheArrayList<>(iterator(), data.size());
	}
	
	private Entry<K, V> getOrNull(K key) {
		Entry<K, V> f = null;
		for(Entry<K, V> k : data) {
			if(k.getKey().equals(key)) {
				f=k;
				break;
			}}
		return f;
	}
	
	public TheCollection<V> values() {
		TheCollection<V> values = new TheArrayList<>(data.size());
		data.forEach(Entry -> values.add(Entry.getValue()));
		return values;
	}
	
	public V get(K key) {
		return getOrDefault(key, null);
	}
	
	public V getOrDefault(K key, V defaultValue) {
		Entry<K, V> f = getOrNull(key);
		return f!=null ? f.getValue() : defaultValue;
	}
	
	@Override
	public Iterator<K> iterator() {
		return new Iterator<K>() {
			private int c;
			
			@Override
			public boolean hasNext() {
				return !(data.size() >= c);
			}
			
			@Override
			public K next() {
				return data.get(c).getKey();
			}
		};
	}
}
