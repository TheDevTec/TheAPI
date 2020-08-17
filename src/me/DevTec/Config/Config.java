package me.DevTec.Config;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
 
public class Config {
	protected static String quetos = ":", tag = "#", space = " ";
    private static final LinkedBlockingQueue<Action> queue = Queues.newLinkedBlockingQueue();
    private static Action current;
    protected static void notifyQueue() {
    	if(current!=null)return;
	    current = queue.poll();
	    if(current==null)return;
		String[] path = current.section().getName().split("\\.");
		int action = current.action();
		StringBuffer fs = new StringBuffer();
	    int idSekce = 0;
	    int foundAll = 0;
	    String sc = "";
	    for(int i = 1; i < path.length; ++i)
	     sc+=" ";
	    Object value = current.object();
	    IFile file = current.section().getConfig().getFile();
	    if(!current.section().exists())
	    if(value!=null && action==1 || action==0) {
	    	if(!current.section().getConfig().exists(path[0])) {
    			file.getContents().append(path[0]+":"+System.lineSeparator());
	    	}
    		while(true) {
            for (String s : file.getContents().toString().split(System.lineSeparator())) {
                if(foundAll==0) {
                    if (s.trim().split(":")[0].equals(path[idSekce])) {
                        fs.append(s+System.lineSeparator());
                        if(++idSekce == path.length) {
                            foundAll=1;
                            continue;
                        }
                        String ac = "", pathd="";
                        for(int i = 0; i < idSekce; ++i) {
                            ac+=" ";
                            pathd+="."+path[i];
                        }
                        pathd=pathd.replaceFirst("\\.", "")+"."+path[idSekce];
                        String d = ac+path[idSekce]+":";
                        if(!current.section().getConfig().exists(pathd)) {
                            fs.append(d+System.lineSeparator());
                        }
                        continue;
                }}
                fs.append(s+System.lineSeparator());
            }
            foundAll=0;
            idSekce=0;
            file.setContents(fs);
            fs=new StringBuffer();
    	    if(current.section().exists())break;
    		}
	    }
	    if(action==0) { //add comment
	        for (String s : file.getContents().toString().split(System.lineSeparator())) {
		        if(foundAll==0)
		        if (s.trim().split(":")[0].equals(path[idSekce]))
		            if(++idSekce==path.length) {
		                foundAll=1;
			            fs.append(sc+"# "+ value+System.lineSeparator()+s+System.lineSeparator());
		                continue;
		            }
	            fs.append(s+System.lineSeparator());
		    }
	    }
	    if(action==1) { //set or remove path & value
	            if(value!=null && value instanceof List) {
	            	int i = 0;
	                for (String s : file.getContents().toString().split(System.lineSeparator())) {
	                    if (s.trim().startsWith("#") || s.trim().isEmpty()) {
	                        fs.append(s+System.lineSeparator());
	                        continue;
	                    }
	                    if(foundAll==0) {
	                        if (s.trim().split(":")[0].equals(path[idSekce]))
	                            if(++idSekce == path.length) {
	                            foundAll=1;
	                            fs.append(s+System.lineSeparator());
	                            for(Object sdf : (List<?>)value) {
	                            	if(sdf instanceof Number || sdf instanceof String || sdf instanceof Boolean)
		                            fs.append(sc+"- "+sdf+System.lineSeparator());
	                            	else {
	                            		try {
	                            			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	                        				BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
	                        				dataOutput.writeObject(sdf);
	                        				dataOutput.close();
			                            	fs.append(sc+"- "+Base64Coder.encodeLines(outputStream.toByteArray())+System.lineSeparator());
	                            		}catch(Exception e) {
	    		                            fs.append(sc+"- "+sdf+System.lineSeparator());
	                            	}}}
	                            continue;
	                            }
	                    }else if(i==0 && s.contains("-") && s.split("-")[1].startsWith(" "))continue; //list item¨
	                    else i=1;
	                    fs.append(s+System.lineSeparator());
	                }
	            }else {
	        boolean removingMode = false;
	    	for (String s : file.getContents().toString().split(System.lineSeparator())) {
	            if (s.trim().startsWith("#") || s.trim().isEmpty()) {
	            	fs.append(s+System.lineSeparator());
	                continue;
	            }
	            if(foundAll==0) {
	                if (s.trim().split(":")[0].equals(path[idSekce]))
	                    if(++idSekce == path.length) {
	                        foundAll=1;
	                        if(value==null)
	                        removingMode=true;
	                        if(value!=null) {
	                        	if(value instanceof Number || value instanceof String || value instanceof Boolean)
		                            fs.append(s.split(":")[0]+": "+value+System.lineSeparator());
	                            	else {
	                            		try {
	                            			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	                        				BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
	                        				dataOutput.writeObject(value);
	                        				dataOutput.close();
			                            	fs.append(s.split(":")[0]+": "+Base64Coder.encodeLines(outputStream.toByteArray())+System.lineSeparator());
	                            		}catch(Exception e) {
	    		                            fs.append(s.split(":")[0]+": "+value+System.lineSeparator());
	                            	}}
	                        }
	                        continue;
	                    }
	            }else if(removingMode) {
	                    int count = 0;
	                    for(char c : s.split(quetos)[0].toCharArray())
	                        if(' '==c)++count;
	                        else break;
	                    if(count >= path.length-1)continue; //Remove path
	            	}else removingMode=false;
	            fs.append(s+System.lineSeparator());
	    }}}
	    file.setContents(fs);
	    current=null;
	    notifyQueue();
    }
    
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
    
    public Config(InputStream stream) { //only reading
        this.f=new IFile(stream);
    }

    /**
     * @see see Add default to config
     */
    public void addDefault(Key key, Object value) {
    	defaults.put(key, value);
    	if(!exists(key.getName())) {
    		set(key.getName(), value);
    		if(key.getComment()!=null)
    		addComment(key.getName(), key.getComment());
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
    		addComment(s.getName(), s.getComment());
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
    
    protected void addAction(Action toFinish) {
    	queue.add(toFinish);
    	notifyQueue();
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) continue;
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
    public void set(String path, Object value) {
    	addAction(new Action( getSection(path), 1, value));
    }
    
    /**
     * @see see Set in config to section, value (If section doesn't exist, config create section)
     */
    public void addComment(String path, String value) {
    	addAction(new Action(getSection(path), 0, value));
    }
    
    
    public List<String> getComments(String path) {
    	String[] w = path.split("\\.");
    	List<String> found = Lists.newArrayList();
    	 int idSekce = 0;
         for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
             if (s.trim().isEmpty()) continue;
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
        String[] sections = path.split("\\.");
        int idSekce = 0;
        boolean foundAll = false;

        for (String s : f.getContents().toString().split(System.lineSeparator())) {
        	 if (s.contains("-") && s.split("-")[1].startsWith(" ") || s.trim().startsWith(tag) || s.trim().isEmpty())continue;
             if (!foundAll) {
            	 if(idSekce!=0) {
            		 int count = 0;
                     for(char c : s.toCharArray())
                         if(' '==c)++count;
                         else break;
                     if(idSekce > count)
                     if (!s.trim().split(":")[0].equals(sections[idSekce]))break;
            	 }
                if (s.trim().split(":")[0].equals(sections[idSekce])) {
                    if (++idSekce == sections.length) {
                        foundAll = true;
                    }
                }
            }
        }
        return foundAll;
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
        try {
            Boolean.parseBoolean(getString(path));
            return true;
        }catch(Exception e) {
            return false;
        }
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) continue;
            if (s.trim().split(":")[0].equals(d[idSekce])) {
             	if(f.equals("")||f.trim().equals(d[idSekce])||c(f)>=idSekce-1) {
                 	f=s.split(":")[0];
                 if(++idSekce==d.length) {
                     g=s.split(quetos+" ")[1].split(tag)[0];
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) 
            	continue;
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
                   g.add(s.split("- ")[1].split(tag)[0]);
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) 
            	continue;
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
                   g.add(Integer.parseInt(s.split("- ")[1].split(tag)[0]));
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) 
            	continue;
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
                   g.add(Byte.parseByte(s.split("- ")[1].split(tag)[0]));
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) 
            	continue;
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
                   g.add(Boolean.parseBoolean(s.split("- ")[1].split(tag)[0]));
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) 
            	continue;
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
                   g.add(Double.parseDouble(s.split("- ")[1].split(tag)[0]));
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) 
            	continue;
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
                   g.add(Short.parseShort(s.split("- ")[1].split(tag)[0]));
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) 
            	continue;
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
                   g.add(Float.parseFloat(s.split("- ")[1].split(tag)[0]));
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
        for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
            if (s.trim().startsWith(tag) || s.trim().isEmpty()) 
            	continue;
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
            			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(object));
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
    	if(!getSection(s.getName()).exists())
    		set(s.getName(), defaults.get(s));
    }
}