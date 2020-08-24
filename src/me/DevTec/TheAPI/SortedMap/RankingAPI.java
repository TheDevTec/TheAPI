package me.DevTec.TheAPI.SortedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class RankingAPI<K, V> {
	private HashMap<K, V> s;
	public RankingAPI(HashMap<K, V> map) {
		setMap(map);
	}
	
	public K getObject(int position) {
		if (position == 0)
			position = 1;
		try {
			position = s.keySet().size() - position;
			return new ArrayList<K>(s.keySet()).get((s.keySet().size() - 1) - position);
		} catch (Exception e) {
			return null;
		}
	}

	public int size() {
		return s.keySet().size();
	}

	public void clear() {
		s.clear();
	}

	public Set<K> getKeySet() {
		return s.keySet();
	}

	public HashMap<K, V> getMap() {
		return s;
	}

	public void setMap(HashMap<K, V> map) {
		s=SortedMap.sortNonComparableByValue(map);
	}
	
	public Set<Entry<K, V>> entrySet(){
		return s.entrySet();
	}
	
	public boolean containsKey(K o) {
		return s.containsKey(o);
	}

	public V getValue(K o) {
		if (containsKey(o))
			return s.get(o);
		return null;
	}

	public int getPosition(K o) {
		int i = 0;
		if (s.containsKey(o))
			return new ArrayList<K>(s.keySet()).indexOf(o) + 1;
		return i;
	}
}