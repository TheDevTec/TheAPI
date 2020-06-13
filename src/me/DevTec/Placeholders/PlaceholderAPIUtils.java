package me.DevTec.Placeholders;

import java.util.List;

import org.bukkit.OfflinePlayer;

import me.DevTec.TheAPI;
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
	
	public void registerPlaceholder(PlaceholderRegister e) {
		if (isEnabledPlaceholderAPI())
		PlaceholderAPI.registerExpansion(e);
	}
	
	public void unregisterPlaceholder(PlaceholderRegister e) {
		if (isEnabledPlaceholderAPI())
		PlaceholderAPI.unregisterExpansion(e);
	}
	
	/**
	 * Example:
	 * 
	  PlaceholderRegister reg = new PlaceholderRegister("DevTec", "TheAPI", "5.0") {
			public String onRequest(Player player, String placeholder) {
				if(placeholder.equalsIgnoreCase("version"))return LoaderClass.plugin.getDescription().getVersion();
				if(placeholder.equalsIgnoreCase("usingplugins"))return LoaderClass.plugin.getTheAPIsPlugins().size()+"";
				if(placeholder.equalsIgnoreCase("author"))return "DevTec";
				return null;
			}
		};
		//       DevTec             ???, int              5.0
		//-> %TheAPI_Author%, %TheAPI_UsingPlugins%, %TheAPI_Version%
		
		public void onEnable() {
		TheAPI.getPlaceholderAPIUtils().registerPlaceholder(reg);
		}
		
		public void onDisable() {
		TheAPI.getPlaceholderAPIUtils().unregisterPlaceholder(reg);
		}
	 */
	
}