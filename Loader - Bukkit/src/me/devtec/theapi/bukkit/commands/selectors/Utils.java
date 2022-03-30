package me.devtec.theapi.bukkit.commands.selectors;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.devtec.shared.utility.StringUtils;

public class Utils {

	public static boolean check(Iterable<String> iterable, String string) {
		for(String text : iterable)
			if(text.equalsIgnoreCase(string))return true;
		return false;
	}

	public static boolean check(SelectorType[] selectorTypes, String string) {
		for(SelectorType selectorType : selectorTypes)
			switch(selectorType) {
			case ENTITY_TYPE:
			try {
				EntityType.valueOf(string.toUpperCase());
				return true;
			}catch(Exception | NoSuchFieldError e) {
				return false;
			}
			case BIOME:
			try {
				Biome.valueOf(string.toUpperCase());
				return true;
			}catch(Exception | NoSuchFieldError e) {
				return false;
			}
			case PLAYER:
				return Bukkit.getPlayer(string)!=null;
			case WORLD:
				return Bukkit.getWorld(string)!=null;
			case NUMBER:
				return StringUtils.isNumber(string);
				default:
					break;
			}
		return false;
	}
	
	public static List<String> buildSelectorKeys(SelectorType[] selectorTypes) {
		List<String> text = new ArrayList<>();
		for(SelectorType selectorType : selectorTypes)
			switch(selectorType) {
			case ENTITY_TYPE:
				for(EntityType s : EntityType.values())
					text.add(s.name());
				break;
			case BIOME:
				for(Biome s : Biome.values())
					text.add(s.name());
				break;
			case PLAYER:
				for(Player s : Bukkit.getOnlinePlayers())
					text.add(s.getName());
				break;
			case WORLD:
				for(World world : Bukkit.getWorlds())
					text.add(world.getName());
			case NUMBER:
				text.add("{number}");
				break;
				default:
					break;
			}
		return text;
	}
}
