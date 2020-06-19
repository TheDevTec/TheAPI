package me.DevTec;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class RankingAPI {
	private HashMap<Object, BigDecimal> s;
 
	public RankingAPI(HashMap<?, BigDecimal> map) {
		if (map != null) {
			HashMap<Object, BigDecimal> fixed = new HashMap<Object, BigDecimal>();
			for (Object o : map.keySet())
				fixed.put(o, map.get(o));
			s = sort(fixed);
		}
	}

	private static HashMap<Object, BigDecimal> sort(HashMap<Object, BigDecimal> map) {
		List<Entry<Object, BigDecimal>> list = new LinkedList<Entry<Object, BigDecimal>>(map.entrySet());
		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<Object, BigDecimal>>() {
			@Override
			public int compare(Entry<Object, BigDecimal> o1, Entry<Object, BigDecimal> o2) {
				return o2.getValue().compareTo(o1.getValue());
				// return o1.getValue().compareTo(o2.getValue());
			}
		});
		HashMap<Object, BigDecimal> sortedMap = new LinkedHashMap<Object, BigDecimal>();
		for (Entry<Object, BigDecimal> entry : list) {
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

	public HashMap<Object, BigDecimal> getHashMap() {
		return s;
	}
	
	public boolean containsKey(Object o) {
		return s.containsKey(o);
	}

	public BigDecimal getValue(Object o) {
		if (containsKey(o))
			return s.get(o);
		return new BigDecimal(-1);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int getPosition(Object o) {
		int i = 0;
		if (s.containsKey(o))
			return new ArrayList(s.keySet()).indexOf(o) + 1;
		return i;
	}
}
