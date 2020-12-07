package me.DevTec.TheAPI.ScoreboardAPI;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.TheCoder;
import me.DevTec.TheAPI.Utils.DataKeeper.Data;
import me.DevTec.TheAPI.Utils.DataKeeper.Collections.UnsortedList;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI.Action;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI.DisplayType;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

/**
 * https://gist.github.com/MrZalTy/f8895d84979d49af946fbcc108b1bf2b
 * 
 * @author MrZalTy
 *
 */
public class ScoreboardAPI {
	private Data data = new Data();
	private static Field teamlist = Ref.field(Ref.nms("PacketPlayOutScoreboardTeam"), TheAPI.isOlder1_9() ? "g" : "h");
	private Player p;
	private String player;
	private int slott = -1;
	private String name = "TheAPI";

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
		Ref.sendPacket(p, createObjectivePacket(1, null));
		removeUpperLines(-1);
	}

	public void setTitle(String name) {
		setDisplayName(name);
	}

	public void setName(String name) {
		setDisplayName(name);
	}

	public void setDisplayName(String a) {
		name = TheAPI.colorize(a);
		if (ScoreboardAPI.a)
			name = name.substring(0, name.length() > 32 ? 32 : name.length());
		Ref.sendPacket(p, createObjectivePacket(2, name));
	}

	public void addLine(String value) {
		int i = -1;
		while (true) {
			++i;
			if (!data.exists(player+"."+i)) {
				setLine(i, value);
				break;
			}
		}
	}

	public void setLine(int line, String value) {
		value = TheAPI.colorize(value);
		if(getLine(line)!=null && getLine(line).equals(value))return;
		int slot = data.getKeys(player).size();
		for(String wd : data.getKeys(player)) {
			Team t = getTeam(Integer.valueOf(wd), 0);
			if(t.name.equals(line+"")) {
				slot=Integer.valueOf(TheCoder.fromColor(t.currentPlayer));
			}
		}
		Team team = getTeam(line, slot);
		if(team==null)team = getTeam(line, slot);
		team.setValue(value);
		sendLine(team, line);
	}

	public void removeLine(int line) {
		if(!data.exists(player+"."+line))return;
		Team team = getTeam(line, line);
		String old = team.currentPlayer;
		if (old != null) {
			Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, "", old, 0));
			Ref.sendPacket(p, team.remove());
		}
		data.remove(player+"."+line);
	}
	
	public void removeUpperLines(int line) {
		for(String a : data.getKeys(player)) {
			if(Integer.parseInt(a)>line)
				removeLine(Integer.parseInt(a));
		}
	}

	public String getLine(int line) {
		if (data.exists(player+"."+line) && data.get(player+"."+line)!=null)
			return ((Team) data.get(player+"."+line)).getValue();
		return null;
	}

	public List<String> getLines() {
		List<String> lines = new UnsortedList<>();
		for(String line : data.getKeys(player)) {
			lines.add(getLine(Integer.parseInt(line)));
		}
		return lines;
	}
	

	private void sendLine(Team team, int line) {
		team.sendLine(p);
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.CHANGE, player, team.currentPlayer, slott==-1?line:slott));
		team.reset();
		data.set(player+"."+line, team);
	}

	private Team getTeam(int line, int realPos) {
		if (!data.exists(player+"."+line) || data.get(player+"."+line)==null) {
			data.set(player+"."+line, new Team(line, realPos));
		}
		return (Team) data.get(player+"."+line);
	}

	private Object createObjectivePacket(int mode, String displayName) {
		Object packet = NMSAPI.getPacketPlayOutScoreboardObjective();
		Ref.set(packet, "a", player);
		Ref.set(packet, "d", mode);
		if (mode == 0 || mode == 2) {
			Ref.set(packet, "b", !a ? NMSAPI.getIChatBaseComponentFromCraftBukkit(displayName) : displayName);
			Ref.set(packet, "c", NMSAPI.getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
		}
		return packet;
	}
	
	private static boolean a = !TheAPI.isNewVersion();

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
			Ref.set(packet, "c", a ? prefix : NMSAPI.getIChatBaseComponentFromCraftBukkit(prefix));
			else {
				Ref.set(packet, "c", prefix);
				Ref.set(packet, "d", suffix);
			}
			Ref.set(packet, TheAPI.isOlder1_9() ? "h" : "i", mode);
			return packet;
		}

		public Object remove() {
			Object re = NMSAPI.getPacketPlayOutScoreboardTeam();
			Ref.set(re, "a", name);
			Ref.set(re, TheAPI.isOlder1_9() ? "h" : "i", 1);
			first = true;
			return re;
		}

		public void sendLine(Player p) {
			if (first)
				Ref.sendPacket(p, c(0));
			else if (changed)
				Ref.sendPacket(p, c(2));
			if (first || changedPlayer) {
				if (old != null)
					Ref.sendPacket(p, createPlayer(2, old));
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
			Ref.set(create, TheAPI.isOlder1_9() ? "h" : "i", mode);
			if (TheAPI.isNewerThan(15))
				((Collection<String>) Ref.get(create, teamlist)).add(playerName);
			else
				((List<String>) Ref.get(create, teamlist)).add(playerName);
			return create;
		}

		public String getValue() {
			return prefix;
		}

		private void setPlayer(String a) {
			if (!currentPlayer.equals(a)) {
				old = currentPlayer;
				changedPlayer = true;
			}
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
					setPlayer(d.get(1));
					if (!prefix.equals(d.get(0)))
						changed = true;
					prefix = d.get(0);
					if (!suffix.equals(""))
						changed = true;
					suffix = "";
					return;
				}
				setPlayer(d.get(1));
				if (!prefix.equals(d.get(0)))
					changed = true;
				prefix = d.get(0);
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