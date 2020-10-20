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
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.TheCoder;
import me.DevTec.TheAPI.Utils.DataKeeper.Data;
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
	private static Data data = new Data();
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
			if(!data.exists("sb."+i)) {
				data.set("sb."+i,player.getName());
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
		if(data.exists("sbc."+player)) {
		if(data.getInt("sbc."+player)==id)return;
		else
			if(LoaderClass.plugin.scoreboard.containsKey(data.getInt("sbc."+player)))
			LoaderClass.plugin.scoreboard.get(data.getInt("sbc."+player)).destroy();
		}
		data.set("sbc."+player,id);
		LoaderClass.plugin.scoreboard.put(id,this);
		if(packets) {
		Ref.sendPacket(p,createObjectivePacket(0, name));
		Object packet =NMSAPI.getPacketPlayOutScoreboardDisplayObjective();
		Reflections.setField(packet, "a", 1);
		Reflections.setField(packet, "b", player);
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
		if(data.exists("sbc."+player)) {
		if(data.getInt("sbc."+player)==id)
			data.set("sbc."+player,null);
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
		name=TheAPI.colorize(a);
		if(ScoreboardAPI.a)
		name=name.substring(0, name.length()>32?32:name.length());
		create();
		if(packets) {
			Ref.sendPacket(p,createObjectivePacket(2, name));
		}else
			o.setDisplayName(name.length()>32?name.substring(0, 32):name);
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
		if(!p.isOnline())
		if(TheAPI.getPlayerOrNull(player)!=null)
		p=TheAPI.getPlayer(player);
	}
	
	public void setLine(int line, String value) {
		create();
		value=TheAPI.colorize(value);
		if(getLine(line)!=null&&getLine(line).equals(value))return;
		if(packets) {
			Team team = getTeam(line);
			String old = team.currentPlayer;
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
		String old = team.currentPlayer;
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
		Ref.sendPacket(p,NMSAPI.getPacketPlayOutScoreboardScore(Action.CHANGE, player, team.currentPlayer, line));
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
		Ref.set(packet, "a", player);
		Ref.set(packet, "d", mode);
		if (mode == 0 || mode == 2) {
			Ref.set(packet, "b", !a?NMSAPI.getIChatBaseComponentFromCraftBukkit(displayName):displayName);
			Ref.set(packet, "c", NMSAPI.getEnumScoreboardHealthDisplay(DisplayType.INTEGER));
		}
		return packet;
	}
	
	public static void destroyScoreboard(int id) {
		if(data.exists("sb."+id)) {
			if(LoaderClass.plugin.scoreboard.containsKey(id))
				LoaderClass.plugin.scoreboard.get(id).destroy();
		}
	}

	private static boolean a = !TheAPI.isNewVersion();
	private static Object empty = a?"":NMSAPI.getIChatBaseComponentText("");
	public class Team {
		private final String name;
		private String prefix = "", suffix="",currentPlayer,old;
		private boolean changed, changedPlayer, first = true;

		private Team(int slot) {
			this.name = slot+"";
			currentPlayer=TheCoder.toColor(slot);
		}

		public String getName() {
			return name;
		}

		Object packet = NMSAPI.getPacketPlayOutScoreboardTeam();
		private Object c(int mode) {
			Ref.set(packet, "a", name);
			Ref.set(packet, "b", empty);
			Ref.set(packet, "c", a?prefix:NMSAPI.getIChatBaseComponentText(prefix));
			Ref.set(packet, "d", a?suffix:empty);
			Ref.set(packet, TheAPI.isOlder1_9()?"h":"i", mode);
			return packet;
		}

		Object re = NMSAPI.getPacketPlayOutScoreboardTeam();
		public Object remove() {
			Ref.set(re, "a", name);
			Ref.set(re, TheAPI.isOlder1_9() ? "h" : "i", 1);
			first = true;
			return re;
		}

		public Iterator<Object> sendLine() {
			List<Object> packets = new ArrayList<>();
			if (first)
				packets.add(c(0));
			else if (changed)
				packets.add(c(2));
			if (first || changedPlayer) {
				if(old!=null)
					packets.add(createPlayer(2, old));
				packets.add(createPlayer(3, currentPlayer));
				changedPlayer=false;
				first = false;
			}
			return packets.iterator();
		}

		public void reset() {
			changed = false;
		}

		Object create = NMSAPI.getPacketPlayOutScoreboardTeam();
		@SuppressWarnings("unchecked")
		public Object createPlayer(int mode, String playerName) {
			Ref.set(create, "a", name);
			Ref.set(create, TheAPI.isOlder1_9() ? "h" : "i", mode);
			if(TheAPI.isNewerThan(15))
				((Collection<String>) Ref.get(create, teamlist)).add(playerName);
			else
			((List<String>) Ref.get(create, teamlist)).add(playerName);
			return create;
		}

		public String getValue() {
			return prefix;
		}
		
		private void setPlayer(String a) {
			if(!currentPlayer.equals(a)) {
				old=currentPlayer;
				changedPlayer=true;
			}
			currentPlayer=a;
		}

		private void setValue(String a) {
			if(ScoreboardAPI.a) {
				List<String> d = StringUtils.fixedSplit(a, 16);
				if(a.length()<=16) {
					setPlayer(d.get(0));
					if (!prefix.equals(""))
						changed = true;
					prefix = "";
					if (!suffix.equals(""))
						changed = true;
					suffix = "";
					return;
				}
				if(a.length()<=32) {
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
			}else {
				if (!prefix.equals(a))
					changed = true;
				prefix = a;
			}
		}
	}
}