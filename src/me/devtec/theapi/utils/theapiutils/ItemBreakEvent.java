package me.devtec.theapi.utils.theapiutils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.nms.nbt.NBTEdit;
import me.devtec.theapi.utils.reflections.Ref;

public class ItemBreakEvent implements Listener {

	public boolean isUnbreakable(ItemStack a) {
		try {
			return a.getItemMeta().isUnbreakable();
		} catch (Exception | NoSuchMethodError er) {
			try {
				Object spigot = Ref.invoke(a.getItemMeta(), "spigot");
				if(spigot!=null)
					return (boolean) Ref.invoke(spigot,"isUnbreakable");
				return new NBTEdit(a).getBoolean("unbreakable");
			} catch (Exception | NoSuchMethodError errr) { //use our own wave
				return new NBTEdit(a).getBoolean("unbreakable");
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(PlayerItemDamageEvent e) {
		if(isUnbreakable(e.getItem()))e.setCancelled(true);
	}
}
