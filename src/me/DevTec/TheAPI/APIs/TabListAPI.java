package me.DevTec.TheAPI.APIs;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Error;

public class TabListAPI {

	public static void setTabListName(Player p, String name) {
		NMSAPI.getNMSPlayerAPI(p).setTabListName(TheAPI.colorize(name));
	}

	public static void setHeaderFooter(Player p, String header, String footer) {
		if (p == null) {
			Error.err("sending header/footer", "Player is null");
			return;
		}
		NMSAPI.getNMSPlayerAPI(p).setTabList(TheAPI.colorize(header), TheAPI.colorize(footer));
	}
}
