package me.Straiker123;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

import me.Straiker123.Utils.Error;

public class ConfigAPI {
	String name;
	String h;
	String loc;
	FileConfiguration a;
	Map<String, Object> c=new HashMap<String, Object>();
	
	public ConfigAPI(String localization, String configName) {
	name=configName;	
	loc=localization;
	}
	/**
	 * @return boolean if exist string in config (Can be used before create a config)
	 */
	public boolean existPath(String string) {
		return existFile() && YamlConfiguration.loadConfiguration(getFile()).getString(string) != null;
	}
	
	public void addDefault(String path, Object value) {
		if(!c.containsKey(path))
		c.put(path, value);
		else
			c.replace(path, value);
	}
	
	public void addDefaults(Map<String, Object> defaults){
		c=defaults;
	}
	String end = "yml";
	File f;

	public boolean existFile() {
		return getFile() != null;
	}
	
	public File getFile() {
		if(f==null) {
			File d = new File("plugins/"+loc);
			if(!d.mkdir())
			d.mkdirs();
			File ff = new File("plugins/"+loc+"/"+name+"."+end);
			try {
			if(ff.exists()) { //file exists
				f=ff;
			}else {
			try {
			ff.createNewFile();
			f= ff;
		} catch (Exception e) {
			if(LoaderClass.config.getConfig() == null || LoaderClass.config.getConfig() != null && !LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cError when getting file of "+name+"."+end+" config:"));
				e.printStackTrace();
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
				}else
					Error.sendRequest("&bTheAPI&7: &cError when getting file of "+name+"."+end+" config");
		}
		f= ff;
		}
		}catch(Exception er) {
			try {
				ff.createNewFile();
				f= ff;
			} catch (IOException e) {
				if(LoaderClass.config.getConfig() == null || LoaderClass.config.getConfig() != null && !LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cError when getting file of "+name+"."+end+" config:"));
					e.printStackTrace();
					TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
					}else
						Error.sendRequest("&bTheAPI&7: &cError when getting file of "+name+"."+end+" config");
			}
		}
		}
		return f;
	}
	
	public void setHeader(String header) {
		h=header;
	}
	public FileConfiguration getConfig() {
		return a;
	}
	public void setCustomEnd(String customEnd) {
		if(customEnd != null)
		end=customEnd;
	}

	public boolean removePath(String path) {
		if(a!=null) {
			if(a.getString(path) != null) {
				a.set(path, null);
				return true;
			}
		}
		return false;
	}
	public boolean createPath(String path) {
		if(a!=null) {
			if(a.getString(path) == null) {
				a.createSection(path);
				return true;
			}
		}
		return false;
	}
	
	private boolean check() {
		if(a==null) {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cFileConfiguration is null"));
			return false;
		}else
		if(!existFile()) {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cFile is null"));
			return false;
		}else
		return true;
	}
	
	public boolean save() {
		try {
		if(check()) {
		a.save(f);
		return true;
		}
		return false;
	} catch (Exception e) {
		if(LoaderClass.config.getConfig() == null || LoaderClass.config.getConfig() != null && !LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cError when saving "+name+"."+end+" config:"));
		e.printStackTrace();
		TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
		}else
			Error.sendRequest("&bTheAPI&7: &cError when saving "+name+"."+end+" config");
		return false;
	}
	}
	
	public boolean reload() {
		try {
			f=null;
			a=null;
			f=getFile();
			Reader reader = new InputStreamReader(Files.asByteSource(f).openStream(), "UTF-8");
			a=YamlConfiguration.loadConfiguration(reader);
			if(h!=null)a.options().header(h);
			if(c!=null && !c.isEmpty()) {
			a.addDefaults(c);
			}
			a.options().copyDefaults(true).copyHeader(true);
			save();
			if(!LoaderClass.list.contains(this))
			LoaderClass.list.add(this);
		return true;
		} catch (Exception e) {
			if(LoaderClass.config.getConfig() == null || LoaderClass.config.getConfig() != null && !LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cError when reloading "+name+"."+end+" config:"));
			e.printStackTrace();
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
			}else
				Error.sendRequest("&bTheAPI&7: &cError when reloading "+name+"."+end+" config");
			return false;
		}
	}

	public boolean create() {
		try {
			if(f==null)
		f=getFile();
			try {
			if(a==null) {
				Reader reader = new InputStreamReader(Files.asByteSource(f).openStream(), "UTF-8");
				a=YamlConfiguration.loadConfiguration(reader);
			}
			}catch(Exception repeat) {
				a = YamlConfiguration.loadConfiguration(f);
			}
		if(h!=null)a.options().header(h);
		if(c!=null && !c.isEmpty()) {
		a.addDefaults(c);
		}
		a.options().copyDefaults(true).copyHeader(true);
		save();
		if(!LoaderClass.list.contains(this))
		LoaderClass.list.add(this);
		return true;
		} catch (Exception e) {
			if(LoaderClass.config.getConfig() == null || LoaderClass.config.getConfig() != null && !LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cError when creating "+name+"."+end+" config:"));
			e.printStackTrace();
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
			}else
				Error.sendRequest("&bTheAPI&7: &cError when creating "+name+"."+end+" config");
			return false;
		}
	}
	public Map<String, Object> getDefaults(){
		return c;
	}
	
	public boolean delete() {
		if(f==null)
		f = getFile();
		if(f.exists()) {
		f.delete();
		c.clear();
		if(LoaderClass.list.contains(this))
		LoaderClass.list.remove(this);
		return true;
		}
		return false;
	}
	
}
