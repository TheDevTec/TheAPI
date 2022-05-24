package me.devtec.theapi.bukkit.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.shared.Ref;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;

public class AnvilGUI implements HolderGUI {

	private String title;
	private final Map<Integer, ItemGUI> items = new ConcurrentHashMap<>();
	private final Map<Player, Object> containers = new ConcurrentHashMap<>();
	private final Inventory inv;
	// Defaulty false
	private boolean put;
	private String text = "";

	public AnvilGUI(String original, Player... p) {
		this.title = StringUtils.colorize(original);
		if (Ref.isOlderThan(9) && this.title.length() >= 32)
			this.title = this.title.substring(0, 32);
		this.inv = Bukkit.createInventory(null, InventoryType.ANVIL, this.title);
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

	public final String getName() {
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
	public final void removeItem(int position) {
		this.items.remove(position);
		this.inv.setItem(position, null);
	}

	/**
	 * @apiNote Remove item from position
	 */
	@Override
	public final void remove(int slot) {
		this.removeItem(slot);
	}

	@Override
	public final ItemStack getItem(int slot) {
		return this.inv.getItem(slot);
	}

	/**
	 * @apiNote Return ItemStack from position in gui
	 */
	public final ItemStack getItem(Player target, int slot) {
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
			ItemStack[] item = new ItemStack[3];
			if (this.items.get(0) != null)
				item[0] = this.items.get(0).getItem();
			if (this.items.get(1) != null)
				item[1] = this.items.get(1).getItem();
			if (this.items.get(2) != null)
				item[2] = this.items.get(2).getItem();
			BukkitLoader.getNmsProvider().openAnvilGUI(player,
					container = BukkitLoader.getNmsProvider().createContainer(this.inv, player), this.title, item);
			this.containers.put(player, container);
			BukkitLoader.gui.put(player.getUniqueId(), this);
		}
	}

	@Override
	public Object getContainer(Player player) {
		return this.containers.get(player);
	}

	public String getRenameText() {
		return this.text;
	}

	@Override
	public final void setTitle(String title) {
		title = StringUtils.colorize(title);
		if (Ref.isOlderThan(9) && title.length() >= 32)
			title = title.substring(0, 32);
		if (title.equals(this.title))
			return;
		this.title = title;
		for (Entry<Player, Object> ec : this.containers.entrySet()) {
			BukkitLoader.getNmsProvider().setGUITitle(ec.getKey(), ec.getValue(), "minecraft:anvil", 0, title);
			for (int i = 0; i < 3; ++i)
				if (this.items.get(i) != null)
					BukkitLoader.getNmsProvider().setSlot(ec.getValue(), i,
							BukkitLoader.getNmsProvider().asNMSItem(this.items.get(i).getItem()));
		}
	}

	@Override
	public final String getTitle() {
		return this.title;
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
		this.items.clear();
		this.inv.clear();
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
			Object ac = this.containers.remove(player);
			if (ac != null)
				BukkitLoader.getNmsProvider().closeGUI(player, ac, true);
			BukkitLoader.gui.remove(player.getUniqueId());
			this.onClose(player);
		}
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

	@Override
	public final String toString() {
		StringBuilder items = new StringBuilder();
		for (Entry<Integer, ItemGUI> g : this.getItemGUIs().entrySet())
			items.append('/').append(g.getKey()).append(':').append(g.getValue().toString());
		return "[AnvilGUI:" + this.title + "/" + this.put + "/" + 3 + items.append(']');
	}

	@Override
	public int size() {
		return this.inv.getSize();
	}

	@Override
	public Inventory getInventory() {
		return this.inv;
	}

	public void setRepairText(String text) {
		this.text = text;
		for (Object o : this.containers.values())
			if (Ref.isNewerThan(16)) {
				Object anvil = o;
				for (int i = 0; i < 2; ++i)
					BukkitLoader.getNmsProvider().setSlot(anvil, i, BukkitLoader.getNmsProvider().getSlotItem(o, i));
				Ref.invoke(anvil, "a", text);
			} else
				Ref.invoke(o, "a", text);
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
		if (!list.contains(2))
			list.add(2);
		return list;
	}
}
