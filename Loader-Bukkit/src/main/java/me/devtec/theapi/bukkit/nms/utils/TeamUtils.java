package me.devtec.theapi.bukkit.nms.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

import org.bukkit.ChatColor;

import me.devtec.shared.Ref;
import me.devtec.shared.annotations.Nullable;
import me.devtec.shared.components.Component;
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
		ALWAYS("always"),
		NEVER( "never"),
		HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
		HIDE_FOR_OWN_TEAM("hideForOwnTeam");

		private final String name;
		private Object value;
		Visibility(String name) {
			this.name = name;
		}

		public Object getValue() {
			if(value==null)
				value = Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("world.scores", "Team$Visibility"), name()) : name;
				return value;
		}
	}

	public enum CollisionRule {
		ALWAYS("always"),
		NEVER("never"),
		PUSH_OTHER_TEAMS("pushOtherTeams"),
		PUSH_OWN_TEAM("pushOwnTeam");

		private final String name;
		private Object value;
		CollisionRule(String name) {
			this.name = name;
		}

		public Object getValue() {
			if(value==null)
				value = Ref.isNewerThan(21) || Ref.isNewerThan(20) && Ref.serverVersionRelease()>4 ? Ref.getStatic(Ref.nms("world.scores", "Team$CollisionRule"), name()) : name;
				return value;
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

	public static Object parseColor(ChatColor color) {
		if(color==ChatColor.WHITE||color==null)return white;
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE || Ref.isNewerThan(16))
			return Ref.getStatic(Ref.field(Ref.nms("", "ChatFormatting"), color.name()));
		return Ref.method(Ref.nms("", "EnumChatFormat"), "a", int.class) != null
				? Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", int.class), color.ordinal())
						: Ref.invokeStatic(Ref.method(Ref.nms("", "EnumChatFormat"), "a", char.class), color.getChar());
	}

	public static Object createTeamPacket(int mode, String teamName, ChatColor color, Component prefix, Component suffix, Component displayName, Visibility visibility, CollisionRule collision, int friendlyFlags, Collection<String> players) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
		if (BukkitLoader.NO_OBFUSCATED_NMS_MODE || Ref.isNewerThan(16)) {
			if(mode==METHOD_ADD || mode ==METHOD_CHANGE) {
				Object o = Ref.newUnsafeInstance(TeamUtils.sbTeam);
				Ref.set(o, teamDisplayName, BukkitLoader.getNmsProvider().toIChatBaseComponent(displayName == null ? Component.EMPTY_COMPONENT : displayName));
				Ref.set(o, playerPrefix, BukkitLoader.getNmsProvider().toIChatBaseComponent(prefix == null ? Component.EMPTY_COMPONENT : prefix));
				Ref.set(o, playerSuffix, BukkitLoader.getNmsProvider().toIChatBaseComponent(suffix == null ? Component.EMPTY_COMPONENT : suffix));
				Ref.set(o, nametagVisibility, visibility==null?Visibility.ALWAYS.getValue():visibility.getValue());
				Ref.set(o, collisionRule, collision==null?CollisionRule.ALWAYS.getValue():collision.getValue());
				Ref.set(o, TeamUtils.color, parseColor(color));
				Ref.set(o, options, friendlyFlags);
				Ref.set(packet, parameters, Optional.of(o));
			}
		} else if(mode==METHOD_ADD || mode ==METHOD_CHANGE) {
			Ref.set(packet, teamDisplayName, Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().toIChatBaseComponent(displayName == null ? Component.EMPTY_COMPONENT : displayName) : displayName.toString());
			Ref.set(packet, playerPrefix, Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().toIChatBaseComponent(prefix == null ? Component.EMPTY_COMPONENT : prefix) : prefix == null ? "" : prefix.toString());
			Ref.set(packet, playerSuffix, Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().toIChatBaseComponent(suffix == null ? Component.EMPTY_COMPONENT : suffix) : suffix == null ? "" : suffix.toString());
			Ref.set(packet, nametagVisibility, visibility==null?Visibility.ALWAYS.getValue():visibility.getValue());
			Ref.set(packet, collisionRule, collision==null?CollisionRule.ALWAYS.getValue():collision.getValue());
			if (Ref.isNewerThan(8)) {
				Ref.set(packet, TeamUtils.color, Ref.isNewerThan(12) ? parseColor(color) : color.ordinal());
				Ref.set(packet, options, friendlyFlags);
			}
		}
		Ref.set(packet, name, teamName);
		Ref.set(packet, teamMethod, mode);
		if(mode==METHOD_JOIN||mode==METHOD_ADD||mode==METHOD_REMOVE)
			Ref.set(packet, TeamUtils.players, players);
		return packet;
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
