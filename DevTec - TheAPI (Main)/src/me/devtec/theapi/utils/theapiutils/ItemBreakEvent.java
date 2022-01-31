package me.devtec.theapi.utils.theapiutils;

import java.lang.reflect.Method;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.devtec.theapi.utils.nms.nbt.NBTEdit;
import me.devtec.theapi.utils.reflections.Ref;

public class ItemBreakEvent implements Listener {

	private static Method spigotMethod = Ref.method(ItemMeta.class, "spigot");
	private static Method isUnbreakable = Ref.method(Ref.getClass("org.bukkit.inventory.meta.ItemMeta$Spigot"), "isUnbreakable");
	
	public boolean isUnbreakable(ItemStack a) {
		try {
			return a.getItemMeta().isUnbreakable();
		} catch (Exception | NoSuchMethodError er) {
			try {
				Object spigot = Ref.invoke(a.getItemMeta(), spigotMethod);
				if(spigot!=null)
					return (boolean) Ref.invoke(spigot,isUnbreakable);
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
