package me.DevTec.TheAPI.ConfigAPI;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import me.DevTec.TheAPI.Utils.DataKeeper.Data;
import me.DevTec.TheAPI.Utils.DataKeeper.DataType;

public class Config {
    private Map<String, Object> defaults = Maps.newHashMap();
    private DataType t;
    private Data f;

    public Config(String path) {
    	this(path, DataType.YAML);
    }

    public Config(String path, DataType type) {
    	File file = new File("plugins/"+path);
    	if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
				file.createNewFile();
			} catch (Exception e) {
			}
        }
    	f=new Data(file);
    	t=type;
    	reload();
    }
    
    public DataType getType() {
    	return t;
    }
    
    public void save() {
        f.save(t);
    }

    public void addDefaults(Map<String, Object> values) {
    	for(String key : values.keySet())
    		addDefault(key, values.get(key));
    }

    public void addDefault(String key, Object value) {
    	defaults.put(key, value);
        if(!f.exists(key))
        	f.set(key, value);
    }
    
    public Map<String, Object> getDefaults(){
    	return defaults;
   }

    public void reload() {
    	File file = new File(f.getFile().getPath());
    	if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
				file.createNewFile();
			} catch (Exception e) {
			}
        }
    	f.reload(file);
    }

    public String getName() {
        return f.getFile().getName();
    }

    public boolean exists(String path) {
        return f.exists(path);
    }

    public void setComments(String path, List<String> value) {
    	f.setLines(path, value);
    }
    
    public void addComments(String path, List<String> value) {
    	f.getLines(path).addAll(value);
    }
	
    public void addComment(String path, String value) {
    	f.addLine(path, value);
    }
     
    public List<String> getComments(String path) {
    	return f.getLines(path);
    }
    
    public Section getSection(String path) {
    	return new Section(this,path);
    }

    /**
     * @see see Get List<Integer> from Path
     * @return List<Integer>
     */
    public List<Integer> getIntegerList(String path) {
    	return f.getList(path);
    }

    /**
     * @see see Get List<Byte> from Path
     * @return List<Byte>
     */
    public List<Byte> getByteList(String path) {
    	return f.getList(path);
    }

    /**
     * @see see Get List<Boolean> from Path
     * @return List<Boolean>
     */
    public List<Boolean> getBooleanList(String path) {
    	return f.getList(path);
    }

    /**
     * @see see Get List<Double> from Path
     * @return List<Double>
     */
    public List<Double> getDoubleList(String path) {
    	return f.getList(path);
    }

    /**
     * @see see Get List<Short> from Path
     * @return List<Short>
     */
    public List<Short> getShortList(String path) {
    	return f.getList(path);
    }

    /**
     * @see see Get List<Map<?,?>> from Path
     * @return List<Map<?,?>>
     */
    public List<Map<?,?>> getMapList(String path) {
    	return f.getList(path);
    }

    /**
     * @see see Get List<Float> from Path
     * @return List<Float>
     */
    public List<Float> getFloatList(String path) {
    	return f.getList(path);
    }

    /**
     * @see see Get List<Object> from Path
     * @return List<Object>
     */
    public List<Object> getList(String path) {
    	return f.getList(path);
    }

    /**
     * @see see Get List<String> from Path
     * @return List<String>
     */
    public List<String> getStringList(String path) {
    	return f.getStringList(path);
    }

    public Set<String> getKeys(String path) {
    	return f.getKeys(path);
    }

    public String getString(String path) {
    	return f.getString(path);
    }

    public void set(String path, Object value) {
    	f.set(path, value);
    }

    public boolean isString(String path) {
    	return get(path) instanceof String;
    }

    public boolean isDouble(String path) {
    	return f.get(path) instanceof Double;
    }

    public boolean isLong(String path) {
    	return f.get(path) instanceof Long;
    }

    public boolean isInt(String path) {
    	return f.get(path) instanceof Integer;
    }

    public boolean isBoolean(String path) {
    	return f.get(path) instanceof Boolean;
    }

    public boolean isFloat(String path) {
    	return f.get(path) instanceof Float;
    }

    public boolean isShort(String path) {
    	return f.get(path) instanceof Short;
    }

    public boolean isByte(String path) {
    	return f.get(path) instanceof Byte;
    }

    public Object get(String path) {
    	return f.get(path);
    }

    public int getInt(String path) {
    	return f.getInt(path);
    }

    public double getDouble(String path) {
    	return f.getDouble(path);
    }

    public long getLong(String path) {
    	return f.getLong(path);
    }

    public float getFloat(String path) {
    	return f.getFloat(path);
    }

    public boolean getBoolean(String path) {
    	return f.getBoolean(path);
    }

    public Set<String> getKeys() {
    	return f.getKeys();
    }
    
    public Data getData() {
    	return f;
    }
    
    public String toString() {
    	return "[Config:"+getName()+"/"+t.name()+"]";
    }
}
