package me.devtec.theapi.bukkit.nms.utils;

import java.util.Optional;

import com.google.common.collect.ImmutableList;

import me.devtec.shared.Ref;
import me.devtec.shared.components.Component;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.NmsProvider.DisplayType;

public class TeamUtils {

	public static final Class<?> sbTeam = Ref.getClass("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
	public static final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));
	public static final Object white = Ref.method(Ref.nms("", "EnumChatFormat"), "a", int.class) != null ? Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", int.class), 15)
			: Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", char.class), 'f');

	public static Object createTeamPacket(int mode, Object color, Component prefix, Component suffix, String holderName, String teamName) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
		Object nameList = ImmutableList.of(holderName);
		String always = "ALWAYS";
		if (Ref.isNewerThan(16)) {
			Ref.set(packet, "i", teamName);
			try {
				Object o = TeamUtils.unsafe.allocateInstance(TeamUtils.sbTeam);
				Ref.set(o, "a", BukkitLoader.getNmsProvider().chatBase("{\"text\":\"" + holderName + "\"}"));
				Ref.set(o, "b", BukkitLoader.getNmsProvider().toIChatBaseComponent(prefix));
				Ref.set(o, "c", BukkitLoader.getNmsProvider().toIChatBaseComponent(suffix));
				Ref.set(o, "d", always);
				Ref.set(o, "e", always);
				Ref.set(o, "f", color);
				Ref.set(packet, "k", Optional.of(o));
			} catch (Exception e) {
			}
			Ref.set(packet, "h", mode);
			Ref.set(packet, "j", nameList);
		} else {
			Ref.set(packet, "a", teamName);
			Ref.set(packet, "b", Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().chatBase("{\"text\":\"\"}") : "");
			Ref.set(packet, "c", Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().toIChatBaseComponent(prefix) : prefix == null ? "" : prefix.toString());
			Ref.set(packet, "d", Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().toIChatBaseComponent(suffix) : suffix == null ? "" : suffix.toString());
			if (Ref.isNewerThan(7)) {
				Ref.set(packet, "e", always);
				Ref.set(packet, "f", Ref.isNewerThan(8) ? always : -1);
				if (Ref.isNewerThan(8))
					Ref.set(packet, "g", Ref.isNewerThan(12) ? color : -1);
				Ref.set(packet, Ref.isNewerThan(8) ? "i" : "h", mode);
				Ref.set(packet, Ref.isNewerThan(8) ? "h" : "g", nameList);
			} else {
				Ref.set(packet, "f", mode);
				Ref.set(packet, "e", nameList);
			}
		}
		return packet;
	}

	public static Object createObjectivePacket(int mode, String name, String displayName, DisplayType type) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardObjective();
		if (Ref.isNewerThan(16)) {
			Ref.set(packet, "d", name);
			Ref.set(packet, "e", BukkitLoader.getNmsProvider().chatBase("{\"text\":\"" + displayName + "\"}"));
			Ref.set(packet, "f", BukkitLoader.getNmsProvider().getEnumScoreboardHealthDisplay(type));
			Ref.set(packet, "g", mode);
		} else {
			Ref.set(packet, "a", name);
			Ref.set(packet, "b", Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().chatBase("{\"text\":\"" + displayName + "\"}") : displayName);
			if (Ref.isNewerThan(7)) {
				Ref.set(packet, "c", BukkitLoader.getNmsProvider().getEnumScoreboardHealthDisplay(type));
				Ref.set(packet, "d", mode);
			} else
				Ref.set(packet, "c", mode);
		}
		return packet;
	}
}
