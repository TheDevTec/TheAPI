package me.devtec.theapi.sortedmap;


import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.devtec.theapi.sortedmap.SortedMap.ComparableObject;

public class RankingAPI<K, V> {
	private ComparableObject<K, V>[] s;

	public RankingAPI() {
	}

	public RankingAPI(Map<K, V> map) {
		load(map);
	}

	public ComparableObject<K, V> get(int position) {
		if(s.length<=position)return null;
		return s[position];
	}
	
	public boolean isEmpty() {
		return s==null || s.length==0;
	}

	public int size() {
		return s==null ? 0 : s.length;
	}

	public void clear() {
		s=null;
	}

	public void load(Map<K, V> map) {
		s = SortedMap.sortByValueArray(map);
	}

	@Deprecated
	public boolean containsKey(K o) {
		for(int i = 0; i < size(); ++i)
			if(s[i].getKey().equals(o))return true;
		return false;
	}

	@Deprecated
	public boolean containsValue(V o) {
		for(int i = 0; i < size(); ++i)
			if(o==null? s[i].getValue()==null : o.equals(s[i].getValue()))return true;
		return false;
	}

	@Deprecated
	public int getPosition(K o) {
		for(int i = 0; i < size(); ++i)
			if(s[i].getKey().equals(o))return i;
		return 0;
	}
	
	public List<ComparableObject<K, V>> getTop(int end) {
		return getTop(0, end);
	}
	
	public List<ComparableObject<K, V>> getTop(int start, int end) {
		List<ComparableObject<K, V>> list = new LinkedList<>();
		for(int i = start; i < end; ++i)
			list.add(s[i]);
		return list;
	}

	public ComparableObject<K, V>[] all() {
		return s;
	}
}