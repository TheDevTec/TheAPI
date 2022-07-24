package me.devtec.theapi.bukkit.tablist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.devtec.shared.Ref;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.utils.TeamUtils;

public class NameTagAPI {

	@SuppressWarnings("unchecked")
	private static Map<Character, Object> formatMap = (Map<Character, Object>) Ref.getNulled(Ref.isNewerThan(11) ? Ref.craft("util.CraftChatMessage") : Ref.craft("util.CraftChatMessage$StringMessage"), "formatMap");
	private static Map<UUID, List<NameTagAPI>> teams = new ConcurrentHashMap<>();
	private final Player p;
	private List<UUID> canSee = new ArrayList<>();
	private String prefix = "";
	private String suffix = "";
	private String name;
	private ChatColor color;
	private boolean changed;

	private Object getNMSColor(ChatColor color) {
		Object nmsColor = color == null ? TeamUtils.white : NameTagAPI.formatMap.get(color.getChar());
		if (nmsColor == null)
			nmsColor = TeamUtils.white;
		return nmsColor;
	}

	public NameTagAPI(Player player, String teamName) {
		p = player;
		name = teamName.length() > 12 ? teamName.substring(0, 12) : teamName;
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
		name = sort;
		if (name.equals(sort) || canSee.isEmpty())
			return;
		Object created = create(color, prefix, suffix, name);
		Iterator<UUID> uuids = canSee.iterator();
		while (uuids.hasNext()) {
			UUID see = uuids.next();
			Player p = Bukkit.getPlayer(see);
			if (p != null && p.isOnline()) {
				List<NameTagAPI> team = NameTagAPI.teams.get(p.getUniqueId());
				if (team == null)
					NameTagAPI.teams.put(p.getUniqueId(), team = new ArrayList<>());
				if (!contains(team, name)) {
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
			changed = true;
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
			changed = true;
		this.prefix = prefix;
		if (!this.suffix.equals(suffix))
			changed = true;
		this.suffix = suffix;
	}

	public void reset(Player... players) {
		Object reset = resetPacket();
		for (Player player : players)
			if (canSee.remove(player.getUniqueId())) {
				List<NameTagAPI> team = NameTagAPI.teams.get(player.getUniqueId());
				team.remove(this);
				if (!contains(team, name))
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
			if (!team.contains(this) || !canSee.contains(player.getUniqueId())) {
				if (!contains(team, name)) {
					if (created == null)
						created = create(color, prefix, suffix, name);
					BukkitLoader.getPacketHandler().send(player, created);
				} else {
					if (modified == null)
						modified = modify(color, prefix, suffix, name);
					BukkitLoader.getPacketHandler().send(player, modified);
				}
				canSee.add(player.getUniqueId());
				if (!team.contains(this))
					team.add(this);
				continue;
			}
			if (changed) {
				if (modified == null)
					modified = modify(color, prefix, suffix, name);
				BukkitLoader.getPacketHandler().send(player, modified);
			}
		}
		changed = false;
	}

	private Object create(ChatColor color, String prefix, String suffix, String realName) {
		return TeamUtils.createTeamPacket(0, getNMSColor(color), prefix, suffix, p.getName(), realName);
	}

	private Object modify(ChatColor color, String prefix, String suffix, String realName) {
		return TeamUtils.createTeamPacket(2, getNMSColor(color), prefix, suffix, p.getName(), realName);
	}

	private Object resetPacket() {
		return TeamUtils.createTeamPacket(1, getNMSColor(null), "", "", p.getName(), name);
	}
}
