package me.devtec.shared.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PercentageList<T> {
	private final ConcurrentHashMap<T, Double> a = new ConcurrentHashMap<>();

	public boolean add(T t, double percent) {
		this.a.put(t, percent);
		return true;
	}

	public boolean remove(T t) {
		this.a.remove(t);
		return true;
	}

	public boolean contains(T t) {
		return this.a.containsKey(t);
	}

	public double getChance(T t) {
		return this.a.get(t);
	}

	public boolean isEmpty() {
		return this.a.isEmpty();
	}

	public Set<Entry<T, Double>> entrySet() {
		return this.a.entrySet();
	}

	public Set<T> keySet() {
		return this.a.keySet();
	}

	public Collection<Double> values() {
		return this.a.values();
	}

	public T getRandom() {
		if (this.a.isEmpty())
			return null;

		int total = (int) this.getTotalChance();
		if (total <= 0)
			return null;

		double chance = StringUtils.random.nextInt(total) + StringUtils.random.nextDouble();
		return PercentageList.select(this.a, chance);
	}

	private static <K> K select(Map<K, Double> keys, double chance) {
		@SuppressWarnings("unchecked")
		Entry<K, Double>[] aw = keys.entrySet().toArray(new Entry[0]);
		Entry<K, Double> found = aw[keys.size() - 1];
		List<Entry<K, Double>> duplicates = new ArrayList<>(3);
		for (Entry<K, Double> kDoubleEntry : aw) {
			Entry<K, Double> e = kDoubleEntry;
			if (chance == e.getValue() && chance == found.getValue())
				duplicates.add(e);
			else if (chance < e.getValue() && chance > found.getValue()) {
				found = e;
				duplicates.clear();
			}
		}
		if (!duplicates.isEmpty()) {
			duplicates.add(found);
			return StringUtils.getRandomFromList(duplicates).getKey();
		}
		return found.getKey();
	}

	public double getTotalChance() {
		double d = 0.0;
		for (double as : this.a.values())
			d += as < 0 ? 0 : as;
		return d;
	}

	public List<T> toList() {
		return new ArrayList<>(this.a.keySet());
	}
}