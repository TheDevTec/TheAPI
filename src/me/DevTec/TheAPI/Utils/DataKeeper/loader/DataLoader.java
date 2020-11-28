package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public Set<String> getKeys();

	public void reset();

	public default void load(File f) {
		load(Reader.read(f, true));
		if (!loaded())
			load(Reader.read(f, false)); // for json & byte
	}

	public static DataLoader findLoaderFor(File a) {
		String aa = Reader.read(a, true), b = Reader.read(a, false);
		DataLoader found = new ByteLoader();
		found.load(aa);
		if (!found.loaded())
			found.load(b);
		if (found.loaded())
			return found;
		found = new JsonLoader();
		found.load(aa);
		if (!found.loaded())
			found.load(b);
		if (found.loaded())
			return found;
		found = new YamlLoader();
		found.load(aa);
		if (!found.loaded())
			found.load(b);
		if (found.loaded())
			return found;
		return new EmptyLoader();
	}

	public static DataLoader findLoaderFor(String input) {
		DataLoader found = new ByteLoader();
		found.load(input);
		if (found.loaded())
			return found;
		found = new JsonLoader();
		found.load(input);
		if (found.loaded())
			return found;
		found = new YamlLoader();
		found.load(input);
		if (found.loaded())
			return found;
		return new EmptyLoader();
	}
}
