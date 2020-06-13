package me.DevTec;

import org.bukkit.entity.Player;

import me.DevTec.Utils.Error;
import net.glowstone.entity.GlowPlayer;

public class TabListAPI {

	public void setTabListName(Player p, String name) {
		p.setPlayerListName(TheAPI.colorize(name));
	}

	public void setHeaderFooter(Player p, String header, String footer) {
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
		TheAPI.getNMSAPI().sendPacket(p, TheAPI.getNMSAPI()
				.getPacketPlayOutPlayerListHeaderFooter(TheAPI.colorize(header), TheAPI.colorize(footer)));
	}
}
