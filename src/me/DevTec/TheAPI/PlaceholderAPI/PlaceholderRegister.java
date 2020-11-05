package me.DevTec.TheAPI.PlaceholderAPI;

import org.bukkit.entity.Player;

public abstract class PlaceholderRegister extends PlaceholderPreRegister {
	public PlaceholderRegister(String identifier, String author, String version) {
		super(identifier, author, version);
	}

	@Override
	public abstract String onRequest(Player player, String params);
}
