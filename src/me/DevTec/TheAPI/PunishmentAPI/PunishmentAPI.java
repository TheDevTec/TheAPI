package me.DevTec.TheAPI.PunishmentAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Location;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

public class PunishmentAPI {
	private static final BanList banlist = new BanList();
	private static final Pattern ipFinder = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
	
	public static boolean isIP(String text) {
		if(text==null)return false;
		return ipFinder.matcher(text.replaceFirst("/", "")).find();
	}
	
	public static String getIP(String player) {
		if(player==null)return null;
		if (isIP(player))
			player=getPlayersOnIP(player).get(0);
		return TheAPI.getUser(player).exist("ip")?TheAPI.getUser(player).getString("ip").replace("_", "."):null;
	}

	public static List<String> getPlayersOnIP(String ip) {
		if (!isIP(ip))
			new Exception("PunishmentAPI error, String must be IP, not player.");
		List<String> list = new ArrayList<>();
		if (LoaderClass.data.exists("data"))
			for (String s : LoaderClass.data.getKeys("data")) {
				if (ip.equals(getIP(s)))
					list.add(s);
			}
		return list;
	}

	public static void ban(String playerOrIP, String reason) {
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				ban(s, reason);
			return;
		}
		kick(playerOrIP, reason);
		LoaderClass.data.set("ban." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.set("ban." + playerOrIP + ".reason", reason);
		LoaderClass.data.save();
	}

	public static void banIP(String playerOrIP, String reason) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		kickIP(playerOrIP, reason);
		LoaderClass.data.set("banip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.set("banip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.save();
	}

	public static void tempban(String playerOrIP, String reason, long time) {
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				tempban(s, reason, time);
			return;
		}
		kick(playerOrIP, reason.replace("%time%", StringUtils.timeToString(time)));
		LoaderClass.data.set("tempban." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.set("tempban." + playerOrIP + ".time", time);
		LoaderClass.data.set("tempban." + playerOrIP + ".reason", reason);
		LoaderClass.data.save();
	}

	public static void tempbanIP(String playerOrIP, String reason, long time) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		kickIP(playerOrIP, reason.replace("%time%", StringUtils.timeToString(time)));
		LoaderClass.data.set("tempbanip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.set("tempbanip." + playerOrIP.replace(".", "_") + ".time", time);
		LoaderClass.data.set("tempbanip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.save();
	}


	public static void kick(String playerOrIP, String reason) {
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				kick(s, reason);
			return;
		}
		if (TheAPI.getPlayer(playerOrIP) != null)
			TheAPI.getPlayer(playerOrIP).kickPlayer(TheAPI.colorize(reason.replace("\\n", "\n")));
	}


	public static void kickIP(String playerOrIP, String reason) {
		kick(playerOrIP, reason);
	}


	public static void warn(String player, String reason) {
		if (isIP(player)) {
			for (String s : getPlayersOnIP(player))
				warn(s, reason);
			return;
		}
		if (TheAPI.getPlayer(player) != null)
			TheAPI.msg(reason, TheAPI.getPlayer(player));
	}


	public static void warnIP(String playerOrIP, String reason) {
		warn(playerOrIP, reason);
	}


	public static void jail(String playerOrIP, String reason) {
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				jail(s, reason);
			return;
		}
		jail(playerOrIP, reason, TheAPI.getRandomFromList(getjails()).toString());
	}


	public static void jailIP(String playerOrIP, String reason) {
		if (!isIP(playerOrIP))
			playerOrIP=getIP(playerOrIP);
		jailIP(playerOrIP, reason, TheAPI.getRandomFromList(getjails()).toString());
	}


	public static void jailIP(String playerOrIP, String reason, String jail) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		LoaderClass.data.set("jailip." + playerOrIP.replace(".", "_") + ".id", jail);
		LoaderClass.data.set("jailip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.set("jailip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		for(String player : getPlayersOnIP(playerOrIP))
		if (TheAPI.getPlayer(player) != null) {
			TheAPI.getPlayer(player)
					.teleport(StringUtils.getLocationFromString(LoaderClass.data.getString("jails." + jail)));
			TheAPI.msg(reason, TheAPI.getPlayer(player));
		}
	}
	

	public static void jail(String player, String reason, String jail) {
		if (isIP(player)) {
			for (String s : getPlayersOnIP(player))
				jail(s, reason, jail);
			return;
		}
		LoaderClass.data.set("jail." + player + ".id", jail);
		LoaderClass.data.set("jail." + player + ".reason", reason);
		LoaderClass.data.set("jail." + player + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if (TheAPI.getPlayer(player) != null) {
			TheAPI.getPlayer(player)
					.teleport(StringUtils.getLocationFromString(LoaderClass.data.getString("jails." + jail)));
			TheAPI.msg(reason, TheAPI.getPlayer(player));
		}
	}


	public static void tempjail(String player, String reason, long time) {
		if (isIP(player)) {
			for (String s : getPlayersOnIP(player))
				tempjail(s, reason.replace("%time%", StringUtils.timeToString(time)), time);
			return;
		}
		tempjail(player, reason.replace("%time%", StringUtils.timeToString(time)), time, TheAPI.getRandomFromList(getjails()).toString());
	}


	public static void tempjail(String player, String reason, long time, String jail) {
		if (isIP(player)) {
			for (String s : getPlayersOnIP(player))
				tempjail(s, reason, time, jail);
			return;
		}
		LoaderClass.data.set("tempjail." + player + ".id", jail);
		LoaderClass.data.set("tempjail." + player + ".time", jail);
		LoaderClass.data.set("tempjail." + player + ".reason", reason);
		LoaderClass.data.set("tempjail." + player + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if (TheAPI.getPlayer(player) != null) {
			TheAPI.getPlayer(player)
					.teleport(StringUtils.getLocationFromString(LoaderClass.data.getString("jails." + jail)));
			TheAPI.msg(reason.replace("%time%", StringUtils.timeToString(time)), TheAPI.getPlayer(player));
		}
	}


	public static void tempjailIP(String playerOrIP, String reason, long time) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		tempjailIP(playerOrIP, reason, time, TheAPI.getRandomFromList(getjails()).toString());
	}


	public static void tempjailIP(String playerOrIP, String reason, long time, String jail) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		LoaderClass.data.set("tempjailip." + playerOrIP.replace(".", "_") + ".id", jail);
		LoaderClass.data.set("tempjailip." + playerOrIP.replace(".", "_") + ".time", jail);
		LoaderClass.data.set("tempjailip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.set("tempjailip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		for(String player : getPlayersOnIP(playerOrIP))
		if (TheAPI.getPlayer(player) != null) {
			TheAPI.getPlayer(player)
					.teleport(StringUtils.getLocationFromString(LoaderClass.data.getString("jails." + jail)));
			TheAPI.msg(reason.replace("%time%", StringUtils.timeToString(time)), TheAPI.getPlayer(player));
		}
	}


	public static void setjail(Location location, String name) {
		LoaderClass.data.set("jails." + name, StringUtils.getLocationAsString(location));
		LoaderClass.data.save();
	}


	public static void deljail(String name) {
		LoaderClass.data.set("jails." + name, null);
		LoaderClass.data.save();
	}


	public static List<String> getjails() {
		ArrayList<String> list = new ArrayList<>();
		for (String s : LoaderClass.data.getKeys("jails"))
			list.add(s);
		return list;
	}


	public static void unban(String playerOrIP) {
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				unban(s);
			return;
		}
		LoaderClass.data.set("ban." + playerOrIP, null);
		LoaderClass.data.set("tempban." + playerOrIP, null);
		LoaderClass.data.save();
	}


	public static void unbanIP(String playerOrIP) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		LoaderClass.data.set("banip." + playerOrIP.replace(".", "_"), null);
		LoaderClass.data.set("tempbanip." + playerOrIP.replace(".", "_"), null);
		LoaderClass.data.save();
	}


	public static void unjail(String playerOrIP) {
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				unjail(s);
			return;
		}
		LoaderClass.data.set("jail." + playerOrIP, null);
		LoaderClass.data.set("tempjail." + playerOrIP, null);
		LoaderClass.data.save();
	}


	public static void unjailIP(String playerOrIP) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		LoaderClass.data.set("jailip." + playerOrIP.replace(".", "_"), null);
		LoaderClass.data.set("tempjailip." + playerOrIP.replace(".", "_"), null);
		LoaderClass.data.save();
	}


	public static void unmute(String playerOrIP) {
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				unmute(s);
			return;
		}
		LoaderClass.data.set("mute." + playerOrIP, null);
		LoaderClass.data.set("tempmute." + playerOrIP, null);
		LoaderClass.data.save();
	}


	public static void unmuteIP(String playerOrIP) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		LoaderClass.data.set("muteip." + playerOrIP.replace(".", "_"), null);
		LoaderClass.data.set("tempmuteip." + playerOrIP.replace(".", "_"), null);
		LoaderClass.data.save();
	}


	public static void mute(String playerOrIP, String reason) {
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				mute(s, reason);
			return;
		}
		LoaderClass.data.set("mute." + playerOrIP + ".reason", reason);
		LoaderClass.data.set("mute." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if (TheAPI.getPlayer(playerOrIP) != null)
			TheAPI.msg(reason, TheAPI.getPlayer(playerOrIP));
	}


	public static void tempmute(String playerOrIP, String reason, long time) {
		if (isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				tempmute(s, reason, time);
			return;
		}
		LoaderClass.data.set("tempmute." + playerOrIP + ".reason", reason);
		LoaderClass.data.set("tempmute." + playerOrIP + ".time", time);
		LoaderClass.data.set("tempmute." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		if (TheAPI.getPlayer(playerOrIP) != null)
			TheAPI.msg(reason.replace("%time%", StringUtils.timeToString(time)), TheAPI.getPlayer(playerOrIP));
	}


	public static void muteIP(String playerOrIP, String reason) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		LoaderClass.data.set("muteip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.set("muteip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		for(String player : getPlayersOnIP(playerOrIP))
		if (TheAPI.getPlayer(player) != null)
			TheAPI.msg(reason, TheAPI.getPlayer(player));
	}


	public static void tempmuteIP(String playerOrIP, String reason, long time) {
		if (!isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		LoaderClass.data.set("tempmuteip." + playerOrIP.replace(".", "_") + ".reason", reason);
		LoaderClass.data.set("tempmuteip." + playerOrIP.replace(".", "_") + ".time", time);
		LoaderClass.data.set("tempmuteip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		LoaderClass.data.save();
		for(String player : getPlayersOnIP(playerOrIP))
		if (TheAPI.getPlayer(player) != null)
			TheAPI.msg(reason.replace("%time%", StringUtils.timeToString(time)), TheAPI.getPlayer(player));
	}


	public static BanList getBanList() {
		return banlist;
	}


	public static PlayerBanList getBanList(String player) {
		return new PlayerBanList(player);
	}
}
