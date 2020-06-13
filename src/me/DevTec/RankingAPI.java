package me.DevTec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class RankingAPI {
	private HashMap<Object, Double> s;

	public RankingAPI(HashMap<?, Double> map) {
		if (map != null) {
			HashMap<Object, Double> fixed = new HashMap<Object, Double>();
			for (Object o : map.keySet())
				fixed.put(o, map.get(o));
			s = sort(fixed);
		}
	}

	private static HashMap<Object, Double> sort(HashMap<Object, Double> map) {
		List<Entry<Object, Double>> list = new LinkedList<Entry<Object, Double>>(map.entrySet());
		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<Object, Double>>() {
			@Override
			public int compare(Entry<Object, Double> o1, Entry<Object, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
				// return o1.getValue().compareTo(o2.getValue());
			}
		});
		HashMap<Object, Double> sortedMap = new LinkedHashMap<Object, Double>();
		for (Entry<Object, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getObject(int position) { // 1, 2, 3... 1501, 1578..
		if (position == 0)
			position = 1;
		try {
			position = s.keySet().size() - position;
			return new ArrayList(s.keySet()).get((s.keySet().size() - 1) - (position));
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

	public List<Object> getKeySet() {
		List<Object> o = new ArrayList<Object>();
		for (Object a : s.keySet())
			o.add(a);
		return o;
	}

	public HashMap<?, Double> getHashMap() {
		return s;
	}

	public double getValue(Object o) {
		double d = -1;
		if (s.containsKey(o))
			d = s.get(o);
		return d;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int getPosition(Object o) {
		int i = 0;
		if (s.containsKey(o))
			return new ArrayList(s.keySet()).indexOf(o) + 1;
		return i;
	}
}
