package me.DevTec.TheAPI.Utils.Json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Maker extends ArrayList<Object> {
	private static final long serialVersionUID = 1L;

	public Maker() {
	}

	@SuppressWarnings("unchecked")
	public Maker(String json) {
		addAll((Collection<Object>) Reader.read(json));
	}
	
	public Maker(Collection<Object> obj) {
		addAll(obj);
	}
	
	public void addSerilized(String json) {
		add(Reader.read(json));
	}
	
	public void removeSerilized(String json) {
		remove(Reader.read(json));
	}

	public MakerObject create() {
		return new MakerObject();
	}

	public String toString() {
		return Writer.write(this);
	}

	public String toString(boolean fancy) {
		return Writer.write(this, fancy);
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
			return Writer.write(this);
		}
	}
}
