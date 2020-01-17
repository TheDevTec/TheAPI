package me.Straiker123;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import me.Straiker123.Utils.Error;
import me.Straiker123.Utils.Packets;
import net.glowstone.entity.GlowPlayer;

public class TabListAPI {

	public void setTabListName(Player p, String name) {
		p.setPlayerListName(TheAPI.colorize(name));
	}

	public void setHeaderFooter(Player p, String header, String footer) {
		if(p==null) {
			Error.err("sending header/footer", "Player is null");
			return;
		}
		if(TheAPI.getServerVersion().equals("glowstone")) {
		try {
			((GlowPlayer) p).setPlayerListHeaderFooter(TheAPI.colorize(header),TheAPI.colorize(footer));
			return;
		}catch (Exception e) {
			Error.err("sending header/footer to "+p.getName(), "Header/Footer is null");
		}
		}
		try {
			Object tabHeader = Packets.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + TheAPI.colorize(header) + "\"}" });
			Object tabFooter = Packets.getNMSClass("IChatBaseComponent")
					.getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + TheAPI.colorize(footer) + "\"}" });
			Constructor<?> titleConstructor = Packets.getNMSClass("PacketPlayOutPlayerListHeaderFooter").getConstructor(new Class[0]);
			Object packet = titleConstructor.newInstance(new Object[0]);
			Field aField = null;
			Field bField = null;
			if (TheAPI.isNewVersion() && !TheAPI.getServerVersion().equals("v1_13_R1")) {
			    aField = packet.getClass().getDeclaredField("header");
			    bField = packet.getClass().getDeclaredField("footer");
			} else {
			   aField = packet.getClass().getDeclaredField("a");
			   bField = packet.getClass().getDeclaredField("b");
			}
			   aField.setAccessible(true);
			   aField.set(packet, tabHeader);
			bField.setAccessible(true);
			bField.set(packet, tabFooter);
			Packets.sendPacket(p,packet);
			} catch (Exception e) {
				Error.err("sending header/footer to "+p.getName(), "Header/Footer is null");
			}
	}
}
