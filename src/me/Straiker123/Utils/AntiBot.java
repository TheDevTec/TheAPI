package me.Straiker123.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.Straiker123.LoaderClass;
import me.Straiker123.PunishmentAPI;
import me.Straiker123.TheAPI;

public class AntiBot {
	private static long last;
	private static List<String> c = new ArrayList<String>();
	public static boolean hasAccess(UUID e) {
		boolean s = canJoin();
		if(!s)c.add(e.toString());
		last=System.currentTimeMillis()/1000;
		return s;
	}
	private static PunishmentAPI a = TheAPI.getPunishmentAPI();
	public static boolean isDisallowed(String uuid) {
		String p = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
		return c.contains(uuid)||a.hasBan(p)||a.hasBanIP(p)||a.hasTempBan(p)||a.hasTempBanIP(p);
	}
	@SuppressWarnings("deprecation")
	public static boolean containsDisallowed(String string) {
		boolean f=false;
		for(String s : c) {
			if(string.contains(s)) {
				f=true;
				break;
			}
		}
		if(!f)
		for(String s : a.getBanList().getBannedPlayers()) {
			if(string.contains(Bukkit.getOfflinePlayer(s).getUniqueId().toString())) {
				f=true;
				break;
			}
		}
		return f;
	}
	
	public static boolean canJoin() {
		if(!LoaderClass.config.getBoolean("Options.AntiBot.Use"))return true;
		return last-System.currentTimeMillis()/1000+LoaderClass.config.getLong("Options.AntiBot.TimeBetweenPlayer") < 0;
	}
}
