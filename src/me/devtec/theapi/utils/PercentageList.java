package me.devtec.theapi.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		double chance = random.nextInt((int)getTotalChance())+random.nextDouble();
		return select(a,chance);
	}
	
	private static <K> K select(Map<K, Double> keys, double chance) {
		double nearest = 0;
		@SuppressWarnings("unchecked")
		Entry<K,Double>[] aw = keys.entrySet().toArray(new Entry[0]);
		Entry<K,Double> found = aw[keys.size()-1];
		for(int i = 0; i < aw.length; ++i) {
			Entry<K, Double> e = (Entry<K, Double>) aw[i];
			if(chance<e.getValue() && chance > nearest) {
				nearest=e.getValue();
				found=e;
			}
		}
		return found.getKey();
	}
	
	public double getTotalChance() {
		double d = 0.0;
		for(double as : a.values())d+=as;
		return d;
	}

	public List<T> toList() {
		return new ArrayList<>(a.keySet());
	}
}