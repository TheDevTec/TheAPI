package me.devtec.theapi.guiapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.guiapi.GUI.ClickType;

public abstract class ItemGUI {
	public abstract void onClick(Player player, HolderGUI gui, ClickType click);

	private ItemStack s;
	// Defaulty true
	private boolean unsteal = true;

	public ItemGUI(ItemStack stack) {
		s = stack;
	}

	public final ItemGUI setUnstealable(boolean value) {
		unsteal = value;
		return this;
	}

	public final boolean isUnstealable() {
		return unsteal;
	}

	public final ItemStack getItem() {
		return s;
	}

	public final ItemGUI setItem(ItemStack stack) {
		if (stack != null)
			s = stack;
		return this;
	}
}
