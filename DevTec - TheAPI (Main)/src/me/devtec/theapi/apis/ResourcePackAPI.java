package me.devtec.theapi.apis;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class ResourcePackAPI {
	
	public enum ResourcePackResult {
		SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED
	}
	
	public static interface ResourcePackHandler {
		public void onHandle(Player player, String resourcePack, ResourcePackResult result);
	}

	private static final Map<Player, String> resourcePacks = new HashMap<>();
	private static final Map<Player, ResourcePackHandler> resourcePacksLoading = new HashMap<>();
	
	public static ResourcePackHandler getHandlingPlayer(Player player) {
		return resourcePacksLoading.get(player);
	}
	
	public static ResourcePackHandler removeHandlingPlayer(Player player) {
		return resourcePacksLoading.remove(player);
	}
	
	public static String getResourcePack(Player player) {
		return resourcePacks.get(player);
	}
	
	public static void setResourcePack(Player player, String resourcePack, String sha) {
		setResourcePack(player, resourcePack, sha, false, null, null);
	}
	
	public static void setResourcePack(Player player, String resourcePack, String sha, ResourcePackHandler handler) {
		setResourcePack(player, resourcePack, sha, false, null, handler);
	}
	
	public static void setResourcePack(Player player, String resourcePack, String sha, boolean requireRP, @Nullable String prompt, ResourcePackHandler handler) {
		if(TheAPI.isOlderThan(8))return; //1.8+ only
		if(handler!=null)
			resourcePacksLoading.put(player, handler);
		else resourcePacksLoading.remove(player);
		resourcePacks.put(player, resourcePack);
		Ref.sendPacket(player, LoaderClass.nmsProvider.packetResourcePackSend(resourcePack, sha, requireRP, prompt));
	}
}
