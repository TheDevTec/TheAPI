package me.devtec.theapi.utils.datakeeper.maps;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import me.devtec.theapi.utils.datakeeper.collections.UnsortedSet;

/**
 * 
 * Created 29.11. 2020
 * 
 * @author StraikerinaCZ
 * @since 5.0
 * 
 */

public class UnsortedMap<K, V> implements Map<K, V> {
	private Set<Entry<K, V>> entries;

	public UnsortedMap() {
		this(10);
	}

	public UnsortedMap(int size) {
		entries=new UnsortedSet<>(size);
	}

	public UnsortedMap(Map<? extends K, ? extends V> e) {
		this(e.size());
		putAll(e);
	}

	@Override
	public int size() {
		return entries.size();
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public boolean containsKey(Object value) {
		for(Entry<K, V> e : entries)
			if(e.getKey().equals(value))
				return true;
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		for(Entry<K, V> e : entries)
			if(e.getValue().equals(value))
				return true;
		return false;
	}

	@Override
	public V put(K key, V value) {
		boolean v = false;
		V a = null;
		for(Entry<K, V> e : entries)
			if(e.getKey().equals(key)) {
				a=e.setValue(value);
				v=true;
				break;
			}
		if(v)return a;
		entries.add(new Entry<K, V>() {
				V val = value;
				@Override
				public K getKey() {
					return key;
				}
				
				@Override
				public V getValue() {
					return val;
				}
				
				@Override
				public V setValue(V value) {
					V old = val;
					val=value;
					return old;
				}});
		return null;
	}

	@Override
	public V remove(Object value) {
		V old = null;
		for(Entry<K, V> e : entries)
			if(e.getKey().equals(value)){
				old=e.getValue();
				entries.remove(e);
				break;
			}
		return old;
	}

	@Override
	public void clear() {
		entries.clear();
	}

	public String toString() {
		StringBuffer f = new StringBuffer("{");
		Iterator<Entry<K, V>> iterator = entries.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			if (i != 0)
				f.append(", ");
			i = 1;
			Entry<K, V> v = iterator.next();
			f.append(v==null?"null":v.getKey().toString()+"="+v.getValue().toString());
		}
		return f.append("}").toString();
	}
	
	@Override
	public V get(Object key) {
		for(Entry<K, V> e : entries)
			if(e.getKey().equals(key))
				return e.getValue();
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> cc) {
		if (cc == null)return;
		for (Entry<? extends K, ? extends V> value : cc.entrySet())
			put(value.getKey(), value.getValue());
	}

	@Override
	public Set<K> keySet() {
		Set<K> keys = new UnsortedSet<>();
		for(Entry<K, V> e : entries)
			keys.add(e.getKey());
		return keys;
	}

	@Override
	public Collection<V> values() {
		Set<V> keys = new UnsortedSet<>();
		for(Entry<K, V> e : entries)
			keys.add(e.getValue());
		return keys;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new UnsortedSet<>(entries);
	}
}