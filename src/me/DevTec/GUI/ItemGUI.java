package me.DevTec.GUI;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.DevTec.GUI.GUICreatorAPI.Options;

public abstract class ItemGUI {
	private ItemStack i = new ItemStack(Material.STONE);
	private HashMap<Options, Object> ops = new HashMap<Options, Object>();

	public ItemGUI(ItemStack item) {
		i = item;
	}
	
	public abstract void onClick(Player used);

	public ItemStack getItem() {
		return i;
	}

	public void setItem(ItemStack item) {
		if (item != null)
			i = item;
	}

	public HashMap<Options, Object> getOptions() {
		return ops;
	}

	public boolean hasOption(Options o) {
		return ops.containsKey(o);
	}

	public void removeOption(Options o) {
		ops.remove(o);
	}

	public void addOption(Options o, Object a) {
		ops.put(o, a);
	}

	public void setOption(Options o, Object a) {
		ops.put(o, a);
	}

	/**
	 * @see see Apply this item with options on preparing GUI
	 */
	public void apply(GUICreatorAPI a, int position) {
		a.applyItemGUI(this, position);
	}
}
