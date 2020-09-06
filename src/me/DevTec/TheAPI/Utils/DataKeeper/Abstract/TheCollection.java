package me.DevTec.TheAPI.Utils.DataKeeper.Abstract;

import java.util.Iterator;

public interface TheCollection<T> extends Iterable<T> {
	
	public boolean contains(T item);
	
	public default boolean containsAll(Iterator<T> collection) {
		int i = 1;
		while(collection.hasNext()) {
			T t = collection.next();
			if(!contains(t)) {
				i=0;
				break;
			}
		};
		return i == 1;
	}
	
	public default boolean containsAll(Iterable<T> collection) {
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

	public T add(T item);

	public default void addAll(Iterable<T> collection) {
		collection.forEach(e -> add(e));
	}

	public default void addAll(Iterator<T> collection) {
		collection.forEachRemaining(e -> add(e));
	}

	public void remove(T item);

	public default void removeAll(Iterable<T> collection) {
		collection.forEach(e -> remove(e));
	}

	public default void removeAll(Iterator<T> collection) {
		collection.forEachRemaining(e -> remove(e));
	}

	public void clear();
}
