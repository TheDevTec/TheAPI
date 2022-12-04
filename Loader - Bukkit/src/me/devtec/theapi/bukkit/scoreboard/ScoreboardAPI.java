package me.devtec.theapi.bukkit.scoreboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.devtec.shared.Ref;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.NmsProvider.Action;
import me.devtec.theapi.bukkit.nms.NmsProvider.DisplayType;
import me.devtec.theapi.bukkit.nms.utils.TeamUtils;

/**
 * https://gist.github.com/MrZalTy/f8895d84979d49af946fbcc108b1bf2b
 *
 * @author MrZalTy Forked by StraikerinaCZ
 *
 */
public class ScoreboardAPI {
	protected final Map<Integer, Team> data = new ConcurrentHashMap<>();
	protected Player p;
	protected String player;
	protected String sbname;
	protected int slott;
	protected String name = "";
	protected boolean destroyed;

	public ScoreboardAPI(Player player) {
		this(player, -1);
	}

	/**
	 *
	 * @param player Player - Holder of scoreboard
	 * @param slot   -1 to adaptive slot
	 */
	public ScoreboardAPI(Player player, int slot) {
		p = player;
		slott = slot;
		this.player = player.getName();
		sbname = this.player;
		if (sbname.length() > 16)
			sbname = sbname.substring(0, 16);
		BukkitLoader.getPacketHandler().send(p, createObjectivePacket(0, "§0"));
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardDisplayObjective(1, null);
		Ref.set(packet, "b", sbname);
		BukkitLoader.getPacketHandler().send(p, packet);
	}

	public void setSlot(int slot) {
		slott = slot;
	}

	public void remove() {
		destroy();
	}

	public void destroy() {
		if (destroyed)
			return;
		destroyed = true;
		BukkitLoader.getPacketHandler().send(p, createObjectivePacket(1, ""));
		for (Team team : data.values())
			if (team != null)
				this.remove(p, team.currentPlayer, team.name);
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
		name = StringUtils.colorize(text);
		if (!Ref.isNewerThan(12) && name.length() > 32)
			name = name.substring(0, 32);
		if (!name.equals(displayName))
			BukkitLoader.getPacketHandler().send(p, createObjectivePacket(2, name));
	}

	public void addLine(String value) {
		int i = -1;
		while (data.containsKey(++i))
			;
		setLine(i, value);
	}

	public void setLine(int line, String valueText) {
		String value = StringUtils.colorize(valueText);
		if (getLine(line) != null && getLine(line).equals(!Ref.isNewerThan(12) ? cut(value) : value))
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
		team.setValue(value);
		sendLine(team, line, add);
	}

	private String cut(String original) {
		if (original.isEmpty())
			return original;
		List<String> d = StringUtils.fixedSplit(original, 17);
		if (original.length() <= 17)
			return d.get(0);
		if (original.length() <= 34)
			return d.get(0) + d.get(1);
		String text = d.get(0);
		String modified = original.substring(d.get(0).length());
		d = StringUtils.fixedSplit(modified, 18);
		text += StringUtils.getLastColors(text) + d.get(0);
		modified = modified.substring(d.get(0).length());
		d = StringUtils.fixedSplit(modified, 17);
		text += d.get(0);
		return text;
	}

	public void removeLine(int line) {
		if (!data.containsKey(line))
			return;
		Team team = getTeam(line, line);
		this.remove(p, team.currentPlayer, team.name);
		data.remove(line);
	}

	public void removeUpperLines(int line) {
		for (Entry<Integer, Team> lineName : new HashSet<>(data.entrySet()))
			if (lineName.getKey() > line) {
				Team team = lineName.getValue();
				this.remove(p, team.currentPlayer, team.name);
				data.remove(line);
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
		team.sendLine(line);
		if (add)
			data.put(line, team);
	}

	private Team getTeam(int line, int realPos) {
		Team result = data.get(line);
		if (result == null)
			data.put(line, result = new Team(line, realPos));
		return result;
	}

	private void create(Player sendTo, String prefix, String suffix, String name, String realName, int slot) {
		BukkitLoader.getPacketHandler().send(p, TeamUtils.createTeamPacket(0, TeamUtils.white, ComponentAPI.fromString(prefix), ComponentAPI.fromString(suffix), name, realName));
		BukkitLoader.getPacketHandler().send(p, BukkitLoader.getNmsProvider().packetScoreboardScore(Action.CHANGE, sbname, name, slot));
	}

	private void modify(Player sendTo, String prefix, String suffix, String name, String realName, int slot) {
		BukkitLoader.getPacketHandler().send(p, TeamUtils.createTeamPacket(2, TeamUtils.white, ComponentAPI.fromString(prefix), ComponentAPI.fromString(suffix), name, realName));
		BukkitLoader.getPacketHandler().send(p, BukkitLoader.getNmsProvider().packetScoreboardScore(Action.CHANGE, sbname, name, slot));
	}

	private void remove(Player sendTo, String name, String realName) {
		BukkitLoader.getPacketHandler().send(p, TeamUtils.createTeamPacket(1, TeamUtils.white, Component.EMPTY_COMPONENT, Component.EMPTY_COMPONENT, name, realName));
		BukkitLoader.getPacketHandler().send(p, BukkitLoader.getNmsProvider().packetScoreboardScore(Action.REMOVE, sbname, name, 0));
	}

	private Object createObjectivePacket(int mode, String displayName) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardObjective();
		if (Ref.isNewerThan(16)) {
			Ref.set(packet, "d", sbname);
			Ref.set(packet, "e", BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(displayName)));
			Ref.set(packet, "f", BukkitLoader.getNmsProvider().getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
			Ref.set(packet, "g", mode);
		} else {
			Ref.set(packet, "a", sbname);
			Ref.set(packet, "b", Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(displayName)) : displayName);
			if (Ref.isNewerThan(7)) {
				Ref.set(packet, "c", BukkitLoader.getNmsProvider().getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
				Ref.set(packet, "d", mode);
			} else
				Ref.set(packet, "c", mode);
		}
		return packet;
	}

	public class Team {
		private String prefix = "";
		private String suffix = "";
		private String currentPlayer;
		private String old;
		private String name;
		private String format;
		private int slot;
		private boolean changed;
		private boolean first = true;

		private Team(int slot, int realPos) {
			String s = "" + realPos;
			for (int i = ChatColor.values().length - 1; i > -1; --i)
				s = s.replace(i + "", ChatColor.values()[i] + "");
			currentPlayer = s;
			if (Ref.isOlderThan(13)) {
				currentPlayer += "§f";
				format = currentPlayer;
			} else
				format = null;
			this.slot = slot;
			name = "" + slot;
		}

		public void sendLine(int line) {
			if (first) {
				create(p, prefix, suffix, currentPlayer, name, slott == -1 ? line : slott);
				first = false;
				old = null;
				changed = false;
				return;
			}
			if (old != null) {
				ScoreboardAPI.this.remove(p, old, name);
				old = null;
			}
			if (changed) {
				changed = false;
				modify(p, prefix, suffix, currentPlayer, name, slott == -1 ? line : slott);
			}
		}

		public String getValue() {
			return Ref.isOlderThan(13) ? prefix + currentPlayer.replaceFirst(format, "") + suffix : prefix + suffix;
		}

		private void setPlayer(String teamName) {
			String name = format + teamName;
			if (currentPlayer == null || !currentPlayer.equals(name)) {
				old = currentPlayer;
				currentPlayer = name;
			}
		}

		public void setValue(String value) {
			String a = value;
			if (a == null) {
				if (!prefix.equals(""))
					changed = true;
				prefix = "";
				if (!suffix.equals(""))
					changed = true;
				suffix = "";
				setPlayer("");
				return;
			}
			if (Ref.isOlderThan(13)) {
				if (a.isEmpty()) {
					setPlayer("");
					if (!prefix.equals(""))
						changed = true;
					prefix = "";
					if (!suffix.equals(""))
						changed = true;
					suffix = "";
					return;
				}

				List<String> d = StringUtils.fixedSplit(a, 16);
				if (a.length() <= 16) {
					setPlayer("");
					if (!prefix.equals(d.get(0)))
						changed = true;
					prefix = d.get(0);
					if (!suffix.equals(""))
						changed = true;
					suffix = "";
					return;
				}
				if (a.length() <= 32) {
					if (!prefix.equals(d.get(0)))
						changed = true;
					prefix = d.get(0);
					setPlayer("");
					if (d.size() > 1) {
						if (!suffix.equals(d.get(1)))
							changed = true;
						suffix = d.get(1);
					} else {
						if (!suffix.equals(""))
							changed = true;
						suffix = "";
					}
					return;
				}
				if (Ref.isOlderThan(8)) {
					if (!prefix.equals(d.get(0)))
						changed = true;
					prefix = d.get(0);
					d = StringUtils.fixedSplit(a = a.substring(prefix.length()), 17 - format.length());
					setPlayer(d.get(0));
					d = StringUtils.fixedSplit(a.substring(d.get(0).length()), 16);
					if (!suffix.equals(d.get(0)))
						changed = true;
					suffix = d.get(0);
					return;
				}
				if (!prefix.equals(d.get(0)))
					changed = true;
				prefix = d.get(0);
				a = a.substring(d.get(0).length());
				d = StringUtils.fixedSplit(a, 18);
				setPlayer(StringUtils.getLastColors(prefix) + d.get(0));
				a = a.substring(d.get(0).length());
				d = StringUtils.fixedSplit(a, 17);
				if (d.isEmpty()) {
					if (!suffix.equals(""))
						changed = true;
					suffix = "";
				} else {
					if (!suffix.equals(d.get(0)))
						changed = true;
					suffix = d.get(0);
				}
			} else {
				if (!prefix.equals(a))
					changed = true;
				prefix = a;
			}
		}
	}
}