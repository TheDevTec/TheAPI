package me.Straiker123;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.Straiker123.Utils.Error;

public class Jail {
	private String getName() {
		String s = "0";
		for(int w = 0; w > 0; ++w) {
		if(LoaderClass.data.getConfig().getString("Jails."+w)==null) {
			s=""+w;
			break;
		}}
		return s;
	}
	
	public List<String> getPlayersInJail(String id){
		if(id==null) {
			Error.err("finding jail", "Jail is null");
			return new ArrayList<String>();
		}
		return LoaderClass.data.getConfig().getStringList("Jails."+id+".Players");
	}
	
	public void addLocation(Location location) {
		if(location == null) {
			Error.err("creating jail", "Location is null");
			return;
		}
		LoaderClass.data.getConfig().set("Jails."+getName()+".Location", location);
	}
	
	public boolean existJail(String id) {
		return LoaderClass.data.getConfig().getString("Jails."+id)!=null;
	}
	public void delLocation(String id) {
		if(id == null) {
			Error.err("deleting jail", "Jail is null");
			return;
		}
		unJailAll(id);
		LoaderClass.data.getConfig().set("Jails."+id, null);
	}
	
	public void unJailAll(String id) {
		if(LoaderClass.data.getConfig().getString("Jails."+id+".Players")!=null)
		for(String s : LoaderClass.data.getConfig().getStringList("Jails."+id+".Players"))
			unJail(s);
	}
	
	private String getRandom() {
		List<Object> l = new ArrayList<Object>();
		if(getJails().isEmpty()==false)
			for(String s: getJails()) {
				l.add(s);
			}
		return TheAPI.getRandomFromList(l).toString();
	}
	
	public void setJail(String player, String reason) {
		setJail(player,getRandom(),reason);
	}
	
	public List<String> getJails(){
		List<String> list = new ArrayList<String>();
		if(LoaderClass.data.getConfig().getString("Jails")!=null)
		for(String s: LoaderClass.data.getConfig().getConfigurationSection("Jails").getKeys(false)) {
			list.add(s);
		}
		return list;
	}
	
	public void setJail(String player, String id, String reason) {
		if(id==null) {
			Error.err("jail player "+player, "Jail is null");
			return;
		}
		if(LoaderClass.data.getConfig().getString("Jails."+id)==null) {
			Error.err("jail player "+player, "Jail doesn't exists");
			return;
		}
		Location loc = null;
		if(isJailed(player)) {
			for(String s : getJails()) {
				List<String> list = LoaderClass.data.getConfig().getStringList("Jails."+s+".Players");
				list.remove(player);
				LoaderClass.data.getConfig().set("Jails."+s+".Players", list);
				loc=(Location)LoaderClass.data.getConfig().get("data."+player+".JailedOn");
			}
		}
		List<String> list = LoaderClass.data.getConfig().getStringList("Jails."+id+".Players");
		list.add(player);
		LoaderClass.data.getConfig().set("Jails."+id+".Players", list);
		if(loc==null) {
			if(TheAPI.getPlayer(player)!=null)
			loc=TheAPI.getPlayer(player).getLocation();
			else {
				if(LoaderClass.data.getConfig().getString("data."+player+".LeaveLocation")!=null)
				loc=(Location)LoaderClass.data.getConfig().get("data."+player+".LeaveLocation");
				else
					loc=Bukkit.getWorlds().get(0).getSpawnLocation();
			}
		}
		LoaderClass.data.getConfig().set("Jails."+id+".Players", list);
		LoaderClass.data.getConfig().set("data."+player+".JailedOn", loc);
		LoaderClass.data.getConfig().set("data."+player+".TeleportBack", true);
		LoaderClass.data.getConfig().set("data."+player+".Jail", id);
		if(TheAPI.getPlayer(player)!=null)
		TheAPI.getPlayerAPI(TheAPI.getPlayer(player)).teleport(getJailLocation(id));
	}
	public Location getJailLocation(String id) {
		if(LoaderClass.data.getConfig().getString("Jails."+id+".Location")!=null) {
			return (Location)LoaderClass.data.getConfig().get("Jails."+id+".Location");
		}
		return null;
	}
	
	public boolean isJailed(String player) {
		boolean is=false;
		if(getJails().isEmpty()==false)
		for(String s:getJails()) {
			if(LoaderClass.data.getConfig().getString("Jails."+s+".Players")!=null)
			if(LoaderClass.data.getConfig().getStringList("Jails."+s+".Players").contains(player)) {
				is=true;
				break;
			}
		}
		return is;
	}
	
	public String getJailOfPlayer(String player) {
		String a = null;
		if(getJails().isEmpty()==false)
		for(String s : getJails()) {
			if(LoaderClass.data.getConfig().getStringList("Jails."+s+".Players").contains(player))a=s;
		}
		return a;
	}
	
	public void unJail(String player) {
		for(String id : getJails()) {
		List<String> list = LoaderClass.data.getConfig().getStringList("Jails."+id+".Players");
		list.remove(player);
		LoaderClass.data.getConfig().set("Jails."+id+".Players", list);
		}
		if(TheAPI.getPlayer(player)!=null) {
		TheAPI.getPlayerAPI(TheAPI.getPlayer(player)).teleport((Location)LoaderClass.data.getConfig().get("data."+player+".JailedOn"));
		LoaderClass.data.getConfig().set("data."+player+".JailedOn", null);
		LoaderClass.data.getConfig().set("data."+player+".Jail", null);
		LoaderClass.data.getConfig().set("data."+player+".TeleportBack", null);
		}
	}
}
