package me.DevTec.Placeholders;

import org.bukkit.entity.Player;

public abstract class PlaceholderRegister extends PlaceholderPreRegister {
	public PlaceholderRegister(String author, String prefix, String version) {
		super(author, prefix, version);
	}
    
	public abstract String onRequest(Player player, String placeholder);
}
