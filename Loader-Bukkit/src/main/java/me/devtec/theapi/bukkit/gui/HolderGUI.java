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
    String getTitle();

    /**
     * @apiNote Change menu title
     */
    void setTitle(String title);

    /**
     * @apiNote Get menu size
     */
    int size();

    /**
     * @apiNote Get list of players which have opened this menu
     */
    Collection<Player> getPlayers();

    /**
     * @apiNote Set ItemGUI to the menu
     */
    void setItem(int slot, ItemGUI item);

    /**
     * @apiNote Get ItemGUI from the ItemGUIs Map
     */
    ItemGUI getItemGUI(int slot);

    /**
     * @apiNote Get ItemStack from the Bukkit Inventory
     */
    ItemStack getItem(int slot);

    /**
     * @apiNote This method is called before is called packet to close window (if
     * not client-side)
     */
    void onPreClose(Player player);

    /**
     * @apiNote This method is called after packet to close window
     */
    void onClose(Player player);

    /**
     * @apiNote When someone interact with GUI menu or Player's inventory with
     * multiple slots in the same time (Like SHIFT, DRAG or PICKUP_ALL)
     */
    void onMultipleIteract(Player player, Map<Integer, ItemStack> guiSlots, Map<Integer, ItemStack> playerSlots);

    /**
     * @return Return cancel statement (return true to cancel event)
     * @apiNote When someone interact with GUI menu or Player's inventory
     */
    boolean onInteractItem(Player player, ItemStack newItem, ItemStack oldItem, ClickType type, int slot, boolean gui);

    /**
     * @apiNote Can be menu modified?
     */
    boolean isInsertable();

    /**
     * @apiNote Allow or disallow to users modify this menu
     */
    void setInsertable(boolean value);

    /**
     * @apiNote Remove ItemStack & ItemGUI from menu
     */
    void remove(int slot);

    /**
     * @apiNote Close menu to specified players
     */
    void close(Player... players);

    /**
     * @apiNote Close menu to specified players
     */
    default void close(Collection<? extends Player> players) {
        close(players.toArray(new Player[0]));
    }

    /**
     * @apiNote Close menu to all players
     */
    void close();

    /**
     * @apiNote Remove from menu all ItemStacks and ItemGUIs
     */
    void clear();

    /**
     * @apiNote Get inventory container (nms) of Player
     */
    Object getContainer(Player player);

    /**
     * @apiNote Close menu to specified players without packet (Don't call this
     * method if you don't know what are you doing.)
     */
    void closeWithoutPacket(Player... p);

    /**
     * @apiNote Get Bukkit Inventory of menu
     */
    Inventory getInventory();

    /**
     * @apiNote Get Map of ItemGUIs in the menu
     */
    Map<Integer, ItemGUI> getItemGUIs();

    /**
     * @apiNote List of slots which can't be modified by user
     */
    List<Integer> getNotInterableSlots(Player player);

    /**
     * @apiNote Create new {@link GUI} menu. Use constructor of {@link GUI} class
     * instead.
     */
    @Deprecated
    static GUI ofChest(String title, int size, Player... players) {
        return new GUI(title, size, players);
    }

    /**
     * @apiNote Create new {@link AnvilGUI} menu. Use constructor of
     * {@link AnvilGUI} class instead.
     */
    @Deprecated
    static AnvilGUI ofAnvil(String title, Player... players) {
        return new AnvilGUI(title, players);
    }
}
