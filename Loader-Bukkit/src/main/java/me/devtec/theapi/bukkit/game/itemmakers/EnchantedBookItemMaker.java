package me.devtec.theapi.bukkit.game.itemmakers;

import me.devtec.shared.Ref;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.game.EnchantmentAPI;
import me.devtec.theapi.bukkit.game.ItemMaker;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

public class EnchantedBookItemMaker extends ItemMaker {
    public EnchantedBookItemMaker() {
        super(Material.ENCHANTED_BOOK);
    }

    @Override
    protected ItemMeta apply(ItemMeta meta) {
        if (!(meta instanceof EnchantmentStorageMeta))
            return super.apply(meta);
        EnchantmentStorageMeta iMeta = (EnchantmentStorageMeta) meta;
        if (super.displayName != null)
            iMeta.setDisplayName(super.displayName);
        if (super.lore != null)
            iMeta.setLore(super.lore);
        if (Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() >= 4)
            meta.setEnchantmentGlintOverride(true);
        else {
            if (super.itemFlags != null) {
                super.itemFlags.add("HIDE_ENCHANTS");
                super.itemFlags.add("HIDE_ATTRIBUTES");
            } else
                itemFlags("HIDE_ENCHANTS", "HIDE_ATTRIBUTES");
            if (enchants == null || enchants.isEmpty())
                enchant(EnchantmentAPI.DURABILITY.getEnchantment(), 1);
        }
        if (super.enchants != null)
            for (Entry<Enchantment, Integer> s : super.enchants.entrySet())
                iMeta.addStoredEnchant(s.getKey(), s.getValue(), true);
        if (Ref.isNewerThan(7) && super.itemFlags != null) {
            List<ItemFlag> flags = new ArrayList<>();
            for (String flag : itemFlags)
                try {
                    ItemFlag iFlag = ItemFlag.valueOf(flag.toUpperCase());
                    flags.add(iFlag);
                } catch (NoSuchFieldError | Exception ignored) {
                    BukkitLoader.getPlugin(BukkitLoader.class).getLogger().warning("ItemFlag '"+flag+"' is not a valid item flag. Valid item flags are: "+ Arrays.asList(ItemFlag.values()));
                }
            meta.addItemFlags(flags.toArray(new ItemFlag[0]));
        }
        if (Ref.isNewerThan(13) && super.customModel != 0)
            iMeta.setCustomModelData(super.customModel);
        if (super.unbreakable)
            if (Ref.isNewerThan(10))
                iMeta.setUnbreakable(true);
            else
                try {
                    Ref.invoke(Ref.invoke(meta, "spigot"), "setUnbreakable", true);
                } catch (NoSuchFieldError | Exception e2) {
                    // unsupported
                }
        return iMeta;
    }
}