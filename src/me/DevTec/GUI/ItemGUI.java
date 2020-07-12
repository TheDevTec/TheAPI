package me.DevTec.GUI;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class ItemGUI {
	public abstract void onClick(Player player, GUICreatorAPI gui, ClickType click);
	
	private ItemStack s;
	//Defaulty true
	private boolean unsteal=true;
	
	public ItemGUI(ItemStack stack) {
		s=stack;
	}
	
	public void setUnstealable(boolean value) {
		unsteal=value;
	}
	
	public boolean isUnstealable() {
		return unsteal;
	}
	
	public ItemStack getItem() {
		return s;
	}
	
	public void setItem(ItemStack stack) {
		if(stack!=null)
		s=stack;
	}
}
