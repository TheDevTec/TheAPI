package me.devtec.theapi.placeholderapi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import me.devtec.theapi.apis.PluginManagerAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class PlaceholderAPI {
	public static boolean isEnabledPlaceholderAPI() {
		return PluginManagerAPI.getPlugin("PlaceholderAPI") != null;
	}
	
	private static Method set = Ref.method(Ref.getClass("me.clip.placeholderapi.PlaceholderAPI"),
			"setPlaceholders", Player.class, String.class);

	public static String setPlaceholders(Player player, String where) {
		if (where == null || where.trim().isEmpty())
			return where;
		String e = null;
		if (isEnabledPlaceholderAPI())
			e = (String) Ref.invokeNulled(set, player, where);
		return ThePlaceholderAPI.setPlaceholders(player, e == null ? where : e);
	}

	public static List<String> setPlaceholders(Player player, List<String> where) {
		if (where == null || where.isEmpty())
			return where;
		List<String> e = null;
		if (isEnabledPlaceholderAPI()) {
			where=new ArrayList<>(where);
			where.replaceAll(a -> setPlaceholders(player, a));
		}
		return ThePlaceholderAPI.setPlaceholders(player, e == null ? where : e);
	}
}