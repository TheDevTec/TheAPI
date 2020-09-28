package me.DevTec.TheAPI.Utils.DataKeeper.Abstract;

public class Entry<K, V> implements Data {
	private static final long serialVersionUID = 1L;
	private K k;
	private V t;

	public Entry() {
	}
	
	public Entry(K k, V v) {
		this.k=k;
		this.t=v;
	}

	public K getKey() {
		return k;
	}

	public V getValue() {
		return t;
	}

	public void setKey(K k) {
		this.k=k;
	}

	public void setValue(V v) {
		t=v;
	}
	
	public String toString() {
		return k.toString()+":"+t.toString();
	}

	@Override
	public String getDataName() {
		return "Entry('"+k+"':'"+t+"')";
	}
}