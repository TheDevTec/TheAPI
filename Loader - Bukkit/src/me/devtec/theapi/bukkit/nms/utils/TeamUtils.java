package me.devtec.theapi.bukkit.nms.utils;

import java.util.Optional;

import com.google.common.collect.ImmutableList;

import me.devtec.shared.Ref;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.theapi.bukkit.BukkitLoader;

public class TeamUtils {

	public static final Class<?> sbTeam = Ref
			.getClass("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
	public static final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref
			.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));
	public static final Object white = Ref.method(Ref.nms("", "EnumChatFormat"), "a", char.class) == null
			? Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", int.class), -1)
			: Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", char.class), 'f');

	public static Object createTeamPacket(int mode, Object color, String prefix, String suffix, String name,
			String realName) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
		Object nameList = ImmutableList.of(name);
		String always = "ALWAYS";
		if (Ref.isNewerThan(16)) {
			Ref.set(packet, "i", realName);
			try {
				Object o = TeamUtils.unsafe.allocateInstance(TeamUtils.sbTeam);
				Ref.set(o, "a", BukkitLoader.getNmsProvider().chatBase("{\"text\":\"" + name + "\"}"));
				Ref.set(o, "b", BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(prefix)));
				Ref.set(o, "c", BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(suffix)));
				Ref.set(o, "d", always);
				Ref.set(o, "e", always);
				Ref.set(o, "f", TeamUtils.white);
				Ref.set(packet, "k", Optional.of(o));
			} catch (Exception e) {
			}
			Ref.set(packet, "h", mode);
			Ref.set(packet, "j", nameList);
		} else {
			Ref.set(packet, "a", realName);
			Ref.set(packet, "b", Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().chatBase("{\"text\":\"\"}") : "");
			Ref.set(packet, "c",
					Ref.isNewerThan(12)
							? BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(prefix))
							: prefix);
			Ref.set(packet, "d",
					Ref.isNewerThan(12)
							? BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(suffix))
							: suffix);
			if (Ref.isNewerThan(7)) {
				Ref.set(packet, "e", always);
				Ref.set(packet, "f", Ref.isNewerThan(8) ? always : -1);
				if (Ref.isNewerThan(8))
					Ref.set(packet, "g", Ref.isNewerThan(12) ? TeamUtils.white : -1);
				Ref.set(packet, Ref.isNewerThan(8) ? "i" : "h", mode);
				Ref.set(packet, Ref.isNewerThan(8) ? "h" : "g", nameList);
			} else {
				Ref.set(packet, "f", mode);
				Ref.set(packet, "e", nameList);
			}
		}
		return packet;
	}
}
