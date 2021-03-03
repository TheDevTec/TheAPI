package me.devtec.theapi.guiapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.guiapi.GUI.ClickType;

public class EmptyItemGUI extends ItemGUI {
	
	public EmptyItemGUI(ItemStack stack) {
		super(stack);
	}

	public void onClick(Player player, GUI gui, ClickType click) {}
}
