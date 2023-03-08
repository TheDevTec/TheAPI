package me.devtec.theapi.bukkit.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.bukkit.gui.GUI.ClickType;

public abstract class ItemGUI {
	private ItemStack s;
	// Defaulty true
	private boolean steal;

	public ItemGUI(ItemStack stack) {
		s = stack;
	}

	public abstract void onClick(Player player, HolderGUI gui, ClickType click);

	public final ItemGUI setUnstealable(boolean value) {
		steal = !value;
		return this;
	}

	public final boolean isUnstealable() {
		return !steal;
	}

	public final ItemStack getItem() {
		return s;
	}

	public final ItemGUI setItem(ItemStack stack) {
		if (stack != null)
			s = stack;
		return this;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 18 + s.hashCode();
		return hash * 18 + (steal ? 1 : 0);
	}
}
