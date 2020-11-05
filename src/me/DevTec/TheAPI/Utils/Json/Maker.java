package me.DevTec.TheAPI.Utils.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Maker implements Iterable<String> {
	public static class Reader implements Iterable<Object> {
		private List<Object> values = new ArrayList<>();
		private String json;

		public Reader() {

		}

		public Reader(String json) {
			open(json);
		}

		public void open(String json) {
			this.json = json;
			values = me.DevTec.TheAPI.Utils.Json.Reader.list(json);
		}

		public String getCurrentJson() {
			return json;
		}

		public List<Object> getValues() {
			return values;
		}

		public Iterator<Object> iterator() {
			return values.iterator();
		}
	}

	private List<String> values;

	public Maker() {
		values = new ArrayList<>();
	}

	public Maker(Maker maker) {
		values = maker.values;
	}

	public Iterator<String> iterator() {
		return values.iterator();
	}

	public Maker add(Object o) {
		values.add(Writer.object(o, true, true));
		return this;
	}

	public MakerObject create() {
		return new MakerObject();
	}

	public String toString() {
		return Writer.collection(values, true, true);
	}

	public class MakerObject extends HashMap<Object, Object> {
		private static final long serialVersionUID = 1L;

		public Maker getMaker() {
			return Maker.this;
		}

		public MakerObject add(Object key, Object item) {
			super.put(key, item);
			return this;
		}

		public MakerObject put(Object key, Object item) {
			return add(key, item);
		}

		public MakerObject remove(Object key) {
			super.remove(key);
			return this;
		}

		public String toString() {
			return Writer.map(this, true, true);
		}
	}
}
