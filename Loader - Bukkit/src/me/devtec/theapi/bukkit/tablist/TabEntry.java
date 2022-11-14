package me.devtec.theapi.bukkit.tablist;

import java.util.Optional;

import javax.annotation.Nullable;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.devtec.shared.components.Component;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;
import me.devtec.theapi.bukkit.nms.NmsProvider.Action;
import me.devtec.theapi.bukkit.nms.NmsProvider.PlayerInfoType;

public class TabEntry implements TabView {

	private final Tablist tablist;
	private final Nametag nametag;
	private Optional<Player> tagOwner;

	private Object gameProfile;
	private boolean modifiedGameProfile;
	private Optional<Component> playerlistName = Optional.empty();
	private Optional<GameMode> gameMode = Optional.empty();
	private int yellowNumberValue;
	private Optional<Integer> latency = Optional.empty();

	public static TabEntry of(GameProfileHandler gameProfile, Tablist tablistHolder) {
		return new TabEntry(null, gameProfile, tablistHolder);
	}

	public static TabEntry of(@Nullable Player tagOwner, Object gameProfile, Tablist tablistHolder) {
		return new TabEntry(tagOwner, gameProfile, tablistHolder);
	}

	protected TabEntry(@Nullable Player tagOwner, Object gameProfile, Tablist tablist) {
		this.tablist = tablist;
		this.tagOwner = Optional.ofNullable(tagOwner);
		this.gameProfile = gameProfile;
		nametag = new Nametag(tablist.getPlayer(), BukkitLoader.getNmsProvider().fromGameProfile(gameProfile).getUsername());
		if (tagOwner == null) {
			playerlistName = Optional.of(new Component(BukkitLoader.getNmsProvider().fromGameProfile(gameProfile).getUsername()));
			gameMode = tablist.getGameMode();
			BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.ADD_PLAYER, getGameProfile(),
					getLatency().orElse(getPlayer().isPresent() ? BukkitLoader.getNmsProvider().getPing(getPlayer().get()) : 0), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		}
	}

	public Optional<Player> getPlayer() {
		return tagOwner;
	}

	public Tablist getTablist() {
		return tablist;
	}

	public Nametag getNametag() {
		return nametag;
	}

	@Override
	public Optional<Component> getPlayerListName() {
		return playerlistName;
	}

	@Override
	public TabEntry setPlayerListName(Component name) {
		playerlistName = Optional.ofNullable(name);
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_DISPLAY_NAME, getGameProfile(),
				getLatency().orElse(getPlayer().isPresent() ? BukkitLoader.getNmsProvider().getPing(getPlayer().get()) : 0), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		return this;
	}

	@Override
	public TabEntry setYellowNumber(YellowNumberDisplay display, int value) {
		yellowNumberValue = value;
		if (!getTablist().getYellowNumberDisplay().isPresent())
			return this;
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetScoreboardScore(Action.CHANGE,
				getTablist().getYellowNumberDisplay().get().name().toLowerCase(), getGameProfile().getUsername(), yellowNumberValue));
		return this;
	}

	@Override
	public Optional<YellowNumberDisplay> getYellowNumberDisplay() {
		return getTablist().getYellowNumberDisplay();
	}

	@Override
	public int getYellowNumberValue() {
		return yellowNumberValue;
	}

	@Override
	public TabEntry setGameMode(GameMode gameMode) {
		this.gameMode = Optional.ofNullable(gameMode);
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_GAME_MODE, getGameProfile(),
				getLatency().orElse(getPlayer().isPresent() ? BukkitLoader.getNmsProvider().getPing(getPlayer().get()) : 0), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		return this;
	}

	@Override
	public Optional<GameMode> getGameMode() {
		return gameMode;
	}

	@Override
	public Optional<Integer> getLatency() {
		return latency;
	}

	@Override
	public TabEntry setLatency(Integer latency) {
		this.latency = Optional.ofNullable(latency);
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_LATENCY, getGameProfile(),
				getLatency().orElse(getPlayer().isPresent() ? BukkitLoader.getNmsProvider().getPing(getPlayer().get()) : 0), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		return this;
	}

	@Override
	public GameProfileHandler getGameProfile() {
		return BukkitLoader.getNmsProvider().fromGameProfile(gameProfile);
	}

	@Override
	public TabView setGameProfile(GameProfileHandler gameProfile) {
		Object previous = this.gameProfile;
		modifiedGameProfile = true;
		this.gameProfile = BukkitLoader.getNmsProvider().toGameProfile(gameProfile);
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(),
				BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.REMOVE_PLAYER, BukkitLoader.getNmsProvider().fromGameProfile(previous),
						getLatency().orElse(getPlayer().isPresent() ? BukkitLoader.getNmsProvider().getPing(getPlayer().get()) : 0), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.ADD_PLAYER, gameProfile,
				getLatency().orElse(getPlayer().isPresent() ? BukkitLoader.getNmsProvider().getPing(getPlayer().get()) : 0), getGameMode().orElse(null), getPlayerListName().orElse(null)));
		return this;
	}

	public void remove() {
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.REMOVE_PLAYER, getGameProfile(),
				getLatency().orElse(getPlayer().isPresent() ? BukkitLoader.getNmsProvider().getPing(getPlayer().get()) : 0), getGameMode().orElse(null), getPlayerListName().orElse(null)));
	}

	@Override
	public boolean isGameProfileModified() {
		return modifiedGameProfile;
	}

}
