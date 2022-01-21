package me.devtec.theapi.guiapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.guiapi.GUI.ClickType;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class AnvilGUI implements HolderGUI {
	
	private String title;
	private final Map<Integer, ItemGUI> items = new HashMap<>();
	private final Map<Player, Object> containers = new HashMap<>();
	private final Inventory inv;
	// Defaulty false
	private boolean put;
	private String text = "";

	public AnvilGUI(String title, Player... p) {
		title=StringUtils.colorize(title);
		if(TheAPI.isOlderThan(9)) {
			if(title.length() >= 32) {
				title=title.substring(0, 32);
			}
		}
		this.title=title;
		inv=Bukkit.createInventory(null, InventoryType.ANVIL, title);
		open(p);
	}

	public void onPreClose(Player player) {
	}

	public void onClose(Player player) {
	}

	public boolean onIteractItem(Player player, ItemStack item, ClickType type, int slot, boolean gui) {
		return false;
	}
	
	public final String getName() {
		return title;
	}
	
	/**
	 * @apiNote Set menu insertable for items
	 */
	public final void setInsertable(boolean value) {
		put = value;
	}

	public final boolean isInsertable() {
		return put;
	}
	
	/**
	 * @apiNote Set item on position to the gui with options
	 */
	public final void setItem(int position, ItemGUI item) {
		items.put(position, item);
		inv.setItem(position, item.getItem());
	}

	/**
	 * @apiNote Remove item from position
	 */
	public final void removeItem(int position) {
		items.remove(position);
		inv.setItem(position, null);
	}

	/**
	 * @apiNote Remove item from position
	 */
	public final void remove(int slot) {
		removeItem(slot);
	}
	
	public final ItemStack getItem(int slot) {
		return inv.getItem(slot);
	}

	/**
	 * @apiNote Return ItemStack from position in gui
	 */
	public final ItemStack getItem(Player target, int slot) {
		try {
			return inv.getItem(slot);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @apiNote Return ItemGUI from position in gui
	 */
	public final ItemGUI getItemGUI(int slot) {
		return getItemGUIs().get(slot);
	}
	
	/**
	 * @apiNote Open GUI menu to player
	 * 
	 */
	public final void open(Player... players) {
		for(Player player : players) {
			if (LoaderClass.plugin.gui.containsKey(player.getName())) {
				HolderGUI a = LoaderClass.plugin.gui.get(player.getName());
				LoaderClass.plugin.gui.remove(player.getName());
				a.onClose(player);
			}
			Object container;
			ItemStack[] item = new ItemStack[3];
			if(items.get(0)!=null)
			item[0]=items.get(0).getItem();
			if(items.get(1)!=null)
			item[1]=items.get(1).getItem();
			if(items.get(2)!=null)
			item[2]=items.get(2).getItem();
			LoaderClass.nmsProvider.openAnvilGUI(player,container=LoaderClass.nmsProvider.createContainer(inv, player),title,item);
			containers.put(player, container);
			LoaderClass.plugin.gui.put(player.getName(), this);
		}
	}

	@Override
	public Object getContainer(Player player) {
		return containers.get(player);
	}
	
	public String getRenameText() {
		return text;
	}
	
	public final void setTitle(String title) {
		title=StringUtils.colorize(title);
		if(TheAPI.isOlderThan(9)) {
			if(title.length() >= 32) {
				title=title.substring(0, 32);
			}
		}
		if(title.equals(this.title))return;
		this.title=title;
		for(Entry<Player, Object> ec : containers.entrySet()) {
			LoaderClass.nmsProvider.setGUITitle(ec.getKey(),ec.getValue(), "minecraft:anvil",0,title);
			for(int i = 0; i < 3; ++i)
				if(items.get(i)!=null)
					LoaderClass.nmsProvider.setSlot(ec.getValue(), i, LoaderClass.nmsProvider.asNMSItem(items.get(i).getItem()));
		}
	}
	
	public final String getTitle() {
		return title;
	}

	/**
	 * @return Map<Slot, Item>
	 * 
	 */
	public final Map<Integer, ItemGUI> getItemGUIs() {
		return items;
	}

	/**
	 * @return Collection<Player>
	 * 
	 */
	public final Collection<Player> getPlayers() {
		return containers.keySet();
	}

	/**
	 * @return boolean
	 * 
	 */
	public final boolean hasOpen(Player player) {
		return containers.keySet().contains(player);
	}

	/**
	 * @apiNote Close opened gui for all players
	 * 
	 */
	public final void close() {
		close(containers.keySet().toArray(new Player[0]));
	}

	/**
	 * @apiNote Clear all registered information about gui
	 * 
	 */
	public final void clear() {
		items.clear();
		inv.clear();
	}

	/**
	 * @apiNote Close opened gui for specified player
	 * 
	 */
	public final void close(Player... players) {
		if(players==null)return;
		for(Player player : players) {
			if(player==null)continue;
			onPreClose(player);
			Object ac = containers.remove(player);
			if(ac!=null)
				LoaderClass.nmsProvider.closeGUI(player, ac, true);
			LoaderClass.plugin.gui.remove(player.getName());
			onClose(player);
		}
	}

	@Override
	public void closeWithoutPacket(Player... p) {
		if(p==null)return;
		for (Player player : p) {
			if(player==null)continue;
			onPreClose(player);
			Object ac = containers.remove(player);
			if(ac!=null)
				LoaderClass.nmsProvider.closeGUI(player, ac, false);
			LoaderClass.plugin.gui.remove(player.getName());
			onClose(player);
		}
	}

	public final String toString() {
		StringBuilder items = new StringBuilder();
		for (Entry<Integer, ItemGUI> g : getItemGUIs().entrySet()) {
			items.append('/').append(g.getKey()).append(':').append(g.getValue().toString());
		}
		return "[AnvilGUI:" + title + "/" + put + "/" + 3 + items.append(']');
	}
	
	@Override
	public int size() {
		return inv.getSize();
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}

	public void setRepairText(String text) {
		this.text=text;
		for(Object o : containers.values()) {
			if(TheAPI.isNewerThan(16)) {
				Object anvil = Ref.get(o, "delegate");
				for(int i = 0; i < 2; ++i)
					TheAPI.getNmsProvider().setSlot(anvil, i, TheAPI.getNmsProvider().getSlotItem(o, i));
				Ref.invoke(anvil, "a", text);
			}else {
				Ref.invoke(o, "a", text);
			}
		}
	}

	/**
	 * @apiNote Returns not interable slots via SHIFT click
	 */
	@Override
	public List<Integer> getNotInterableSlots(Player player) {
		List<Integer> list = new ArrayList<>();
		if(isInsertable())
			for(int i = 0; i < size(); ++i) {
				ItemGUI item = items.get(i);
				if(item!=null && item.isUnstealable())list.add(i);
			}else {
				for(int i = 0; i < size(); ++i) {
					list.add(i);
				}
			}
		if(!list.contains(2))
			list.add(2);
		return list;
	}
}
