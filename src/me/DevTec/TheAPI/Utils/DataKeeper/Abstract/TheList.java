package me.DevTec.TheAPI.Utils.DataKeeper.Abstract;

import java.io.Serializable;
import java.util.RandomAccess;

public interface TheList<T> extends TheCollection<T>, RandomAccess, Cloneable, Serializable {

	public T get(int index);

	public T set(int index, T item);
}
