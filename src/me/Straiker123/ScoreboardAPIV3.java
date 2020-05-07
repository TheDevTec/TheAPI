package me.Straiker123;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import me.Straiker123.Reflections;
import me.Straiker123.TheAPI;
public class ScoreboardAPIV3 {
private boolean created = false;
private static Constructor<?> c = Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"), new Class[0]),
d=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"), new Class[0]),
e=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardObjective"),new Class[0]),
g=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"), new Class[0]),
v=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardDisplayObjective"),new Class[0]),
b=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardScore"), String.class),
a=Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardScore"), String.class);
private static Object integer,change;
static{
	try {
	integer=Reflections.getNMSClass("IScoreboardCriteria").getDeclaredClasses()[0].getField("INTEGER").get(null);
	change=Reflections.getNMSClass("PacketPlayOutScoreboardScore").getDeclaredClasses()[0].getField("CHANGE").get(null);
	}catch(Exception e) {
		
	}
}
private final HashMap<Integer,TheTeam> lines = new HashMap<Integer,TheTeam>();
private final Player player;
private String objectiveName;


public ScoreboardAPIV3(Player player) {
	this(player,"a");
}

public ScoreboardAPIV3(Player player, String objectiveName) {
	this.player = player;
	this.objectiveName = objectiveName;
}

public void delete() {
	destroy();
}

public void remove() {
	destroy();
}

public void destroy() {
	if (!created)return;
	TheAPI.getNMSAPI().sendPacket(player, createObjectivePacket(1, null));
	for (Integer team : lines.keySet())
			TheAPI.getNMSAPI().sendPacket(player, lines.get(team).removeTeam());
	created = false;
}

public void setName(String name) {
	setDisplayName(name);	
}

public void setDisplayName(String name) {
	if(name==null||name.equals(""))return;
	this.objectiveName = name;
	if (!created || created && player.getScoreboard().getObjectives().isEmpty() && player.getScoreboard().getEntries().isEmpty()) {
	TheAPI.getNMSAPI().sendPacket(player, createObjectivePacket(0, objectiveName));
	Object packet = Reflections.c(v,new Object[0]);
	Reflections.setField(packet, "a", 1);
	Reflections.setField(packet, "b", player.getName());
	TheAPI.getNMSAPI().sendPacket(player, packet);
	int i = 0;
	while (i < lines.size())sendLine(i++);
	created = true;
	}
	TheAPI.getNMSAPI().sendPacket(player, createObjectivePacket(2, name));
}

public void addLine(String value) {
	for(int i = 0; i > -1; ++i) {
		if(!lines.containsKey(i)) {
			setLine(i,value);
			break;
		}
	}
}

public void updateLine(int line, String value) {
	setLine(line,value);
}

public void setLine(int line, String value) {
	if (!created || created && player.getScoreboard().getObjectives().isEmpty() && player.getScoreboard().getEntries().isEmpty())
		setDisplayName(objectiveName);
	TheTeam team = getOrCreateTeam(line);
	String old = team.getTeam();
	if (old != null && created)
		TheAPI.getNMSAPI().sendPacket(player, Reflections.c(a,old));
	team.setValue(value);
	sendLine(line);
}

public void deleteLine(int line) {
	removeLine(line);
}

public void deleteLine(String value) {
	removeLine(getLineByValue(value));
}

public int getLineByValue(String value) {
	int find = -1;
	for(Integer a : lines.keySet()) {
		if(lines.get(a).getValue().equals(value)) {
			find=a;
			break;
		}
	}
	return find;
}

public void removeLine(int line) {
	TheTeam team = getOrCreateTeam(line);
	String old = team.getTeam();
	if (old != null && created) {
		TheAPI.getNMSAPI().sendPacket(player, Reflections.c(a,old));
		TheAPI.getNMSAPI().sendPacket(player, team.removeTeam());
	}
	lines.remove(line);
}

public String getLine(int line) {
	return getOrCreateTeam(line).getValue();
}

public TheTeam getTeam(int line) {
	return getOrCreateTeam(line);
}


private void sendLine(int line) {
	if (!created)return;
	TheTeam team = getOrCreateTeam(line);
	for (Object packet : team.sendLine())
		TheAPI.getNMSAPI().sendPacket(player, packet);
	Object packet = Reflections.c(b,line);
	Reflections.setField(packet, "b", player.getName());
	Reflections.setField(packet, "c", line);
	Reflections.setField(packet, "d", change);
	TheAPI.getNMSAPI().sendPacket(player, packet);
	team.reset();
}

private TheTeam getOrCreateTeam(int line) {
	if (!lines.containsKey(line))
		lines.put(line,new TheTeam("score" + line));
	return lines.get(line);
}

private Object createObjectivePacket(int mode, String displayName) {
	Object packet = Reflections.c(e,new Object[0]);
	Reflections.setField(packet, "a", player.getName());
	Reflections.setField(packet, "d", mode);
	if (mode == 0 || mode == 2) {
		Reflections.setField(packet, "b", displayName);
		Reflections.setField(packet, "c", integer);
	}
	return packet;
}

public class TheTeam {
	private final String name;
	private String prefix;
	private String suffix;
	private String currentPlayer;
	private String oldPlayer;

	private boolean prefixChanged, suffixChanged, playerChanged = false;
	private boolean first = true;

	private TheTeam(String name, String prefix, String suffix) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	private TheTeam(String name) {
		this(name, "", "");
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		if (this.prefix == null || !this.prefix.equals(prefix))
			this.prefixChanged = true;
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		if (this.suffix == null || !this.suffix.equals(prefix))
			this.suffixChanged = true;
		this.suffix = suffix;
	}

	private Object createPacket(int mode) {
		Object packet = Reflections.c(g,new Object[0]);
		Reflections.setField(packet, "a", name);
		Reflections.setField(packet, "b", "");
		Reflections.setField(packet, "c", prefix);
		Reflections.setField(packet, "d", suffix);
		Reflections.setField(packet, "i", 0);
		Reflections.setField(packet, "e", "always");
		Reflections.setField(packet, "g", 0);
		Reflections.setField(packet, "i", mode);
		return packet;
	}

	public Object removeTeam() {
		Object packet = Reflections.c(c,new Object[0]);
		Reflections.setField(packet, "a", name);
		Reflections.setField(packet, "i", 1);
		first = true;
		return packet;
	}

	public void setPlayer(String name) {
		if (this.currentPlayer == null || !this.currentPlayer.equals(name))
			this.playerChanged = true;
		this.oldPlayer = this.currentPlayer;
		this.currentPlayer = name;
	}

	public Iterable<Object> sendLine() {
		List<Object> packets = new ArrayList<>();
		if (first) {
			packets.add(createPacket(0));
		} else if (prefixChanged || suffixChanged) {
			packets.add(createPacket(2));
		}
		if (first || playerChanged) {
			if (oldPlayer != null)
				packets.add(createTeam(4, oldPlayer));
			packets.add(changePlayer());
		}
		if (first)
			first = false;
		return packets;
	}

	public void reset() {
		prefixChanged = false;
		suffixChanged = false;
		playerChanged = false;
		oldPlayer = null;
	}

	public Object changePlayer() {
		return createTeam(3, currentPlayer);
	}

	@SuppressWarnings("unchecked")
	public Object createTeam(int mode, String playerName) {
		Object packet = Reflections.c(d,new Object[0]);
		Reflections.setField(packet, "a", name);
		Reflections.setField(packet, "i", mode);

		try {
			Field f = packet.getClass().getDeclaredField("h");
			f.setAccessible(true);
			((List<String>) f.get(packet)).add(playerName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return packet;
	}

	public String getTeam() {
		return currentPlayer;
	}

	public String getValue() {
		return getPrefix() + getTeam() + getSuffix();
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
			setSuffix(value.substring(32,value.length() > 48 ? 48 : value.length()));
		}
	}
}}