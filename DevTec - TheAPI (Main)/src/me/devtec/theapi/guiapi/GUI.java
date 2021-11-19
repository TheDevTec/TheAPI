package me.devtec.theapi.guiapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.components.ComponentAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class GUI implements HolderGUI {
	public static final int LINES_6 = 54;
	public static final int LINES_5 = 45;
	public static final int LINES_4 = 36;
	public static final int LINES_3 = 27;
	public static final int LINES_2 = 18;
	public static final int LINES_1 = 9;
	
	public enum ClickType {
		MIDDLE_PICKUP, MIDDLE_DROP, LEFT_DROP, RIGHT_PICKUP, RIGHT_DROP, LEFT_PICKUP, SHIFT_LEFT_DROP, SHIFT_RIGHT_PICKUP, SHIFT_RIGHT_DROP, SHIFT_LEFT_PICKUP
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
		if(TheAPI.isOlderThan(9)) {
			if(title.length() >= 32) {
				title=title.substring(0, 32);
			}
		}
		this.title=title;
		inv = Bukkit.createInventory(null, size, this.title);
		if(TheAPI.isNewerThan(13))
		switch (size) {
		case 9 : {
			windowType = Ref.getStatic(Ref.nmsOrOld("world.inventory.Containers","Containers"),TheAPI.isNewerThan(16)?"a":"GENERIC_9X1");
			break;
		}
		case 18 : {
			windowType = Ref.getStatic(Ref.nmsOrOld("world.inventory.Containers","Containers"),TheAPI.isNewerThan(16)?"b":"GENERIC_9X2");
			break;
		}
		case 27 : {
			windowType = Ref.getStatic(Ref.nmsOrOld("world.inventory.Containers","Containers"),TheAPI.isNewerThan(16)?"c":"GENERIC_9X3");
			break;
		}
		case 36 : {
			windowType = Ref.getStatic(Ref.nmsOrOld("world.inventory.Containers","Containers"),TheAPI.isNewerThan(16)?"d":"GENERIC_9X4");
			break;
		}
		case 45 : {
			windowType = Ref.getStatic(Ref.nmsOrOld("world.inventory.Containers","Containers"),TheAPI.isNewerThan(16)?"e":"GENERIC_9X5");
			break;
		}
		case 54 : {
			windowType = Ref.getStatic(Ref.nmsOrOld("world.inventory.Containers","Containers"),TheAPI.isNewerThan(16)?"f":"GENERIC_9X6");
			break;
		}
		}
		open(p);
	}

	public void onPreClose(Player player) {
	}

	public void onClose(Player player) {
	}

	public boolean onIteractItem(Player player, ItemStack item, ClickType type, int slot, boolean gui) {
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
		if(TheAPI.isNewerThan(16))
			for(Entry<Player, Object> p : containers.entrySet())
				Ref.sendPacket(p.getKey(), airplane==1?Ref.newInstance(setSlot,Ref.get(p.getValue(),"j"),position,position, LoaderClass.nmsProvider.asNMSItem(item.getItem())):Ref.newInstance(setSlot,Ref.get(p.getValue(),"j"),position, LoaderClass.nmsProvider.asNMSItem(item.getItem())));
	}

	/**
	 * @apiNote Remove item from position
	 */
	public final void removeItem(int slot) {
		items.remove(slot);
		inv.setItem(slot, null);
		if(TheAPI.isNewerThan(16))
			for(Entry<Player, Object> p : containers.entrySet())
				Ref.sendPacket(p.getKey(), airplane==1?Ref.newInstance(setSlot,Ref.get(p.getValue(),"j"),slot,slot, LoaderClass.nmsProvider.asNMSItem(new ItemStack(Material.AIR))):Ref.newInstance(setSlot,Ref.get(p.getValue(),"j"),slot, LoaderClass.nmsProvider.asNMSItem(new ItemStack(Material.AIR))));
	}

	/**
	 * @apiNote Remove item from position
	 */
	public final void remove(int slot) {
		removeItem(slot);
	}

	/**
	 * @apiNote Add item to the first empty slot in gui
	 */
	public final void addItem(ItemGUI item) {
		if (getFirstEmpty() != -1)
			setItem(getFirstEmpty(), item);
	}

	/**
	 * @apiNote Add item to the first empty slot in gui
	 */
	public final void add(ItemGUI item) {
		addItem(item);
	}

	/**
	 * @apiNote Return ItemStack from position in gui
	 */
	public final ItemStack getItem(int slot) {
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
	 *
	 * @return boolean is gui full
	 */
	public final boolean isFull() {
		return getFirstEmpty() == -1;
	}

	/**
	 * @apiNote -1 mean menu is full
	 * @return int return first empty slot (if available)
	 */
	public final int getFirstEmpty() {
		return inv.firstEmpty();
	}

	static int airplane;
	protected static Constructor<?> openWindow;
	protected static final Constructor<?> closeWindow = Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutCloseWindow","PacketPlayOutCloseWindow"), int.class);
	protected static Constructor<?> containerClass;
	protected static Constructor<?> setSlot=Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutSetSlot","PacketPlayOutSetSlot"), int.class, int.class, Ref.nmsOrOld("world.item.ItemStack","ItemStack"));
	protected static int type;
	protected static final Method
	transfer=Ref.method(Ref.nmsOrOld("world.inventory.Container","Container"),"transferTo", Ref.nmsOrOld("world.inventory.Container","Container"), Ref.craft("entity.CraftHumanEntity"));
	private Object windowType;
	static {
		if(setSlot==null) {
			++airplane;
			setSlot=Ref.findConstructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutSetSlot","PacketPlayOutSetSlot"), int.class, int.class, Ref.nmsOrOld("world.item.ItemStack","ItemStack"));
		}
		if(TheAPI.isOlderThan(8)) {
			openWindow=Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutOpenWindow","PacketPlayOutOpenWindow"), int.class, int.class, String.class, int.class, boolean.class);
		}else if(TheAPI.isOlderThan(14)) {
			openWindow=Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutOpenWindow","PacketPlayOutOpenWindow"), int.class, String.class, Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"), int.class);
		}else {
			openWindow=Ref.constructor(Ref.nmsOrOld("network.protocol.game.PacketPlayOutOpenWindow","PacketPlayOutOpenWindow"), int.class, Ref.nmsOrOld("world.inventory.Containers","Containers"), Ref.nmsOrOld("network.chat.IChatBaseComponent","IChatBaseComponent"));
		}
		containerClass=Ref.constructor(Ref.craft("inventory.CraftContainer"), Inventory.class, Ref.nmsOrOld("world.entity.player.EntityHuman","EntityHuman"), int.class);
		if(containerClass==null) {
			++type;
			containerClass=Ref.constructor(Ref.craft("inventory.CraftContainer"), Inventory.class, HumanEntity.class, int.class);
		}
	}
	
	/**
	 * @apiNote Open GUI menu to player
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
			int id = nextCounter(f);
			Object container = type==0?Ref.newInstance(containerClass, inv, f, id):Ref.newInstance(containerClass, inv, player, id);
			Map<Integer, Object> map = new HashMap<>();
			for(int i = 0; i < inv.getSize(); ++i) {
				ItemStack is = inv.getItem(i);
				if(is==null||is.getType()==Material.AIR)continue;
				Object item = null;
				Ref.invoke(Ref.invoke(container, "getSlot", i),"set", item=LoaderClass.nmsProvider.asNMSItem(is));
				map.put(i,  item);
			}
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id,0,title, getSize(), false));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id,"minecraft:container",ComponentAPI.toIChatBaseComponent(title, true), getSize()));
			}else {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id, windowType, ComponentAPI.toIChatBaseComponent(title, true)));
			}
			for(Entry<Integer,Object> o : map.entrySet()) 
				Ref.sendPacket(player, airplane==1?Ref.newInstance(setSlot,id,id,o.getKey(), o.getValue()):Ref.newInstance(setSlot,id,o.getKey(), o.getValue()));
			Ref.set(container, "windowId", id);
			Ref.set(f, TheAPI.isNewerThan(16)?"bV":"activeContainer", container);
			Ref.invoke(container, addListener, f);
			Ref.set(container, "checkReachable", false);
			containers.put(player, container);
			LoaderClass.plugin.gui.put(player.getName(), this);
		}
	}
	
	static final Method next = Ref.method(Ref.nmsOrOld("server.level.EntityPlayer", "EntityPlayer"), "nextContainerCounter");
	static final Method getItems=Ref.method(Ref.nmsOrOld("world.inventory.Container", "Container"), TheAPI.isNewerThan(16)?"c":TheAPI.isNewerThan(13)?"b":"a");
	static final Field nextF = Ref.field(Ref.nmsOrOld("server.level.EntityPlayer", "EntityPlayer"), TheAPI.isNewerThan(16)?"cY":"containerCounter");
	
	protected static int nextCounter(Object f) {
		try {
			return (int)Ref.invoke(f, next);
		}catch(Exception ex) {
			int containerCounter = (int)Ref.get(f, nextF);
		    Ref.set(f, nextF, (containerCounter = containerCounter % 100 + 1));
			return containerCounter;
		}
	}

	protected static final Method addListener = Ref.method(Ref.nmsOrOld("world.inventory.Container","Container"), "addSlotListener", Ref.nmsOrOld("world.inventory.ICrafting","ICrafting"));
	protected static final Object empty = Ref.getStatic(Ref.nmsOrOld("world.item.ItemStack","ItemStack"), "b");

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
			Player player = ec.getKey();
			Object container = ec.getValue();
			int id = TheAPI.isOlderThan(17)?(int)Ref.get(container,"windowId"):(int)Ref.get(container,"j");
			Object f= Ref.player(player);
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id,0,title, inv.getSize(), false));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id,"minecraft:chest",ComponentAPI.toIChatBaseComponent(title, true), inv.getSize()));
			}else {
				Ref.sendPacket(player, Ref.newInstance(openWindow,id, windowType, ComponentAPI.toIChatBaseComponent(title, true)));
			}
			int slot = 0;
			for(Object o : (Collection<?>)Ref.invoke(container,getItems))
				Ref.sendPacket(player, airplane==1?Ref.newInstance(setSlot,id,id,slot++, o):Ref.newInstance(setSlot,id,slot++, o));
			Object inv = Ref.invoke(f, "getInventory");
			if(inv==null)inv=Ref.get(f, "inventory");
			Object carry = Ref.invoke(inv,"getCarried");
			if(carry!=empty) //Don't send useless packets
				Ref.sendPacket(player, airplane==1?Ref.newInstance(setSlot, -1, -1, -1, carry):Ref.newInstance(setSlot, -1, -1, carry));
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
		inv.clear();
		items.clear();
	}

	/**
	 * @apiNote Close opened gui for specified player
	 * 
	 */
	public final void close(Player... players) {
		if(players==null)return;
		for (Player player : players) {
			if(player==null)continue;
			onPreClose(player);
			Object ac = containers.remove(player);
			if(ac!=null) {
				Ref.sendPacket(player, Ref.newInstance(closeWindow, TheAPI.isOlderThan(17)?Ref.get(ac,"windowId"):Ref.get(ac,"j")));
				Object d = Ref.player(player);
				Ref.set(Ref.player(player), TheAPI.isNewerThan(16)?"bV":"activeContainer", Ref.get(d, TheAPI.isNewerThan(16)?"bU":"defaultContainer"));
				Ref.invoke(ac, GUI.transfer, Ref.get(d, TheAPI.isNewerThan(16)?"bU":"defaultContainer"), Ref.cast(Ref.craft("entity.CraftHumanEntity"), player));
			}
			LoaderClass.plugin.gui.remove(player.getName());
			onClose(player);
		}
	}

	public final String toString() {
		StringBuilder items = new StringBuilder();
		for (Integer g : getItemGUIs().keySet()) {
			items.append('/').append(g).append(':').append(getItemGUIs().get(g).toString());
		}
		return "[GUI:" + title + "/" + put + "/" + inv.getSize() + items.append(']');
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
			onPreClose(player);
			Object ac = containers.remove(player);
			if(ac!=null) {
				Object d = Ref.player(player);
				Ref.invoke(ac, "b", d);
				Ref.set(Ref.player(player), TheAPI.isNewerThan(16)?"bV":"activeContainer", Ref.get(d, TheAPI.isNewerThan(16)?"bU":"defaultContainer"));
				Ref.invoke(ac, GUI.transfer, Ref.get(d, TheAPI.isNewerThan(16)?"bU":"defaultContainer"), Ref.cast(Ref.craft("entity.CraftHumanEntity"), player));
			}
			LoaderClass.plugin.gui.remove(player.getName());
			onClose(player);
		}
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}
}
