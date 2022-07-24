package me.devtec.theapi.bukkit.game;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import me.devtec.shared.Ref;
import me.devtec.theapi.bukkit.BukkitLoader;

public class ResourcePackAPI {

	private static final Map<Player, String> resourcePacks = new ConcurrentHashMap<>();
	private static final Map<Player, ResourcePackHandler> resourcePacksLoading = new ConcurrentHashMap<>();

	public static ResourcePackHandler getHandlingPlayer(Player player) {
		return ResourcePackAPI.resourcePacksLoading.get(player);
	}

	public static ResourcePackHandler removeHandlingPlayer(Player player) {
		return ResourcePackAPI.resourcePacksLoading.remove(player);
	}

	public static String getResourcePack(Player player) {
		return ResourcePackAPI.resourcePacks.get(player);
	}

	public static void setResourcePack(Player player, String resourcePack, String sha) {
		ResourcePackAPI.setResourcePack(player, resourcePack, sha, false, null, null);
	}

	public static void setResourcePack(Player player, String resourcePack, String sha, ResourcePackHandler handler) {
		ResourcePackAPI.setResourcePack(player, resourcePack, sha, false, null, handler);
	}

	public static void setResourcePack(Player player, String resourcePack, String sha, boolean requireRP, @Nullable String prompt, ResourcePackHandler handler) {
		if (Ref.isOlderThan(8))
			return; // 1.8+ only
		if (handler != null)
			ResourcePackAPI.resourcePacksLoading.put(player, handler);
		else
			ResourcePackAPI.resourcePacksLoading.remove(player);
		ResourcePackAPI.resourcePacks.put(player, resourcePack);
		BukkitLoader.getPacketHandler().send(player, BukkitLoader.getNmsProvider().packetResourcePackSend(resourcePack, sha, requireRP, prompt));
	}

	public enum ResourcePackResult {
		SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED
	}

	public static interface ResourcePackHandler {
		public void onHandle(Player player, String resourcePack, ResourcePackResult result);
	}
}
