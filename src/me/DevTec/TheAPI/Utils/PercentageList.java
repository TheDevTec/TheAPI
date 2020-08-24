package me.DevTec.TheAPI.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PercentageList<T> {
	private final HashMap<T, Double> a = Maps.newHashMap();
	
	public boolean add(T t, double percent) {
		a.put(t, percent);
		return true;
	}
	
	public boolean remove(T t) {
		if(!contains(t))return false;
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
		return t.get(new Random().nextInt(t.size()));
	}

	public List<T> toList() {
		List<T> t = Lists.newArrayList();
		for(T a : a.keySet())
		for(double i = 0; i < getChance(a); ++i)
			t.add(a);
		Collections.shuffle(t);
		return t;
	}
}