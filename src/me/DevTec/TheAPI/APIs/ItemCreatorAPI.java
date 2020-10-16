package me.DevTec.TheAPI.APIs;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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
import com.mojang.authlib.properties.Property;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.TheAPIUtils.Validator;

@SuppressWarnings("deprecation")
public class ItemCreatorAPI implements Cloneable {
	
	//Simple creation
	
	public static ItemStack create(Material material, int amount, String displayName) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		return a.create();
	}
	
	public static ItemStack create(Material material, int amount, String displayName, List<String> lore) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		return a.create();
	}
	
	public static ItemStack create(Material material, int amount, String displayName, int data) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setDurability(data);
		return a.create();
	}
	
	public static ItemStack create(Material material, int amount, String displayName, List<String> lore, int data) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setDurability(data);
		return a.create();
	}
	
	public static ItemStack createPotion(Material material, int amount, String displayName, PotionEffect... effects) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		for(PotionEffect effect : effects)
		a.addPotionEffect(effect);
		return a.create();
	}
	
	public static ItemStack createPotion(Material material, int amount, String displayName, List<String> lore, PotionEffect... effects) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		for(PotionEffect effect : effects)
		a.addPotionEffect(effect);
		return a.create();
	}
	
	public static ItemStack createPotion(Material material, int amount, String displayName, Color color, PotionEffect... effects) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		for(PotionEffect effect : effects)
		a.addPotionEffect(effect);
		a.setPotionColor(color);
		return a.create();
	}
	
	public static ItemStack createPotion(Material material, int amount, String displayName, List<String> lore, Color color, PotionEffect... effects) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		for(PotionEffect effect : effects)
		a.addPotionEffect(effect);
		a.setPotionColor(color);
		return a.create();
	}
	
	public static ItemStack createBook(Material material, int amount, String displayName, String author, String title, List<String> pages) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setBookAuthor(author);
		a.setBookTitle(title);
		a.setBookPages(pages);
		return a.create();
	}
	
	public static ItemStack createBook(Material material, int amount, String displayName, List<String> lore, String author, String title, List<String> pages) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setBookAuthor(author);
		a.setBookTitle(title);
		a.setBookPages(pages);
		return a.create();
	}
	
	//Only for newer minecraft versions
	public static ItemStack createBook(Material material, int amount, String displayName, String author, String title, List<String> pages, Generation gen) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setBookAuthor(author);
		a.setBookTitle(title);
		a.setBookPages(pages);
		a.setBookGeneration(gen);
		return a.create();
	}

	//Only for newer minecraft versions
	public static ItemStack createBook(Material material, int amount, String displayName, List<String> lore, String author, String title, List<String> pages, Generation gen) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setBookAuthor(author);
		a.setBookTitle(title);
		a.setBookPages(pages);
		a.setBookGeneration(gen);
		return a.create();
	}
	
	public static ItemStack createHead(int amount, String displayName, String owner) {
		Material mat = null;
		try{
			mat=Material.LEGACY_SKULL_ITEM;
		}catch(Exception | NoSuchFieldError e) {
			mat=Material.matchMaterial("SKULL_ITEM");
		}
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setOwner(owner);
		a.setSkullType("PLAYER");
		return a.create();
	}
	
	public static ItemStack createHead(int amount, String displayName, String owner, List<String> lore) {
		Material mat = null;
		try{
			mat=Material.LEGACY_SKULL_ITEM;
		}catch(Exception | NoSuchFieldError e) {
			mat=Material.matchMaterial("SKULL_ITEM");
		}
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setOwner(owner);
		a.setSkullType("PLAYER");
		return a.create();
	}
	
	public static ItemStack createHead(int amount, String displayName, SkullType type) {
		Material mat = null;
		try{
			mat=Material.LEGACY_SKULL_ITEM;
		}catch(Exception | NoSuchFieldError e) {
			mat=Material.matchMaterial("SKULL_ITEM");
		}
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setSkullType(type);
		return a.create();
	}
	
	public static ItemStack createHead(int amount, String displayName, List<String> lore, SkullType type) {
		Material mat = null;
		try{
			mat=Material.LEGACY_SKULL_ITEM;
		}catch(Exception | NoSuchFieldError e) {
			mat=Material.matchMaterial("SKULL_ITEM");
		}
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setSkullType(type);
		return a.create();
	}
	
	public static ItemStack createHeadByValues(int amount, String displayName, String ownerValues) {
		Material mat = null;
		try{
			mat=Material.LEGACY_SKULL_ITEM;
		}catch(Exception | NoSuchFieldError e) {
			mat=Material.matchMaterial("SKULL_ITEM");
		}
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setOwnerFromValues(ownerValues);
		a.setSkullType("PLAYER");
		return a.create();
	}
	
	public static ItemStack createHeadByValues(int amount, String displayName, List<String> lore, String ownerValues) {
		Material mat = null;
		try{
			mat=Material.LEGACY_SKULL_ITEM;
		}catch(Exception | NoSuchFieldError e) {
			mat=Material.matchMaterial("SKULL_ITEM");
		}
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setSkullType("PLAYER");
		a.setOwnerFromValues(ownerValues);
		return a.create();
	}
	
	public static ItemStack createHeadByWeb(int amount, String displayName, String ownerLink) {
		Material mat = null;
		try{
			mat=Material.LEGACY_SKULL_ITEM;
		}catch(Exception | NoSuchFieldError e) {
			mat=Material.matchMaterial("SKULL_ITEM");
		}
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setSkullType("PLAYER");
		a.setOwnerFromWeb(ownerLink);
		return a.create();
	}
	
	public static ItemStack createHeadByWeb(int amount, String displayName, List<String> lore, String ownerLink) {
		Material mat = null;
		try{
			mat=Material.PLAYER_HEAD;
		}catch(Exception | NoSuchFieldError e) {
			mat=Material.getMaterial("SKULL_ITEM");
		}
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setSkullType("PLAYER");
		a.setOwnerFromWeb(ownerLink);
		return a.create();
	}
	
	private ItemStack a;
	private String author= "", title= "", name, owner, url, text;
	private Color c;
	private boolean unb;
	private SkullType type;
	private HashMap<Attribute, AttributeModifier> w=new HashMap<>();
	private int s= 1, model= -1, dur= -1;
	private HashMap<PotionEffectType, String> ef = new HashMap<>();
	private HashMap<Enchantment, Integer> enchs = new HashMap<>();
	private List<Object> pages=new ArrayList<>(), lore=new ArrayList<>(), map=new ArrayList<>();
	private MaterialData data = null;
	private Generation gen;

	private int getSkullInt(String w) {
		return SkullType.valueOf(w).ordinal();
	}

	public ItemCreatorAPI(Material icon) {
		this(new ItemStack(icon));
	}

	public ItemCreatorAPI(ItemStack icon) {
		a = icon != null ? icon : new ItemStack(Material.AIR);
		unb = isUnbreakable();
		if (hasPotionEffects())
			for (PotionEffect e : getPotionEffects()) {
				addPotionEffect(e.getType(), e.getDuration(), e.getAmplifier());
			}
		if (hasPotionColor())
			c = getPotionColor();
		if (hasDisplayName())
			name = getDisplayName();
		owner = getOwner();
		text = getOwnerByValues();
		text = getOwnerByWeb();
		if (hasLore())
			for (String s : getLore()) {
				addLore(s);
			}
		if (hasEnchants())
			for (Enchantment e : getEnchantments().keySet())
				addEnchantment(e, getEnchantments().get(e));
		s = getAmount();
		if (hasCustomModelData())
			model = getCustomModelData();
		type = getSkullType();
		try {
			for (ItemFlag s : getItemFlags())
				map.add(s);
		} catch (Exception | NoSuchMethodError er) {
		}
		try {
			data = getMaterialData();
		} catch (Exception er) {
		}
		dur = getDurability();
		try {
			if (hasAttributeModifiers())
				for (Attribute s : getAttributeModifiers().keySet())
					addAttributeModifier(s, getAttributeModifiers().get(s));
		} catch (Exception | NoSuchMethodError er) {
		}
		if (hasBookAuthor())
			author = getBookAuthor();
		for (String s : getBookPages()) {
			addBookPage(s);
		}
		if (hasBookTitle())
			title = getBookTitle();
		try {
			if (hasBookGeneration())
				gen = getBookGeneration();
		} catch (Exception | NoSuchMethodError er) {
		}
	}

	public String getOwnerByWeb() {
		return url;
	}

	public String getOwnerByValues() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof SkullMeta)
				return (String)Ref.invoke(Ref.invoke(Ref.invoke(Ref.get(a.getItemMeta(), "profile"), "getProperties"), Ref.method(Ref.invoke(Ref.get(a.getItemMeta(), "profile"), "getProperties").getClass().getSuperclass(), "get", Object.class), "textures"), "getValue");
		return text;
	}

	public Material getMaterial() {
		return a.getType();
	}

	public boolean isItem(boolean canBeLegacy) {
		String s = a.getType().name();
		return !s.contains("WALL_") && !isAir() && !s.contains("_STEM") && !s.contains("POTTED_")
				&& (canBeLegacy ? true : !s.contains("LEGACY_")) && !s.equals("END_PORTAL") && !s.equals("END_GATEWAY")
				&& !s.equals("NETHER_PORTAL") || isVisibleBlock();
	}

	public boolean isAir() {
		return a.getType().name().equals("AIR") || a.getType().name().equals("VOID_AIR")
				|| a.getType().name().equals("STRUCTURE_VOID");
	}

	public boolean isBlock() {
		return a.getType().isBlock();
	}

	public boolean isVisibleBlock() {
		return isBlock() && a.getType().isOccluding();
	}

	public void setOwnerFromWeb(String web) {
		if (web != null)
			url = web;
	}

	public void setOwnerFromValues(String values) {
		if (values != null)
			text = values;
	}

	public void setMaterial(String byName) {
		try {
			a.setType(Material.matchMaterial(byName));
		} catch (Exception e) {
			Validator.send("Material doesn't exist", e);
		}
	}

	public List<PotionEffect> getPotionEffects() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof PotionMeta)
				return ((PotionMeta) a.getItemMeta()).getCustomEffects();
		return new ArrayList<PotionEffect>();
	}

	public ItemMeta getItemMeta() {
		return a.getItemMeta();
	}

	public boolean hasPotionEffects() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof PotionMeta)
				return ((PotionMeta) a.getItemMeta()).hasCustomEffects();
		return false;
	}

	public boolean hasPotionEffect(PotionEffectType type) {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof PotionMeta)
				return ((PotionMeta) a.getItemMeta()).hasCustomEffect(type);
		return false;
	}

	public boolean hasPotionColor() {
		try {
			if (a.hasItemMeta())
				if (a.getItemMeta() instanceof PotionMeta)
					return ((PotionMeta) a.getItemMeta()).hasColor();
			return false;
		} catch (Exception | NoSuchMethodError er) {
			return false;
		}
	}

	public void addPotionEffect(PotionEffect effect) {
		if (effect != null) {
			addPotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier());
			try {
				setPotionColor(effect.getColor());
			}catch(Exception | NoSuchFieldError | NoSuchMethodError e) {
			}
		}
	}

	public void addPotionEffect(PotionEffectType potionEffect, int duration, int amlifier) {
		if (potionEffect != null)
			ef.put(potionEffect, duration+":"+amlifier);
	}

	public void addPotionEffect(String potionEffect, int duration, int amlifier) {
		addPotionEffect(PotionEffectType.getByName(potionEffect), duration, amlifier);
	}

	public Color getPotionColor() {
		try {
			if (a.hasItemMeta())
				if (a.getItemMeta() instanceof PotionMeta)
					return ((PotionMeta) a.getItemMeta()).getColor();
			return null;
		} catch (Exception | NoSuchMethodError er) {
			return null;
		}
	}

	public void setPotionColor(Color color) {
		if (color != null)
			c = color;
	}

	public void setDisplayName(String newName) {
		if (newName != null)
			name = TheAPI.colorize(newName);
	}

	public String getDisplayName() {
		if (a.hasItemMeta())
			return a.getItemMeta().getDisplayName();
		return null;
	}

	public void addLore(String line) {
		if (line != null)
			lore.add(TheAPI.colorize(line));
	}

	public List<String> getLore() {
		if (a.hasItemMeta())
			return a.getItemMeta().getLore();
		return new ArrayList<String>();
	}

	public String getOwner() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof SkullMeta)
				return ((SkullMeta) a.getItemMeta()).getOwner();
		return null;
	}

	public void setOwner(String owner) {
		if (owner != null)
			this.owner = owner;
	}

	public HashMap<Enchantment, Integer> getEnchantments() {
		HashMap<Enchantment, Integer> e = new HashMap<>();
		for (Enchantment d : a.getEnchantments().keySet())
			e.put(d, a.getEnchantments().get(d).intValue());
		return e;
	}

	public void addEnchantment(Enchantment e, int level) {
		if (e != null)
			enchs.put(e, level);
	}

	public void addEnchantment(String e, int level) {
		if (e != null && EnchantmentAPI.byName(e) != null)
			enchs.put(EnchantmentAPI.byName(e).getEnchantment(), level);
	}

	public int getAmount() {
		return a.getAmount();
	}

	public void setAmount(int amount) {
		if (amount > 64)
			amount = 64;
		s = amount;
	}

	public void setLore(List<String> lore) {
		if (lore != null && lore.isEmpty() == false)
			for (String s : lore)
				addLore(s);
	}

	public int getCustomModelData() {
		try {
			return a.getItemMeta().getCustomModelData();
		} catch (Exception | NoSuchMethodError er) {
			return -1;
		}
	}

	public void setCustomModelData(int i) {
		model = i;
	}

	public boolean isUnbreakable() {
		try {
			return a.getItemMeta().isUnbreakable();
		} catch (Exception | NoSuchMethodError er) {
			return hasLore() && getLore().contains(TheAPI.colorize("&9UNBREAKABLE"));
		}
	}

	public void setUnbreakable(boolean unbreakable) {
		unb = unbreakable;
	}

	public SkullType getSkullType() {
		if (a.getItemMeta() instanceof SkullMeta) {
			return getSkullFromInt(a.getDurability());
		}
		return null;
	}

	private SkullType getSkullFromInt(int i) {
		return SkullType.values()[i];
	}

	public void setSkullType(SkullType t) {
		if (t != null)
			type = t;
	}

	public void setSkullType(int t) {
		if (getSkullFromInt(t) != null)
			type = getSkullFromInt(t);
	}

	public void setSkullType(String t) {
		if (getSkullFromInt(getSkullInt(t)) != null)
			type = getSkullFromInt(getSkullInt(t));
	}

	public List<ItemFlag> getItemFlags() {
		try {
			List<ItemFlag> items = new ArrayList<ItemFlag>();
			if (a.hasItemMeta())
				for (ItemFlag f : a.getItemMeta().getItemFlags())
					items.add(f);
			return items;
		} catch (Exception | NoSuchMethodError er) {
			return null;
		}
	}

	public void addItemFlag(ItemFlag... itemflag) {
		if (itemflag != null)
			for(ItemFlag f : itemflag)
			map.add(f);
	}

	@SuppressWarnings("unchecked")
	public HashMap<Attribute, AttributeModifier> getAttributeModifiers() {
		try {
			HashMap<Attribute, AttributeModifier> h = new HashMap<Attribute, AttributeModifier>();
			try {
				if (hasAttributeModifiers()) {
					HashMap<Attribute, AttributeModifier> map = (HashMap<Attribute, AttributeModifier>) a.getItemMeta()
							.getAttributeModifiers();
					for (Attribute a : map.keySet())
						h.put(a, map.get(a));
				}
				return h;
			} catch (Exception | NoSuchMethodError er) {
				return h;
			}
		} catch (Exception | NoSuchMethodError er) {
			return null;
		}
	}

	public void addAttributeModifier(Attribute a, AttributeModifier s) {
		try {
			if (TheAPI.isNewVersion() && !TheAPI.getServerVersion().equals("v1_13_R1") && a != null && s != null)
				w.put(a, s);
		} catch (Exception | NoSuchMethodError er) {
		}
	}

	public void addAttributeModifiers(HashMap<Attribute, AttributeModifier> s) {
		if (TheAPI.isNewVersion() && !TheAPI.getServerVersion().equals("v1_13_R1") && s != null)
			for (Attribute r : s.keySet()) {
				addAttributeModifier(r, s.get(r));
			}
	}

	public short getDurability() {
		return a.getDurability();
	}

	public void setDurability(int amount) {
		dur = amount;
	}

	public MaterialData getMaterialData() {
		try {
			return a.getData();
		} catch (Exception er) {
			return null;
		}
	}

	public void setMaterialData(MaterialData data) {
		this.data = data;
	}

	public boolean hasDisplayName() {
		if (a.hasItemMeta())
			return a.getItemMeta().hasDisplayName();
		return false;
	}

	public boolean hasLore() {
		if (a.hasItemMeta())
			return a.getItemMeta().hasLore();
		return false;
	}

	public boolean hasEnchants() {
		if (a.hasItemMeta())
			return a.getItemMeta().hasEnchants();
		return false;
	}

	public boolean hasCustomModelData() {
		try {
			return a.getItemMeta().hasCustomModelData();
		} catch (Exception | NoSuchMethodError er) {
			return false;
		}
	}

	public boolean hasAttributeModifiers() {
		try {
			return a.getItemMeta().hasAttributeModifiers();
		} catch (Exception | NoSuchMethodError err) {
			return false;
		}
	}

	public boolean hasItemFlag(ItemFlag flag) {
		try {
			return a.getItemMeta().hasItemFlag(flag);
		} catch (Exception | NoSuchMethodError e) {
			return false;
		}
	}

	public boolean hasConflictingEnchant(Enchantment ench) {
		if (a.hasItemMeta())
			return a.getItemMeta().hasConflictingEnchant(ench);
		return false;
	}

	public boolean hasEnchant(Enchantment ench) {
		if (a.hasItemMeta())
			return a.getItemMeta().hasEnchant(ench);
		return false;
	}

	public String getBookAuthor() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof BookMeta) {
				return ((BookMeta) a.getItemMeta()).getAuthor();
			}
		return null;
	}

	public boolean hasBookAuthor() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof BookMeta) {
				return ((BookMeta) a.getItemMeta()).hasAuthor();
			}
		return false;
	}

	public void setBookAuthor(String author) {
		if (author != null)
			this.author = TheAPI.colorize(author);
	}

	public boolean hasBookTitle() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof BookMeta) {
				return ((BookMeta) a.getItemMeta()).hasTitle();
			}
		return false;
	}

	public String getBookTitle() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof BookMeta) {
				return ((BookMeta) a.getItemMeta()).getTitle();
			}
		return null;
	}

	public void setBookTitle(String title) {
		if (title != null)
			this.title = TheAPI.colorize(title);
	}

	public List<String> getBookPages() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof BookMeta) {
				return ((BookMeta) a.getItemMeta()).getPages();
			}
		return new ArrayList<String>();
	}

	public String getBookPage(int page) {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof BookMeta) {
				return ((BookMeta) a.getItemMeta()).getPage(page);
			}
		return null;
	}

	public int getBookPageCount() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof BookMeta) {
				return ((BookMeta) a.getItemMeta()).getPageCount();
			}
		return 0;
	}

	public void addBookPage(String lines) {
		if (lines == null)
			lines = "";
		pages.add(TheAPI.colorize(lines));
	}

	public void addBookPage(int page, String lines) {
		if (lines == null && pages.get(page) != null)
			pages.remove(page);
		else
			pages.set(page, TheAPI.colorize(lines));
	}

	public void setBookPages(List<String> lines) {
		if (lines != null)
			for (String s : lines)
				addBookPage(s);
	}

	public boolean hasBookGeneration() {
		try {
			if (a.hasItemMeta())
				if (a.getItemMeta() instanceof BookMeta) {
					return ((BookMeta) a.getItemMeta()).hasGeneration();
				}
			return false;
		} catch (Exception | NoClassDefFoundError er) {
			return false;
		}
	}

	public Generation getBookGeneration() {
		try {
			if (a.hasItemMeta())
				if (a.getItemMeta() instanceof BookMeta) {
					return ((BookMeta) a.getItemMeta()).getGeneration();
				}
			return null;
		} catch (Exception | NoClassDefFoundError er) {
			return null;
		}
	}

	public void setBookGeneration(Generation generation) {
		try {
			if (generation != null)
				gen = generation;
		} catch (Exception | NoSuchMethodError e) {

		}
	}

	@SuppressWarnings("unchecked")
	public ItemStack create() {
		ItemStack i = a;
		try {
			if (type != null) {
				a.setDurability((short) type.ordinal());
			} else if (owner != null) {
				a.setDurability((short) SkullType.PLAYER.ordinal());
			} else {
				if (dur != -1)
					a.setDurability((short) dur);
			}
			i.setAmount(s);
			ItemMeta mf = i.getItemMeta();
			if (data != null)
				i.setData(data);

			if (name != null)
				mf.setDisplayName(name);
			if (model != -1 && TheAPI.isNewVersion() // 1.13+
					&& !TheAPI.getServerVersion().contains("v1_13"))
				mf.setCustomModelData(model);
			if (unb) {
				if (!TheAPI.isOlder1_9() && !TheAPI.getServerVersion().contains("v1_9")
						&& !TheAPI.getServerVersion().contains("v1_10"))
					mf.setUnbreakable(unb);
				else {
					addLore(" ");
					addLore("&9UNBREAKABLE");
				}
			}
			if (lore != null && !lore.isEmpty()) {
				List<String> lor = new ArrayList<String>();
				for (Object o : lore)
					lor.add(o.toString());
				mf.setLore(lor);
			}
			try {
				if (map != null && !map.isEmpty())
					for (Object f : map)
						mf.addItemFlags((ItemFlag) f);
				if (w != null && !w.isEmpty() && TheAPI.isNewVersion()
						&& !TheAPI.getServerVersion().equals("v1_13_R1")) {// 1.14+
					mf.setAttributeModifiers((Multimap<Attribute, AttributeModifier>) w);
				}
			} catch (Exception | NoSuchMethodError er) {
			}
			i.setItemMeta(mf);
			if (!i.getType().name().equalsIgnoreCase("ENCHANTED_BOOK")) {
				if (enchs != null && !enchs.keySet().isEmpty())
					for (Enchantment t : enchs.keySet()) {
						i.addUnsafeEnchantment(t, enchs.get(t));
					}
			}
			if (i.getType().name().equalsIgnoreCase("ENCHANTED_BOOK")) {
				EnchantmentStorageMeta m = (EnchantmentStorageMeta) i.getItemMeta();
				if (enchs != null && !enchs.keySet().isEmpty())
					for (Enchantment e : enchs.keySet())
						m.addStoredEnchant(e, enchs.get(e),true);
				i.setItemMeta(m);

				a = i;
			} else if (i.getType().name().equalsIgnoreCase("WRITABLE_BOOK")
					|| i.getType().name().equalsIgnoreCase("BOOK_AND_QUILL")) {
				BookMeta m = (BookMeta) i.getItemMeta();
				m.setAuthor(author);
				List<String> page = new ArrayList<String>();
				for (Object o : pages)
					page.add(o.toString());
				m.setPages(page);
				m.setTitle(title);
				try {
					m.setGeneration(gen);
				} catch (Exception | NoSuchMethodError e) {
				}
				i.setItemMeta(m);
			} else if (i.getType().name().startsWith("LINGERING_POTION_OF_")
					|| i.getType().name().startsWith("SPLASH_POTION_OF_")
					|| i.getType().name().startsWith("POTION_OF_")) {
				PotionMeta meta = (PotionMeta) i.getItemMeta();
				try {
					meta.setColor(c);
				} catch (Exception | NoSuchMethodError er) {
				}
				if (!ef.keySet().isEmpty())
					for (PotionEffectType t : ef.keySet()) {
						if (t == null)
							continue;
						int amp = StringUtils.getInt(ef.get(t).split(":")[1]);
						meta.addCustomEffect(new PotionEffect(t, StringUtils.getInt(ef.get(t).split(":")[0]), (amp <= 0 ? 1 : amp)), true);
						}
				i.setItemMeta(meta);
			} else if (type != null && type==SkullType.PLAYER) {
				SkullMeta m = (SkullMeta) i.getItemMeta();
				if (owner != null)
					m.setOwner(owner);
				if (url != null || text != null) {
					try {
						Object profile = Ref.createGameProfile(null, null);
						byte[] encodedData = null;
						try{
							if(url!=null)
							encodedData=Base64.getEncoder().encode(("{textures:{SKIN:{url:\""+url+"\"}}}").getBytes());
						}catch(Exception err) {
						}
						Ref.invoke(Ref.invoke(profile, "getProperties"), Ref.method(Ref.invoke(profile, "getProperties").getClass().getSuperclass(), "put", Object.class, Object.class), "textures", new Property("textures", encodedData!=null?new String(encodedData):text));
						Ref.set(m, "profile", profile);
					} catch (Exception | NoSuchMethodError e) {
					}
				}
				i.setItemMeta(m);
			}
		} catch (Exception | NoSuchMethodError err) {
			Validator.send("Creating ItemStack exception", err);
		}
		return i;
	}

}
