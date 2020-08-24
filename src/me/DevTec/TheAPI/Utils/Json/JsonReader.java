package me.DevTec.TheAPI.Utils.Json;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonReader {
	private final JSONArray a;
	private int next = 0;
    public JsonReader(File f) {
        JSONArray found = null;
         try {
        	 found = (JSONArray)new JSONParser().parse(new FileReader(f));
		} catch (Exception e) {
			e.printStackTrace();
		}
         a=found;
    }
 
    public JSONObject next() {
    	return (JSONObject) a.get(next++);
    }
 
    public boolean hasNext() {
    	return a.size()-1>next;
    }
}
