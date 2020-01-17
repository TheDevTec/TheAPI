package me.Straiker123;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BanList {
	public List<String> getIPBannedIPs(){
		List<String> banned = new ArrayList<String>();
		if(LoaderClass.data.getConfig().getString("bans")!=null)
			for(String s  :LoaderClass.data.getConfig().getConfigurationSection("bans").getKeys(false)) {
				if(isIP(s))banned.add(s.replace("_", "."));
			}
		return banned;
	}
	public List<String> getBannedPlayers(){
		List<String> banned = new ArrayList<String>();
		if(LoaderClass.data.getConfig().getString("bans")!=null)
			for(String s  :LoaderClass.data.getConfig().getConfigurationSection("bans").getKeys(false)) {
				if(!isIP(s))banned.add(s);
			}
		return banned;
	}

	public String getBanReason(String player) {
		if(player==null)return null;
		return LoaderClass.data.getConfig().getString("bans."+player+".ban");
	}

	public String getBanIPReason(String playerOrIP) {
		if(playerOrIP==null)return null;
		if(isIP(playerOrIP))
			return LoaderClass.data.getConfig().getString("bans."+playerOrIP+".banip");
		return LoaderClass.data.getConfig().getString("bans."+TheAPI.getPunishmentAPI().getIP(playerOrIP)+".banip");
	}

	public String getMuteReason(String player) {
		return LoaderClass.data.getConfig().getString("bans."+player+".mute");
	}

	public String getTempMuteReason(String player) {
		return LoaderClass.data.getConfig().getString("bans."+player+".tempmute.reason");
	}
	
	public long getTempBanTime(String player) {
		if(player==null)return 0;
		return LoaderClass.data.getConfig().getLong("bans."+player+".tempban.time");
	}
	public long getTempMuteTime(String player) {
		if(player==null)return 0;
		return LoaderClass.data.getConfig().getLong("bans."+player+".tempmute.time");
	}

	public long getTempBanStart(String player) {
		if(player==null)return 0;
		return LoaderClass.data.getConfig().getLong("bans."+player+".tempban.start");
	}
	public long getTempMuteStart(String player) {
		if(player==null)return 0;
		return LoaderClass.data.getConfig().getLong("bans."+player+".tempmute.start");
	}

	public long getTempBanIPStart(String player) {
		if(player==null)return 0;
		if(isIP(player))
			return LoaderClass.data.getConfig().getLong("bans."+player+".tempbanip.start");
		return LoaderClass.data.getConfig().getLong("bans."+TheAPI.getPunishmentAPI().getIP(player)+".tempbanip.start");
	}
	public long getTempBanIPTime(String player) {
		if(player==null)return 0;
		if(isIP(player))
			return LoaderClass.data.getConfig().getLong("bans."+player+".tempbanip.time");
		return LoaderClass.data.getConfig().getLong("bans."+TheAPI.getPunishmentAPI().getIP(player)+".tempbanip.time");
	}
	
	private boolean isIP(String s) {
		s=s.replaceFirst("/", "");
        Matcher m = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$").matcher(s.toLowerCase());
   	     return m.find();
	}
}
