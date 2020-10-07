package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.Json.jsonmaker.Maker;

public class JsonLoader implements DataLoader {
	private boolean l;
	private HashMap<String, DataHolder> data = new HashMap<>();
	
	@Override
	public HashMap<String, DataHolder> get() {
		return data;
	}
	
	@Override
	public void load(String input) {
		data.clear();
		synchronized(this) {
		try {
			ArrayList<?> s = (ArrayList<?>)Maker.objectFromJson(input);
		for(int ir = 0; ir < s.size(); ++ir) {
			HashMap<?,?> o = (HashMap<?,?>) s.get(ir);
		for(Entry<?, ?> keyed : o.entrySet()) {
			String key = keyed.getKey().toString();
			String object = keyed.getValue().toString();
			data.put(key, new DataHolder(Maker.objectFromJson(object)));
		}}
		l=true;
		}catch(Exception er) {
			l=false;
		}}
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

	@Override
	public String getDataName() {
		return "Data(JsonLoader:"+data.size()+")";
	}
}
