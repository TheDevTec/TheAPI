package me.devtec.theapi.scoreboardapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.TheCoder;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.nms.NMSAPI.Action;
import me.devtec.theapi.utils.nms.NMSAPI.DisplayType;
import me.devtec.theapi.utils.reflections.Ref;

/**
 * https://gist.github.com/MrZalTy/f8895d84979d49af946fbcc108b1bf2b
 * 
 * @author MrZalTy
 *
 */
public class ScoreboardAPI {
	protected Data data = new Data();
	protected static Field teamlist = Ref.field(Ref.nms("PacketPlayOutScoreboardTeam"), TheAPI.isOlderThan(9) ? "g" : "h");
	protected Player p;
	protected String player;
	protected int slott = -1;
	protected String name = "TheAPI";

	public ScoreboardAPI(Player player) {
		this(player, -1);
	}
	
	/**
	 * 
	 * @param player Player - Holder of scoreboard
	 * @param usePackets Use nms teams?
	 * @param useTeams Use bukkit teams?
	 * @param slot -1 to adaptive slot
	 */
	public ScoreboardAPI(Player player, int slot) {
		p = player;
		slott=slot;
		this.player = player.getName();
		Ref.sendPacket(p, createObjectivePacket(0, name));
		Object packetD = NMSAPI.getPacketPlayOutScoreboardDisplayObjective();
		Ref.set(packetD, "a", 1);
		Ref.set(packetD, "b", this.player);
		Ref.sendPacket(p, packetD);
	}
	
	public void setSlot(int slot) {
		slott=slot;
	}
	
	public void remove() {
		destroy();
	}

	public void destroy() {
		for(String a : data.getKeys(player)){
			Team team = data.getAs(player+"."+a, Team.class);
			Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, "", team.currentPlayer, 0));
			Ref.sendPacket(p, team.remove());
		}
		Ref.sendPacket(p, createObjectivePacket(1, null));
	}

	public void setTitle(String name) {
		setDisplayName(name);
	}

	public void setName(String name) {
		setDisplayName(name);
	}

	public void setDisplayName(String a) {
		name = TheAPI.colorize(a);
		if (ScoreboardAPI.a && name.length() > 32)
			name = name.substring(0, 32);
		Ref.sendPacket(p, createObjectivePacket(2, name));
	}

	public void addLine(String value) {
		int i = -1;
		Set<String> slots = data.getKeys(player);
		while (!slots.contains(""+(++i)));
		setLine(i, value);
	}

	public void setLine(int line, String value) {
		value = TheAPI.colorize(value);
		if(getLine(line)!=null && getLine(line).equals(a?cut(value):value))return;
		Team team = null;
		boolean add = true;
		for(String wd : data.getKeys(player)) {
			Team t = data.getAs(player+"."+wd, Team.class);
			if(t.name.equals(line+"")) {
				team=t;
				add=false;
			}
		}
		if(team==null)team = getTeam(line, data.getKeys(player).size());
		team.setValue(value);
		sendLine(team, line, add);
	}

	private String cut(String a) {
		List<String> d = StringUtils.fixedSplit(a, 16);
		if (a.length() <= 16)return d.get(0);
		if (a.length() <= 32)return d.get(0)+d.get(1);
		return d.get(0)+d.get(1)+d.get(2);
	}

	public void removeLine(int line) {
		if(!data.exists(player+"."+line))return;
		Team team = getTeam(line, 0);
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, "", team.currentPlayer, 0));
		Ref.sendPacket(p, team.remove());
		data.remove(player+"."+line);
	}
	
	public void removeUpperLines(int line) {
		for(String a : data.getKeys(player)) {
			if(Integer.parseInt(a)>line) {
				Team team = data.getAs(player+"."+a, Team.class);
				Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, "", team.currentPlayer, 0));
				Ref.sendPacket(p, team.remove());
				data.remove(player+"."+line);
			}
		}
	}

	public String getLine(int line) {
		if (data.exists(player+"."+line) && data.get(player+"."+line)!=null)
			return ((Team) data.get(player+"."+line)).getValue();
		return null;
	}

	public List<String> getLines() {
		List<String> lines = new ArrayList<>();
		for(String line : data.getKeys(player)) {
			lines.add(getLine(Integer.parseInt(line)));
		}
		return lines;
	}

	private void sendLine(Team team, int line, boolean add) {
		if(a)Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, player, team.old, line));
		team.sendLine();
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.CHANGE, player, team.currentPlayer, slott==-1?line:slott));
		team.reset();
		if(add)
			data.set(player+"."+line, team);
	}

	private Team getTeam(int line, int realPos) {
		if (!data.exists(player+"."+line) || data.get(player+"."+line)==null)
			data.set(player+"."+line, new Team(line, realPos));
		return data.getAs(player+"."+line, Team.class);
	}

	private Object createObjectivePacket(int mode, String displayName) {
		Object packet = NMSAPI.getPacketPlayOutScoreboardObjective();
		Ref.set(packet, "a", player);
		Ref.set(packet, "d", mode);
		if (mode == 0 || mode == 2) {
			Ref.set(packet, "b", !a ? NMSAPI.getFixedIChatBaseComponent(displayName) : displayName);
			Ref.set(packet, "c", NMSAPI.getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
		}
		return packet;
	}
	
	private static boolean a = !TheAPI.isNewVersion();

	private static String path = TheAPI.isOlderThan(9) ? "h" : "i";
	
	public class Team {
		private String prefix = "", suffix = "", currentPlayer, old;
		private final String name;
		private boolean changed, changedPlayer, first = true;
		
		private Team(int slot, int realPos) {
			name=""+slot;
			currentPlayer = TheCoder.toColor(realPos);
		}

		private Object c(int mode) {
			Object packet = NMSAPI.getPacketPlayOutScoreboardTeam();
			Ref.set(packet, "a", name);
			if(!a)
			Ref.set(packet, "c", a ? prefix : NMSAPI.getFixedIChatBaseComponent(prefix));
			else {
				Ref.set(packet, "c", prefix);
				Ref.set(packet, "d", suffix);
			}
			Ref.set(packet, path, mode);
			return packet;
		}

		public Object remove() {
			Object re = NMSAPI.getPacketPlayOutScoreboardTeam();
			Ref.set(re, "a", name);
			Ref.set(re, path, 1);
			first = true;
			return re;
		}

		public void sendLine() {
			if (first)
				Ref.sendPacket(p, c(0));
			else if (changed)
				Ref.sendPacket(p, c(2));
			if (first || changedPlayer) {
				if (old != null) 
					Ref.sendPacket(p, createPlayer(4, old));
				Ref.sendPacket(p, createPlayer(3, currentPlayer));
				changedPlayer = false;
				first = false;
			}
		}

		public void reset() {
			changed = false;
		}

		@SuppressWarnings("unchecked")
		public Object createPlayer(int mode, String playerName) {
			Object create = NMSAPI.getPacketPlayOutScoreboardTeam();
			Ref.set(create, "a", name);
			Ref.set(create, path, mode);
			((Collection<String>) Ref.get(create, teamlist)).add(playerName);
			return create;
		}

		public String getValue() {
			return ScoreboardAPI.a?prefix+currentPlayer+suffix:prefix;
		}

		private void setPlayer(String a) {
			if (currentPlayer==null||!currentPlayer.equals(a))
				changedPlayer = true;
			old = currentPlayer;
			currentPlayer = a;
		}

		public void setValue(String a) {
			if (ScoreboardAPI.a) {
				List<String> d = StringUtils.fixedSplit(a, 16);
				if (a.length() <= 16) {
					setPlayer(d.get(0));
					if (!prefix.equals(""))
						changed = true;
					prefix = "";
					if (!suffix.equals(""))
						changed = true;
					suffix = "";
					return;
				}
				if (a.length() <= 32) {
					if (!prefix.equals(d.get(0)))
						changed = true;
					prefix = d.get(0);
					setPlayer(d.get(1));
					if (!suffix.equals(""))
						changed = true;
					suffix = "";
					return;
				}
				if (!prefix.equals(d.get(0)))
					changed = true;
				prefix = d.get(0);
				setPlayer(d.get(1));
				if (!suffix.equals(d.get(2)))
					changed = true;
				suffix = d.get(2);
			} else {
				if (!prefix.equals(a))
					changed = true;
				prefix = a;
			}
		}
	}
}