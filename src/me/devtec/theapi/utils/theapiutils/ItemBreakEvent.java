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
			return (boolean) Ref.invoke(Ref.invoke(a.getItemMeta(), "spigot"),"isUnbreakable");
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
