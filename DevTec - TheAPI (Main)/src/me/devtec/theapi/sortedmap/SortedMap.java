package me.devtec.theapi.sortedmap;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SortedMap {
	
	public static <K, V> Map<K, V> sortByKey(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>(map.size());
		for(ComparableObject<K, V> o : sortByKeyArray(map))result.put(o.getKey(), o.getValue());
		return result;
	}

	public static <K, V> Map<K, V> sortByValue(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>(map.size());
		for(ComparableObject<K, V> o : sortByValueArray(map))result.put(o.getKey(), o.getValue());
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ComparableObject<K, V>[] sortByKeyArray(Map<K, V> map) {
		ComparableObject<K, V>[] sort = new ComparKey[map.size()];
		int i = 0;
		for(Entry<K, V> d : map.entrySet())
			sort[i++]=new ComparKey<K, V>(d.getKey(), d.getValue());
		Arrays.sort(sort);
		return sort;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ComparableObject<K, V>[] sortByValueArray(Map<K, V> map) {
		ComparableObject<K, V>[] sort = new ComparValue[map.size()];
		int i = 0;
		for(Entry<K, V> d : map.entrySet())
			sort[i++]=new ComparValue<K, V>(d.getKey(), d.getValue());
		Arrays.sort(sort);
		return sort;
	}
	
	public static abstract class ComparableObject<K, V> implements Comparable<ComparableObject<K,V>> {
		public abstract K getKey();
		public abstract V getValue();
		public abstract int compareTo(ComparableObject<K,V> o);
	}
	
	public static class ComparKey<K, V> extends ComparableObject<K, V>  {
		K key;
		V value;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public int compareTo(ComparableObject o) {
			return o.getKey() instanceof Comparable ? ((Comparable)o.getKey()).compareTo(key) : o.getKey().toString().compareTo(key.toString());
		}
		
		@SuppressWarnings("unchecked")
		public ComparKey(Object key, Object value){
			this.key=(K)key;
			this.value=(V)value;
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}
	}
	
	public static class ComparValue<K, V>  extends ComparableObject<K, V> {
		K key;
		V value;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public int compareTo(ComparableObject o) {
			return o.getValue() instanceof Comparable ? ((Comparable)o.getValue()).compareTo(value) : o.getValue().toString().compareTo(value.toString());
		}
		
		@SuppressWarnings("unchecked")
		public ComparValue(Object key, Object value){
			this.key=(K)key;
			this.value=(V)value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}
	}
}
