package me.Straiker123;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CooldownAPI {
	private String c;
	private FileConfiguration f;
	public CooldownAPI(String cooldownname) {
		c=cooldownname;
		f=LoaderClass.data.getConfig();
	}
	
	public String getCooldownName() {
		return c;
	}
	
	public void createCooldown(String player, double length) {
		f.set("cooldown."+c+"."+player+".start", System.currentTimeMillis());
		f.set("cooldown."+c+"."+player+".time", length);
	}
	
	public boolean expired(String player) {
		return getTimeToExpire(player) < 0;
	}
	/**
	 * 
	 * @return long
	 * If return is -1, it mean cooldown isn't exist
	 */
	public long getStart(String player) {
		return f.getString("cooldown."+c+"."+player+".start")!=null? f.getLong("cooldown."+c+"."+player+".start"):-1;
	}

	/**
	 * 
	 * @return long
	 * If return is -1, it mean cooldown isn't exist
	 */
	public long getTimeToExpire(String player) {
		return getStart(player)!=-1 ? (getStart(player)/1000-System.currentTimeMillis()/1000)+(long)getCooldown(player):-1;
		
	}
	/**
	 * 
	 * @return double
	 * If return is -1, it mean cooldown isn't exist
	 */
	public double getCooldown(String player) {
		return f.getString("cooldown."+c+"."+player+".time")!=null?f.getDouble("cooldown."+c+"."+player+".time"):-1;
	}

	public void removeCooldown(String player) {
		f.set("cooldown."+c+"."+player, null);
	} 
	
	//Player method
	
	public void createCooldown(Player player, double length) {
		createCooldown(player.getName(), length);
	}
	
	public boolean expired(Player player) {
		return expired(player.getName());
	}
	/**
	 * 
	 * @return long
	 * If return is -1, it mean cooldown isn't exist
	 */
	public long getStart(Player player) {
		return getStart(player.getName());
	}

	/**
	 * 
	 * @return long
	 * If return is -1, it mean cooldown isn't exist
	 */
	public long getTimeToExpire(Player player) {
		return getTimeToExpire(player.getName());
		
	}
	/**
	 * 
	 * @return double
	 * If return is -1, it mean cooldown isn't exist
	 */
	public double getCooldown(Player player) {
		return getCooldown(player.getName());
	}

	public void removeCooldown(Player player) {
		removeCooldown(player.getName());
	} 
}
