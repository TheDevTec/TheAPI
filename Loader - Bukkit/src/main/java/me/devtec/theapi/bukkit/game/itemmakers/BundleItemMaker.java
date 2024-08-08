package me.devtec.theapi.bukkit.game.itemmakers;

import me.devtec.shared.Ref;
import me.devtec.shared.annotations.Nullable;
import me.devtec.theapi.bukkit.game.ItemMaker;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BundleItemMaker extends ItemMaker {
    protected List<ItemStack> contents;

    public BundleItemMaker() {
        super(Material.getMaterial("BUNDLE"));
    }

    @Override
    public Map<String, Object> serializeToMap() {
        Map<String, Object> map = super.serializeToMap();
        if (contents != null) {
            List<Map<String, Object>> serialized = new ArrayList<>(contents.size());
            for (ItemStack content : contents)
                serialized.add(ItemMaker.of(content).serializeToMap());
            map.put("bundle.contents", serialized);
        }
        return map;
    }

    public BundleItemMaker contents(ItemStack... contents) {
        return this.contents(Arrays.asList(contents));
    }

    public BundleItemMaker contents(List<ItemStack> contents) {
        List<ItemStack> items = new ArrayList<>();
        if (contents != null)
            for (ItemStack stack : contents)
                if (stack != null && stack.getType() != Material.AIR)
                    items.add(stack);
        this.contents = items;
        return this;
    }

    @Nullable
    public List<ItemStack> getContents() {
        return contents;
    }

    @Override
    public ItemMaker clone() {
        BundleItemMaker maker = (BundleItemMaker) super.clone();
        return maker.contents(contents);
    }
    private static final Class<?> bundleMeta = Ref.getClass("org.bukkit.inventory.meta.BundleMeta");
    private static final Method setItems = Ref.method(bundleMeta, "setItems",List.class);

    @Override
    protected ItemMeta apply(ItemMeta meta) {
        if (!meta.getClass().isAssignableFrom(bundleMeta))
            return super.apply(meta);
        Ref.invoke(meta, setItems, contents);
        return super.apply(meta);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (contents != null)
            hash = hash * 33 + contents.hashCode();
        return hash;
    }
}