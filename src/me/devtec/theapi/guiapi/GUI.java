package me.devtec.theapi.guiapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.thapiutils.LoaderClass;
import net.minecraft.server.v1_16_R3.NonNullList;

public class GUI {
	public static final int LINES_6 = 54;
	public static final int LINES_5 = 45;
	public static final int LINES_4 = 36;
	public static final int LINES_3 = 27;
	public static final int LINES_2 = 18;
	public static final int LINES_1 = 9;
	
	public static enum ClickType {
		MIDDLE_PICKUP, MIDDLE_DROP, RIGHT_PICKUP, RIGHT_DROP, LEFT_PICKUP, SHIFT_LEFT_DROP, SHIFT_RIGHT_PICKUP, SHIFT_RIGHT_DROP, SHIFT_LEFT_PICKUP
	}
	
	private String title;
	private final Map<Integer, ItemGUI> items = new HashMap<>();
	private final Map<Player, Object> containers = new HashMap<>();
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
		windowType = Ref.invokeStatic(getType, inv);
		setTitle(title);
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

	private static Constructor<?> openWindow, closeWindow = Ref.constructor(Ref.nms("PacketPlayOutCloseWindow"), int.class), containerClass,
			setSlot=Ref.constructor(Ref.nms("PacketPlayOutSetSlot"), int.class, int.class, Ref.nms("ItemStack")),
			itemsS=Ref.getConstructors(Ref.nms("PacketPlayOutWindowItems"))[0];
	static {
		if(TheAPI.isOlderThan(8)) {
			openWindow=Ref.constructor(Ref.nms("PacketPlayOutOpenWindow"), int.class, int.class, String.class, int.class, boolean.class);
		}else if(TheAPI.isOlderThan(14)) {
			openWindow=Ref.constructor(Ref.nms("PacketPlayOutOpenWindow"), int.class, String.class, Ref.nms("IChatBaseComponent"), int.class);
		}else {
			openWindow=Ref.constructor(Ref.nms("PacketPlayOutOpenWindow"), int.class, Ref.nms("Containers"), Ref.nms("IChatBaseComponent"));
		}
		containerClass=Ref.constructor(Ref.craft("inventory.CraftContainer"), Inventory.class, Ref.nms("EntityHuman"), int.class);
	}
	private static Method getType = Ref.method(Ref.craft("inventory.CraftContainer"),"getNotchInventoryType", Inventory.class),
			transfer=Ref.method(Ref.nms("Container"),"transferTo", Ref.nms("Container"), Ref.craft("entity.CraftHumanEntity"));
	private Object windowType;
	
	/**
	 * @see see Open GUI menu to player
	 * 
	 */
	public final void open(Player... players) {
		for (Player player : players) {
			int window = containers.containsKey(player)?(int) Ref.get(containers.get(player), "windowId"):-1;
			if (LoaderClass.plugin.gui.containsKey(player.getName()+":"+window)) {
				GUI a = LoaderClass.plugin.gui.get(player.getName()+":"+window);
				LoaderClass.plugin.gui.remove(player.getName()+":"+window);
				a.onClose(player);
			}
			int id = (int) Ref.invoke(Ref.player(player), "nextContainerCounter");
			Object container = Ref.newInstance(containerClass, inv, Ref.player(player), id);
			Object active = Ref.get(Ref.player(player), "activeContainer");
			Ref.invoke(active, transfer, container, Ref.cast(Ref.craft("entity.CraftHumanEntity"), player));
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,Ref.get(Ref.player(player),"containerCounter"),0,title, Ref.invoke(container,"getSize"), false));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,Ref.get(Ref.player(player),"containerCounter"),"minecraft:container",NMSAPI.getIChatBaseComponentFromCraftBukkit(title), Ref.invoke(container,"getSize")));
			}else {
				Ref.sendPacket(player, Ref.newInstance(openWindow,Ref.get(container,"windowId"), windowType, NMSAPI.getIChatBaseComponentFromCraftBukkit(title)));
			}
			Ref.set(Ref.player(player), "activeContainer", container);
			Ref.invoke(container, Ref.method(Ref.nms("Container"), "addSlotListener", Ref.nms("ICrafting")), Ref.cast(Ref.nms("ICrafting"), Ref.player(player)));
			Ref.set(container, "checkReachable", false);
			inv.getViewers().add(player);
			containers.put(player, container);
			LoaderClass.plugin.gui.put(player.getName()+":"+id, this);
		}
	}
	
	private static Object empty = Ref.getStatic(Ref.nms("ItemStack"), "b");
	
	public final void setTitle(String title) {
		title=StringUtils.colorize(title);
		this.title=title;
		for(HumanEntity player : inv.getViewers()) {
			Object container = containers.get(player);
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket((Player)player, Ref.newInstance(openWindow,Ref.get(Ref.player((Player)player),"containerCounter"),0,title, inv.getSize(), false));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket((Player)player, Ref.newInstance(openWindow,Ref.get(Ref.player((Player)player),"containerCounter"),"minecraft:container",NMSAPI.getIChatBaseComponentFromCraftBukkit(title), inv.getSize()));
			}else {
				Ref.sendPacket((Player)player, Ref.newInstance(openWindow,Ref.get(container,"windowId"), windowType, NMSAPI.getIChatBaseComponentFromCraftBukkit(title)));
			}
			Ref.sendPacket((Player)player, Ref.newInstance(itemsS, Ref.get(container, "windowId"), Ref.get(container,"items")));
			Object carry = Ref.invoke(Ref.get(Ref.player((Player)player),"inventory"),"getCarried");
			if(carry!=empty) //Don't send useless packets
				Ref.sendPacket((Player)player, Ref.newInstance(setSlot, -1, -1, carry));
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
	 * @return List<HumanEntity>
	 * 
	 */
	public final List<HumanEntity> getPlayers() {
		return inv.getViewers();
	}

	/**
	 * @return List<Player>
	 * 
	 */
	public final boolean hasOpen(Player player) {
		return inv.getViewers().contains(player);
	}

	/**
	 * @see see Close opened gui for all players
	 * 
	 */
	public final void close() {
		for(HumanEntity player : new ArrayList<>(inv.getViewers()))
			close((Player)player);
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
		if(players==null)return;
		for (Player player : players) {
			if(player==null)continue;
			Object ac = containers.remove(player);
			if(ac!=null) {
				Ref.sendPacket(player, Ref.newInstance(closeWindow, Ref.get(ac, "windowId")));
				Ref.invoke(ac, "b", Ref.player(player));
				Ref.set(Ref.player(player), "activeContainer", Ref.get(Ref.player(player), "defaultContainer"));
				Ref.invoke(ac, transfer, Ref.get(Ref.player(player), "defaultContainer"), Ref.cast(Ref.craft("entity.CraftHumanEntity"), player));
			}
			inv.getViewers().remove(player);
			LoaderClass.plugin.gui.remove(player.getName()+":"+Ref.get(ac, "windowId"));
			onClose(player);
		}
	}

	public final String toString() {
		String items = "";
		for (Integer g : getItemGUIs().keySet()) {
			items += "/" + g + ":" + getItemGUIs().get(g).toString();
		}
		return "[GUI:" + title + "/" + put + "/" + inv.getSize() + items + "]";
	}

	public int getSize() {
		return inv.getSize();
	}

	@SuppressWarnings("unchecked")
	public NonNullList<net.minecraft.server.v1_16_R3.ItemStack> getNMSItems() {
		return (NonNullList<net.minecraft.server.v1_16_R3.ItemStack>) Ref.get(Ref.get(containers.values().iterator().next(),"delegate"),"items");
	}
}
