package me.DevTec.GUI;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import me.DevTec.ConfigAPI;
import me.DevTec.ItemCreatorAPI;
import me.DevTec.TheAPI;
import me.DevTec.Events.GUIOpenEvent;
import me.DevTec.GUI.GUIID.GRunnable;
import me.DevTec.Other.LoaderClass;

public class GUICreatorAPI {
	private final GUIID id;
	private final String title;
	private final HashMap<Integer, ItemGUI> actions = Maps.newHashMap();
	private final List<Player> opened = Lists.newArrayList();
	private final Inventory inv;
	
	public GUICreatorAPI(String title, int size, Player... p) {
		id = new GUIID(this, g);
		LoaderClass.plugin.gui.add(id);
		g.set("guis." +  getID() + ".t", title);
		this.title = TheAPI.colorize(title);
		inv = Bukkit.createInventory(null, getRealSize(size), this.title);
		id.setInv(inv);
		for(Player s : p) {
		GUIOpenEvent e = new GUIOpenEvent(s, inv, this.title);
		Bukkit.getPluginManager().callEvent(e);
		if (!e.isCancelled()) {
			if(g.exist("pgui."+s.getName())) {
				for(GUIID d : LoaderClass.plugin.gui) {
					if(d.getID().equals(g.getString("pgui."+s.getName())))
						d.getGUI().close(s);
				}
			}
			g.set("pgui." +  s.getName(), getID());
			s.openInventory(inv);
			opened.add(s);
		}}
	}

	private int getRealSize(int o) {
		switch (o) {
		case 8:
		case 9:
			return 9;
		case 17:
		case 18:
			return 18;
		case 26:
		case 27:
			return 27;
		case 35:
		case 36:
			return 36;
		case 44:
		case 45:
			return 45;
		case 53:
		case 54:
			return 54;
		default:
			return 9;
		}
	}

	public void applyItemGUI(ItemGUI toApply, int position) {
		actions.put(position, toApply);
		setItem(position, toApply.getItem(), toApply.getOptions());
	}

	public List<Player> getPlayers() {
		return opened;
	}

	public String getID() {
		return id.getID();
	}

	public static enum Options {
		CANT_BE_TAKEN, CANT_PUT_ITEM,

		RUNNABLE_ON_INV_CLOSE, SENDMESSAGES_ON_INV_CLOSE, SENDCOMMANDS_ON_INV_CLOSE,

		RUNNABLE, SENDMESSAGES, SENDCOMMANDS,

		RUNNABLE_RIGHT_CLICK, SENDMESSAGES_RIGHT_CLICK, SENDCOMMANDS_RIGHT_CLICK,

		RUNNABLE_LEFT_CLICK, SENDMESSAGES_LEFT_CLICK, SENDCOMMANDS_LEFT_CLICK,

		RUNNABLE_SHIFT_WITH_RIGHT_CLICK, SENDMESSAGES_SHIFT_WITH_RIGHT_CLICK, SENDCOMMANDS_SHIFT_WITH_RIGHT_CLICK,

		RUNNABLE_SHIFT_WITH_LEFT_CLICK, SENDMESSAGES_SHIFT_WITH_LEFT_CLICK, SENDCOMMANDS_SHIFT_WITH_LEFT_CLICK,

		RUNNABLE_MIDDLE_CLICK, SENDMESSAGES_MIDDLE_CLICK, SENDCOMMANDS_MIDDLE_CLICK,
	}

	private static ItemStack createWrittenBook(ItemStack a) {
		Material ms = Material.matchMaterial("WRITABLE_BOOK");
		if (ms == null)
			ms = Material.matchMaterial("BOOK_AND_QUILL");
		ItemCreatorAPI s = TheAPI.getItemCreatorAPI(ms);
		if (a.getItemMeta().hasDisplayName())
			s.setDisplayName(a.getItemMeta().getDisplayName());
		if (a.getItemMeta().hasLore())
			s.setLore(a.getItemMeta().getLore());
		if (TheAPI.isNewVersion() && !TheAPI.getServerVersion().contains("v1_13"))
			if (a.getItemMeta().hasCustomModelData())
				s.setCustomModelData(a.getItemMeta().getCustomModelData());
		if (!TheAPI.isOlder1_9() && !TheAPI.getServerVersion().contains("v1_9")
				&& !TheAPI.getServerVersion().contains("v1_10"))
			s.setUnbreakable(a.getItemMeta().isUnbreakable());
		return s.create();
	}

	private static ItemStack createHead(ItemStack a) {
		ItemCreatorAPI s = TheAPI.getItemCreatorAPI(
				Material.matchMaterial("LEGACY_SKULL_ITEM") != null ? Material.matchMaterial("LEGACY_SKULL_ITEM")
						: Material.matchMaterial("SKULL_ITEM"));
		if (a.getItemMeta().hasDisplayName())
			s.setDisplayName(a.getItemMeta().getDisplayName());
		if (a.getItemMeta().hasLore())
			s.setLore(a.getItemMeta().getLore());
		if (TheAPI.isNewVersion() && !TheAPI.getServerVersion().contains("v1_13"))
			if (a.getItemMeta().hasCustomModelData())
				s.setCustomModelData(a.getItemMeta().getCustomModelData());
		if (!TheAPI.isOlder1_9() && !TheAPI.getServerVersion().contains("v1_9")
				&& !TheAPI.getServerVersion().contains("v1_10"))
			s.setUnbreakable(a.getItemMeta().isUnbreakable());
		return s.create();
	}

	private ConfigAPI g= LoaderClass.unused;

	/**
	 * @see see Set item on position to the gui with options
	 * @param options CANT_PUT_ITEM - Global, can player put to the gui item from
	 *                his inventory (true/false). CANT_BE_TAKEN - Can player take
	 *                item from gui (true/false).
	 * 
	 *                RUNNABLE - Ignoring click type, run everything in runnable
	 *                (Runnable). SENDMESSAGES - Ignoring click type, send list of
	 *                messages to the player (List<String>). SENDCOMMANDS - Ignoring
	 *                click type, send list of commands as console (List<String>).
	 */
	public void setItem(int position, ItemCreatorAPI item, HashMap<Options, Object> options) {
		setItem(position,item.create(),options);
	}

	/**
	 * @see see Set item on position to the gui with options
	 * @param options CANT_PUT_ITEM - Global, can player put to the gui item from
	 *                his inventory (true/false). CANT_BE_TAKEN - Can player take
	 *                item from gui (true/false).
	 * 
	 *                RUNNABLE - Ignoring click type, run everything in runnable
	 *                (Runnable). SENDMESSAGES - Ignoring click type, send list of
	 *                messages to the player (List<String>). SENDCOMMANDS - Ignoring
	 *                click type, send list of commands as console (List<String>).
	 */
	public void setItem(int position, ItemStack item, HashMap<Options, Object> options) {
		inv.setItem(position, item);
		for (Options a : options.keySet()) {
			switch (a) {
			case CANT_PUT_ITEM:
				g.set("guis." + getID() + ".PUT", options.get(a));
				break;
			case CANT_BE_TAKEN:
				g.set("guis." + getID() + "." + position + ".TAKE", options.get(a));
				break;
			case RUNNABLE:
				id.setRunnable(GRunnable.RUNNABLE, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES:
				g.set("guis." + getID() + "." + position + ".MSG", options.get(a));
				break;
			case SENDCOMMANDS:
				g.set("guis." + getID() + "." + position + ".CMD", options.get(a));
				break;

			case RUNNABLE_ON_INV_CLOSE:
				id.setRunnable(GRunnable.RUNNABLE_ON_INV_CLOSE, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_ON_INV_CLOSE:
				g.set("guis." +  getID() + ".MSGCLOSE", options.get(a));
				break;
			case SENDCOMMANDS_ON_INV_CLOSE:
				g.set("guis." +  getID() + ".CMDCLOSE", options.get(a));
				break;

			case RUNNABLE_LEFT_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_LEFT_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_LEFT_CLICK:
				g.set("guis." +  getID() + "." + position + ".MSGLC", options.get(a));
				break;
			case SENDCOMMANDS_LEFT_CLICK:
				g.set("guis." +  getID() + "." + position + ".CMDLC", options.get(a));
				break;

			case RUNNABLE_RIGHT_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_RIGHT_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_RIGHT_CLICK:
				g.set("guis." +  getID() + "." + position + ".MSGRC", options.get(a));
				break;
			case SENDCOMMANDS_RIGHT_CLICK:
				g.set("guis." +  getID() + "." + position + ".CMDRC", options.get(a));
				break;

			case RUNNABLE_MIDDLE_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_MIDDLE_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_MIDDLE_CLICK:
				g.set("guis." +  getID() + "." + position + ".MSGMC", options.get(a));
				break;
			case SENDCOMMANDS_MIDDLE_CLICK:
				g.set("guis." +  getID() + "." + position + ".CMDMC", options.get(a));
				break;

			case RUNNABLE_SHIFT_WITH_LEFT_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_SHIFT_WITH_LEFT_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_SHIFT_WITH_LEFT_CLICK:
				g.set("guis." +  getID() + "." + position + ".MSGSLC", options.get(a));
				break;
			case SENDCOMMANDS_SHIFT_WITH_LEFT_CLICK:
				g.set("guis." +  getID() + "." + position + ".CMDSLC", options.get(a));
				break;

			case RUNNABLE_SHIFT_WITH_RIGHT_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_SHIFT_WITH_RIGHT_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_SHIFT_WITH_RIGHT_CLICK:
				g.set("guis." +  getID() + "." + position + ".MSGWRC", options.get(a));
				break;
			case SENDCOMMANDS_SHIFT_WITH_RIGHT_CLICK:
				g.set("guis." +  getID() + "." + position + ".CMDWRC", options.get(a));
				break;

			}
		}
		if (item.getType().name().equals("WRITTEN_BOOK") || item.getType().name().equals("BOOK_AND_QUILL"))
			g.set("guis." +  getID() + "." + position + ".i", createWrittenBook(item));
		else if (item.getType().name().equals("LEGACY_SKULL_ITEM") || item.getType().name().equals("SKULL_ITEM")
				|| item.getType().name().equals("PLAYER_HEAD"))
			g.set("guis." +  getID() + "." + position + ".i", createHead(item));
		else
			g.set("guis." +  getID() + "." + position + ".i", item);
	}

	public void removeItem(int slot) {
		g.set("guis." +  getID() + "." + slot, null);
		inv.setItem(slot, new ItemStack(Material.AIR));
	}

	/**
	 * @see see Add item to the first empty slot in gui
	 * @param item Item in gui, you can use instance geItemCreatorAPI to create item
	 */
	public void addItem(ItemStack item) {
		if (getFirstEmpty() != -1)
			setItem(getFirstEmpty(), item);
	}

	/**
	 *
	 * @return opened gui
	 */
	public Inventory getGUI() {
		return inv;
	}

	/**
	 *
	 * @return boolean is gui full
	 */
	public boolean isFull() {
		return getFirstEmpty() == -1;
	}

	/**
	 * @see see return -1 mean in menu isn't empty slot
	 * @return int where is empty slot (if available)
	 */
	public int getFirstEmpty() {
		return inv.firstEmpty();
	}

	/**
	 * @see see Add item to the first empty slot in gui with options
	 * @param options CANT_PUT_ITEM - Global, can player put to the gui item from
	 *                his inventory (true/false) CANT_BE_TAKEN - Can player take
	 *                item from gui (true/false)
	 * 
	 *                RUNNABLE - Ignoring click type, run everything in runnable
	 *                (Runnable) SENDMESSAGES - Ignoring click type, send list of
	 *                messages to the player (List<String>) SENDCOMMANDS - Ignoring
	 *                click type, send list of commands as console (List<String>)
	 */
	public void addItem(ItemStack item, HashMap<Options, Object> options) {
		if (getFirstEmpty() != -1)
			setItem(getFirstEmpty(), item, options);
	}

	/**
	 * @see see Set item on position to the gui
	 * @param position Position in gui
	 * @param item     Item in gui, you can use instance ItemCreatorAPI to create
	 *                 item
	 */
	public void setItem(int position, ItemStack item) {
		inv.setItem(position, item);
		if (item.getType().name().equals("WRITTEN_BOOK") || item.getType().name().equals("BOOK_AND_QUILL"))
			g.set("guis." +  getID() + "." + position + ".i", createWrittenBook(item));
		else if (item.getType().name().equals("LEGACY_SKULL_ITEM") || item.getType().name().equals("SKULL_ITEM")
				|| item.getType().name().equals("PLAYER_HEAD"))
			g.set("guis." +  getID() + "." + position + ".i", createHead(item));
		else
			g.set("guis." +  getID() + "." + position + ".i", item);
	}
	
	public void setItem(int position, ItemCreatorAPI item) {
		setItem(position,item.create());
	}

	/**
	 * @see see Open GUI menu to another player
	 * 
	 */
	public void open(Player player) {
	GUIOpenEvent e = new GUIOpenEvent(player, inv, TheAPI.colorize(title));
	Bukkit.getPluginManager().callEvent(e);
	if (!e.isCancelled()) {
		player.openInventory(inv);
		opened.add(player);
	}
	}
	
	public HashMap<Integer, ItemGUI> getItemGUIs(){
		return actions;
	}

	/**
	 * @see see Close opened gui for all players
	 * 
	 */
	public void close() {
		for(Player s : opened)
			if(s.getOpenInventory()==inv)
		s.getOpenInventory().close();
		opened.clear();
	}
	
	/**
	 * @see see Close opened gui for specified player
	 * 
	 */
	public void close(Player player) {
			if(player.getOpenInventory()==inv)
				player.getOpenInventory().close();
			opened.remove(player);
	}

}
