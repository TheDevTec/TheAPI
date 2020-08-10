package me.DevTec.Config;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
 
public class Config {
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
	    if(!current.section().getConfig().exists(current.section().getName()))
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
                        if(!current.section().getConfig().exists(pathd))
                            fs.append(d+System.lineSeparator());
                        continue;
                }}
                fs.append(s+System.lineSeparator());
            }
            foundAll=0;
            idSekce=0;
            file.setContents(fs);
            fs=new StringBuffer();
            if(current.section().getConfig().exists(current.section().getName()))break;
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
	    	if(value!=null) {
	            if(value instanceof List) {
	                for (String s : file.getContents().toString().split(System.lineSeparator())) {
	                    if (s.trim().startsWith("#") || s.trim().isEmpty()) {
	                        fs.append(s+System.lineSeparator());
	                        continue;
	                    }
	                    if(foundAll==0) {
	                        if (s.trim().split(":")[0].equals(path[idSekce])) {
	                            if(++idSekce == path.length) {
	                            foundAll=1;
	                            String sg = s+System.lineSeparator();
	                            for(Object sdf : (List<?>)value)
	                            sg+=sc+"- "+sdf+System.lineSeparator();
	                            fs.append(sg);
	                            continue;
	                            }
	                        }
	                    }else if(s.contains("-") && s.split("-")[1].startsWith(" "))continue; //list item
	                    fs.append(s+System.lineSeparator());
	                }
	            }
	    	}
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
	                        if(value!=null)
	                        	fs.append(s.split(":")[0]+": "+value+System.lineSeparator());
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
	        }}
	    if(action==2) { //set comments
	    	//not finished yet
	    }
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

	protected static String quetos = ":", tag = "#";
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
     * @return String
     */
    public String getString(String path) {
        return getSection(path).getString();
    }
    
    /**
     * @return double
     */
    public double getDouble(String path) {
        return getSection(path).getDouble();
    }
    
    /**
     * @return int
     */
    public int getInt(String path) {
        return getSection(path).getInt();
    }
    
    /**
     * @return long
     */
    public long getLong(String path) {
        return getSection(path).getLong();
    }
    
    /**
     * @return boolean
     */
    public boolean getBoolean(String path) {
        return getSection(path).getBoolean();
    }
    
    /**
     * @return float
     */
    public float getFloat(String path) {
        return getSection(path).getFloat();
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
        return getSection(path).isString();
    }
    
    /**
     * @see see Return true if value in section is Double
     */
    public boolean isDouble(String path) {
        return getSection(path).isDouble();
    }
    
    /**
     * @see see Return true if value in section is Long
     */
    public boolean isLong(String path) {
    	isDouble(path);
        return getSection(path).isLong();
    }
    
    /**
     * @see see Return true if value in section is Int
     */
    public boolean isInt(String path) {
        return getSection(path).isInt();
    }
    
    /**
     * @see see Return true if value in section is Boolean
     */
    public boolean isBoolean(String path) {
        return getSection(path).isBoolean();
    }
    
    /**
     * @see see Return true if value in section is Float
     */
    public boolean isFloat(String path) {
        return getSection(path).isFloat();
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