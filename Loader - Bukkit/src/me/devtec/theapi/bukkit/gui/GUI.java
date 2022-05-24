package me.devtec.theapi.bukkit.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.shared.Ref;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;

public class GUI implements HolderGUI {
	public static final int LINES_6 = 54;
	public static final int LINES_5 = 45;
	public static final int LINES_4 = 36;
	public static final int LINES_3 = 27;
	public static final int LINES_2 = 18;
	public static final int LINES_1 = 9;

	public enum ClickType {
		MIDDLE_PICKUP, MIDDLE_DROP, LEFT_DROP, RIGHT_PICKUP, RIGHT_DROP, LEFT_PICKUP, SHIFT_LEFT_DROP,
		SHIFT_RIGHT_PICKUP, SHIFT_RIGHT_DROP, SHIFT_LEFT_PICKUP
	}

	private String title;
	private final Map<Integer, ItemGUI> items = new ConcurrentHashMap<>();
	private final Map<Player, Object> containers = new ConcurrentHashMap<>();
	private final Inventory inv;
	// Defaulty false
	private boolean put;

	public GUI(String original, int size, Player... p) {
		this.title = StringUtils.colorize(this.title);
		switch (size) {
		case 17:
		case 18:
		case 19:
			size = 18;
			break;
		case 26:
		case 27:
		case 28:
			size = 27;
			break;
		case 35:
		case 36:
		case 37:
			size = 36;
			break;
		case 44:
		case 45:
		case 46:
			size = 45;
			break;
		default:
			if (size > 46)
				size = 54;
			else
				size = 9;
			break;
		}
		this.title = StringUtils.colorize(this.title);
		if (Ref.isOlderThan(9) && this.title.length() >= 32)
			this.title = this.title.substring(0, 32);
		this.inv = Bukkit.createInventory(null, size, this.title);
		this.open(p);
	}

	/**
	 * @apiNote Actions before close gui
	 */
	public void onPreClose(Player player) {
		// Before gui is closed actions
	}

	/**
	 * @apiNote Actions on close gui
	 */
	@Override
	public void onClose(Player player) {
		// Closed gui actions
	}

	@Override
	public boolean onIteractItem(Player player, ItemStack item, ClickType type, int slot, boolean gui) {
		return false;
	}

	public final ItemStack[] getContents() {
		return this.inv.getContents();
	}

	public final String getName() {
		return this.title;
	}

	@Override
	public final String getTitle() {
		return this.title;
	}

	/**
	 * @apiNote Set menu insertable for items
	 */
	@Override
	public final void setInsertable(boolean value) {
		this.put = value;
	}

	@Override
	public final boolean isInsertable() {
		return this.put;
	}

	/**
	 * @apiNote Set item on position to the gui with options
	 */
	@Override
	public final void setItem(int position, ItemGUI item) {
		this.items.put(position, item);
		this.inv.setItem(position, item.getItem());
	}

	/**
	 * @apiNote Remove item from position
	 */
	public final void removeItem(int slot) {
		this.items.remove(slot);
		this.inv.setItem(slot, null);
	}

	/**
	 * @apiNote Remove item from position
	 */
	@Override
	public final void remove(int slot) {
		this.removeItem(slot);
	}

	/**
	 * @apiNote Add item to the first empty slot in gui
	 */
	public final void addItem(ItemGUI item) {
		if (this.getFirstEmpty() != -1)
			this.setItem(this.getFirstEmpty(), item);
	}

	/**
	 * @apiNote Add item to the first empty slot in gui
	 */
	public final void add(ItemGUI item) {
		this.addItem(item);
	}

	/**
	 * @apiNote Return ItemStack from position in gui
	 */
	@Override
	public final ItemStack getItem(int slot) {
		try {
			return this.inv.getItem(slot);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @apiNote Return ItemGUI from position in gui
	 */
	@Override
	public final ItemGUI getItemGUI(int slot) {
		return this.getItemGUIs().get(slot);
	}

	/**
	 *
	 * @return boolean is gui full
	 */
	public final boolean isFull() {
		return this.getFirstEmpty() == -1;
	}

	/**
	 * @apiNote -1 mean menu is full
	 * @return int return first empty slot (if available)
	 */
	public final int getFirstEmpty() {
		return this.inv.firstEmpty();
	}

	/**
	 * @apiNote Open GUI menu to player
	 *
	 */
	public final void open(Player... players) {
		for (Player player : players) {
			if (BukkitLoader.gui.containsKey(player.getUniqueId())) {
				HolderGUI a = BukkitLoader.gui.get(player.getUniqueId());
				BukkitLoader.gui.remove(player.getUniqueId());
				a.onClose(player);
			}
			Object container;
			BukkitLoader.getNmsProvider().openGUI(player,
					container = BukkitLoader.getNmsProvider().createContainer(this.inv, player), "minecraft:chest",
					this.inv.getSize(), this.title, this.inv.getContents());
			this.containers.put(player, container);
			BukkitLoader.gui.put(player.getUniqueId(), this);
		}
	}

	@Override
	public final void setTitle(String title) {
		title = StringUtils.colorize(title);
		if (Ref.isOlderThan(9) && title.length() >= 32)
			title = title.substring(0, 32);
		if (title.equals(this.title))
			return;
		this.title = title;
		for (Entry<Player, Object> ec : this.containers.entrySet())
			BukkitLoader.getNmsProvider().setGUITitle(ec.getKey(), ec.getValue(), "minecraft:chest", this.inv.getSize(),
					title);
	}

	/**
	 * @return Map<Slot, Item>
	 *
	 */
	@Override
	public final Map<Integer, ItemGUI> getItemGUIs() {
		return this.items;
	}

	/**
	 * @return Collection<Player>
	 *
	 */
	@Override
	public final Collection<Player> getPlayers() {
		return this.containers.keySet();
	}

	/**
	 * @return boolean
	 *
	 */
	public final boolean hasOpen(Player player) {
		return this.containers.containsKey(player);
	}

	/**
	 * @apiNote Close opened gui for all players
	 *
	 */
	@Override
	public final void close() {
		this.close(this.containers.keySet().toArray(new Player[0]));
	}

	/**
	 * @apiNote Clear all registered information about gui
	 *
	 */
	@Override
	public final void clear() {
		this.inv.clear();
		this.items.clear();
	}

	/**
	 * @apiNote Close opened gui for specified player
	 *
	 */
	@Override
	public final void close(Player... players) {
		if (players == null)
			return;
		for (Player player : players) {
			if (player == null)
				continue;
			this.onPreClose(player);
			Object container = this.containers.remove(player);
			if (container != null)
				BukkitLoader.getNmsProvider().closeGUI(player, container, true);
			BukkitLoader.gui.remove(player.getUniqueId());
			this.onClose(player);
		}
	}

	@Override
	public final String toString() {
		StringBuilder items = new StringBuilder();
		for (Entry<Integer, ItemGUI> g : this.getItemGUIs().entrySet())
			items.append('/').append(g.getKey()).append(':').append(g.getValue().toString());
		return "[GUI:" + this.title + "/" + this.put + "/" + this.inv.getSize() + items.append(']');
	}

	public int getSize() {
		return this.inv.getSize();
	}

	@Override
	public int size() {
		return this.inv.getSize();
	}

	@Override
	public Object getContainer(Player player) {
		return this.containers.get(player);
	}

	@Override
	public void closeWithoutPacket(Player... p) {
		if (p == null)
			return;
		for (Player player : p) {
			if (player == null)
				continue;
			this.onPreClose(player);
			Object ac = this.containers.remove(player);
			if (ac != null)
				BukkitLoader.getNmsProvider().closeGUI(player, ac, false);
			BukkitLoader.gui.remove(player.getUniqueId());
			this.onClose(player);
		}
	}

	/**
	 * @apiNote Returns not interable slots via SHIFT click
	 */
	@Override
	public List<Integer> getNotInterableSlots(Player player) {
		List<Integer> list = new ArrayList<>();
		if (this.isInsertable())
			for (int i = 0; i < this.size(); ++i) {
				ItemGUI item = this.items.get(i);
				if (item != null && item.isUnstealable())
					list.add(i);
			}
		else
			for (int i = 0; i < this.size(); ++i)
				list.add(i);
		return list;
	}

	@Override
	public Inventory getInventory() {
		return this.inv;
	}
}
