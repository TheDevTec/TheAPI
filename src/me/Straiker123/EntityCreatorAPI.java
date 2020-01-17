package me.Straiker123;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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

import me.Straiker123.Utils.Error;

public class EntityCreatorAPI {
	EntityType t;
	public EntityCreatorAPI(EntityType type) {
		t=type;
	}
	public static enum AttackDamageType{
		MINIMAL,
		MAXIMAL
	}
	double min;
	double max;
	double set;
	
	/**
	 * This method rewrite whole attack damage
	 *  but will not register additional damage to the sword, bow, etc.
	 */
	public void setAttackDamage(double amount) {
		if(amount>0)
			set=amount;
	}
	
	public void addAttackDamage(AttackDamageType type, double amount) {
		if(amount>0)
		switch(type) {
		case MAXIMAL:
			max=amount;
			break;
		case MINIMAL:
			min=amount;
			break;
		}
	}
	boolean ai=true;
	public void setAI(boolean setAI) {
		ai=setAI;
	}
	boolean collidable=true;
	public void setCollidable(boolean collidable) {
		this.collidable=collidable;
	}
	
	String name;
	public void setCustomName(String name) {
		this.name=TheAPI.colorize(name);
	}
	boolean visible=true;
	public void setCustomNameVisible(boolean visible) {
		this.visible=visible;
	}
	double hp;
	public void setHealth(double amount) {
		if(amount>0)
		hp=amount;
	}
	boolean items=true;
	public void setCanPickupItems(boolean can) {
		items = can;
	}
	boolean gravity=true;
	public void setGravity(boolean setGravity) {
		gravity=setGravity;
	}
	boolean glow;
	public void setGlowing(boolean setGlow) {
		glow=setGlow;
	}
	boolean isGod;
	public void setGod(boolean setGod) {
		setGod=isGod;
	}
	int god;
	public void setGodOnTime(int time) {
		god=time;
	}
	Entity entity;
	public void setPassenger(Entity passenger) {
		entity=passenger;
	}
	boolean silent;
	public void setSilent(boolean setSilent) {
		silent=setSilent;
	}
	public static enum ArmorStandOptions{
		Arms, //boolean
		BasePlate, //boolean
		Small, //boolean
		Marker, //boolean
		Visible, //boolean
		BodyPose, //EulerAngle
		HeadPose, //EulerAngle
		LeftLegPose, //EulerAngle
		LeftArmPose, //EulerAngle
		RightArmPose, //EulerAngle
		RightLegPose, //EulerAngle
		Helmet, //ItemStack
		ChestPlate, //ItemStack
		Leggings, //ItemStack
		Boots, //ItemStack
		ItemInHand //ItemStack
	}

	boolean arms;
	boolean base = true;
	boolean small;
	boolean marker;
	boolean visible_armor = true;
	EulerAngle body;
	EulerAngle head;

	EulerAngle l_leg;
	EulerAngle l_arm;
	EulerAngle r_leg;
	EulerAngle r_arm;
	
	ItemStack helmet;
	ItemStack chestplate;
	ItemStack leggings;
	ItemStack boots;
	ItemStack item;
	
	public static enum VillagerOptions{
		Adult, //boolean
		Baby, //boolean
		AgeLock, //boolean
		Breed, //boolean
		LootTable, //LootTable
		Recipes, //List<MerchantRecipe>
		Profession, //villager Profession
		VillagerType, //villager Type
		Age, //int
		VillagerExperience, //int
		VillagerLevel, //int
		Seed //long
	}
	long v_seed;
	int v_level;
	int v_exp;
	int v_age;
	Type v_type;
	Profession v_pro;
	List<MerchantRecipe> v_rec;
	LootTable v_loot;
	boolean v_breed;
	boolean v_locage;
	boolean v_baby;
	boolean v_adult;

	public static enum TNTOptions{
		IsIncendiary, //boolean
		FuseTicks, //int
		Yield //long
	}
	public void setTNTOptions(HashMap<TNTOptions, Object> w) {
		for(TNTOptions a : w.keySet()) {
			switch(a) {
			case IsIncendiary:
				tnt_inc=Boolean.getBoolean(w.get(a).toString());
				break;
			case FuseTicks:
				tnt_fuse=Integer.parseInt(w.get(a).toString());
				break;
			case Yield:
				tnt_yield=Long.parseLong(w.get(a).toString());
				break;
			}
		}}
	@SuppressWarnings("unchecked")
	public void setVillagerOptions(HashMap<VillagerOptions, Object> w) {
		for(VillagerOptions a : w.keySet()) {
			switch(a) {
			case Adult:
				v_adult=Boolean.getBoolean(w.get(a).toString());
				break;
			case Baby:
				v_baby=Boolean.getBoolean(w.get(a).toString());
				break;
			case AgeLock:
				v_locage=Boolean.getBoolean(w.get(a).toString());
				break;
			case Breed:
				v_breed=Boolean.getBoolean(w.get(a).toString());
				break;
			case LootTable:
				try {
				if((LootTable)w.get(a)!=null)
				v_loot=(LootTable)w.get(a);
				}catch(Exception e) {
					
				}
				break;
			case Profession:
				if((Profession)w.get(a)!=null)
				v_pro=(Profession)w.get(a);
				break;
			case Recipes:
				if((List<MerchantRecipe>)w.get(a)!=null&&((List<MerchantRecipe>)w.get(a)).isEmpty()==false)
				v_rec=(List<MerchantRecipe>)w.get(a);
				break;
			case VillagerExperience:
				v_exp=Integer.parseInt(w.get(a).toString());
				break;
			case VillagerLevel:
				v_level=Integer.parseInt(w.get(a).toString());
				break;
			case Age:
				v_age=Integer.parseInt(w.get(a).toString());
				break;
			case Seed:
				v_seed=Long.getLong(w.get(a).toString());
				break;
			case VillagerType:
				try {
				if((Type)w.get(a)!=null)
				v_type=(Type)w.get(a);
				}catch(Exception e){
					
				}
				break;
			}}}
	
	public void setArmorStandOptions(HashMap<ArmorStandOptions, Object> w) {
		for(ArmorStandOptions a : w.keySet()) {
			switch(a) {
			case Arms:
				arms=Boolean.getBoolean(w.get(a).toString());
				break;
			case BasePlate:
				base=Boolean.getBoolean(w.get(a).toString());
				break;
			case Small:
				small=Boolean.getBoolean(w.get(a).toString());
				break;
			case Marker:
				marker=Boolean.getBoolean(w.get(a).toString());
				break;
			case Visible:
				visible_armor=Boolean.getBoolean(w.get(a).toString());
				break;
			case BodyPose:
				if((EulerAngle)w.get(a)!=null)
				body=(EulerAngle)w.get(a);
				break;
			case HeadPose:
				if((EulerAngle)w.get(a)!=null)
				head=(EulerAngle)w.get(a);
				break;
			case LeftArmPose:
				if((EulerAngle)w.get(a)!=null)
				l_arm=(EulerAngle)w.get(a);
				break;
			case LeftLegPose:
				if((EulerAngle)w.get(a)!=null)
				l_leg=(EulerAngle)w.get(a);
				break;
			case RightArmPose:
				if((EulerAngle)w.get(a)!=null)
				r_arm=(EulerAngle)w.get(a);
				break;
			case RightLegPose:
				if((EulerAngle)w.get(a)!=null)
				r_leg=(EulerAngle)w.get(a);
				break;
			case Helmet:
				if((ItemStack)w.get(a)!=null)
				helmet=(ItemStack)w.get(a);
				break;
			case ChestPlate:
				if((ItemStack)w.get(a)!=null)
				chestplate=(ItemStack)w.get(a);
				break;
			case Leggings:
				if((ItemStack)w.get(a)!=null)
				leggings=(ItemStack)w.get(a);
				break;
			case Boots:
				if((ItemStack)w.get(a)!=null)
				boots=(ItemStack)w.get(a);
				break;
			case ItemInHand:
				if((ItemStack)w.get(a)!=null)
				item=(ItemStack)w.get(a);
				break;
			}
		}
	}

	int tnt_fuse = -1;
	boolean tnt_inc; //this is ?
	long tnt_yield = -1;
	
	@SuppressWarnings("deprecation")
	public void summonEntity(Location l) {
		try {
		LivingEntity e = (LivingEntity) l.getWorld().spawnEntity(l, t);
		if(hp>0) {
		e.setMaxHealth(hp);
		e.setHealth(hp);
		}
		if(e.getType()==EntityType.ARMOR_STAND) {
			ArmorStand a = (ArmorStand)e;
			a.setArms(arms);
			a.setBasePlate(base);
			if(body != null)
			a.setBodyPose(body);
			if(head!=null)
			a.setHeadPose(head);
			if(helmet!=null)
			a.setHelmet(helmet);
			if(chestplate!=null)
			a.setChestplate(chestplate);
			if(leggings!=null)
			a.setLeggings(leggings);
			if(boots!=null)
			a.setBoots(boots);
			if(item != null)
			a.setItemInHand(item);
			if(l_arm!=null)
			a.setLeftArmPose(l_arm);
			if(l_leg!=null)
			a.setLeftLegPose(l_leg);
			a.setMarker(marker);
			if(r_arm!=null)
			a.setRightArmPose(r_arm);
			if(r_leg!=null)
			a.setRightLegPose(r_leg);
			a.setSmall(small);
			a.setVisible(visible_armor);
		}
		if(e.getType()==EntityType.VILLAGER) {
			Villager v = (Villager)e;
			if(v_adult)
			v.setAdult();
			v.setAge(v_age);
			v.setAgeLock(v_locage);
			if(v_baby)
			v.setBaby();
			v.setBreed(v_breed);
			try {
			if(v_loot != null)
			v.setLootTable(v_loot);
			if(v_rec != null)
			v.setRecipes(v_rec);
			}catch(Exception err) {
				
			}
			if(v_pro != null)
			v.setProfession(v_pro);
			try {
			v.setVillagerExperience(v_exp);
			v.setVillagerLevel(v_level);
			if(v_type!=null)
			v.setVillagerType(v_type);
			}catch(Exception er) {
				//1.13+ only
			}
			try {
			v.setSeed(v_seed);
			}catch(Exception er) {
				//1.13+ only ?
			}
		}
		if(e.getType()==EntityType.PRIMED_TNT) {
		TNTPrimed a = (TNTPrimed)e;
		if(tnt_fuse!=-1)
		a.setFuseTicks(tnt_fuse);
		a.setIsIncendiary(tnt_inc);
		if(tnt_yield!=-1)
		a.setYield(tnt_yield);
		} //Straiker123 is bored ? :( 4:30 AM
		e.setCustomNameVisible(visible);
		
		e.setCustomName(name);
		if(set==0) {
		if(min>0) {

			MetadataValue metadataValue = new FixedMetadataValue(LoaderClass.plugin, min);
			e.setMetadata("damage:min", metadataValue);
		}
		if(max>0){

			MetadataValue metadataValue = new FixedMetadataValue(LoaderClass.plugin, max);
			e.setMetadata("damage:max", metadataValue);
		}
		}else {
			MetadataValue metadataValue = new FixedMetadataValue(LoaderClass.plugin, set);
			e.setMetadata("damage:set", metadataValue);
		}
		try {
		e.setAI(ai);
		e.setCollidable(collidable);
		}catch(Exception err) {
			
		}
		e.setCanPickupItems(items);
		try {
		e.setGravity(gravity);
		e.setGlowing(glow);
		e.setInvulnerable(isGod);
		}catch(Exception err) {
			
		}
		if(god>0)
		e.setNoDamageTicks(20*god);
		if(entity!=null)
		e.setPassenger(entity);
		try {
		e.setSilent(silent);
		}catch(Exception err) {
			
		}
		}catch(Exception es) {
			if(!LoaderClass.config.getConfig().getBoolean("Options.HideErrors")) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cError when spawning entity using EnttiyCreatorAPI:"));
				es.printStackTrace();
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&bTheAPI&7: &cEnd of error."));
				}else
					Error.sendRequest("&bTheAPI&7: &cError when spawning entity using EnttiyCreatorAPI");
		}
	}
}
