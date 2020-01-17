package me.Straiker123;

public class CooldownAPI {
	String c;
	public CooldownAPI(String cooldownname) {
		c=cooldownname;
	}
	
	
	public void createCooldown(String player, double length) {
		LoaderClass.data.getConfig().set("cooldown."+c+"."+player+".start", System.currentTimeMillis());
		LoaderClass.data.getConfig().set("cooldown."+c+"."+player+".time", length);
		LoaderClass.data.save();
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
		if(LoaderClass.data.getConfig().getString("cooldown."+c+"."+player+".start")!=null)return LoaderClass.data.getConfig().getLong("cooldown."+c+"."+player+".start");
		return -1;
	}

	/**
	 * 
	 * @return long
	 * If return is -1, it mean cooldown isn't exist
	 */
	public long getTimeToExpire(String player) {
		if(getStart(player)!=-1)
		return (getStart(player)/1000-System.currentTimeMillis()/1000)+(long)getCooldown(player);
		return -1;
		
	}
	

	/**
	 * 
	 * @return double
	 * If return is -1, it mean cooldown isn't exist
	 */
	public double getCooldown(String player) {
		if(LoaderClass.data.getConfig().getString("cooldown."+c+"."+player+".time")!=null)return LoaderClass.data.getConfig().getDouble("cooldown."+c+"."+player+".time");
		return -1;
	}

	public void removeCooldown(String player) {
		LoaderClass.data.getConfig().set("cooldown."+c+"."+player, null);
		LoaderClass.data.save();
	} 
}
