package me.devtec.theapi.bukkit.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.bukkit.gui.GUI.ClickType;

public class EmptyItemGUI extends ItemGUI {

	public EmptyItemGUI(ItemStack stack) {
		super(stack);
	}

	@Override
	public void onClick(Player player, HolderGUI gui, ClickType click) {
		// ItemGUI without action
	}
}
