package me.DevTec.Placeholders;

import java.util.List;

import org.bukkit.OfflinePlayer;

import me.DevTec.TheAPI;
import me.DevTec.NMS.Reflections;

public class PlaceholderAPIUtils {

	public boolean isEnabledPlaceholderAPI() {
		return TheAPI.getPluginsManagerAPI().isEnabledPlugin("PlaceholderAPI");
	}

	public String setPlaceholders(OfflinePlayer player, String where) {
		if (isEnabledPlaceholderAPI())
			where = (String)Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"setPlaceholders",OfflinePlayer.class,String.class),player, where);
		return where;
	}

	@SuppressWarnings("unchecked")
	public List<String> setPlaceholders(OfflinePlayer player, List<String> where) {
		if (isEnabledPlaceholderAPI())
			where = (List<String>)Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"setPlaceholders",OfflinePlayer.class,List.class),player, where);
		return where;
	}
	
	public void registerPlaceholder(PlaceholderRegister e) {
		if (isEnabledPlaceholderAPI())
			Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"registerExpansion",Reflections.getClass("me.clip.placeholderapi.expansion.PlaceholderExpansion")),e);
	}
	
	public void unregisterPlaceholder(PlaceholderRegister e) {
		if (isEnabledPlaceholderAPI())
			Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"unregisterExpansion",Reflections.getClass("me.clip.placeholderapi.expansion.PlaceholderExpansion")),e);
	}
}