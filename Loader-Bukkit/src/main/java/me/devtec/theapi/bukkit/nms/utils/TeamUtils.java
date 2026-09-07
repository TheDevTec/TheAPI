package me.devtec.theapi.bukkit.nms.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

import org.bukkit.ChatColor;

import me.devtec.shared.Ref;
import me.devtec.shared.annotations.Nullable;
import me.devtec.shared.components.base.Component;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.NmsProvider.DisplayType;

public class TeamUtils {
	public static final int METHOD_ADD = 0;
	public static final int METHOD_REMOVE = 1;
	public static final int METHOD_CHANGE = 2;
	public static final int METHOD_JOIN = 3;
	public static final int METHOD_LEAVE = 4;

	@Nullable
	public static final Class<?> sbTeam;// class of PacketPlayOutScoreboardTeam$b
	public static final Constructor<?> teamPacket;
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
		ALWAYS("always"), NEVER("never"), HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
		HIDE_FOR_OWN_TEAM("hideForOwnTeam");

		private final String name;
		private Object value;

		Visibility(String name) {
			this.name = name;
		}

		public Object getValue() {
			if (value == null)
				value = Ref.isAtLeast(22, 0) || Ref.isAtLeast(21, 0) && Ref.release() > 4
						? Ref.getStatic(Ref.nms("world.scores", "Team$Visibility"), name())
						: name;
			return value;
		}
	}

	public enum CollisionRule {
		ALWAYS("always"), NEVER("never"), PUSH_OTHER_TEAMS("pushOtherTeams"), PUSH_OWN_TEAM("pushOwnTeam");

		private final String name;
		private Object value;

		CollisionRule(String name) {
			this.name = name;
		}

		public Object getValue() {
			if (value == null)
				value = Ref.isAtLeast(22, 0) || Ref.isAtLeast(21, 0) && Ref.release() > 4
						? Ref.getStatic(Ref.nms("world.scores", "Team$CollisionRule"), name())
						: name;
			return value;
		}
	}

	static {
		Class<?> sb;
		Class<?> so;
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE) {
			sb = Ref.nms("network.protocol.game", "ClientboundSetPlayerTeamPacket");
			sbTeam = Ref.nms("network.protocol.game", "ClientboundSetPlayerTeamPacket$Parameters");
			white = Ref.getStatic(Ref.field(Ref.nms(Ref.isAtLeast(26, 0) ? "world.scores" : "",
					Ref.isAtLeast(26, 0) ? "TeamColor" : "ChatFormatting"), "WHITE"));
			name = Ref.field(sb, "name");
			teamMethod = Ref.field(sb, "method");
			players = Ref.field(sb, "players");
			parameters = Ref.field(sb, "parameters");
			teamDisplayName = Ref.field(sbTeam, "displayName");
			playerPrefix = Ref.field(sbTeam, "playerPrefix");
			playerSuffix = Ref.field(sbTeam, "playerSuffix");
			nametagVisibility = Ref.field(sbTeam, "nametagVisibility") == null ? Ref.field(sbTeam, "nameTagVisibility")
					: Ref.field(sbTeam, "nametagVisibility");
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
			if (Ref.isAtLeast(17, 0)) {
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
				teamMethod = Ref.field(sb, Ref.isAtLeast(9, 0) ? "i" : "h");
				players = Ref.field(sb, Ref.isAtLeast(9, 0) ? "h" : "g");
				parameters = null;
				teamDisplayName = Ref.field(sb, "b");
				playerPrefix = Ref.field(sb, "c");
				playerSuffix = Ref.field(sb, "d");
				nametagVisibility = Ref.field(sb, "d");
				collisionRule = Ref.field(sb, "e");
				color = Ref.field(sb, "f");
				if (Ref.isAtLeast(9, 0))
					options = Ref.field(sb, "g");
				else
					options = null;
			}
			so = Ref.nms("network.protocol.game", "PacketPlayOutScoreboardObjective");
			if (Ref.isAtLeast(17, 0)) {
				objectiveName = Ref.field(so, "d");
				objectiveDisplayName = Ref.field(so, "e");
				renderType = Ref.field(so, "f");
				if (Ref.isAtLeast(21, 0) || Ref.version() == 20 && Ref.release() >= 3) {
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
		Class<?> cp = Ref.nms("network.chat", "Component");
		Class<?> tag = Ref.nms("world.scores", "Team$Visibility");
		Class<?> coll = Ref.nms("world.scores", "Team$CollisionRule");
		teamPacket = Ref.constructor(sbTeam, cp, cp, cp, tag, coll, Optional.class, byte.class);
	}

	public static Object parseColor(ChatColor color) {
		if (color == ChatColor.WHITE || color == null)
			return white;
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE || Ref.isAtLeast(17, 0))
			return Ref.getStatic(Ref.field(Ref.nms(Ref.isAtLeast(26, 0) ? "world.scores" : "",
					Ref.isAtLeast(26, 0) ? "TeamColor" : "ChatFormatting"), color.name()));
		return Ref.method(Ref.nms("", "EnumChatFormat"), "a", int.class) != null
				? Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", int.class), color.ordinal())
				: Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", char.class), color.getChar());
	}

	public static Object createTeamPacket(int mode, String teamName, ChatColor color, Component prefix,
			Component suffix, Component displayName, Visibility visibility, CollisionRule collision, int friendlyFlags,
			Collection<String> players) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE || Ref.isAtLeast(17, 0)) {
			if (mode == METHOD_ADD || mode == METHOD_CHANGE)
				if (Ref.isAtLeast(26, 0))
					Ref.set(packet, parameters,
							Optional.of(Ref.newInstance(teamPacket,
									BukkitLoader.getNmsProvider().toIChatBaseComponent(
											displayName == null ? Component.EMPTY_COMPONENT : displayName),
									BukkitLoader.getNmsProvider()
											.toIChatBaseComponent(prefix == null ? Component.EMPTY_COMPONENT : prefix),
									BukkitLoader.getNmsProvider()
											.toIChatBaseComponent(suffix == null ? Component.EMPTY_COMPONENT : suffix),
									visibility == null ? Visibility.ALWAYS.getValue() : visibility.getValue(),
									collision == null ? CollisionRule.ALWAYS.getValue() : collision.getValue(),
									Optional.of(parseColor(color)), (byte) friendlyFlags)));
				else {
					Object o = Ref.newUnsafeInstance(TeamUtils.sbTeam);
					Ref.set(o, teamDisplayName, BukkitLoader.getNmsProvider()
							.toIChatBaseComponent(displayName == null ? Component.EMPTY_COMPONENT : displayName));
					Ref.set(o, playerPrefix, BukkitLoader.getNmsProvider()
							.toIChatBaseComponent(prefix == null ? Component.EMPTY_COMPONENT : prefix));
					Ref.set(o, playerSuffix, BukkitLoader.getNmsProvider()
							.toIChatBaseComponent(suffix == null ? Component.EMPTY_COMPONENT : suffix));
					Ref.set(o, nametagVisibility,
							visibility == null ? Visibility.ALWAYS.getValue() : visibility.getValue());
					Ref.set(o, collisionRule,
							collision == null ? CollisionRule.ALWAYS.getValue() : collision.getValue());
					Ref.set(o, TeamUtils.color, parseColor(color));
					Ref.set(o, options, friendlyFlags);
					Ref.set(packet, parameters, Optional.of(o));
				}
		} else if (mode == METHOD_ADD || mode == METHOD_CHANGE) {
			Ref.set(packet, teamDisplayName,
					Ref.isAtLeast(13, 0)
							? BukkitLoader.getNmsProvider()
									.toIChatBaseComponent(displayName == null ? Component.EMPTY_COMPONENT : displayName)
							: displayName.toString());
			Ref.set(packet, playerPrefix,
					Ref.isAtLeast(13, 0)
							? BukkitLoader.getNmsProvider()
									.toIChatBaseComponent(prefix == null ? Component.EMPTY_COMPONENT : prefix)
							: prefix == null ? "" : prefix.toString());
			Ref.set(packet, playerSuffix,
					Ref.isAtLeast(13, 0)
							? BukkitLoader.getNmsProvider()
									.toIChatBaseComponent(suffix == null ? Component.EMPTY_COMPONENT : suffix)
							: suffix == null ? "" : suffix.toString());
			Ref.set(packet, nametagVisibility,
					visibility == null ? Visibility.ALWAYS.getValue() : visibility.getValue());
			Ref.set(packet, collisionRule, collision == null ? CollisionRule.ALWAYS.getValue() : collision.getValue());
			if (Ref.isAtLeast(9, 0)) {
				Ref.set(packet, TeamUtils.color, Ref.isAtLeast(13, 0) ? parseColor(color) : color.ordinal());
				Ref.set(packet, options, friendlyFlags);
			}
		}
		Ref.set(packet, name, teamName);
		Ref.set(packet, teamMethod, mode);
		if (mode == METHOD_JOIN || mode == METHOD_ADD || mode == METHOD_REMOVE)
			Ref.set(packet, TeamUtils.players, players);
		return packet;
	}

	public static Object createObjectivePacket(int mode, String name, Component displayName,
			@Nullable Optional<?> numberFormat, DisplayType type) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardObjective();
		Ref.set(packet, objectiveDisplayName,
				Ref.isAtLeast(13, 0)
						? BukkitLoader.getNmsProvider()
								.toIChatBaseComponent(displayName == null ? Component.EMPTY_COMPONENT : displayName)
						: displayName.toString());
		Ref.set(packet, objectiveName, name);
		Ref.set(packet, renderType, BukkitLoader.getNmsProvider().getEnumScoreboardHealthDisplay(type));
		if (Ref.version() == 20 && Ref.release() == 3)
			Ref.set(packet, TeamUtils.numberFormat, numberFormat == null ? null : numberFormat.orElse(null));
		else if (Ref.isAtLeast(21, 0) || Ref.version() == 20 && Ref.release() > 3)
			Ref.set(packet, TeamUtils.numberFormat, numberFormat);
		Ref.set(packet, objectiveMethod, mode);
		return packet;
	}
}
