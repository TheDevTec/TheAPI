package me.devtec.theapi.utils;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import me.devtec.theapi.utils.datakeeper.collections.UnsortedList;
import me.devtec.theapi.utils.datakeeper.maps.UnsortedMap;

public class PercentageList<T> {
	private static Random random = new Random();
	private final UnsortedMap<T, Double> a = new UnsortedMap<>();

	public boolean add(T t, double percent) {
		a.put(t, percent);
		return true;
	}

	public boolean remove(T t) {
		if (!contains(t))
			return false;
		a.remove(t);
		return true;
	}

	public boolean contains(T t) {
		return a.containsKey(t);
	}

	public double getChance(T t) {
		return a.get(t);
	}

	public T getRandom() {
		List<T> t = toList();
		return t.isEmpty() ? null : t.get(random.nextInt(t.size()));
	}

	public List<T> toList() {
		List<T> t = new UnsortedList<>();
		for (T a : a.keySet())
			for (double i = 0; i < getChance(a); ++i)
				t.add(a);
		Collections.shuffle(t);
		return t;
	}
}