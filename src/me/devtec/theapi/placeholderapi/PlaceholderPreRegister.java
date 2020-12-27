package me.devtec.theapi.placeholderapi;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public abstract class PlaceholderPreRegister extends PlaceholderExpansion {
	private final String a, b, c;

	public PlaceholderPreRegister(String identifier, String author, String version) {
		a = identifier;
		b = author;
		c = version;
	}

	@Override
	public String getName() {
		return a;
	}

	@Override
	public String getAuthor() {
		return b;
	}

	@Override
	public String getVersion() {
		return c;
	}

	@Override
	public String getIdentifier() {
		return a;
	}

	@Override
	public String getPlugin() {
		return "TheAPI";
	}

	@Override
	public String getRequiredPlugin() {
		return null;
	}

	@Override
	public boolean register() {
		return PlaceholderAPI.registerPlaceholderHook(a, this);
	}

	@Override
	public String onRequest(OfflinePlayer player, String params) {
		return onPlaceholderRequest(player != null ? player.getPlayer() : null, params);
	}

	public String onPlaceholderRequest(Player player, String params) {
		if (params == null || params.trim().isEmpty())
			return null;
		return onRequest(player, params);
	}

	public abstract String onRequest(Player player, String params);
}
