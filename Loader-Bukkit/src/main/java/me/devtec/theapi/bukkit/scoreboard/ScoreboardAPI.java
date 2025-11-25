package me.devtec.theapi.bukkit.scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.devtec.shared.Ref;
import me.devtec.shared.components.Component;
import me.devtec.shared.dataholder.StringContainer;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.NmsProvider.Action;
import me.devtec.theapi.bukkit.nms.NmsProvider.DisplayType;
import me.devtec.theapi.bukkit.nms.utils.TeamUtils;
import me.devtec.theapi.bukkit.nms.utils.TeamUtils.CollisionRule;
import me.devtec.theapi.bukkit.nms.utils.TeamUtils.Visibility;

/**
 * <a href="https://gist.github.com/f8895d84979d49af946fbcc108b1bf2b">Original source</a>
 *
 * @author zyuiop Forked by Straikerinos
 */
public class ScoreboardAPI {
	public static boolean SPLIT_MODERN_LINES;

	protected final Map<Integer, Team> data = new ConcurrentHashMap<>();
	protected final Player p;
	protected final String player;
	protected String sbname;
	protected int staticScoreboardScore;
	protected String name = "";
	protected boolean destroyed;

	public ScoreboardAPI(Player player) {
		this(player, -1);
	}

	/**
	 * @param player Player - Holder of scoreboard
	 * @param slot   -1 to adaptive slot
	 */
	public ScoreboardAPI(Player player, int slot) {
		p = player;
		staticScoreboardScore = slot;
		this.player = player.getName();
		sbname = this.player;
		if (sbname.length() > 16)
			sbname = sbname.substring(0, 16);
		BukkitLoader.getPacketHandler().send(p, createObjectivePacket(0, new Component("§0")));
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardDisplayObjective(1, null);
		Ref.set(packet, BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "objectiveName" : "b", sbname);
		BukkitLoader.getPacketHandler().send(p, packet);
	}

	public void setSlot(int slot) {
		staticScoreboardScore = slot;
	}

	public void remove() {
		destroy();
	}

	public void destroy() {
		if (destroyed)
			return;
		destroyed = true;
		BukkitLoader.getPacketHandler().send(p, createObjectivePacket(1, Component.EMPTY_COMPONENT));
		for (Team team : data.values())
			if (team != null) {
				removeTeam(team.currentPlayer, team.name);
				BukkitLoader.getPacketHandler().send(p, BukkitLoader.getNmsProvider().packetScoreboardScore(Action.REMOVE, sbname, team.currentPlayer, 0));
			}
		data.clear();
	}

	public void setTitle(String name) {
		setDisplayName(name);
	}

	public void setName(String name) {
		setDisplayName(name);
	}

	public void setDisplayName(String text) {
		destroyed = false;
		String displayName = name;
		name = ColorUtils.colorize(text);
		if (!Ref.isNewerThan(12) && name.length() > 32)
			name = name.substring(0, 32);
		if (!name.equals(displayName))
			BukkitLoader.getPacketHandler().send(p, createObjectivePacket(2, Component.fromString(name)));
	}

	public void addLine(String value) {
		int i = -1;
		while (data.containsKey(++i))
			;
		setLine(i, value);
	}

	public void setLine(int line, String valueText) {
		String value = ColorUtils.colorize(valueText);
		if (getLine(line) != null && getLine(line).equals(value))
			return;
		Team team = null;
		boolean add = true;
		for (Team t : data.values())
			if (t.slot == line) {
				team = t;
				add = false;
			}
		if (add)
			team = getTeam(line, line);
		if (team.setValue(value) || add)
			sendLine(team, line, add);
	}

	public void removeLine(int line) {
		if (!data.containsKey(line))
			return;
		Team team = getTeam(line, line);
		removeTeam(team.currentPlayer, team.name);
		BukkitLoader.getPacketHandler().send(p, BukkitLoader.getNmsProvider().packetScoreboardScore(Action.REMOVE, sbname, team.currentPlayer, 0));
		data.remove(line);
	}

	public void removeUpperLines(int line) {
		Iterator<Entry<Integer, Team>> itr = data.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<Integer, Team> entry = itr.next();
			if (entry.getKey() >= line) {
				itr.remove();
				removeTeam(entry.getValue().currentPlayer, entry.getValue().name);
				BukkitLoader.getPacketHandler().send(p, BukkitLoader.getNmsProvider().packetScoreboardScore(Action.REMOVE, sbname, entry.getValue().currentPlayer, 0));
			}
		}
	}

	public String getLine(int line) {
		if (data.get(line) != null)
			return data.get(line).getValue();
		return null;
	}

	public List<String> getLines() {
		List<String> lines = new ArrayList<>();
		for (Team line : data.values())
			lines.add(line.getValue());
		return lines;
	}

	private void sendLine(Team team, int line, boolean add) {
		destroyed = false;
		if (team.old != null && !team.first)
			BukkitLoader.getPacketHandler().send(p, BukkitLoader.getNmsProvider().packetScoreboardScore(Action.REMOVE, sbname, team.old, 0));
		team.sendLine();
		BukkitLoader.getPacketHandler().send(p,
				BukkitLoader.getNmsProvider().packetScoreboardScore(Action.CHANGE, sbname, team.currentPlayer, staticScoreboardScore == -1 ? line : staticScoreboardScore));
		if (add)
			data.put(line, team);
	}

	private Team getTeam(int line, int realPos) {
		Team result = data.get(line);
		if (result == null)
			data.put(line, result = new Team(line, realPos));
		return result;
	}

	private void createTeam(Component prefix, Component suffix, Component displayName, String teamName) {
		BukkitLoader.getPacketHandler().send(p, TeamUtils.createTeamPacket(TeamUtils.METHOD_ADD, teamName, ChatColor.WHITE, prefix, suffix, displayName, Visibility.ALWAYS, CollisionRule.ALWAYS, 0, Collections.emptyList()));
	}

	private void modifyTeam(Component prefix, Component suffix, Component displayName, String teamName) {
		BukkitLoader.getPacketHandler().send(p, TeamUtils.createTeamPacket(TeamUtils.METHOD_CHANGE, teamName, ChatColor.WHITE, prefix, suffix, displayName, Visibility.ALWAYS, CollisionRule.ALWAYS, 0, Collections.emptyList()));
	}

	private void removeTeam(String playerName, String teamName) {
		BukkitLoader.getPacketHandler().send(p, TeamUtils.createTeamPacket(TeamUtils.METHOD_REMOVE, teamName, null, null, null, null, null, null, 0, Collections.singleton(playerName)));
	}

	private void leaveTeam(String playerName, String teamName) {
		BukkitLoader.getPacketHandler().send(p, TeamUtils.createTeamPacket(TeamUtils.METHOD_LEAVE, teamName, null, null, null, null, null, null, 0, Collections.singleton(playerName)));
	}

	private void joinTeam(String playerName, String teamName) {
		BukkitLoader.getPacketHandler().send(p, TeamUtils.createTeamPacket(TeamUtils.METHOD_JOIN, teamName, null, null, null, null, null, null, 0, Collections.singleton(playerName)));
	}

	private Object createObjectivePacket(int mode, Component displayName) {
		return TeamUtils.createObjectivePacket(mode, sbname, displayName, Optional.empty(), DisplayType.INTEGER);
	}

	public class Team {
		private String prefix = "";
		private String suffix = "";
		private String currentPlayer;
		private String old;
		private final String name;
		private final String format;
		private String resultLine = "";
		private final int slot;
		private boolean changed;
		private boolean first = true;

		private Team(int slot, int realPos) {
			String integerInString = "" + realPos;
			for (int i = ChatColor.values().length - 1; i > -1; --i)
				integerInString = integerInString.replace(i + "", ChatColor.values()[i] + "");
			currentPlayer = integerInString + "§f";
			format = currentPlayer;
			this.slot = slot;
			name = "" + slot;
		}

		public void sendLine() {
			if (first) {
				createTeam(Component.fromString(prefix), Component.fromString(suffix), Component.fromString(currentPlayer), name);
				changed = false;
			} else if (changed) {
				changed = false;
				modifyTeam(Component.fromString(prefix), Component.fromString(suffix), Component.fromString(currentPlayer), name);
			}
			if (first || old != null) {
				if (old != null)
					leaveTeam(old, name);
				joinTeam(currentPlayer, name);
				old = null;
				first = false;
			}
		}

		public String getValue() {
			return resultLine;
		}

		private boolean setPlayer(String teamName) {
			String name = format + teamName;
			if (!currentPlayer.equals(name)) {
				old = currentPlayer;
				currentPlayer = name;
				return true;
			}
			return false;
		}

		public boolean setValue(String value) {
			if (old != null)
				return true; // require update!
			String text = value;
			if (text == null || text.isEmpty()) {
				resultLine = "";
				if (!prefix.isEmpty())
					changed = true;
				prefix = "";
				if (!suffix.isEmpty())
					changed = true;
				suffix = "";
				return setPlayer("") || changed;
			}
			if (Ref.isNewerThan(12) && !SPLIT_MODERN_LINES) { // 1.13+ only
				if (!prefix.equals(text))
					changed = true;
				prefix = text;
				resultLine = prefix;
				return changed;
			}
			List<String> splitted = StringUtils.fixedSplit(text, 16);
			if (splitted.size() == 1) {
				if (!prefix.equals(splitted.get(0)))
					changed = true;
				prefix = splitted.get(0);
				if (!suffix.isEmpty())
					changed = true;
				suffix = "";
				resultLine = prefix;
				return setPlayer("") || changed;
			}
			if (splitted.size() == 2 && text.length() <= 32) {
				if (!prefix.equals(splitted.get(0)))
					changed = true;
				prefix = splitted.get(0);

				text = text.substring(prefix.length());
				String lastColors = ColorUtils.getLastColors(prefix);
				if (!lastColors.isEmpty()) {
					StringContainer container = new StringContainer(text, 0, lastColors.length() * 2);
					for (int i = lastColors.length() - 1; i > -1; --i)
						container.insert(0, lastColors.charAt(i)).insert(0, '§');
					text = container.toString();
				}
				splitted = StringUtils.fixedSplit(text, 16 - format.length());

				if (splitted.size() != 1) {
					boolean someChange = setPlayer(text);
					resultLine += lastColors.isEmpty() ? text : text.substring(lastColors.length() * 2);
					if (!suffix.isEmpty())
						changed = true;
					suffix = "";
					return changed || someChange;
				}
				setPlayer("");
				if (!suffix.equals(splitted.get(0)))
					changed = true;
				suffix = splitted.get(0);
				resultLine = prefix + suffix;
				return changed;
			}
			if (!prefix.equals(splitted.get(0)))
				changed = true;
			prefix = splitted.get(0);

			resultLine = prefix;

			text = text.substring(prefix.length());

			String lastColors = ColorUtils.getLastColors(prefix);
			if (!lastColors.isEmpty()) {

				StringContainer container = new StringContainer(text, 0, lastColors.length() * 2);
				for (int i = lastColors.length() - 1; i > -1; --i)
					container.insert(0, lastColors.charAt(i)).insert(0, '§');
				text = container.toString();
			}
			splitted = StringUtils.fixedSplit(text, 40 - format.length());
			boolean someChange = setPlayer(splitted.get(0));
			resultLine += lastColors.isEmpty() ? splitted.get(0) : splitted.get(0).substring(lastColors.length() * 2);

			text = text.substring(splitted.get(0).length());
			lastColors = ColorUtils.getLastColors(splitted.get(0));
			if (!lastColors.isEmpty()) {
				StringContainer container = new StringContainer(text, 0, lastColors.length() * 2);
				for (int i = lastColors.length() - 1; i > -1; --i)
					container.insert(0, lastColors.charAt(i)).insert(0, '§');
				text = container.toString();
			}

			String result;
			if (Ref.isNewerThan(12))
				result = text;
			else
				result = StringUtils.fixedSplit(text, 16).get(0);
			if (!suffix.equals(result))
				changed = true;
			suffix = result;
			resultLine += lastColors.isEmpty() ? suffix : suffix.substring(lastColors.length() * 2);
			return changed || someChange;
		}
	}
}