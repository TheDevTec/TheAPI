package me.devtec.theapi.guiapi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.guiapi.GUI.ClickType;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class AnvilGUI implements HolderGUI {
	
	private String title;
	private final Map<Integer, ItemGUI> items = new HashMap<>();
	private final Map<Player, Object> containers = new HashMap<>();
	// Defaulty false
	private boolean put;

	public AnvilGUI(String title, Player... p) {
		title=StringUtils.colorize(title);
		if(TheAPI.isOlderThan(9)) {
			if(title.length() >= 32) {
				title=title.substring(0, 32);
			}
		}
		this.title=title;
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
	 * @see see Set menu insertable for items
	 */
	public final void setInsertable(boolean value) {
		put = value;
	}

	public final boolean isInsertable() {
		return put;
	}

	private static Method set = Ref.method(Ref.nmsOrOld("world.inventory.Container","Container"), "setItem", int.class, Ref.nmsOrOld("world.item.ItemStack","ItemStack"));
	
	/**
	 * @see see Set item on position to the gui with options
	 */
	public final void setItem(int position, ItemGUI item) {
		items.put(position, item);
		for(Entry<Player, Object> c : containers.entrySet())
			Ref.invoke(c.getValue(), set, position, NMSAPI.asNMSItem(item.getItem()));
	}

	/**
	 * @seee see Remove item from position
	 */
	public final void removeItem(int position) {
		items.remove(position);
		for(Entry<Player, Object> c : containers.entrySet())
			Ref.invoke(c.getValue(), set, position, GUI.empty);
	}

	/**
	 * @see see Remove item from position
	 */
	public final void remove(int slot) {
		removeItem(slot);
	}
	
	public final ItemStack getItem(int slot) {
		return null;
	}

	/**
	 * @see see Return ItemStack from position in gui
	 */
	public final ItemStack getItem(Player target, int slot) {
		return NMSAPI.asBukkitItem(Ref.invoke(Ref.invoke(containers.get(target), getSlot, slot),"getItem"));
	}

	/**
	 * @see see Return ItemGUI from position in gui
	 */
	public final ItemGUI getItemGUI(int slot) {
		return getItemGUIs().get(slot);
	}

	private static Object windowType = Ref.getStatic(Ref.nmsOrOld("world.inventory.Containers","Containers"), TheAPI.isNewerThan(16)?"h":"ANVIL");
	
	private static Method getAt = Ref.method(Ref.nmsOrOld("world.inventory.ContainerAccess","ContainerAccess"), "at", Ref.nmsOrOld("world.level.World","World"), Ref.nmsOrOld("core.BlockPosition","BlockPosition")),
			getSlot=Ref.method(Ref.nmsOrOld("world.inventory.ContainerAnvil","Container"), "getSlot", int.class);
	private static Constructor<?> anvil;
	static {
		if(TheAPI.isNewerThan(14)) {
			anvil=Ref.constructor(Ref.nmsOrOld("world.inventory.ContainerAnvil","ContainerAnvil"), int.class, Ref.nmsOrOld("world.entity.player.PlayerInventory","PlayerInventory"), Ref.nmsOrOld("world.inventory.ContainerAccess","ContainerAccess"));
		}else {
			if(TheAPI.isOlderThan(8))
				anvil=Ref.constructor(Ref.nmsOrOld("world.inventory.ContainerAnvil","ContainerAnvil"), Ref.nmsOrOld("world.entity.player.PlayerInventory","PlayerInventory"), Ref.nmsOrOld("world.level.World","World"), int.class, int.class, int.class, Ref.nmsOrOld("world.entity.player.EntityHuman","EntityHuman"));
			else
			anvil=Ref.constructor(Ref.nmsOrOld("world.inventory.ContainerAnvil","ContainerAnvil"), Ref.nmsOrOld("world.entity.player.PlayerInventory","PlayerInventory"), Ref.nmsOrOld("world.level.World","World"), Ref.nmsOrOld("core.BlockPosition","BlockPosition"), Ref.nmsOrOld("world.entity.player.EntityHuman","EntityHuman"));
		}
	}
	
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
			Object entityPlayer = Ref.player(player);
			int id = (int) GUI.nextCounter(entityPlayer);
			Object inv = Ref.invoke(entityPlayer, "getInventory");
			if(inv==null)inv=Ref.get(entityPlayer, "inventory");
			Object g = TheAPI.isNewerThan(14)?Ref.newInstance(anvil, id, inv, Ref.invokeNulled(getAt, Ref.get(entityPlayer, TheAPI.isNewerThan(16)?"t":"world"), Ref.invoke(entityPlayer, "getChunkCoordinates")))
					:(TheAPI.isOlderThan(8)?Ref.newInstance(anvil, inv, Ref.get(entityPlayer, TheAPI.isNewerThan(16)?"t":"world"), 0, 0, 0, entityPlayer)
							:Ref.newInstance(anvil, inv, Ref.get(entityPlayer, TheAPI.isNewerThan(16)?"t":"world"), Ref.invoke(entityPlayer, "getChunkCoordinates"), entityPlayer));
			if(TheAPI.isOlderThan(17))
			Ref.set(g, "windowId", id);
			Ref.set(g, "checkReachable", false);
			Ref.set(entityPlayer, TheAPI.isNewerThan(16)?"bV":"activeContainer", g);
			if(TheAPI.isOlderThan(15))
			Ref.set(Ref.get(g, "repairInventory")!=null?Ref.get(g, "repairInventory"):Ref.get(g, "h"),"a", TheAPI.isOlderThan(14)?NMSAPI.getFixedIChatBaseComponent(title):title);
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,8,title, 0, true));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,"minecraft:anvil",NMSAPI.getFixedIChatBaseComponent(title), 0));
			}else {
				Object obj = NMSAPI.getFixedIChatBaseComponent(title);
				Ref.set(g, "title", obj);
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id, windowType, obj));
			}
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,8,title, 0, false));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,"minecraft:anvil",NMSAPI.getFixedIChatBaseComponent(title), 0));
			}else
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id, windowType, NMSAPI.getFixedIChatBaseComponent(title)));
			Ref.invoke(g, GUI.addListener, entityPlayer);
			for(int i = 0; i < 3; ++i)
				if(items.get(i)!=null)
					Ref.invoke(g, set, i, NMSAPI.asNMSItem(items.get(i).getItem()));
			containers.put(player, g);
			LoaderClass.plugin.gui.put(player.getName(), this);
		}
	}

	@Override
	public Object getContainer(Player player) {
		return containers.get(player);
	}
	
	public String getRenameText(Player player) {
		Object c = containers.get(player);
		if(c==null)return null;
		return (String)Ref.get(c, "renameText");
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
		for(Entry<Player, Object> entry : containers.entrySet()) {
			Player player = entry.getKey();
			Object container = entry.getValue();
			int id = (int)Ref.get(container, TheAPI.isOlderThan(17)?"windowId":"j");
			if(TheAPI.isOlderThan(15))
			Ref.set(Ref.get(container, "repairInventory")!=null?Ref.get(container, "repairInventory"):Ref.get(container, "h"),"a", TheAPI.isOlderThan(14)?NMSAPI.getFixedIChatBaseComponent(title):title);
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,8,title, 0, true));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,"minecraft:anvil",NMSAPI.getFixedIChatBaseComponent(title), 0));
			}else {
				Object obj = NMSAPI.getFixedIChatBaseComponent(title);
				Ref.set(container, "title", obj);
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id, windowType, obj));
			}
			if(TheAPI.isOlderThan(8)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,8,title, 0, false));
			}else if(TheAPI.isOlderThan(14)) {
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id,"minecraft:anvil",NMSAPI.getFixedIChatBaseComponent(title), 0));
			}else
				Ref.sendPacket(player, Ref.newInstance(GUI.openWindow,id, windowType, NMSAPI.getFixedIChatBaseComponent(title)));
			for(int i = 0; i < 3; ++i)
				if(items.get(i)!=null)
					Ref.invoke(container, set, i, NMSAPI.asNMSItem(items.get(i).getItem()));
			Object f = Ref.player(player);
			Object inv = Ref.invoke(f, "getInventory");
			if(inv==null)inv=Ref.get(f, "inventory");
			Object carry = Ref.invoke(inv,"getCarried");
			if(carry!=GUI.empty) //Don't send useless packets
				Ref.sendPacket(player, Ref.newInstance(GUI.setSlot, -1, -1, carry));
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
			onPreClose(player);
			Object ac = containers.remove(player);
			if(ac!=null) {
				Ref.sendPacket(player, Ref.newInstance(GUI.closeWindow, (int)Ref.get(ac, TheAPI.isOlderThan(17)?"windowId":"j")));
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

	public final String toString() {
		String items = "";
		for (Integer g : getItemGUIs().keySet()) {
			items += "/" + g + ":" + getItemGUIs().get(g).toString();
		}
		return "[AnvilGUI:" + title + "/" + put + "/" + 3 + items + "]";
	}

	public int getSize() {
		return 2;
	}

	@Override
	public int size() {
		return 2;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}
}
