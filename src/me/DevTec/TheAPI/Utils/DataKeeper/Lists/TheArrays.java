package me.DevTec.TheAPI.Utils.DataKeeper.Lists;

import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.TheHashMap;

public class TheArrays {
	@SafeVarargs
	public static <T> TheArrayList<T> asList(T... items){
		TheArrayList<T> i = new TheArrayList<>(items.length);
		for(T t : items)
			i.add(t);
		return i;
	}
	@SafeVarargs
	public static <T> TheArrayList<T> newList(T... items){
		return asList(items);
	}
	
	public static <T> T[] copyOf(T[] origin, int newSize) {
		@SuppressWarnings("unchecked")
		T[] o = (T[]) new Object[newSize];
		int i = 0;
		for(T t : origin)
			o[i++]=t;
		return o;
	}
	
	public static <K, V> TheHashMap<K, V> newHashMap(){
		return new TheHashMap<>();
	}
	
	public static <K, T, V> MultiMap<K, T, V> newMultiMap(){
		return new MultiMap<>();
	}
}
