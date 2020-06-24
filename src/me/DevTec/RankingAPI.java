package me.DevTec;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class RankingAPI<T> {
	private HashMap<T, BigDecimal> s;
	private boolean startFromZero;
	public RankingAPI(HashMap<T, BigDecimal> map) {
		setHashMap(map);
	}
	
	public void setHashMap(HashMap<T, BigDecimal> map) {
		if (map == null)return;
		List<Entry<T, BigDecimal>> list = Lists.newLinkedList(map.entrySet());
		Collections.sort(list, new Comparator<Entry<T, BigDecimal>>() {
			@Override
			public int compare(Entry<T, BigDecimal> o1, Entry<T, BigDecimal> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		HashMap<T, BigDecimal> sortedMap = Maps.newLinkedHashMap();
		for (Entry<T, BigDecimal> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		s= sortedMap;
	}
	
	public HashMap<T, BigDecimal> getSortedMap() {
		List<Entry<T, BigDecimal>> list = Lists.newLinkedList(s.entrySet());
		Collections.sort(list, new Comparator<Entry<T, BigDecimal>>() {
			@Override
			public int compare(Entry<T, BigDecimal> o1, Entry<T, BigDecimal> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		HashMap<T, BigDecimal> sortedMap = Maps.newLinkedHashMap();
		for (Entry<T, BigDecimal> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public T getObject(int position) {
		if (position == 0) {
			startFromZero=true;
			position = 1;
		}
		try {
			position = s.keySet().size() - position+(startFromZero?1:0);
			return (T) Lists.newArrayList(s.keySet()).get((s.keySet().size() - 1) - position+(startFromZero?1:0));
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

	public void reset() {
		startFromZero=false;
	}

	public List<T> getKeySet() {
		return Lists.newArrayList(s.keySet());
	}

	public HashMap<T, BigDecimal> getHashMap() {
		return s;
	}
	
	public boolean containsKey(T o) {
		return s.containsKey(o);
	}

	public BigDecimal getValue(T o) {
		if (containsKey(o))
			return s.get(o);
		return new BigDecimal(-1);
	}

	public int getPosition(T o) {
		int i = 0;
		if (s.containsKey(o))
			return Lists.newArrayList(s.keySet()).indexOf(o) + 1;
		return i;
	}
}
