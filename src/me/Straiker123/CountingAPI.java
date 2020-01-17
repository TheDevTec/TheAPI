package me.Straiker123;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CountingAPI {
	
	public int getMaxPlayers() {
		return Bukkit.getMaxPlayers();
	}
	
	public List<Player> getOnlinePlayers(){
		List<Player> a = new ArrayList<Player>();
		for(Player p : Bukkit.getOnlinePlayers())
			a.add(p);
		return a;
	}

	public List<Plugin> getEnabledPlugins(){
		List<Plugin> a = new ArrayList<Plugin>();
		 for(Plugin p : Bukkit.getPluginManager().getPlugins()) {
			 if(p.isEnabled())a.add(p);
		 }
		 return a;
	}
	public List<Plugin> getDisabledPlugins(){
		 List<Plugin> a = new ArrayList<Plugin>();
		 for(Plugin p : Bukkit.getPluginManager().getPlugins()) {
			 if(!p.isEnabled())a.add(p);
		 }
		 return a;
	}
	

	public List<Plugin> getPlugins(){
		List<Plugin> a = new ArrayList<Plugin>();
		 for(Plugin p : Bukkit.getPluginManager().getPlugins()) {
			a.add(p);
		 }
		 return a;
	}
	
	
	public List<Plugin> getPluginsUsingTheAPI() {
		List<Plugin> a = new ArrayList<Plugin>();
		for(Plugin all : getPlugins())
		 if(TheAPI.getPluginsManagerAPI().getDepend(all.getName()).contains("TheAPI")
				 ||TheAPI.getPluginsManagerAPI().getSoftDepend(all.getName()).contains("TheAPI"))a.add(all);
		 return a;
	}
}
