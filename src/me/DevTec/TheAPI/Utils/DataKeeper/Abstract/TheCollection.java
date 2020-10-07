package me.DevTec.TheAPI.Utils.DataKeeper.Abstract;

import java.util.Collection;
import java.util.HashSet;

public interface TheCollection<T> extends Iterable<T>, Data {
	
	public boolean contains(Object item);
	
	public default boolean containsAll(TheCollection<? extends T> collection) {
		int i = 1;
		for(T t : collection) {
			if(!contains(t)) {
				i=0;
				break;
			}
		};
		return i == 1;
	}
	
	public default boolean containsAll(Collection<? extends T> collection) {
		int i = 1;
		for(T t : collection) {
			if(!contains(t)) {
				i=0;
				break;
			}
		};
		return i == 1;
	}

	public boolean isEmpty();

	public int size();

	public boolean add(T item);

	public boolean addAll(TheCollection<? extends T> collection);

	public boolean addAll(Collection<? extends T> collection);

	public boolean remove(T item);

	public boolean removeAll(TheCollection<? extends T> collection);
	
	public boolean removeAll(Collection<? extends T> collection);

	public void clear();

	public T[] toArray();
	
	public default Collection<T> toCollection() {
		Collection<T> collection = new HashSet<>();
		for(T t : this)
		collection.add(t);
		return collection;
	}
}
