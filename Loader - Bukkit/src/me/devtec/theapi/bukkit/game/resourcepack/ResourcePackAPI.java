package me.devtec.theapi.bukkit.game.resourcepack;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.devtec.shared.Ref;
import me.devtec.theapi.bukkit.BukkitLoader;

public class ResourcePackAPI {
	public void sendOffer(Player player, ResourcePackOffer offer, @Nullable ResourcePackHandler result) {
		if (Ref.isOlderThan(8))
			return;
		if (result != null)
			JavaPlugin.getPlugin(BukkitLoader.class).resourcePackHandler.put(player.getUniqueId(), result);
		BukkitLoader.getPacketHandler().send(player, BukkitLoader.getNmsProvider().packetResourcePackSend(offer.getUrl(), offer.getHash(), offer.isShouldForce(), offer.getPrompt()));
	}
}
