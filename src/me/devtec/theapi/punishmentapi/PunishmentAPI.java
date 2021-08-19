package me.devtec.theapi.punishmentapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.bukkit.Location;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class PunishmentAPI {
	private static final BanList banlist = new BanList();
	private static final Pattern ipFinder = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)[\\._]){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

	public static boolean isIP(String ip) {
		if (ip == null)return false;
		ip=fixIP(ip);
		return ipFinder.matcher(ip).find();
	}

	public static String getIP(String player) {
		if (player == null)
			return null;
		if ((player.contains(".") || player.contains("_")) && isIP(fixIP(player)))
			return fixIP(player);
		if(!TheAPI.existsUser(player))return null;
		return TheAPI.getUser(player).exists("ip") ? TheAPI.getUser(player).getString("ip") : null;
	}

	protected static String fixIP(String ip) {
		ip=ip.replace("_", ".").replaceAll("[^0-9.]+", "").replaceAll("\\.\\.", "");
		while(ip.startsWith("."))
			ip=ip.substring(1);
		while(ip.endsWith("."))
			ip=ip.substring(0,ip.length()-1);
		return ip;
	}
	
	public static List<String> getPlayersOnIP(String ip) {
		if(ip==null)return new ArrayList<>();
		ip=fixIP(ip);
		if (!isIP(ip))
			new Exception("PunishmentAPI error, String must be IP, not player.").printStackTrace();
		ip=ip.replace(".", "_");
		return LoaderClass.data.getStringList("data."+ip);
	}

	public static void ban(String playerOrIP, String reason) {
		if(playerOrIP==null)return;
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				ban(s, reason);
			return;
		}
		kick(playerOrIP, reason);
		/*if(LoaderClass.banlist!=null) {
			if(LoaderClass.banlist.exists("ta_banlist", "name", playerOrIP)) {
				LoaderClass.banlist.query("");
				//LoaderClass.banlist.set("ta_banlist", "name", playerOrIP);
			}
			return;
		}*/
		LoaderClass.data.set("ban." + playerOrIP.toLowerCase() + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.set("ban." + playerOrIP.toLowerCase() + ".reason", reason);
		LoaderClass.data.save();
	}

	public static void banIP(String playerOrIP, String reason) {
		if(playerOrIP==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		if(playerOrIP==null)return;
		kickIP(playerOrIP, reason);
		LoaderClass.data.set("banip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.set("banip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.save();
	}

	public static void tempban(String playerOrIP, String reason, long time) {
		if(playerOrIP==null)return;
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				tempban(s, reason, time);
			return;
		}
		kick(playerOrIP, reason==null?null:reason.replace("%time%", StringUtils.timeToString(time)));
		LoaderClass.data.set("tempban." + playerOrIP.toLowerCase() + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.set("tempban." + playerOrIP.toLowerCase() + ".time", time);
		LoaderClass.data.set("tempban." + playerOrIP.toLowerCase() + ".reason", reason);
		LoaderClass.data.save();
	}

	public static void tempbanIP(String playerOrIP, String reason, long time) {
		if(playerOrIP==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		if(playerOrIP==null)return;
		kickIP(playerOrIP, reason==null?null:reason.replace("%time%", StringUtils.timeToString(time)));
		LoaderClass.data.set("tempbanip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.set("tempbanip." + playerOrIP.replace(".", "_") + ".time", time);
		LoaderClass.data.set("tempbanip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.save();
	}

	public static void kick(String playerOrIP, String reason) {
		if(playerOrIP==null)return;
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				kick(s, reason);
			return;
		}
		if (TheAPI.getPlayerOrNull(playerOrIP) != null)
			TheAPI.getPlayerOrNull(playerOrIP).kickPlayer(TheAPI.colorize((""+reason).replace("\\n", "\n")));
	}

	public static void kickIP(String playerOrIP, String reason) {
		kick(playerOrIP, reason);
	}

	public static void warn(String player, String reason) {
		if(player==null)return;
		if (isIP(player)) {
			for (String s : getPlayersOnIP(player))
				warn(s, reason);
			return;
		}
		if (TheAPI.getPlayerOrNull(player) != null)
			TheAPI.msg(reason, TheAPI.getPlayerOrNull(player));
	}

	public static void warnIP(String playerOrIP, String reason) {
		warn(playerOrIP, reason);
	}

	public static void jail(String playerOrIP, String reason) {
		if(playerOrIP==null)return;
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				jail(s, reason);
			return;
		}
		jail(playerOrIP, reason, TheAPI.getRandomFromList(getjails()).toString());
	}

	public static void jailIP(String playerOrIP, String reason) {
		if(playerOrIP==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		jailIP(playerOrIP, reason, TheAPI.getRandomFromList(getjails()).toString());
	}

	public static void jailIP(String playerOrIP, String reason, String jail) {
		if(playerOrIP==null||jail==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		if(playerOrIP==null)return;
		LoaderClass.data.set("jailip." + playerOrIP.replace(".", "_") + ".id", jail);
		LoaderClass.data.set("jailip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.set("jailip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		for (String player : getPlayersOnIP(playerOrIP))
			if (TheAPI.getPlayerOrNull(player) != null) {
				TheAPI.getPlayerOrNull(player)
						.teleport(StringUtils.getLocationFromString(LoaderClass.data.getString("jails." + jail)));
				TheAPI.msg(reason, TheAPI.getPlayerOrNull(player));
			}
	}

	public static void jail(String player, String reason, String jail) {
		if(player==null||jail==null)return;
		if (isIP(player)) {
			for (String s : getPlayersOnIP(player))
				jail(s, reason, jail);
			return;
		}
		LoaderClass.data.set("jail." + player.toLowerCase() + ".id", jail);
		LoaderClass.data.set("jail." + player.toLowerCase() + ".reason", reason);
		LoaderClass.data.set("jail." + player.toLowerCase() + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if (TheAPI.getPlayerOrNull(player) != null) {
			TheAPI.getPlayerOrNull(player)
					.teleport(StringUtils.getLocationFromString(LoaderClass.data.getString("jails." + jail)));
			TheAPI.msg(reason, TheAPI.getPlayerOrNull(player));
		}
	}

	public static void tempjail(String player, String reason, long time) {
		if(player==null)return;
		if (isIP(player)) {
			for (String s : getPlayersOnIP(player))
				tempjail(s, reason.replace("%time%", StringUtils.timeToString(time)), time);
			return;
		}
		tempjail(player, reason.replace("%time%", StringUtils.timeToString(time)), time,
				TheAPI.getRandomFromList(getjails()).toString());
	}

	public static void tempjail(String player, String reason, long time, String jail) {
		if(player==null||jail==null)return;
		if (isIP(player)) {
			for (String s : getPlayersOnIP(player))
				tempjail(s, reason, time, jail);
			return;
		}
		LoaderClass.data.set("tempjail." + player.toLowerCase() + ".id", jail);
		LoaderClass.data.set("tempjail." + player.toLowerCase() + ".time", time);
		LoaderClass.data.set("tempjail." + player.toLowerCase() + ".reason", reason);
		LoaderClass.data.set("tempjail." + player.toLowerCase() + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if (TheAPI.getPlayerOrNull(player) != null) {
			TheAPI.getPlayerOrNull(player)
					.teleport(StringUtils.getLocationFromString(LoaderClass.data.getString("jails." + jail)));
			if(reason!=null)
			TheAPI.msg(reason.replace("%time%", StringUtils.timeToString(time)), TheAPI.getPlayerOrNull(player));
		}
	}

	public static void tempjailIP(String playerOrIP, String reason, long time) {
		if(playerOrIP==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		tempjailIP(playerOrIP, reason, time, TheAPI.getRandomFromList(getjails()).toString());
	}

	public static void tempjailIP(String playerOrIP, String reason, long time, String jail) {
		if(playerOrIP==null || jail==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		if(playerOrIP==null)return;
		LoaderClass.data.set("tempjailip." + playerOrIP.replace(".", "_") + ".id", jail);
		LoaderClass.data.set("tempjailip." + playerOrIP.replace(".", "_") + ".time", time);
		LoaderClass.data.set("tempjailip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.set("tempjailip." + playerOrIP.replace(".", "_") + ".start",
				System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		for (String player : getPlayersOnIP(playerOrIP))
			if (TheAPI.getPlayerOrNull(player) != null) {
				TheAPI.getPlayerOrNull(player)
						.teleport(StringUtils.getLocationFromString(LoaderClass.data.getString("jails." + jail)));
				if(reason!=null)
				TheAPI.msg(reason.replace("%time%", StringUtils.timeToString(time)), TheAPI.getPlayerOrNull(player));
			}
	}

	public static void setjail(Location location, String name) {
		if(location==null||name==null)return;
		LoaderClass.data.set("jails." + name, StringUtils.getLocationAsString(location));
		LoaderClass.data.save();
	}

	public static void deljail(String name) {
		if(name==null)return;
		LoaderClass.data.set("jails." + name, null);
		LoaderClass.data.save();
	}

	public static List<String> getjails() {
		return new ArrayList<>(LoaderClass.data.getKeys("jails"));
	}

	public static void unban(String playerOrIP) {
		if(playerOrIP==null)return;
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP.toLowerCase()))
				unban(s);
			return;
		}
		LoaderClass.data.remove("ban." + playerOrIP.toLowerCase());
		LoaderClass.data.remove("tempban." + playerOrIP.toLowerCase());
		LoaderClass.data.save();
	}

	public static void unbanIP(String playerOrIP) {
		if(playerOrIP==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		if(playerOrIP==null)return;
		LoaderClass.data.remove("banip." + playerOrIP.replace(".", "_"));
		LoaderClass.data.remove("tempbanip." + playerOrIP.replace(".", "_"));
		LoaderClass.data.save();
	}

	public static void unjail(String playerOrIP) {
		if(playerOrIP==null)return;
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				unjail(s);
			return;
		}
		LoaderClass.data.remove("jail." + playerOrIP.toLowerCase());
		LoaderClass.data.remove("tempjail." + playerOrIP.toLowerCase());
		LoaderClass.data.save();
	}

	public static void unjailIP(String playerOrIP) {
		if(playerOrIP==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		if(playerOrIP==null)return;
		LoaderClass.data.remove("jailip." + playerOrIP.replace(".", "_"));
		LoaderClass.data.remove("tempjailip." + playerOrIP.replace(".", "_"));
		LoaderClass.data.save();
	}

	public static void unmute(String playerOrIP) {
		if(playerOrIP==null)return;
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				unmute(s);
			return;
		}
		LoaderClass.data.remove("mute." + playerOrIP.toLowerCase());
		LoaderClass.data.remove("tempmute." + playerOrIP.toLowerCase());
		LoaderClass.data.save();
	}

	public static void unmuteIP(String playerOrIP) {
		if(playerOrIP==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		if(playerOrIP==null)return;
		LoaderClass.data.remove("muteip." + playerOrIP.replace(".", "_"));
		LoaderClass.data.remove("tempmuteip." + playerOrIP.replace(".", "_"));
		LoaderClass.data.save();
	}

	public static void mute(String playerOrIP, String reason) {
		if(playerOrIP==null)return;
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				mute(s, reason);
			return;
		}
		LoaderClass.data.set("mute." + playerOrIP.toLowerCase() + ".reason", reason);
		LoaderClass.data.set("mute." + playerOrIP.toLowerCase() + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if (TheAPI.getPlayerOrNull(playerOrIP) != null && reason!=null)
			TheAPI.msg(reason, TheAPI.getPlayerOrNull(playerOrIP));
	}

	public static void tempmute(String playerOrIP, String reason, long time) {
		if(playerOrIP==null)return;
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				tempmute(s, reason, time);
			return;
		}
		LoaderClass.data.set("tempmute." + playerOrIP.toLowerCase() + ".reason", reason);
		LoaderClass.data.set("tempmute." + playerOrIP.toLowerCase() + ".time", time);
		LoaderClass.data.set("tempmute." + playerOrIP.toLowerCase() + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if (TheAPI.getPlayerOrNull(playerOrIP) != null && reason!=null)
			TheAPI.msg(reason.replace("%time%", StringUtils.timeToString(time)), TheAPI.getPlayerOrNull(playerOrIP));
	}

	public static void muteIP(String playerOrIP, String reason) {
		if(playerOrIP==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		if(playerOrIP==null)return;
		LoaderClass.data.set("muteip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.set("muteip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if(reason!=null)
		for (String player : getPlayersOnIP(playerOrIP))
			if (TheAPI.getPlayerOrNull(player) != null)
				TheAPI.msg(reason, TheAPI.getPlayerOrNull(player));
	}

	public static void tempmuteIP(String playerOrIP, String reason, long time) {
		if(playerOrIP==null)return;
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		if(playerOrIP==null)return;
		LoaderClass.data.set("tempmuteip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.set("tempmuteip." + playerOrIP.replace(".", "_") + ".time", time);
		LoaderClass.data.set("tempmuteip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if(reason!=null)
		for (String player : getPlayersOnIP(playerOrIP))
			if (TheAPI.getPlayerOrNull(player) != null)
				TheAPI.msg(reason.replace("%time%", StringUtils.timeToString(time)), TheAPI.getPlayerOrNull(player));
	}

	public static BanList getBanList() {
		return banlist;
	}

	private static final Map<String, PlayerBanList> players = new HashMap<>(); 
	
	public static PlayerBanList getBanList(String player) {
		if(player==null)return null;
		PlayerBanList banlist = players.get(player.toLowerCase());
		if(banlist==null) {
			banlist=new PlayerBanList(player);
			players.put(player.toLowerCase(), banlist);
		}
		return banlist;
	}
}
