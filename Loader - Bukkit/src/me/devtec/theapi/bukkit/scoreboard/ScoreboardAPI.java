package me.devtec.theapi.bukkit.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import me.devtec.shared.Ref;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.NmsProvider.Action;
import me.devtec.theapi.bukkit.nms.NmsProvider.DisplayType;

/**
 * https://gist.github.com/MrZalTy/f8895d84979d49af946fbcc108b1bf2b
 *
 * @author MrZalTy Forked by StraikerinaCZ
 *
 */
public class ScoreboardAPI {

	private static final Class<?> sbTeam = Ref
			.getClass("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
	private static final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref
			.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));
	private static final Object white = Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a",
			char.class) == null
					? Ref.invokeStatic(Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a", int.class), -1)
					: Ref.invokeStatic(Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a", char.class),
							'f');

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
		this.p = player;
		this.slott = slot;
		this.player = player.getName();
		this.sbname = ScoreboardAPI.protectId + this.player;
		if (this.sbname.length() > 16)
			this.sbname = this.sbname.substring(0, 16);
		BukkitLoader.getPacketHandler().send(this.p, this.createObjectivePacket(0, "§0"));
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardDisplayObjective(1, null);
		Ref.set(packet, "b", this.sbname);
		BukkitLoader.getPacketHandler().send(this.p, packet);
	}

	public void setSlot(int slot) {
		this.slott = slot;
	}

	public void remove() {
		this.destroy();
	}

	public void destroy() {
		if (this.destroyed)
			return;
		this.destroyed = true;
		BukkitLoader.getPacketHandler().send(this.p, this.createObjectivePacket(1, Ref.isNewerThan(12) ? null : ""));
		for (String a : this.data.getKeys(this.player)) {
			Team team = this.data.getAs(this.player + "." + a, Team.class);
			if (team != null)
				for (Object o : this.remove(team.currentPlayer, team.name))
					BukkitLoader.getPacketHandler().send(this.p, o);
		}
		this.data.clear();
	}

	public void setTitle(String name) {
		this.setDisplayName(name);
	}

	public void setName(String name) {
		this.setDisplayName(name);
	}

	public void setDisplayName(String text) {
		this.destroyed = false;
		String displayName = this.name;
		this.name = StringUtils.colorize(text);
		if (!Ref.isNewerThan(12) && this.name.length() > 32)
			this.name = this.name.substring(0, 32);
		if (!this.name.equals(displayName))
			BukkitLoader.getPacketHandler().send(this.p, this.createObjectivePacket(2, this.name));
	}

	public void addLine(String value) {
		int i = -1;
		Set<String> slots = this.data.getKeys(this.player);
		while (!slots.contains("" + (++i)))
			;
		this.setLine(i, value);
	}

	public void setLine(int line, String valueText) {
		String value = StringUtils.colorize(valueText);
		if (this.getLine(line) != null && this.getLine(line).equals(!Ref.isNewerThan(12) ? this.cut(value) : value))
			return;
		Team team = null;
		boolean add = true;
		Set<String> s = this.data.getKeys(this.player);
		for (String wd : s) {
			Team t = this.data.getAs(this.player + "." + wd, Team.class);
			if (t.slot == line) {
				team = t;
				add = false;
			}
		}
		if (add)
			team = this.getTeam(line, line);
		team.setValue(value);
		this.sendLine(team, line, add);
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
		if (!this.data.exists(this.player + "." + line))
			return;
		Team team = this.getTeam(line, line);
		for (Object o : this.remove(team.currentPlayer, team.name))
			BukkitLoader.getPacketHandler().send(this.p, o);
		this.data.remove(this.player + "." + line);
	}

	public void removeUpperLines(int line) {
		for (String a : this.data.getKeys(this.player))
			if (Integer.parseInt(a) > line) {
				Team team = this.data.getAs(this.player + "." + a, Team.class);
				for (Object o : this.remove(team.currentPlayer, team.name))
					BukkitLoader.getPacketHandler().send(this.p, o);
				this.data.remove(this.player + "." + line);
			}
	}

	public String getLine(int line) {
		if (this.data.exists(this.player + "." + line) && this.data.get(this.player + "." + line) != null)
			return ((Team) this.data.get(this.player + "." + line)).getValue();
		return null;
	}

	public List<String> getLines() {
		List<String> lines = new ArrayList<>();
		for (String line : this.data.getKeys(this.player))
			lines.add(((Team) this.data.get(this.player + "." + line)).getValue());
		return lines;
	}

	private void sendLine(Team team, int line, boolean add) {
		this.destroyed = false;
		team.sendLine(line);
		if (add)
			this.data.set(this.player + "." + line, team);
	}

	private Team getTeam(int line, int realPos) {
		Team result = this.data.getAs(this.player + "." + line, Team.class);
		if (result == null)
			this.data.set(this.player + "." + line, result = new Team(line, realPos));
		return result;
	}

	private Object[] create(String prefix, String suffix, String name, String realName, int slot) {
		ScoreboardAPI.protection.set(this.player + "." + name, true);
		Object[] o = new Object[2];
		o[0] = this.createTeamPacket(0, prefix, suffix, name, realName);
		o[1] = BukkitLoader.getNmsProvider().packetScoreboardScore(Action.CHANGE, this.sbname, name, slot);
		return o;
	}

	private Object[] modify(String prefix, String suffix, String name, String realName, int slot) {
		Object[] o = new Object[2];
		o[0] = this.createTeamPacket(2, prefix, suffix, name, realName);
		o[1] = BukkitLoader.getNmsProvider().packetScoreboardScore(Action.CHANGE, this.sbname, name, slot);
		return o;
	}

	private Object[] remove(String name, String realName) {
		ScoreboardAPI.protection.remove(this.player + "." + name);
		Object[] o = new Object[2];
		o[0] = this.createTeamPacket(1, "", "", name, realName);
		o[1] = BukkitLoader.getNmsProvider().packetScoreboardScore(Action.REMOVE, this.sbname, name, 0);
		return o;
	}

	private Object createTeamPacket(int mode, String prefix, String suffix, String name, String realName) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
		Object nameList = ImmutableList.of(name);
		String always = "ALWAYS";
		if (Ref.isNewerThan(16)) {
			Ref.set(packet, "i", realName);
			try {
				Object o = ScoreboardAPI.unsafe.allocateInstance(ScoreboardAPI.sbTeam);
				Ref.set(o, "a", BukkitLoader.getNmsProvider().chatBase("{\"text\":\"" + name + "\"}"));
				Ref.set(o, "b", BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(prefix)));
				Ref.set(o, "c", BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(suffix)));
				Ref.set(o, "d", always);
				Ref.set(o, "e", always);
				Ref.set(o, "f", ScoreboardAPI.white);
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
					Ref.set(packet, "g", Ref.isNewerThan(12) ? ScoreboardAPI.white : -1);
				Ref.set(packet, Ref.isNewerThan(8) ? "i" : "h", mode);
				Ref.set(packet, Ref.isNewerThan(8) ? "h" : "g", nameList);
			} else {
				Ref.set(packet, "f", mode);
				Ref.set(packet, "e", nameList);
			}
		}
		return packet;
	}

	private Object createObjectivePacket(int mode, String displayName) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardObjective();
		if (Ref.isNewerThan(16)) {
			Ref.set(packet, "d", this.sbname);
			Ref.set(packet, "e",
					BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(displayName)));
			Ref.set(packet, "f", BukkitLoader.getNmsProvider().getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
			Ref.set(packet, "g", mode);
		} else {
			Ref.set(packet, "a", this.sbname);
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
			this.currentPlayer = s;
			if (Ref.isOlderThan(13)) {
				this.currentPlayer += "§f";
				this.format = this.currentPlayer;
			} else
				this.format = null;
			this.slot = slot;
			this.name = "" + slot;
		}

		public void sendLine(int line) {
			if (this.first) {
				if (ScoreboardAPI.protection.getBoolean(ScoreboardAPI.this.player + "." + this.name))
					this.name += ScoreboardAPI.protectId;
				Object[] o = ScoreboardAPI.this.create(this.prefix, this.suffix, this.currentPlayer, this.name,
						ScoreboardAPI.this.slott == -1 ? line : ScoreboardAPI.this.slott);
				BukkitLoader.getPacketHandler().send(ScoreboardAPI.this.p, o[0]);
				BukkitLoader.getPacketHandler().send(ScoreboardAPI.this.p, o[1]);
				this.first = false;
				this.old = null;
				this.changed = false;
				return;
			}
			if (this.old != null) {
				Object[] o = ScoreboardAPI.this.remove(this.old, this.name);
				BukkitLoader.getPacketHandler().send(ScoreboardAPI.this.p, o[0]);
				BukkitLoader.getPacketHandler().send(ScoreboardAPI.this.p, o[1]);
				this.old = null;
			}
			if (this.changed) {
				this.changed = false;
				Object[] o = ScoreboardAPI.this.modify(this.prefix, this.suffix, this.currentPlayer, this.name,
						ScoreboardAPI.this.slott == -1 ? line : ScoreboardAPI.this.slott);
				BukkitLoader.getPacketHandler().send(ScoreboardAPI.this.p, o[0]);
				BukkitLoader.getPacketHandler().send(ScoreboardAPI.this.p, o[1]);
			}
		}

		public String getValue() {
			return Ref.isOlderThan(13) ? this.prefix + this.currentPlayer.replaceFirst(this.format, "") + this.suffix
					: this.prefix + this.suffix;
		}

		private void setPlayer(String a) {
			a = this.format + a;
			if (this.currentPlayer == null || !this.currentPlayer.equals(a)) {
				this.old = this.currentPlayer;
				this.currentPlayer = a;
			}
		}

		public void setValue(String a) {
			if (a == null) {
				if (!this.prefix.equals(""))
					this.changed = true;
				this.prefix = "";
				if (!this.suffix.equals(""))
					this.changed = true;
				this.suffix = "";
				this.setPlayer("");
				return;
			}
			if (Ref.isOlderThan(13)) {
				if (a.isEmpty()) {
					this.setPlayer("");
					if (!this.prefix.equals(""))
						this.changed = true;
					this.prefix = "";
					if (!this.suffix.equals(""))
						this.changed = true;
					this.suffix = "";
					return;
				}

				List<String> d = StringUtils.fixedSplit(a, 16);
				if (a.length() <= 16) {
					this.setPlayer("");
					if (!this.prefix.equals(d.get(0)))
						this.changed = true;
					this.prefix = d.get(0);
					if (!this.suffix.equals(""))
						this.changed = true;
					this.suffix = "";
					return;
				}
				if (a.length() <= 32) {
					if (!this.prefix.equals(d.get(0)))
						this.changed = true;
					this.prefix = d.get(0);
					this.setPlayer("");
					if (d.size() > 1) {
						if (!this.suffix.equals(d.get(1)))
							this.changed = true;
						this.suffix = d.get(1);
					} else {
						if (!this.suffix.equals(""))
							this.changed = true;
						this.suffix = "";
					}
					return;
				}
				if (Ref.isOlderThan(8)) {
					if (!this.prefix.equals(d.get(0)))
						this.changed = true;
					this.prefix = d.get(0);
					d = StringUtils.fixedSplit(a = a.substring(this.prefix.length()), 17 - this.format.length());
					this.setPlayer(d.get(0));
					d = StringUtils.fixedSplit(a.substring(d.get(0).length()), 16);
					if (!this.suffix.equals(d.get(0)))
						this.changed = true;
					this.suffix = d.get(0);
					return;
				}
				if (!this.prefix.equals(d.get(0)))
					this.changed = true;
				this.prefix = d.get(0);
				a = a.substring(d.get(0).length());
				d = StringUtils.fixedSplit(a, 18);
				this.setPlayer(StringUtils.getLastColors(this.prefix) + d.get(0));
				a = a.substring(d.get(0).length());
				d = StringUtils.fixedSplit(a, 17);
				if (d.isEmpty()) {
					if (!this.suffix.equals(""))
						this.changed = true;
					this.suffix = "";
				} else {
					if (!this.suffix.equals(d.get(0)))
						this.changed = true;
					this.suffix = d.get(0);
				}
			} else {
				if (!this.prefix.equals(a))
					this.changed = true;
				this.prefix = a;
			}
		}
	}
}