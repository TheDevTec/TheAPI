package me.Straiker123;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MultiMap {
	HashMap<Object,List<Object>> map = new HashMap<Object, List<Object>>();
	
	public void put(Object key, Object... value) {
		if(map.containsKey(key))map.remove(key);
		map.put(key, Arrays.asList(value));
	}
	
	public void remove(Object key) {
		map.remove(key);
	}
	
	public void remove(Object key, Object value) {
		if(containsKey(key)) {
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
	
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	
	public boolean containsValue(Object value) {
		boolean c = false;
		for(Object key : getKeySet()) {
			if(map.get(key).contains(value)) {
				c=true;
				break;
			}
		}
		return c;
	}
	public List<Object> getValues(Object key){
		return map.get(key);
	}
	
	public List<Object> getKeySet(){
		return Arrays.asList(map.keySet());
	}
}
