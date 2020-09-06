package me.DevTec.TheAPI.Utils.DataKeeper.Abstract;

public interface TheList<T> extends TheCollection<T> {

	public T get(int index);

	public T set(int index, T item);
}
