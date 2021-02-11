package me.devtec.theapi.guiapi;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class EmptyItemGUI extends ItemGUI {
	
	public EmptyItemGUI(ItemStack stack) {
		super(stack);
	}

	public void onClick(Player player, GUI gui, ClickType click) {}
}
