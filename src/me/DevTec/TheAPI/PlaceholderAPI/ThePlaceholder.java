package me.DevTec.TheAPI.PlaceholderAPI;

import org.bukkit.entity.Player;

public abstract class ThePlaceholder {
	private final String a;
	public ThePlaceholder(String name) {
		a=name;
	}
	
	public String getName() {
		return a;
	}
	
	public abstract String onRequest(Player player, String placeholder);

	public String onPlaceholderRequest(Player player, String placeholder) {
		if(placeholder==null||placeholder.trim().isEmpty())return "";
		return onRequest(player, placeholder);
	}
}
