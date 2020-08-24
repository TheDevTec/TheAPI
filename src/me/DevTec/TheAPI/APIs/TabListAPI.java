package me.DevTec.TheAPI.APIs;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Error;
import net.glowstone.entity.GlowPlayer;

public class TabListAPI {

	public static void setTabListName(Player p, String name) {
		NMSAPI.getNMSPlayerAPI(p).setTabListName(TheAPI.colorize(name));
	}

	public static void setHeaderFooter(Player p, String header, String footer) {
		if (p == null) {
			Error.err("sending header/footer", "Player is null");
			return;
		}
		if (TheAPI.getServerVersion().equals("glowstone")) {
			try {
				((GlowPlayer) p).setPlayerListHeaderFooter(TheAPI.colorize(header), TheAPI.colorize(footer));
				return;
			} catch (Exception e) {
				Error.err("sending header/footer to " + p.getName(), "Header/Footer is null");
			}
		}
		NMSAPI.getNMSPlayerAPI(p).setTabList(TheAPI.colorize(header), TheAPI.colorize(footer));
	}
}
