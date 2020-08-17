package me.DevTec.Config;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
 
public class Section {
	protected static String quetos = ":", tag = "#", space = " ";
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
        StringBuffer g = new StringBuffer();
        int idSekce = 0;
        for (String s : c.getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) continue;
        	if(idSekce!=this.s.length) {
            if (s.trim().split(quetos)[0].equals(this.s[idSekce]))
                if(++idSekce==this.s.length)
                    g.append((s.split(quetos)[1].replaceFirst(space, "")).split(tag)[0]);
            }
        }
        try {
        return g.toString();
        }catch(Exception e) {
        	return null;
        }
    }
    
    public int getInt() {
        String s = getString();
        if(s==null)return 0;
        try {
        return Integer.parseInt(s);
        }catch(Exception e) {
            return 0;
        }
    }
    
    public double getDouble() {
        String s = getString();
        if(s==null)return 0.0;
        try {
        return Double.parseDouble(s);
        }catch(Exception e) {
            return 0.0;
        }
    }
    
    public boolean isString() {
        return getString()!=null;
    }
    
    public boolean isDouble() {
        try {
            Double.parseDouble(getString());
            return true;
        }catch(Exception e) {
            return false;
        }
    }
    
    public boolean isLong() {
        try {
            Long.parseLong(getString());
            return true;
        }catch(Exception e) {
            return false;
        }
    }
    
    public boolean isInt() {
        try {
            Integer.parseInt(getString());
            return true;
        }catch(Exception e) {
            return false;
        }
    }
    
    public boolean isBoolean() {
        try {
            Boolean.parseBoolean(getString());
            return true;
        }catch(Exception e) {
            return false;
        }
    }
    
    public boolean isFloat() {
        try {
            Float.parseFloat(getString());
            return true;
        }catch(Exception e) {
            return false;
        }
    }
    
    public long getLong() {
        String s = getString();
        if(s==null)return 0;
        try {
        return Long.parseLong(s);
        }catch(Exception e) {
            return 0L;
        }
    }
    
    public float getFloat() {
        String s = getString();
        if(s==null)return 0;
        try {
        return Float.parseFloat(s);
        }catch(Exception e) {
            return 0F;
        }
    }
    
    public boolean getBoolean() {
        String s = getString();
        if(s==null)return false;
        try {
        return Boolean.parseBoolean(s);
        }catch(Exception e) {
            return false;
        }
    }
    
    public List<String> getList() {
        List<String> g = Lists.newArrayList();
        int idSekce = 0;
        int foundAll = 0;
        for (String s : c.getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith("#") || s.trim().isEmpty()) 
            	continue;
            if(foundAll==0) {
            if (s.trim().split(":")[0].equals(this.s[idSekce]))
                if(++idSekce==this.s.length)
                	foundAll=1;
            }else {
                if(s.contains("-") && s.split("-").length>=2 && s.split("-")[1].equals(" ")) {
                    g.add(s.split("- ")[1]);
                }else break;
                
            }
        }
        return g;
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