package me.DevTec.Bans;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.google.common.collect.Lists;

import me.DevTec.ConfigAPI;
import me.DevTec.TheAPI;
import me.DevTec.Abstract.AbstractPunishmentAPI;
import me.DevTec.Other.LoaderClass;

public class PunishmentAPI implements AbstractPunishmentAPI {
	private static final ConfigAPI c = LoaderClass.data;
	private static final BanList banlist = new BanList();

	@Override
	public String getIP(String player) {
		if(player==null)return null;
		if (AbstractPunishmentAPI.isIP(player))
			new Exception("PunishmentAPI error, String must be player, not IP.").printStackTrace();
		return TheAPI.getUser(player).getString("ip").replace("_", ".");
	}

	@Override
	public List<String> getPlayersOnIP(String ip) {
		if (!AbstractPunishmentAPI.isIP(ip))
			new Exception("PunishmentAPI error, String must be IP, not player.");
		ArrayList<String> list = Lists.newArrayList();
		if (c.exist("data"))
			for (String s : c.getKeys("data")) {
				if (ip.equals(getIP(s)))
					list.add(s);
			}
		return list;
	}

	@Override
	public void ban(String playerOrIP, String reason) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				ban(s, reason);
			return;
		}
		kick(playerOrIP, reason);
		c.set("ban." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		c.set("ban." + playerOrIP + ".reason", reason);
		c.save();
	}

	@Override
	public void banIP(String playerOrIP, String reason) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		kickIP(playerOrIP, reason);
		c.set("banip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		c.set("banip." + playerOrIP.replace(".", "_") + ".reason", reason);
		c.save();
	}

	@Override
	public void tempban(String playerOrIP, String reason, long time) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				tempban(s, reason, time);
			return;
		}
		kick(playerOrIP, reason.replace("%time%", TheAPI.getStringUtils().timeToString(time)));
		c.set("tempban." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		c.set("tempban." + playerOrIP + ".time", time);
		c.set("tempban." + playerOrIP + ".reason", reason);
		c.save();
	}

	@Override
	public void tempbanIP(String playerOrIP, String reason, long time) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		kickIP(playerOrIP, reason.replace("%time%", TheAPI.getStringUtils().timeToString(time)));
		c.set("tempbanip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		c.set("tempbanip." + playerOrIP.replace(".", "_") + ".time", time);
		c.set("tempbanip." + playerOrIP.replace(".", "_") + ".reason", reason);
		c.save();
	}

	@Override
	public void kick(String playerOrIP, String reason) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				kick(s, reason);
			return;
		}
		if (TheAPI.getPlayer(playerOrIP) != null)
			TheAPI.getPlayer(playerOrIP).kickPlayer(TheAPI.colorize(reason.replace("\\n", "\n")));
	}

	@Override
	public void kickIP(String playerOrIP, String reason) {
		kick(playerOrIP, reason);
	}

	@Override
	public void warn(String player, String reason) {
		if (AbstractPunishmentAPI.isIP(player)) {
			for (String s : getPlayersOnIP(player))
				warn(s, reason);
			return;
		}
		if (TheAPI.getPlayer(player) != null)
			TheAPI.msg(reason, TheAPI.getPlayer(player));
	}

	@Override
	public void warnIP(String playerOrIP, String reason) {
		warn(playerOrIP, reason);
	}

	@Override
	public void jail(String playerOrIP, String reason) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				jail(s, reason);
			return;
		}
		jail(playerOrIP, reason, TheAPI.getRandomFromList(getjails()).toString());
	}

	@Override
	public void jailIP(String playerOrIP, String reason) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP=getIP(playerOrIP);
		jailIP(playerOrIP, reason, TheAPI.getRandomFromList(getjails()).toString());
	}

	@Override
	public void jailIP(String playerOrIP, String reason, String jail) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		c.set("jailip." + playerOrIP.replace(".", "_") + ".id", jail);
		c.set("jailip." + playerOrIP.replace(".", "_") + ".reason", reason);
		c.set("jailip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		c.save();
		for(String player : getPlayersOnIP(playerOrIP))
		if (TheAPI.getPlayer(player) != null) {
			TheAPI.getPlayer(player)
					.teleport(TheAPI.getStringUtils().getLocationFromString(c.getString("jails." + jail)));
			TheAPI.msg(reason, TheAPI.getPlayer(player));
		}
	}
	
	@Override
	public void jail(String player, String reason, String jail) {
		if (AbstractPunishmentAPI.isIP(player)) {
			for (String s : getPlayersOnIP(player))
				jail(s, reason, jail);
			return;
		}
		c.set("jail." + player + ".id", jail);
		c.set("jail." + player + ".reason", reason);
		c.set("jail." + player + ".start", System.currentTimeMillis() / 1000);
		c.save();
		if (TheAPI.getPlayer(player) != null) {
			TheAPI.getPlayer(player)
					.teleport(TheAPI.getStringUtils().getLocationFromString(c.getString("jails." + jail)));
			TheAPI.msg(reason, TheAPI.getPlayer(player));
		}
	}

	@Override
	public void tempjail(String player, String reason, long time) {
		if (AbstractPunishmentAPI.isIP(player)) {
			for (String s : getPlayersOnIP(player))
				tempjail(s, reason.replace("%time%", TheAPI.getStringUtils().timeToString(time)), time);
			return;
		}
		tempjail(player, reason.replace("%time%", TheAPI.getStringUtils().timeToString(time)), time, TheAPI.getRandomFromList(getjails()).toString());
	}

	@Override
	public void tempjail(String player, String reason, long time, String jail) {
		if (AbstractPunishmentAPI.isIP(player)) {
			for (String s : getPlayersOnIP(player))
				tempjail(s, reason, time, jail);
			return;
		}
		c.set("tempjail." + player + ".id", jail);
		c.set("tempjail." + player + ".time", jail);
		c.set("tempjail." + player + ".reason", reason);
		c.set("tempjail." + player + ".start", System.currentTimeMillis() / 1000);
		c.save();
		if (TheAPI.getPlayer(player) != null) {
			TheAPI.getPlayer(player)
					.teleport(TheAPI.getStringUtils().getLocationFromString(c.getString("jails." + jail)));
			TheAPI.msg(reason.replace("%time%", TheAPI.getStringUtils().timeToString(time)), TheAPI.getPlayer(player));
		}
	}

	@Override
	public void tempjailIP(String playerOrIP, String reason, long time) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		tempjailIP(playerOrIP, reason, time, TheAPI.getRandomFromList(getjails()).toString());
	}

	@Override
	public void tempjailIP(String playerOrIP, String reason, long time, String jail) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		c.set("tempjailip." + playerOrIP.replace(".", "_") + ".id", jail);
		c.set("tempjailip." + playerOrIP.replace(".", "_") + ".time", jail);
		c.set("tempjailip." + playerOrIP.replace(".", "_") + ".reason", reason);
		c.set("tempjailip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		c.save();
		for(String player : getPlayersOnIP(playerOrIP))
		if (TheAPI.getPlayer(player) != null) {
			TheAPI.getPlayer(player)
					.teleport(TheAPI.getStringUtils().getLocationFromString(c.getString("jails." + jail)));
			TheAPI.msg(reason.replace("%time%", TheAPI.getStringUtils().timeToString(time)), TheAPI.getPlayer(player));
		}
	}

	@Override
	public void setjail(Location location, String name) {
		c.set("jails." + name, TheAPI.getStringUtils().getLocationAsString(location));
		c.save();
	}

	@Override
	public void deljail(String name) {
		c.set("jails." + name, null);
		c.save();
	}

	@Override
	public List<String> getjails() {
		ArrayList<String> list = Lists.newArrayList();
		for (String s : c.getKeys("jails"))
			list.add(s);
		return list;
	}

	@Override
	public void unban(String playerOrIP) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				unban(s);
			return;
		}
		c.set("ban." + playerOrIP, null);
		c.set("tempban." + playerOrIP, null);
		c.save();
	}

	@Override
	public void unbanIP(String playerOrIP) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		c.set("banip." + playerOrIP.replace(".", "_"), null);
		c.set("tempbanip." + playerOrIP.replace(".", "_"), null);
		c.save();
	}

	@Override
	public void unjail(String playerOrIP) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				unjail(s);
			return;
		}
		c.set("jail." + playerOrIP, null);
		c.set("tempjail." + playerOrIP, null);
		c.save();
	}

	@Override
	public void unjailIP(String playerOrIP) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		c.set("jailip." + playerOrIP.replace(".", "_"), null);
		c.set("tempjailip." + playerOrIP.replace(".", "_"), null);
		c.save();
	}

	@Override
	public void unmute(String playerOrIP) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				unmute(s);
			return;
		}
		c.set("mute." + playerOrIP, null);
		c.set("tempmute." + playerOrIP, null);
		c.save();
	}

	@Override
	public void unmuteIP(String playerOrIP) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		c.set("muteip." + playerOrIP.replace(".", "_"), null);
		c.set("tempmuteip." + playerOrIP.replace(".", "_"), null);
		c.save();
	}

	@Override
	public void mute(String playerOrIP, String reason) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				mute(s, reason);
			return;
		}
		c.set("mute." + playerOrIP + ".reason", reason);
		c.set("mute." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		c.save();
		if (TheAPI.getPlayer(playerOrIP) != null)
			TheAPI.msg(reason, TheAPI.getPlayer(playerOrIP));
	}

	@Override
	public void tempmute(String playerOrIP, String reason, long time) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				tempmute(s, reason, time);
			return;
		}
		c.set("tempmute." + playerOrIP + ".reason", reason);
		c.set("tempmute." + playerOrIP + ".time", time);
		c.set("tempmute." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		c.save();
		if (TheAPI.getPlayer(playerOrIP) != null)
			TheAPI.msg(reason.replace("%time%", TheAPI.getStringUtils().timeToString(time)), TheAPI.getPlayer(playerOrIP));
	}

	@Override
	public void muteIP(String playerOrIP, String reason) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		c.set("muteip." + playerOrIP.replace(".", "_") + ".reason", reason);
		c.set("muteip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		c.save();
		for(String player : getPlayersOnIP(playerOrIP))
		if (TheAPI.getPlayer(player) != null)
			TheAPI.msg(reason, TheAPI.getPlayer(player));
	}

	@Override
	public void tempmuteIP(String playerOrIP, String reason, long time) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		c.set("tempmuteip." + playerOrIP.replace(".", "_") + ".reason", reason);
		c.set("tempmuteip." + playerOrIP.replace(".", "_") + ".time", time);
		c.set("tempmuteip." + playerOrIP.replace(".", "_") + ".start", System.currentTimeMillis() / 1000);
		c.save();
		for(String player : getPlayersOnIP(playerOrIP))
		if (TheAPI.getPlayer(player) != null)
			TheAPI.msg(reason.replace("%time%", TheAPI.getStringUtils().timeToString(time)), TheAPI.getPlayer(player));
	}

	@Override
	public BanList getBanList() {
		return banlist;
	}

	@Override
	public PlayerBanList getBanList(String player) {
		return new PlayerBanList(player);
	}
}
