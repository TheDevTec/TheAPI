package me.devtec.theapi.bukkit.tablist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import me.devtec.shared.Ref;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.theapi.bukkit.BukkitLoader;

public class NameTagAPI {
	@SuppressWarnings("unchecked")
	private static Map<Character, Object> formatMap = (Map<Character, Object>)Ref.getNulled(Ref.isNewerThan(11)?Ref.craft("util.CraftChatMessage"):Ref.craft("util.CraftChatMessage$StringMessage"), "formatMap");
	private static Map<UUID, List<NameTagAPI>> teams = new HashMap<>();
	private final Player p;
	private List<UUID> canSee = new ArrayList<>();
	private String prefix = "";
	private String suffix = "";
	private String name;
	private ChatColor color;
	private boolean changed;
	
	private Object getNMSColor(ChatColor color) {
		return color==null?white:formatMap.getOrDefault(color.getChar(), white);
	}
	
	public NameTagAPI(Player player, String teamName) {
		this.p=player;
		name=teamName.length()>12?teamName.substring(0, 12):teamName;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public String getTeamName() {
		return name;
	}
	
	public ChatColor getColor() {
		return color;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}
	
	public List<UUID> getPlayers() {
		return canSee;
	}
	
	public void setName(String sort) {
		name=sort;
		if(name.equals(sort)||canSee.isEmpty())return;
		Object created = create(color, prefix, suffix, name);
		Iterator<UUID> uuids = canSee.iterator();
		while(uuids.hasNext()) {
			UUID see = uuids.next();
			Player p = Bukkit.getPlayer(see);
			if(p!=null && p.isOnline()) {
				List<NameTagAPI> team = teams.get(p.getUniqueId());
				if(team==null)teams.put(p.getUniqueId(), team=new ArrayList<>());
				if(!contains(team, name)) {
					BukkitLoader.getPacketHandler().send(p, created);
					team.add(this);
				}
			}else {
				uuids.remove();
			}
		}
	}

	private boolean contains(List<NameTagAPI> team, String prev) {
		for(NameTagAPI t : team) {
			if(t.p.isOnline() && t.name.equals(prev))return true;
		}
		return false;
	}

	public void set(ChatColor color, String prefixText, String suffixText) {
		if (this.color!=color)
			changed = true;
		this.color=color;
		String prefix;
		String suffix;
		if (Ref.isOlderThan(13)) {
			if (prefixText.length() > 16)prefix=prefixText.substring(0,15);
			else prefix = prefixText;
			if (suffixText.length() > 16)suffix=suffixText.substring(0,15);
			else suffix=suffixText;
		}else {
			prefix=prefixText;
			suffix=suffixText;
		}
		if (!this.prefix.equals(prefix))
			changed = true;
		this.prefix = prefix;
		if (!this.suffix.equals(suffix))
			changed = true;
		this.suffix = suffix;
	}
	
	public void reset(Player...players) {
		Object reset = resetPacket();
		for(Player player : players) {
			if(canSee.remove(player.getUniqueId())) {
				List<NameTagAPI> team = teams.get(player.getUniqueId());
				team.remove(this);
				if(!contains(team, name))
					BukkitLoader.getPacketHandler().send(player, reset);
			}
		}
	}
	
	public void send(Player...players) {
		Object created = null;
		Object modified = null;
		for(Player player : players) {
			List<NameTagAPI> team = teams.get(player.getUniqueId());
			if(team==null)teams.put(player.getUniqueId(), team=new ArrayList<>());
			if (!team.contains(this) || !canSee.contains(player.getUniqueId())) {
				if(!contains(team,name)) {
					if(created==null)created=create(color, prefix, suffix, name);
					BukkitLoader.getPacketHandler().send(player, created);
				}else {
					if(modified==null)modified=modify(color, prefix, suffix, name);
					BukkitLoader.getPacketHandler().send(player, modified);
				}
				canSee.add(player.getUniqueId());
				if(!team.contains(this))
					team.add(this);
				continue;
			}
			if (changed) {
				if(modified==null)modified=modify(color, prefix, suffix, name);
				BukkitLoader.getPacketHandler().send(player, modified);
			}
		}
		changed=false;
	}
	
	private Object create(ChatColor color, String prefix, String suffix, String realName) {
		return c(0, color, prefix, suffix, p.getName(), realName);
	}

	private Object modify(ChatColor color, String prefix, String suffix, String realName) {
		return c(2, color, prefix, suffix, p.getName(), realName);
	}
	
	private Object resetPacket() {
		return c(1, null, "", "", p.getName(), name);
	}
	
	private static final Class<?> sbTeam = Ref.getClass("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
	private static final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));
	private static final Object white = Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a",char.class)==null?
			Ref.invokeStatic(Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a",int.class), -1):
			Ref.invokeStatic(Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a",char.class), 'f');
	
	private Object c(int mode, ChatColor color, String prefix, String suffix, String name, String realName) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
		String always = "ALWAYS";
		if(Ref.isNewerThan(16)) {
			Ref.set(packet, "i", realName);
			try {
				Object o = unsafe.allocateInstance(sbTeam);
				Ref.set(o, "a", BukkitLoader.getNmsProvider().chatBase("{\"text\":\""+name+"\"}"));
				Ref.set(o, "b", BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.toComponent(prefix, true)));
				Ref.set(o, "c", BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.toComponent(suffix, true)));
				Ref.set(o, "d", always);
				Ref.set(o, "e", always);
				Ref.set(o, "f", color==null?white:getNMSColor(color));
				Ref.set(packet, "k", Optional.of(o));
			} catch (Exception e) {
			}
			Ref.set(packet, "h", mode);
			Ref.set(packet, "j", ImmutableList.copyOf(new String[] {name}));
		}else {
			Ref.set(packet, "a", realName);
			Ref.set(packet, "b", Ref.isNewerThan(12)?BukkitLoader.getNmsProvider().chatBase("{\"text\":\""+name+"\"}"):"");
			Ref.set(packet, "c", Ref.isNewerThan(12)?BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.toComponent(prefix, true)):prefix);
			Ref.set(packet, "d", Ref.isNewerThan(12)?BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.toComponent(suffix, true)):suffix);
			if(Ref.isNewerThan(7)) {
				Ref.set(packet, "e", always);
				Ref.set(packet, "f", Ref.isNewerThan(8) ? always : -1);
				if(Ref.isNewerThan(8))
					Ref.set(packet, "g",Ref.isNewerThan(12)?(color==null?white:getNMSColor(color)):(color==null?-1:color.ordinal()));
				Ref.set(packet, Ref.isNewerThan(8)?"i":"h", mode);
				Ref.set(packet, Ref.isNewerThan(8)?"h":"g", ImmutableList.copyOf(new String[] {name}));
			}else {
				Ref.set(packet, "f", mode);
				Ref.set(packet, "e", ImmutableList.copyOf(new String[] {name}));
			}
		}
		return packet;
	}
}
