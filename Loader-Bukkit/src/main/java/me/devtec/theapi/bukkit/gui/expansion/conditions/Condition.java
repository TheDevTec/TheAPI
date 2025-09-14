package me.devtec.theapi.bukkit.gui.expansion.conditions;

import java.util.Map;

import org.bukkit.entity.Player;

import me.devtec.shared.dataholder.Config;

public interface Condition {

	boolean has(Player player, Config sharedData, Map<String, Object> placeholders);

}
