package me.devtec.theapi.bukkit.game.resourcepack;

import org.bukkit.entity.Player;

public interface ResourcePackHandler {
    void call(Player player, ResourcePackResult result);
}
