package me.devtec.theapi.apis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class NameTagAPI {
	public final Player p;

	@SuppressWarnings("unchecked")
	private static Map<Character, Object> formatMap = (Map<Character, Object>)Ref.getNulled(TheAPI.isNewerThan(11)?Ref.craft("util.CraftChatMessage"):Ref.craft("util.CraftChatMessage$StringMessage"), "formatMap");

	private Object getNMSColor(ChatColor color) {
		return color==null?white:formatMap.getOrDefault(color.getChar(), white);
	}

	private List<UUID> canSee = new ArrayList<>();
	private String prefix = "", suffix = "", currentPlayer;
	public String name;
	private ChatColor color;
	private boolean changed;
	public NameTagAPI(Player player, String teamName) {
		this.p=player;
		currentPlayer = player.getName();
		name=teamName.length()>12?teamName.substring(0, 12):teamName;
	}

	public void send(Player...players) {
		for(Player player : players) {
			if (!canSee.contains(player.getUniqueId())) {
				canSee.add(player.getUniqueId());
				Ref.sendPacket(player, create(color, prefix, suffix, currentPlayer, name));
				continue;
			}
			if (changed) {
				Ref.sendPacket(player, modify(color, prefix, suffix, currentPlayer, name));
			}
		}
		changed=false;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public String getTeamName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void set(ChatColor color, String prefix, String suffix) {
		if (this.color!=color)
			changed = true;
		this.color=color;
		if (!TheAPI.isNewVersion()) {
			if (prefix.length() > 16)prefix=prefix.substring(0,15);
			if (!this.prefix.equals(prefix))
				changed = true;
			this.prefix = prefix;
			if (suffix.length() > 16)suffix=suffix.substring(0,15);
			if (!this.suffix.equals(suffix))
				changed = true;
			this.suffix = suffix;
		} else {
			if (!this.prefix.equals(prefix))
				changed = true;
			this.prefix = prefix;
			if (!this.suffix.equals(suffix))
				changed = true;
			this.suffix = suffix;
		}
	}

	private Object create(ChatColor color, String prefix, String suffix, String name, String realName) {
		return c(0, color, prefix, suffix, name, realName);
	}

	private Object modify(ChatColor color, String prefix, String suffix, String name, String realName) {
		return c(2, color, prefix, suffix, name, realName);
	}
	
	public void reset(Player...players) {
		for(Player player : players)
			canSee.remove(player.getUniqueId());
		Ref.sendPacket(players, resetPacket());
	}

	private Object resetPacket() {
		return c(1, null, "", "", currentPlayer, name);
	}

	private static final Class<?> sbTeam = Ref.getClass("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
	private static final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));
	private static final Object white = Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a",char.class)==null?
			Ref.invokeStatic(Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a",int.class), -1):
			Ref.invokeStatic(Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a",char.class), 'f');

	private Object c(int mode, ChatColor color, String prefix, String suffix, String name, String realName) {
		Object packet = LoaderClass.nmsProvider.packetScoreboardTeam();
		String always = "ALWAYS";
		if(TheAPI.isNewerThan(16)) {
			Ref.set(packet, "i", realName);
			try {
				Object o = unsafe.allocateInstance(sbTeam);
				Ref.set(o, "a", LoaderClass.nmsProvider.chatBase("{\"text\":\""+name+"\"}"));
				Ref.set(o, "b", ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(prefix, true)));
				Ref.set(o, "c", ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(suffix, true)));
				Ref.set(o, "d", always);
				Ref.set(o, "e", always);
				Ref.set(o, "f", color==null?white:getNMSColor(color));
				Ref.set(packet, "k", Optional.of(o));
			} catch (Exception e) {
			}
			Ref.set(packet, "h", mode);
			Ref.set(packet, "j", ImmutableList.copyOf(new String[]{name}));
		}else {
			Ref.set(packet, "a", realName);
			Ref.set(packet, "b", TheAPI.isNewerThan(12)?LoaderClass.nmsProvider.chatBase("{\"text\":\"\"}"):"");
			Ref.set(packet, "c", TheAPI.isNewerThan(12)?ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(prefix, true)):prefix);
			Ref.set(packet, "d", TheAPI.isNewerThan(12)?ComponentAPI.toIChatBaseComponent(ComponentAPI.toComponent(suffix, true)):suffix);
			if(TheAPI.isNewerThan(7)) {
				Ref.set(packet, "e", always);
				Ref.set(packet, "f", TheAPI.isNewerThan(8)? always : -1);
				if(TheAPI.isNewerThan(8))
					Ref.set(packet, "g",TheAPI.isNewerThan(12)?(color==null?white:getNMSColor(color)):(color==null?-1:color.ordinal()));
				Ref.set(packet, TheAPI.isNewerThan(8)?"i":"h", mode);
				Ref.set(packet, TheAPI.isNewerThan(8)?"h":"g", ImmutableList.copyOf(new String[]{name}));
			}else {
				Ref.set(packet, "f", mode);
				Ref.set(packet, "e", ImmutableList.copyOf(new String[]{name}));
			}
		}
		return packet;
	}

	public void setName(String sort) {
		if(name.equals(sort))return;
		name=sort;
		changed=true;
	}
}
