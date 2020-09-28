package me.DevTec.TheAPI.Utils.DataKeeper;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Storage implements me.DevTec.TheAPI.Utils.DataKeeper.Abstract.Data {
	private static final long serialVersionUID = 1L;
	private List<Inventory> invs = new ArrayList<>();
	private Inventory inv = Bukkit.createInventory(null, 54);

	public void add(ItemStack item) {
		if (invs.contains(inv))
			invs.remove(inv);
		if (inv.firstEmpty() == -1) {
			invs.add(inv);
			inv = Bukkit.createInventory(null, 54);
		} else {
		}
		inv.addItem(item);
	}

	public List<Inventory> getInventories() {
		if (invs.contains(inv) == false)
			invs.add(inv);
		return invs;
	}

	public boolean isEmpty() {
		return getInventories().isEmpty();
	}

	public List<ItemStack> getItems() {
		List<ItemStack> items = new ArrayList<>();
		for (Inventory i : getInventories()) {
			for (ItemStack a : i.getContents()) {
				try {
					items.add(a);
				} catch (Exception er) {
				}
			}
		}
		return items;
	}

	public void clear() {
		inv = Bukkit.createInventory(null, 54);
		invs.clear();
	}

	public int size() {
		return invs.size();
	}
	
	public String toString() {
		return invs.toString();
	}

	@Override
	public String getDataName() {
		return "Storage("+toString()+")";
	}
}
