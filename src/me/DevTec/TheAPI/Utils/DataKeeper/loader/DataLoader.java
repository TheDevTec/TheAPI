package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.Data;
import me.DevTec.TheAPI.Utils.File.Reader;

public interface DataLoader extends Data {
	public Map<String, DataHolder> get();

	public boolean loaded();

	public void set(String key, DataHolder value);

	public void remove(String key);
	
	public List<String> getHeader();
	
	public List<String> getFooter();
	
	public void load(String input);

	public Collection<String> getKeys();
	
	public void reset();
	
	public default void load(File f) {
		load(Reader.read(f, true));
		if(!loaded())
			load(Reader.read(f, false)); //for json & byte
	}

	public static DataLoader findLoaderFor(File a) {
		String aa = Reader.read(a, true), b=Reader.read(a, false);
		DataLoader found = null;
		for(String key : Arrays.asList("JSON", "BYTE", "YAML")) {
			found = key.equals("JSON")?new JsonLoader() : (key.equals("BYTE")?new ByteLoader() : new YamlLoader());
			found.load(aa);
			if(!found.loaded()) {
			found.load(b);
				if(!found.loaded())
				found=null;
				else break;
				}
		}
		return found==null ? new EmptyLoader() : found;
	}

	public static DataLoader findLoaderFor(String input) {
		DataLoader found = null;
		for(String key : Arrays.asList("JSON", "BYTE", "YAML")) {
			found = key.equals("JSON")?new JsonLoader() : (key.equals("BYTE")?new ByteLoader() : new YamlLoader());
			found.load(input);
			if(!found.loaded())
				found=null;
			else break;
		}
		return found==null ? new EmptyLoader() : found;
	}
}
