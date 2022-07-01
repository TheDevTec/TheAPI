package me.devtec.shared;

public class Pair {
	Object key;
	Object value;

	public Pair(Object o, Object o1) {
		this.key = o;
		this.value = o1;
	}

	public Object getKey() {
		return this.key;
	}

	public Object getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "Pair{" + this.key + "=" + this.value + "}";
	}
}