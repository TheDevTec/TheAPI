package me.devtec.theapi.bukkit.tablist;

import java.util.Optional;

import javax.annotation.Nullable;

import org.bukkit.GameMode;

import me.devtec.shared.components.Component;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;

public interface TabView {

	public TabView setPlayerListName(@Nullable Component name);

	@Nullable
	public Optional<Component> getPlayerListName();

	public TabView setYellowNumber(YellowNumberDisplay type, int value);

	public default TabView setYellowNumber(int value) {
		return setYellowNumber(getYellowNumberDisplay().orElse(null), value);
	}

	@Nullable
	public Optional<YellowNumberDisplay> getYellowNumberDisplay();

	public int getYellowNumberValue();

	public TabView setLatency(@Nullable Integer latency);

	public Optional<Integer> getLatency();

	public TabView setGameProfile(GameProfileHandler gameProfile);

	public GameProfileHandler getGameProfile();

	public TabView setGameMode(@Nullable GameMode gameMode);

	public Optional<GameMode> getGameMode();

	public boolean isGameProfileModified();
}
