package me.DevTec.Config;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
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
    
    public void addComment(String comment) {
    	c.addComment(getName(), comment);
    }
    
    public List<String> getComments() {
    	return c.getComments(getName());
    }
    
    public Set<String> getKeys() {
    	return c.getKeys(getName());
    }
    
    public void set(Object value) {
    	c.set(getName(), value);
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