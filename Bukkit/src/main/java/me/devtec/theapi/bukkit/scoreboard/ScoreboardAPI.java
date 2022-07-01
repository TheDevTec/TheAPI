package me.devtec.theapi.bukkit.scoreboard;

import me.devtec.shared.Ref;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.NmsProvider.Action;
import me.devtec.theapi.bukkit.nms.NmsProvider.DisplayType;
import me.devtec.theapi.bukkit.nms.utils.TeamUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * https://gist.github.com/MrZalTy/f8895d84979d49af946fbcc108b1bf2b
 *
 * @author MrZalTy Forked by StraikerinaCZ
 *
 */
public class ScoreboardAPI {

	private static final String protectId = (new Random().nextLong() + "").substring(0, 5);

	private static final Config protection = new Config();

	protected final Config data = new Config();
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
		sbname = ScoreboardAPI.protectId + this.player;
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
		BukkitLoader.getPacketHandler().send(p, createObjectivePacket(1, Ref.isNewerThan(12) ? null : ""));
		for (String a : data.getKeys(player)) {
			Team team = data.getAs(player + "." + a, Team.class);
			if (team != null)
				for (Object o : this.remove(team.currentPlayer, team.name))
					BukkitLoader.getPacketHandler().send(p, o);
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
		name = StringUtils.colorize(text);
		if (!Ref.isNewerThan(12) && name.length() > 32)
			name = name.substring(0, 32);
		if (!name.equals(displayName))
			BukkitLoader.getPacketHandler().send(p, createObjectivePacket(2, name));
	}

	public void addLine(String value) {
		int i = -1;
		Set<String> slots = data.getKeys(player);
		while (slots.contains("" + (++i)));
		setLine(i, value);
	}

	public void setLine(int line, String valueText) {
		String value = StringUtils.colorize(valueText);
		if (getLine(line) != null && getLine(line).equals(!Ref.isNewerThan(12) ? cut(value) : value))
			return;
		Team team = null;
		boolean add = true;
		Set<String> s = data.getKeys(player);
		for (String wd : s) {
			Team t = data.getAs(player + "." + wd, Team.class);
			if (t.slot == line) {
				team = t;
				add = false;
			}
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
		if (!data.exists(player + "." + line))
			return;
		Team team = getTeam(line, line);
		for (Object o : this.remove(team.currentPlayer, team.name))
			BukkitLoader.getPacketHandler().send(p, o);
		data.remove(player + "." + line);
	}

	public void removeUpperLines(int line) {
		for (String a : data.getKeys(player))
			if (Integer.parseInt(a) > line) {
				Team team = data.getAs(player + "." + a, Team.class);
				for (Object o : this.remove(team.currentPlayer, team.name))
					BukkitLoader.getPacketHandler().send(p, o);
				data.remove(player + "." + line);
			}
	}

	public String getLine(int line) {
		if (data.exists(player + "." + line) && data.get(player + "." + line) != null)
			return ((Team) data.get(player + "." + line)).getValue();
		return null;
	}

	public List<String> getLines() {
		List<String> lines = new ArrayList<>();
		for (String line : data.getKeys(player))
			lines.add(((Team) data.get(player + "." + line)).getValue());
		return lines;
	}

	private void sendLine(Team team, int line, boolean add) {
		destroyed = false;
		team.sendLine(line);
		if (add)
			data.set(player + "." + line, team);
	}

	private Team getTeam(int line, int realPos) {
		Team result = data.getAs(player + "." + line, Team.class);
		if (result == null)
			data.set(player + "." + line, result = new Team(line, realPos));
		return result;
	}

	private Object[] create(String prefix, String suffix, String name, String realName, int slot) {
		ScoreboardAPI.protection.set(player + "." + name, true);
		Object[] o = new Object[2];
		o[0] = TeamUtils.createTeamPacket(0, TeamUtils.white, prefix, suffix, name, realName);
		o[1] = BukkitLoader.getNmsProvider().packetScoreboardScore(Action.CHANGE, sbname, name, slot);
		return o;
	}

	private Object[] modify(String prefix, String suffix, String name, String realName, int slot) {
		Object[] o = new Object[2];
		o[0] = TeamUtils.createTeamPacket(2, TeamUtils.white, prefix, suffix, name, realName);
		o[1] = BukkitLoader.getNmsProvider().packetScoreboardScore(Action.CHANGE, sbname, name, slot);
		return o;
	}

	private Object[] remove(String name, String realName) {
		ScoreboardAPI.protection.remove(player + "." + name);
		Object[] o = new Object[2];
		o[0] = TeamUtils.createTeamPacket(1, TeamUtils.white, "", "", name, realName);
		o[1] = BukkitLoader.getNmsProvider().packetScoreboardScore(Action.REMOVE, sbname, name, 0);
		return o;
	}

	private Object createObjectivePacket(int mode, String displayName) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardObjective();
		if (Ref.isNewerThan(16)) {
			Ref.set(packet, "d", sbname);
			Ref.set(packet, "e",
					BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(displayName)));
			Ref.set(packet, "f", BukkitLoader.getNmsProvider().getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
			Ref.set(packet, "g", mode);
		} else {
			Ref.set(packet, "a", sbname);
			Ref.set(packet, "b",
					Ref.isNewerThan(12)
					? BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(displayName))
							: displayName);
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
				if (ScoreboardAPI.protection.getBoolean(player + "." + name))
					name += ScoreboardAPI.protectId;
				Object[] o = create(prefix, suffix, currentPlayer, name,
						slott == -1 ? line : slott);
				BukkitLoader.getPacketHandler().send(p, o[0]);
				BukkitLoader.getPacketHandler().send(p, o[1]);
				first = false;
				old = null;
				changed = false;
				return;
			}
			if (old != null) {
				Object[] o = ScoreboardAPI.this.remove(old, name);
				BukkitLoader.getPacketHandler().send(p, o[0]);
				BukkitLoader.getPacketHandler().send(p, o[1]);
				old = null;
			}
			if (changed) {
				changed = false;
				Object[] o = modify(prefix, suffix, currentPlayer, name,
						slott == -1 ? line : slott);
				BukkitLoader.getPacketHandler().send(p, o[0]);
				BukkitLoader.getPacketHandler().send(p, o[1]);
			}
		}

		public String getValue() {
			return Ref.isOlderThan(13) ? prefix + currentPlayer.replaceFirst(format, "") + suffix
					: prefix + suffix;
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