package me.devtec.theapi.sortedmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SortedMap {
	public static <K extends Comparable<? super K>, V> LinkedHashMap<K, V> sortByKey(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return o2.getKey().compareTo(o1.getKey());
			}
		});
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>(list.size());
		for (Map.Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());
		return result;
	}

	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>(list.size());
		for (Map.Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());
		return result;
	}

	public static <K, V> LinkedHashMap<K, V> sortNonComparableByKey(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, new Comparator<Entry<K, V>>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				if (e1.getKey() instanceof Comparable)
					return ((Comparable) e1.getKey()).compareTo((Comparable) e2.getKey());
				return (e2.getKey() + "").compareTo(e1.getKey() + "");
			}
		});
		LinkedHashMap<K, V> result = new LinkedHashMap<>(list.size());
		for (Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());
		return result;
	}

	public static <K, V> LinkedHashMap<K, V> sortNonComparableByValue(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
		Collections.sort(list, new Comparator<Entry<K, V>>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
				if (e1.getValue() instanceof Comparable) {
					return ((Comparable) e2.getValue()).compareTo((Comparable) e1.getValue());
				}
				return (e2.getValue() + "").compareTo(e1.getValue() + "");
			}
		});
		LinkedHashMap<K, V> result = new LinkedHashMap<>(list.size());
		for (Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());
		return result;
	}
}
