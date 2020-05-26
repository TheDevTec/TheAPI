package me.Straiker123;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.Maps;

import me.Straiker123.Abstract.MultiHashtable;

public class MultiMap<K, T, V> implements MultiHashtable<K, T, V> {
	private HashMap<K, T> threads = Maps.newHashMap();
	private HashMap<T, V> values = Maps.newHashMap();

	@Override
	public ArrayList<K> keySet() {
		return new ArrayList<K>(threads.keySet());
	}

	@Override
	public T getThread(K key) {
		return threads.get(key);
	}

	@Override
	public V getValue(T thread) {
		return values.get(thread);
	}

	@Override
	public V put(K key, T thread, V value) {
		threads.put(key, thread);
		values.put(thread,value);
		return value;
	}

	@Override
	public void removeKey(K key) {
		values.remove(getThread(key));
		threads.remove(key);
	}

	@Override
	public void removeThread(T thread) {
		values.remove(thread);
	}

	@Override
	public ArrayList<T> getThreads() {
		return new ArrayList<T>(values.keySet());
	}

	@Override
	public ArrayList<V> getValues() {
		return new ArrayList<V>(values.values());
	}

	@Override
	public Object[] getArray(K key) {
		return new Object[] {getThread(key), getValue(getThread(key))};
	}

}
