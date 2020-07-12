package me.DevTec.GUI;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.DevTec.TheAPI;
import me.DevTec.Other.LoaderClass;

public abstract class GUICreatorAPI {
	private final String title;
	private final LinkedHashMap<Integer, ItemGUI> items = Maps.newLinkedHashMap();
	private final List<Player> opened = Lists.newArrayList();
	private final Inventory inv;
	//Defaulty false
	private boolean put;
	
	public GUICreatorAPI(String title, int size, Player... p) {
		this.title = TheAPI.colorize(title);
		if(size ==17||size ==18||size ==19)size=18;
		else
		if(size ==26||size ==27||size ==28)size=27;
		else
		if(size ==35||size ==36||size ==37)size=36;
		else
		if(size ==44||size ==45||size ==46)size=45;
		else
		if(size ==53||size ==54||size>54)size=54;
		else size=9;
		inv = Bukkit.createInventory(null, size, this.title);
		open(p);
	}
	
	public abstract void onClose(Player player);
	
	public ItemStack[] getContents() {
		return inv.getContents();
	}
	
	public String getName() {
		return title;
	}

	public List<Player> getPlayers() {
		return opened;
	}
	
	/**
	 * @see see Set menu insertable for items
	 */
	public void setInsertable(boolean value) {
		put=value;
	}
	
	public boolean isInsertable() {
		return put;
	}

	/**
	 * @see see Set item on position to the gui with options
	 */
	public void setItem(int position, ItemGUI item) {
		items.put(position, item);
		inv.setItem(position, item.getItem());
	}

	/**
	 * @see see Remove item from position
	 */
	public void removeItem(int slot) {
		items.remove(slot);
		inv.setItem(slot, new ItemStack(Material.AIR));
	}

	/**
	 * @see see Remove item from position
	 */
	public void remove(int slot) {
		removeItem(slot);
	}

	/**
	 * @see see Add item to the first empty slot in gui
	 */
	public void addItem(ItemGUI item) {
		if (getFirstEmpty() != -1)
			setItem(getFirstEmpty(), item);
	}

	/**
	 * @see see Add item to the first empty slot in gui
	 */
	public void add(ItemGUI item) {
		addItem(item);
	}

	/**
	 * @see see Return ItemStack from position in gui
	 */
	public ItemStack getItem(int slot) {
		try {
		return inv.getItem(slot);
		}catch(Exception e) {
			return null;
		}
	}

	/**
	 *
	 * @return boolean is gui full
	 */
	public boolean isFull() {
		return getFirstEmpty() == -1;
	}

	/**
	 * @see see -1 mean menu is full
	 * @return int return first empty slot (if available)
	 */
	public int getFirstEmpty() {
		return inv.firstEmpty();
	}

	/**
	 * @see see Open GUI menu to player
	 * 
	 */
	public void open(Player... players) {
		for(Player player : players) {
			if(LoaderClass.plugin.gui.containsKey(player.getName())) {
				GUICreatorAPI a = LoaderClass.plugin.gui.get(player.getName());
				LoaderClass.plugin.gui.remove(player.getName());
				a.opened.remove(player);
				a.onClose(player);
			}
			player.openInventory(inv);
			opened.add(player);
			LoaderClass.plugin.gui.put(player.getName(), this);
		}
	}

	/**
	 * @return LinkedHashMap<Slot, Item>
	 * 
	 */
	public LinkedHashMap<Integer, ItemGUI> getItemGUIs(){
		return items;
	}

	/**
	 * @see see Close opened gui for all players
	 * 
	 */
	public void close() {
		for(Player a : Lists.newArrayList(opened))
		close(a);
	}

	/**
	 * @see see Clear all registered informations about gui
	 * 
	 */
	public void clear() {
		inv.clear();
		items.clear();
		close();
	}
	
	/**
	 * @see see Close opened gui for specified player
	 * 
	 */
	public void close(Player... players) {
		for(Player player : players)
			player.getOpenInventory().close();
	}
	
	public String toString() {
		return "[GUICreatorAPI:"+title+"/"+put+"/"+inv.getSize()+"]";
	}

	public boolean equals(Object other) {
		if(other instanceof Inventory) {
			Inventory c = (Inventory)other;
			return c.equals(inv);
		}
		if(other instanceof GUICreatorAPI) {
			GUICreatorAPI c = (GUICreatorAPI)other;
			return c.inv.equals(inv) && c.title.equals(title);
		}
		return false;
	}
}
