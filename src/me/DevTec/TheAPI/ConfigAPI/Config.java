package me.DevTec.TheAPI.ConfigAPI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.DevTec.TheAPI.Utils.StringUtils;

public class Config {
    private Map<String, Object> defaults = Maps.newHashMap();
    private File f = null;
    private FileWriter w;
    private final String configPath;
    private boolean c;
    private String[] text;

    public Config(String path) {
    	configPath="plugins/"+path;
    	reload();
    }

    private void close() {
        c = true;

        try {
            w.close();
        } catch(Exception e) { /* */ }
    }

    private void open() {
        c = false;
        createFile(f);
        try {
            w = new FileWriter(f);
        } catch (Exception e) { /* */ }
    }

    public void save() {
        try {
            for (String sc : text) {
                getWriter().append(sc + System.lineSeparator());
            }

            getWriter().flush();
        } catch (Exception e) { /* */ }
        close();
    }

    public void addDefaults(Map<String, Object> values) {
    	for(String key : values.keySet())
    		addDefault(key, values.get(key));
    }

    public void addDefault(String key, Object value) {
    	defaults.put(key, value);
        if(!exists(key))
        	set(key, value);
    }

    public void reload() {
    	File file = new File(configPath);
        createFile(file);
        loadFile(file);
        loadContents();
    }

    private void createFile(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }

    private void loadFile(File file) {
        f = file;
        c = true;
    }

    public File getFile() {
        return f;
    }

    public String getName() {
        return f.getName();
    }

    private FileWriter getWriter() {
        if (c) {
            open();
        }

        return w;
    }

    private void loadContents() {
        StringBuffer buffer = new StringBuffer();

        try {
            Scanner sc = new Scanner(f);

            while (sc.hasNextLine()) {
                buffer.append(sc.nextLine() + System.lineSeparator());
            }

            sc.close();
        } catch (Exception e) { /* */ }
        setContents(buffer);
    }

    private String[] getContents() {
        return text;
    }

    private void setContents(StringBuffer content) {
        text = content.toString().split(System.lineSeparator());
    }

    public boolean exists(String path) {
        return exists(getContents(), path.split("\\."));
    }

	private synchronized boolean exists(String[] text, String[] sections) {
	    int id = 0,key_spaces = 0,spaces = 0;
	    String key = null;
	    for (String s : text) {
	        key = s.split(":")[0];
	        key_spaces = key.replaceAll("[^ ]", "").length();
	        if (spaces > key_spaces) {
	          spaces -= 2;
	        }
	        if (key.trim().equals(sections[id])) {
	             if (key_spaces == spaces) {
	                    id++;
	                    spaces += 2;
	                    if (id == sections.length)
	                        break;
	                }
	            }}
	    return id == sections.length;
	}
	
    public void addComments(String paths, String... value) {
    	for(String s : value)
    		addComment(paths, s);
    }
	
    public void addComment(String paths, String value) {
    	int idSekce = 0,foundAll = 0;
		String[] path = paths.split("\\.");
		StringBuffer fs = new StringBuffer();
        for (String s : getContents()) {
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
        setContents(fs);
    }
     
    public List<String> getComments(String path) {
    	String[] w = path.split("\\.");
    	List<String> found = Lists.newArrayList();
    	 int idSekce = 0;
         for (String s : getContents()) {
             if(s.trim().startsWith("#"))found.add(s.split("#")[1].replaceFirst(" ", ""));
             if (s.trim().split(":")[0].equals(w[idSekce])) {
                 if(++idSekce==w.length)break; //stop finding
             }
             found.clear();
         }
    	return found;
    }
    
    public Section getSection(String path) {
    	return new Section(this,path);
    }

    /**
     * @see see Get List<Integer> from Path
     * @return List<Integer>
     */
    public List<Integer> getIntegerList(String path) {
    	String[] sections = path.split("\\.");
        List<Integer> list = Lists.newArrayList();
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();
            if (id != sections.length) {
                if (spaces > key_spaces) {
                    spaces -= 2;
                }

                if (key.trim().equals(sections[id])) {
                    if (key_spaces == spaces) {
                        id++;
                        spaces += 2;

                        if (id == sections.length) {
                        }
                    }
                }
            } else {
                if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                	s=s.replace(s.split("- ")[0], "").replaceFirst("- ","");
                    list.add(StringUtils.getInt(removeQuetos(s)));
                } else {
                    break;
                }
            }
        }
        return list;
    }

    /**
     * @see see Get List<Byte> from Path
     * @return List<Byte>
     */
    public List<Byte> getByteList(String path) {
    	String[] sections = path.split("\\.");
        List<Byte> list = Lists.newArrayList();
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();
            if (id != sections.length) {
                if (spaces > key_spaces) {
                    spaces -= 2;
                }

                if (key.trim().equals(sections[id])) {
                    if (key_spaces == spaces) {
                        id++;
                        spaces += 2;

                        if (id == sections.length) {
                        }
                    }
                }
            } else {
                if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                	s=s.replace(s.split("- ")[0], "").replaceFirst("- ","");
                    list.add(StringUtils.getByte(removeQuetos(s)));
                } else {
                    break;
                }
            }
        }
        return list;
    }

    /**
     * @see see Get List<Boolean> from Path
     * @return List<Boolean>
     */
    public List<Boolean> getBooleanList(String path) {
    	String[] sections = path.split("\\.");
        List<Boolean> list = Lists.newArrayList();
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();
            if (id != sections.length) {
                if (spaces > key_spaces) {
                    spaces -= 2;
                }

                if (key.trim().equals(sections[id])) {
                    if (key_spaces == spaces) {
                        id++;
                        spaces += 2;

                        if (id == sections.length) {
                        }
                    }
                }
            } else {
                if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                	s=s.replace(s.split("- ")[0], "").replaceFirst("- ","");
                    list.add(StringUtils.getBoolean(removeQuetos(s)));
                } else {
                    break;
                }
            }
        }
        return list;
    }

    /**
     * @see see Get List<Double> from Path
     * @return List<Double>
     */
    public List<Double> getDoubleList(String path) {
    	String[] sections = path.split("\\.");
        List<Double> list = Lists.newArrayList();
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();
            if (id != sections.length) {
                if (spaces > key_spaces) {
                    spaces -= 2;
                }

                if (key.trim().equals(sections[id])) {
                    if (key_spaces == spaces) {
                        id++;
                        spaces += 2;

                        if (id == sections.length) {
                        }
                    }
                }
            } else {
                if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                	s=s.replace(s.split("- ")[0], "").replaceFirst("- ","");
                    list.add(StringUtils.getDouble(removeQuetos(s)));
                } else {
                    break;
                }
            }
        }
        return list;
    }

    /**
     * @see see Get List<Short> from Path
     * @return List<Short>
     */
    public List<Short> getShortList(String path) {
    	String[] sections = path.split("\\.");
        List<Short> list = Lists.newArrayList();
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();
            if (id != sections.length) {
                if (spaces > key_spaces) {
                    spaces -= 2;
                }

                if (key.trim().equals(sections[id])) {
                    if (key_spaces == spaces) {
                        id++;
                        spaces += 2;

                        if (id == sections.length) {
                        }
                    }
                }
            } else {
                if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                	s=s.replace(s.split("- ")[0], "").replaceFirst("- ", "");
                    list.add(StringUtils.getShort(removeQuetos(s)));
                } else {
                    break;
                }
            }
        }
        return list;
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
    	String[] sections = path.split("\\.");
        List<Float> list = Lists.newArrayList();
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();
            if (id != sections.length) {
                if (spaces > key_spaces) {
                    spaces -= 2;
                }

                if (key.trim().equals(sections[id])) {
                    if (key_spaces == spaces) {
                        id++;
                        spaces += 2;

                        if (id == sections.length) {
                        }
                    }
                }
            } else {
                if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                	s=s.replace(s.split("- ")[0], "").replaceFirst("- ", "");
                    list.add(StringUtils.getFloat(removeQuetos(s)));
                } else {
                    break;
                }
            }
        }
        return list;
    }

    /**
     * @see see Get List<Object> from Path
     * @return List<Object>
     */
    public List<Object> getList(String path) {
    	String[] sections = path.split("\\.");
        List<Object> list = Lists.newArrayList();
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();
            if (id != sections.length) {
                if (spaces > key_spaces) {
                    spaces -= 2;
                }

                if (key.trim().equals(sections[id])) {
                    if (key_spaces == spaces) {
                        id++;
                        spaces += 2;

                        if (id == sections.length) {
                        }
                    }
                }
            } else {
                if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                	s=s.replace(s.split("- ")[0], "").replaceFirst("- ", "");
                    String object = removeQuetos(s);
                	try {
            			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(object));
            			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                	try {
            			while (true) {
                        	try {
                        		list.add(dataInput.readObject());
                        	}catch(Exception reading2) {
                        		break;
                        	}
            			}
            			dataInput.close();
                	}catch(Exception reading) {}
                	}catch(Exception e) {
                		list.add(object);
                	}
                } else {
                    break;
                }
            }
        }
        return list;
    }

    public List<String> getKeys(String path) {
    	if(path.trim().isEmpty())return getKeys();
        String[] sections = path.split("\\.");
        List<String> list = Lists.newArrayList();
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();

            if (id != sections.length) {
                if (spaces > key_spaces) {
                    spaces -= 2;
                }
                if (key.trim().equals(sections[id])) {
                    if (key_spaces == spaces) {
                        id++;
                        spaces += 2;

                        if (id == sections.length) {
                            continue;
                        }
                    }
                }
            }else {
                if (key_spaces == spaces) {
                    list.add(key.trim());
                } else {
                    break;
                }
            }
        }
        return list;
    }

    public String getString(String path) {
        String[] sections = path.split("\\.");
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null,value = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();
            if (spaces > key_spaces) {
                spaces -= 2;
            }
            if (key.trim().equals(sections[id])) {
                if (key_spaces == spaces) {
                    id++;
                    spaces += 2;
                    if (id == sections.length) {
                    	s=s.replace(s.split(": ")[0], "").replaceFirst(": ", "");
                        value= removeQuetos(s);
                        break;
                    }
                }
            }
        }
        return value;
    }

    public List<String> getStringList(String path) {
        String[] sections = path.split("\\.");
        List<String> list = Lists.newArrayList();
        int id = 0,key_spaces = 0,spaces = 0;
        String key = null;
        for (String s : getContents()) {
            key = s.split(":")[0];
            key_spaces = key.replaceAll("[^ ]", "").length();
            if (id != sections.length) {
                if (spaces > key_spaces) {
                    spaces -= 2;
                }

                if (key.trim().equals(sections[id])) {
                    if (key_spaces == spaces) {
                        id++;
                        spaces += 2;

                        if (id == sections.length) {
                        }
                    }
                }
            } else {
                if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                	s=s.replace(s.split("- ")[0], "").replaceFirst("- ", "");
                    list.add(removeQuetos(s));
                } else {
                    break;
                }
            }
        }
        return list;
    }

    public void set(String path, Object object) {
        String sections[] = path.split("\\.");

        if (object == null) {
            remove(getContents(), sections);
            return;
        }

        setContents(write(sections, object));
    }

    private synchronized void create(String[] path) {
        StringBuffer sb = new StringBuffer();
        int first = 0;
        String paths = "";
        for (String s : getContents()) {
            sb.append(s + System.lineSeparator());
        }
        if (!exists(sb.toString().split(System.lineSeparator()), path[0].split("\\."))) {
            sb.append(path[0] +":"+ System.lineSeparator());
        }
        for (String a : path) {
            paths += "."+ a;

            if (first == 0) {
                first = 1;
                paths = paths.substring(1);
            }

            sb = write(sb.toString().split(System.lineSeparator()), paths.split("\\."));
        }

        setContents(sb);
    }

    private synchronized void remove(String[] text, String[] sections) {
        StringBuffer sb = new StringBuffer();
        int id = 0, remove = 0;
        for (String s : text) {
            if(id != sections.length) {
                if (s.trim().split(":")[0].equals(sections[id])) {
                    id++;

                    if (id == sections.length) {
                        remove = 0;
                        continue;
                    }
                }
            } else if (remove==1) {
                if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                    continue;
                } else {
                    remove = 0;
                }
            }

            sb.append(s + System.lineSeparator());
        }
        setContents(sb);
    }

    private synchronized StringBuffer write(String[] text, String[] sections) {
        StringBuffer sb = new StringBuffer();
        int id = 0;
        String ac = "", path = "";
        for (String s : text) {
            sb.append(s + System.lineSeparator());

            if (id != sections.length) {
                if (s.trim().split(":")[0].equals(sections[id])) {
                    id++;

                    if (id == sections.length) {
                        continue;
                    }

                    ac = "";
                    path = "";

                    for (int i = 0; i < id; ++i) {
                        ac += "  ";
                        path += "."+ sections[i];
                    }

                    path = path.substring(1) +"."+ sections[id];

                    if (!exists(text, path.split("\\."))) {
                        sb.append(ac + sections[id] +":"+ System.lineSeparator());
                    }
                }
            }
        }

        return sb;
    }

    private synchronized StringBuffer write(String[] sections, Object object) {
        if (!exists(getContents(),sections))
            create(sections);
        StringBuffer sb = new StringBuffer();
        String[] map = getContents();
        int id = 0, remove = 0;
        String sc = "";
            if (object instanceof List) {
                for (String s : map) {
                    if(id != sections.length) {
                        if (s.trim().split(":")[0].equals(sections[id])) {
                            id++;

                            if(id == sections.length) {
                                remove = 1;
                                sc = "";
                                sb.append(s + System.lineSeparator());

                                for (int i = 1; i < sections.length; ++i) {
                                    sc += "  ";
                                }

                                for (Object string : (List<?>)object) {
                                    if (string instanceof Number || string instanceof String || string instanceof Boolean) {
                                        sb.append(sc + "  - " + addQuetos(string + "") + System.lineSeparator());
                                    } else {
                                        try {
                                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
                                            dataOutput.writeObject(string);
                                            dataOutput.close();
                                            sb.append(sc+"  - " +addQuetos(Base64Coder.encodeLines(outputStream.toByteArray())) + System.lineSeparator());
                                        } catch (Exception e) {
                                            sb.append(sc +"  - "+ addQuetos(string + System.lineSeparator()));
                                        }
                                    }
                                }

                                continue;
                            }
                        }
                    } else if (remove == 1) {
                        if (s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) {
                            continue;
                        } else {
                            remove = 0;
                        }
                    }

                    sb.append(s+System.lineSeparator());
                }

                return sb;
            }

            for (String s : map) {
                if (id != sections.length) {
                    if (s.trim().split(":")[0].equals(sections[id])) {
                        id++;

                        if (id == sections.length) {
                            if (object instanceof Number || object instanceof String || object instanceof Boolean) {
                                sb.append(s.split(":")[0] + ": " + addQuetos("" + object) + System.lineSeparator());
                                continue;
                            } else {
                                try {
                                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                    BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

                                    dataOutput.writeObject(object);
                                    dataOutput.close();

                                    sb.append(s.split(":")[0] + ": " + addQuetos(Base64Coder.encodeLines(outputStream.toByteArray())) + System.lineSeparator());
                                } catch (Exception e) {
                                    sb.append(s.split(":")[0] + ": " + addQuetos("" + object) + System.lineSeparator());
                                }

                                continue;
                            }
                        }
                    }
                }

                sb.append(s+System.lineSeparator());
            }

            return sb;
    }

    private static String removeQuetos(String value) {
        if (value.startsWith("\"") && value.endsWith("\"") || value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length()-1);
        }

        return value;
    }

    private static String addQuetos(String value) {
    	if(value==null)return value;
        if (!value.startsWith("\"") && !value.endsWith("\"") && !value.startsWith("'") && !value.endsWith("'")) {
            if (StringUtils.isNumber(value)) {
                return "'" + value + "'";
            }

            if (StringUtils.isBoolean(value)) {
                return value;
            }

            if (StringUtils.containsSpecial(value) || value.length() >= 16) {
                return "\"" + value + "\"";
            }

            return value;
        }

        return value;
    }

    public boolean isString(String path) {
        return getString(path) != null;
    }

    public boolean isDouble(String path) {
        try {
            Double.parseDouble(getString(path));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean isLong(String path) {
        try {
            Long.parseLong(getString(path));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean isInt(String path) {
        try {
            Integer.parseInt(getString(path));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean isBoolean(String path) {
        String a = getString(path);

        if (a == null) {
            return false;
        }

        return a.equalsIgnoreCase("true") || a.equalsIgnoreCase("false");
    }

    public boolean isFloat(String path) {
        try {
            Float.parseFloat(getString(path));
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean isShort(String fromString) {
        try {
            Short.parseShort(fromString);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public boolean isByte(String fromString) {
        try {
            Byte.parseByte(fromString);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public int getInt(String path) {
        String s = getString(path);

        if (s == null) {
            return 0;
        }

        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    public double getDouble(String path) {
        String s = getString(path);

        if (s == null) {
            return 0.0;
        }

        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public long getLong(String path) {
        String s = getString(path);

        if (s == null) {
            return 0L;
        }

        try {
            return Long.parseLong(s);
        } catch(Exception e) {
            return 0L;
        }
    }

    public float getFloat(String path) {
        String s = getString(path);

        if (s == null) {
            return 0F;
        }

        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return 0F;
        }
    }

    public boolean getBoolean(String path) {
        String s = getString(path);

        if (s == null) {
            return false;
        }

        try {
            return Boolean.parseBoolean(s);
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getKeys() {
        List<String> list = Lists.newArrayList();
        for (String s : getContents()) {
        	if(!s.trim().isEmpty())
        	if (!(s.split("-").length >= 2 && s.split("-")[1].startsWith(" ")) && !s.trim().startsWith("#"))
            if (c(s)==0)
                list.add(s.split(":")[0].trim());
        }
        return list;
    }
    
    private static int c(String s) {
    	int a = 0;
    	for(char c : s.toCharArray())
    		if(c==' ')++a;
    		else break;
    	return a;
    }

	public String getStringContents() {
		return getStringContents(true);
	}

	public String getStringContents(boolean addSplit) {
		StringBuffer f = new StringBuffer();
		for(String s : getContents())
			f.append(s+(addSplit?System.lineSeparator():""));
		return f.toString();
	}
}
