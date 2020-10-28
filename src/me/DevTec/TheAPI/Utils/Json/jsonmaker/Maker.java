package me.DevTec.TheAPI.Utils.Json.jsonmaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Maker {
	
	public static class Reader {
		private List<Object> values = new ArrayList<>();
		private String json;
		public Reader() {
			
		}
		
		public Reader(String json) {
			open(json);
		}
		
		public void open(String json) {
			this.json=json;
			values=me.DevTec.TheAPI.Utils.Json.jsonmaker.Reader.list(json);
		}
		
		public String getCurrentJson() {
			return json;
		}
		
		public List<Object> getValues() {
			return values;
		}
	}
	
	private List<String> values = new ArrayList<>();
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
	
	public class MakerObject {
		private HashMap<Object, Object> o = new HashMap<>();
		public Maker getMaker() {
			return Maker.this;
		}
		
		public MakerObject add(Object key, Object item) {
			o.put(key, item);
			return this;
		}

		public MakerObject put(Object key, Object item) {
			return add(key, item);
		}
		
		public MakerObject remove(Object key) {
			o.remove(key);
			return this;
		}
		
		public Object get(Object key) {
			return o.get(key);
		}
		
		public boolean containsKey(Object key) {
			return o.containsKey(key);
		}
		
		public boolean containsValue(Object value) {
			return o.containsKey(value);
		}
		
		public String toString() {
			return Writer.map(o, true, true);
		}
	}
}
