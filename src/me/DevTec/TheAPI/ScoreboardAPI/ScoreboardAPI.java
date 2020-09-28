package me.DevTec.TheAPI.ScoreboardAPI;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.TheCoder;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI.Action;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI.DisplayType;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.Reflections.Reflections;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

/**
 * https://gist.github.com/MrZalTy/f8895d84979d49af946fbcc108b1bf2b
 * @author MrZalTy
 *
 */
public class ScoreboardAPI {
	private static Field teamlist = Reflections.getField(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"), TheAPI.isOlder1_9() ? "g" : "h");
	private Player p;
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
		p=player;
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
		this(player, type==ScoreboardType.PACKETS, type==ScoreboardType.TEAMS);
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
		Ref.sendPacket(p,createObjectivePacket(0, name));
		Object packet =NMSAPI.getPacketPlayOutScoreboardDisplayObjective();
		Reflections.setField(packet, "a", 1);
		Reflections.setField(packet, "b", p.getName());
		Ref.sendPacket(p,packet);
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
			Ref.sendPacket(p,createObjectivePacket(1, null));
		if(LoaderClass.plugin.map.containsKey(id))
			for (int line : LoaderClass.plugin.map.threadSet(id))
		for (Object team : LoaderClass.plugin.map.values(id, line))
			Ref.sendPacket(p,((Team)team).remove());
		}else {
			if(LoaderClass.plugin.map.containsKey(id))
			for (int line : LoaderClass.plugin.map.threadSet(id))
				removeLine(line);
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
			Ref.sendPacket(p,createObjectivePacket(2, name));
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
		p=TheAPI.getPlayer(player);
	}
	
	public void setLine(int line, String value) {
		create();
		value=TheAPI.colorize(value);
		if(getLine(line)!=null&&getLine(line).equals(value))return;
		if(packets) {
			Team team = getTeam(line);
			String old = team.getCurrentPlayer();
		if (old != null)
			Ref.sendPacket(p,NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, "", old, 0));
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
			}
			o.getScore(TheCoder.toColor(line)).setScore(line);
			LoaderClass.plugin.map.put(id, line, team);
			}else {
				String old = LoaderClass.plugin.map.containsThread(id, line) ? (String)LoaderClass.plugin.map.get(id, line) : null;
				if(old!=null)sb.resetScores(old);
				value=TheAPI.isNewVersion() ? (value.length() > 48 ? value.substring(0,48) : value) : (value.length() > 32 ? value.substring(0,32) : value);
				o.getScore(value).setScore(line);
				LoaderClass.plugin.map.put(id,line, value);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void removeLine(int line) {
		create();
		if(!LoaderClass.plugin.map.containsThread(id,line))return;
		if(packets) {
		Team team = getTeam(line);
		String old = team.getCurrentPlayer();
		if (old != null) {
			Ref.sendPacket(p,NMSAPI.getPacketPlayOutScoreboardScore(Action.REMOVE, "", old, 0));
			Ref.sendPacket(p,team.remove());
		}
		}else {
			if(teams) {
			org.bukkit.scoreboard.Team team = sb.getTeam(line+"");
			if(team==null)return;
			team.unregister();
			sb.resetScores(Bukkit.getOfflinePlayer(line+""));
			}else {
				String old = LoaderClass.plugin.map.containsThread(id, line) ? (String)LoaderClass.plugin.map.get(id, line) : null;
				if(old==null)return;
				sb.resetScores(old);
			}
		}
		LoaderClass.plugin.map.remove(id,line);
	}

	public String getLine(int line) {
		if(LoaderClass.plugin.map.containsThread(id,line))
			return packets ? ((Team)LoaderClass.plugin.map.get(id,line)).getValue() : 
				(teams? ((org.bukkit.scoreboard.Team)LoaderClass.plugin.map.get(id,line)).getPrefix():(String)LoaderClass.plugin.map.get(id,line));
		return null;
	}

	public int getLines() {
		return LoaderClass.plugin.map.threadSet(id).size();
	}

	private void sendLine(Team team,int line) {
		for (Iterator<Object> r = team.sendLine(); r.hasNext();)
			Ref.sendPacket(p,r.next());
		Ref.sendPacket(p,NMSAPI.getPacketPlayOutScoreboardScore(Action.CHANGE, p.getName(), team.getCurrentPlayer(), line));
		team.reset();
		LoaderClass.plugin.map.put(id,line, team);
	}

	private Team getTeam(int line) {
		if (!LoaderClass.plugin.map.containsThread(id,line)) {
			LoaderClass.plugin.map.put(id,line, new Team(line));
		}
		return (Team) LoaderClass.plugin.map.get(id,line);
	}
	
	private Object createObjectivePacket(int mode, String displayName) {
		Object packet =NMSAPI.getPacketPlayOutScoreboardObjective();
		Reflections.setField(packet, "a", p.getName());
		Reflections.setField(packet, "d", mode);
		if (mode == 0 || mode == 2) {
			Reflections.setField(packet, "b", TheAPI.isNewVersion()?NMSAPI.getIChatBaseCompomentFromCraftBukkit(displayName):displayName);
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

	private static boolean a = !TheAPI.isNewVersion();
	public class Team {
		private final String name;
		private String prefix = "",suffix = "",currentPlayer,oldPlayer;
		private boolean changed, playerChanged, first = true;

		private Team(int slot) {
			this.name = slot+"";
		}

		public String getName() {
			return name;
		}

		private Object c(int mode) {
			Object packet = NMSAPI.getPacketPlayOutScoreboardTeam();
			Ref.set(packet, "a", name);
			Ref.set(packet, "b", a?"":NMSAPI.getIChatBaseCompomentFromCraftBukkit(""));
			Ref.set(packet, "c", a?prefix:NMSAPI.getIChatBaseCompomentFromCraftBukkit(prefix));
			Ref.set(packet, "d", a?suffix:NMSAPI.getIChatBaseCompomentFromCraftBukkit(suffix));
			Ref.set(packet, TheAPI.isOlder1_9()?"h":"i", mode);
			return packet;
		}

		public Object remove() {
			Object packet = NMSAPI.getPacketPlayOutScoreboardTeam();
			Ref.set(packet, "a", name);
			Ref.set(packet, TheAPI.isOlder1_9() ? "h" : "i", 1);
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
			ArrayList<Object> packets = new ArrayList<>();
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
			Ref.set(packet, "a", name);
			Ref.set(packet, TheAPI.isOlder1_9() ? "h" : "i", mode);
			if(TheAPI.isNewerThan(15))
				((Collection<String>) Ref.get(packet, teamlist)).add(playerName);
			else
			((List<String>) Ref.get(packet, teamlist)).add(playerName);
			return packet;
		}

		public String getCurrentPlayer() {
			return currentPlayer;
		}

		public String getValue() {
			return prefix + currentPlayer + suffix;
		}

		public void setValue(String value) {
			if (value.length() <= 16) {
				if (prefix == null || !prefix.equals(""))
					changed = true;
				prefix = "";
				if (suffix == null || !suffix.equals(""))
					changed = true;
				suffix = "";
				setPlayer(value);
			} else if (value.length() <= 32) {
				if (prefix == null || !prefix.equals(value.substring(0, 16)))
					changed = true;
				prefix = value.substring(0, 16);
				if (suffix == null || !suffix.equals(""))
					changed = true;
				suffix = "";
				setPlayer(value.substring(16));
			} else {
				if (prefix == null || !prefix.equals(value.substring(0, 16)))
					changed = true;
				prefix = value.substring(0, 16);
				if (suffix == null || !suffix.equals(value.length() < 48 ? value.substring(32) : value.substring(32,48)))
					changed = true;
				suffix = value.length() < 48 ? value.substring(32) : value.substring(32,48);
				setPlayer(value.substring(16, 32));
			}
		}
	}}