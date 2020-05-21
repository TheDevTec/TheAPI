package me.Straiker123;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

public enum EnchantmentAPI {
	SHARPNESS("DAMAGE_ALL"),
	DAMAGEALL("DAMAGE_ALL"),
	ALLDAMAGE("DAMAGE_ALL"),
	DAMAGE_ALL("DAMAGE_ALL"),
	
	DAMAGE_ARTHROPODS("DAMAGE_ARTHROPODS"),
	ARTHROPODS("DAMAGE_ARTHROPODS"),
	BANEOFARTHROPODS("DAMAGE_ARTHROPODS"),

	EFFICIENCY("DIG_SPEED"),
	DIG_SPEED("DIG_SPEED"),
	DIGSPEED("DIG_SPEED"),
	SPEEDDIG("DIG_SPEED"),
	
	CHANNELING("CHANNELING",13),
	DEPTH_STRIDER("DEPTH_STRIDER",8),
	
	FROST_WALKER("FROST_WALKER",9),
	
	KNOCKBACK("KNOCKBACK"),
	
	MULTISHOT("MULTISHOT",14),
	
	PIERCING("PIERCING",13),
	
	LOYALTY("LOYALTY",13),
	
	RIPTIDE("RIPTIDE",13),

	QUICK_CHARGE("QUICK_CHARGE",14),

	THORNS("THORNS",4),

	SILK_TOUCH("SILK_TOUCH"),

	SWEEPING_EDGE("SWEEPING_EDGE",11),

	MENDING("MENDING",9),
	REPEAIRING("MENDING",9),
	
	ARROW_FIRE("ARROW_FIRE",1),
	FIRE("ARROW_FIRE",1),
	
	FIREASPECT("FIRE_ASPECT"),
	FIRE_ASPECT("FIRE_ASPECT"),
	
	INFINITY("ARROW_INFINITE",11),
	ARROW_INFINITE("ARROW_INFINITE",11),
	
	IMPALING("IMPALING",13),
	
	LURE("LURE",7),
	
	LUCK("LUCK",7),
	LUCK_OF_SEA("LUCK",7),
	LUCKOFSEA("LUCK",7),
	
	FORTUNE("LOOT_BONUS_BLOCKS"),
	LOOTBLOCKS("LOOT_BONUS_BLOCKS"),
	LOOT_BONUS_BLOCKS("LOOT_BONUS_BLOCKS"),
	
	RESPIRATION("OXYGEN"),
	OXYGEN("OXYGEN"),
	
	UNBREAKING("DURABILITY"),
	DURABILITY("DURABILITY"),
	
	AQUA_AFFINITY("WATER_WORKER"),
	WATER_WORKER("WATER_WORKER"),
	
	PROTECTION("PROTECTION_ENVIRONMENTAL"),
	PROTECTION_ENVIRONMENTAL("PROTECTION_ENVIRONMENTAL"),
	
	BLAST_PROTECTION("PROTECTION_EXPLOSIONS"),
	PROTECTION_EXPLOSIONS("PROTECTION_EXPLOSIONS"),

	FEATHER_FALLING("PROTECTION_FALL"),
	PROTECTION_FALL("PROTECTION_FALL"),

	FIRE_PROTECTION("PROTECTION_FIRE"),
	PROTECTION_FIRE("PROTECTION_FIRE"),

	PROJECTILE_PROTECTION("PROTECTION_PROJECTILE"),
	PROTECTION_PROJECTILE("PROTECTION_PROJECTILE"),

	CURSE_OF_VANISHING("VANISHING_CURSE",11),
	VANISHING_CURSE("VANISHING_CURSE",11),

	CURSE_OF_BINDING("BINDING_CURSE",11),
	BINDING_CURSE("BINDING_CURSE",11),

	DAMAGE_UNDEAD("DAMAGE_UNDEAD"),
	SMITE("DAMAGE_UNDEAD"),

	POWER("ARROW_DAMAGE",1),
	ARROW_DAMAGE("ARROW_DAMAGE",1),

	PUNCH("ARROW_KNOCKBACK",1),
	ARROW_KNOCKBACK("ARROW_KNOCKBACK",1),
	
	LOOTMOBS("LOOT_BONUS_MOBS"),
	LOOTBONUSMOBS("LOOT_BONUS_MOBS"),
	LOOT_BONUS_MOBS("LOOT_BONUS_MOBS"),
	LOOTING("LOOT_BONUS_MOBS");
	private final String s;
	private final int v;
	private static Method getByName = Reflections.getMethod(org.bukkit.enchantments.Enchantment.class,"getByName", String.class);
	EnchantmentAPI(String real){
		s=real;
		v=0;
	}
	
	EnchantmentAPI(String real,int version){
		s=real;
		v=version;
	}
	
	public void enchant(ItemStack to, int level) { //1_15_1R0 -> 15
		if(TheAPI.getStringUtils().getInt(TheAPI.getServerVersion().split("_")[1]) >= v)
		to.addUnsafeEnchantment(getEnchantment(), level);
	}
	
	public org.bukkit.enchantments.Enchantment getEnchantment(){
		if(TheAPI.getStringUtils().getInt(TheAPI.getServerVersion().split("_")[1]) >= v) {
		Object o = Reflections.invoke(null,getByName,s);
		try {
			if (o == null) {
				o = Reflections.invoke(null,getByName,s);
				return o==null ? null : (org.bukkit.enchantments.Enchantment)o;
			}
			return (org.bukkit.enchantments.Enchantment)o;
		} catch (Exception e) {
			return null;
		}}
		return null;
	}
	
	/**
	 * @see see Return enchantment real name
	 * @return String
	 */
	public String getName() {
		return s;
	}
	
	public static EnchantmentAPI byName(String name) {
		return valueOf(name.toUpperCase());
	}
	
	public static EnchantmentAPI fromEnchant(org.bukkit.enchantments.Enchantment enchant){
		return byName(enchant.toString());
	}

	public static List<org.bukkit.enchantments.Enchantment> getEnchantments(ItemStack item) {
		List<org.bukkit.enchantments.Enchantment> list = new ArrayList<org.bukkit.enchantments.Enchantment>(item.getEnchantments().keySet());
		return list;
	}
	
	public static boolean registerEnchantment(org.bukkit.enchantments.Enchantment e) {
		boolean registered = false;
		Reflections.setFieldWithNull(org.bukkit.enchantments.Enchantment.class, "acceptingNew", true);
		try {
			org.bukkit.enchantments.Enchantment.registerEnchantment(e);
			registered = true;
		} catch (Exception ea) {
		}
		return registered;
	}
}
