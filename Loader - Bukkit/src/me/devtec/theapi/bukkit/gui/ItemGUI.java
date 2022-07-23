package me.devtec.theapi.bukkit.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.bukkit.gui.GUI.ClickType;

public abstract class ItemGUI {
	private ItemStack s;
	// Defaulty true
	private boolean unsteal = true;

	public ItemGUI(ItemStack stack) {
		this.s = stack;
	}

	public abstract void onClick(Player player, HolderGUI gui, ClickType click);

	public final ItemGUI setUnstealable(boolean value)
	{
		this.unsteal = value;
		return this;
	}

	public final boolean isUnstealable()
	{
		return this.unsteal;
	}

	public final ItemStack getItem()
	{
		return this.s;
	}

	public final ItemGUI setItem(ItemStack stack)
	{
		if (stack != null)
			this.s = stack;
		return this;
	}
}
