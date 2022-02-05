package me.devtec.theapi.apis;

import java.util.List;

import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.nms.NmsProvider.PlayerInfoType;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.Validator;

public class TabListAPI {

	public static void setTabListName(Player p, String name) {
		Validator.validate(p == null, "Player is null");
		Object obj = TheAPI.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_DISPLAY_NAME, p);
		Ref.set(((List<?>)Ref.get(obj,"b")).get(0), "d", ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(StringUtils.colorize(name==null?p.getName():name), true)));
		Ref.sendPacket(TheAPI.getOnlinePlayers(), obj);
	}

	public static void setHeaderFooter(Player p, String header, String footer) {
		Validator.validate(p == null, "Player is null");
		Object ff = LoaderClass.nmsProvider.packetPlayerListHeaderFooter(TheAPI.colorize(header), TheAPI.colorize(footer));
		if(ff!=null)Ref.sendPacket(p, ff);
	}
}
