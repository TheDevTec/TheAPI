package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.MultiMap;
import me.DevTec.TheAPI.Utils.File.Reader;

public interface DataLoader {
	public MultiMap<String, String, DataHolder> get();

	public boolean loaded();
	
	public List<String> getHeader();
	
	public List<String> getFooter();
	
	public void load(String input);
	
	public default void load(File f) {
		load(Reader.read(f, true));
		if(!loaded())
			load(Reader.read(f, false)); //for json & byte
	}

	public static DataLoader findLoaderFor(File a) {
		DataLoader found = null;
		for(String key : Arrays.asList("JSON", "BYTE", "YAML")) {
			found = key.equals("JSON")?new JsonLoader() : (key.equals("BYTE")?new ByteLoader() : new YamlLoader());
			found.load(a);
			if(!found.loaded())
				found=null;
			else break;
		}
		return found;
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
		return found;
	}
}
