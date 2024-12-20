package me.devtec.theapi.bukkit.game;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import me.devtec.shared.Ref;
import me.devtec.shared.annotations.Nonnull;
import me.devtec.shared.annotations.Nullable;

public enum EnchantmentAPI {
    SHARPNESS("DAMAGE_ALL", "SHARPNESS"), DAMAGEALL("DAMAGE_ALL", "SHARPNESS"), ALLDAMAGE("DAMAGE_ALL", "SHARPNESS"), DAMAGE_ALL("DAMAGE_ALL", "SHARPNESS"),

    DAMAGE_ARTHROPODS("DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPODS"), ARTHROPODS("DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPODS"), BANEOFARTHROPODS("DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPODS"),
    BANE_OF_ARTHROPODS("DAMAGE_ARTHROPODS", "BANE_OF_ARTHROPODS"),

    EFFICIENCY("DIG_SPEED", "EFFICIENCY"), DIG_SPEED("DIG_SPEED", "EFFICIENCY"), DIGSPEED("DIG_SPEED", "EFFICIENCY"), SPEEDDIG("DIG_SPEED", "EFFICIENCY"),

    CHANNELING("CHANNELING", "CHANNELING", 13), DEPTH_STRIDER("DEPTH_STRIDER", "DEPTH_STRIDER", 8),

    FROST_WALKER("FROST_WALKER", "FROST_WALKER", 9),

    KNOCKBACK("KNOCKBACK", "KNOCKBACK"),

    MULTISHOT("MULTISHOT", "MULTISHOT", 14),

    PIERCING("PIERCING", "PIERCING", 13),

    LOYALTY("LOYALTY", "LOYALTY", 13),

    RIPTIDE("RIPTIDE", "RIPTIDE", 13),

    QUICK_CHARGE("QUICK_CHARGE", "QUICK_CHARGE", 14),

    THORNS("THORNS", "THORNS"),

    SILK_TOUCH("SILK_TOUCH", "SILK_TOUCH"),

    SWEEPING_EDGE("SWEEPING_EDGE", "SWEEPING_EDGE", 11),

    MENDING("MENDING", "MENDING", 9), REPEAIRING("MENDING", "MENDING", 9),

    ARROW_FIRE("ARROW_FIRE", "FLAME"), FIRE("ARROW_FIRE", "FLAME"), FLAME("ARROW_FIRE", "FLAME"),

    FIREASPECT("FIRE_ASPECT", "FIRE_ASPECT"), FIRE_ASPECT("FIRE_ASPECT", "FIRE_ASPECT"),

    INFINITY("ARROW_INFINITE", "INFINITE", 11), ARROW_INFINITE("ARROW_INFINITE", "INFINITE", 11),

    IMPALING("IMPALING", "IMPALING", 13),

    LURE("LURE", "LURE", 7),

    LUCK("LUCK", "LUCK_OF_THE_SEA", 7), LUCK_OF_SEA("LUCK", "LUCK_OF_THE_SEA", 7), LUCKOFSEA("LUCK", "LUCK_OF_THE_SEA", 7), LUCK_OF_THE_SEA("LUCK", "LUCK_OF_THE_SEA", 7),

    FORTUNE("LOOT_BONUS_BLOCKS", "FORTUNE"), LOOTBLOCKS("LOOT_BONUS_BLOCKS", "FORTUNE"), LOOT_BONUS_BLOCKS("LOOT_BONUS_BLOCKS", "FORTUNE"),

    RESPIRATION("OXYGEN", "RESPIRATION"), OXYGEN("OXYGEN", "RESPIRATION"),

    UNBREAKING("DURABILITY", "UNBREAKING"), DURABILITY("DURABILITY", "UNBREAKING"),

    AQUA_AFFINITY("WATER_WORKER", "AQUA_AFFINITY"), WATER_WORKER("WATER_WORKER", "AQUA_AFFINITY"),

    PROTECTION("PROTECTION_ENVIRONMENTAL", "PROTECTION"), PROTECTION_ENVIRONMENTAL("PROTECTION_ENVIRONMENTAL", "PROTECTION"),

    BLAST_PROTECTION("PROTECTION_EXPLOSIONS", "BLAST_PROTECTION"), PROTECTION_EXPLOSIONS("PROTECTION_EXPLOSIONS", "BLAST_PROTECTION"),

    FEATHER_FALLING("PROTECTION_FALL", "FEATHER_FALLING"), PROTECTION_FALL("PROTECTION_FALL", "FEATHER_FALLING"),

    FIRE_PROTECTION("PROTECTION_FIRE", "FIRE_PROTECTION"), PROTECTION_FIRE("PROTECTION_FIRE", "FIRE_PROTECTION"),

    PROJECTILE_PROTECTION("PROTECTION_PROJECTILE", "PROJECTILE_PROTECTION"), PROTECTION_PROJECTILE("PROTECTION_PROJECTILE", "PROJECTILE_PROTECTION"),

    CURSE_OF_VANISHING("VANISHING_CURSE", "VANISHING_CURSE", 11), VANISHING_CURSE("VANISHING_CURSE", "VANISHING_CURSE", 11),

    CURSE_OF_BINDING("BINDING_CURSE", "BINDING_CURSE", 11), BINDING_CURSE("BINDING_CURSE", "BINDING_CURSE", 11),

    DAMAGE_UNDEAD("DAMAGE_UNDEAD", "SMITE"), SMITE("DAMAGE_UNDEAD", "SMITE"),

    POWER("ARROW_DAMAGE", "POWER"), ARROW_DAMAGE("ARROW_DAMAGE", "POWER"),

    PUNCH("ARROW_KNOCKBACK", "PUNCH"), ARROW_KNOCKBACK("ARROW_KNOCKBACK", "PUNCH"),

    LOOTMOBS("LOOT_BONUS_MOBS", "LOOTING"), LOOTBONUSMOBS("LOOT_BONUS_MOBS", "LOOTING"), LOOT_BONUS_MOBS("LOOT_BONUS_MOBS", "LOOTING"), LOOTING("LOOT_BONUS_MOBS", "LOOTING"),
    UKNOWN("UKNOWN", "UKNOWN");

    private final String bukkitName;
    private final String paperName;
    private final int version;

    private static final Field acceptingNew = Ref.field(Enchantment.class, "acceptingNew");
    private static final Field byName = Ref.field(Enchantment.class, "byName"); // All
    private static final Field byKey = Ref.field(Enchantment.class, "byKey"); // 1.13+
    private static final Field key = Ref.field(Enchantment.class, "key"); // 1.13+
    private static final Field byId = Ref.field(Enchantment.class, "byId"); // 1.12-
    private static final Field id = Ref.field(Enchantment.class, "id"); // 1.12-

    EnchantmentAPI(String bukkitName, String paperName) {
        this(bukkitName, paperName, 0);
    }

    EnchantmentAPI(String bukkitName, String paperName, int version) {
        this.bukkitName = bukkitName;
        this.paperName = paperName;
        this.version = version;
    }

    /**
     * @apiNote Enchant ItemStack with this Enchantment if supported
     */
    public void enchant(ItemStack item, int level) {
        if (isSupported()) {
            Enchantment enchant = getEnchantment();
            if (enchant == null) {
				return;
			}
            if (item.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
                meta.addStoredEnchant(enchant, level, true);
                item.setItemMeta(meta);
            } else {
				item.addUnsafeEnchantment(enchant, level);
			}
        }
    }

    /**
     * @return boolean
     * @apiNote Get Enchantment by bukkitName if supported
     */
    @Nullable
    public Enchantment getEnchantment() {
        if (isSupported()) {
			return Enchantment.getByName(Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() > 3 ? paperName : bukkitName);
		}
        return null;
    }

    /**
     * @return boolean
     * @apiNote Does server have this enchant
     */
    public boolean isSupported() {
        return version == 0 || Ref.isNewerThan(version - 1);
    }

    /**
     * @return String
     * @apiNote Return enchantment bukkit name
     */
    @Nonnull
    public String getName() {
        return Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() > 3 ? paperName : bukkitName;
    }

    /**
     * @return EnchantmentAPI
     * @apiNote Get EnchantmentAPI from bukkit or vanilla name
     */
    @Nullable
    public static EnchantmentAPI byName(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (Exception e) {
            if (Enchantment.getByName(name.toUpperCase()) != null) {
				return UKNOWN;
			}
            return null;
        }
    }

    /**
     * @return EnchantmentAPI
     * @apiNote Conventor Enchantment to EnchantmentAPI
     */
    @Nullable
    public static EnchantmentAPI fromEnchant(Enchantment enchantment) {
        return byName(enchantment.getName());
    }

    /**
     * @return boolean state of success
     * @apiNote Register enchantment to the bukkit
     */
    @SuppressWarnings("unchecked")
	public static boolean registerEnchantment(Enchantment enchantment) {
        boolean registered = false;
        if (Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() >= 3) {
            Map<Object, Object> map = (Map<Object, Object>) Ref.get(Ref.getStatic(Ref.getClass("org.bukkit.Registry"), "ENCHANTMENT"), "map");
            if(map==null) {
            	//Then we get "cache" and "byValue"
            	Object key = Ref.invoke(enchantment, "getKey");
            	//NamespacedKey, Instance
                Map<Object, Object> cache = (Map<Object, Object>) Ref.get(Ref.getStatic(Ref.getClass("org.bukkit.Registry"), "ENCHANTMENT"), "cache");
            	//Instance, NamespacedKey
                Map<Object, Object> byValue = (Map<Object, Object>) Ref.get(Ref.getStatic(Ref.getClass("org.bukkit.Registry"), "ENCHANTMENT"), "byValue");
                cache.put(key, enchantment);
                byValue.put(enchantment, key);
            } else {
				map.put(Ref.invoke(enchantment, "getKey"), enchantment);
			}
            registered = true;
        } else {
            Ref.set(null, acceptingNew, true);
            try {
                Ref.invokeStatic(Ref.method(Enchantment.class, "registerEnchantment", Enchantment.class), enchantment);
                registered = true;
            } catch (Exception ignored) {
            }
        }
        return registered;
    }

    /**
     * @return boolean state of success
     * @apiNote Unregister enchantment
     */
    @SuppressWarnings("unchecked")
    public static boolean unregisterEnchantment(Enchantment enchantment) {
        boolean unregistered = false;
        try {
            ((Map<String, Enchantment>) Ref.get(null, byName)).remove(enchantment.getName());
            if (Ref.isNewerThan(12)) {
				unregistered = ((Map<?, ?>) Ref.get(null, byKey)).remove(Ref.get(enchantment, key)) != null;
			} else {
				unregistered = ((Map<Integer, Enchantment>) Ref.get(null, byId)).remove(Ref.get(enchantment, id)) != null;
			}
        } catch (Exception ignored) {
        }
        return unregistered;
    }
}