package me.Straiker123;

import me.Straiker123.Abstract.AbstractPunishmentAPI;

public class PlayerBanList {
	private static final ConfigAPI c = LoaderClass.data;
	private String s;
	public PlayerBanList(String player) {
		if(AbstractPunishmentAPI.isIP(player))
			new Exception("PlayerBanList error, String must be player, not IP.");
		s=player;
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
	
	public long getExpire(PunishmentType type) {
		if(type==PunishmentType.BAN||type==PunishmentType.BANIP||type==PunishmentType.JAIL||type==PunishmentType.MUTE)
			return 1;
		return getStartTime(type)-System.currentTimeMillis()/1000+getTime(type);
	}

	public String getReason(PunishmentType type) {
		String r = null;
		switch(type) {
		case BAN:
			r=c.getString("ban."+s+".reason");
			break;
		case BANIP:
			r=c.getString("banip."+TheAPI.getPunishmentAPI().getIP(s).replace(".", "_")+".reason");
			break;
		case TEMPBAN:
			r=c.getString("tempban."+s+".reason");
			break;
		case TEMPBANIP:
			r=c.getString("tempbanip."+TheAPI.getPunishmentAPI().getIP(s).replace(".", "_")+".reason");
			break;
		case JAIL:
			r=c.getString("jail."+s+".reason");
			break;
		case TEMPJAIL:
			r=c.getString("tempjail."+s+".reason");
			break;
		case MUTE:
			r=c.getString("mute."+s+".reason");
			break;
		case TEMPMUTE:
			r=c.getString("tempmute."+s+".reason");
			break;
		}
		return r;
	}

	public long getStartTime(PunishmentType type) {
		long r = 0;
		switch(type) {
		case BAN:
			r=c.getLong("ban."+s+".start");
			break;
		case BANIP:
			r=c.getLong("banip."+TheAPI.getPunishmentAPI().getIP(s).replace(".", "_")+".start");
			break;
		case TEMPBAN:
			r=c.getLong("tempban."+s+".start");
			break;
		case TEMPBANIP:
			r=c.getLong("tempbanip."+TheAPI.getPunishmentAPI().getIP(s).replace(".", "_")+".start");
			break;
		case JAIL:
			r=c.getLong("jail."+s+".start");
			break;
		case TEMPJAIL:
			r=c.getLong("tempjail."+s+".start");
			break;
		case MUTE:
			r=c.getLong("mute."+s+".start");
			break;
		case TEMPMUTE:
			r=c.getLong("tempmute."+s+".start");
			break;
		}
		return r;
	}

	public long getTime(PunishmentType type) {
		long r = 0;
		switch(type) {
		case TEMPBANIP:
			r=c.getLong("tempbanip."+TheAPI.getPunishmentAPI().getIP(s).replace(".", "_")+".time");
			break;
		case TEMPJAIL:
			r=c.getLong("tempjail."+s+".time");
			break;
		case TEMPMUTE:
			r=c.getLong("tempmute."+s+".time");
			break;
		case TEMPBAN:
			r=c.getLong("tempban."+s+".time");
			break;
		default:
			break;
		}
		return r;
	}

	public static enum PunishmentType {
		MUTE,
		TEMPMUTE,
		BAN,
		TEMPBAN,
		BANIP,
		TEMPBANIP,
		JAIL,
		TEMPJAIL
	}
	
}
