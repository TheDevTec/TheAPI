package me.devtec.theapi.punishmentapi;

public interface Punishment {
	public String getUser();
	
	public default long getExpire() {
		return getStart() - System.currentTimeMillis() / 1000 + getDuration();
	}
	
	public long getStart();
	
	public long getDuration();
	
	public String getReason();
	
	public PunishmentType getType();
	
	public String getTypeName();
	
	public Object getValue(String path);
	
	public boolean isIP();
	
	public void remove();
	
	public static enum PunishmentType {
		MUTE, BAN, JAIL, CUSTOM
	}
}
