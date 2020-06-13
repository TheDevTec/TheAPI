package me.DevTec.Json;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonWriter {
	private final File s;
	public JsonWriter(File f) {
		s=f;
	}
	
	public JSONObject object() {
	    return new JSONObject();
	}
	
	public JSONArray path() {
		return new JSONArray();
	}
	
	public void write(JSONArray... o) {
    try (FileWriter file = new FileWriter(s)) {
    	for(JSONArray a : o) {
        file.write(a.toJSONString());
        file.flush();
    	}
    } catch (Exception e) {
        e.printStackTrace();
    }
}

	public void write(List<JSONArray> o) {
		try (FileWriter file = new FileWriter(s)) {
	    	for(JSONArray a : o) {
	        file.write(a.toJSONString());
	        file.flush();
	    	}
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
