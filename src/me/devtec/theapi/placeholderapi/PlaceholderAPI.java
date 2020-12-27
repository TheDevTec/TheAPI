package me.devtec.theapi.placeholderapi;

import java.util.List;

import org.bukkit.entity.Player;

import me.devtec.theapi.apis.PluginManagerAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class PlaceholderAPI {
	public static boolean isEnabledPlaceholderAPI() {
		return PluginManagerAPI.getPlugin("PlaceholderAPI") != null;
	}

	public static String setPlaceholders(Player player, String where) {
		if (where == null || where.trim().isEmpty())
			return null;
		String e = null;
		if (isEnabledPlaceholderAPI())
			e = (String) Ref.invokeNulled(Ref.method(Ref.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"setPlaceholders", Player.class, String.class), player, where);
		return ThePlaceholderAPI.setPlaceholders(player, e == null ? where : e);
	}

	@SuppressWarnings("unchecked")
	public static List<String> setPlaceholders(Player player, List<String> where) {
		if (where == null || where.isEmpty())
			return where;
		List<String> e = null;
		if (isEnabledPlaceholderAPI())
			e = (List<String>) Ref.invokeNulled(Ref.method(Ref.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"setPlaceholders", Player.class, List.class), player, where);
		return ThePlaceholderAPI.setPlaceholders(player, e == null ? where : e);
	}
}