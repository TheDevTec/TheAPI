package me.DevTec.Bans;

import me.DevTec.ConfigAPI;
import me.DevTec.TheAPI;
import me.DevTec.Abstract.AbstractPunishmentAPI;
import me.DevTec.Other.LoaderClass;

public class PlayerBanList {
	private static final ConfigAPI c = LoaderClass.data;
	private final String s;

	public PlayerBanList(String player) {
		if (AbstractPunishmentAPI.isIP(player))
			new Exception("PlayerBanList error, String must be player, not IP.");
		s = player;
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
				|| type == PunishmentType.MUTE)
			return getStartTime(type) != 0 ? 1 : 0;
		if (getStartTime(type) <= 0 || getTime(type) <= 0)
			return 0;
		return getStartTime(type) - System.currentTimeMillis() / 1000 + getTime(type);
	}

	public String getReason(PunishmentType type) {
		String r = null;
		switch (type) {
		case BAN:
			r = c.getString("ban." + s + ".reason");
			break;
		case BANIP:
			if(getIP()!=null)
			r = c.getString("banip." + getIP().replace(".", "_") + ".reason");
			break;
		case TEMPBAN:
			r = c.getString("tempban." + s + ".reason");
			break;
		case TEMPBANIP:
			if(getIP()!=null)
			r = c.getString("tempbanip." + getIP().replace(".", "_") + ".reason");
			break;
		case JAIL:
			r = c.getString("jail." + s + ".reason");
			break;
		case TEMPJAIL:
			r = c.getString("tempjail." + s + ".reason");
			break;
		case MUTE:
			r = c.getString("mute." + s + ".reason");
			break;
		case TEMPMUTE:
			r = c.getString("tempmute." + s + ".reason");
			break;
		case JAILIP:
			r = c.getString("mute." + s + ".reason");
			break;
		case MUTEIP:
			if(getIP()!=null)
			r = c.getString("muteip." + getIP().replace(".", "_") + ".reason");
			break;
		case TEMPJAILIP:
			if(getIP()!=null)
			r = c.getString("tempjailip." + getIP().replace(".", "_") + ".reason");
			break;
		case TEMPMUTEIP:
			if(getIP()!=null)
			r = c.getString("tempmuteip." + getIP().replace(".", "_")+ ".reason");
			break;
		}
		return r;
	}

	public long getStartTime(PunishmentType type) {
		long r = 0;
		switch (type) {
		case BAN:
			if (c.exist("ban." + s + ".start"))
				r = c.getLong("ban." + s + ".start");
			break;
		case BANIP:
			if(getIP()!=null)
			if (c.exist("banip." + getIP().replace(".", "_") + ".start"))
				r = c.getLong("banip." + getIP().replace(".", "_") + ".start");
			break;
		case TEMPBAN:
			if (c.exist("tempban." + s + ".start"))
				r = c.getLong("tempban." + s + ".start");
			break;
		case TEMPBANIP:
			if(getIP()!=null)
			if (c.exist("tempbanip." + getIP().replace(".", "_") + ".start"))
				r = c.getLong("tempbanip." + getIP().replace(".", "_") + ".start");
			break;
		case JAIL:
			if (c.exist("jail." + s + ".start"))
				r = c.getLong("jail." + s + ".start");
			break;
		case TEMPJAIL:
			if (c.exist("tempjail." + s + ".start"))
				r = c.getLong("tempjail." + s + ".start");
			break;
		case MUTE:
			if (c.exist("mute." + s + ".start"))
				r = c.getLong("mute." + s + ".start");
			break;
		case TEMPMUTE:
			if (c.exist("tempmute." + s + ".start"))
				r = c.getLong("tempmute." + s + ".start");
			break;
		case JAILIP:
			if(getIP()!=null)
			if (c.exist("jailip." +getIP().replace(".", "_") + ".start"))
				r = c.getLong("jailip." + getIP().replace(".", "_") + ".start");
			break;
		case MUTEIP:
			if(getIP()!=null)
			if (c.exist("muteip." +getIP().replace(".", "_") + ".start"))
				r = c.getLong("muteip." + getIP().replace(".", "_") + ".start");
			break;
		case TEMPJAILIP:
			if(getIP()!=null)
			if (c.exist("tempjailip." +getIP().replace(".", "_") + ".start"))
				r = c.getLong("tempjailip." + getIP().replace(".", "_") + ".start");
			break;
		case TEMPMUTEIP:
			if(getIP()!=null)
			if (c.exist("tempmuteip." +getIP().replace(".", "_") + ".start"))
				r = c.getLong("tempmuteip." + getIP().replace(".", "_") + ".start");
			break;
		}
		return r;
	}

	public long getTime(PunishmentType type) {
		long r = 0;
		switch (type) {
		case TEMPBANIP:
			if(getIP()!=null)
			if (c.exist("tempbanip." + getIP().replace(".", "_") + ".time"))
				r = c.getLong("tempbanip." + getIP().replace(".", "_") + ".time");
			break;
		case TEMPJAIL:
			if (c.exist("tempjail." + s + ".time"))
				r = c.getLong("tempjail." + s + ".time");
			break;
		case TEMPMUTE:
			if (c.exist("tempmute." + s + ".time"))
				r = c.getLong("tempmute." + s + ".time");
			break;
		case TEMPBAN:
			if (c.exist("tempban." + s + ".time"))
				r = c.getLong("tempban." + s + ".time");
			break;
		case TEMPJAILIP:
			if(getIP()!=null)
			if (c.exist("tempjailip." +getIP().replace(".", "_") + ".time"))
				r = c.getLong("tempjailip." + getIP().replace(".", "_") + ".time");
			break;
		case TEMPMUTEIP:
			if(getIP()!=null)
			if (c.exist("tempmuteip." +getIP().replace(".", "_") + ".time"))
				r = c.getLong("tempmuteip." + getIP().replace(".", "_") + ".time");
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
		return TheAPI.getPunishmentAPI().getIP(s);
	}

}
