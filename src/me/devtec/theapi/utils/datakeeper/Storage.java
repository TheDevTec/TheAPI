package me.devtec.theapi.utils.datakeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.utils.json.Writer;

public class Storage implements me.devtec.theapi.utils.datakeeper.abstracts.Data {
	private List<ItemStack> items = new ArrayList<>();

	public void add(ItemStack item) {
		ItemStack add = new ItemStack(item);
		for(ItemStack i : items) {
			if(i.getAmount()<i.getMaxStackSize()) {
				if(i.getItemMeta().equals(item.getItemMeta()) && i.getType()==item.getType() && i.getDurability()==item.getDurability()) {
					int count = i.getAmount()+add.getAmount();
					if(count>i.getMaxStackSize()) {
						add.setAmount(count-i.getMaxStackSize());
						i.setAmount(i.getMaxStackSize());
					}else {
						i.setAmount(count);
						add=null;
						break;
					}
				}
			}
		}
		if(add!=null)
			items.add(add);
	}

	public void remove(int slot) {
		if(items.size()>slot && slot >= 0)items.remove(slot);
	}

	public void remove(ItemStack item, int count) {
		int amount=count;
		Iterator<ItemStack> it = items.iterator();
		while(it.hasNext()) {
		ItemStack i = it.next();
			if(i.getAmount()<i.getMaxStackSize()) {
				if(i.getItemMeta().equals(item.getItemMeta()) && i.getType()==item.getType() && i.getDurability()==item.getDurability()) {
					int counter = i.getAmount()-amount;
					if(counter>0) {
						i.setAmount(counter);
						break;
					}else {
						it.remove();
						amount-=i.getAmount();
					}
				}
			}
		}
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public List<ItemStack> getItems() {
		return items;
	}

	public void clear() {
		items.clear();
	}

	public int size() {
		return items.size();
	}

	@Override
	public String toString() {
		return getDataName();
	}
	
	@Override
	public String getDataName() {
		HashMap<String, Object> s = new HashMap<>();
		s.put("items", items.size());
		s.put("list", items);
		return Writer.write(s);
	}
}
