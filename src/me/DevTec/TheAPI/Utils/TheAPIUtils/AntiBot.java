package me.DevTec.TheAPI.Utils.TheAPIUtils;

import java.util.List;
import java.util.UUID;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.PunishmentAPI.BanList;
import me.DevTec.TheAPI.PunishmentAPI.PlayerBanList;
import me.DevTec.TheAPI.PunishmentAPI.PunishmentAPI;
import me.DevTec.TheAPI.Utils.DataKeeper.Collections.UnsortedList;

public class AntiBot {
	private static long last;
	private static List<String> c = new UnsortedList<String>();

	public static boolean hasAccess(UUID e) {
		boolean s = canJoin();
		if (!s)
			c.add(e.toString());
		last = System.currentTimeMillis() / 100;
		return s;
	}

	public static boolean isDisallowed(String uuid) {
		String p = TheAPI.getUser(uuid).getName();
		PlayerBanList a = PunishmentAPI.getBanList(p);
		return c.contains(uuid) || a.isBanned() || a.isTempBanned() || a.isIPBanned() || a.isTempIPBanned();
	}

	public static boolean containsDisallowed(String string) {
		boolean f = false;
		for (String s : c) {
			if (string.contains(s)) {
				f = true;
				break;
			}
		}
		if (!f)
			for (String s : BanList.getBanned()) {
				if (string.contains(TheAPI.getUser(s).getUUID().toString())) {
					f = true;
					break;
				}
			}
		return f;
	}

	public static boolean canJoin() {
		if (!LoaderClass.config.getBoolean("Options.AntiBot.Use"))
			return true;
		return last - System.currentTimeMillis() / 100
				+ LoaderClass.config.getLong("Options.AntiBot.TimeBetweenPlayer") < 0;
	}
}
