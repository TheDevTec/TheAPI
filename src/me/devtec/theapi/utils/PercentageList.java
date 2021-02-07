package me.devtec.theapi.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class PercentageList<T> {
	private static Random random = new Random();
	private final HashMap<T, Double> a = new HashMap<>();

	public boolean add(T t, double percent) {
		a.put(t, percent);
		return true;
	}

	public boolean remove(T t) {
		a.remove(t);
		return true;
	}

	public boolean contains(T t) {
		return a.containsKey(t);
	}

	public double getChance(T t) {
		return a.get(t);
	}
	
	public boolean isEmpty() {
		return a.isEmpty();
	}
	
	public Set<Entry<T, Double>> entrySet() {
		return a.entrySet();
	}
	
	public Set<T> keySet() {
		return a.keySet();
	}
	
	public Collection<Double> values() {
		return a.values();
	}

	public T getRandom() {
		ArrayList<T> t = new ArrayList<>(a.size());
		for (Entry<T, Double> a : a.entrySet())
			for (double i = 0; i < a.getValue(); ++i)
				t.add(a.getKey());
		if(t.isEmpty())return null;
		int r = random.nextInt(t.size());
		Collections.shuffle(t);
		return t.get(r);
	}

	public double getTotalChance() {
		double chance = 0;
		for (Entry<T, Double> a : a.entrySet())
			chance+=a.getValue();
		return chance;
	}

	public List<T> toList() {
		ArrayList<T> t = new ArrayList<>(a.size());
		for (Entry<T, Double> a : a.entrySet())
			for (double i = 0; i < a.getValue(); ++i)
				t.add(a.getKey());
		Collections.shuffle(t);
		return t;
	}
}