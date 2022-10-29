package me.devtec.theapi.bukkit.gui;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.devtec.theapi.bukkit.gui.GUI.ClickType;

public interface HolderGUI {
	/**
	 * @apiNote Get menu title
	 */
	public String getTitle();

	/**
	 * @apiNote Change menu title
	 */
	public void setTitle(String title);

	/**
	 * @apiNote Get menu size
	 */
	public int size();

	/**
	 * @apiNote Get list of players which have opened this menu
	 */
	public Collection<Player> getPlayers();

	/**
	 * @apiNote Set ItemGUI to the menu
	 */
	public void setItem(int slot, ItemGUI item);

	/**
	 * @apiNote Get ItemGUI from the ItemGUIs Map
	 */
	public ItemGUI getItemGUI(int slot);

	/**
	 * @apiNote Get ItemStack from the Bukkit Inventory
	 */
	public ItemStack getItem(int slot);

	/**
	 * @apiNote This method is called before is called packet to close window (if
	 *          not client-side)
	 */
	public void onPreClose(Player player);

	/**
	 * @apiNote This method is called after packet to close window
	 */
	public void onClose(Player player);

	/**
	 * @apiNote When someone interact with GUI menu or Player's inventory
	 * @return Return cancel statement (return true to cancel event)
	 */
	public boolean onInteractItem(Player player, ItemStack newItem, ItemStack oldItem, ClickType type, int slot, boolean gui);

	/**
	 * @apiNote Can be menu modified?
	 */
	public boolean isInsertable();

	/**
	 * @apiNote Allow or disallow to users modify this menu
	 */
	public void setInsertable(boolean value);

	/**
	 * @apiNote Remove ItemStack & ItemGUI from menu
	 */
	public void remove(int slot);

	/**
	 * @apiNote Close menu to specified players
	 */
	public void close(Player... players);

	/**
	 * @apiNote Close menu to specified players
	 */
	public default void close(Collection<? extends Player> players) {
		close(players.toArray(new Player[0]));
	}

	/**
	 * @apiNote Close menu to all players
	 */
	public void close();

	/**
	 * @apiNote Remove from menu all ItemStacks and ItemGUIs
	 */
	public void clear();

	/**
	 * @apiNote Get inventory container (nms) of Player
	 */
	public Object getContainer(Player player);

	/**
	 * @apiNote Close menu to specified players without packet (Don't call this
	 *          method if you don't know what are you doing.)
	 */
	public void closeWithoutPacket(Player... p);

	/**
	 * @apiNote Get Bukkit Inventory of menu
	 */
	public Inventory getInventory();

	/**
	 * @apiNote Get Map of ItemGUIs in the menu
	 */
	public Map<Integer, ItemGUI> getItemGUIs();

	/**
	 * @apiNote List of slots which can't be modified by user
	 */
	public List<Integer> getNotInterableSlots(Player player);

	/**
	 * 
	 * @apiNote Create new {@link GUI} menu. Use constructor of {@link GUI} class
	 *          instead.
	 */
	@Deprecated
	public static GUI ofChest(String title, int size, Player... players) {
		return new GUI(title, size, players);
	}

	/**
	 * 
	 * @apiNote Create new {@link AnvilGUI} menu. Use constructor of
	 *          {@link AnvilGUI} class instead.
	 */
	@Deprecated
	public static AnvilGUI ofAnvil(String title, Player... players) {
		return new AnvilGUI(title, players);
	}
}
