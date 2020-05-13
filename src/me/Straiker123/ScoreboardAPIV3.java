package me.Straiker123;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Maps;

public class ScoreboardAPIV3 {
	private boolean created = false;
	private final HashMap<Integer,VirtualTeam> lines = Maps.newHashMap();
	private final org.bukkit.entity.Player player;
	private String objectiveName;

	/**
	 * Create a scoreboard sign for a given player and using a specifig objective name
	 * @param player the player viewing the scoreboard sign
	 * @param objectiveName the name of the scoreboard sign (displayed at the top of the scoreboard)
	 */
	public ScoreboardAPIV3(org.bukkit.entity.Player player, String objectiveName) {
		this.player = player;
		this.objectiveName = objectiveName;
	}

	/**
	 * Send the initial creation packets for this scoreboard sign. Must be called at least once.
	 */
	public void create() {
		if (created)
			return;
		TheAPI.getNMSAPI().sendPacket(player,createObjectivePacket(0, objectiveName));
		TheAPI.getNMSAPI().sendPacket(player,setObjectiveSlot());
		int i = 0;
		while (i < lines.size())
			sendLine(i++);

		created = true;
	}

	/**
	 * Send the packets to remove this scoreboard sign. A destroyed scoreboard sign must be recreated using {@link ScoreboardAPIV3#create()} in order
	 * to be used again
	 */
	public void destroy() {
		if (!created)
			return;

		TheAPI.getNMSAPI().sendPacket(player,createObjectivePacket(1, null));
		for (Integer team : lines.keySet())
			TheAPI.getNMSAPI().sendPacket(player,lines.get(team).removeTeam());

		created = false;
	}

	/**
	 * Change the name of the objective. The name is displayed at the top of the scoreboard.
	 * @param name the name of the objective, max 32 char
	 */
	public void setObjectiveName(String name) {
		this.objectiveName = name;
		if (created)
			TheAPI.getNMSAPI().sendPacket(player,createObjectivePacket(2, name));
	}

	/**
	 * Change a scoreboard line and send the packets to the player. Can be called async.
	 * @param line the number of the line (0 <= line < 15)
	 * @param value the new value for the scoreboard line
	 */
	public void setLine(int line, String value) {
		VirtualTeam team = getOrCreateTeam(line);
		String old = team.getCurrentPlayer();

		if (old != null && created)
			TheAPI.getNMSAPI().sendPacket(player,removeLine(old));

		team.setValue(value);
		sendLine(line);
	}

	/**
	 * Remove a given scoreboard line
	 * @param line the line to remove
	 */
	public void removeLine(int line) {
		VirtualTeam team = getOrCreateTeam(line);
		String old = team.getCurrentPlayer();

		if (old != null && created) {
			TheAPI.getNMSAPI().sendPacket(player,removeLine(old));
			TheAPI.getNMSAPI().sendPacket(player,team.removeTeam());
		}
		lines.remove(line);
	}

	/**
	 * Get the current value for a line
	 * @param line the line
	 * @return the content of the line
	 */
	public String getLine(int line) {
		return getOrCreateTeam(line).getValue();
	}

	/**
	 * Get the team assigned to a line
	 * @return the {@link VirtualTeam} used to display this line
	 */
	public VirtualTeam getTeam(int line) {
		return getOrCreateTeam(line);
	}

	private void sendLine(int line) {
		if (!created)
			return;

		int score = line;
		VirtualTeam val = getOrCreateTeam(line);
		for (Object packet : val.sendLine())
			TheAPI.getNMSAPI().sendPacket(player,packet);
		TheAPI.getNMSAPI().sendPacket(player,sendScore(val.getCurrentPlayer(), score));
		val.reset();
	}

	private VirtualTeam getOrCreateTeam(int line) {
		if (!lines.containsKey(line))
			lines.put(line, new VirtualTeam("Score" + line));

		return lines.get(line);
	}

	/*
		Factories
		 */
	private Object createObjectivePacket(int mode, String displayName) {
		Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardObjective"),new Class[0]),new Object[0]);
		Reflections.setField(packet, "a", player.getName());
		Reflections.setField(packet, "d", mode);
		if (mode == 0 || mode == 2) {
			Reflections.setField(packet, "b", TheAPI.getNMSAPI().getIChatBaseComponentText(displayName));
			Object integer = null;
			try {
				integer= Reflections.getNMSClass("IScoreboardCriteria").getDeclaredClasses()[0].getField("INTEGER").get(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Reflections.setField(packet, "c", integer);
		}
		return packet;
	}

	private Object setObjectiveSlot() {
		Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardDisplayObjective"),new Class[0]),new Object[0]);
		Reflections.setField(packet, "a", 1);
		Reflections.setField(packet, "b", player.getName());
		return packet;
	}

	private Object sendScore(String line, int score) {
		//ScoreboardServer.Action;
		Class<?> c = Reflections.getNMSClass("ScoreboardServer").getDeclaredClasses()[0];
		Object change = null;
		try {
			change = c.getField("CHANGE").get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardScore"),c,String.class,String.class,int.class),
				change,player.getName(), line, score);
		return packet;
	}

	private Object removeLine(String line) {
		Class<?> c = Reflections.getNMSClass("ScoreboardServer").getDeclaredClasses()[0];
		Object change = null;
		try {
			change = c.getField("REMOVE").get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardScore"),c,String.class,String.class,int.class),
				change,player.getName(), line, 0);
		return packet;
	}

	/**
	 * This class is used to manage the content of a line. Advanced users can use it as they want, but they are encouraged to read and understand the
	 * code before doing so. Use these methods at your own risk.
	 */
	public class VirtualTeam {
		private final String name;
		private String prefix;
		private String suffix;
		private String currentPlayer;
		private String oldPlayer;

		private boolean prefixChanged, suffixChanged, playerChanged = false;
		private boolean first = true;

		private VirtualTeam(String name, String prefix, String suffix) {
			this.name = name;
			this.prefix = prefix;
			this.suffix = suffix;
		}

		private VirtualTeam(String name) {
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
			Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"),new Class[0]),new Object[0]);
			Reflections.setField(packet, "a", name);
			Reflections.setField(packet, "b", TheAPI.getNMSAPI().getIChatBaseComponentText(""));
			Reflections.setField(packet, "c", TheAPI.getNMSAPI().getIChatBaseComponentText(prefix));
			Reflections.setField(packet, "d", TheAPI.getNMSAPI().getIChatBaseComponentText(suffix));
			Reflections.setField(packet, "i", 0);
			Reflections.setField(packet, "e", "always");
			try {
				Reflections.setField(packet, "g", Reflections.getNMSClass("EnumChatFormat").getField("RESET").get(null));
			} catch (Exception e) {
				e.printStackTrace();
			}
			Reflections.setField(packet, "i", mode);
			return packet;
		}

		public Object createTeam() {
			return createPacket(0);
		}

		public Object updateTeam() {
			return createPacket(2);
		}

		public Object removeTeam() {
			Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"),new Class[0]),new Object[0]);
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
				packets.add(createTeam());
			} else if (prefixChanged || suffixChanged) {
				packets.add(updateTeam());
			}

			if (first || playerChanged) {
				if (oldPlayer != null)
					packets.add(addOrRemovePlayer(4, oldPlayer));
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
			return addOrRemovePlayer(3, currentPlayer);
		}

		@SuppressWarnings("unchecked")
		public Object addOrRemovePlayer(int mode, String playerName) {
			Object packet = Reflections.c(Reflections.getConstructor(Reflections.getNMSClass("PacketPlayOutScoreboardTeam"),new Class[0]),new Object[0]);
			Reflections.setField(packet, "a", name);
			Reflections.setField(packet, "i", mode);

			try {
				Field f = packet.getClass().getDeclaredField("h");
				f.setAccessible(true);
				((List<String>) f.get(packet)).add(playerName);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}

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
				setSuffix(value.substring(32, value.length() > 48 ? 48 : value.length()));
			}
		}
	}
}