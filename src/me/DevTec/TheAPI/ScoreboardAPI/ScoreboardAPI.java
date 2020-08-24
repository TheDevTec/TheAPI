package me.DevTec.TheAPI.ScoreboardAPI;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.google.common.collect.Lists;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.TheCoder;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSPlayer;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI.Action;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI.DisplayType;
import me.DevTec.TheAPI.Utils.Reflections.Reflections;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

/**
 * https://gist.github.com/MrZalTy/f8895d84979d49af946fbcc108b1bf2b
 * @author MrZalTy
 *
 */
public class ScoreboardAPI {
	private static Field teamlist= Reflections.getField(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"), TheAPI.isOlder1_9() ? "g" : "h");
	private static Object reset=Reflections.get(Reflections.getField(Reflections.getNMSClass("EnumChatFormat"),"RESET"),null);
	private NMSPlayer p;
	private String player;
	private final int id;
	private org.bukkit.scoreboard.Scoreboard sb;
	private org.bukkit.scoreboard.Objective o;
	private boolean packets, teams;
	private String name="TheAPI";

	public ScoreboardAPI(Player player) {
		this(player, false, false);
	}

	public ScoreboardAPI(Player player, boolean usePackets) {
		this(player, usePackets, false);
	}

	@SuppressWarnings("deprecation")
	public ScoreboardAPI(Player player, boolean usePackets, boolean useTeams) {
		p=NMSAPI.getNMSPlayerAPI(player);
		this.player=player.getName();
		packets=usePackets;
		teams=useTeams;
		int sel = 0;
		for(int i = 1; i > 0; ++i) { //search first empty id
			if(!LoaderClass.unused.exist("sb."+i)) {
				LoaderClass.unused.set("sb."+i,player.getName());
				sel=i;
				break;
			}
		}
		id=sel;
		if(!packets) {
			sb=player.getServer().getScoreboardManager().getNewScoreboard();
			o=sb.getObjective(id+"")!=null ? sb.getObjective(id+"") : sb.registerNewObjective(id+"", "dummy");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		create();
	}
	
	public ScoreboardAPI(Player player, ScoreboardType type) {
		this(player, type==ScoreboardType.PACKETS?true:false, type==ScoreboardType.TEAMS?true:false);
	}
	
	public boolean usingPackets() {
		return packets;
	}
	
	public boolean usingTeams() {
		return teams;
	}
	
	private void create() {
		updatePlayer();
		if(LoaderClass.unused.exist("sbc."+p.getName())) {
		if(LoaderClass.unused.getInt("sbc."+p.getName())==id)return;
		else
			if(LoaderClass.plugin.scoreboard.containsKey(LoaderClass.unused.getInt("sbc."+p.getName())))
			LoaderClass.plugin.scoreboard.get(LoaderClass.unused.getInt("sbc."+p.getName())).destroy();
		}
		LoaderClass.unused.set("sbc."+p.getName(),id);
		LoaderClass.plugin.scoreboard.put(id,this);
		if(packets) {
		p.sendPacket(createObjectivePacket(0, name));
		Object packet =NMSAPI.getPacketPlayOutScoreboardDisplayObjective();
		Reflections.setField(packet, "a", 1);
		Reflections.setField(packet, "b", p.getName());
		p.sendPacket(packet);
		}else {
			if(p.getPlayer()!=sb)
			p.getPlayer().setScoreboard(sb);
		}
	}

	public void remove() {
		destroy();
	}
	
	public int getId() {
		return id;
	}
	
	public void destroy() { //you can destroy scoreboard, but scoreboard still "exists" -> you can recreate it by update line / displayName
		if(LoaderClass.unused.exist("sbc."+p.getName())) {
		if(LoaderClass.unused.getInt("sbc."+p.getName())==id)
			LoaderClass.unused.set("sbc."+p.getName(),null);
		}
		if(packets) {
		p.sendPacket(createObjectivePacket(1, null));
		if(LoaderClass.plugin.map.containsKey(id))
		for (Object team : LoaderClass.plugin.map.getValues(id))
			p.sendPacket(((Team)team).remove());
		}else {
			if(LoaderClass.plugin.map.containsKey(id))
			for (Integer line : LoaderClass.plugin.map.getThreads(id)) {
				removeLine(line);
			}
			if(p.getPlayer().getScoreboard()==sb)
			p.getPlayer().setScoreboard(p.getPlayer().getServer().getScoreboardManager().getNewScoreboard());
		}
		LoaderClass.plugin.scoreboard.remove(id);
		LoaderClass.plugin.map.remove(id);
	}

	public void setTitle(String name) {
		setDisplayName(name);
	}

	public void setName(String name) {
		setDisplayName(name);
	}

	public void setDisplayName(String a) {
		create();
		name = TheAPI.colorize(a);
		if(packets)
		p.sendPacket(createObjectivePacket(2, name));
		else
			o.setDisplayName(name);
	}

	public void addLine(String value) {
		for(int i = 0; i > -1; ++i) {
			if(!LoaderClass.plugin.map.containsThread(id, i)) {
				setLine(i, value);
				break;
			}
		}
	}
	
	public void updatePlayer() {
		if(!p.getPlayer().isOnline())
		if(TheAPI.getPlayer(player).getName().equals(player))
		p=NMSAPI.getNMSPlayerAPI(TheAPI.getPlayer(player));
	}

	public void setLine(int line, String value) {
		create();
		value=TheAPI.colorize(value);
		if(getLine(line)!=null&&getLine(line).equals(value))return;
		if(packets) {
			Team team = getTeam(line);
			String old = team.getCurrentPlayer();
		if (old != null)
			p.sendPacket(NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, "", old, 0));
		team.setValue(value);
		sendLine(team,line);
		}else {
			if(teams) {
			org.bukkit.scoreboard.Team team = sb.getTeam(line+"");
			if(team==null) {
				team=sb.registerNewTeam(line+"");
				team.addEntry(TheCoder.toColor(line));
			}
			try {
				if(TheAPI.isNewVersion()) {
					String prefix = value.length() > 48 ? value.substring(0,48) : value;
					team.setPrefix(prefix);
					if(value.length() > 48) {
						String suffix = value.length() > 96 ? value.substring(48, 96) : value.substring(48);
						team.setSuffix(suffix);
					}
				}else {
					String prefix = value.length() > 16 ? value.substring(0,16) : value;
					team.setPrefix(prefix);
					if(value.length() > 16) {
						String suffix = value.length() > 32 ? value.substring(16,32) : value.substring(16);
						team.setSuffix(suffix);
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			o.getScore(TheCoder.toColor(line)).setScore(line);
			LoaderClass.plugin.map.put(id,line, team);
			}else {
				String old = LoaderClass.plugin.map.containsThread(id, line) ? LoaderClass.plugin.map.get(id, line).toString() : null;
				if(old!=null)sb.resetScores(old);
				value=TheAPI.isNewVersion() ? (value.length() > 48 ? value.substring(0,48) : value) : (value.length() > 32 ? value.substring(0,32) : value);
				o.getScore(value).setScore(line);
				LoaderClass.plugin.map.put(id,line, value);
			}
		}
	}

	public void removeLine(int line) {
		create();
		if(!LoaderClass.plugin.map.containsThread(id,line))return;
		if(packets) {
		Team team = getTeam(line);
		String old = team.getCurrentPlayer();
		if (old != null) {
			p.sendPacket(NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, "", old, 0));
			p.sendPacket(team.remove());
		}
		}else {
			if(teams) {
			org.bukkit.scoreboard.Team team = sb.getTeam(line+"");
			if(team==null)return;
			team.unregister();
			sb.resetScores(TheCoder.toColor(line));
			}else {
				String old = LoaderClass.plugin.map.containsThread(id, line) ? LoaderClass.plugin.map.get(id, line).toString() : null;
				if(old==null)return;
				sb.resetScores(old);
			}
		}
		LoaderClass.plugin.map.removeThread(id,line);
	}

	public String getLine(int line) {
		if(LoaderClass.plugin.map.containsThread(id,line))
			return packets ? ((Team)LoaderClass.plugin.map.get(id,line)).getValue() : 
				(teams? ((org.bukkit.scoreboard.Team)LoaderClass.plugin.map.get(id,line)).getPrefix():LoaderClass.plugin.map.get(id,line).toString());
		return null;
	}

	public int getLines() {
		return LoaderClass.plugin.map.getThreads(id).size();
	}

	private void sendLine(Team team,int line) {
		for (Iterator<Object> r = team.sendLine(); r.hasNext();)
			p.sendPacket(r.next());
		p.sendPacket(NMSAPI.getPacketPlayOutScoreboardScore(Action.CHANGE, p.getName(), team.getCurrentPlayer(), line));
		team.reset();
		LoaderClass.plugin.map.put(id,line, team);
	}

	private Team getTeam(int line) {
		if (!LoaderClass.plugin.map.containsThread(id,line))LoaderClass.plugin.map.put(id,line, new Team(line));
		return (Team) LoaderClass.plugin.map.get(id,line);
	}
	
	private Object createObjectivePacket(int mode, String displayName) {
		Object packet =NMSAPI.getPacketPlayOutScoreboardObjective();
		Reflections.setField(packet, "a", p.getName());
		Reflections.setField(packet, "d", mode);
		if (mode == 0 || mode == 2) {
			Reflections.setField(packet, "b", displayName);
			Reflections.setField(packet, "c", NMSAPI.getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
		}
		return packet;
	}
	
	public static void destroyScoreboard(int id) {
		if(LoaderClass.unused.exist("sb."+id)) {
			if(LoaderClass.plugin.scoreboard.containsKey(id))
				LoaderClass.plugin.scoreboard.get(id).destroy();
		}
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
			Object packet = NMSAPI.getPacketPlayOutScoreboardTeam();
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
			Reflections.setField(packet, "b", NMSAPI.getIChatBaseComponentText(""));
			Reflections.setField(packet, "c", NMSAPI.getIChatBaseComponentText(prefix));
			Reflections.setField(packet, "d", NMSAPI.getIChatBaseComponentText(suffix));
			Reflections.setField(packet, "e", "always");
			Reflections.setField(packet, "g", reset);
			Reflections.setField(packet, "i", mode);
			return packet;
		}

		public Object remove() {
			Object packet = NMSAPI.getPacketPlayOutScoreboardTeam();
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
			Object packet = NMSAPI.getPacketPlayOutScoreboardTeam();
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