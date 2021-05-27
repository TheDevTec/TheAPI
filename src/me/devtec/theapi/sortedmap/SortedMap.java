package me.devtec.theapi.sortedmap;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SortedMap {
	public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
		return sortNonComparableByKey(map);
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		return sortNonComparableByValue(map);
	}

	public static <K, V> Map<K, V> sortNonComparableByKey(Map<K, V> map) {
		TreeMap<K, V> result = new TreeMap<>(new Comparator<K>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public int compare(K o1, K o2) {
				if(o2 instanceof Comparable && o1 instanceof Comparable)
					return ((Comparable)o2).compareTo((Comparable)o1);
				return o2.toString().compareTo(o1.toString());
			}
		});
		result.putAll(map);
		LinkedHashMap<K, V> resultFix = new LinkedHashMap<>();
		for(Entry<K, V> entry : result.entrySet())
			resultFix.put(entry.getKey(), entry.getValue());
		return resultFix;
	}

	public static <K, V> Map<K, V> sortNonComparableByValue(Map<K, V> map) {
		TreeMap<V, List<K>> result = new TreeMap<>(new Comparator<V>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public int compare(V o1, V o2) {
				if(o2 instanceof Comparable && o1 instanceof Comparable)
					return ((Comparable)o2).compareTo((Comparable)o1);
				return o2.toString().compareTo(o1.toString());
			}
		});
		for(Entry<K, V> entry : map.entrySet()) {
			List<K> e = result.get(entry.getValue());
			if(e==null)
				result.put(entry.getValue(), e=new ArrayList<>());
			e.add(entry.getKey());
		}
		LinkedHashMap<K, V> resultFix = new LinkedHashMap<>();
		for(Entry<V, List<K>> entry : result.entrySet())
			for(K r : entry.getValue())
			resultFix.put(r, entry.getKey());
		return resultFix;
	}
}
