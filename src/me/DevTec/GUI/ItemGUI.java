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
	
	public final void setUnstealable(boolean value) {
		unsteal=value;
	}
	
	public final boolean isUnstealable() {
		return unsteal;
	}
	
	public final ItemStack getItem() {
		return s;
	}
	
	public final void setItem(ItemStack stack) {
		if(stack!=null)
		s=stack;
	}
	
	public final String toString() {
		return "[ItemGUI:"+unsteal+"/"+s.toString()+"]";
	}
}
