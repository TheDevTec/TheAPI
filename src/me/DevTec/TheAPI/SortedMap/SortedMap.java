package me.DevTec.TheAPI.SortedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SortedMap {
	public static <K extends Comparable<? super K>, V> HashMap<K, V> sortByKey(HashMap<K, V> map) {
	    List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
	    Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
	        public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
	            return o1.getKey().compareTo(o2.getKey());
	        }
	    });
	    LinkedHashMap<K, V> result = new LinkedHashMap<K, V>(list.size());
	    for (Map.Entry<K, V> entry : list)
	        result.put(entry.getKey(), entry.getValue());
	    return result;
	}

	public static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(HashMap<K, V> map) {
	    List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
	    Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
	        public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
	            return o1.getValue().compareTo(o2.getValue());
	        }
	    });
	    LinkedHashMap<K, V> result = new LinkedHashMap<K, V>(list.size());
	    for (Map.Entry<K, V> entry : list)
	        result.put(entry.getKey(), entry.getValue());
	    return result;
	}
	
	public static <K, V> HashMap<K, V> sortNonComparableByKey(HashMap<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Entry<K,V>>() {
	        @SuppressWarnings({ "unchecked", "rawtypes" })
	        public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
	        	if(e1.getKey() instanceof Comparable)
	                return ((Comparable)e1.getKey()).compareTo(e2.getKey());
                return (e1.getKey()+"").compareTo(e2.getKey()+"");
            }
        });
        LinkedHashMap<K, V> result = new LinkedHashMap<>(list.size());
        for(Entry<K, V> entry : list)
        	result.put(entry.getKey(), entry.getValue());
        return result;
	}
	
	public static <K, V> HashMap<K, V> sortNonComparableByValue(HashMap<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Entry<K,V>>() {
	        @SuppressWarnings({ "unchecked", "rawtypes" })
			public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
	        	if(e1.getValue() instanceof Comparable)
	                return ((Comparable)e1.getValue()).compareTo(e2.getValue());
                return (e1.getValue()+"").compareTo(e2.getValue()+"");
            }
        });
        LinkedHashMap<K, V> result = new LinkedHashMap<>(list.size());
        for(Entry<K, V> entry : list)
        	result.put(entry.getKey(), entry.getValue());
        return result;
	}
}
