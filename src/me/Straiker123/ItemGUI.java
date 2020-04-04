package me.Straiker123;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import me.Straiker123.GUICreatorAPI.Options;

public class ItemGUI {
	private ItemStack i=new ItemStack(Material.STONE);
	private HashMap<Options, Object> ops = new HashMap<Options, Object>();
	
	public ItemGUI(ItemStack item) {
		i=item;
	}
	
	public ItemStack getItem() {
		return i;
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
		ops.put(o,a);
	}
	/**
	 * @see see Apply this item with options on preparing GUI
	 */
	public void apply(GUICreatorAPI a, int position) {
		a.setItem(position, i, ops);
	}
}
