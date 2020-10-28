package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.Json.jsonmaker.Reader;

public class JsonLoader implements DataLoader {
	private boolean l;
	private Map<String, DataHolder> data = new HashMap<>();
	
	@Override
	public Map<String, DataHolder> get() {
		return data;
	}
	
	public Collection<String> getKeys() {
		return data.keySet();
	}
	
	public void set(String key, DataHolder holder) {
		if(key==null)return;
		if(holder==null) {
			data.remove(key);
			return;
		}
		data.put(key, holder);
	}
	
	public void remove(String key) {
		if(key==null)return;
		data.remove(key);
	}
	
	public void reset() {
		data.clear();
	}
	
	@Override
	public void load(String input) {
		data.clear();
		synchronized(this) {
		try {
			ArrayList<?> s = (ArrayList<?>)Reader.object(input);
		for(int ir = 0; ir < s.size(); ++ir) {
			HashMap<?,?> o = (HashMap<?,?>) s.get(ir);
		for(Entry<?, ?> keyed : o.entrySet()) {
			String key = keyed.getKey().toString();
			String object = keyed.getValue().toString();
			data.put(key, new DataHolder(Reader.object(object)));
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
