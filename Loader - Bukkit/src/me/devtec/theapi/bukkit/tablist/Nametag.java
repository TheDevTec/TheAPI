package me.devtec.theapi.bukkit.tablist;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.base.Objects;

import me.devtec.shared.Ref;
import me.devtec.shared.components.Component;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.utils.TeamUtils;

public class Nametag {
	@SuppressWarnings("unchecked")
	private static Map<Character, Object> formatMap = (Map<Character, Object>) Ref
			.getNulled(Ref.isNewerThan(11) ? Ref.craft("util.CraftChatMessage") : Ref.craft("util.CraftChatMessage$StringMessage"), "formatMap");

	private static Object getNMSColor(ChatColor color) {
		return color == null ? TeamUtils.white : formatMap.getOrDefault(color.getChar(), TeamUtils.white);
	}

	private final Player player;
	private final String ownerName;

	private Component prefix;
	private Component suffix;
	private String teamName;
	private ChatColor color;

	private boolean created;

	protected Nametag(Player player, String ownerName) {
		this.player = player;
		this.ownerName = ownerName;
		teamName = ownerName;
	}

	public Player getPlayer() {
		return player;
	}

	public Component getPrefix() {
		return prefix;
	}

	public Nametag setPrefix(Component prefix) {
		if (Objects.equal(this.prefix, prefix))
			return this;
		this.prefix = prefix;
		return update();
	}

	public ChatColor getColor() {
		return color;
	}

	public Nametag setColor(ChatColor color) {
		if (Objects.equal(this.color, color))
			return this;
		this.color = color;
		return update();
	}

	public Component getSuffix() {
		return suffix;
	}

	public Nametag setSuffix(Component suffix) {
		if (Objects.equal(this.suffix, suffix))
			return this;
		this.suffix = suffix;
		return update();
	}

	public String getTeamName() {
		return teamName;
	}

	public Nametag setTeamName(String teamName) {
		if (Objects.equal(this.teamName, teamName))
			return this;
		this.teamName = teamName;
		if (created) {
			created = false;
			BukkitLoader.getPacketHandler().send(player, TeamUtils.createTeamPacket(1, TeamUtils.white, prefix, suffix, ownerName, teamName));
		}
		return update();
	}

	public void reset() {
		if (created) {
			created = false;
			BukkitLoader.getPacketHandler().send(player, TeamUtils.createTeamPacket(1, TeamUtils.white, prefix, suffix, ownerName, teamName));
		}
	}

	protected Nametag update() {
		if (!created) {
			created = true;
			BukkitLoader.getPacketHandler().send(player, TeamUtils.createTeamPacket(0, getNMSColor(color), prefix, suffix, ownerName, teamName));
		} else
			BukkitLoader.getPacketHandler().send(player, TeamUtils.createTeamPacket(2, getNMSColor(color), prefix, suffix, ownerName, teamName));
		return this;
	}

	protected Nametag clone(Nametag nametag) {
		teamName = nametag.teamName;
		prefix = nametag.prefix;
		suffix = nametag.suffix;
		color = nametag.color;
		return this;
	}
}
