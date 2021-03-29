package me.devtec.theapi.guiapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class GUI implements HolderGUI {
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
		title=StringUtils.colorize(title);
		this.title=title;
		inv = Bukkit.createInventory(null, size, this.title);
		switch (size) {
		case 9 : {
			windowType = Ref.getStatic(Ref.nms("Containers"),"GENERIC_9X1");
			break;
		}
		case 18 : {
			windowType = Ref.getStatic(Ref.nms("Containers"),"GENERIC_9X2");
			break;
		}
		case 27 : {
			windowType = Ref.getStatic(Ref.nms("Containers"),"GENERIC_9X3");
			break;
		}
		case 41 : {
			windowType = Ref.getStatic(Ref.nms("Containers"),"GENERIC_9X4");
			break;
		}
		case 45 : {
			windowType = Ref.getStatic(Ref.nms("Containers"),"GENERIC_9X5");
			break;
		}
		case 54 : {
			windowType = Ref.getStatic(Ref.nms("Containers"),"GENERIC_9X6");
			break;
		}
		}
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

	public final String getTitle() {
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

	protected static Constructor<?> openWindow, closeWindow = Ref.constructor(Ref.nms("PacketPlayOutCloseWindow"), int.class), containerClass,
			setSlot=Ref.constructor(Ref.nms("PacketPlayOutSetSlot"), int.class, int.class, Ref.nms("ItemStack")),
			itemsS=Ref.getConstructors(Ref.nms("PacketPlayOutWindowItems"))[0];
	protected static int type;
	protected static Method
	transfer=Ref.method(Ref.nms("Container"),"transferTo", Ref.nms("Container"), Ref.craft("entity.CraftHumanEntity"));
	private Object windowType;
	static {
		if(TheAPI.isOlderThan(8)) {
			openWindow=Ref.constructor(Ref.nms("PacketPlayOutOpenWindow"), int.class, int.class, String.class, int.class, boolean.class);
		}else if(TheAPI.isOlderThan(14)) {
			openWindow=Ref.constructor(Ref.nms("PacketPlayOutOpenWindow"), int.class, String.class, Ref.nms("IChatBaseComponent"), int.class);
		}else {
			openWindow=Ref.constructor(Ref.nms("PacketPlayOutOpenWindow"), int.class, Ref.nms("Containers"), Ref.nms("IChatBaseComponent"));
		}
		containerClass=Ref.constructor(Ref.craft("inventory.CraftContainer"), Inventory.class, Ref.nms("EntityHuman"), int.class);
		if(containerClass==null) {
			++type;
			containerClass=Ref.constructor(Ref.craft("inventory.CraftContainer"), Inventory.class, HumanEntity.class, int.class);
		}
	}
	
	/**
	 * @see see Open GUI menu to player
	 * 
	 */
	public final void open(Player... players) {
		for (Player player : players) {
			if (LoaderClass.plugin.gui.containsKey(player.getName())) {
				HolderGUI a = LoaderClass.plugin.gui.get(player.getName());
				LoaderClass.plugin.gui.remove(player.getName());
				a.onClose(player);
			}
			Object f= Ref.player(player);
			int id = (int) Ref.invoke(f, "nextContainerCounter");
			Object container = type==0?Ref.newInstance(containerClass, inv, f, id):Ref.newInstance(containerClass, inv, player, id);
			Object active = Ref.get(f, "activeContainer");
			Ref.invoke(active, transfer, container, Ref.cast(Ref.craft("entity.CraftHumanEntity"), player));
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id,0,title, getSize(), false));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id,"minecraft:container",NMSAPI.getFixedIChatBaseComponent(title), getSize()));
			}else {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id, windowType, NMSAPI.getFixedIChatBaseComponent(title)));
			}
			Ref.set(f, "activeContainer", container);
			Ref.invoke(container, addListener, Ref.cast(Ref.nms("ICrafting"), f));
			Ref.set(container, "checkReachable", false);
			containers.put(player, container);
			LoaderClass.plugin.gui.put(player.getName(), this);
		}
	}
	
	protected static Method addListener = Ref.method(Ref.nms("Container"), "addSlotListener", Ref.nms("ICrafting"));
	protected static Object empty = Ref.getStatic(Ref.nms("ItemStack"), "b");
	
	public final void setTitle(String title) {
		title=StringUtils.colorize(title);
		this.title=title;
		for(Entry<Player, Object> ec : containers.entrySet()) {
			Player player = ec.getKey();
			Object container = ec.getValue();
			int id = (int)Ref.get(container,"windowId");
			Object f= Ref.player(player);
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id,0,title, inv.getSize(), false));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id,"minecraft:container",NMSAPI.getFixedIChatBaseComponent(title), inv.getSize()));
			}else {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id, windowType, NMSAPI.getFixedIChatBaseComponent(title)));
			}
			Ref.sendPacket(player, Ref.newInstance(itemsS,id, Ref.get(container,"items")));
			Object carry = Ref.invoke(Ref.get(f,"inventory"),"getCarried");
			if(carry!=empty) //Don't send useless packets
				Ref.sendPacket(player, Ref.newInstance(setSlot, -1, -1, carry));
		}
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
		return containers.containsKey(player);
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
				Ref.sendPacket(player, Ref.newInstance(closeWindow, (int)Ref.get(ac, "windowId")));
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
		return "[GUI:" + title + "/" + put + "/" + inv.getSize() + items + "]";
	}

	public int getSize() {
		return inv.getSize();
	}

	@Override
	public int size() {
		return inv.getSize();
	}

	@Override
	public Object getContainer(Player player) {
		return containers.get(player);
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
}
