package me.devtec.theapi.apis;

import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.Validator;

public class TabListAPI {

	public static void setTabListName(Player p, String name) {
		Validator.validate(p == null, "Player is null");
		Ref.set(Ref.player(p), "listName", TheAPI.isOlderThan(8) ? TheAPI.colorize(name) : ComponentAPI.toIChatBaseComponent(TheAPI.colorize(name), true));
	}

	public static void setHeaderFooter(Player p, String header, String footer) {
		Validator.validate(p == null, "Player is null");
		Object ff = LoaderClass.nmsProvider.packetPlayerListHeaderFooter(TheAPI.colorize(header), TheAPI.colorize(footer));
		if(ff!=null)Ref.sendPacket(p, ff);
	}
}
