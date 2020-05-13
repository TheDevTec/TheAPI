package me.Straiker123;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.Straiker123.Events.GUIOpenEvent;
import me.Straiker123.Utils.GUIID;
import me.Straiker123.Utils.GUIID.GRunnable;

public class GUICreatorAPI {
	
	private Player p;
	public GUICreatorAPI(Player s) {
		p=s;
		g =LoaderClass.unused;
		id=new GUIID(p,g);
	}
	private GUIID id;
	private String t = "Missing name of GUI";
	public void setTitle(String title) {
		if(title!=null)
		t=title;
	}
	private int getRealSize(int o) {
	switch(o) {
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
	
	public ItemGUI createItemGUI(ItemStack item) {
		return new ItemGUI(item);
	}
	
	public void applyItemGUI(ItemGUI toApply, int position) {
		toApply.apply(this,position);
	}
	
	public Player getPlayer() {
		return p;
	}
	
	private int f = 9;
	public void setSize(int size) {
		f = getRealSize(size);
	}
	public String getID() {
		return id.getID();
	}
	
	public static enum Options{
		CANT_BE_TAKEN,
		CANT_PUT_ITEM,

		RUNNABLE_ON_INV_CLOSE,
		SENDMESSAGES_ON_INV_CLOSE,
		SENDCOMMANDS_ON_INV_CLOSE,
		
		RUNNABLE,
		SENDMESSAGES,
		SENDCOMMANDS,

		RUNNABLE_RIGHT_CLICK,
		SENDMESSAGES_RIGHT_CLICK,
		SENDCOMMANDS_RIGHT_CLICK,
		
		RUNNABLE_LEFT_CLICK,
		SENDMESSAGES_LEFT_CLICK,
		SENDCOMMANDS_LEFT_CLICK,
		
		RUNNABLE_SHIFT_WITH_RIGHT_CLICK,
		SENDMESSAGES_SHIFT_WITH_RIGHT_CLICK,
		SENDCOMMANDS_SHIFT_WITH_RIGHT_CLICK,
		
		RUNNABLE_SHIFT_WITH_LEFT_CLICK,
		SENDMESSAGES_SHIFT_WITH_LEFT_CLICK,
		SENDCOMMANDS_SHIFT_WITH_LEFT_CLICK,
		
		RUNNABLE_MIDDLE_CLICK,
		SENDMESSAGES_MIDDLE_CLICK,
		SENDCOMMANDS_MIDDLE_CLICK,
	}
	
	private HashMap<Integer,ItemStack> map = new HashMap<Integer,ItemStack>();
	
	private static ItemStack createWrittenBook(ItemStack a) {
		Material ms = Material.matchMaterial("WRITABLE_BOOK");
		if(ms==null)ms=Material.matchMaterial("BOOK_AND_QUILL");
		ItemCreatorAPI s = TheAPI.getItemCreatorAPI(ms);
		 if(a.getItemMeta().hasDisplayName())
		 s.setDisplayName(a.getItemMeta().getDisplayName());
		 if(a.getItemMeta().hasLore())s.setLore(a.getItemMeta().getLore());
		 if(TheAPI.isNewVersion()
				 &&!TheAPI.getServerVersion().contains("v1_13"))
		if(a.getItemMeta().hasCustomModelData()) s.setCustomModelData(a.getItemMeta().getCustomModelData());
		 if(!TheAPI.isOlder1_9()
				 &&!TheAPI.getServerVersion().contains("v1_9")
				 &&!TheAPI.getServerVersion().contains("v1_10"))
		 s.setUnbreakable(a.getItemMeta().isUnbreakable());
		 return s.create();
	}
	
	private static ItemStack createHead(ItemStack a) {
		ItemCreatorAPI s = TheAPI.getItemCreatorAPI(Material.matchMaterial("LEGACY_SKULL_ITEM") != null ? Material.matchMaterial("LEGACY_SKULL_ITEM") : Material.matchMaterial("SKULL_ITEM"));
		 if(a.getItemMeta().hasDisplayName())
		 s.setDisplayName(a.getItemMeta().getDisplayName());
		 if(a.getItemMeta().hasLore())s.setLore(a.getItemMeta().getLore());
		 if(TheAPI.isNewVersion()
				 &&!TheAPI.getServerVersion().contains("v1_13"))
		if(a.getItemMeta().hasCustomModelData()) s.setCustomModelData(a.getItemMeta().getCustomModelData());
		 if(!TheAPI.isOlder1_9()
				 &&!TheAPI.getServerVersion().contains("v1_9")
				 &&!TheAPI.getServerVersion().contains("v1_10"))
		 s.setUnbreakable(a.getItemMeta().isUnbreakable());
		 return s.create();
	}
	private static ConfigAPI g;
	/**
	 * @see see Set item on position to the gui with options
	 * @param options
	 * CANT_PUT_ITEM - Global, can player put to the gui item from his inventory (true/false).
	 * CANT_BE_TAKEN - Can player take item from gui (true/false).
	 * 
	 * RUNNABLE - Ignoring click type, run everything in runnable (Runnable).
	 * SENDMESSAGES - Ignoring click type, send list of messages to the player (List<String>).
	 * SENDCOMMANDS - Ignoring click type, send list of commands as console (List<String>).
	 */
	public void setItem(int position, ItemStack item, HashMap<Options, Object> options) {
		if(isOpened())
			inv.setItem(position, item);
		else
		map.put(position,item);
		for(Options a:options.keySet()) {
			switch(a) {
			case CANT_PUT_ITEM:
				g.set("guis."+p.getName()+"."+getID()+".PUT", options.get(a));
				break;
			case CANT_BE_TAKEN:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".TAKE", options.get(a));
				break;
			case RUNNABLE:
					id.setRunnable(GRunnable.RUNNABLE, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".MSG", options.get(a));
				break;
			case SENDCOMMANDS:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".CMD", options.get(a));
				break;
				

			case RUNNABLE_ON_INV_CLOSE:
				id.setRunnable(GRunnable.RUNNABLE_ON_INV_CLOSE, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_ON_INV_CLOSE:
				g.set("guis."+p.getName()+"."+getID()+".MSGCLOSE", options.get(a));
				break;
			case SENDCOMMANDS_ON_INV_CLOSE:
				g.set("guis."+p.getName()+"."+getID()+".CMDCLOSE", options.get(a));
				break;

			case RUNNABLE_LEFT_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_LEFT_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_LEFT_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".MSGLC", options.get(a));
				break;
			case SENDCOMMANDS_LEFT_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".CMDLC", options.get(a));
				break;

			case RUNNABLE_RIGHT_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_RIGHT_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_RIGHT_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".MSGRC", options.get(a));
				break;
			case SENDCOMMANDS_RIGHT_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".CMDRC", options.get(a));
				break;

			case RUNNABLE_MIDDLE_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_MIDDLE_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_MIDDLE_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".MSGMC", options.get(a));
				break;
			case SENDCOMMANDS_MIDDLE_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".CMDMC", options.get(a));
				break;

			case RUNNABLE_SHIFT_WITH_LEFT_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_SHIFT_WITH_LEFT_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_SHIFT_WITH_LEFT_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".MSGSLC", options.get(a));
				break;
			case SENDCOMMANDS_SHIFT_WITH_LEFT_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".CMDSLC", options.get(a));
				break;

			case RUNNABLE_SHIFT_WITH_RIGHT_CLICK:
				id.setRunnable(GRunnable.RUNNABLE_SHIFT_WITH_RIGHT_CLICK, position, (Runnable) options.get(a));
				break;
			case SENDMESSAGES_SHIFT_WITH_RIGHT_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".MSGWRC", options.get(a));
				break;
			case SENDCOMMANDS_SHIFT_WITH_RIGHT_CLICK:
				g.set("guis."+p.getName()+"."+getID()+"."+position+".CMDWRC", options.get(a));
				break;
				
			}
		}
		if(item.getType().name().equals("WRITTEN_BOOK")||item.getType().name().equals("BOOK_AND_QUILL"))
			g.set("guis."+p.getName()+"."+getID()+"."+position+".i", createWrittenBook(item));
		else
			if(item.getType().name().equals("LEGACY_SKULL_ITEM")||
					item.getType().name().equals("SKULL_ITEM")
					||item.getType().name().equals("PLAYER_HEAD"))
				g.set("guis."+p.getName()+"."+getID()+"."+position+".i", createHead(item));
			else
		g.set("guis."+p.getName()+"."+getID()+"."+position+".i", item);
	}

	/**
	 * @see see Add item to the first empty slot in gui
	 * @param item
	 * Item in gui, you can use instance geItemCreatorAPI to create item
	 */
	public void addItem(ItemStack item) {
		if(getFirstEmpty()!=-1)
		setItem(getFirstEmpty(), item);
	}
	public boolean isOpened() {
		return p.getOpenInventory().getTopInventory() != null && inv != null;
	}
	private Inventory inv;
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
		return getFirstEmpty()==-1;
	}
	
	/**
	 * @see see return -1 mean in menu isn't empty slot
	 * @return int where is empty slot (if available)
	 */
	public int getFirstEmpty() {
		if(isOpened())return inv.firstEmpty();
		int i = -1;
		boolean find=false;
		for(int a=0; a<f; ++a) {
			if(find)break;
			if(map.get(a)==null) {
				i=a;
				find=true;
			}
		}
		return i;
	}
	/**
	 * @see see Add item to the first empty slot in gui with options
	 * @param options
	 * CANT_PUT_ITEM - Global, can player put to the gui item from his inventory (true/false)
	 * CANT_BE_TAKEN - Can player take item from gui (true/false)
	 * 
	 * RUNNABLE - Ignoring click type, run everything in runnable (Runnable)
	 * SENDMESSAGES - Ignoring click type, send list of messages to the player (List<String>)
	 * SENDCOMMANDS - Ignoring click type, send list of commands as console (List<String>)
	 */
	public void addItem(ItemStack item, HashMap<Options, Object> options) {
		if(getFirstEmpty()!=-1)
		setItem(getFirstEmpty(), item, options);
	}
	

	/**
	 * @see see Set item on position to the gui
	 * @param position
	 * Position in gui
	 * @param item
	 * Item in gui, you can use instance ItemCreatorAPI to create item
	 */
	public void setItem(int position, ItemStack item) {
		if(isOpened())
			inv.setItem(position, item);
		else
		map.put(position,item);
		if(item.getType().name().equals("WRITTEN_BOOK")||item.getType().name().equals("BOOK_AND_QUILL"))
		g.set("guis."+p.getName()+"."+getID()+"."+position+".i", createWrittenBook(item));
		else
			if(item.getType().name().equals("LEGACY_SKULL_ITEM")||
					item.getType().name().equals("SKULL_ITEM")
					||item.getType().name().equals("PLAYER_HEAD"))
			g.set("guis."+p.getName()+"."+getID()+"."+position+".i", createHead(item));
			else
		g.set("guis."+p.getName()+"."+getID()+"."+position+".i", item);
	}

	/**
	 * @see see Open GUI menu
	 * 
	 */
	public void open() {
		Inventory i = Bukkit.createInventory(p, f,TheAPI.colorize(t));
		for(Integer a : map.keySet()) {
			i.setItem(a, map.get(a));
		}
		g.set("guis."+p.getName()+"."+getID()+".t", t);
		GUIOpenEvent e = new GUIOpenEvent(p,i,TheAPI.colorize(t));
		Bukkit.getPluginManager().callEvent(e);
		if(!e.isCancelled()) {
			inv=i;
			id.setInv(i);
			p.openInventory(i);
			LoaderClass.gui.put(p, id);
		}else {
		g.set("guis."+p.getName()+"."+getID(), null);
		id.clear();
		}
	}

	/**
	 * @see see Close opened gui
	 * 
	 */
	public void close() {
		id.clear();
		inv=null;
		p.getOpenInventory().close();
	}
	
}
