package me.DevTec;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import me.DevTec.NMS.NMSAPI.Action;
import me.DevTec.NMS.NMSAPI.DisplayType;
import me.DevTec.NMS.NMSPlayer;
import me.DevTec.NMS.Reflections;
import me.DevTec.Other.LoaderClass;

/**
 * https://gist.github.com/MrZalTy/f8895d84979d49af946fbcc108b1bf2b
 * @author MrZalTy
 *
 */
public class ScoreboardAPI {
	private static Field teamlist= Reflections.getField(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"), TheAPI.isOlder1_9() ? "g" : "h");
	private static Object reset=Reflections.get(Reflections.getField(Reflections.getNMSClass("EnumChatFormat"),"RESET"),null);
	private final NMSPlayer p;
	private String name="TheAPI";

	public ScoreboardAPI(Player player) {
		p=TheAPI.getNMSAPI().getNMSPlayerAPI(player);
		create();
	}
	
	private void create() {
		if(LoaderClass.scoreboard.containsKey(p.getName()) && LoaderClass.scoreboard.get(p.getName())==this)return;
		if(LoaderClass.scoreboard.containsKey(p.getName()) && LoaderClass.scoreboard.get(p.getName())!=this)
			LoaderClass.scoreboard.get(p.getName()).destroy();
		LoaderClass.scoreboard.put(p.getName(),this);
		p.sendPacket(createObjectivePacket(0, name));
		Object packet =TheAPI.getNMSAPI().getPacketPlayOutScoreboardDisplayObjective();
		Reflections.setField(packet, "a", 1);
		Reflections.setField(packet, "b", p.getName());
		p.sendPacket(packet);
	}

	public void remove() {
		destroy();
	}
	
	public void destroy() {
		if(LoaderClass.scoreboard.containsKey(p.getName()))
			LoaderClass.scoreboard.remove(p.getName());
		p.sendPacket(createObjectivePacket(1, null));
		for (Team team : LoaderClass.map.getValues(p.getName()))
			p.sendPacket(team.remove());
		LoaderClass.map.clear();
	}

	public void setTitle(String name) {
		setDisplayName(name);
	}

	public void setName(String name) {
		setDisplayName(name);
	}

	public void setDisplayName(String name) {
		create();
		this.name = TheAPI.colorize(name);
		p.sendPacket(createObjectivePacket(2, this.name));
	}

	public void addLine(String value) {
		for(int i = 0; i > -1; ++i) {
			if(!LoaderClass.map.containsThread(p.getName(),i)) {
				setLine(i, value);
				break;
			}
		}
	}

	public void setLine(int line, String value) {
		create();
		value=TheAPI.colorize(value);
		if(LoaderClass.map.containsThread(p.getName(),line) && LoaderClass.map.get(p.getName(),line).getValue().equals(value))return;
		Team team = getTeam(line);
		String old = team.getCurrentPlayer();
		if (old != null)
			p.sendPacket(TheAPI.getNMSAPI().getPacketPlayOutScoreboardScore(Action.REMOVE, "", old, 0));
		team.setValue(value);
		sendLine(team,line);
	}

	public void removeLine(int line) {
		create();
		if(!LoaderClass.map.containsThread(p.getName(),line))return;
		Team team = getTeam(line);
		String old = team.getCurrentPlayer();
		if (old != null) {
			p.sendPacket(TheAPI.getNMSAPI().getPacketPlayOutScoreboardScore(Action.REMOVE, "", old, 0));
			p.sendPacket(team.remove());
		}
		LoaderClass.map.removeThread(p.getName(),line);
	}

	public String getLine(int line) {
		return getTeam(line).getValue();
	}

	public int getLines() {
		return LoaderClass.map.size();
	}

	private void sendLine(Team team,int line) {
		for (Iterator<Object> r = team.sendLine(); r.hasNext();)
			p.sendPacket(r.next());
		p.sendPacket(TheAPI.getNMSAPI().getPacketPlayOutScoreboardScore(Action.CHANGE, p.getName(), team.getCurrentPlayer(), line));
		team.reset();
		LoaderClass.map.put(p.getName(),line, team);
	}

	private Team getTeam(int line) {
		if (!LoaderClass.map.containsThread(p.getName(),line))LoaderClass.map.put(p.getName(),line, new Team(line));
		return LoaderClass.map.get(p.getName(),line);
	}
	
	private Object createObjectivePacket(int mode, String displayName) {
		Object packet =TheAPI.getNMSAPI().getPacketPlayOutScoreboardObjective();
		Reflections.setField(packet, "a", p.getName());
		Reflections.setField(packet, "d", mode);
		if (mode == 0 || mode == 2) {
			Reflections.setField(packet, "b", displayName);
			Reflections.setField(packet, "c", TheAPI.getNMSAPI().getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
		}
		return packet;
	}

	public class Team {
		private final String name;
		private String prefix = "",suffix = "",currentPlayer,oldPlayer;
		private boolean changed, playerChanged;
		private boolean first = true;

		private Team(int slot) {
			this.name = slot+"";
		}

		public String getName() {
			return name;
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			if (this.prefix == null || !this.prefix.equals(prefix))
				this.changed = true;
			this.prefix = prefix;
		}

		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(String suffix) {
			if (this.suffix == null || !this.suffix.equals(prefix))
				this.changed = true;
			this.suffix = suffix;
		}

		private Object c(int mode) {
			Object packet = TheAPI.getNMSAPI().getPacketPlayOutScoreboardTeam();
			if(TheAPI.isOlder1_9()) {
				Reflections.setField(packet, "a", name);
				Reflections.setField(packet, "h", mode);
				Reflections.setField(packet, "b", "");
				Reflections.setField(packet, "c", prefix);
				Reflections.setField(packet, "d", suffix);
				Reflections.setField(packet, "i", 0);
				Reflections.setField(packet, "e", "always");
				Reflections.setField(packet, "f", 0);
				return packet;
			}
			if(!TheAPI.isNewVersion()) {
				Reflections.setField(packet, "a", name);
				Reflections.setField(packet, "b", "");
				Reflections.setField(packet, "c", prefix);
				Reflections.setField(packet, "d", suffix);
				Reflections.setField(packet, "e", "always");
				Reflections.setField(packet, "g", 0);
				Reflections.setField(packet, "i", mode);
				return packet;
			}
			Reflections.setField(packet, "a", name);
			Reflections.setField(packet, "b", TheAPI.getNMSAPI().getIChatBaseComponentText(""));
			Reflections.setField(packet, "c", TheAPI.getNMSAPI().getIChatBaseComponentText(prefix));
			Reflections.setField(packet, "d", TheAPI.getNMSAPI().getIChatBaseComponentText(suffix));
			Reflections.setField(packet, "e", "always");
			Reflections.setField(packet, "g", reset);
			Reflections.setField(packet, "i", mode);
			return packet;
		}

		public Object remove() {
			Object packet = TheAPI.getNMSAPI().getPacketPlayOutScoreboardTeam();
			Reflections.setField(packet, "a", name);
			Reflections.setField(packet, TheAPI.isOlder1_9() ? "h" : "i", 1);
			first = true;
			return packet;
		}

		public void setPlayer(String name) {
			if (currentPlayer == null || !currentPlayer.equals(name))
				playerChanged = true;
			oldPlayer = currentPlayer;
			currentPlayer = name;
		}

		public Iterator<Object> sendLine() {
			ArrayList<Object> packets = Lists.newArrayList();
			if (first) {
				packets.add(c(0));
			} else if (changed) {
				packets.add(c(2));
			}
			if (first || playerChanged) {
				if (oldPlayer != null)
					packets.add(createPlayer(4, oldPlayer));
				packets.add(createPlayer(3, currentPlayer));
			}
			if (first)
				first = false;
			return packets.iterator();
		}

		public void reset() {
			changed = false;
			playerChanged = false;
			oldPlayer = null;
		}

		@SuppressWarnings("unchecked")
		public Object createPlayer(int mode, String playerName) {
			Object packet = TheAPI.getNMSAPI().getPacketPlayOutScoreboardTeam();
			Reflections.setField(packet, "a", name);
			Reflections.setField(packet, TheAPI.isOlder1_9() ? "h" : "i", mode);
			((List<String>) Reflections.get(teamlist, packet)).add(playerName);
			return packet;
		}

		public String getCurrentPlayer() {
			return currentPlayer;
		}

		public String getValue() {
			return getPrefix() + getCurrentPlayer() + getSuffix();
		}

		public void setValue(String value) {
			if (value.length() <= 16) {
				setPrefix("");
				setSuffix("");
				setPlayer(value);
			} else if (value.length() <= 32) {
				setPrefix(value.substring(0, 16));
				setPlayer(value.substring(16));
				setSuffix("");
			} else {
				setPrefix(value.substring(0, 16));
				setPlayer(value.substring(16, 32));
				setSuffix(value.length() < 48 ? value.substring(32) : value.substring(32,48));
			}
		}
	}}