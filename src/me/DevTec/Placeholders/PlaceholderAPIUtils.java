package me.DevTec.Placeholders;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import me.DevTec.TheAPI;
import me.DevTec.NMS.Reflections;

public class PlaceholderAPIUtils {
	private static List<PlaceholderRegister> reg = Lists.newArrayList();
	public boolean isEnabledPlaceholderAPI() {
		return TheAPI.getPluginsManagerAPI().isEnabledPlugin("PlaceholderAPI");
	}

	public String setPlaceholders(Player player, String where) {
		String edited = where;
		for(PlaceholderRegister r : reg) {
			if(edited==null)break;
			edited=r.onRequest(player, edited);
		}
		if(edited==null)return null;
		if (isEnabledPlaceholderAPI())
			edited = (String)Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"setPlaceholders",OfflinePlayer.class,String.class),player, edited);
		return edited;
	}

	@SuppressWarnings("unchecked")
	public List<String> setPlaceholders(Player player, List<String> where) {
		List<String> edited = Lists.newArrayList();
		for(String s : where) {
			for(PlaceholderRegister r : reg)s=r.onRequest(player, s);
			edited.add(s);
		}
		if (isEnabledPlaceholderAPI())
			edited = (List<String>)Reflections.invoke(null, Reflections.getMethod(Reflections.getClass("me.clip.placeholderapi.PlaceholderAPI"),
					"setPlaceholders",OfflinePlayer.class,List.class),player, where);
		return edited;
	}
	
	public boolean isRegistredPlaceholder(PlaceholderRegister e) {
		return reg.contains(e);
	}
	
	public void registerPlaceholder(PlaceholderRegister e) {
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