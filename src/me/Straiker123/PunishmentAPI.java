package me.Straiker123;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.google.common.collect.Lists;

import me.Straiker123.Abstract.AbstractPunishmentAPI;

public class PunishmentAPI implements AbstractPunishmentAPI {
	private static final ConfigAPI c = LoaderClass.data;
	private static final BanList banlist = new me.Straiker123.BanList();

	@Override
	public String getIP(String player) {
		if (AbstractPunishmentAPI.isIP(player))
			new Exception("PunishmentAPI error, String must be player, not IP.");
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
			for (String s : getPlayersOnIP(playerOrIP)) // This ban all players on ip
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
		kick(playerOrIP, reason);
		c.set("banip." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		c.set("banip." + playerOrIP + ".reason", reason);
		c.save();
	}

	@Override
	public void tempban(String playerOrIP, String reason, long time) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP)) // This ban all players on ip
				ban(s, reason);
			return;
		}
		kick(playerOrIP, reason);
		c.set("tempban." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		c.set("tempban." + playerOrIP + ".time", time);
		c.set("tempban." + playerOrIP + ".reason", reason);
		c.save();
	}

	@Override
	public void tempbanIP(String playerOrIP, String reason, long time) {
		if (!AbstractPunishmentAPI.isIP(playerOrIP))
			playerOrIP = getIP(playerOrIP);
		kick(playerOrIP, reason);
		c.set("tempbanip." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		c.set("tempbanip." + playerOrIP + ".time", time);
		c.set("tempbanip." + playerOrIP + ".reason", reason);
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
	public void warn(String playerOrIP, String reason) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				warn(s, reason);
			return;
		}
		if (TheAPI.getPlayer(playerOrIP) != null)
			TheAPI.msg(reason, TheAPI.getPlayer(playerOrIP));
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
	public void jail(String playerOrIP, String reason, String jail) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				jail(s, reason, jail);
			return;
		}
		c.set("jail." + playerOrIP + ".id", jail);
		c.set("jail." + playerOrIP + ".reason", reason);
		c.set("jail." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		c.save();
		if (TheAPI.getPlayer(playerOrIP) != null) {
			TheAPI.getPlayer(playerOrIP)
					.teleport(TheAPI.getStringUtils().getLocationFromString(c.getString("jails." + jail)));
			TheAPI.msg(reason, TheAPI.getPlayer(playerOrIP));
		}
	}

	@Override
	public void tempjail(String playerOrIP, String reason, long time) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				jail(s, reason);
			return;
		}
		tempjail(playerOrIP, reason, time, TheAPI.getRandomFromList(getjails()).toString());
	}

	@Override
	public void tempjail(String playerOrIP, String reason, long time, String jail) {
		if (AbstractPunishmentAPI.isIP(playerOrIP)) {
			for (String s : getPlayersOnIP(playerOrIP))
				jail(s, reason, jail);
			return;
		}
		c.set("tempjail." + playerOrIP + ".id", jail);
		c.set("tempjail.." + playerOrIP + ".time", jail);
		c.set("tempjail.." + playerOrIP + ".reason", reason);
		c.set("tempjail.." + playerOrIP + ".start", System.currentTimeMillis() / 1000);
		c.save();
		if (TheAPI.getPlayer(playerOrIP) != null) {
			TheAPI.getPlayer(playerOrIP)
					.teleport(TheAPI.getStringUtils().getLocationFromString(c.getString("jails." + jail)));
			TheAPI.msg(reason, TheAPI.getPlayer(playerOrIP));
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
		c.set("banip." + playerOrIP, null);
		c.set("tempbanip." + playerOrIP, null);
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
			TheAPI.msg(reason, TheAPI.getPlayer(playerOrIP));
	}

	@Override
	public BanList getBanList() {
		return banlist;
	}

	@Override
	public PlayerBanList getBanList(String player) {
		return new me.Straiker123.PlayerBanList(player);
	}
}
