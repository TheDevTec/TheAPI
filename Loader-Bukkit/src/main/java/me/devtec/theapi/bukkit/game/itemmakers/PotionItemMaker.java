package me.devtec.theapi.bukkit.game.itemmakers;

import me.devtec.shared.Ref;
import me.devtec.shared.annotations.Nullable;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PotionItemMaker extends ItemMaker {
    protected Color color;
    protected List<PotionEffect> effects;

    public PotionItemMaker(Material material) {
        super(material);
    }

    @Override
    public Map<String, Object> serializeToMap() {
        Map<String, Object> map = super.serializeToMap();
        if (Ref.isNewerThan(10) && color != null) {
            String hex = Integer.toHexString(color.asRGB());
            map.put("potion.color", "#" + (hex.length() > 6 ? hex.substring(2) : hex));
        }
        if (effects != null) {
            List<String> serialized = new ArrayList<>(effects.size());
            for (PotionEffect effect : effects)
                serialized.add(effect.getType().getName() + ":" + effect.getDuration() + ":" + effect.getAmplifier() + ":" + effect.isAmbient() + ":" + effect.hasParticles());
            map.put("potion.effects", serialized);
        }
        return map;
    }

    public PotionItemMaker color(Color color) {
        this.color = color;
        return this;
    }

    @Nullable
    public Color getColor() {
        return color;
    }

    public PotionItemMaker potionEffects(PotionEffect... effects) {
        return this.potionEffects(Arrays.asList(effects));
    }

    public PotionItemMaker potionEffects(List<PotionEffect> effects) {
        this.effects = effects == null ? null : new ArrayList<>(effects);
        return this;
    }

    @Nullable
    public List<PotionEffect> getPotionEffects() {
        return effects;
    }

    @Override
    public ItemMaker clone() {
        PotionItemMaker maker = (PotionItemMaker) super.clone();
        return maker.potionEffects(effects).color(color);
    }

    @Override
    protected ItemMeta apply(ItemMeta meta) {
        if (!(meta instanceof PotionMeta))
            return super.apply(meta);
        PotionMeta iMeta = (PotionMeta) meta;
        if (color != null && Ref.isNewerThan(10))
            iMeta.setColor(color);
        if (effects != null)
            for (PotionEffect effect : effects)
                iMeta.addCustomEffect(effect, true);
        return super.apply(iMeta);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        if (color != null)
            hash = hash * 33 + color.hashCode();
        if (effects != null)
            hash = hash * 33 + effects.hashCode();
        return hash;
    }

    public enum Potion {
        LINGERING(Material.getMaterial("LINGERING_POTION")), SPLASH(Material.getMaterial("SPLASH_POTION")), POTION(Material.POTION);

        private final Material m;

        Potion(Material mat) {
            m = mat;
        }

        public static Potion fromType(XMaterial material) {
            switch (material) {
                case POTION:
                    return POTION;
                case LINGERING_POTION:
                    return LINGERING;
                case SPLASH_POTION:
                    return SPLASH;
                default:
                    break;
            }
            return POTION;
        }

        public Material toMaterial() {
            return m;
        }
    }
}
