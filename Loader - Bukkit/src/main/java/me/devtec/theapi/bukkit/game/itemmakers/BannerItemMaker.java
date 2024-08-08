package me.devtec.theapi.bukkit.game.itemmakers;

import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BannerItemMaker extends ItemMaker {
    protected List<Pattern> patterns;

    public BannerItemMaker(XMaterial xMaterial) {
        super(xMaterial.parseMaterial());
        super.data = xMaterial.getData();
    }

    @Override
    public Map<String, Object> serializeToMap() {
        Map<String, Object> map = super.serializeToMap();
        if (patterns != null) {
            List<String> serialized = new ArrayList<>(patterns.size());
            for (Pattern pattern : patterns)
                serialized.add(pattern.getColor().name() + ":" + pattern.getPattern().name());
            map.put("banner.patterns", serialized);
        }
        return map;
    }

    public BannerItemMaker patterns(Pattern... contents) {
        return this.patterns(Arrays.asList(contents));
    }

    public BannerItemMaker patterns(List<Pattern> patterns) {
        this.patterns = patterns == null ? null : new ArrayList<>(patterns);
        return this;
    }

    @Nullable
    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public ItemMaker clone() {
        BannerItemMaker maker = (BannerItemMaker) super.clone();
        return maker.patterns(patterns);
    }

    @Override
    protected ItemMeta apply(ItemMeta meta) {
        if (!(meta instanceof BannerMeta))
            return super.apply(meta);
        BannerMeta iMeta = (BannerMeta) meta;
        if (patterns != null)
            iMeta.setPatterns(patterns);
        return super.apply(iMeta);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (patterns != null)
            hash = hash * 33 + patterns.hashCode();
        return hash;
    }

    public enum BannerColor {
        NONE(XMaterial.WHITE_BANNER), WHITE(XMaterial.WHITE_BANNER), BLACK(XMaterial.BLACK_BANNER), BLUE(XMaterial.BLUE_BANNER), BROWN(XMaterial.BROWN_BANNER), CYAN(XMaterial.CYAN_BANNER),
        GRAY(XMaterial.GRAY_BANNER), GREEN(XMaterial.GREEN_BANNER), LIGHT_BLUE(XMaterial.LIGHT_BLUE_BANNER), LIGHT_GRAY(XMaterial.LIGHT_GRAY_BANNER), LIME(XMaterial.LIME_BANNER),
        MAGENTA(XMaterial.MAGENTA_BANNER), ORANGE(XMaterial.ORANGE_BANNER), YELLOW(XMaterial.YELLOW_BANNER), RED(XMaterial.RED_BANNER), PURPLE(XMaterial.PURPLE_BANNER), PINK(XMaterial.PINK_BANNER);

        private final XMaterial m;

        BannerColor(XMaterial mat) {
            m = mat;
        }

        public static BannerColor fromType(XMaterial material) {
            switch (material) {
                case BLACK_BANNER:
                case BLACK_WALL_BANNER:
                    return BLACK;
                case BLUE_BANNER:
                case BLUE_WALL_BANNER:
                    return BLUE;
                case BROWN_BANNER:
                case BROWN_WALL_BANNER:
                    return BROWN;
                case CYAN_BANNER:
                case CYAN_WALL_BANNER:
                    return CYAN;
                case GRAY_BANNER:
                case GRAY_WALL_BANNER:
                    return GRAY;
                case GREEN_BANNER:
                case GREEN_WALL_BANNER:
                    return GREEN;
                case LIGHT_BLUE_BANNER:
                case LIGHT_BLUE_WALL_BANNER:
                    return LIGHT_BLUE;
                case LIGHT_GRAY_BANNER:
                case LIGHT_GRAY_WALL_BANNER:
                    return LIGHT_GRAY;
                case ORANGE_BANNER:
                case ORANGE_WALL_BANNER:
                    return ORANGE;
                case LIME_BANNER:
                case LIME_WALL_BANNER:
                    return LIME;
                case MAGENTA_BANNER:
                case MAGENTA_WALL_BANNER:
                    return MAGENTA;
                case PINK_BANNER:
                case PINK_WALL_BANNER:
                    return PINK;
                case PURPLE_BANNER:
                case PURPLE_WALL_BANNER:
                    return PURPLE;
                case RED_BANNER:
                case RED_WALL_BANNER:
                    return RED;
                case WHITE_BANNER:
                case WHITE_WALL_BANNER:
                    return WHITE;
                case YELLOW_BANNER:
                case YELLOW_WALL_BANNER:
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