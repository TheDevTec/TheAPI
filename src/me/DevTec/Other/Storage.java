package me.DevTec.Other;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

public class Storage {
	private List<Inventory> invs = Lists.newArrayList();
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
		List<ItemStack> items = Lists.newArrayList();
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
}
