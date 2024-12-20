package me.devtec.theapi.bukkit.game.itemmakers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import me.devtec.shared.annotations.Nullable;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.xseries.XMaterial;

public class ShulkerBoxItemMaker extends ItemMaker {
    protected String name;
    protected ItemStack[] contents;

    public ShulkerBoxItemMaker(XMaterial xMaterial) {
        super(xMaterial.parseMaterial());
        super.data = xMaterial.getData();
    }

    @Override
    public Map<String, Object> serializeToMap() {
        Map<String, Object> map = super.serializeToMap();
        if (name != null) {
			map.put("shulker.name", name);
		}
        if (contents != null) {
            List<Map<String, Object>> serialized = new ArrayList<>(contents.length);
            for (ItemStack content : contents) {
				if (content == null || content.getType() == Material.AIR) {
					serialized.add(null);
				} else {
					serialized.add(ItemMaker.of(content).serializeToMap());
				}
			}
            map.put("shulker.contents", serialized);
        }
        return map;
    }

    public ShulkerBoxItemMaker name(String name) {
        this.name = name;
        return this;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public ShulkerBoxItemMaker contents(ItemStack[] contents) {
        ItemStack[] copy = new ItemStack[27];
        if (contents != null) {
			for (int i = 0; i < 27 && i < contents.length; ++i) {
				copy[i] = contents[i];
			}
		}
        this.contents = copy;
        return this;
    }

    @Nullable
    public ItemStack[] getContents() {
        return contents;
    }

    @Override
    public ItemMaker clone() {
        ShulkerBoxItemMaker maker = (ShulkerBoxItemMaker) super.clone();
        return maker.name(name).contents(contents);
    }

    @Override
    protected ItemMeta apply(ItemMeta meta) {
        if (!(meta instanceof BlockStateMeta)) {
			return super.apply(meta);
		}
        BlockStateMeta iMeta = (BlockStateMeta) meta;
        ShulkerBox shulker = (ShulkerBox) iMeta.getBlockState();
        if (name != null) {
			shulker.setCustomName(name);
		}
        if (contents != null) {
			shulker.getInventory().setContents(contents);
		}
        iMeta.setBlockState(shulker);
        return super.apply(iMeta);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (name != null) {
			hash = hash * 33 + name.hashCode();
		}
        if (contents != null) {
			hash = hash * 33 + Arrays.hashCode(contents);
		}
        return hash;
    }

    public enum ShulkerBoxColor {
        NONE(XMaterial.SHULKER_BOX), WHITE(XMaterial.WHITE_SHULKER_BOX), BLACK(XMaterial.BLACK_SHULKER_BOX), BLUE(XMaterial.BLUE_SHULKER_BOX), BROWN(XMaterial.BROWN_SHULKER_BOX),
        CYAN(XMaterial.CYAN_SHULKER_BOX), GRAY(XMaterial.GRAY_SHULKER_BOX), GREEN(XMaterial.GREEN_SHULKER_BOX), LIGHT_BLUE(XMaterial.LIGHT_BLUE_SHULKER_BOX),
        LIGHT_GRAY(XMaterial.LIGHT_GRAY_SHULKER_BOX), LIME(XMaterial.LIME_SHULKER_BOX), MAGENTA(XMaterial.MAGENTA_SHULKER_BOX), ORANGE(XMaterial.ORANGE_SHULKER_BOX),
        YELLOW(XMaterial.YELLOW_SHULKER_BOX), RED(XMaterial.RED_SHULKER_BOX), PURPLE(XMaterial.PURPLE_SHULKER_BOX), PINK(XMaterial.PINK_SHULKER_BOX);

        private final XMaterial m;

        ShulkerBoxColor(XMaterial mat) {
            m = mat;
        }

        public static ShulkerBoxColor fromType(XMaterial material) {
            switch (material) {
                case BLACK_SHULKER_BOX:
                    return BLACK;
                case BLUE_SHULKER_BOX:
                    return BLUE;
                case BROWN_SHULKER_BOX:
                    return BROWN;
                case CYAN_SHULKER_BOX:
                    return CYAN;
                case GRAY_SHULKER_BOX:
                    return GRAY;
                case GREEN_SHULKER_BOX:
                    return GREEN;
                case LIGHT_BLUE_SHULKER_BOX:
                    return LIGHT_BLUE;
                case LIGHT_GRAY_SHULKER_BOX:
                    return LIGHT_GRAY;
                case ORANGE_SHULKER_BOX:
                    return ORANGE;
                case LIME_SHULKER_BOX:
                    return LIME;
                case MAGENTA_SHULKER_BOX:
                    return MAGENTA;
                case PINK_SHULKER_BOX:
                    return PINK;
                case PURPLE_SHULKER_BOX:
                    return PURPLE;
                case RED_SHULKER_BOX:
                    return RED;
                case WHITE_SHULKER_BOX:
                    return WHITE;
                case YELLOW_SHULKER_BOX:
                    return YELLOW;
                default:
                    break;
            }
            return NONE;
        }

        public XMaterial toMaterial() {
            return m;
        }
    }
}