package me.DevTec.TheAPI.APIs;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

public class TabListAPI {

	public static void setTabListName(Player p, String name) {
		NMSAPI.getNMSPlayerAPI(p).setTabListName(TheAPI.colorize(name));
	}

	public static void setHeaderFooter(Player p, String header, String footer) {
		Validator.validate(p == null,"Player is null");
		Validator.validate(header == null,"Header is null");
		Validator.validate(footer == null,"Footer is null");
		NMSAPI.getNMSPlayerAPI(p).setTabList(TheAPI.colorize(header), TheAPI.colorize(footer));
	}
}
