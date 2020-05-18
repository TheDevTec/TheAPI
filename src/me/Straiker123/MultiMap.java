package me.Straiker123;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MultiMap<T> {
	private HashMap<T, List<Object>> map = new HashMap<T, List<Object>>();

	public void put(T key, Object... value) {
		if (map.containsKey(key))
			map.remove(key);
		List<Object> l = new ArrayList<Object>();
		for (Object o : value)
			l.add(o);
		map.put(key, l);
	}

	public void remove(T key) {
		map.remove(key);
	}

	public void remove(T key, Object value) {
		if (containsKey(key)) {
			List<Object> o = map.get(key);
			o.remove(value);
			map.replace(key, o);
		}
	}

	public void clear() {
		map.clear();
	}

	public int size() {
		return getKeySet().size();
	}

	public boolean containsKey(T key) {
		return map.containsKey(key);
	}

	public List<T> getKeysTheyContain(Object value) {
		List<T> c = new ArrayList<T>();
		for (T key : keySet()) {
			if (map.get(key).contains(value)) {
				c.add(key);
			}
		}
		return c;
	}

	public boolean containsValue(Object value) {
		boolean c = false;
		for (T key : keySet()) {
			if (map.get(key).contains(value)) {
				c = true;
				break;
			}
		}
		return c;
	}

	public List<Object> getValues(T key) {
		return map.get(key);
	}

	public Set<T> keySet() {
		return map.keySet();
	}

	public List<T> getKeySet() {
		List<T> keys = new ArrayList<T>();
		for (T t : keySet())
			keys.add(t);
		return keys;
	}
}
