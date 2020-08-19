package me.DevTec.Config;
import java.util.List;
import java.util.Map;
 
public class Section {
    private String[] s;
    private Config c;
    public Section(Config config, String section) {
        c=config;
        s=section.split("\\.");
    }
    
    public boolean exists() {
        return c.exists(getName());
    }
    
    public boolean exists(String path) {
        return c.exists(getName()+"."+path);
    }
    
    public void addComment(String comment) {
    	c.addComment(getName(), comment);
    }
    
    public List<String> getComments() {
    	return c.getComments(getName());
    }
    
    public List<String> getKeys() {
    	return c.getKeys(getName());
    }
    
    public void set(Object value) {
    	c.set(getName(), value);
    }
    
    public void createSection(String path) {
       set(getName()+"."+path, "");
    }
    
    public void set(String path,Object value) {
    	c.set(getName()+"."+path, value);
    }
    
    public void createSection() {
       set("");
    }

    public String getString() {
        return c.getString(getName());
    }
    
    public int getInt() {
    	return c.getInt(getName());
    }
    
    public double getDouble() {
    	return c.getDouble(getName());
    }
    
    public boolean isString() {
    	return c.isString(getName());
    }
    
    public boolean isDouble() {
    	return c.isDouble(getName());
    }
    
    public boolean isLong() {
    	return c.isLong(getName());
    }
    
    public boolean isInt() {
    	return c.isInt(getName());
    }
    
    public boolean isBoolean() {
    	return c.isBoolean(getName());
    }
    
    public boolean isFloat() {
    	return c.isFloat(getName());
    }
    
    public long getLong() {
    	return c.getLong(getName());
    }
    
    public float getFloat() {
    	return c.getFloat(getName());
    }
    
    public boolean getBoolean() {
    	return c.getBoolean(getName());
    }
    
    public List<String> getStringList() {
        return c.getStringList(getName());
    }
    
    public List<Integer> getIntegerList() {
        return c.getIntegerList(getName());
    }
    
    public List<Byte> getByteList() {
        return c.getByteList(getName());
    }
    
    public List<Boolean> getBooleanList() {
        return c.getBooleanList(getName());
    }
    
    public List<Double> getDoubleList() {
        return c.getDoubleList(getName());
    }
    
    public List<Short> getShortList() {
        return c.getShortList(getName());
    }
    
    public List<Map<?,?>> getMapList() {
        return c.getMapList(getName());
    }
    
    public List<Float> getFloatList() {
        return c.getFloatList(getName());
    }
    
    public List<Object> getList() {
        return c.getList(getName());
    }

    public String getString(String path) {
        return c.getString(getName()+"."+path);
    }
    
    public int getInt(String path) {
    	return c.getInt(getName()+"."+path);
    }
    
    public double getDouble(String path) {
    	return c.getDouble(getName()+"."+path);
    }
    
    public boolean isString(String path) {
    	return c.isString(getName()+"."+path);
    }
    
    public boolean isDouble(String path) {
    	return c.isDouble(getName()+"."+path);
    }
    
    public boolean isLong(String path) {
    	return c.isLong(getName()+"."+path);
    }
    
    public boolean isInt(String path) {
    	return c.isInt(getName()+"."+path);
    }
    
    public boolean isBoolean(String path) {
    	return c.isBoolean(getName()+"."+path);
    }
    
    public boolean isFloat(String path) {
    	return c.isFloat(getName()+"."+path);
    }
    
    public long getLong(String path) {
    	return c.getLong(getName()+"."+path);
    }
    
    public float getFloat(String path) {
    	return c.getFloat(getName()+"."+path);
    }
    
    public boolean getBoolean(String path) {
    	return c.getBoolean(getName()+"."+path);
    }
    
    public List<String> getStringList(String path) {
        return c.getStringList(getName()+"."+path);
    }
    
    public List<Integer> getIntegerList(String path) {
        return c.getIntegerList(getName()+"."+path);
    }
    
    public List<Byte> getByteList(String path) {
        return c.getByteList(getName()+"."+path);
    }
    
    public List<Boolean> getBooleanList(String path) {
        return c.getBooleanList(getName()+"."+path);
    }
    
    public List<Double> getDoubleList(String path) {
        return c.getDoubleList(getName()+"."+path);
    }
    
    public List<Short> getShortList(String path) {
        return c.getShortList(getName()+"."+path);
    }
    
    public List<Map<?,?>> getMapList(String path) {
        return c.getMapList(getName()+"."+path);
    }
    
    public List<Float> getFloatList(String path) {
        return c.getFloatList(getName()+"."+path);
    }
    
    public List<Object> getList(String path) {
        return c.getList(getName()+"."+path);
    }
    
    public Section getSection(String name) {
        return new Section(c, getName()+"."+name);
    }
    
    public Config getConfig() {
        return c;
    }
    
    public String getName() {
        String f =  "";
        for(String d : s)f+="."+d;
        return f.replaceFirst("\\.", "");
    }
    
    public String toString() {
        return "[Section:"+c.getFile().getName()+"/"+getName()+"]";
    }
}