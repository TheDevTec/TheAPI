package me.DevTec.TheAPI.MultiHashMap;

import java.util.ArrayList;

public interface MultiHashtable<K, T, V> {
	
	public V get(K key, T thread);
	
	public V put(K key, T thread, V value);
	
	public void remove(K key);
	
	public void removeThread(K key, T thread);
	
	public ArrayList<K> keySet();
	
	public ArrayList<T> getThreads(K key);
	
	public ArrayList<V> getValues(K key);
	
	public boolean containsKey(K key);

	public boolean containsThread(K key, T thread);

	public boolean containsValue(K key, V value);

	public void clear(K key);
	
	public void clear();
	
	public int size();
}
