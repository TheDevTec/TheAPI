package me.devtec.theapi.utils.nms.datawatcher;

public class DataWatcherObject {
	private Object a;

	public DataWatcherObject(Object c) {
		a = c;
	}

	public Object get() {
		return a;
	}

	public void set(Object newData) {
		a = newData;
	}
}
