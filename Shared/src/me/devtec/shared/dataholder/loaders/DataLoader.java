package me.devtec.shared.dataholder.loaders;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.devtec.shared.dataholder.loaders.constructor.DataLoaderConstructor;
import me.devtec.shared.dataholder.loaders.constructor.LoaderPriority;
import me.devtec.shared.utility.StreamUtils;

public abstract class DataLoader {
	private DataLoaderConstructor constructor;
	private LoaderPriority priority;
	
	//Data loaders hierarchy
	public static Map<LoaderPriority, Set<DataLoaderConstructor>> dataLoaders = new HashMap<>();
	static {
		for(LoaderPriority priority : LoaderPriority.values())
			dataLoaders.put(priority, new HashSet<>());
		
		//BUILT-IN LOADERS
		dataLoaders.get(LoaderPriority.LOW).add(() -> {return new ByteLoader();});
		dataLoaders.get(LoaderPriority.NORMAL).add(() -> {return new JsonLoader();});
		dataLoaders.get(LoaderPriority.NORMAL).add(() -> {return new PropertiesLoader();});
		dataLoaders.get(LoaderPriority.HIGH).add(() -> {return new YamlLoader();});
		dataLoaders.get(LoaderPriority.HIGHEST).add(() -> {return new EmptyLoader();});
	}
	
	public void register(LoaderPriority priority, DataLoaderConstructor constructor) {
		if(constructor==null)return;
		
		this.constructor=constructor;
		dataLoaders.get(priority).add(constructor);
	}
	
	public void unregister() {
		if(priority!=null)
			dataLoaders.get(priority).remove(constructor);
	}
	
	public DataLoaderConstructor getConstructor() {
		return constructor;
	}
	
	public LoaderPriority getPriority() {
		return priority;
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
		for(Set<DataLoaderConstructor> constructors : dataLoaders.values()) {
			for(DataLoaderConstructor constructor : constructors) {
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
}
