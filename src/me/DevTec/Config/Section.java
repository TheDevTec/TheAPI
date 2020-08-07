package me.DevTec.Config;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
 
public class Section {
	private static String quetos = ":", tag = "#";
    private String[] s;
    private Config c;
    public Section(Config config, String section) {
        c=config;
        s=section.split("\\.");
    }
    
    public boolean exists() {
        int idSekce = 0;
        int foundAll = 0;
        for (String s : c.getFile().getContents().toString().split(System.lineSeparator()))
            if (s.trim().split(quetos)[0].equals(this.s[idSekce])) 
                    if(++idSekce == this.s.length) {
                        foundAll=1;
                        break;
                    }
        return foundAll==1;
    }
    
    public void addComment(String comment) {
        c.addAction(new Action(this, "c", comment));
    }
    
    public Set<String> getKeys() {
    	HashSet<String> fs = Sets.newHashSet();
        int idSekce = 0, foundAll = 0;
        for (String s : c.getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) continue;
            if(foundAll==0) {
            if (s.trim().split(quetos)[0].equals(this.s[idSekce]))
                if(++idSekce==this.s.length)
                    foundAll=1;
            }else {
                int count = 0;
                for(char c : s.split(quetos)[0].toCharArray())
                    if(' '==c)++count;
                    else {
                        if(count==this.s.length && s.split(":")[1].startsWith(" "))
                        	fs.add(s.trim().split(quetos)[0]);
                    	break;
                    }
            }
        }
        return fs;
    }
    
    public void set(Object value) {
    	c.addAction(new Action(this, "s", value));
    }
    
    public void createSection() {
       set("");
    }
    
    private static String space = " ";

    public String getString() {
        StringBuffer g = new StringBuffer();
        int idSekce = 0;
        for (String s : c.getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) continue;
        	if(idSekce!=this.s.length) {
            if (s.trim().split(quetos)[0].equals(this.s[idSekce]))
                if(++idSekce==this.s.length)
                    g.append((s.split(quetos)[1].replaceFirst(space, "")).split(tag)[0]);
            }else {
                if(s.startsWith(space)||s.startsWith("- ")||s.contains(quetos) && s.split(quetos).length>=2 && s.split(quetos)[1].startsWith(space))break;
                int count = 0;
                for(char c : s.toCharArray())
                    if(' '==c)++count;
                    else {
                    	break;
            }
        if(count==0)
        g.append(s.split(tag)[0]);
        else break;
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
        String[] text = c.getFile().getContents().toString().split(System.lineSeparator());
        int idSekce = 0;
        List<String> g = Lists.newArrayList();
        boolean foundAll = false;
        for (String s : text) {
            if (s.trim().startsWith("#") || s.trim().isEmpty()) continue;
            if(!foundAll) {
            if (s.trim().split(":")[0].equals(this.s[idSekce]))
                if(++idSekce==this.s.length)foundAll=true;
            }else {
                if(s.trim().startsWith("-")) {
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