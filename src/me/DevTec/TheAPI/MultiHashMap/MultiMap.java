package me.DevTec.TheAPI.MultiHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MultiMap<K, T, V> implements MultiHashtable<K, T, V> {
	private HashMap<K, LinkedHashMap<T, V>> a = Maps.newHashMap();

	@Override
	public boolean containsKey(K key) {
		return a.containsKey(key);
	}

	@Override
	public boolean containsThread(K key, T thread) {
		if(a.containsKey(key))
		return a.get(key).containsKey(thread);
		return false;
	}

	@Override
	public boolean containsValue(K key, V value) {
		if(a.containsKey(key))
		return a.get(key).containsValue(value);
		return false;
	}

	@Override
	public V get(K key, T thread) {
		return a.get(key).get(thread);
	}

	@Override
	public V put(K key, T thread, V value) {
		LinkedHashMap<T, V> c = a.containsKey(key) ? a.get(key) : Maps.newLinkedHashMap();
		c.put(thread, value);
		a.put(key,c);
		return value;
	}

	@Override
	public void remove(K key) {
		a.remove(key);
	}

	@Override
	public void removeThread(K key, T thread) {
		a.get(key).remove(thread);
	}
	
	@Override
	public ArrayList<K> keySet() {
		return new ArrayList<K>(a.keySet());
	}

	@Override
	public ArrayList<T> getThreads(K key) {
		return new ArrayList<T>(a.get(key).keySet());
	}

	@Override
	public ArrayList<V> getValues(K key) {
		if(a.containsKey(key))
		return Lists.newArrayList(a.get(key).values());
		return Lists.newArrayList();
	}

	@Override
	public void clear() {
		a.clear();
	}

	@Override
	public void clear(K key) {
		a.get(key).clear();
	}

	@Override
	public int size() {
		return a.size();
	}

}
