package me.devtec.shared.dataholder.loaders;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.devtec.shared.dataholder.loaders.constructor.DataLoaderConstructor;
import me.devtec.shared.dataholder.loaders.constructor.LoaderPriority;
import me.devtec.shared.utility.StreamUtils;

public abstract class DataLoader {
	
	//Data loaders hierarchy
	public static Map<LoaderPriority, Set<DataLoaderConstructor>> dataLoaders = new ConcurrentHashMap<>();
	static final LoaderPriority[] priorities = new LoaderPriority[] {LoaderPriority.LOWEST, LoaderPriority.LOW, LoaderPriority.NORMAL, LoaderPriority.HIGH, LoaderPriority.HIGHEST};
	static {
		for(LoaderPriority priority : priorities)
			dataLoaders.put(priority, new HashSet<>());
		
		//BUILT-IN LOADERS
		dataLoaders.get(LoaderPriority.LOW).add(() -> {return new ByteLoader();});
		dataLoaders.get(LoaderPriority.NORMAL).add(() -> {return new JsonLoader();});
		dataLoaders.get(LoaderPriority.NORMAL).add(() -> {return new PropertiesLoader();});
		dataLoaders.get(LoaderPriority.HIGH).add(() -> {return new YamlLoader();});
		dataLoaders.get(LoaderPriority.HIGHEST).add(() -> {return new EmptyLoader();});
	}
	
	public static void register(LoaderPriority priority, DataLoaderConstructor constructor) {
		dataLoaders.get(priority).add(constructor);
	}
	
	public void unregister(DataLoaderConstructor constructor) {
		LoaderPriority priority = null;
		for(Entry<LoaderPriority, Set<DataLoaderConstructor>> entry : dataLoaders.entrySet()) {
			if(entry.getValue().contains(constructor)) {
				priority = entry.getKey();
				break;
			}
		}
		if(priority!=null)
			dataLoaders.get(priority).remove(constructor);
	}
	
	//Does DataLoader have own loader from file?
	public abstract boolean loadingFromFile();
	
	public abstract Map<String, Object[]> get();

	public abstract void set(String key, Object[] value);

	public abstract void remove(String key);

	public abstract Collection<String> getHeader();

	public abstract Collection<String> getFooter();

	public abstract Set<String> getKeys();

	public abstract void reset();
	
	public abstract void load(String input);
	
	public abstract boolean isLoaded();
	
	public void load(File file) {
		if (file == null || !file.exists())
			return;
		load(StreamUtils.fromStream(file));
	}

	public static DataLoader findLoaderFor(File input) {
		String inputString = null;
		for(LoaderPriority priority : priorities) {
			for(DataLoaderConstructor constructor : dataLoaders.get(priority)) {
				DataLoader loader = constructor.construct();
				if(loader.loadingFromFile()) {
					loader.load(input);
				}else {
					if(inputString==null)inputString=StreamUtils.fromStream(input);
					loader.load(inputString);
				}
				if(loader.isLoaded())return loader;
			}
		}
		return null;
	}

	public static DataLoader findLoaderFor(String inputString) {
		for(LoaderPriority priority : priorities) {
			for(DataLoaderConstructor constructor : dataLoaders.get(priority)) {
				DataLoader loader = constructor.construct();
				loader.load(inputString);
				if(loader.isLoaded())return loader;
			}
		}
		return null;
	}
}
