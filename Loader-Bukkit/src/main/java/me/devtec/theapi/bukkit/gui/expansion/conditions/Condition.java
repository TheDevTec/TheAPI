package me.devtec.theapi.bukkit.gui.expansion.conditions;

import java.util.Map;

import org.bukkit.entity.Player;

public interface Condition {

	boolean has(Player player, Map<String, Object> placeholders);

}
