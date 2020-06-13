package me.DevTec;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Villager.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.loot.LootTable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.EulerAngle;

import me.DevTec.Other.LoaderClass;
import me.DevTec.Other.Position;
import me.DevTec.Utils.Error;

@SuppressWarnings("deprecation")
public class EntityCreatorAPI {
	private EntityType t;

	public EntityCreatorAPI(EntityType type) {
		t = type;
	}

	public EntityType getEntityType() {
		return t;
	}

	public static enum AttackDamageType {
		MINIMAL, MAXIMAL
	}

	double min, max, set, jump;

	/**
	 * @see see This method rewrite whole attack damage. but will not register
	 *      additional damage to the sword, bow, etc.
	 */
	public void setAttackDamage(double amount) {
		if (amount > 0)
			set = amount;
	}

	public void addAttackDamage(AttackDamageType type, double amount) {
		if (amount > 0)
			switch (type) {
			case MAXIMAL:
				max = amount;
				break;
			case MINIMAL:
				min = amount;
				break;
			}
	}

	boolean ai = true;

	public void setAI(boolean setAI) {
		ai = setAI;
	}

	boolean collidable = true;

	public void setCollidable(boolean collidable) {
		this.collidable = collidable;
	}

	String name;

	public void setCustomName(String name) {
		this.name = TheAPI.colorize(name);
	}

	boolean visible = true;

	public void setCustomNameVisible(boolean visible) {
		this.visible = visible;
	}

	double hp;

	public void setHealth(double amount) {
		if (amount > 0)
			hp = amount;
	}

	boolean items = true;

	public void setCanPickupItems(boolean can) {
		items = can;
	}

	boolean gravity = true;

	public void setGravity(boolean setGravity) {
		gravity = setGravity;
	}

	public void setGlowing(boolean setGlow) {
		glow = setGlow;
	}

	public void setGod(boolean setGod) {
		setGod = isGod;
	}

	public void setGodOnTime(int time) {
		god = time;
	}

	Entity entity;

	public void setPassenger(Entity passenger) {
		entity = passenger;
	}

	public void setSilent(boolean setSilent) {
		silent = setSilent;
	}

	public static enum ArmorStandOptions {
		Arms, // boolean
		BasePlate, // boolean
		Small, // boolean
		Marker, // boolean
		Visible, // boolean
		BodyPose, // EulerAngle
		HeadPose, // EulerAngle
		LeftLegPose, // EulerAngle
		LeftArmPose, // EulerAngle
		RightArmPose, // EulerAngle
		RightLegPose, // EulerAngle
		Helmet, // ItemStack
		ChestPlate, // ItemStack
		Leggings, // ItemStack
		Boots, // ItemStack
		ItemInHand // ItemStack
	}

	boolean base = true;
	boolean small, marker, arms, isGod, glow, silent, v_breed, v_locage, v_baby, v_adult, chest, tamed;
	boolean visible_armor = true;
	EulerAngle body, head, l_leg, l_arm, r_leg, r_arm;

	ItemStack helmet, chestplate, leggings, boots, item;

	public static enum VillagerOptions {
		Adult, // boolean
		Baby, // boolean
		AgeLock, // boolean
		Breed, // boolean
		LootTable, // LootTable
		Recipes, // List<MerchantRecipe>
		Profession, // villager Profession
		VillagerType, // villager Type
		Age, // int
		VillagerExperience, // int
		VillagerLevel, // int
		Seed // long
	}

	long v_seed;
	int v_level, god, v_exp, v_age, d, m_d;
	Type v_type;
	Profession v_pro;
	List<MerchantRecipe> v_rec;
	LootTable v_loot;

	public static enum HorseOptions {
		CarryingChest, // boolean
		Tamed, // boolean
		Color, // Color
		Domestication, // int
		MaxDomestication, // int
		Jump, // double
		Owner, // AnimalTamer
		Variant // Variant
	}

	public static enum TNTOptions {
		IsIncendiary, // boolean
		FuseTicks, // int
		Yield // long
	}

	Variant v; // horse variant
	Color c; // horse color
	AnimalTamer owner; // horse owner

	public void setHorseOptions(HashMap<HorseOptions, Object> w) {
		for (HorseOptions a : w.keySet()) {
			switch (a) {
			case CarryingChest:
				chest = Boolean.getBoolean(w.get(a).toString());
				break;
			case Tamed:
				tamed = Boolean.getBoolean(w.get(a).toString());
				break;
			case Color:
				try {
					c = (Color) w.get(a);
				} catch (Exception erro) {
					c = Color.WHITE;
				}
				break;
			case Domestication:
				d = Integer.parseInt(w.get(a).toString());
				break;
			case MaxDomestication:
				m_d = Integer.parseInt(w.get(a).toString());
				break;
			case Jump:
				jump = Double.parseDouble(w.get(a).toString());
				break;
			case Owner:
				owner = w.get(a) instanceof Player ? (AnimalTamer) w.get(a) : (AnimalTamer) w.get(a);
				break;
			case Variant:
				try {
					v = (Variant) w.get(a);
				} catch (Exception erro) {
					v = Variant.HORSE;
				}
				break;
			}
		}
	}

	public void setTNTOptions(HashMap<TNTOptions, Object> w) {
		for (TNTOptions a : w.keySet()) {
			switch (a) {
			case IsIncendiary:
				tnt_inc = Boolean.getBoolean(w.get(a).toString());
				break;
			case FuseTicks:
				tnt_fuse = Integer.parseInt(w.get(a).toString());
				break;
			case Yield:
				tnt_yield = Long.parseLong(w.get(a).toString());
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setVillagerOptions(HashMap<VillagerOptions, Object> w) {
		for (VillagerOptions a : w.keySet()) {
			switch (a) {
			case Adult:
				v_adult = Boolean.getBoolean(w.get(a).toString());
				break;
			case Baby:
				v_baby = Boolean.getBoolean(w.get(a).toString());
				break;
			case AgeLock:
				v_locage = Boolean.getBoolean(w.get(a).toString());
				break;
			case Breed:
				v_breed = Boolean.getBoolean(w.get(a).toString());
				break;
			case LootTable:
				try {
					if ((LootTable) w.get(a) != null)
						v_loot = (LootTable) w.get(a);
				} catch (Exception e) {

				}
				break;
			case Profession:
				if ((Profession) w.get(a) != null)
					v_pro = (Profession) w.get(a);
				break;
			case Recipes:
				if ((List<MerchantRecipe>) w.get(a) != null && ((List<MerchantRecipe>) w.get(a)).isEmpty() == false)
					v_rec = (List<MerchantRecipe>) w.get(a);
				break;
			case VillagerExperience:
				v_exp = Integer.parseInt(w.get(a).toString());
				break;
			case VillagerLevel:
				v_level = Integer.parseInt(w.get(a).toString());
				break;
			case Age:
				v_age = Integer.parseInt(w.get(a).toString());
				break;
			case Seed:
				v_seed = Long.getLong(w.get(a).toString());
				break;
			case VillagerType:
				try {
					if ((Type) w.get(a) != null)
						v_type = (Type) w.get(a);
				} catch (Exception e) {

				}
				break;
			}
		}
	}

	public void setArmorStandOptions(HashMap<ArmorStandOptions, Object> w) {
		for (ArmorStandOptions a : w.keySet()) {
			switch (a) {
			case Arms:
				arms = Boolean.getBoolean(w.get(a).toString());
				break;
			case BasePlate:
				base = Boolean.getBoolean(w.get(a).toString());
				break;
			case Small:
				small = Boolean.getBoolean(w.get(a).toString());
				break;
			case Marker:
				marker = Boolean.getBoolean(w.get(a).toString());
				break;
			case Visible:
				visible_armor = Boolean.getBoolean(w.get(a).toString());
				break;
			case BodyPose:
				try {
					if ((EulerAngle) w.get(a) != null)
						body = (EulerAngle) w.get(a);
				} catch (Exception err) {

				}
				break;
			case HeadPose:
				try {
					if ((EulerAngle) w.get(a) != null)
						head = (EulerAngle) w.get(a);
				} catch (Exception err) {

				}
				break;
			case LeftArmPose:
				try {
					if ((EulerAngle) w.get(a) != null)
						l_arm = (EulerAngle) w.get(a);
				} catch (Exception err) {

				}
				break;
			case LeftLegPose:
				try {
					if ((EulerAngle) w.get(a) != null)
						l_leg = (EulerAngle) w.get(a);
				} catch (Exception err) {

				}
				break;
			case RightArmPose:
				try {
					if ((EulerAngle) w.get(a) != null)
						r_arm = (EulerAngle) w.get(a);
				} catch (Exception err) {

				}
				break;
			case RightLegPose:
				try {
					if ((EulerAngle) w.get(a) != null)
						r_leg = (EulerAngle) w.get(a);
				} catch (Exception err) {

				}
				break;
			case Helmet:
				if ((ItemStack) w.get(a) != null)
					helmet = (ItemStack) w.get(a);
				break;
			case ChestPlate:
				if ((ItemStack) w.get(a) != null)
					chestplate = (ItemStack) w.get(a);
				break;
			case Leggings:
				if ((ItemStack) w.get(a) != null)
					leggings = (ItemStack) w.get(a);
				break;
			case Boots:
				if ((ItemStack) w.get(a) != null)
					boots = (ItemStack) w.get(a);
				break;
			case ItemInHand:
				if ((ItemStack) w.get(a) != null)
					item = (ItemStack) w.get(a);
				break;
			}
		}
	}

	int tnt_fuse = -1;
	boolean tnt_inc; // this is ?
	long tnt_yield = -1;

	public void summonEntity(Location l) {
		summonEntity(new Position(l));
	}

	public void summonEntity(Position l) {
		try {
			LivingEntity e = (LivingEntity) l.getWorld().spawnEntity(l.toLocation(), t);
			if (hp > 0) {
				e.setMaxHealth(hp);
				e.setHealth(hp);
			}
			try {
				if (e.getType() == EntityType.ARMOR_STAND) {
					ArmorStand a = (ArmorStand) e;
					a.setArms(arms);
					a.setBasePlate(base);
					if (body != null)
						a.setBodyPose(body);
					if (head != null)
						a.setHeadPose(head);
					if (helmet != null)
						a.setHelmet(helmet);
					if (chestplate != null)
						a.setChestplate(chestplate);
					if (leggings != null)
						a.setLeggings(leggings);
					if (boots != null)
						a.setBoots(boots);
					if (item != null)
						a.setItemInHand(item);
					if (l_arm != null)
						a.setLeftArmPose(l_arm);
					if (l_leg != null)
						a.setLeftLegPose(l_leg);
					a.setMarker(marker);
					if (r_arm != null)
						a.setRightArmPose(r_arm);
					if (r_leg != null)
						a.setRightLegPose(r_leg);
					a.setSmall(small);
					a.setVisible(visible_armor);
				}
			} catch (Exception err) {

			}
			if (e.getType() == EntityType.VILLAGER) {
				Villager v = (Villager) e;
				if (v_adult)
					v.setAdult();
				v.setAge(v_age);
				v.setAgeLock(v_locage);
				if (v_baby)
					v.setBaby();
				v.setBreed(v_breed);
				try {
					if (v_loot != null)
						v.setLootTable(v_loot);
					if (v_rec != null)
						v.setRecipes(v_rec);
				} catch (Exception err) {

				}
				if (v_pro != null)
					v.setProfession(v_pro);
				try {
					v.setVillagerExperience(v_exp);
					v.setVillagerLevel(v_level);
					if (v_type != null)
						v.setVillagerType(v_type);
				} catch (Exception er) {
					// 1.13+ only
				}
				try {
					v.setSeed(v_seed);
				} catch (Exception er) {
					// 1.13+ only ?
				}
			}
			if (e.getType() == EntityType.PRIMED_TNT) {
				TNTPrimed a = (TNTPrimed) e;
				if (tnt_fuse != -1)
					a.setFuseTicks(tnt_fuse);
				a.setIsIncendiary(tnt_inc);
				if (tnt_yield != -1)
					a.setYield(tnt_yield);
			}
			if (e.getType().getName().equals("HORSE") || e.getType().getName().equals("DONKEY")
					|| e.getType().getName().equals("MULE") || e.getType().getName().equals("ZOMBIE_HORSE")
					|| e.getType().getName().equals("SKELETON_HORSE")) {
				Horse h = (Horse) e;
				try {
					h.setCarryingChest(chest);
					if (c != null)
						h.setColor(c);
					if (d > 0)
						h.setDomestication(d);
					if (jump > 0)
						h.setJumpStrength(jump);
					if (m_d > 0)
						h.setMaxDomestication(m_d);
					if (owner != null)
						h.setOwner(owner); // AnimalTamer
					if (v != null)
						h.setVariant(v);
					h.setTamed(tamed);
				} catch (Exception err) {

				}
			}
			e.setCustomNameVisible(visible);

			e.setCustomName(name);
			if (set == 0) {
				if (min > 0) {

					MetadataValue metadataValue = new FixedMetadataValue(LoaderClass.plugin, min);
					e.setMetadata("damage:min", metadataValue);
				}
				if (max > 0) {

					MetadataValue metadataValue = new FixedMetadataValue(LoaderClass.plugin, max);
					e.setMetadata("damage:max", metadataValue);
				}
			} else {
				MetadataValue metadataValue = new FixedMetadataValue(LoaderClass.plugin, set);
				e.setMetadata("damage:set", metadataValue);
			}
			try {
				e.setAI(ai);
				e.setCollidable(collidable);
			} catch (Exception err) {

			}
			e.setCanPickupItems(items);
			try {
				e.setGravity(gravity);
				e.setGlowing(glow);
				e.setInvulnerable(isGod);
			} catch (Exception err) {

			}
			if (god > 0)
				e.setNoDamageTicks(20 * god);
			if (entity != null)
				e.setPassenger(entity);
			try {
				e.setSilent(silent);
			} catch (Exception err) {

			}
		} catch (Exception es) {
			if (!LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				TheAPI.getConsole().sendMessage(
						TheAPI.colorize("&bTheAPI&7: &cError when spawning entity using EntityCreatorAPI:"));
				es.printStackTrace();
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
			} else
				Error.sendRequest("&bTheAPI&7: &cError when spawning entity using EntityCreatorAPI");
		}
	}
}
