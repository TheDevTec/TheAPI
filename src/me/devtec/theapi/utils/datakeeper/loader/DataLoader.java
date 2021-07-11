package me.devtec.theapi.utils.datakeeper.loader;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.datakeeper.abstracts.Data;

public abstract class DataLoader implements Data {
	public abstract Map<String, Object[]> get();

	public abstract void set(String key, Object[] value);

	public abstract void remove(String key);

	public abstract Collection<String> getHeader();

	public abstract Collection<String> getFooter();

	public abstract Set<String> getKeys();

	public abstract void reset();
	
	public abstract void load(String input);
	
	public abstract boolean isLoaded();
	
	public void load(File f) {
		load(StreamUtils.fromStream(f));
	}

	public static DataLoader findLoaderFor(File input) {
		DataLoader data = new ByteLoader();
		try {
		data.load(input);
		if(data.isLoaded())return data;
		}catch(Exception err) {}
		data = new JsonLoader();
		try {
		data.load(input);
		if(data.isLoaded())return data;
		}catch(Exception err) {}
		PropertiesLoader data3 = new PropertiesLoader();
		try {
		data3.load(input);
		}catch(Exception err) {}
		data = new YamlLoader();
		try {
		data.load(input);
		if(data3.isLoaded() && !data3.get().isEmpty() && data3.get().size()>data.get().size())return data3;
		if(data.isLoaded())return data;
		}catch(Exception err) {}
		return new EmptyLoader();
	}

	public static DataLoader findLoaderFor(String input) {
		DataLoader data = new ByteLoader();
		try {
		data.load(input);
		if(data.isLoaded())return data;
		}catch(Exception err) {}
		data = new JsonLoader();
		try {
		data.load(input);
		if(data.isLoaded())return data;
		}catch(Exception err) {}
		PropertiesLoader data3 = new PropertiesLoader();
		try {
		data3.load(input);
		}catch(Exception err) {}
		data = new YamlLoader();
		try {
		data.load(input);
		if(data3.isLoaded() && !data3.get().isEmpty() && data3.get().size()>data.get().size())return data3;
		if(data.isLoaded())return data;
		}catch(Exception err) {}
		return new EmptyLoader();
	}
	
	public String toString() {
		return "{\"Data\":{\"name\":\""+this.getClass().getCanonicalName()+"\",\"size\":"+get().size()+",\"load\":"+isLoaded()+"}}";
	}
	
	public String getDataName() {
		return toString();
	}
}
