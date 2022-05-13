package me.devtec.theapi.bukkit.game;

import java.lang.reflect.Field;
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

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.json.Json;
import me.devtec.shared.utility.StreamUtils;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import net.md_5.bungee.api.chat.BaseComponent;

public class ItemMaker {
	private static Material skull = XMaterial.PLAYER_HEAD.parseMaterial();
	
	private Material material;
	private int amount = 1;
	private short damage;
	
	//additional
	private String displayName;
	private List<String> lore;
	private Map<Enchantment, Integer> enchants;
	private List<String> itemFlags;
	private int customModel;
	private boolean unbreakable;
	public byte data;
	
	protected ItemMaker(Material material) {
		this.material=material;
	}
	
	protected ItemMeta apply(ItemMeta meta) {
		if(displayName!=null)
			meta.setDisplayName(displayName);
		if(lore!=null)
			meta.setLore(lore);
		if(enchants!=null)
			for(Entry<Enchantment, Integer> s : enchants.entrySet())
				meta.addEnchant(s.getKey(), s.getValue(), true);
		if(itemFlags!=null)
			for(String flag : itemFlags)
				meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
		if(customModel!=0)
			meta.setCustomModelData(customModel);
		if(unbreakable)
			try {
				meta.setUnbreakable(unbreakable);
			}catch(NoSuchFieldError | Exception e) {
				try {
					Ref.invoke(Ref.invoke(meta, "spigot"), "setUnbreakable", unbreakable);
				}catch(NoSuchFieldError | Exception e2) {
					//unsupported
				}
			}
		return meta;
	}

	public ItemMaker amount(int amount) {
		this.amount=amount;
		return this;
	}

	public ItemMaker damage(int damage) {
		this.damage=(short)damage;
		return this;
	}

	public ItemMaker data(int data) {
		this.data=(byte)data;
		return this;
	}

	public ItemMaker displayName(String name) {
		displayName = StringUtils.colorize(name);
		return this;
	}

	public ItemMaker lore(String... lore) {
		return lore(Arrays.asList(lore));
	}

	public ItemMaker lore(List<String> lore) {
		this.lore=StringUtils.colorize(lore);
		return this;
	}

	public ItemMaker customModel(int customModel) {
		this.customModel=customModel;
		return this;
	}

	public ItemMaker unbreakable(boolean unbreakable) {
		this.unbreakable=unbreakable;
		return this;
	}

	public ItemMaker itemFlags(String... flag) {
		return itemFlags(Arrays.asList(flag));
	}

	public ItemMaker itemFlags(List<String> flag) {
		itemFlags=flag;
		return this;
	}

	public ItemMaker enchant(Enchantment enchant, int level) {
		if(enchants==null)enchants=new HashMap<>();
		enchants.put(enchant, level);
		return this;
	}
	
	public ItemStack build() {
		ItemStack item = data!=0?new ItemStack(material, amount, damage, data):new ItemStack(material, amount, damage);
		item.setItemMeta(apply(item.getItemMeta()));
		return item;
	}
	
	public static class HeadItemMaker extends ItemMaker {
		static final String URL_FORMAT = "https://api.mineskin.org/generate/url?url=%s&%s";
		static final Field profileField = Ref.field(Ref.craft("inventory.CraftMetaSkull"), "profile");
		
		private String owner;
		/**
		 * 0 = offlinePlayer
		 * 1 = player.values
		 * 2 = url.png
		 */
		private int ownerType;
		
		protected HeadItemMaker() {
			super(skull);
		}
		
		public HeadItemMaker skinName(String name) {
			owner=name;
			ownerType=0;
			return this;
		}
		
		public HeadItemMaker skinValues(String name) {
			owner=name;
			ownerType=1;
			return this;
		}
		
		public HeadItemMaker skinUrl(String name) {
			owner=name;
			ownerType=2;
			return this;
		}
		
		protected ItemMeta apply(ItemMeta meta) {
			SkullMeta iMeta = (SkullMeta)meta;
			if(owner!=null)
				switch(ownerType) {
				case 0:
					iMeta.setOwner(owner);
					break;
				case 1: {
					GameProfile profile = new GameProfile(UUID.randomUUID(), "DevTec");
					profile.getProperties().put("textures", new Property("textures", owner));
					Ref.set(iMeta, profileField, profile);
					break;
				}
				case 2: {
					GameProfile profile = new GameProfile(UUID.randomUUID(), "DevTec");
					profile.getProperties().put("textures", new Property("textures", fromUrl(owner)));
					Ref.set(iMeta, profileField, profile);
					break;
				}
				default:
					break;
				}
			return super.apply(iMeta);
		}
		
		@SuppressWarnings("unchecked")
		public static String fromUrl(String url) {
			try {
				java.net.URLConnection connection = new URL(url).openConnection();
				connection.setRequestProperty("User-Agent", "DevTec-JavaClient");
				HttpURLConnection conn = (HttpURLConnection)new URL(String.format(URL_FORMAT, url, "name=DevTec&model=steve&visibility=1")).openConnection();
				conn.setRequestProperty("User-Agent", "DevTec-JavaClient");
				conn.setRequestProperty("Accept-Encoding", "gzip");
				conn.setRequestMethod("POST");
				conn.connect();
				Map<String, Object> text = (Map<String, Object>) Json.reader().simpleRead(StreamUtils.fromStream(new GZIPInputStream(conn.getInputStream())));
				return (String) ((Map<String, Object>)((Map<String, Object>)text.get("data")).get("texture")).get("value");
			}catch(Exception err) {}
			return null;
		}
		
	}
	
	public static class LeatherItemMaker extends ItemMaker {
		private Color color;
		protected LeatherItemMaker(Material material) {
			super(material);
		}
		
		public LeatherItemMaker color(Color color) {
			this.color=color;
			return this;
		}
		
		protected ItemMeta apply(ItemMeta meta) {
			LeatherArmorMeta iMeta = (LeatherArmorMeta)meta;
			if(color!=null)
				iMeta.setColor(color);
			return super.apply(iMeta);
		}
	}
	
	public static class BookItemMaker extends ItemMaker {
		private String author;
		private String title;
		private List<Component> pages;
		private String generation;
		protected BookItemMaker() {
			super(Material.WRITTEN_BOOK);
		}
		
		public BookItemMaker author(String author) {
			this.author=StringUtils.colorize(author);
			return this;
		}
		
		public BookItemMaker title(String title) {
			this.title=StringUtils.colorize(title);
			return this;
		}
		
		public BookItemMaker generation(String generation) {
			this.generation=generation;
			return this;
		}
		
		public BookItemMaker pages(String... pages) {
			return pages(Arrays.asList(pages));
		}
		
		public BookItemMaker pages(List<String> pages) {
			this.pages=new ArrayList<>();
			for(String string : StringUtils.colorize(pages))
				this.pages.add(ComponentAPI.fromString(string));
			return this;
		}
		
		public BookItemMaker pagesComp(Component... pages) {
			return pagesComp(Arrays.asList(pages));
		}
		
		public BookItemMaker pagesComp(List<Component> pages) {
			this.pages=pages;
			return this;
		}
		
		protected ItemMeta apply(ItemMeta meta) {
			BookMeta iMeta = (BookMeta)meta;
			if(author!=null)
				iMeta.setAuthor(author);
			if(pages!=null) {
				if(Ref.serverType()==ServerType.BUKKIT) {
					List<String> page = new ArrayList<>(pages.size());
					for(Component comp : pages)page.add(comp.toString());
					iMeta.setPages(page);
				}else {
					for(Component page : pages)
						iMeta.spigot().addPage((BaseComponent[])ComponentAPI.bungee().fromComponents(page));
				}
			}
			if(generation!=null)
				iMeta.setGeneration(Generation.valueOf(generation.toUpperCase()));
			if(title!=null)
				iMeta.setTitle(title);
			return super.apply(iMeta);
		}
	}
	
	public static class EnchantedBookItemMaker extends ItemMaker {
		protected EnchantedBookItemMaker() {
			super(Material.ENCHANTED_BOOK);
		}
		
		protected ItemMeta apply(ItemMeta meta) {
			EnchantmentStorageMeta iMeta = (EnchantmentStorageMeta)meta;
			if(super.displayName!=null)
				iMeta.setDisplayName(super.displayName);
			if(super.lore!=null)
				iMeta.setLore(super.lore);
			if(super.enchants!=null)
				for(Entry<Enchantment, Integer> s : super.enchants.entrySet())
					iMeta.addStoredEnchant(s.getKey(), s.getValue(), true);
			if(super.itemFlags!=null)
				for(String flag : super.itemFlags)
					iMeta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
			if(super.customModel!=0)
				iMeta.setCustomModelData(super.customModel);
			if(super.unbreakable)
				try {
					iMeta.setUnbreakable(super.unbreakable);
				}catch(NoSuchFieldError | Exception e) {
					try {
						Ref.invoke(Ref.invoke(meta, "spigot"), "setUnbreakable", super.unbreakable);
					}catch(NoSuchFieldError | Exception e2) {
						//unsupported
					}
				}
			return iMeta;
		}
	}
	
	public static class PotionItemMaker extends ItemMaker {
		private Color color;
		private List<PotionEffect> effects;
		protected PotionItemMaker(Material material) {
			super(material);
		}
		
		public PotionItemMaker color(Color color) {
			this.color=color;
			return this;
		}
		
		public PotionItemMaker potionEffects(PotionEffect... effects) {
			return potionEffects(Arrays.asList(effects));
		}
		
		public PotionItemMaker potionEffects(List<PotionEffect> effects) {
			this.effects=effects;
			return this;
		}
		
		protected ItemMeta apply(ItemMeta meta) {
			PotionMeta iMeta = (PotionMeta)meta;
			if(color!=null)
				iMeta.setColor(color);
			if(effects!=null)
				for(PotionEffect effect : effects)
					iMeta.addCustomEffect(effect, true);
			return super.apply(iMeta);
		}
	}
	
	public static class ShulkerBoxItemMaker extends ItemMaker {
		private String name;
		private ItemStack[] contents;
		protected ShulkerBoxItemMaker(XMaterial xMaterial) {
			super(xMaterial.parseMaterial());
			super.data=xMaterial.getData();
		}
		
		public ShulkerBoxItemMaker name(String name) {
			this.name=name;
			return this;
		}
		
		public ShulkerBoxItemMaker contents(ItemStack[] contents) {
			this.contents=contents;
			return this;
		}
		
		protected ItemMeta apply(ItemMeta meta) {
			BlockStateMeta iMeta = (BlockStateMeta)meta;
			ShulkerBox shulker = (ShulkerBox)iMeta.getBlockState();
			if(name!=null)
				shulker.setCustomName(name);
			if(contents!=null)
				shulker.getInventory().setContents(contents);
			iMeta.setBlockState(shulker);
			return super.apply(iMeta);
		}
	}
	
	public static class BundleItemMaker extends ItemMaker {
		private List<ItemStack> contents;
		protected BundleItemMaker() {
			super(Material.getMaterial("BUNDLE"));
		}
		
		public BundleItemMaker contents(ItemStack... contents) {
			return contents(Arrays.asList(contents));
		}
		
		public BundleItemMaker contents(List<ItemStack> contents) {
			this.contents=contents;
			return this;
		}
		
		protected ItemMeta apply(ItemMeta meta) {
			BundleMeta iMeta = (BundleMeta)meta;
			if(contents!=null)
				iMeta.setItems(contents);
			return super.apply(iMeta);
		}
	}
	
	public static class BannerItemMaker extends ItemMaker {
		private List<Pattern> patterns;
		protected BannerItemMaker(XMaterial xMaterial) {
			super(xMaterial.parseMaterial());
			super.data=xMaterial.getData();
		}
		
		public BannerItemMaker patterns(Pattern... contents) {
			return patterns(Arrays.asList(contents));
		}
		
		public BannerItemMaker patterns(List<Pattern> patterns) {
			this.patterns=patterns;
			return this;
		}
		
		protected ItemMeta apply(ItemMeta meta) {
			BannerMeta iMeta = (BannerMeta)meta;
			if(patterns!=null)
				iMeta.setPatterns(patterns);
			return super.apply(iMeta);
		}
	}

	public static ItemMaker of(Material material) {
		return new ItemMaker(material);
	}

	public static HeadItemMaker ofHead() {
		return new HeadItemMaker();
	}

	public static LeatherItemMaker ofLeatherArmor(Material material) {
		return new LeatherItemMaker(skull);
	}

	public static BookItemMaker ofBook() {
		return new BookItemMaker();
	}

	public static EnchantedBookItemMaker ofEnchantedBook() {
		return new EnchantedBookItemMaker();
	}
	
	static enum Potion {
		LINGERING(Material.getMaterial("LINGERING_POTION")), SPLASH(Material.getMaterial("SPLASH_POTION")), POTION(Material.POTION);

		private Material m;
		Potion(Material mat) {
			m=mat;
		}
		
		public Material toMaterial() {
			return m;
		}
	}
	
	public static PotionItemMaker ofPotion(Potion potionType) {
		return new PotionItemMaker(potionType.toMaterial());
	}
	
	static enum ShulkerBoxColor {
		NONE(XMaterial.SHULKER_BOX), WHITE(XMaterial.WHITE_SHULKER_BOX), BLACK(XMaterial.BLACK_SHULKER_BOX), BLUE(XMaterial.BLUE_SHULKER_BOX), BROWN(XMaterial.BROWN_SHULKER_BOX),
		CYAN(XMaterial.CYAN_SHULKER_BOX), GRAY(XMaterial.GRAY_SHULKER_BOX), GREEN(XMaterial.GREEN_SHULKER_BOX), LIGHT_BLUE(XMaterial.LIGHT_BLUE_SHULKER_BOX),
		LIGHT_GRAY(XMaterial.LIGHT_GRAY_SHULKER_BOX), LIME(XMaterial.LIME_SHULKER_BOX), MAGENTA(XMaterial.MAGENTA_SHULKER_BOX), ORANGE(XMaterial.ORANGE_SHULKER_BOX),
		YELLOW(XMaterial.YELLOW_SHULKER_BOX), RED(XMaterial.RED_SHULKER_BOX), PURPLE(XMaterial.PURPLE_SHULKER_BOX), PINK(XMaterial.PINK_SHULKER_BOX);
		
		private XMaterial m;
		ShulkerBoxColor(XMaterial mat){
			m=mat;
		}
		
		public XMaterial toMaterial() {
			return m;
		}
	}

	public static ShulkerBoxItemMaker ofShulkerBox(ShulkerBoxColor color) {
		return new ShulkerBoxItemMaker(color.toMaterial());
	}

	public static BundleItemMaker ofBundle() {
		return new BundleItemMaker();
	}
	
	static enum BannerColor {
		NONE(XMaterial.WHITE_BANNER), WHITE(XMaterial.WHITE_BANNER), BLACK(XMaterial.BLACK_BANNER), BLUE(XMaterial.BLUE_BANNER), BROWN(XMaterial.BROWN_BANNER),
		CYAN(XMaterial.CYAN_BANNER), GRAY(XMaterial.GRAY_BANNER), GREEN(XMaterial.GREEN_BANNER), LIGHT_BLUE(XMaterial.LIGHT_BLUE_BANNER),
		LIGHT_GRAY(XMaterial.LIGHT_GRAY_BANNER), LIME(XMaterial.LIME_BANNER), MAGENTA(XMaterial.MAGENTA_BANNER), ORANGE(XMaterial.ORANGE_BANNER),
		YELLOW(XMaterial.YELLOW_BANNER), RED(XMaterial.RED_BANNER), PURPLE(XMaterial.PURPLE_BANNER), PINK(XMaterial.PINK_BANNER);
		
		private XMaterial m;
		BannerColor(XMaterial mat){
			m=mat;
		}
		
		public XMaterial toMaterial() {
			return m;
		}
	}
	
	public static BannerItemMaker ofBanner(BannerColor color) {
		return new BannerItemMaker(color.toMaterial());
	}
}
