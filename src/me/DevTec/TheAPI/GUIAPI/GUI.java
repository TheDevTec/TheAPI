package me.DevTec.TheAPI.GUIAPI;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.DataKeeper.Collections.UnsortedList;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

public class GUI {
	private final String title;
	private final UnsortedMap<Integer, ItemGUI> items = new UnsortedMap<>();
	private final List<Player> opened = new UnsortedList<>();
	private final Inventory inv;
	// Defaulty false
	private boolean put;

	public GUI(String title, int size, Player... p) {
		this.title = TheAPI.colorize(title);
		if (size == 17 || size == 18 || size == 19)
			size = 18;
		else if (size == 26 || size == 27 || size == 28)
			size = 27;
		else if (size == 35 || size == 36 || size == 37)
			size = 36;
		else if (size == 44 || size == 45 || size == 46)
			size = 45;
		else if (size == 53 || size == 54 || size > 54)
			size = 54;
		else
			size = 9;
		inv = Bukkit.createInventory(null, size, this.title);
		open(p);
	}

	public void onClose(Player player) {
	}

	public boolean onPutItem(Player player, ItemStack item, int slot) {
		return false;
	}

	public boolean onTakeItem(Player player, ItemStack item, int slot) {
		return false;
	}

	public final ItemStack[] getContents() {
		return inv.getContents();
	}

	public final String getName() {
		return title;
	}

	public final List<Player> getPlayers() {
		return opened;
	}

	/**
	 * @see see Set menu insertable for items
	 */
	public final void setInsertable(boolean value) {
		put = value;
	}

	public final boolean isInsertable() {
		return put;
	}

	/**
	 * @see see Set item on position to the gui with options
	 */
	public final void setItem(int position, ItemGUI item) {
		items.put(position, item);
		inv.setItem(position, item.getItem());
	}

	/**
	 * @see see Remove item from position
	 */
	public final void removeItem(int slot) {
		items.remove(slot);
		inv.setItem(slot, null);
	}

	/**
	 * @see see Remove item from position
	 */
	public final void remove(int slot) {
		removeItem(slot);
	}

	/**
	 * @see see Add item to the first empty slot in gui
	 */
	public final void addItem(ItemGUI item) {
		if (getFirstEmpty() != -1)
			setItem(getFirstEmpty(), item);
	}

	/**
	 * @see see Add item to the first empty slot in gui
	 */
	public final void add(ItemGUI item) {
		addItem(item);
	}

	/**
	 * @see see Return ItemStack from position in gui
	 */
	public final ItemStack getItem(int slot) {
		try {
			return inv.getItem(slot);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @see see Return ItemGUI from position in gui
	 */
	public final ItemGUI getItemGUI(int slot) {
		return getItemGUIs().getOrDefault(slot, null);
	}

	/**
	 *
	 * @return boolean is gui full
	 */
	public final boolean isFull() {
		return getFirstEmpty() == -1;
	}

	/**
	 * @see see -1 mean menu is full
	 * @return int return first empty slot (if available)
	 */
	public final int getFirstEmpty() {
		return inv.firstEmpty();
	}

	/**
	 * @see see Open GUI menu to player
	 * 
	 */
	public final void open(Player... players) {
		for (Player player : players) {
			if (LoaderClass.plugin.gui.containsKey(player.getName())) {
				GUI a = LoaderClass.plugin.gui.get(player.getName());
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
	 * @return UnsortedMap<Slot, Item>
	 * 
	 */
	public final UnsortedMap<Integer, ItemGUI> getItemGUIs() {
		return items;
	}

	/**
	 * @see see Close opened gui for all players
	 * 
	 */
	public final void close() {
		for (Player a : new UnsortedList<>(opened))
			close(a);
	}

	/**
	 * @see see Clear all registered informations about gui
	 * 
	 */
	public final void clear() {
		inv.clear();
		items.clear();
	}

	/**
	 * @see see Close opened gui for specified player
	 * 
	 */
	public final void close(Player... players) {
		for (Player player : players)
			player.getOpenInventory().close();
	}

	public final String toString() {
		String items = "";
		for (Integer g : getItemGUIs().keySet()) {
			items += "/" + g + ":" + getItemGUIs().get(g).toString();
		}
		return "[GUI:" + title + "/" + put + "/" + inv.getSize() + items + "]";
	}

	public boolean equals(Object other) {
		if (other instanceof Inventory) {
			Inventory c = (Inventory) other;
			return c.equals(inv);
		}
		if (other instanceof GUI) {
			GUI c = (GUI) other;
			return c.inv.equals(inv) && c.title.equals(title);
		}
		return false;
	}
}
