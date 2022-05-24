package me.devtec.theapi.bukkit.tablist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import me.devtec.shared.Ref;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.theapi.bukkit.BukkitLoader;

public class NameTagAPI {

	private static final Class<?> sbTeam = Ref
			.getClass("net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam$b");
	private static final sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref
			.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));
	private static final Object white = Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a",
			char.class) == null
					? Ref.invokeStatic(Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a", int.class), -1)
					: Ref.invokeStatic(Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a", char.class),
							'f');

	@SuppressWarnings("unchecked")
	private static Map<Character, Object> formatMap = (Map<Character, Object>) Ref.getNulled(
			Ref.isNewerThan(11) ? Ref.craft("util.CraftChatMessage") : Ref.craft("util.CraftChatMessage$StringMessage"),
			"formatMap");
	private static Map<UUID, List<NameTagAPI>> teams = new ConcurrentHashMap<>();
	private final Player p;
	private List<UUID> canSee = new ArrayList<>();
	private String prefix = "";
	private String suffix = "";
	private String name;
	private ChatColor color;
	private boolean changed;

	private Object getNMSColor(ChatColor color) {
		return color == null ? NameTagAPI.white : NameTagAPI.formatMap.getOrDefault(color.getChar(), NameTagAPI.white);
	}

	public NameTagAPI(Player player, String teamName) {
		this.p = player;
		this.name = teamName.length() > 12 ? teamName.substring(0, 12) : teamName;
	}

	public Player getPlayer() {
		return this.p;
	}

	public String getTeamName() {
		return this.name;
	}

	public ChatColor getColor() {
		return this.color;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getSuffix() {
		return this.suffix;
	}

	public List<UUID> getPlayers() {
		return this.canSee;
	}

	public void setName(String sort) {
		this.name = sort;
		if (this.name.equals(sort) || this.canSee.isEmpty())
			return;
		Object created = this.create(this.color, this.prefix, this.suffix, this.name);
		Iterator<UUID> uuids = this.canSee.iterator();
		while (uuids.hasNext()) {
			UUID see = uuids.next();
			Player p = Bukkit.getPlayer(see);
			if (p != null && p.isOnline()) {
				List<NameTagAPI> team = NameTagAPI.teams.get(p.getUniqueId());
				if (team == null)
					NameTagAPI.teams.put(p.getUniqueId(), team = new ArrayList<>());
				if (!this.contains(team, this.name)) {
					BukkitLoader.getPacketHandler().send(p, created);
					team.add(this);
				}
			} else
				uuids.remove();
		}
	}

	private boolean contains(List<NameTagAPI> team, String prev) {
		for (NameTagAPI t : team)
			if (t.p.isOnline() && t.name.equals(prev))
				return true;
		return false;
	}

	public void set(ChatColor color, String prefixText, String suffixText) {
		if (this.color != color)
			this.changed = true;
		this.color = color;
		String prefix;
		String suffix;
		if (Ref.isOlderThan(13)) {
			if (prefixText.length() > 16)
				prefix = prefixText.substring(0, 15);
			else
				prefix = prefixText;
			if (suffixText.length() > 16)
				suffix = suffixText.substring(0, 15);
			else
				suffix = suffixText;
		} else {
			prefix = prefixText;
			suffix = suffixText;
		}
		if (!this.prefix.equals(prefix))
			this.changed = true;
		this.prefix = prefix;
		if (!this.suffix.equals(suffix))
			this.changed = true;
		this.suffix = suffix;
	}

	public void reset(Player... players) {
		Object reset = this.resetPacket();
		for (Player player : players)
			if (this.canSee.remove(player.getUniqueId())) {
				List<NameTagAPI> team = NameTagAPI.teams.get(player.getUniqueId());
				team.remove(this);
				if (!this.contains(team, this.name))
					BukkitLoader.getPacketHandler().send(player, reset);
			}
	}

	public void send(Player... players) {
		Object created = null;
		Object modified = null;
		for (Player player : players) {
			List<NameTagAPI> team = NameTagAPI.teams.get(player.getUniqueId());
			if (team == null)
				NameTagAPI.teams.put(player.getUniqueId(), team = new ArrayList<>());
			if (!team.contains(this) || !this.canSee.contains(player.getUniqueId())) {
				if (!this.contains(team, this.name)) {
					if (created == null)
						created = this.create(this.color, this.prefix, this.suffix, this.name);
					BukkitLoader.getPacketHandler().send(player, created);
				} else {
					if (modified == null)
						modified = this.modify(this.color, this.prefix, this.suffix, this.name);
					BukkitLoader.getPacketHandler().send(player, modified);
				}
				this.canSee.add(player.getUniqueId());
				if (!team.contains(this))
					team.add(this);
				continue;
			}
			if (this.changed) {
				if (modified == null)
					modified = this.modify(this.color, this.prefix, this.suffix, this.name);
				BukkitLoader.getPacketHandler().send(player, modified);
			}
		}
		this.changed = false;
	}

	private Object create(ChatColor color, String prefix, String suffix, String realName) {
		return this.createPacket(0, color, prefix, suffix, this.p.getName(), realName);
	}

	private Object modify(ChatColor color, String prefix, String suffix, String realName) {
		return this.createPacket(2, color, prefix, suffix, this.p.getName(), realName);
	}

	private Object resetPacket() {
		return this.createPacket(1, null, "", "", this.p.getName(), this.name);
	}

	private Object createPacket(int mode, ChatColor color, String prefix, String suffix, String name, String realName) {
		Object packet = BukkitLoader.getNmsProvider().packetScoreboardTeam();
		String always = "ALWAYS";
		if (Ref.isNewerThan(16)) {
			Ref.set(packet, "i", realName);
			try {
				Object o = NameTagAPI.unsafe.allocateInstance(NameTagAPI.sbTeam);
				Ref.set(o, "a", BukkitLoader.getNmsProvider().chatBase("{\"text\":\"" + name + "\"}"));
				Ref.set(o, "b", BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(prefix)));
				Ref.set(o, "c", BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(suffix)));
				Ref.set(o, "d", always);
				Ref.set(o, "e", always);
				Ref.set(o, "f", color == null ? NameTagAPI.white : this.getNMSColor(color));
				Ref.set(packet, "k", Optional.of(o));
			} catch (Exception e) {
			}
			Ref.set(packet, "h", mode);
			Ref.set(packet, "j", ImmutableList.copyOf(new String[] { name }));
		} else {
			Ref.set(packet, "a", realName);
			Ref.set(packet, "b",
					Ref.isNewerThan(12) ? BukkitLoader.getNmsProvider().chatBase("{\"text\":\"" + name + "\"}") : "");
			Ref.set(packet, "c",
					Ref.isNewerThan(12)
							? BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(prefix))
							: prefix);
			Ref.set(packet, "d",
					Ref.isNewerThan(12)
							? BukkitLoader.getNmsProvider().toIChatBaseComponent(ComponentAPI.fromString(suffix))
							: suffix);
			if (Ref.isNewerThan(7)) {
				Ref.set(packet, "e", always);
				Ref.set(packet, "f", Ref.isNewerThan(8) ? always : -1);
				if (Ref.isNewerThan(8))
					Ref.set(packet, "g",
							Ref.isNewerThan(12) ? color == null ? NameTagAPI.white : this.getNMSColor(color)
									: color == null ? -1 : color.ordinal());
				Ref.set(packet, Ref.isNewerThan(8) ? "i" : "h", mode);
				Ref.set(packet, Ref.isNewerThan(8) ? "h" : "g", ImmutableList.copyOf(new String[] { name }));
			} else {
				Ref.set(packet, "f", mode);
				Ref.set(packet, "e", ImmutableList.copyOf(new String[] { name }));
			}
		}
		return packet;
	}
}
