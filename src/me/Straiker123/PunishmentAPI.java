package me.Straiker123;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.Straiker123.Utils.Error;

public class PunishmentAPI {
	public List<String> getAccounts(String player){
		if(player==null) {
			Error.err("getting accounts of player", "Player is null");
			return new ArrayList<String>();
		}
		return findPlayerByIP(getIP(player));
	}
	public BanList getBanList() {
		return new BanList();
	}
	public boolean existPlayerOrIP(String string) {
		return LoaderClass.data.getConfig().getString("bans."+string)!=null;
	}
	public void setBan(String player, String reason) {
		if(player==null) {
			Error.err("banning player", "Player is null");
			return;
		}
		if(reason==null)reason="Uknown";
		LoaderClass.data.getConfig().set("bans."+player+".tempban", null);
		LoaderClass.data.getConfig().set("bans."+player+".ban", reason);
		LoaderClass.data.save();
		Player p = Bukkit.getPlayer(player);
		if(p!=null)
			p.kickPlayer(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.Ban")
				.replace("%player%", player)
				.replace("%reason%", reason)));
			if(!silent) {
				TheAPI.broadcastMessage(LoaderClass.config.getConfig().getString("Format.Broadcast.Ban")
						.replace("%player%", player)
						.replace("%reason%", reason));
			}else {
			TheAPI.broadcast(LoaderClass.config.getConfig().getString("Format.Broadcast.Ban")
					.replace("%player%", player)
					.replace("%reason%", reason),LoaderClass.config.getConfig().getString("Format.Broadcast-Ban-Permission"));
			}
		}
	public static boolean silent;
	public void setSilent(boolean silent) {
		PunishmentAPI.silent=silent;
	}
	public Jail getJailAPI() {
		return new Jail();
	}
	public void setTempBan(String player, String reason, long time) {
		if(player==null) {
			Error.err("temp-banning player", "Player is null");
			return;
		}
		if(reason==null)reason="Uknown";
		LoaderClass.data.getConfig().set("bans."+player+".ban", null);
		LoaderClass.data.getConfig().set("bans."+player+".tempban.reason", reason);
		LoaderClass.data.getConfig().set("bans."+player+".tempban.start", System.currentTimeMillis());
		LoaderClass.data.getConfig().set("bans."+player+".tempban.time", time);
		LoaderClass.data.save();
		Player p = Bukkit.getPlayer(player);
		if(p!=null)
		p.kickPlayer(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.TempBan")
				.replace("%player%", player)
				.replace("%reason%", reason)
				.replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(time)))); 
		if(!silent) {
			TheAPI.broadcastMessage(LoaderClass.config.getConfig().getString("Format.Broadcast.TempBan")
					.replace("%player%", player)
					.replace("%reason%", reason)
					.replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(time)));
		}else {
			TheAPI.broadcast(LoaderClass.config.getConfig().getString("Format.Broadcast.TempBan")
					.replace("%player%", player)
					.replace("%reason%", reason)
					.replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(time)),LoaderClass.config.getConfig().getString("Format.Broadcast-TempBan-Permission"));
		}
	}
	private boolean isIP(String text) {
		text=text.replaceFirst("/", "");
	    Pattern p = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
	    Matcher m = p.matcher(text);
	    return m.find();
	}

	public void setBanIP(String playerOrIP, String reason) {
		if(playerOrIP==null) {
			Error.err("ip-banning player/IP", "Player/IP is null");
			return;
		}
		if(reason==null)reason="Uknown";
		playerOrIP=playerOrIP.replace("_", ".");
		String ip =playerOrIP;
		if(!isIP(playerOrIP))ip=getIP(playerOrIP);
		else
			ip=ip.replace(".", "_");
		if(ip==null) {
			Error.err("temp ip-banning IP", "IP is null");
			return;
		}
		LoaderClass.data.getConfig().set("bans."+ip+".tempbanip", null);
		LoaderClass.data.getConfig().set("bans."+ip+".banip", reason);
		LoaderClass.data.save();
		for(String s : findPlayerByIP(ip)) {
		Player p = Bukkit.getPlayer(s);
		if(p!=null)
			p.kickPlayer(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.BanIP")
					.replace("%player%", s)
					.replace("%reason%", reason))); 
		}
		if(!silent) {
			TheAPI.broadcastMessage(LoaderClass.config.getConfig().getString("Format.Broadcast.BanIP")
					.replace("%target%", ip)
					.replace("%reason%", reason));
		}else {
			TheAPI.broadcast(LoaderClass.config.getConfig().getString("Format.Broadcast.BanIP")
					.replace("%target%", ip)
					.replace("%reason%", reason),LoaderClass.config.getConfig().getString("Format.Broadcast.BanIP-Permission"));
		}
	}
	public List<String> findPlayerByIP(String ip) {
		if(ip==null) {
			Error.err("finding player by IP", "IP is null");
			return new ArrayList<String>();
		}
		List<String> ips = new ArrayList<String>();
		if(LoaderClass.data.getConfig().getString("data")!=null)
		for(String s:LoaderClass.data.getConfig().getConfigurationSection("data").getKeys(false)) {
			if(ip.equals(getIP(s)))ips.add(s);
		}
		return ips;
	}
	
	public String getIP(String player) {
		if(player==null) {
			Error.err("getting IP of player", "Player is null");
			return null;
		}
		if(isIP(player))return player;
		if(LoaderClass.data.getConfig().getString("data."+player+".ip")!=null)
		return LoaderClass.data.getConfig().getString("data."+player+".ip").replace("_", ".");
				Error.err("getting IP of player "+player, "Player's IP is null");
				return null;
			
	}
	
	public void setMute(String player, String reason) {
		if(player==null) {
			Error.err("muting player", "Player is null");
			return;
		}
		if(reason==null)reason="Uknown"; 
		LoaderClass.data.getConfig().set("bans."+player+".tempmute", null);
		LoaderClass.data.getConfig().set("bans."+player+".mute", reason);
		LoaderClass.data.save(); 
		if(!silent) {
			TheAPI.broadcastMessage(LoaderClass.config.getConfig().getString("Format.Broadcast.Mute")
					.replace("%player%", player)
					.replace("%reason%", reason));
		}else {
			TheAPI.broadcast(LoaderClass.config.getConfig().getString("Format.Broadcast.Mute")
					.replace("%player%", player)
					.replace("%reason%", reason),LoaderClass.config.getConfig().getString("Format.Broadcast.Mute-Permission"));
		}
	}
	public void setTempMute(String player, String reason, long time) {
		if(player==null) {
			Error.err("temp-muting player", "Player is null");
			return;
		}
		if(reason==null)reason="Uknown";
		LoaderClass.data.getConfig().set("bans."+player+".mute", null);
		LoaderClass.data.getConfig().set("bans."+player+".tempmute.time", time);
		LoaderClass.data.getConfig().set("bans."+player+".tempmute.start", System.currentTimeMillis());
		LoaderClass.data.getConfig().set("bans."+player+".tempmute.reason", reason);
		LoaderClass.data.save(); 
		if(!silent) {
			TheAPI.broadcastMessage(LoaderClass.config.getConfig().getString("Format.Broadcast.TempMute")
					.replace("%player%", player)
					.replace("%reason%", reason).replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(time)));
			}else {
			TheAPI.broadcast(LoaderClass.config.getConfig().getString("Format.Broadcast.TempMute")
					.replace("%player%", player)
					.replace("%reason%", reason).replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(time))
					,LoaderClass.config.getConfig().getString("Format.Broadcast.TempMute-Permission"));
		}
	}
	public void setTempBanIP(String playerOrIP, String reason, long time) {
		if(playerOrIP==null) {
			Error.err("temp ip-banning player/IP", "Player/IP is null");
			return;
		}
		if(reason==null)reason="Uknown";
		playerOrIP=playerOrIP.replace("_", ".");
		String ip =playerOrIP;
		if(!isIP(ip))
		ip=getIP(playerOrIP);
		else
			ip=ip.replace(".", "_");
		if(ip==null) {
			Error.err("temp ip-banning IP", "IP is null");
			return;
		}
		LoaderClass.data.getConfig().set("bans."+ip+".banip", null);
		LoaderClass.data.getConfig().set("bans."+ip+".tempbanip.time", time);
		LoaderClass.data.getConfig().set("bans."+ip+".tempbanip.start", System.currentTimeMillis());
		LoaderClass.data.getConfig().set("bans."+ip+".tempbanip.reason", reason);
		LoaderClass.data.save();
		for(String s : findPlayerByIP(ip)) {
			Player p = Bukkit.getPlayer(s);
			if(p!=null)
				p.kickPlayer(TheAPI.colorize(LoaderClass.config.getConfig().getString("Format.TempBanIP")
						.replace("%player%", ip)
						.replace("%reason%", reason).replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(time))));
		}
		if(!silent) {
			TheAPI.broadcastMessage(LoaderClass.config.getConfig().getString("Format.Broadcast.TempBanIP")
					.replace("%target%", ip)
					.replace("%reason%", reason).replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(time)));
			}else {
			TheAPI.broadcast(LoaderClass.config.getConfig().getString("Format.Broadcast.TempBanIP")
					.replace("%target%", ip)
					.replace("%reason%", reason).replace("%time%", TheAPI.getTimeConventorAPI().setTimeToString(time))
					,LoaderClass.config.getConfig().getString("Format.Broadcast.TempBanIP-Permission"));
		}
	}
	public boolean hasBan(String player) {
		if(player==null)return false;
		return LoaderClass.data.getConfig().getString("bans."+player+".ban") != null;
	}
	public boolean hasTempMute(String player) {
		if(player==null)return false;
		if(getTempMuteStart(player)==-0)return false;
		long time = getTempMuteStart(player)/1000 - System.currentTimeMillis()/1000 + getTempMuteTime(player);
		return time > 0;
		}
	public boolean hasBanIP(String playerOrIP) {
		if(playerOrIP==null)return false;
		String test = playerOrIP.replace(".", "_");
		if(isIP(test))
			return LoaderClass.data.getConfig().getString("bans."+test+".banip") != null;
		return LoaderClass.data.getConfig().getString("bans."+getIP(playerOrIP)+".banip") != null;
	}
	public boolean hasTempBanIP(String playerOrIP) {
		if(playerOrIP==null)return false;
		try {
			String test = playerOrIP.replace(".", "_");
		if(isIP(test)) {
			if(getTempBanIPStart(test)==-0)return false;
		long time = getTempBanIPStart(test)/1000 - System.currentTimeMillis()/1000 + getTempBanIPTime(test);
		return time > 0;
		}else {
			if(getTempBanIPStart(getIP(playerOrIP))==-0)return false;
			long time =getTempBanIPStart(getIP(playerOrIP))/1000 - System.currentTimeMillis()/1000 + getTempBanIPTime(getIP(playerOrIP));
			return time > 0;
		}
		}catch(Exception e) {
			return false;
		}
	}
	public int getTempBanIP_ExpireTime(String playerOrIP){
		if(playerOrIP==null)return -0;
		String test = playerOrIP.replace(".", "_");
		if(isIP(test)) {
			long time =getTempBanIPStart(test)/1000 - System.currentTimeMillis()/1000 + getTempBanIPTime(test);
			return (int)time;
			}else {
				long time =getTempBanIPStart(getIP(playerOrIP))/1000 - System.currentTimeMillis()/1000 + getTempBanIPTime(getIP(playerOrIP));
				return (int)time;
			}
	}
	public boolean hasMute(String player) {
		if(player==null)return false;
		return LoaderClass.data.getConfig().getString("bans."+player+".mute") != null;
	}
	public boolean hasTempBan(String player) {
		if(player==null)return false;
		if(getTempBanStart(player)==-0)return false;
		long time = getTempBanStart(player)/1000 - System.currentTimeMillis()/1000 + getTempBanTime(player);
		return time > 0;
		}
	public long getTempBanExpireTime(String player) {
		if(player==null)return -0;
		long time = getTempBanStart(player)/1000 - System.currentTimeMillis()/1000 + getTempBanTime(player);
		return time;
	}
	public long getTempMuteExpireTime(String player) {
		if(player==null)return -0;
		long time = getTempMuteStart(player)/1000 - System.currentTimeMillis()/1000 + getTempMuteTime(player);
		return time;
	}
	public long getTempBanIPExpireTime(String playerOrIP) {
		if(playerOrIP==null)return -0;
		long time = getTempBanIPStart(playerOrIP)/1000 - System.currentTimeMillis()/1000 + getTempBanIPTime(playerOrIP);
		return time;
	}
	public String getBanReason(String player) {
		if(player==null)return null;
		return LoaderClass.data.getConfig().getString("bans."+player+".ban");
	}
	public String getTempBanReason(String player) {
		if(player==null)return null;
		return LoaderClass.data.getConfig().getString("bans."+player+".tempban.reason");
	}
	public String getBanIPReason(String playerOrIP) {
		if(playerOrIP==null)return null;
		if(isIP(playerOrIP))
			return LoaderClass.data.getConfig().getString("bans."+playerOrIP+".banip");
		return LoaderClass.data.getConfig().getString("bans."+getIP(playerOrIP)+".banip");
	}
	public String getMuteReason(String player) {
		if(player==null)return null;
		return LoaderClass.data.getConfig().getString("bans."+player+".mute");
	}
	public String getTempMuteReason(String player) {
		if(player==null)return null;
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
		return LoaderClass.data.getConfig().getLong("bans."+getIP(player)+".tempbanip.start");
	}
	public long getTempBanIPTime(String player) {
		if(player==null)return 0;
		if(isIP(player))
			return LoaderClass.data.getConfig().getLong("bans."+player+".tempbanip.time");
		return LoaderClass.data.getConfig().getLong("bans."+getIP(player)+".tempbanip.time");
	}
	public String getTempBanIPReason(String player) {
		if(player==null)return null;
		if(isIP(player))
			return LoaderClass.data.getConfig().getString("bans."+player+".tempbanip.reason");
		return LoaderClass.data.getConfig().getString("bans."+getIP(player)+".tempbanip.reason");
	}
	public void unMute(String player) {
		if(player==null)return;
		LoaderClass.data.getConfig().set("bans."+player+".mute", null);
		LoaderClass.data.save();
	}
	public void unTempMute(String player) {
		if(player==null)return;
		LoaderClass.data.getConfig().set("bans."+player+".tempmute", null);
		LoaderClass.data.save();
	}
	public void unBan(String player) {
		if(player==null)return;
		LoaderClass.data.getConfig().set("bans."+player+".ban", null);
		LoaderClass.data.save();
	}
	public void unTempBan(String player) {
		if(player==null)return;
		LoaderClass.data.getConfig().set("bans."+player+".tempban", null);
		LoaderClass.data.save();
	}
	public void unBanIP(String playerOrIP) {
		if(playerOrIP==null)return;
		if(isIP(playerOrIP))
			LoaderClass.data.getConfig().set("bans."+playerOrIP+".banip", null);
		else
			LoaderClass.data.getConfig().set("bans."+getIP(playerOrIP)+".banip", null);
		LoaderClass.data.save();
	}

	public void unTempBanIP(String playerOrIP) {
		if(playerOrIP==null)return;
		String test = playerOrIP.replace("_", ".");
		if(isIP(test))
			LoaderClass.data.getConfig().set("bans."+test+".tempbanip", null);
		else
			LoaderClass.data.getConfig().set("bans."+getIP(playerOrIP)+".tempbanip", null);
		LoaderClass.data.save();
	}
}
