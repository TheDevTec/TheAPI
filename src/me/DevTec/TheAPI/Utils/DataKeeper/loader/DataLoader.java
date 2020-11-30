package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.DevTec.TheAPI.Utils.StreamUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.DataKeeper.Abstract.Data;

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
		load(StreamUtils.fromStream(f));
	}

	public static DataLoader findLoaderFor(File a) {
		String aa = StreamUtils.fromStream(a);
		DataLoader found = new ByteLoader();
		found.load(aa);
		if (found.loaded())
			return found;
		found = new JsonLoader();
		found.load(aa);
		if (found.loaded())
			return found;
		found = new YamlLoader();
		found.load(aa);
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
