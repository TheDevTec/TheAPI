package me.DevTec.TheAPI.Utils.DataKeeper;

import me.DevTec.TheAPI.Utils.DataKeeper.Lists.TheArrayList;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap;

public class TheArrays {
	@SafeVarargs
	public static <T> TheArrayList<T> asList(T... items) {
		return new TheArrayList<>(items);
	}

	@SafeVarargs
	public static <T> TheArrayList<T> newList(T... items) {
		return new TheArrayList<>(items);
	}

	public static <T> T[] copyOf(T[] original, int newLength) {
		@SuppressWarnings("unchecked")
		T[] copy = (T[]) new Object[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

	public static <K, T, V> MultiMap<K, T, V> newMultiMap() {
		return new MultiMap<>();
	}
}
