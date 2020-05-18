package me.Straiker123;

import java.util.List;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderAPIUtils {

	public boolean isEnabledPlaceholderAPI() {
		return TheAPI.getPluginsManagerAPI().isEnabledPlugin("PlaceholderAPI");
	}

	public String setPlaceholders(OfflinePlayer player, String where) {
		if (isEnabledPlaceholderAPI())
			where = PlaceholderAPI.setPlaceholders(player, where);
		return where;
	}

	public List<String> setPlaceholders(OfflinePlayer player, List<String> where) {
		if (isEnabledPlaceholderAPI())
			where = PlaceholderAPI.setPlaceholders(player, where);
		return where;
	}

}