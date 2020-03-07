package me.Straiker123;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.Straiker123.Utils.Error;

@SuppressWarnings("deprecation")
public class ItemCreatorAPI {
	ItemStack a;
	public ItemCreatorAPI(ItemStack icon) {
		if(icon==null)a=new ItemStack(Material.STONE);
		else
		a=icon;
	}
	
	public Material getMaterial() {
		return a.getType();
	}

	public void setOwnerFromWeb(String web) {
		if(web!=null)
			this.url=web;
	}
	
	public void setOwnerFromValues(String values) {
		if(values!=null)
		text=values;
	}
	
	public void setMaterial(String byName) {
		try {
		a=new ItemStack(Material.matchMaterial(byName));
		}catch(Exception e) {
			Error.err("set material in ItemCreatorAPI", "Uknown Material");
		}
	}
	MultiMap ef = TheAPI.getMultiMap();
	public void addPotionEffect(PotionEffectType potionEffect, int duration, int amlifier) {
		if(potionEffect!=null)
		ef.put(potionEffect, duration,amlifier);
	}
	public void addPotionEffect(String potionEffect, int duration, int amlifier) {
		addPotionEffect(PotionEffectType.getByName(potionEffect),duration,amlifier);
	}
	Color c;
	public void setPotionColor(Color color) {
		if(color != null)
		c=color;
	}
	
	String name,owner,url,text;
	public void setDisplayName(String newName) {
		if(newName!=null)
		name=TheAPI.colorize(newName);
	}
	
	List<String> lore = new ArrayList<String>();
	public void addLore(String line) {
		if(line!=null)
		lore.add(TheAPI.colorize(line));
	}
	
	public void setOwner(String owner) {
		if(owner!=null)
			this.owner=owner;
	}
	
	HashMap<Enchantment, Integer> enchs = new HashMap<Enchantment, Integer>();
	public void addEnchantment(Enchantment e, int level) {
		if(e!= null)enchs.put(e, level);
	}
	public void addEnchantment(String e, int level) {
		if(e!= null)enchs.put(TheAPI.getEnchantmentAPI().getByName(e), level);
	}
	int s = 1;
	public void setAmount(int amount) {
		if(amount > 64)amount=64;
		s=amount;
	}
	
	public void setLore(List<String> lore) {
		if(lore!=null && lore.isEmpty()==false)
			for(String s:lore)
				addLore(s);
	}
	
	int model = -1;
	public void setCustomModelData(int i) {
		model=i;
	}
	boolean unb;
	public void setUnbreakable(boolean unbreakable) {
		unb=unbreakable;
	}
	SkullType type = null;
	public void setSkullType(SkullType t) {
		if(t!=null)
			type=t;
	}
	private List<ItemFlag> map = new ArrayList<ItemFlag>();
	public void addItemFlag(ItemFlag itemflag) {
		if(itemflag!=null)
		map.add(itemflag);
	}
	HashMap<Attribute, AttributeModifier> w = new HashMap<Attribute, AttributeModifier>();
	public void addAttrubuteModifier(Attribute a, AttributeModifier s) {
		if(TheAPI.isNewVersion()&&!TheAPI.getServerVersion().equals("v1_13_R1") && a != null && s!=null)
		w.put(a, s);
	}
	public void addAttrubuteModifiers(HashMap<Attribute, AttributeModifier> s) {
		if(TheAPI.isNewVersion()&&!TheAPI.getServerVersion().equals("v1_13_R1") && s!=null)
		w=s;
	}
	int dur=-1;
	public void setDurability(int amount) {
		dur=amount;
	}
	
	MaterialData data = null;
	public void setMaterialData(MaterialData data) {
		this.data=data;
	}
	
	String author = "";
	List<String> pages = new ArrayList<String>();
	String title = "UKNOWN";

	public void setBookAuthor(String author) {
		if(author!=null)
		this.author=TheAPI.colorize(author);
	}
	public void setBookTitle(String title) {
		if(title!=null)
		this.title=TheAPI.colorize(title);
	}
	public void addBookPage(String lines) {
		if(lines==null)lines="";
		pages.add(TheAPI.colorize(lines));
	}
	public void addBookPage(int page, String lines) {
		if(lines==null && pages.get(page)!=null)pages.remove(page);
		else
		pages.set(page,TheAPI.colorize(lines));
	}
	public void setBookPages(List<String> lines) {
		pages=new ArrayList<String>();
		if(lines!=null)
		for(String s : lines)
		addBookPage(s);
	}
	Generation gen = Generation.ORIGINAL;
	public void setBookGeneration(Generation generation) {
		try {
		if(generation!=null)
			gen=generation;
		}catch(Exception e) {
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public ItemStack create() {
		
		ItemStack i = a;
		try {
		if(type!=null) {
			a.setDurability((short)type.ordinal());
		}else {
			if(dur!=-1)
			a.setDurability((short)dur);
		}
		ItemMeta mf=i.getItemMeta();
		if(data != null)
			i.setData(data);
			if(enchs != null && !enchs.isEmpty())i.addUnsafeEnchantments(enchs);
			if(name!=null)
			mf.setDisplayName(name);
			if(model != -1 && TheAPI.isNewVersion() //1.14+
					 &&!TheAPI.getServerVersion().contains("v1_13"))
			mf.setCustomModelData(model);
			if(!TheAPI.isOlder1_9()
					 &&!TheAPI.getServerVersion().contains("v1_9")
					 &&!TheAPI.getServerVersion().contains("v1_10"))
			mf.setUnbreakable(unb);
			 else {
				 addLore("");
				 addLore("&9UNBREAKABLE");
			 }
				if(lore!=null && !lore.isEmpty())mf.setLore(lore);
			if(map != null && !map.isEmpty())
			for(ItemFlag f: map)
			mf.addItemFlags(f);
			if(w!=null && !w.isEmpty() && TheAPI.isNewVersion()
					 &&!TheAPI.getServerVersion().equals("v1_13_R1"))//1.13.2+
			mf.setAttributeModifiers((Multimap<Attribute, AttributeModifier>) w);
			i.setItemMeta(mf);
		if(i.getType().name().equalsIgnoreCase("ENCHANTED_BOOK")) {
		EnchantmentStorageMeta m = (EnchantmentStorageMeta) i.getItemMeta();
		if(data != null)
			i.setData(data);
		if(enchs != null && !enchs.isEmpty())
			for(Enchantment e : enchs.keySet())
			m.addStoredEnchant(e, enchs.get(e), true);
		if(name!=null)
			m.setDisplayName(name);
			if(model != -1 && TheAPI.isNewVersion() //1.14+
					 &&!TheAPI.getServerVersion().contains("v1_13"))
			m.setCustomModelData(model);
			if(!TheAPI.isOlder1_9()
					 &&!TheAPI.getServerVersion().contains("v1_9")
					 &&!TheAPI.getServerVersion().contains("v1_10"))
			m.setUnbreakable(unb);
			 else {
				 addLore("");
				 addLore("&9UNBREAKABLE");
			 }
				if(lore!=null && !lore.isEmpty())m.setLore(lore);
			if(map != null && !map.isEmpty())
			for(ItemFlag f: map)
			m.addItemFlags(f);
			if(w!=null && !w.isEmpty() && TheAPI.isNewVersion()
					 &&!TheAPI.getServerVersion().equals("v1_13_R1"))//1.13.2+
			m.setAttributeModifiers((Multimap<Attribute, AttributeModifier>) w);
		i.setItemMeta(m);
		}else
		if(i.getType().name().equalsIgnoreCase("WRITABLE_BOOK")||i.getType().name().equalsIgnoreCase("BOOK_AND_QUILL")) {
			BookMeta m = (BookMeta)i.getItemMeta();
			m.setAuthor(author);
			m.setPages(pages);
			m.setTitle(title);
			try {
			m.setGeneration(gen);
			}catch(Exception e) {
				
			}
			if(data != null)
				i.setData(data);
			if(enchs != null && !enchs.isEmpty())i.addUnsafeEnchantments(enchs);
			if(name!=null)
				m.setDisplayName(name);
				if(model != -1 && TheAPI.isNewVersion() //1.14+
						 &&!TheAPI.getServerVersion().contains("v1_13"))
				m.setCustomModelData(model);
				if(!TheAPI.isOlder1_9()
						 &&!TheAPI.getServerVersion().contains("v1_9")
						 &&!TheAPI.getServerVersion().contains("v1_10"))
				m.setUnbreakable(unb);
				 else {
					 addLore("");
					 addLore("&9UNBREAKABLE");
				 }
					if(lore!=null && !lore.isEmpty())m.setLore(lore);
				if(map != null && !map.isEmpty())
				for(ItemFlag f: map)
				m.addItemFlags(f);
				if(w!=null && !w.isEmpty() && TheAPI.isNewVersion()
						 &&!TheAPI.getServerVersion().equals("v1_13_R1"))//1.13.2+
				m.setAttributeModifiers((Multimap<Attribute, AttributeModifier>) w);
				i.setItemMeta(m);
		}else
			if(i.getType().name().startsWith("LINGERING_POTION_OF_")||i.getType().name().startsWith("SPLASH_POTION_OF_")||i.getType().name().startsWith("POTION_OF_")) {
				PotionMeta meta = (PotionMeta)i.getItemMeta();
				meta.setColor(c);
				for(Object o : ef.getKeySet()) {
					Object[] f = ef.getValues(o).toArray();
					PotionEffectType t = PotionEffectType.getByName(o.toString());
					if(t==null) {
						Error.err("creating ItemStack in ItemCreatorAPI", "Uknown PotionEffectType "+o.toString());
						continue;
					}
					int dur = TheAPI.getStringUtils().getInt(f[0].toString());
					int amp = TheAPI.getStringUtils().getInt(f[1].toString());
					meta.addCustomEffect(new PotionEffect(t, dur, (amp <= 0 ? 1 : amp)), true);
				}
				i.setItemMeta(meta);
			}else
			if(type!=null) {
				SkullMeta m=(SkullMeta)i.getItemMeta();
				if(data != null)
					i.setData(data);
					if(enchs != null && !enchs.isEmpty())i.addUnsafeEnchantments(enchs);
					if(name!=null)
					m.setDisplayName(name);
					if(model != -1 && TheAPI.isNewVersion()
							 &&!TheAPI.getServerVersion().contains("v1_13"))
					m.setCustomModelData(model);
					if(!TheAPI.isOlder1_9()
							 &&!TheAPI.getServerVersion().contains("v1_9")
							 &&!TheAPI.getServerVersion().contains("v1_10"))
					m.setUnbreakable(unb);
					 else {
						 addLore("");
						 addLore("&9UNBREAKABLE");
					 }
						if(lore!=null && !lore.isEmpty())m.setLore(lore);
					if(map != null && !map.isEmpty())
					for(ItemFlag f: map)
					m.addItemFlags(f);
					if(w!=null && !w.isEmpty() 
							&& TheAPI.isNewVersion()
							 &&!TheAPI.getServerVersion().equals("v1_13_R1"))
					m.setAttributeModifiers((Multimap<Attribute, AttributeModifier>) w);	
					if(owner!=null) {
						m.setOwner(owner);
					}else if (url != null || url ==null && text != null){
						GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
			        profile.getProperties().put("textures", new Property("textures",url == null && text != null ? text :  new String(encodedData)));
			        Field profileField = null;
			        try {
			            profileField = m.getClass().getDeclaredField("profile");
			            profileField.setAccessible(true);
			            profileField.set(m, profile);
			        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			            e1.printStackTrace();
			        }
					}
					i.setItemMeta(m);
			}
		}catch(Exception err) {
			Error.err("creating ItemStack in ItemCreatorAPI", "Uknown Material/ItemStack");
		}
		return i;
	}
	
}
