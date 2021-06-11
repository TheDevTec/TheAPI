package me.devtec.theapi.scoreboardapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
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
	protected Player p;
	protected String player;
	protected int slott = -1;
	protected String name = "";

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
		Ref.sendPacket(p, createObjectivePacket(1, ""));
		for(String a : data.getKeys(player)){
			Team team = data.getAs(player+'.'+a, Team.class);
			if(team!=null) {
				team.remove();
			}
		}
		data.clear();
	}

	public void setTitle(String name) {
		setDisplayName(name);
	}

	public void setName(String name) {
		setDisplayName(name);
	}

	public void setDisplayName(String a) {
		String displayName = name;
		name = TheAPI.colorize(a);
		if (ScoreboardAPI.a && name.length() > 32)
			name = name.substring(0, 32);
		if(!name.equals(displayName))
		Ref.sendPacket(p, createObjectivePacket(2, name));
	}

	public void addLine(String value) {
		int i = -1;
		Set<String> slots = data.getKeys(player);
		while (!slots.contains(""+(++i)));
		setLine(i, value);
	}

	public synchronized void setLine(int line, String value) {
		value = TheAPI.colorize(value);
		if(getLine(line)!=null && getLine(line).equals(a?cut(value):value))return;
		Team team = null;
		boolean add = true;
		for(String wd : data.getKeys(player)) {
			Team t = data.getAs(player+'.'+wd, Team.class);
			if(t.name.equals(""+line)) {
				team=t;
				add=false;
			}
		}
		if(team==null)team = getTeam(line, data.getKeys(player).size());
		team.setValue(value);
		sendLine(team, line, add);
	}

	private String cut(String a) {
		List<String> d = StringUtils.fixedSplit(a, 17);
		if (a.length() <= 17)return d.get(0);
		if (a.length() <= 34)return d.get(0)+d.get(1);
		String text = d.get(0);
		a=a.substring(d.get(0).length());
		d = StringUtils.fixedSplit(a, 18);
		text+=StringUtils.getLastColors(text)+d.get(0);
		a=a.substring(d.get(0).length());
		d = StringUtils.fixedSplit(a, 17);
		text+=d.get(0);
		return text;
	}

	public synchronized void removeLine(int line) {
		if(!data.exists(player+'.'+line))return;
		Team team = getTeam(line, line);
		team.remove();
		Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, player, team.currentPlayer, 0));
		data.remove(player+'.'+line);
	}
	
	public synchronized void removeUpperLines(int line) {
		for(String a : data.getKeys(player)) {
			if(Integer.parseInt(a)>line) {
				Team team = data.getAs(player+'.'+a, Team.class);
				team.remove();
				Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, player, team.currentPlayer, 0));
				data.remove(player+'.'+line);
			}
		}
	}

	public String getLine(int line) {
		if (data.exists(player+'.'+line) && data.get(player+'.'+line)!=null)
			return ((Team) data.get(player+'.'+line)).getValue();
		return null;
	}

	public List<String> getLines() {
		List<String> lines = new ArrayList<>();
		for(String line : data.getKeys(player))
			lines.add(((Team) data.get(player+'.'+line)).getValue());
		return lines;
	}

	private synchronized void sendLine(Team team, int line, boolean add) {
		team.sendLine(line);
		if(add)
			data.set(player+'.'+line, team);
	}

	private Team getTeam(int line, int realPos) {
		if (!data.exists(player+'.'+line) || data.get(player+'.'+line)==null)
			data.set(player+'.'+line, new Team(line, realPos));
		return data.getAs(player+'.'+line, Team.class);
	}

	private Object createObjectivePacket(int mode, String displayName) {
		Object packet = NMSAPI.getPacketPlayOutScoreboardObjective();
		Ref.set(packet, "a", player);
		Ref.set(packet, "b", !a ? NMSAPI.getFixedIChatBaseComponent(displayName) : displayName);
		if(TheAPI.isNewerThan(7)) {
			Ref.set(packet, "c", NMSAPI.getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
			Ref.set(packet, "d", mode);
		}else
			Ref.set(packet, "c", mode);
		return packet;
	}
	
	private static boolean a = !TheAPI.isNewVersion();
	private static Field modus = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardTeam","PacketPlayOutScoreboardTeam"), TheAPI.isOlderThan(9) ? (TheAPI.isOlderThan(8)?"f":"h") : "i"),
			players = Ref.field(Ref.nmsOrOld("network.protocol.game.PacketPlayOutScoreboardTeam","PacketPlayOutScoreboardTeam"), TheAPI.isOlderThan(9) ? (TheAPI.isOlderThan(8)?"e":"g") : "h");
	
	public class Team {
		private String prefix = "", suffix = "", currentPlayer, old;
		private final String name, format;
		private boolean changed, first = true;
		private Team(int slot, int realPos) {
			currentPlayer = TheCoder.toColor(realPos)+"";
			if(a) {
				currentPlayer+="§f";
				format=currentPlayer;
			}else format=null;
			name=""+realPos;
		}

		private synchronized void c(int mode) {
			Object packet = NMSAPI.getPacketPlayOutScoreboardTeam();
			Ref.set(packet, "a", name);
			if(!a)
				Ref.set(packet, "c", NMSAPI.getFixedIChatBaseComponent(prefix));
			else {
				Ref.set(packet, "c", prefix);
				Ref.set(packet, "d", suffix);
			}
			Ref.set(packet, modus, mode);
			Ref.set(packet, players, Collections.singleton(currentPlayer));
			Ref.sendPacket(p, packet);
		}

		public synchronized void remove() {
			createPlayer(4, currentPlayer);
			first = true;
			Object delete = NMSAPI.getPacketPlayOutScoreboardTeam();
			Ref.set(delete, "a", name);
			Ref.set(delete, modus, 1);
			Ref.sendPacket(p, delete);
		}

		public synchronized void sendLine(int line) {
			if (first) {
				c(0);
				createPlayer(3, currentPlayer);
				first = false;
				old=null;
				Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.CHANGE, player, currentPlayer, slott==-1?line:slott));
				return;
			}
			if (changed)
				c(2);
			if(old!=null) {
				createPlayer(3, currentPlayer);
				createPlayer(4, old);
				Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, "", old, 0));
				old=null;
			}
			Ref.sendPacket(p, NMSAPI.getPacketPlayOutScoreboardScore(Action.CHANGE, player, currentPlayer, slott==-1?line:slott));
			changed = false;
		}
		
		private synchronized void createPlayer(int mode, String playerName) {
			Object create = NMSAPI.getPacketPlayOutScoreboardTeam();
			Ref.set(create, "a", name);
			Ref.set(create, modus, mode);
			Ref.set(create, players, Collections.singleton(playerName));
			Ref.sendPacket(p, create);
		}

		public String getValue() {
			return ScoreboardAPI.a?prefix+currentPlayer.replaceFirst(format,"")+suffix:prefix;
		}

		private void setPlayer(String a) {
			a=format+a;
			if (currentPlayer==null||!currentPlayer.equals(a)) {
				old = currentPlayer;
				currentPlayer = a;
			}
		}

		public synchronized void setValue(String a) {
			if(a==null)a="";
			if (ScoreboardAPI.a) {
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
					if(d.size()>1) {
						if (!suffix.equals(d.get(1)))
							changed = true;
						suffix = d.get(1);
					}else {
						if (!suffix.equals(""))
							changed = true;
						suffix = "";
					}
					return;
				}
				if(TheAPI.isOlderThan(8)) {
					if (!prefix.equals(d.get(0)))
						changed = true;
					prefix = d.get(0);
					d = StringUtils.fixedSplit(a=a.substring(prefix.length()), 17-format.length());
					setPlayer(d.get(0));
					d = StringUtils.fixedSplit(a=a.substring(d.get(0).length()), 16);
					if (!suffix.equals(d.get(0)))
						changed = true;
					suffix = d.get(0);
					return;
				}
				if (!prefix.equals(d.get(0)))
					changed = true;
				prefix = d.get(0);
				a=a.substring(d.get(0).length());
				d = StringUtils.fixedSplit(a, 18);
				setPlayer(StringUtils.getLastColors(prefix)+d.get(0));
				a=a.substring(d.get(0).length());
				d = StringUtils.fixedSplit(a, 17);
				if(d.isEmpty()) {
					if (!suffix.equals(""))
						changed = true;
					suffix = "";
				}else {
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