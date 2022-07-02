package me.devtec.shared.dataholder.loaders;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.devtec.shared.dataholder.loaders.constructor.DataLoaderConstructor;
import me.devtec.shared.dataholder.loaders.constructor.DataValue;
import me.devtec.shared.dataholder.loaders.constructor.LoaderPriority;
import me.devtec.shared.utility.StreamUtils;

public abstract class DataLoader {

	// Data loaders hierarchy
	public static Map<LoaderPriority, Set<DataLoaderConstructor>> dataLoaders = new ConcurrentHashMap<>();
	static final LoaderPriority[] priorities = { LoaderPriority.LOWEST, LoaderPriority.LOW, LoaderPriority.NORMAL,
			LoaderPriority.HIGH, LoaderPriority.HIGHEST };
	static {
		for (LoaderPriority priority : DataLoader.priorities)
			DataLoader.dataLoaders.put(priority, new HashSet<>());

		// BUILT-IN LOADERS
		DataLoader.dataLoaders.get(LoaderPriority.LOW).add(ByteLoader::new);
		DataLoader.dataLoaders.get(LoaderPriority.NORMAL).add(JsonLoader::new);
		DataLoader.dataLoaders.get(LoaderPriority.NORMAL).add(PropertiesLoader::new);
		DataLoader.dataLoaders.get(LoaderPriority.HIGH).add(YamlLoader::new);
		DataLoader.dataLoaders.get(LoaderPriority.HIGHEST).add(EmptyLoader::new);
	}

	public static void register(LoaderPriority priority, DataLoaderConstructor constructor) {
		DataLoader.dataLoaders.get(priority).add(constructor);
	}

	public void unregister(DataLoaderConstructor constructor) {
		LoaderPriority priority = null;
		for (Entry<LoaderPriority, Set<DataLoaderConstructor>> entry : DataLoader.dataLoaders.entrySet())
			if (entry.getValue().contains(constructor)) {
				priority = entry.getKey();
				break;
			}
		if (priority != null)
			DataLoader.dataLoaders.get(priority).remove(constructor);
	}

	// Does DataLoader have own loader from file?
	public abstract boolean loadingFromFile();

	public abstract Map<String, DataValue> get();

	public abstract void set(String key, DataValue value);

	public abstract boolean remove(String key);

	public abstract Collection<String> getHeader();

	public abstract Collection<String> getFooter();

	public abstract Set<String> getKeys();

	public abstract void reset();

	public abstract void load(String input);

	public abstract boolean isLoaded();

	public void load(File file) {
		if (file == null || !file.exists())
			return;
		this.load(StreamUtils.fromStream(file));
	}

	public static DataLoader findLoaderFor(File input) {
		String inputString = null;
		for (LoaderPriority priority : DataLoader.priorities)
			for (DataLoaderConstructor constructor : DataLoader.dataLoaders.get(priority)) {
				DataLoader loader = constructor.construct();
				if (loader.loadingFromFile())
					loader.load(input);
				else {
					if (inputString == null)
						inputString = StreamUtils.fromStream(input);
					loader.load(inputString);
				}
				if (loader.isLoaded())
					return loader;
			}
		return null;
	}

	public static DataLoader findLoaderFor(String inputString) {
		for (LoaderPriority priority : DataLoader.priorities)
			for (DataLoaderConstructor constructor : DataLoader.dataLoaders.get(priority)) {
				DataLoader loader = constructor.construct();
				loader.load(inputString);
				if (loader.isLoaded())
					return loader;
			}
		return null;
	}
}
