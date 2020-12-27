package me.devtec.theapi.utils.datakeeper;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.datakeeper.collections.UnsortedList;

public class Storage implements me.devtec.theapi.utils.datakeeper.abstracts.Data {
	private List<Inventory> invs = new UnsortedList<>();
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
		List<ItemStack> items = new UnsortedList<>();
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
		return "Storage(" + toString() + ")";
	}
}
