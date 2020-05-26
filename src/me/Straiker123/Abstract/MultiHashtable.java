package me.Straiker123.Abstract;

import java.util.ArrayList;

public interface MultiHashtable<K, T, V> {
	
	public T getThread(K key);
	
	public V getValue(T thread);
	
	public V put(K key, T thread, V value);
	
	public void removeKey(K key);
	
	public void removeThread(T thread);
	
	public ArrayList<K> keySet();
	
	public ArrayList<T> getThreads();
	
	public ArrayList<V> getValues();
	
	public Object[] getArray(K key);
}
