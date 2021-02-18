package me.devtec.theapi.punishmentapi;

import me.devtec.theapi.utils.thapiutils.LoaderClass;

public class PlayerBanList {
	private final String s, ip;

	public PlayerBanList(String player) {
		s = player;
		ip=PunishmentAPI.getIP(s);
	}

	public boolean isTempBanned() {
		return getExpire(PunishmentType.TEMPBAN) > 0;
	}

	public boolean isBanned() {
		return getExpire(PunishmentType.BAN) > 0;
	}

	public boolean isTempIPBanned() {
		return getExpire(PunishmentType.TEMPBANIP) > 0;
	}

	public boolean isIPBanned() {
		return getExpire(PunishmentType.BANIP) > 0;
	}

	public boolean isTempMuted() {
		return getExpire(PunishmentType.TEMPMUTE) > 0;
	}

	public boolean isMuted() {
		return getExpire(PunishmentType.MUTE) > 0;
	}

	public boolean isTempJailed() {
		return getExpire(PunishmentType.TEMPJAIL) > 0;
	}

	public boolean isJailed() {
		return getExpire(PunishmentType.JAIL) > 0;
	}

	public boolean isTempIPMuted() {
		return getExpire(PunishmentType.TEMPMUTEIP) > 0;
	}

	public boolean isIPMuted() {
		return getExpire(PunishmentType.MUTEIP) > 0;
	}

	public boolean isTempIPJailed() {
		return getExpire(PunishmentType.TEMPJAILIP) > 0;
	}

	public boolean isIPJailed() {
		return getExpire(PunishmentType.JAILIP) > 0;
	}

	public long getExpire(PunishmentType type) {
		if (type == PunishmentType.BAN || type == PunishmentType.BANIP || type == PunishmentType.JAIL
				|| type == PunishmentType.MUTE || type == PunishmentType.MUTEIP)
			return getStartTime(type) != 0 ? 1 : 0;
		if (getStartTime(type) <= 0 || getTime(type) <= 0)
			return 0;
		return getStartTime(type) - System.currentTimeMillis() / 1000 + getTime(type);
	}

	public String getReason(PunishmentType type) {
		String r = null;
		switch (type) {
		case BAN:
			r = LoaderClass.data.getString("ban." + s.toLowerCase() + ".reason");
			break;
		case BANIP:
			if (getIP() != null)
				r = LoaderClass.data.getString("banip." + getIP().replace(".", "_") + ".reason");
			break;
		case TEMPBAN:
			r = LoaderClass.data.getString("tempban." + s.toLowerCase() + ".reason");
			break;
		case TEMPBANIP:
			if (getIP() != null)
				r = LoaderClass.data.getString("tempbanip." + getIP().replace(".", "_") + ".reason");
			break;
		case JAIL:
			r = LoaderClass.data.getString("jail." + s.toLowerCase() + ".reason");
			break;
		case TEMPJAIL:
			r = LoaderClass.data.getString("tempjail." + s.toLowerCase() + ".reason");
			break;
		case MUTE:
			r = LoaderClass.data.getString("mute." + s.toLowerCase() + ".reason");
			break;
		case TEMPMUTE:
			r = LoaderClass.data.getString("tempmute." + s.toLowerCase() + ".reason");
			break;
		case JAILIP:
			r = LoaderClass.data.getString("mute." + s.toLowerCase() + ".reason");
			break;
		case MUTEIP:
			if (getIP() != null)
				r = LoaderClass.data.getString("muteip." + getIP().replace(".", "_") + ".reason");
			break;
		case TEMPJAILIP:
			if (getIP() != null)
				r = LoaderClass.data.getString("tempjailip." + getIP().replace(".", "_") + ".reason");
			break;
		case TEMPMUTEIP:
			if (getIP() != null)
				r = LoaderClass.data.getString("tempmuteip." + getIP().replace(".", "_") + ".reason");
			break;
		}
		return r;
	}

	public long getStartTime(PunishmentType type) {
		long r = 0;
		switch (type) {
		case BAN:
			if (LoaderClass.data.exists("ban." + s.toLowerCase() + ".start"))
				r = LoaderClass.data.getLong("ban." + s.toLowerCase() + ".start");
			break;
		case BANIP:
			if (getIP() != null)
				if (LoaderClass.data.exists("banip." + getIP().replace(".", "_") + ".start"))
					r = LoaderClass.data.getLong("banip." + getIP().replace(".", "_") + ".start");
			break;
		case TEMPBAN:
			if (LoaderClass.data.exists("tempban." + s.toLowerCase() + ".start"))
				r = LoaderClass.data.getLong("tempban." + s.toLowerCase() + ".start");
			break;
		case TEMPBANIP:
			if (getIP() != null)
				if (LoaderClass.data.exists("tempbanip." + getIP().replace(".", "_") + ".start"))
					r = LoaderClass.data.getLong("tempbanip." + getIP().replace(".", "_") + ".start");
			break;
		case JAIL:
			if (LoaderClass.data.exists("jail." + s.toLowerCase() + ".start"))
				r = LoaderClass.data.getLong("jail." + s.toLowerCase() + ".start");
			break;
		case TEMPJAIL:
			if (LoaderClass.data.exists("tempjail." + s.toLowerCase() + ".start"))
				r = LoaderClass.data.getLong("tempjail." + s.toLowerCase() + ".start");
			break;
		case MUTE:
			if (LoaderClass.data.exists("mute." + s.toLowerCase() + ".start"))
				r = LoaderClass.data.getLong("mute." + s.toLowerCase() + ".start");
			break;
		case TEMPMUTE:
			if (LoaderClass.data.exists("tempmute." + s.toLowerCase() + ".start"))
				r = LoaderClass.data.getLong("tempmute." + s.toLowerCase() + ".start");
			break;
		case JAILIP:
			if (getIP() != null)
				if (LoaderClass.data.exists("jailip." + getIP().replace(".", "_") + ".start"))
					r = LoaderClass.data.getLong("jailip." + getIP().replace(".", "_") + ".start");
			break;
		case MUTEIP:
			if (getIP() != null)
				if (LoaderClass.data.exists("muteip." + getIP().replace(".", "_") + ".start"))
					r = LoaderClass.data.getLong("muteip." + getIP().replace(".", "_") + ".start");
			break;
		case TEMPJAILIP:
			if (getIP() != null)
				if (LoaderClass.data.exists("tempjailip." + getIP().replace(".", "_") + ".start"))
					r = LoaderClass.data.getLong("tempjailip." + getIP().replace(".", "_") + ".start");
			break;
		case TEMPMUTEIP:
			if (getIP() != null)
				if (LoaderClass.data.exists("tempmuteip." + getIP().replace(".", "_") + ".start"))
					r = LoaderClass.data.getLong("tempmuteip." + getIP().replace(".", "_") + ".start");
			break;
		}
		return r;
	}

	public long getTime(PunishmentType type) {
		long r = 0;
		switch (type) {
		case TEMPBANIP:
			if (getIP() != null)
				if (LoaderClass.data.exists("tempbanip." + getIP().replace(".", "_") + ".time"))
					r = LoaderClass.data.getLong("tempbanip." + getIP().replace(".", "_") + ".time");
			break;
		case TEMPJAIL:
			if (LoaderClass.data.exists("tempjail." + s.toLowerCase() + ".time"))
				r = LoaderClass.data.getLong("tempjail." + s.toLowerCase() + ".time");
			break;
		case TEMPMUTE:
			if (LoaderClass.data.exists("tempmute." + s.toLowerCase() + ".time"))
				r = LoaderClass.data.getLong("tempmute." + s.toLowerCase() + ".time");
			break;
		case TEMPBAN:
			if (LoaderClass.data.exists("tempban." + s.toLowerCase() + ".time"))
				r = LoaderClass.data.getLong("tempban." + s.toLowerCase() + ".time");
			break;
		case TEMPJAILIP:
			if (getIP() != null)
				if (LoaderClass.data.exists("tempjailip." + getIP().replace(".", "_") + ".time"))
					r = LoaderClass.data.getLong("tempjailip." + getIP().replace(".", "_") + ".time");
			break;
		case TEMPMUTEIP:
			if (getIP() != null)
				if (LoaderClass.data.exists("tempmuteip." + getIP().replace(".", "_") + ".time"))
					r = LoaderClass.data.getLong("tempmuteip." + getIP().replace(".", "_") + ".time");
			break;
		default:
			break;
		}
		return r;
	}

	public static enum PunishmentType {
		MUTE, MUTEIP, TEMPMUTE, TEMPMUTEIP, BAN, TEMPBAN, BANIP, TEMPBANIP, JAIL, JAILIP, TEMPJAIL, TEMPJAILIP
	}

	public String getIP() {
		return ip;
	}
}
