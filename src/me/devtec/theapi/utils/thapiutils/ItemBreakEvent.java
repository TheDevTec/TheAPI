package me.devtec.theapi.utils.thapiutils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;

public class ItemBreakEvent implements Listener {

	private boolean isUnbreakable(ItemStack i) {
		boolean is = false;
		if (i.getItemMeta().hasLore()) {
			if (i.getItemMeta().getLore().isEmpty() == false) {
				for (String s : i.getItemMeta().getLore()) {
					if (s.equals(TheAPI.colorize("&9UNBREAKABLE")))
						is = true;
				}
			}
		}
		return is;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onItemDestroy(PlayerItemBreakEvent e) {
		me.devtec.theapi.utils.listener.events.PlayerItemBreakEvent event = new me.devtec.theapi.utils.listener.events.PlayerItemBreakEvent(e.getPlayer(), e.getBrokenItem());
		TheAPI.callEvent(event);
		if (event.isCancelled() || isUnbreakable(event.getItem())) {
			ItemStack a = e.getBrokenItem();
			a.setDurability((short) 0);
			TheAPI.giveItem(e.getPlayer(), a);
		}
	}
}
