package me.devtec.theapi.bukkit.game.itemmakers;

import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me.devtec.shared.annotations.Nullable;
import me.devtec.theapi.bukkit.game.ItemMaker;

public class LeatherItemMaker extends ItemMaker {
    protected Color color;

    public LeatherItemMaker(Material material) {
        super(material);
    }

    @Override
    public Map<String, Object> serializeToMap() {
        Map<String, Object> map = super.serializeToMap();
        if (color != null) {
            String hex = Integer.toHexString(color.asRGB());
            map.put("leather.color", "#" + (hex.length() > 6 ? hex.substring(2) : hex));
        }
        return map;
    }

    public LeatherItemMaker color(Color color) {
        this.color = color;
        return this;
    }

    @Nullable
    public Color getColor() {
        return color;
    }

    @Override
    public ItemMaker clone() {
        LeatherItemMaker maker = (LeatherItemMaker) super.clone();
        maker.color = color;
        return maker;
    }

    @Override
    protected ItemMeta apply(ItemMeta meta) {
        if (!(meta instanceof LeatherArmorMeta)) {
			return super.apply(meta);
		}
        LeatherArmorMeta iMeta = (LeatherArmorMeta) meta;
        if (color != null) {
			iMeta.setColor(color);
		}
        return super.apply(iMeta);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (color != null) {
			hash = hash * 33 + color.hashCode();
		}
        return hash;
    }
}