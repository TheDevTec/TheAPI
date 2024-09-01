package me.devtec.theapi.bukkit.gui;

import me.devtec.shared.Ref;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.dataholder.StringContainer;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @apiNote Anvil GUI menu
 */
public class AnvilGUI implements HolderGUI {

    private String title;
    private final Map<Integer, ItemGUI> items = new ConcurrentHashMap<>();
    private final Map<Player, Object> containers = new ConcurrentHashMap<>();
    private final Inventory inv;
    // Default false
    private boolean put;
    private String text = "";

    public AnvilGUI(String original, Player... p) {
        title = ColorUtils.colorize(original);
        if (Ref.isOlderThan(9) && title.length() >= 32)
            title = title.substring(0, 32);
        inv = Bukkit.createInventory(null, InventoryType.ANVIL, title);
        open(p);
    }

    /**
     * @apiNote Actions before close gui
     */
    @Override
    public void onPreClose(Player player) {
        // Before gui is closed actions
    }

    /**
     * @apiNote Actions on close gui
     */
    @Override
    public void onClose(Player player) {
        // Closed gui actions
    }

    @Override
    public boolean onInteractItem(Player player, ItemStack newItem, ItemStack oldItem, ClickType type, int slot, boolean gui) {
        return false;
    }

    @Override
    public void onMultipleIteract(Player player, Map<Integer, ItemStack> guiSlots, Map<Integer, ItemStack> playerSlots) {

    }

    public final String getName() {
        return title;
    }

    /**
     * @apiNote Set menu insertable for items
     */
    @Override
    public final void setInsertable(boolean value) {
        put = value;
    }

    @Override
    public final boolean isInsertable() {
        return put;
    }

    /**
     * @apiNote Set item on position to the gui with options
     */
    @Override
    public final void setItem(int position, ItemGUI item) {
        items.put(position, item);
        if (position < size())
            inv.setItem(position, item.getItem());
        if (position == 0)
            text = item.getItem() != null && item.getItem().hasItemMeta() && item.getItem().getItemMeta().hasDisplayName() ? item.getItem().getItemMeta().getDisplayName() : "";
    }

    /**
     * @apiNote Remove item from position
     */
    public final void removeItem(int position) {
        items.remove(position);
        if (position < size())
            inv.setItem(position, null);
    }

    /**
     * @apiNote Remove item from position
     */
    @Override
    public final void remove(int slot) {
        removeItem(slot);
    }

    @Override
    public final ItemStack getItem(int slot) {
        return inv.getItem(slot);
    }

    /**
     * @apiNote Return ItemGUI from position in gui
     */
    @Override
    public final ItemGUI getItemGUI(int slot) {
        return getItemGUIs().get(slot);
    }

    /**
     * @apiNote Open GUI menu to player
     */
    public final void open(Player... players) {
        Component titleComp = ComponentAPI.fromString(title);
        for (Player player : players) {
            if (JavaPlugin.getPlugin(BukkitLoader.class).gui.containsKey(player.getUniqueId())) {
                HolderGUI a = JavaPlugin.getPlugin(BukkitLoader.class).gui.get(player.getUniqueId());
                JavaPlugin.getPlugin(BukkitLoader.class).gui.remove(player.getUniqueId());
                a.onClose(player);
            }
            Object container;
            BukkitLoader.getNmsProvider().openAnvilGUI(player, container = BukkitLoader.getNmsProvider().createContainer(inv, player), titleComp);
            containers.put(player, container);
            JavaPlugin.getPlugin(BukkitLoader.class).gui.put(player.getUniqueId(), this);
        }
    }

    @Override
    public Object getContainer(Player player) {
        return containers.get(player);
    }

    public String getRenameText() {
        return text;
    }

    @Override
    public final void setTitle(String value) {
        value = ColorUtils.colorize(value);
        if (Ref.isOlderThan(9) && value.length() >= 32)
            value = value.substring(0, 32);
        if (title.equals(value))
            return;
        title = value;
        Component titleComp = ComponentAPI.fromString(title);
        for (Entry<Player, Object> ec : containers.entrySet()) {
            BukkitLoader.getNmsProvider().setGUITitle(ec.getKey(), ec.getValue(), "minecraft:anvil", 0, titleComp);
            for (int i = 0; i < 3; ++i)
                if (items.get(i) != null)
                    BukkitLoader.getNmsProvider().setSlot(ec.getValue(), i, BukkitLoader.getNmsProvider().asNMSItem(items.get(i).getItem()));
        }
    }

    @Override
    public final String getTitle() {
        return title;
    }

    /**
     * @return Map<Slot, Item>
     */
    @Override
    public final Map<Integer, ItemGUI> getItemGUIs() {
        return items;
    }

    /**
     * @return Collection<Player>
     */
    @Override
    public final Collection<Player> getPlayers() {
        return containers.keySet();
    }

    /**
     * @return boolean
     */
    public final boolean hasOpen(Player player) {
        return containers.containsKey(player);
    }

    /**
     * @apiNote Close opened gui for all players
     */
    @Override
    public final void close() {
        this.close(containers.keySet().toArray(new Player[0]));
    }

    /**
     * @apiNote Clear all registered information about gui
     */
    @Override
    public final void clear() {
        items.clear();
        inv.clear();
    }

    /**
     * @apiNote Close opened gui for specified player
     */
    @Override
    public final void close(Player... players) {
        if (players == null)
            return;
        for (Player player : players) {
            if (player == null)
                continue;
            onPreClose(player);
            Object container = containers.remove(player);
            if (container != null)
                BukkitLoader.getNmsProvider().closeGUI(player, container, true);
            JavaPlugin.getPlugin(BukkitLoader.class).gui.remove(player.getUniqueId());
            onClose(player);
        }
    }

    @Override
    public void closeWithoutPacket(Player... p) {
        if (p == null)
            return;
        for (Player player : p) {
            if (player == null)
                continue;
            onPreClose(player);
            Object ac = containers.remove(player);
            if (ac != null)
                BukkitLoader.getNmsProvider().closeGUI(player, ac, false);
            JavaPlugin.getPlugin(BukkitLoader.class).gui.remove(player.getUniqueId());
            onClose(player);
        }
    }

    @Override
    public final String toString() {
        StringContainer items = new StringContainer(128);
        for (Entry<Integer, ItemGUI> g : getItemGUIs().entrySet())
            items.append('/').append(g.getKey()).append(':').append(g.getValue().toString());
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
        this.text = text;
        for (Object o : containers.values())
            if (Ref.isNewerThan(16)) {
                for (int i = 0; i < 2; ++i)
                    BukkitLoader.getNmsProvider().setSlot(o, i, BukkitLoader.getNmsProvider().getSlotItem(o, i));
                Ref.invoke(o, BukkitLoader.NO_OBFUSCATED_NMS_MODE ? "setItemName" : "a", text);
            } else
                Ref.invoke(o, "a", text);
    }

    /**
     * @apiNote Returns not interactable slots via SHIFT click
     */
    @Override
    public List<Integer> getNotInterableSlots(Player player) {
        List<Integer> list = new ArrayList<>();
        if (isInsertable())
            for (int i = 0; i < size(); ++i) {
                ItemGUI item = items.get(i);
                if (item != null && item.isUnstealable())
                    list.add(i);
            }
        else
            for (int i = 0; i < size(); ++i)
                list.add(i);
        if (!list.contains(2))
            list.add(2);
        return list;
    }
}
