package me.devtec.theapi.placeholderapi;

import org.bukkit.entity.Player;

public abstract class ThePlaceholder {
	private final String a;

	public ThePlaceholder(String name) {
		a = name;
	}

	public String getName() {
		return a;
	}

	public void register() {
		ThePlaceholderAPI.register(this);
	}

	public final void unregister() {
		ThePlaceholderAPI.unregister(this);
	}

	public abstract String onRequest(Player player, String placeholder);

	public String onPlaceholderRequest(Player player, String placeholder) {
		if (placeholder == null || placeholder.trim().isEmpty())
			return "";
		return onRequest(player, placeholder);
	}
}
