package me.devtec.theapi.guiapi;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class AnvilGUI implements HolderGUI {
	
	private String title;
	private final Map<Integer, ItemGUI> items = new HashMap<>();
	private final Map<Integer, ItemStack> inv = new HashMap<>();
	private final Map<Player, Object> containers = new HashMap<>();
	// Defaulty false
	private boolean put;

	public AnvilGUI(String title, Player... p) {
		title=StringUtils.colorize(title);
		this.title=title;
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

	public final String getName() {
		return title;
	}

	public final Map<Integer, ItemStack> getInventory() {
		return inv;
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
		inv.put(position, item.getItem());
		for(Entry<Player, Object> p : containers.entrySet()) {
			Ref.sendPacket(p.getKey(), Ref.newInstance(setSlot, Ref.get(p.getValue(), "windowId"),position, NMSAPI.asNMSItem(item.getItem())));
			Ref.invoke(p.getValue(), Ref.method(Ref.nms("Container"), "setItem", int.class, Ref.nms("ItemStack")), position, NMSAPI.asNMSItem(item.getItem()));
		}
	}

	/**
	 * @see see Remove item from position
	 */
	public final void removeItem(int slot) {
		items.remove(slot);
		inv.remove(slot);
		for(Entry<Player, Object> p : containers.entrySet()) {
			Ref.sendPacket(p.getKey(), Ref.newInstance(setSlot, Ref.get(p.getValue(), "windowId"),slot, GUI.empty));
			Ref.invoke(p.getValue(), Ref.method(Ref.nms("Container"), "setItem", int.class, Ref.nms("ItemStack")), slot, GUI.empty);
		}
	}

	/**
	 * @see see Remove item from position
	 */
	public final void remove(int slot) {
		removeItem(slot);
	}

	/**
	 * @see see Return ItemStack from position in gui
	 */
	public final ItemStack getItem(int slot) {
		return inv.getOrDefault(slot, new ItemStack(Material.AIR));
	}

	/**
	 * @see see Return ItemGUI from position in gui
	 */
	public final ItemGUI getItemGUI(int slot) {
		return getItemGUIs().getOrDefault(slot, null);
	}

	private static Constructor<?> createAnvil = Ref.constructor(Ref.nms("ContainerAnvil"), int.class, Ref.nms("PlayerInventory"), Ref.nms("ContainerAccess")),
			setSlot=Ref.constructor(Ref.nms("PacketPlayOutSetSlot"), int.class, int.class, Ref.nms("ItemStack")),
			itemsS=Ref.getConstructors(Ref.nms("PacketPlayOutWindowItems"))[0];
	private static Object windowType = Ref.getStatic(Ref.nms("Containers"), "ANVIL");
	private final int size=2;
	private static Object zero = new Position("world",0,0,0).getBlockPosition();
	
	/**
	 * @see see Open GUI menu to player
	 * 
	 */
	public final void open(Player... players) {
		for(Player player : players) {
		if (LoaderClass.plugin.gui.containsKey(player.getName())) {
			HolderGUI a = LoaderClass.plugin.gui.get(player.getName());
			LoaderClass.plugin.gui.remove(player.getName());
			a.onClose(player);
		}
		Object aw = Ref.player(player);
		int id = (int) Ref.invoke(aw, "nextContainerCounter");
		Object container = Ref.newInstance(createAnvil, id, Ref.get(aw, "inventory"), Ref.invokeStatic(Ref.method(Ref.nms("ContainerAccess"), "at", Ref.nms("World"), Ref.nms("BlockPosition")), Ref.get(aw,"world"), zero));
		Object active = Ref.get(aw, "activeContainer");
		Ref.invoke(active, GUI.transfer, container, Ref.cast(Ref.craft("entity.CraftHumanEntity"), player));
		if(TheAPI.isOlderThan(8)) {
			Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,8,title, size, false));
		}else if(TheAPI.isOlderThan(14)) {
			Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,"minecraft:anvil",NMSAPI.getFixedIChatBaseComponent(title), size));
		}else {
			Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id, windowType, NMSAPI.getFixedIChatBaseComponent(title)));
		}
		Ref.set(aw, "activeContainer", container);
		Ref.invoke(container, GUI.addListener, aw);
		Ref.set(container, "checkReachable", false);
		containers.put(player, container);
		LoaderClass.plugin.gui.put(player.getName(), this);
		}
	}

	@Override
	public Object getContainer(Player player) {
		return containers.get(player);
	}
	
	public String getRenameText(Player player) {
		if(containers.get(player)==null)return null;
		return (String)Ref.get(containers.get(player), "renameText");
	}
	
	public final void setTitle(String title) {
		title=StringUtils.colorize(title);
		this.title=title;
		for(Entry<Player, Object> entry : this.containers.entrySet()) {
			Player player = entry.getKey();
			Object container = entry.getValue();
			int id = (int)Ref.get(container,"windowId");
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,8,title, size, false));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,"minecraft:anvil",NMSAPI.getFixedIChatBaseComponent(title), size));
			}else
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id, windowType, NMSAPI.getFixedIChatBaseComponent(title)));
			Ref.sendPacket(player, Ref.newInstance(itemsS,id, Ref.get(container,"items")));
			Object carry = Ref.invoke(Ref.get(Ref.player(player),"inventory"),"getCarried");
			if(carry!=GUI.empty) //Don't send useless packets
				Ref.sendPacket(player, Ref.newInstance(setSlot, -1, -1, carry));
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
	 * @see see Close opened gui for all players
	 * 
	 */
	public final void close() {
		close(containers.keySet().toArray(new Player[containers.size()]));
	}

	/**
	 * @see see Clear all registered informations about gui
	 * 
	 */
	public final void clear() {
		items.clear();
	}

	/**
	 * @see see Close opened gui for specified player
	 * 
	 */
	public final void close(Player... players) {
		if(players==null)return;
		for(Player player : players) {
			if(player==null)continue;
			Object ac = containers.remove(player);
			if(ac!=null) {
				Ref.sendPacket(player, Ref.newInstance(GUI.closeWindow, (int)Ref.get(ac, "windowId")));
				Object d = Ref.player(player);
				Ref.invoke(ac, "b", d);
				Ref.set(Ref.player(player), "activeContainer", Ref.get(d, "defaultContainer"));
				Ref.invoke(ac, GUI.transfer, Ref.get(d, "defaultContainer"), Ref.cast(Ref.craft("entity.CraftHumanEntity"), player));
			}
			LoaderClass.plugin.gui.remove(player.getName());
			onClose(player);
		}
	}

	@Override
	public void closeWithoutPacket(Player... p) {
		if(p==null)return;
		for (Player player : p) {
			if(player==null)continue;
			Object ac = containers.remove(player);
			if(ac!=null) {
				Object d = Ref.player(player);
				Ref.invoke(ac, "b", d);
				Ref.set(Ref.player(player), "activeContainer", Ref.get(d, "defaultContainer"));
				Ref.invoke(ac, GUI.transfer, Ref.get(d, "defaultContainer"), Ref.cast(Ref.craft("entity.CraftHumanEntity"), player));
			}
			LoaderClass.plugin.gui.remove(player.getName());
			onClose(player);
		}
	}

	public final String toString() {
		String items = "";
		for (Integer g : getItemGUIs().keySet()) {
			items += "/" + g + ":" + getItemGUIs().get(g).toString();
		}
		return "[GUI:" + title + "/" + put + "/" + 3 + items + "]";
	}

	public int getSize() {
		return size;
	}

	@Override
	public int size() {
		return size;
	}
}
