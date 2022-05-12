package me.devtec.shared.sorting;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SortingAPI {
	
	public static <K, V> Map<K, V> sortByKey(Map<K, V> map, boolean foward) {
		Map<K, V> result = new LinkedHashMap<>(map.size());
		for(ComparableObject<K, V> o : sortByKeyArray(map, foward))result.put(o.getKey(), o.getValue());
		return result;
	}

	public static <K, V> Map<K, V> sortByValue(Map<K, V> map, boolean foward) {
		Map<K, V> result = new LinkedHashMap<>(map.size());
		for(ComparableObject<K, V> o : sortByValueArray(map, foward))result.put(o.getKey(), o.getValue());
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ComparableObject<K, V>[] sortByKeyArray(Map<K, V> map, boolean foward) {
		ComparableObject<K, V>[] sort = new ComparKey[map.size()];
		int i = 0;
		for(Entry<K, V> d : map.entrySet())
			sort[i++]=new ComparKey<K, V>(d.getKey(), d.getValue(), foward);
		Arrays.sort(sort);
		return sort;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ComparableObject<K, V>[] sortByValueArray(Map<K, V> map, boolean foward) {
		ComparableObject<K, V>[] sort = new ComparValue[map.size()];
		int i = 0;
		for(Entry<K, V> d : map.entrySet())
			sort[i++]=new ComparValue<K, V>(d.getKey(), d.getValue(), foward);
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
		boolean foward;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public int compareTo(ComparableObject o) {
			return foward ? 
					(o.getKey() instanceof Comparable ? ((Comparable)o.getKey()).compareTo(key) : o.getKey().toString().compareTo(key.toString()))
					: o.getKey() instanceof Comparable ? ((Comparable)(key instanceof Comparable ? key : key.toString())).compareTo(((Comparable)o.getKey())) : key.toString().compareTo(o.getKey().toString());
		}
		
		@SuppressWarnings("unchecked")
		public ComparKey(Object key, Object value, boolean foward){
			this.key=(K)key;
			this.value=(V)value;
			this.foward=foward;
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
		boolean foward;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public int compareTo(ComparableObject o) {
			return foward ? 
					(o.getValue() instanceof Comparable ? ((Comparable)o.getValue()).compareTo(value) : o.getValue().toString().compareTo(value.toString()))
					: o.getValue() instanceof Comparable ? ((Comparable)(value instanceof Comparable ? value : value.toString())).compareTo(((Comparable)o.getValue())) : key.toString().compareTo(o.getKey().toString());
		}
		
		@SuppressWarnings("unchecked")
		public ComparValue(Object key, Object value, boolean foward){
			this.key=(K)key;
			this.value=(V)value;
			this.foward=foward;
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
