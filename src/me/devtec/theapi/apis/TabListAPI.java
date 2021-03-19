package me.devtec.theapi.apis;

import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.ChatMessage;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.Validator;

public class TabListAPI {

	public static void setTabListName(Player p, String name) {
		Ref.set(Ref.player(p), "listName", TheAPI.isOlderThan(8) ? TheAPI.colorize(name) : NMSAPI.getIChatBaseComponentJson(new ChatMessage(TheAPI.colorize(name)).getJson()));
	}

	public static void setHeaderFooter(Player p, String header, String footer) {
		Validator.validate(p == null, "Player is null");
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutPlayerListHeaderFooter(TheAPI.colorize(header), TheAPI.colorize(footer)));
	}
}
