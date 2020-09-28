package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap;
import me.DevTec.TheAPI.Utils.Json.jsonmaker.Maker;

public class JsonLoader implements DataLoader {
	private static JSONParser parser = new JSONParser();
	private boolean l;
	private MultiMap<String, String, DataHolder> data = new MultiMap<>();
	
	@Override
	public MultiMap<String, String, DataHolder> get() {
		return data;
	}
	
	@Override
	public void load(String input) {
		data.clear();
		try {
		JSONArray s = (JSONArray)parser.parse(input);
		for(int i = 0; i < s.size(); ++i) {
			JSONObject o = (JSONObject) s.get(i);
		for(Object key : o.keySet()) {
			Object read = o.get(key);
			data.put(key.toString().split("\\.")[0], key.toString(), new DataHolder(read instanceof String?Maker.objectFromJson(read.toString()):read));
		}}
		l=true;
		}catch(Exception er) {
			l=false;
		}
	}

	@Override
	public List<String> getHeader() {
		//NOT SUPPORTED
		return null;
	}

	@Override
	public List<String> getFooter() {
		//NOT SUPPORTED
		return null;
	}

	@Override
	public boolean loaded() {
		return l;
	}
}
