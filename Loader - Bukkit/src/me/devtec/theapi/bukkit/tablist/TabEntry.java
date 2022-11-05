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

	private GameProfileHandler gameProfile;
	private Optional<Component> playerlistName;
	private Optional<GameMode> gameMode;
	private int yellowNumberValue;
	private Optional<Integer> latency;

	public static TabEntry of(GameProfileHandler gameProfile, Tablist tablistHolder) {
		return new TabEntry(null, gameProfile, tablistHolder);
	}

	public static TabEntry of(@Nullable Player tagOwner, GameProfileHandler gameProfile, Tablist tablistHolder) {
		return new TabEntry(tagOwner, gameProfile, tablistHolder);
	}

	protected TabEntry(@Nullable Player tagOwner, GameProfileHandler gameProfile, Tablist tablist) {
		this.tablist = tablist;
		yellowNumberValue = tablist.getYellowNumberValue();
		gameMode = tablist.getGameMode();
		this.gameProfile = gameProfile;
		playerlistName = Optional.of(new Component(gameProfile.getUsername()));
		nametag = new Nametag(tablist.getPlayer(), gameProfile.getUsername());
		if (tagOwner == null)
			BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.ADD_PLAYER, getGameProfile(), getLatency().orElse(null),
					getGameMode().orElse(null), getPlayerListName().orElse(new Component(getGameProfile().getUsername()))));
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
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_DISPLAY_NAME, getGameProfile(), getLatency().orElse(null),
				getGameMode().orElse(null), getPlayerListName().orElse(new Component(getGameProfile().getUsername()))));
		return this;
	}

	@Override
	public TabEntry setYellowNumber(YellowNumberDisplay display, int value) {
		yellowNumberValue = value;
		if (getTablist().getYellowNumberDisplay() == null)
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
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_GAME_MODE, getGameProfile(), getLatency().orElse(null),
				getGameMode().orElse(null), getPlayerListName().orElse(new Component(getGameProfile().getUsername()))));
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
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_LATENCY, getGameProfile(), getLatency().orElse(null),
				getGameMode().orElse(null), getPlayerListName().orElse(new Component(getGameProfile().getUsername()))));
		return this;
	}

	@Override
	public GameProfileHandler getGameProfile() {
		return gameProfile;
	}

	@Override
	public TabView setGameProfile(GameProfileHandler gameProfile) {
		GameProfileHandler previous = gameProfile;
		this.gameProfile = gameProfile;
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.REMOVE_PLAYER, previous, getLatency().orElse(null),
				getGameMode().orElse(null), getPlayerListName().orElse(new Component(getGameProfile().getUsername()))));
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.ADD_PLAYER, getGameProfile(), getLatency().orElse(null),
				getGameMode().orElse(null), getPlayerListName().orElse(new Component(getGameProfile().getUsername()))));
		return this;
	}

	public void remove() {
		BukkitLoader.getPacketHandler().send(getTablist().getPlayer(), BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.REMOVE_PLAYER, getGameProfile(), getLatency().orElse(null),
				getGameMode().orElse(null), getPlayerListName().orElse(new Component(getGameProfile().getUsername()))));
	}

}
