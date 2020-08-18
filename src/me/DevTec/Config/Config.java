package me.DevTec.Config;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import me.DevTec.Other.StringUtils;
 
public class Config {
	protected static String quetos = ":", tag = "#", space = " ";
    
    private final HashMap<Key, Object> defaults = Maps.newHashMap();
    private final IFile f;
    public Config(String path) {
    	File f = new File("plugins/"+path);
        f.getParentFile().mkdir();
        if(!f.exists())
        try {
           f.createNewFile();
        }catch (Exception e) {}
        this.f=new IFile(f);
    }

    /**
     * @see see Add default to config
     */
    public void addDefault(Key key, Object value) {
    	defaults.put(key, value);
    	if(!exists(key.getName())) {
    		set(key.getName(), value);
    		for(String c : key.getComment())
    		addComment(key.getName(), c);
    	}
    }
    
    /**
     * @see see Add defaults to config
     */
    public void addDefaults(HashMap<Key, Object> values) {
    	defaults.putAll(values);
    	for(Key s : values.keySet())
    	if(!exists(s.getName())) {
    		set(s.getName(), values.get(s));
    		for(String c : s.getComment())
    		addComment(s.getName(), c);
    	}
    }

    /**
     * @see see Add default to config
     */
    public void addDefault(String key, Object value) {
    	addDefault(new Key(key), value);
    }
    
    /**
     * @return HashMap<Key, Object>
     */
    public HashMap<Key, Object> getDefaults(){
    	return defaults;
    }

    /**
     * @see see Save whole config
     */
    public void save() {
    	f.save();
    }

    /**
     * @return IFile
     */
    public IFile getFile() {
        return f;
    }

    /**
     * @return Section
     */
    public Section getSection(String name) {
    	return new Section(this, name);
    }

    public Set<String> getKeys(String path) {
    	String[] w= path.split("\\.");
    	HashSet<String> fs = Sets.newHashSet();
        int idSekce = 0, foundAll = path.trim().isEmpty()?1:0;
        for (String s : getFile().getContents()) {
            if(foundAll==0) {
            if (s.trim().split(quetos)[0].equals(w[idSekce]))
                if(++idSekce==w.length)
                    foundAll=1;
            }else {
                int count = 0;
                for(char c : s.split(quetos)[0].toCharArray())
                    if(' '==c)++count;
                    else break;
                if(count==(path.trim().isEmpty()?0:w.length))
                	fs.add(s.trim().split(quetos)[0]);
            }
        }
        return fs;
    }

    /**
     * @return Set<String> Keys in config
     */
    public Set<String> getKeys() {
        return getKeys("");
    }
    
    /**
     * @see see Set in config to section, value (If section doesn't exist, config create section)
     */
    public void addComment(String paths, String value) {
    	int idSekce = 0,foundAll = 0;
		String[] path = paths.split("\\.");
		StringBuffer fs = new StringBuffer();
        for (String s : getFile().getContents()) {
	        if(foundAll==0)
	        if (s.trim().split(":")[0].equals(path[idSekce]))
	            if(++idSekce==path.length) {
	                foundAll=1;
	        	    String sc = "";
	        	    for(int i = 1; i < path.length; ++i)sc+=" ";
		            fs.append(sc+"# "+ value+System.lineSeparator()+s+System.lineSeparator());
	                continue;
	            }
            fs.append(s+System.lineSeparator());
	    }
        getFile().setContents(fs);
    }
    
    
    public List<String> getComments(String path) {
    	String[] w = path.split("\\.");
    	List<String> found = Lists.newArrayList();
    	 int idSekce = 0;
         for (String s : getFile().getContents()) {
             if(s.trim().startsWith(tag))found.add(s.split(tag)[1].replaceFirst(" ", ""));
             if (s.trim().split(quetos)[0].equals(w[idSekce])) {
                 if(++idSekce==w.length)break; //stop finding
             }
             found.clear();
         }
    	return found;
    }
    
    /**
     * @see see Return true if section exists
     */
    public boolean exists(String path) {
    	return exists(f.getContents(),path.split("\\."));
    }

    /**
     * @see see Return true if value in section is String
     */
    public boolean isString(String path) {
        return getString(path)!=null;
    }

    /**
     * @see see Return true if value in section is Double
     */
    public boolean isDouble(String path) {
        try {
            Double.parseDouble(getString(path));
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    /**
     * @see see Return true if value in section is Long
     */
    public boolean isLong(String path) {
        try {
            Long.parseLong(getString(path));
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    /**
     * @see see Return true if value in section is Integer
     */
    public boolean isInt(String path) {
        try {
            Integer.parseInt(getString(path));
            return true;
        }catch(Exception e) {
            return false;
        }
    }

    /**
     * @see see Return true if value in section is Boolean
     */
    public boolean isBoolean(String path) {
    	String a = getString(path);
    	if(a==null)return false;
		return a.equalsIgnoreCase("true")||a.equalsIgnoreCase("false");
    }

    /**
     * @see see Return true if value in section is Float
     */
    public boolean isFloat(String path) {
        try {
            Float.parseFloat(getString(path));
            return true;
        }catch(Exception e) {
            return false;
        }
    }
    
    /**
     * @see see Get String from Path
     * @return String
     */
    public String getString(String path) {
    	String[] d = path.split("\\.");
        String g = null;
        int idSekce = 0;
        String f = "";
        if(exists(path))
            for (String s : getFile().getContents()) {
            if (s.trim().split(":")[0].equals(d[idSekce])) {
             	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                 	f=s.split(":")[0];
                 if(++idSekce==d.length) {
                     g=removeQuetos(s.split(quetos+" ")[1].split(tag)[0]);
                     break;
                 }
             	}
             }else 
             	f=s.split(":")[0];
        }
        return g;
    }

    /**
     * @see see Get Int from Path
     * @return int
     */
    public int getInt(String path) {
        String s = getString(path);
        if(s==null)return 0;
        try {
        return Integer.parseInt(s);
        }catch(Exception e) {
            return 0;
        }
    }
    
    /**
     * @see see Get Double from Path
     * @return double
     */
    public double getDouble(String path) {
        String s = getString(path);
        if(s==null)return 0.0;
        try {
        return Double.parseDouble(s);
        }catch(Exception e) {
            return 0.0;
        }
    }

    /**
     * @see see Get Long from Path
     * @return long
     */
    public long getLong(String path) {
        String s = getString(path);
        if(s==null)return 0;
        try {
        return Long.parseLong(s);
        }catch(Exception e) {
            return 0L;
        }
    }

    /**
     * @see see Get Float from Path
     * @return float
     */
    public float getFloat(String path) {
        String s = getString(path);
        if(s==null)return 0;
        try {
        return Float.parseFloat(s);
        }catch(Exception e) {
            return 0F;
        }
    }

    /**
     * @see see Get Boolean from Path
     * @return boolean
     */
    public boolean getBoolean(String path) {
        String s = getString(path);
        if(s==null)return false;
        try {
        return Boolean.parseBoolean(s);
        }catch(Exception e) {
            return false;
        }
    }

    /**
     * @see see Get List<String> from Path
     * @return List<String>
     */
    public List<String> getStringList(String path) {
    	String[] d = path.split("\\.");
        List<String> g = Lists.newArrayList();
        String f = "";
        int idSekce = 0;
        int foundAll = 0;
        if(exists(path))
            for (String s : getFile().getContents()) {
            if(foundAll==0) {
            if (s.trim().split(":")[0].equals(d[idSekce])) {
            	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                	f=s.split(":")[0];
                if(++idSekce==d.length)
                	foundAll=1;
            	}
            }else 
            	f=s.split(":")[0];
            }else {
                if(s.split("-").length>=2 && s.split("-")[1].startsWith(" ")) {
                   g.add(removeQuetos(s.split("- ")[1].split(tag)[0]));
                }else break;
            }
        }
        return g;
    }

    private static int c(String s) {
    	int i = 0;
    	for(char c : s.toCharArray())
    		if(c==' ')++i;
    		else break;
    	return i;
    }
    
    /**
     * @see see Get List<Integer> from Path
     * @return List<Integer>
     */
    public List<Integer> getIntegerList(String path) {
    	String[] d = path.split("\\.");
        List<Integer> g = Lists.newArrayList();
        int idSekce = 0;
        int foundAll = 0;
        String f = "";
        if(exists(path))
            for (String s : getFile().getContents()) {
            if(foundAll==0) {
            	 if (s.trim().split(":")[0].equals(d[idSekce])) {
                 	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                     	f=s.split(":")[0];
                     if(++idSekce==d.length)
                     	foundAll=1;
                 	}
                 }else 
                 	f=s.split(":")[0];
            }else {
                if(s.split("-").length>=2 && s.split("-")[1].startsWith(" ")) {
                	try {
                   g.add(Integer.parseInt(removeQuetos(s.split("- ")[1].split(tag)[0])));
                	}catch(Exception e) {
                        g.add(0);
                	}
                }else break;
            }
        }
        return g;
    }

    /**
     * @see see Get List<Byte> from Path
     * @return List<Byte>
     */
    public List<Byte> getByteList(String path) {
    	String[] d = path.split("\\.");
        List<Byte> g = Lists.newArrayList();
        int idSekce = 0;
        String f = "";
        int foundAll = 0;
        if(exists(path))
            for (String s : getFile().getContents()) {
            if(foundAll==0) {
            	if (s.trim().split(":")[0].equals(d[idSekce])) {
                	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                     	f=s.split(":")[0];
                     if(++idSekce==d.length)
                     	foundAll=1;
                 	}
                 }else 
                 	f=s.split(":")[0];
            }else {
                if(s.split("-").length>=2 && s.split("-")[1].startsWith(" ")) {
                	try {
                   g.add(Byte.parseByte(removeQuetos(s.split("- ")[1].split(tag)[0])));
                	}catch(Exception e) {
                        g.add((byte)0);
                	}
                }else break;
            }
        }
        return g;
    }

    /**
     * @see see Get List<Boolean> from Path
     * @return List<Boolean>
     */
    public List<Boolean> getBooleanList(String path) {
    	String[] d = path.split("\\.");
        List<Boolean> g = Lists.newArrayList();
        int idSekce = 0;
        String f= "";
        int foundAll = 0;
        if(exists(path))
            for (String s : getFile().getContents()) {
            if(foundAll==0) {
            	if (s.trim().split(":")[0].equals(d[idSekce])) {
                	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                     	f=s.split(":")[0];
                     if(++idSekce==d.length)
                     	foundAll=1;
                 	}
                 }else 
                 	f=s.split(":")[0];
            }else {
                if(s.split("-").length>=2 && s.split("-")[1].startsWith(" ")) {
                	try {
                   g.add(Boolean.parseBoolean(removeQuetos(s.split("- ")[1].split(tag)[0])));
                	}catch(Exception e) {
                        g.add(false);
                	}
                }else break;
            }
        }
        return g;
    }

    /**
     * @see see Get List<Double> from Path
     * @return List<Double>
     */
    public List<Double> getDoubleList(String path) {
    	String[] d = path.split("\\.");
        List<Double> g = Lists.newArrayList();
        int idSekce = 0;
        int foundAll = 0;
        String f="";
        if(exists(path))
            for (String s : getFile().getContents()) {
            if(foundAll==0) {
            	if (s.trim().split(":")[0].equals(d[idSekce])) {
                	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                     	f=s.split(":")[0];
                     if(++idSekce==d.length)
                     	foundAll=1;
                 	}
                 }else 
                 	f=s.split(":")[0];
            }else {
                if(s.split("-").length>=2 && s.split("-")[1].startsWith(" ")) {
                	try {
                   g.add(Double.parseDouble(removeQuetos(s.split("- ")[1].split(tag)[0])));
                	}catch(Exception e) {
                        g.add(0D);
                	}
                }else break;
            }
        }
        return g;
    }

    /**
     * @see see Get List<Short> from Path
     * @return List<Short>
     */
    public List<Short> getShortList(String path) {
    	String[] d = path.split("\\.");
        List<Short> g = Lists.newArrayList();
        int idSekce = 0;
        int foundAll = 0;
        String f= "";
        if(exists(path))
            for (String s : getFile().getContents()) {
            if(foundAll==0) {
            	if (s.trim().split(":")[0].equals(d[idSekce])) {
                	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                     	f=s.split(":")[0];
                     if(++idSekce==d.length)
                     	foundAll=1;
                 	}
                 }else 
                 	f=s.split(":")[0];
            }else {
                if(s.split("-").length>=2 && s.split("-")[1].startsWith(" ")) {
                	try {
                   g.add(Short.parseShort(removeQuetos(s.split("- ")[1].split(tag)[0])));
                	}catch(Exception e) {
                        g.add((short)0);
                	}
                }else break;
            }
        }
        return g;
    }

    /**
     * @see see Get List<Map<?,?>> from Path
     * @return List<Map<?,?>>
     */
    public List<Map<?,?>> getMapList(String path) {
    	List<Map<?,?>> maps = Lists.newArrayList();
    	for(Object o : getList(path)) {
    		try {
    		maps.add((Map<?,?>)o);
    		}catch(Exception not) {
    			break;
    		}
    	}
    	return maps;
    }

    /**
     * @see see Get List<Float> from Path
     * @return List<Float>
     */
    public List<Float> getFloatList(String path) {
    	String[] d = path.split("\\.");
        List<Float> g = Lists.newArrayList();
        int idSekce = 0;
        String f = "";
        int foundAll = 0;
        if(exists(path))
            for (String s : getFile().getContents()) {
            if(foundAll==0) {
            	if (s.trim().split(":")[0].equals(d[idSekce])) {
                	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                     	f=s.split(":")[0];
                     if(++idSekce==d.length)
                     	foundAll=1;
                 	}
                 }else 
                 	f=s.split(":")[0];
            }else {
                if(s.split("-").length>=2 && s.split("-")[1].startsWith(" ")) {
                	try {
                   g.add(Float.parseFloat(removeQuetos(s.split("- ")[1].split(tag)[0])));
                	}catch(Exception e) {
                        g.add(0F);
                	}
                }else break;
            }
        }
        return g;
    }

    /**
     * @see see Get List<Object> from Path
     * @return List<Object>
     */
    public List<Object> getList(String path) {
    	String[] d = path.split("\\.");
        List<Object> g = Lists.newArrayList();
        int idSekce = 0;
        String f = "";
        int foundAll = 0;
        if(exists(path))
            for (String s : getFile().getContents()) {
            if(foundAll==0) {
            	if (s.trim().split(":")[0].equals(d[idSekce])) {
                	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                     	f=s.split(":")[0];
                     if(++idSekce==d.length)
                     	foundAll=1;
                 	}
                 }else 
                 	f=s.split(":")[0];
            }else {
                if(s.split("-").length>=2 && s.split("-")[1].startsWith(" ")) {
                	String object = s.split("- ")[1].split(tag)[0];
                	try {
            			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(removeQuetos(object)));
            			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                	try {
            			while (true) {
                        	try {
            				g.add(dataInput.readObject());
                        	}catch(Exception reading2) {
                        		break;
                        	}
            			}
            			dataInput.close();
                	}catch(Exception reading) {}
                	}catch(Exception e) {
                	g.add(object);
                	}
                }else break;
            }
        }
        return g;
    }
    
    /**
     * @see see Reopen file & restore defaults
     */
    public void reload() {
        f.close();
        f.open();
    	for(Key s : defaults.keySet())
    	if(!exists(s.getName()))
    		set(s.getName(), defaults.get(s));
    }

    /**
     * @see see Set in config to section, value (If section doesn't exist, config create section)
     */
	public void set(String path, Object object) {
		if(object==null) {
			remove(f.getContents(),path.split("\\."));
			return;
		}
		f.setContents(write(path.split("\\."), object));
	}
	
	private synchronized void create(String[] path) {
		StringBuffer bb = new StringBuffer();
		for(String s : getFile().getContents())
			bb.append(s+System.lineSeparator());
		int first = 0;
		String paths="";
		if(!exists(bb.toString().split(System.lineSeparator()),path[0].split("\\.")))
			bb.append(path[0]+":"+System.lineSeparator());
		for(String a : path) {
			paths+="."+a;
            if(first==0) {
            	first=1;
            	paths=paths.substring(1);
            }
			bb=write(bb.toString().split(System.lineSeparator()),paths.split("\\."));
		}
		getFile().setContents(bb);
	}
	
    private synchronized boolean exists(String[] map, String[] d) {
        int idSekce = 0;
        synchronized(this) {
        for (String s:map) {
        	boolean a = s.trim().split(":")[0].equals(d[idSekce]);
        	 if(idSekce!=0)
                 if(idSekce!=0&&!a&&idSekce>c(s))break;
        	if(a)if(++idSekce == d.length)break;
        }}
        return idSekce == d.length;
    }
	
    private synchronized StringBuffer write(String[] map, String[] d) {
		int idSekce=0;
		StringBuffer fs=new StringBuffer();
	    for (String s : map) {
             fs.append(s+System.lineSeparator());
             if(idSekce!=d.length) {
	        if (s.trim().split(":")[0].equals(d[idSekce])) {
	           if(++idSekce==d.length)
	              continue;
	           String ac = "", pathd="";
               for(int i = 0; i < idSekce; ++i) {
                   ac+=" ";
                   pathd+="."+d[i];
               }
               pathd=pathd.substring(1)+"."+d[idSekce];
               if(!exists(map,pathd.split("\\.")))
                   	fs.append(ac+d[idSekce]+":"+System.lineSeparator());
        }}}
	    return fs;
	}
    
    private synchronized void remove(String[] map, String[] d) {
		int idSekce=0,r=0;
		StringBuffer fs=new StringBuffer();
	    for (String s : map) {
	         if(idSekce!=d.length) {
	         if (s.trim().split(":")[0].equals(d[idSekce])) {
	           if(++idSekce==d.length) {
	              r=1;
	              continue;
	            }
	         }
	         }else if(r==1) {
	        	 if(s.split("-").length>=2 && s.split("-")[1].startsWith(" "))continue;
	        	 else r=0;
	         }
	         fs.append(s+System.lineSeparator());
	    }
		f.setContents(fs);
	}
    
    private synchronized StringBuffer write(String[] d, Object o) {
		if(!exists(f.getContents(),d))
		create(d);
    	String[] map = f.getContents();
		int idSekce=0;
        synchronized(this) {
		StringBuffer fs=new StringBuffer();
		if(o instanceof List) {
			int r=0;
	    for (String s : map) {
	         if(idSekce!=d.length) {
	         if (s.trim().split(":")[0].equals(d[idSekce])) {
	           if(++idSekce==d.length) {
	              r=1;
                  fs.append(s+System.lineSeparator());
	              String sc = "";
	      	      for(int i = 1; i < d.length; ++i)
	      	      sc+=" ";
            	  for(Object sdf : (List<?>)o) {
                  	if(sdf instanceof Number || sdf instanceof String || sdf instanceof Boolean)
                      fs.append(sc+"- "+addQuetos(sdf+"")+System.lineSeparator());
                  	else {
                  		try {
                  			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
              				BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
              				dataOutput.writeObject(sdf);
              				dataOutput.close();
                            fs.append(sc+"- "+addQuetos(Base64Coder.encodeLines(outputStream.toByteArray()))+System.lineSeparator());
                  		}catch(Exception e) {
	                        fs.append(sc+"- "+addQuetos(sdf+System.lineSeparator()));
                  	}}}
	              continue;
	            }
	         }
	         }else if(r==1) {
	        	 if(s.split("-").length>=2 && s.split("-")[1].startsWith(" "))continue;
	        	 else r=0;
	         }
	         fs.append(s+System.lineSeparator());
	    }
	    return fs;
		}
		for (String s : map) {
	         if(idSekce!=d.length)
	         if (s.trim().split(":")[0].equals(d[idSekce]))
	           if(++idSekce==d.length)
	        	   if(o instanceof Number || o instanceof String || o instanceof Boolean) {
	                      fs.append(s.split(":")[0]+": "+addQuetos(""+o)+System.lineSeparator());
	    	              continue;
		              }else {
                  		try {
                  			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
              				BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
              				dataOutput.writeObject(o);
              				dataOutput.close();
                            fs.append(s.split(":")[0]+": "+addQuetos(Base64Coder.encodeLines(outputStream.toByteArray()))+System.lineSeparator());
                  		}catch(Exception e) {
	                        fs.append(s.split(":")[0]+": "+addQuetos(""+o)+System.lineSeparator());
                  }
	              continue;
	            }
	         fs.append(s+System.lineSeparator());
	    }
	    return fs;
	    }
	}
    
    private String removeQuetos(String value) {
    	if(value.startsWith("\"")&&value.endsWith("\"")) {
    		return value.substring(1, value.length()-1);
    	}
    	if(value.startsWith("'")&&value.endsWith("'")) {
    		return value.substring(1, value.length()-1);
    	}
    	return value;
    }
    
    private String addQuetos(String value) {
    	if(!value.startsWith("\"")&&!value.endsWith("\"")&&!value.startsWith("'")&&!value.endsWith("'")) {
        	if(StringUtils.isNumber(value))
        		return "'"+value+"'";
        	if(StringUtils.isBoolean(value))
        		return value;
        	if(StringUtils.containsSpecial(value)||value.length()>=16)
        		return "\""+value+"\"";
    		return value;
    	}
    	return value;
    }
    
}