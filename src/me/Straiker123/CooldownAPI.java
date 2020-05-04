package me.Straiker123;

import org.bukkit.entity.Player;

public class CooldownAPI {
	private String c;
	public CooldownAPI(String cooldownname) {
		c=cooldownname;
	}
	
	public String getCooldownName() {
		return c;
	}
	
	public void createCooldown(String player, double length) {
		User s = TheAPI.getUser(player);
		s.set("cooldown."+c+"."+player+".start", System.currentTimeMillis()/1000);
		s.set("cooldown."+c+"."+player+".time", length);
		s.save();
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
		User s = TheAPI.getUser(player);
		return s.getString("cooldown."+c+"."+player+".start")!=null? s.getLong("cooldown."+c+"."+player+".start"):-1;
	}

	/**
	 * 
	 * @return long
	 * If return is -1, it mean cooldown isn't exist
	 */
	public long getTimeToExpire(String player) {
		return getStart(player)!=-1 ? (getStart(player)-System.currentTimeMillis()/1000)+(long)getCooldown(player):-1;
		
	}
	/**
	 * 
	 * @return double
	 * If return is -1, it mean cooldown isn't exist
	 */
	public double getCooldown(String player) {
		User s = TheAPI.getUser(player);
		return s.getString("cooldown."+c+"."+player+".time")!=null?s.getDouble("cooldown."+c+"."+player+".time"):-1;
	}

	public void removeCooldown(String player) {
		TheAPI.getUser(player).setAndSave("cooldown."+c+"."+player, null);
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
