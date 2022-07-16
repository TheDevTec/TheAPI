package me.devtec.shared.sorting;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SortingAPI {

	public static <K, V> Map<K, V> sortByKey(Map<K, V> map, boolean asc) {
		Map<K, V> result = new LinkedHashMap<>(map.size());
		for (ComparableObject<K, V> o : SortingAPI.sortByKeyArray(map, asc))
			result.put(o.getKey(), o.getValue());
		return result;
	}

	public static <K, V> Map<K, V> sortByValue(Map<K, V> map, boolean asc) {
		Map<K, V> result = new LinkedHashMap<>(map.size());
		for (ComparableObject<K, V> o : SortingAPI.sortByValueArray(map, asc))
			result.put(o.getKey(), o.getValue());
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ComparableObject<K, V>[] sortByKeyArray(Map<K, V> map, boolean asc) {
		ComparableObject<K, V>[] sort = new ComparKey[map.size()];
		int i = 0;
		for (Entry<K, V> d : map.entrySet())
			sort[i++] = new ComparKey<>(d.getKey(), d.getValue(), asc);
		Arrays.sort(sort);
		return sort;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> ComparableObject<K, V>[] sortByValueArray(Map<K, V> map, boolean asc) {
		ComparableObject<K, V>[] sort = new ComparValue[map.size()];
		int i = 0;
		for (Entry<K, V> d : map.entrySet())
			sort[i++] = new ComparValue<>(d.getKey(), d.getValue(), asc);
		Arrays.sort(sort);
		return sort;
	}

	public static abstract class ComparableObject<K, V> implements Comparable<ComparableObject<K, V>> {
		public abstract K getKey();

		public abstract V getValue();

		@Override
		public abstract int compareTo(ComparableObject<K, V> o);
	}

	public static class ComparKey<K, V> extends ComparableObject<K, V> {
		K key;
		V value;
		boolean asc;

		@Override
		@SuppressWarnings({ "rawtypes" })
		public int compareTo(ComparableObject o) {
			return SortingAPI.compare(asc, o.getKey(), key);
		}

		@SuppressWarnings("unchecked")
		public ComparKey(Object key, Object value, boolean asc) {
			this.key = (K) key;
			this.value = (V) value;
			this.asc = asc;
		}

		@Override
		public K getKey() {
			return this.key;
		}

		@Override
		public V getValue() {
			return this.value;
		}
	}

	public static class ComparValue<K, V> extends ComparableObject<K, V> {
		K key;
		V value;
		boolean asc;

		@Override
		@SuppressWarnings({ "rawtypes" })
		public int compareTo(ComparableObject o) {
			return SortingAPI.compare(asc, o.getValue(), value);
		}

		@SuppressWarnings("unchecked")
		public ComparValue(Object key, Object value, boolean asc) {
			this.key = (K) key;
			this.value = (V) value;
			this.asc = asc;
		}

		@Override
		public K getKey() {
			return this.key;
		}

		@Override
		public V getValue() {
			return this.value;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static int compare(boolean asc, Object a, Object b) {
		return asc ? a instanceof Comparable ? ((Comparable) a).compareTo(b) : a.toString().compareTo(b.toString())
				: b instanceof Comparable ? ((Comparable) (b instanceof Comparable ? b : b.toString())).compareTo(a)
						: b.toString().compareTo(a.toString());
	}
}
