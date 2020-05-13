package me.Straiker123;

import org.bukkit.entity.Player;

public class CooldownAPI {
	private final User c;
	public CooldownAPI(User player) {
		c=player;
	}
	
	public User getUser() {
		return c;
	}
	
	public void createCooldown(String cooldown, double length) {
		c.set("cooldown."+cooldown+".start", System.currentTimeMillis()/1000);
		c.setAndSave("cooldown."+cooldown+".time", length);
	}
	
	public void createCooldown(String cooldown, int length) {
		c.set("cooldown."+cooldown+".start", System.currentTimeMillis()/1000);
		c.setAndSave("cooldown."+cooldown+".time", length);
	}
	
	public boolean expired(String player) {
		return getTimeToExpire(player) < 0;
	}
	/**
	 * 
	 * @return long
	 * If return is -1, it mean cooldown isn't exist
	 */
	public long getStart(String cooldown) {
		return c.exist("cooldown."+cooldown+".start")?c.getLong("cooldown."+cooldown+".start"):-1;
	}

	/**
	 * 
	 * @return long
	 * If return is -1, it mean cooldown isn't exist
	 */
	public long getTimeToExpire(String cooldown) {
		return getStart(cooldown)!=-1 ? (getStart(cooldown)-System.currentTimeMillis()/1000)+(long)getCooldown(cooldown):-1;
		
	}
	/**
	 * 
	 * @return double
	 * If return is -1, it mean cooldown isn't exist
	 */
	public double getCooldown(String cooldown) {
		return c.exist("cooldown."+cooldown+".time")?c.getDouble("cooldown."+cooldown+".time"):-1;
	}

	public void removeCooldown(String cooldown) {
		c.setAndSave("cooldown."+cooldown, null);
	} 
	
	//Player method
	
	public void createCooldown(Player player, double length) {
		createCooldown(player.getName(), length);
	}
	
	public void createCooldown(Player player, int length) {
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
