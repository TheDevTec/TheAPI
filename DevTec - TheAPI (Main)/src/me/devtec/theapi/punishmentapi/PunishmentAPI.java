package me.devtec.theapi.punishmentapi;

import java.util.List;

import me.devtec.theapi.TheAPI;

public interface PunishmentAPI {
	//MUTE
	public Punishment mute(String user, long duration, String reason);

	public Punishment muteIP(String ip, long duration, String reason);

	//BAN
	public Punishment ban(String user, long duration, String reason);

	public Punishment banIP(String ip, long duration, String reason);

	//JAIL
	public Punishment jail(String user, long duration, String reason);

	public Punishment jailIP(String ip, long duration, String reason);

	public void warn(String user, String reason);
	
	public List<Punishment> getPunishments(String user);

	public List<Punishment> getPunishmentsIP(String ip);
	
	public default String getIp(String user) {
		return TheAPI.getUser(user).getString("ip").replace("_", ".");
	}
}