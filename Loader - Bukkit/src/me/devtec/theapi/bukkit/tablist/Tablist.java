package me.devtec.theapi.bukkit.tablist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.devtec.shared.Ref;
import me.devtec.shared.components.Component;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;
import me.devtec.theapi.bukkit.nms.NmsProvider.Action;
import me.devtec.theapi.bukkit.nms.NmsProvider.DisplayType;
import me.devtec.theapi.bukkit.nms.NmsProvider.PlayerInfoType;
import me.devtec.theapi.bukkit.nms.utils.TeamUtils;

public class Tablist implements TabView {
	private static Map<UUID, Tablist> cachedTab = new HashMap<>();

	private final Player player;

	private final List<TabEntry> entries = new ArrayList<>();
	private final Nametag nametag;

	private Object gameProfile;
	private boolean modifiedGameProfile;
	private Optional<Component> playerlistName = Optional.empty();
	private Optional<Component> header = Optional.empty();
	private Optional<Component> footer = Optional.empty();
	private Optional<YellowNumberDisplay> yellowNumberDisplay = Optional.empty();
	private Optional<GameMode> gameMode = Optional.empty();
	private int yellowNumberValue;
	private Optional<Integer> latency = Optional.empty();

	private Tablist(Player player) {
		this.player = player;
		gameProfile = BukkitLoader.getNmsProvider().getGameProfile(player);
		nametag = new Nametag(player, player.getName());
	}

	public Player getPlayer() {
		return player;
	}

	public Nametag getNametag() {
		return nametag;
	}

	public List<TabEntry> entries() {
		return Collections.unmodifiableList(entries);
	}

	public Tablist addEntry(TabEntry entry) {
		entries.add(entry);
		return this;
	}

	public Tablist removeEntry(TabEntry entry) {
		entries.remove(entry);
		return this;
	}

	public Optional<Component> getHeader() {
		return header;
	}

	/**
	 * @apiNote Use {@link #setHeaderAndFooter(Component, Component)} method
	 */
	@Deprecated
	public Tablist setHeader(Component header) {
		this.header = Optional.ofNullable(header);
		BukkitLoader.getPacketHandler().send(player,
				BukkitLoader.getNmsProvider().packetPlayerListHeaderFooter(this.header.orElse(Component.EMPTY_COMPONENT), footer.orElse(Component.EMPTY_COMPONENT)));
		return this;
	}

	public Optional<Component> getFooter() {
		return footer;
	}

	/**
	 * @apiNote Use {@link #setHeaderAndFooter(Component, Component)} method
	 */
	@Deprecated
	public Tablist setFooter(Component footer) {
		this.footer = Optional.ofNullable(footer);
		BukkitLoader.getPacketHandler().send(player,
				BukkitLoader.getNmsProvider().packetPlayerListHeaderFooter(header.orElse(Component.EMPTY_COMPONENT), this.footer.orElse(Component.EMPTY_COMPONENT)));
		return this;
	}

	public Tablist setHeaderAndFooter(Component header, Component footer) {
		this.header = Optional.ofNullable(header);
		this.footer = Optional.ofNullable(footer);
		BukkitLoader.getPacketHandler().send(player, BukkitLoader.getNmsProvider().packetPlayerListHeaderFooter(header, footer));
		return this;
	}

	@Override
	public Optional<Component> getPlayerListName() {
		return playerlistName;
	}

	@Override
	public Tablist setPlayerListName(Component name) {
		playerlistName = Optional.ofNullable(name);
		BukkitLoader.getPacketHandler().send(player, BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_DISPLAY_NAME, getGameProfile(),
				getLatency().orElse(BukkitLoader.getNmsProvider().getPing(getPlayer())), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		return this;
	}

	@Override
	public Tablist setYellowNumber(YellowNumberDisplay type, int value) {
		YellowNumberDisplay previous = yellowNumberDisplay.orElse(null);
		yellowNumberDisplay = Optional.ofNullable(type);
		yellowNumberValue = value;
		// remove yellow number
		if (type == null && previous != null) {
			BukkitLoader.getPacketHandler().send(getPlayer(), BukkitLoader.getNmsProvider().packetScoreboardScore(Action.REMOVE, previous.name().toLowerCase(), getPlayer().getName(), 0));
			BukkitLoader.getPacketHandler().send(getPlayer(), TeamUtils.createObjectivePacket(1, previous.name().toLowerCase(), getPlayer().getName(), DisplayType.INTEGER));
		} else {
			if (type == null)
				return this;
			// remove old yellow number
			if (previous != type && previous != null) {
				BukkitLoader.getPacketHandler().send(getPlayer(), BukkitLoader.getNmsProvider().packetScoreboardScore(Action.REMOVE, previous.name().toLowerCase(), getPlayer().getName(), 0));
				BukkitLoader.getPacketHandler().send(getPlayer(), TeamUtils.createObjectivePacket(1, previous.name().toLowerCase(), getPlayer().getName(), DisplayType.INTEGER));
			}
			// create yellow number
			BukkitLoader.getPacketHandler().send(getPlayer(),
					TeamUtils.createObjectivePacket(0, type.name().toLowerCase(), getPlayer().getName(), type == YellowNumberDisplay.HEARTS ? DisplayType.HEARTS : DisplayType.INTEGER));
			Object packet = BukkitLoader.getNmsProvider().packetScoreboardDisplayObjective(0, null);
			Ref.set(packet, "b", type.name().toLowerCase());
			BukkitLoader.getPacketHandler().send(getPlayer(), packet);
			BukkitLoader.getPacketHandler().send(getPlayer(), BukkitLoader.getNmsProvider().packetScoreboardScore(Action.CHANGE, type.name().toLowerCase(), getPlayer().getName(), yellowNumberValue));
		}
		return this;
	}

	@Override
	public Optional<YellowNumberDisplay> getYellowNumberDisplay() {
		return yellowNumberDisplay;
	}

	@Override
	public int getYellowNumberValue() {
		return yellowNumberValue;
	}

	@Override
	public Optional<GameMode> getGameMode() {
		return gameMode;
	}

	@Override
	public GameProfileHandler getGameProfile() {
		return BukkitLoader.getNmsProvider().fromGameProfile(gameProfile);
	}

	@Override
	public boolean isGameProfileModified() {
		return modifiedGameProfile;
	}

	@Override
	public Optional<Integer> getLatency() {
		return latency;
	}

	@Override
	public Tablist setLatency(Integer latency) {
		this.latency = Optional.ofNullable(latency);
		BukkitLoader.getPacketHandler().send(player, BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_LATENCY, getGameProfile(),
				getLatency().orElse(BukkitLoader.getNmsProvider().getPing(getPlayer())), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		return this;
	}

	@Override
	public Tablist setGameProfile(GameProfileHandler gameProfile) {
		Object previous = this.gameProfile;
		modifiedGameProfile = true;
		this.gameProfile = BukkitLoader.getNmsProvider().toGameProfile(gameProfile);
		BukkitLoader.getPacketHandler().send(player, BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.REMOVE_PLAYER, BukkitLoader.getNmsProvider().fromGameProfile(previous),
				getLatency().orElse(BukkitLoader.getNmsProvider().getPing(getPlayer())), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		BukkitLoader.getPacketHandler().send(player, BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.ADD_PLAYER, gameProfile,
				getLatency().orElse(BukkitLoader.getNmsProvider().getPing(getPlayer())), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		return this;
	}

	@Override
	public Tablist setGameMode(GameMode gameMode) {
		this.gameMode = Optional.ofNullable(gameMode);
		BukkitLoader.getPacketHandler().send(player, BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_GAME_MODE, getGameProfile(),
				getLatency().orElse(BukkitLoader.getNmsProvider().getPing(getPlayer())), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		return this;
	}

	public void remove() {
		cachedTab.remove(player.getUniqueId());
	}

	public TabEntry asEntry(Tablist holder) {
		TabEntry entry = TabEntry.of(player, gameProfile, holder);
		entry.setYellowNumber(entry.getYellowNumberValue());
		entry.getNametag().clone(getNametag()).update();
		return entry;
	}

	public static Tablist of(Player player) {
		Tablist tab = cachedTab.get(player.getUniqueId());
		if (tab == null)
			cachedTab.put(player.getUniqueId(), tab = new Tablist(player));
		return tab;
	}

	public TabEntry getEntryByName(String username) {
		for (TabEntry entry : entries)
			if (entry.getGameProfile().getUsername().equals(username))
				return entry;
		return null;
	}

	public TabEntry getEntryById(UUID id) {
		for (TabEntry entry : entries)
			if (entry.getGameProfile().getUUID().equals(id))
				return entry;
		return null;
	}
}
