package me.DevTec.TheAPI.Utils.DataKeeper.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import me.DevTec.TheAPI.Utils.DataKeeper.TheArrays;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.TheCollection;
import me.DevTec.TheAPI.Utils.DataKeeper.Lists.TheArrayList;

public class SortedMultiMap<K, T, V> extends MultiMap<K, T, V> {
	private Map<K, Map<T, V>> data = new HashMap<>();

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
		if(data.get(key).isEmpty())data.remove(key);
	}
	
	public void remove(K key) {
		data.remove(key);
	}
	
	public V get(K key, T thread) {
		try {
		Map<T, V> t = data.get(key);
		return t.get(thread);
		}catch(Exception er) {
			return null;
		}
	}
	
	public V getOrDefault(K key, T thread, V def) {
		V get = get(key, thread);
		return get==null?def:get;
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
		Map<T, V> map = data.getOrDefault(key, null);
		if(map==null) {
			map=new TreeMap<>();
			data.put(key, map);
		}
		map.put(thread, value);
		return value;
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}
	
	public void putAll(SortedMultiMap<K, T, V> map) {
		map.entrySet().forEach(Entry -> put(Entry.getKey(), Entry.getThread(), Entry.getValue()));
	}

	public TheCollection<K> keySet(){
		return new TheArrayList<>(data.keySet());
	}

	public TheCollection<T> threadSet(K key){
		return data.containsKey(key)?new TheArrayList<>(data.get(key).keySet()):TheArrays.newList();
	}

	public TheCollection<V> values(K key, T thread){
		return data.containsKey(key)?new TheArrayList<>(data.get(key).values()):TheArrays.newList();
	}
	
	public TheCollection<me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap.Entry<K, T, V>> entrySet(){
		TheArrayList<me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap.Entry<K, T, V>> entries = new TheArrayList<>(data.size());
		for(K key : keySet())
			for(T thread : threadSet(key))
				entries.add(new Entry<>(key, thread, get(key, thread)));
		return entries;
	}
	
	public String toString() {
		String builder = "";
		for(Entry<K, T, V> e : entrySet()) {
			builder+=(builder.isEmpty()?"":", ")+e.toString();
		}
		return "MultiMap:["+builder+"]";
	}

	@Override
	public String getDataName() {
		return "SortedMultiMap("+toString()+")";
	}
}
