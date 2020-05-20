package me.Straiker123;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class EnchantmentAPI {
	public Enchantment[] values() {
		return Enchantment.values();
	}

	public List<Enchantment> valuesInList() {
		List<Enchantment> a = new ArrayList<Enchantment>();
		for (Enchantment e : Enchantment.values())
			a.add(e);
		return a;
	}
	
	public List<String> getNames(){
		return Arrays.asList("SHARPNESS", "DAMAGE_ALL","FIRE", "ARROW_FIRE",
				"BANEOFARTHROPODS", "DAMAGE_ARTHROPODS","LUCK", "LUCK_OF_SEA","LUCKOFSEA"
		,"INFINITY", "ARROW_INFINITE","FIREASPECT", "FIRE_ASPECT"
		,"RESPIRATION", "OXYGEN","LOOTING","FORTUNE","UNBREAKING", "DURABILITY"
		,"AQUA_AFFINITY", "WATER_WORKER","PROTECTION", "PROTECTION_ENVIRONMENTAL"
		,"BLAST_PROTECTION", "PROTECTION_EXPLOSIONS","FEATHER_FALLING", "PROTECTION_FALL"
		,"FIRE_PROTECTION", "PROTECTION_FIRE","PROJECTILE_PROTECTION", "PROTECTION_PROJECTILE"
		,"CURSE_OF_VANISHING", "VANISHING_CURSE","CURSE_OF_BINDING", "BINDING_CURSE"
		,"SMITE", "DAMAGE_UNDEAD","POWER", "ARROW_DAMAGE","ALLDAMAGE","DAMAGEALL"
		,"BANE_OF_ARTHROPODS","EFFICIENCY", "DIG_SPEED","PUNCH", "ARROW_KNOCKBACK"
		,"LOOTMOBS", "LOOT_BONUS_MOBS","LOOTBLOCKS", "LOOT_BONUS_BLOCKS");
	}

	private String replace(String s) {
		return s.replace("%SHARPNESS%", "DAMAGE_ALL").replace("%FIRE%", "ARROW_FIRE")
				.replace("%BANEOFARTHROPODS%", "DAMAGE_ARTHROPODS").replace("%LUCK%", "LUCK_OF_SEA")
				.replace("%LUCKOFSEA%", "LUCK_OF_SEA").replace("%LUCK_OF_SEA%", "LUCK_OF_SEA")
				.replace("%INFINITY%", "ARROW_INFINITE").replace("%FIREASPECT%", "FIRE_ASPECT")
				.replace("%RESPIRATION%", "OXYGEN").replace("%LOOTING%", "LOOT_BONUS_MOBS")
				.replace("%FORTUNE%", "LOOT_BONUS_BLOCKS").replace("%UNBREAKING%", "DURABILITY")
				.replace("%AQUA_AFFINITY%", "WATER_WORKER").replace("%PROTECTION%", "PROTECTION_ENVIRONMENTAL")
				.replace("%BLAST_PROTECTION%", "PROTECTION_EXPLOSIONS").replace("%FEATHER_FALLING%", "PROTECTION_FALL")
				.replace("%FIRE_PROTECTION%", "PROTECTION_FIRE")
				.replace("%PROJECTILE_PROTECTION%", "PROTECTION_PROJECTILE")
				.replace("%CURSE_OF_VANISHING%", "VANISHING_CURSE").replace("%CURSE_OF_BINDING%", "BINDING_CURSE")
				.replace("%SMITE%", "DAMAGE_UNDEAD").replace("%POWER%", "ARROW_DAMAGE")
				.replace("%ALLDAMAGE%", "DAMAGE_ALL").replace("%DAMAGEALL%", "DAMAGE_ALL")
				.replace("%BANE_OF_ARTHROPODS%", "DAMAGE_ARTHROPODS").replace("%EFFICIENCY%", "DIG_SPEED")
				.replace("%ALL_DAMAGE%", "DAMAGE_ALL").replace("%PUNCH%", "ARROW_KNOCKBACK")
				.replace("%LOOTMOBS%", "LOOT_BONUS_MOBS").replace("%LOOTBLOCKS%", "LOOT_BONUS_BLOCKS");
	}

	public boolean isEnchantment(String string) {
		return getByName(string) != null;
	}

	@SuppressWarnings("deprecation")
	public Enchantment getByName(String enchant) {
		try {
			if (Enchantment.getByName(enchant.toUpperCase()) == null)
				return Enchantment.getByName(replace("%" + enchant.toUpperCase() + "%"));
			return Enchantment.getByName(enchant.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}

	public Enchantment valueOf(String string) {
		return getByName(string);
	}

	public List<Enchantment> getEnchantments(ItemStack item) {
		List<Enchantment> list = new ArrayList<Enchantment>();
		list.addAll(item.getEnchantments().keySet());
		return list;
	}

	@SuppressWarnings("deprecation")
	public List<String> getEnchantmentsInString(ItemStack item) {
		List<String> list = new ArrayList<String>();
		for (Enchantment e : item.getEnchantments().keySet())
			list.add(e.getName());
		return list;
	}

	public boolean registerEnchantment(Enchantment e) {
		boolean registered = false;
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
		} catch (Exception ea) {
		}
		try {
			Enchantment.registerEnchantment(e);
			registered = true;
		} catch (Exception ea) {
		}
		return registered;
	}
}
