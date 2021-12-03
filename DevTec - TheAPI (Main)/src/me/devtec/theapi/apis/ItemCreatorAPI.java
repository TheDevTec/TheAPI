package me.devtec.theapi.apis;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
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
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StreamUtils;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.json.Json;
import me.devtec.theapi.utils.nms.nbt.NBTEdit;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import me.devtec.theapi.utils.theapiutils.Validator;

public class ItemCreatorAPI implements Cloneable {

	// Simple creation

	public static ItemStack create(Material material, int amount, String displayName) {
		return create(material, amount, displayName, null, 0);
	}

	public static ItemStack create(Material material, int amount, String displayName, List<String> lore) {
		return create(material, amount, displayName, lore, 0);
	}

	public static ItemStack create(Material material, int amount, String displayName, int data) {
		return create(material, amount, displayName, null, data);
	}

	public static ItemStack create(Material material, int amount, String displayName, List<String> lore, int data) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount, (byte) data));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setDurability(data);
		return a.create();
	}

	public static ItemStack createPotion(Material material, int amount, String displayName, PotionEffect... effects) {
		return createPotion(material, amount, displayName, null, null, effects);
	}

	public static ItemStack createPotion(Material material, int amount, String displayName, List<String> lore,
			PotionEffect... effects) {
		return createPotion(material, amount, displayName, lore, null, effects);
	}

	public static ItemStack createPotion(Material material, int amount, String displayName, Color color,
			PotionEffect... effects) {
		return createPotion(material, amount, displayName, null, color, effects);
	}

	public static ItemStack createPotion(Material material, int amount, String displayName, List<String> lore,
			Color color, PotionEffect... effects) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		for (PotionEffect effect : effects)
			a.addPotionEffect(effect);
		a.setColor(color);
		return a.create();
	}

	public static ItemStack createLeatherArmor(Material material, int amount, String displayName, Color color) {
		return createLeatherArmor(material, amount, displayName, null, color);
	}

	public static ItemStack createLeatherArmor(Material material, int amount, String displayName, List<String> lore,
			Color color) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setColor(color);
		return a.create();
	}

	public static ItemStack createBook(Material material, int amount, String displayName, String author, String title,
			List<String> pages) {
		return createBook(material, amount, displayName, null, author, title, pages, null);
	}

	public static ItemStack createBook(Material material, int amount, String displayName, List<String> lore,
			String author, String title, List<String> pages) {
		return createBook(material, amount, displayName, lore, author, title, pages, null);
	}

	// Only for newer minecraft versions
	public static ItemStack createBook(Material material, int amount, String displayName, String author, String title,
			List<String> pages, Generation gen) {
		return createBook(material, amount, displayName, null, author, title, pages, gen);
	}

	// Only for newer minecraft versions
	public static ItemStack createBook(Material material, int amount, String displayName, List<String> lore,
			String author, String title, List<String> pages, Generation gen) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(material, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setBookAuthor(author);
		a.setBookTitle(title);
		a.setBookPages(pages);
		a.setBookGeneration(gen);
		return a.create();
	}

	private static Material mat;
	static {
		try {
			mat = Material.PLAYER_HEAD;
		} catch (Exception | NoSuchFieldError e) {
			mat = Material.getMaterial("SKULL_ITEM");
		}
	}

	public static ItemStack createHead(int amount, String displayName, String owner) {
		return createHead(amount, displayName, owner, null);
	}

	public static ItemStack createHead(int amount, String displayName, String owner, List<String> lore) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setOwner(owner);
		a.setSkullType(SkullType.PLAYER);
		return a.create();
	}

	public static ItemStack createHead(int amount, String displayName, SkullType type) {
		return createHead(amount, displayName, null, type);
	}

	public static ItemStack createHead(int amount, String displayName, List<String> lore, SkullType type) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setSkullType(type);
		return a.create();
	}

	public static ItemStack createHeadByValues(int amount, String displayName, String ownerValues) {
		return createHeadByValues(amount, displayName, null, ownerValues);
	}

	public static ItemStack createHeadByValues(int amount, String displayName, List<String> lore, String ownerValues) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setSkullType(SkullType.PLAYER);
		a.setOwnerFromValues(ownerValues);
		return a.create();
	}

	public static ItemStack createHeadByWeb(int amount, String displayName, String ownerLink) {
		return createHeadByWeb(amount, displayName, null, ownerLink);
	}

	public static ItemStack createHeadByWeb(int amount, String displayName, List<String> lore, String ownerLink) {
		ItemCreatorAPI a = new ItemCreatorAPI(new ItemStack(mat, amount));
		a.setDisplayName(displayName);
		a.setLore(lore);
		a.setSkullType(SkullType.PLAYER);
		a.setOwnerFromWeb(ownerLink);
		return a.create();
	}

	private ItemStack a;
	private String author = "", title = "", name, owner, url, text;
	private Color c;
	private boolean unb;
	private SkullType type;
	private final HashMap<Attribute, AttributeModifier> w = new HashMap<>();
	private int s, model, dur;
	private final HashMap<PotionEffectType, String> ef = new HashMap<>();
	private final HashMap<Enchantment, Integer> enchs = new HashMap<>();
	private final List<Object> pages = new ArrayList<>();
	private List<Object> lore = new ArrayList<>();
	private final List<Object> map = new ArrayList<>();
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
		if (hasColor())
			c = getColor();
		if (hasDisplayName())
			name = getDisplayName();
		owner = getOwner();
		text = getOwnerByValues();
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
			map.addAll(getItemFlags());
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

	private static Method get = Ref.method(Ref.getClass("com.google.common.collect.ForwardingMultimap"), "get", Object.class);
	private static Method set = Ref.method(Ref.getClass("com.google.common.collect.ForwardingMultimap"), "put", Object.class, Object.class);
	
	static {
		if(get==null)
			get = Ref.method(Ref.getClass("net.minecraft.util.com.google.common.collect.ForwardingMultimap"), "get", Object.class);
		if(set==null)
			set = Ref.method(Ref.getClass("net.minecraft.util.com.google.common.collect.ForwardingMultimap"), "put", Object.class, Object.class);
	}
	
	public String getOwnerByValues() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof SkullMeta)
				return (String) Ref.invoke(Ref
								.invoke(Ref.invoke(Ref.get(a.getItemMeta(), "profile"), "getProperties"),get,"textures"),"getValue");
		return text;
	}

	public Material getMaterial() {
		return a.getType();
	}

	public void setMaterial(Material mat) {
		if(mat!=null)
			a.setType(mat);
	}

	public boolean isItem(boolean canBeLegacy) {
		String s = a.getType().name();
		return !s.contains("WALL_") && !isAir() && !s.contains("_STEM") && !s.contains("POTTED_")
				&& (canBeLegacy || !s.contains("LEGACY_")) && !s.equals("END_PORTAL") && !s.equals("END_GATEWAY")
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
			a.setType(Material.getMaterial(byName.toUpperCase()));
		} catch (Exception e) {
			Validator.send("Material doesn't exist", e);
		}
	}

	public List<PotionEffect> getPotionEffects() {
		if (a.hasItemMeta())
			if (a.getItemMeta() instanceof PotionMeta)
				return ((PotionMeta) a.getItemMeta()).getCustomEffects();
		return new ArrayList<>();
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

	public boolean hasColor() {
		try {
			if (a.hasItemMeta()) {
				if (a.getItemMeta() instanceof PotionMeta)
					return ((PotionMeta) a.getItemMeta()).hasColor();
				if (a.getItemMeta() instanceof LeatherArmorMeta)
					return ((LeatherArmorMeta) a.getItemMeta()).getColor() != null;
			}
			return false;
		} catch (Exception | NoSuchMethodError er) {
			return false;
		}
	}

	public void addPotionEffect(PotionEffect effect) {
		if (effect != null) {
			addPotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier());
			try {
				setColor(effect.getColor());
			} catch (Exception | NoSuchFieldError | NoSuchMethodError e) {
			}
		}
	}

	public void addPotionEffect(PotionEffectType potionEffect, int duration, int amlifier) {
		if (potionEffect != null)
			ef.put(potionEffect, duration + ":" + amlifier);
	}

	public void addPotionEffect(String potionEffect, int duration, int amlifier) {
		addPotionEffect(PotionEffectType.getByName(potionEffect), duration, amlifier);
	}

	public Color getColor() {
		try {
			if (a.hasItemMeta()) {
				if (a.getItemMeta() instanceof PotionMeta)
					return ((PotionMeta) a.getItemMeta()).getColor();
				if (a.getItemMeta() instanceof LeatherArmorMeta)
					return ((LeatherArmorMeta) a.getItemMeta()).getColor();
			}
			return null;
		} catch (Exception | NoSuchMethodError er) {
			return null;
		}
	}

	public void setColor(Color color) {
		if (color != null)
			c = color;
	}

	public void setDisplayName(String newName) {
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
		return new ArrayList<>();
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

	public Map<Enchantment, Integer> getEnchantments() {
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
		if (EnchantmentAPI.byName(e) != null)
			enchs.put(EnchantmentAPI.byName(e).getEnchantment(), level);
	}

	public int getAmount() {
		return a.getAmount();
	}

	public void setAmount(int amount) {
		s = amount;
	}

	public void setLore(List<String> lore) {
		if (lore != null) {
			this.lore.clear();
			for (String s : lore)
				addLore(s);
		}else this.lore =null;
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
		if(!a.hasItemMeta())return false;
		try {
			return a.getItemMeta().isUnbreakable();
		} catch (Exception | NoSuchMethodError er) {
			try {
			return (boolean) Ref.invoke(Ref.invoke(a.getItemMeta(), "spigot"),"isUnbreakable");
			} catch (Exception | NoSuchMethodError err) { //use our own wave
				return new NBTEdit(a).getBoolean("unbreakable");
			}
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
			List<ItemFlag> items = new ArrayList<>();
			if (a.hasItemMeta())
				items.addAll(a.getItemMeta().getItemFlags());
			return items;
		} catch (Exception | NoSuchMethodError er) {
			return null;
		}
	}

	public void addItemFlag(ItemFlag... itemflag) {
		if (itemflag != null)
			map.addAll(Arrays.asList(itemflag));
	}

	public Map<Attribute, AttributeModifier> getAttributeModifiers() {
		try {
			Map<Attribute, AttributeModifier> h = new HashMap<>();
			try {
				if (hasAttributeModifiers()) {
					Multimap<Attribute, AttributeModifier> map = a.getItemMeta().getAttributeModifiers();
					for (Entry<Attribute, AttributeModifier> a : map.entries())
						h.put(a.getKey(), a.getValue());
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
			if (TheAPI.isNewerThan(13) && a != null && s != null)
				w.put(a, s);
		} catch (Exception | NoSuchMethodError er) {
		}
	}

	public void addAttributeModifiers(Map<Attribute, AttributeModifier> s) {
		if (TheAPI.isNewerThan(13) && s != null)
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
		return new ArrayList<>();
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

	public ItemStack create() {
		ItemStack i = a;
		if(i.getType().name().equals("LEGACY_SKULL_ITEM")||i.getType().name().equals("LEGACY_SKULL")||i.getType().name().equals("SKULL_ITEM")||i.getType().name().equals("SKULL")||i.getType().name().contains("_HEAD")) {
			if(type==null)
				setSkullType(dur);
		}
		
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
			if (model != -1 && TheAPI.isNewerThan(13))
				mf.setCustomModelData(model);
			if (unb) {
				if (TheAPI.isNewerThan(10))
					mf.setUnbreakable(unb);
				else {
					try { //spigot version
						Ref.invoke(Ref.invoke(mf, "spigot"),"setUnbreakable", unb);
					} catch (Exception | NoSuchMethodError errr) { //use our own wave - craft bukkit
						a.setItemMeta(mf);
						NBTEdit edit = new NBTEdit(a);
						edit.setBoolean("unbreakable", unb);
						a=LoaderClass.nmsProvider.setNBT(a, edit);
						mf=a.getItemMeta();
					}
				}
			}
			if (lore != null && !lore.isEmpty()) {
				List<String> lor = new ArrayList<>();
				for (Object o : lore)
					lor.add(o+"");
				mf.setLore(lor);
			}
			try {
				try {
				if (map != null)
					for (Object f : map)
						mf.addItemFlags((ItemFlag) f);
				}catch(Exception | NoSuchFieldError | NoClassDefFoundError | NoSuchMethodError e) {}
				if (w != null && !w.isEmpty() && TheAPI.isNewerThan(13)) {// 1.14+
					Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
					for(Entry<Attribute, AttributeModifier> e : w.entrySet())
					multimap.put(e.getKey(), e.getValue());
					mf.setAttributeModifiers(multimap);
				}
			} catch (Exception | NoSuchMethodError er) {
			}
			i.setItemMeta(mf);
			if (!i.getType().name().equalsIgnoreCase("ENCHANTED_BOOK")) {
				if (enchs != null)
					i.addUnsafeEnchantments(enchs);
			} else {
				EnchantmentStorageMeta m = (EnchantmentStorageMeta) i.getItemMeta();
				if (enchs != null)
					for (Enchantment e : enchs.keySet())
						m.addStoredEnchant(e, enchs.get(e), true);
				i.setItemMeta(m);
			}
			if (i.getType().name().equalsIgnoreCase("WRITABLE_BOOK")
					|| i.getType().name().equalsIgnoreCase("BOOK_AND_QUILL")) {
				BookMeta m = (BookMeta) i.getItemMeta();
				m.setAuthor(author);
				List<String> page = new ArrayList<>();
				for (Object o : pages)
					page.add(o+"");
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
						meta.addCustomEffect(
								new PotionEffect(t, StringUtils.getInt(ef.get(t).split(":")[0]), (amp <= 0 ? 1 : amp)),
								true);
					}
				i.setItemMeta(meta);
			} else if (i.getType().name().startsWith("LEATHER_")) {
				try {
					LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
					meta.setColor(c);
					i.setItemMeta(meta);
				} catch (Exception | NoSuchMethodError er) {
				}
			} else if (type != null && type == SkullType.PLAYER) {
				SkullMeta m = (SkullMeta) i.getItemMeta();
				if (owner != null && !owner.trim().isEmpty() && url == null && text == null) {
					if(Bukkit.getOfflinePlayer(owner)!=null && Bukkit.getOfflinePlayer(owner).getFirstPlayed()>0)
						m.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
					else {
						SkinData data = generateSkin(owner);
						if(data!=null) {
							if(TheAPI.isOlderThan(8)) {
								Object profile = Ref.createGameProfile(null, owner!=null && !owner.trim().isEmpty()?owner:"TheAPI");
								Ref.invoke(Ref.invoke(profile, "getProperties"), set, "textures", Ref.createProperty("textures", data.value, data.signature));
								Ref.set(m, "profile", profile);
							}else {
								GameProfile profile = new GameProfile(UUID.randomUUID(), owner);
								profile.getProperties().put("textures", new Property("textures", data.value, data.signature));
								Ref.set(m, "profile", profile);
								if(TheAPI.isNewerThan(15))
									Ref.invoke(m, setProfile, profile);
							}
						}
					}
				}
				if (url != null || text != null) {
					if(TheAPI.isOlderThan(8)) {
						Object profile = Ref.createGameProfile(null, owner!=null && !owner.trim().isEmpty()?owner:"TheAPI");
						if(url!=null) {
							SkinData data = generateSkin(url);
							if(data!=null)
							Ref.invoke(Ref.invoke(profile, "getProperties"), set, "textures", Ref.createProperty("textures", data.value, data.signature));
						}else
							Ref.invoke(Ref.invoke(profile, "getProperties"), set, "textures", Ref.createProperty("textures", text));
						Ref.set(m, "profile", profile);
					}else {
						GameProfile profile = new GameProfile(UUID.randomUUID(), owner!=null && !owner.trim().isEmpty()?owner:"TheAPI");
						if(url!=null) {
							SkinData data = generateSkin(url);
							if(data!=null)
							profile.getProperties().put("textures", new Property("textures", data.value, data.signature));
						}else
							profile.getProperties().put("textures", new Property("textures", text));
						Ref.set(m, "profile", profile);
						if(TheAPI.isNewerThan(15))
							Ref.invoke(m, setProfile, profile);
					}
				}
				i.setItemMeta(m);
			}
		} catch (Exception | NoSuchMethodError err) {
			Validator.send("Creating ItemStack exception", err);
		}
		a=i;
		return i;
	}
	
	public static class SkinData {
		public String value;
		public String signature;
		
		public long lastUpdate = System.currentTimeMillis()/1000;
		
		public boolean isFinite() {
			return value != null && signature != null;
		}
		
		public String toString() {
			HashMap<String, String> data = new HashMap<>();
			data.put("texture.value", value);
			data.put("texture.signature", signature);
			return Json.writer().simpleWrite(data);
		}
	}

	private static final String URL_FORMAT = "https://api.mineskin.org/generate/url?url=%s&%s",
			USER_FORMAT="https://api.ashcon.app/mojang/v2/user/%s";
	private static String getSkinType(java.awt.image.BufferedImage image) {
		final byte[] pixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
		int argb = ((int) pixels[4002] & 0xff);
		argb += (((int) pixels[4003] & 0xff) << 8);
		argb += (((int) pixels[4004] & 0xff) << 16);
		return argb==2631720?"steve":"alex";
    }
	
	@SuppressWarnings("unchecked")
	public static synchronized SkinData generateSkin(String urlOrName) {
		if(urlOrName==null)return null;
		if(urlOrName.toLowerCase().startsWith("https://")||urlOrName.toLowerCase().startsWith("http://")) {
			try {
				java.net.URLConnection connection = new URL(urlOrName).openConnection();
				connection.setRequestProperty("User-Agent", "ServerControlReloaded-JavaClient");
				HttpURLConnection conn = (HttpURLConnection)new URL(String.format(URL_FORMAT, urlOrName, "name=DevTec&model="+getSkinType(ImageIO.read(connection.getInputStream()))+"&visibility=1")).openConnection();
				conn.setRequestProperty("User-Agent", "TheAPI-JavaClient");
				conn.setRequestProperty("Accept-Encoding", "gzip");
				conn.setRequestMethod("POST");
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				conn.connect();
				Map<String, Object> text = (Map<String, Object>) Json.reader().simpleRead(StreamUtils.fromStream(new GZIPInputStream(conn.getInputStream())));
				SkinData data = new SkinData();
				if(!text.containsKey("error")) {
					data.signature=(String) ((Map<String, Object>)((Map<String, Object>)text.get("data")).get("texture")).get("signature");
					data.value=(String) ((Map<String, Object>)((Map<String, Object>)text.get("data")).get("texture")).get("value");
				}
				return data;
			}catch(Exception err) {}
		}
		try {
			HttpURLConnection conn = (HttpURLConnection)new URL(String.format(USER_FORMAT, urlOrName)).openConnection();
			conn.setRequestProperty("User-Agent", "TheAPI-JavaClient");
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.connect();
			Map<String, Object> text = (Map<String, Object>) Json.reader().simpleRead(StreamUtils.fromStream(conn.getInputStream()));
			SkinData data = new SkinData();
			if(!text.containsKey("error")) {
				data.signature=(String) ((Map<String, Object>)((Map<String, Object>)text.get("textures")).get("raw")).get("signature");
				data.value=(String) ((Map<String, Object>)((Map<String, Object>)text.get("textures")).get("raw")).get("value");
			}
			return data;
		}catch(Exception err) {}
		return null;
	}
	
	static Method setProfile = Ref.method(Ref.craft("inventory.CraftMetaSkull"), "setProfile", Ref.getClass("com.mojang.authlib.GameProfile") != null
			? Ref.getClass("com.mojang.authlib.GameProfile")
			: Ref.getClass("net.minecraft.util.com.mojang.authlib.GameProfile"));

	@Override
	public ItemCreatorAPI clone() {
		try {
			return (ItemCreatorAPI) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
