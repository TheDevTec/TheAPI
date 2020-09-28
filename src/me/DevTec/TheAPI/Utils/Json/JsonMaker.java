package me.DevTec.TheAPI.Utils.Json;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonMaker {
	private final List<JSONObject> values = new ArrayList<>();
	private JSONObject json = new JSONObject();
	private boolean edit;

	@SuppressWarnings("unchecked")
	public JsonMaker add(Object key, Object value) {
		edit=true;
		json.put(key, value);
		return this;
	}
	
	public JsonMaker jsonObject() {
		if(edit) {
			values.add(json);
			edit=false;
		}
		json=new JSONObject();
		return this;
	}
	
	public JSONObject getCurrent() {
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String toString() {
		JSONArray a = new JSONArray();
		for(JSONObject o : values)a.add(o);
		if(edit)
		a.add(json);
		return a.toJSONString();
		
	}
}
