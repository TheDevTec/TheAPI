package me.DevTec.Config;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
 
public class Config {
    private IFile f;
    private final LinkedBlockingQueue<Action> queue = Queues.newLinkedBlockingQueue();
    private Action current;
    public Config(File f) {
        f.getParentFile().mkdir();
        if(!f.exists())
        try {
           f.createNewFile();
        }catch (Exception e) {}
        this.f=new IFile(f);
    }
    
    public void addAction(Action toFinish) {
    	queue.add(toFinish);
    	notifyQueue();
    }
    
    private void notifyQueue() { //new queue system
    	if(current!=null)return; //just wait
    	current=queue.poll();
    	if(current==null)return;
    	String[] path = current.section().getName().split("\\.");
    	String action = current.action();
    	StringBuffer fs = new StringBuffer();
        int idSekce = 0;
        int foundAll = 0;
        String sc = "";
        for(int i = 1; i < path.length; ++i)
         sc+=" ";
        Object value = current.object();
        if(action.equals("c")) {
            for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
    	        if(foundAll==0) {
    	        if (s.trim().split(":")[0].equals(path[idSekce])) {
    	            if(++idSekce==path.length) {
    	    	        fs.append(s+System.lineSeparator());
    	                foundAll=1;
    	            }}
    	        }else {
    	            int count = 0;
    	            for(char c : s.split(":")[0].toCharArray()) {
    	                if(' '==c)++count;
    	            }
    	            if(count==path.length)
    	                fs.append(s+System.lineSeparator());
    	            else
    	                fs.append(sc+"# "+ value+System.lineSeparator()+s+System.lineSeparator());
    	        }
    	    }
        }
        if(action.equals("s")) {
        	boolean list = value!=null && value instanceof List;
        	if(value!=null) {
        		if(!exists(path[0])) {
        			getFile().getContents().append(path[0]+":"+System.lineSeparator());
        		}
                for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
                    if(foundAll==0) {
                        if (s.trim().split(":")[0].equals(path[idSekce])) {
                            fs.append(s+System.lineSeparator());
                            if(++idSekce >= path.length) {
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
                            if(!exists(pathd))
                                fs.append(d+System.lineSeparator());
                            continue;
                    }}
                    fs.append(s+System.lineSeparator());
                }
                foundAll=0;
                idSekce=0;
                getFile().setContents(fs);
                fs=new StringBuffer();
                if(list) {
                    for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
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
                        }else if(s.trim().startsWith("-"))continue;
                        fs.append(s+System.lineSeparator());
                    }
                }
        	}
        	if(value!=null && !list || value==null){
                boolean removingMode = false;
        	for (String s : getFile().getContents().toString().split(System.lineSeparator())) {
                if (s.trim().startsWith("#") || s.trim().isEmpty()) {
                	fs.append(s+System.lineSeparator());
                    continue;
                }
                if(foundAll==0) {
                    if (s.trim().split(":")[0].equals(path[idSekce]))
                        if(++idSekce == path.length) {
                            foundAll=1;
                            removingMode=true;
                            if(value!=null)
                            	fs.append(s.split(":")[0]+": "+value+System.lineSeparator());
                            continue;
                        }
                }else if(removingMode && value==null && s.trim().startsWith("-"))continue;
                else if(removingMode && value==null && !s.trim().startsWith("-"))removingMode=false;
                fs.append(s+System.lineSeparator());
            }}}
	    getFile().setContents(fs);
	    current=null;
	    notifyQueue();
    }
    
    public void save() {
    	f.save();
    }
    
    public IFile getFile() {
        return f;
    }
    
    public Section getSection(String name) {
    	return new Section(this, name);
    }
    
    public Set<String> getKeys() {
        String[] text = f.getContents().toString().split(System.lineSeparator());
        List<String> fs = Lists.newArrayList();
        for (String s : text) {
        if (s.trim().startsWith("#") || s.trim().isEmpty()) continue;
            int count = 0;
            for(char c : s.split(":")[0].toCharArray())if(' '==c)++count;
            else {
            	 if(count==0)fs.add(s.split(":")[0].trim());
            	break;
            }
        }
        return new HashSet<String>(fs);
    }
    
    public void set(String path, Object value) {
        getSection(path).set(value);
    }
    
    public String getString(String path) {
        return getSection(path).getString();
    }
    
    public double getDouble(String path) {
        return getSection(path).getDouble();
    }
    
    public int getInt(String path) {
        return getSection(path).getInt();
    }
    
    public long getLong(String path) {
        return getSection(path).getLong();
    }
    
    public boolean getBoolean(String path) {
        return getSection(path).getBoolean();
    }
    
    public float getFloat(String path) {
        return getSection(path).getFloat();
    }
    
    public boolean exists(String path) {
        return getSection(path).exists();
    }
 
    public boolean isString(String path) {
        return getSection(path).isString();
    }
    
    public boolean isDouble(String path) {
        return getSection(path).isDouble();
    }
    
    public boolean isLong(String path) {
        return getSection(path).isLong();
    }
    
    public boolean isInt(String path) {
        return getSection(path).isInt();
    }
    
    public boolean isBoolean(String path) {
        return getSection(path).isBoolean();
    }
    
    public boolean isFloat(String path) {
        return getSection(path).isFloat();
    }
    
    public void reload() {
        f.close();
        f.open();
    }
}