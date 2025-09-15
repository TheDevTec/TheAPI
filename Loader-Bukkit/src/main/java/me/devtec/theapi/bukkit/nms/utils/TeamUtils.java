package me.devtec.theapi.bukkit.nms.utils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.Getter;
import me.devtec.shared.Ref;
import me.devtec.shared.annotations.Nullable;
import me.devtec.shared.components.Component;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.NmsProvider.DisplayType;

public class TeamUtils {

	@Nullable
	public static final Class<?> sbTeam;// class of PacketPlayOutScoreboardTeam$b
	public static final Object white;// ChatFormatting
	public static final Field name;// String
	public static final Field teamMethod;// int
	public static final Field players; // Collection<String>
	@Nullable // 1.16+ only
	public static final Field parameters; // Optional(PacketPlayOutScoreboardTeam$b)

	public static final Field teamDisplayName; // IChatBaseComponent or String
	public static final Field playerPrefix; // IChatBaseComponent or String
	public static final Field playerSuffix; // IChatBaseComponent or String
	public static final Field nametagVisibility; // String
	@Nullable // 1.8+ only
	public static final Field collisionRule; // String
	@Nullable // 1.8+ only
	public static final Field color;// ChatFormatting
	@Nullable // 1.9+ only
	public static final Field options; // int

	public static final Field objectiveName; // String
	public static final Field objectiveDisplayName; // IChatBaseComponent or String
	public static final Field renderType; // EnumScoreboardHealthDisplay
	@Nullable // 1.20.3+ only
	public static final Field numberFormat; // Optional(NumberFormat)
	public static final Field objectiveMethod; // int

	public enum Visibility {
		ALWAYS(Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("net.minecraft.world.scores", "Team$Visibility"), "ALWAYS") : "always"),
		NEVER(Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("net.minecraft.world.scores", "Team$Visibility"), "NEVER") : "never"),
		HIDE_FOR_OTHER_TEAMS(Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("net.minecraft.world.scores", "Team$Visibility"), "HIDE_FOR_OTHER_TEAMS") : "hideForOtherTeams"),
		HIDE_FOR_OWN_TEAM(Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("net.minecraft.world.scores", "Team$Visibility"), "HIDE_FOR_OWN_TEAM") : "hideForOwnTeam");

		@Getter
		private final Object value;
		Visibility(Object value) {
			this.value = value;
		}
	}

	public enum CollisionRule {
		ALWAYS(Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("net.minecraft.world.scores", "Team$CollisionRule"), "ALWAYS") : "always"),
		NEVER(Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("net.minecraft.world.scores", "Team$CollisionRule"), "NEVER") : "never"),
		PUSH_OTHER_TEAMS(Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("net.minecraft.world.scores", "Team$CollisionRule"), "PUSH_OTHER_TEAMS") : "pushOtherTeams"),
		PUSH_OWN_TEAM(Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("net.minecraft.world.scores", "Team$CollisionRule"), "PUSH_OWN_TEAM") : "pushOwnTeam");

		@Getter
		private final Object value;
		CollisionRule(Object value) {
			this.value = value;
		}
	}

	static {
		Class<?> sb;
		Class<?> so;
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE) {
			sb = Ref.nms("network.protocol.game", "ClientboundSetPlayerTeamPacket");
			sbTeam = Ref.nms("network.protocol.game", "ClientboundSetPlayerTeamPacket$Parameters");
			white = Ref.getStatic(Ref.field(Ref.nms("", "ChatFormatting"), "WHITE"));
			name = Ref.field(sb, "name");
			teamMethod = Ref.field(sb, "method");
			players = Ref.field(sb, "players");
			parameters = Ref.field(sb, "parameters");
			teamDisplayName = Ref.field(sbTeam, "displayName");
			playerPrefix = Ref.field(sbTeam, "playerPrefix");
			playerSuffix = Ref.field(sbTeam, "playerSuffix");
			nametagVisibility = Ref.field(sbTeam, "nametagVisibility");
			collisionRule = Ref.field(sbTeam, "collisionRule");
			color = Ref.field(sbTeam, "color");
			options = Ref.field(sbTeam, "options");
			so = Ref.nms("network.protocol.game", "ClientboundSetObjectivePacket");
			objectiveName = Ref.field(so, "objectiveName");
			objectiveDisplayName = Ref.field(so, "displayName");
			renderType = Ref.field(so, "renderType");
			numberFormat = Ref.field(so, "numberFormat");
			objectiveMethod = Ref.field(so, "method");
		} else {
			sb = Ref.nms("network.protocol.game", "PacketPlayOutScoreboardTeam");
			sbTeam = Ref.nms("network.protocol.game", "PacketPlayOutScoreboardTeam$b");
			white = Ref.method(Ref.nms("", "EnumChatFormat"), "a", int.class) != null
					? Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", int.class), 15)
							: Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", char.class), 'f');
			if (Ref.isNewerThan(16)) {
				name = Ref.field(sb, "i");
				teamMethod = Ref.field(sb, "h");
				players = Ref.field(sb, "j");
				parameters = Ref.field(sb, "k");
				teamDisplayName = Ref.field(sbTeam, "a");
				playerPrefix = Ref.field(sbTeam, "b");
				playerSuffix = Ref.field(sbTeam, "c");
				nametagVisibility = Ref.field(sbTeam, "d");
				collisionRule = Ref.field(sbTeam, "e");
				color = Ref.field(sbTeam, "f");
				options = Ref.field(sbTeam, "g");
			} else {
				name = Ref.field(sb, "a");
				teamMethod = Ref.field(sb, Ref.isNewerThan(8) ? "i" : "h");
				players = Ref.field(sb, Ref.isNewerThan(8) ? "h" : "g");
				parameters = null;
				teamDisplayName = Ref.field(sb, "b");
				playerPrefix = Ref.field(sb, "c");
				playerSuffix = Ref.field(sb, "d");
				nametagVisibility = Ref.field(sb, "d");
				collisionRule = Ref.field(sb, "e");
				color = Ref.field(sb, "f");
				if (Ref.isNewerThan(8))
					options = Ref.field(sb, "g");
				else
					options = null;
			}
			so = Ref.nms("network.protocol.game", "PacketPlayOutScoreboardObjective");
			if (Ref.isNewerThan(16)) {
				objectiveName = Ref.field(so, "d");
				objectiveDisplayName = Ref.field(so, "e");
				renderType = Ref.field(so, "f");
				if (Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() >= 3) {
					numberFormat = Ref.field(so, "g");
					objectiveMethod = Ref.field(so, "h");
				} else {
					numberFormat = null;
					objectiveMethod = Ref.field(so, "g");
				}
			} else {
				objectiveName = Ref.field(so, "a");
				objectiveDisplayName = Ref.field(so, "b");
				numberFormat = null;
				renderType = Ref.field(so, "c");
				objectiveMethod = Ref.field(so, "d");
			}
		}
	}

	public static Object createTeamPacket(int mode, Object color, Component prefix, Component suffix, String holderName, String teamName, Visibility visibility, CollisionRule collision) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
		List<String> nameList = Collections.singletonList(holderName);
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE || Ref.isNewerThan(16)) {
			Object o = Ref.newUnsafeInstance(TeamUtils.sbTeam);
			Ref.set(o, teamDisplayName, BukkitLoader.getNmsProvider().chatBase("{\"text\":\"" + holderName + "\"}"));
			Ref.set(o, playerPrefix, BukkitLoader.getNmsProvider()
					.toIChatBaseComponent(prefix == null ? Component.EMPTY_COMPONENT : prefix));
			Ref.set(o, playerSuffix, BukkitLoader.getNmsProvider()
					.toIChatBaseComponent(suffix == null ? Component.EMPTY_COMPONENT : suffix));
			Ref.set(o, nametagVisibility, visibility.getValue());
			Ref.set(o, collisionRule, collision.getValue());
			Ref.set(o, TeamUtils.color, color);
			Ref.set(o, options, 0);
			Ref.set(packet, parameters, Optional.of(o));
		} else {
			Ref.set(packet, teamDisplayName,
					Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().chatBase("{\"text\":\"\"}") : "");
			Ref.set(packet, playerPrefix,
					Ref.isNewerThan(12)
					? BukkitLoader.getNmsProvider()
							.toIChatBaseComponent(prefix == null ? Component.EMPTY_COMPONENT : prefix)
							: prefix == null ? "" : prefix.toString());
			Ref.set(packet, playerSuffix,
					Ref.isNewerThan(12)
					? BukkitLoader.getNmsProvider()
							.toIChatBaseComponent(suffix == null ? Component.EMPTY_COMPONENT : suffix)
							: suffix == null ? "" : suffix.toString());
			Ref.set(packet, nametagVisibility, visibility.getValue());
			Ref.set(packet, collisionRule, collision.getValue());
			if (Ref.isNewerThan(8))
				Ref.set(packet, TeamUtils.color, Ref.isNewerThan(12) ? color : -1);
		}
		Ref.set(packet, name, teamName);
		Ref.set(packet, teamMethod, mode);
		Ref.set(packet, players, nameList);
		return packet;
	}

	public static Object createTeamPacket(int mode, Object color, Component prefix, Component suffix, String holderName, String teamName) {
		return createTeamPacket(mode, color, prefix, suffix, holderName, teamName, Visibility.ALWAYS, CollisionRule.ALWAYS);
	}

	public static Object createObjectivePacket(int mode, String name, Component displayName,
			@Nullable Optional<?> numberFormat, DisplayType type) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardObjective();
		Ref.set(packet, objectiveDisplayName,
				Ref.isNewerThan(12)
				? BukkitLoader.getNmsProvider()
						.toIChatBaseComponent(displayName == null ? Component.EMPTY_COMPONENT : displayName)
						: displayName.toString());
		Ref.set(packet, objectiveName, name);
		Ref.set(packet, renderType, BukkitLoader.getNmsProvider().getEnumScoreboardHealthDisplay(type));
		if (Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() == 3)
			Ref.set(packet, TeamUtils.numberFormat, numberFormat == null ? null : numberFormat.orElse(null));
		else if (Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() > 3)
			Ref.set(packet, TeamUtils.numberFormat, numberFormat);
		Ref.set(packet, objectiveMethod, mode);
		return packet;
	}
}
