package me.devtec.theapi.bukkit.tablist;

import java.util.List;

import org.bukkit.entity.Player;

import me.devtec.shared.Ref;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.NmsProvider.PlayerInfoType;

public class TabAPI {
	public static void setTabListName(Player p, String name) {
		Object obj = BukkitLoader.getNmsProvider().packetPlayerInfo(PlayerInfoType.UPDATE_DISPLAY_NAME, p);
		Ref.set(((List<?>) Ref.get(obj, "b")).get(0), "d", BukkitLoader.getNmsProvider().toIChatBaseComponent(
				ComponentAPI.fromString(StringUtils.colorize(name == null ? p.getName() : name))));
		BukkitLoader.getNmsProvider().getOnlinePlayers()
				.forEach(player -> BukkitLoader.getPacketHandler().send(player, obj));
	}

	public static void setHeaderFooter(Player player, String header, String footer) {
		if (Ref.isOlderThan(8))
			return;
		BukkitLoader.getPacketHandler().send(player, BukkitLoader.getNmsProvider()
				.packetPlayerListHeaderFooter(StringUtils.colorize(header), StringUtils.colorize(footer)));
	}
}
