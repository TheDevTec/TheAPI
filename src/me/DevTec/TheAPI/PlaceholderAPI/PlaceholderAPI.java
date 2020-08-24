package me.DevTec.TheAPI.PlaceholderAPI;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import me.DevTec.TheAPI.APIs.PluginManagerAPI;
import me.DevTec.TheAPI.Utils.Reflections.Reflections;

public class PlaceholderAPI {
	private static List<PlaceholderRegister> reg = Lists.newArrayList();
	public static boolean isEnabledPlaceholderAPI() {
		return PluginManagerAPI.isEnabledPlugin("PlaceholderAPI");
	}

	public static String setPlaceholders(Player player, String where) {
		String edited = where;
		if (isEnabledPlaceholderAPI()) {
		for(PlaceholderRegister r : reg) {
			if(edited==null)break;
			edited=r.onRequest(player, edited);
		}}
		if(edited==null)return edited;
		edited=ThePlaceholderAPI.setPlaceholders(player, edited);
		if(edited==null)return edited;
		if (isEnabledPlaceholderAPI())
			edited = (String)Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"setPlaceholders",OfflinePlayer.class,String.class),player, edited);
		return edited;
	}

	@SuppressWarnings("unchecked")
	public static List<String> setPlaceholders(Player player, List<String> where) {
		List<String> edited = Lists.newArrayList();
		if (isEnabledPlaceholderAPI()) {
		for(String s : where) {
			for(PlaceholderRegister r : reg)s=r.onRequest(player, s);
			edited.add(s);
		}
		edited=ThePlaceholderAPI.setPlaceholders(player, edited);
		edited = (List<String>)Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
				"setPlaceholders",OfflinePlayer.class,List.class),player, where);
		}else {
			for(String s : where) {
				edited.add(ThePlaceholderAPI.setPlaceholders(player, s));
			}	
		}
		return edited;
	}
	
	public static boolean isRegistredPlaceholder(PlaceholderRegister e) {
		return reg.contains(e);
	}
	
	public static void registerPlaceholder(PlaceholderRegister e) {
		if(isRegistredPlaceholder(e))return;
		reg.add(e);
		if (isEnabledPlaceholderAPI()) {
			Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"registerExpansion",Reflections.getClass("me.clip.placeholderapi.expansion.PlaceholderExpansion")),e);
	}}
	
	public void unregisterPlaceholder(PlaceholderRegister e) {
		if(!isRegistredPlaceholder(e))return;
		reg.remove(e);
		if (isEnabledPlaceholderAPI()) {
		Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
				"unregisterExpansion",Reflections.getClass("me.clip.placeholderapi.expansion.PlaceholderExpansion")),e);
	}}
}