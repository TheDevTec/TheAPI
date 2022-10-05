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

import javax.annotation.Nullable;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
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
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.json.Json;
import me.devtec.shared.utility.StreamUtils;
import me.devtec.shared.utility.StringUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.nms.GameProfileHandler;
import me.devtec.theapi.bukkit.nms.GameProfileHandler.PropertyHandler;
import me.devtec.theapi.bukkit.nms.NBTEdit;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import net.md_5.bungee.api.chat.BaseComponent;

public class ItemMaker {
	private static Material skull = XMaterial.PLAYER_HEAD.parseMaterial();

	private Material material;
	private int amount = 1;
	private short damage;

	// additional
	private String displayName;
	private List<String> lore;
	private Map<Enchantment, Integer> enchants;
	private List<String> itemFlags;
	private int customModel;
	private boolean unbreakable;
	public byte data;
	private NBTEdit nbt;

	protected ItemMaker(Material material) {
		this.material = material;
	}

	protected ItemMeta apply(ItemMeta meta) {
		if (displayName != null)
			meta.setDisplayName(displayName);
		if (lore != null)
			meta.setLore(lore);
		if (enchants != null)
			for (Entry<Enchantment, Integer> s : enchants.entrySet())
				meta.addEnchant(s.getKey(), s.getValue(), true);
		if (Ref.isNewerThan(7) && itemFlags != null)
			for (String flag : itemFlags) {
				ItemFlag iFlag = ItemFlag.valueOf(flag.toUpperCase());
				if (iFlag != null)
					meta.addItemFlags(iFlag);
			}
		if (Ref.isNewerThan(13) && customModel != 0)
			meta.setCustomModelData(customModel);
		if (unbreakable)
			if (Ref.isNewerThan(10))
				meta.setUnbreakable(true);
			else
				try {
					Ref.invoke(Ref.invoke(meta, "spigot"), "setUnbreakable", true);
				} catch (NoSuchFieldError | Exception e2) {
					// unsupported
				}
		return meta;
	}

	public ItemMaker amount(int amount) {
		this.amount = amount;
		return this;
	}

	public int getAmount() {
		return amount;
	}

	public ItemMaker damage(int damage) {
		this.damage = (short) damage;
		return this;
	}

	public short getDamage() {
		return damage;
	}

	public ItemMaker data(int data) {
		this.data = (byte) data;
		return this;
	}

	public byte getData() {
		return data;
	}

	public ItemMaker displayName(String name) {
		displayName = StringUtils.colorize(name);
		return this;
	}

	@Nullable
	public String getDisplayName() {
		return displayName;
	}

	public ItemMaker lore(String... lore) {
		return this.lore(Arrays.asList(lore));
	}

	public ItemMaker lore(List<String> lore) {
		this.lore = StringUtils.colorize(lore);
		return this;
	}

	@Nullable
	public List<String> getLore() {
		return lore;
	}

	public ItemMaker customModel(int customModel) {
		this.customModel = customModel;
		return this;
	}

	public int getCustomModel() {
		return customModel;
	}

	public ItemMaker unbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	public boolean isUnbreakable() {
		return unbreakable;
	}

	public ItemMaker itemFlags(String... flag) {
		return this.itemFlags(Arrays.asList(flag));
	}

	public ItemMaker itemFlags(List<String> flag) {
		itemFlags = flag;
		return this;
	}

	@Nullable
	public List<String> getItemFlags() {
		return itemFlags;
	}

	public ItemMaker enchant(Enchantment enchant, int level) {
		if (enchant == null)
			return this;
		if (enchants == null)
			enchants = new HashMap<>();
		enchants.put(enchant, level);
		return this;
	}

	@Nullable
	public Map<Enchantment, Integer> getEnchants() {
		return enchants;
	}

	public ItemMaker nbt(NBTEdit nbtEdit) {
		if (nbtEdit == null) {
			nbt = null;
			return this;
		}
		// remove unused tags
		nbtEdit.remove("id");
		nbtEdit.remove("Count");
		nbtEdit.remove("lvl");
		nbtEdit.remove("display");
		nbtEdit.remove("Name");
		nbtEdit.remove("Lore");
		nbtEdit.remove("Damage");
		nbtEdit.remove("color");
		nbtEdit.remove("Unbreakable");
		nbtEdit.remove("HideFlags");
		nbtEdit.remove("Enchantments");
		nbtEdit.remove("CustomModelData");
		nbtEdit.remove("ench");

		if (!nbtEdit.getKeys().isEmpty())
			nbt = nbtEdit;
		return this;
	}

	@Nullable
	public NBTEdit getNbt() {
		if (nbt == null)
			return new NBTEdit(new ItemStack(material));
		return nbt;
	}

	public ItemMaker itemMeta(ItemMeta meta) {
		XMaterial xmaterial = XMaterial.matchXMaterial(material);
		ItemMaker maker = this;

		if (material.name().contains("BANNER")) {
			BannerMeta banner = (BannerMeta) meta;
			maker = ofBanner(BannerColor.valueOf(banner.getBaseColor() != null ? banner.getBaseColor().toString().toUpperCase() : "NONE"));
			List<Pattern> patternlist = new ArrayList<>(banner.getPatterns());
			if (!patternlist.isEmpty())
				((BannerItemMaker) maker).patterns = patternlist;
		}

		if (material.name().contains("LEATHER_")) {
			LeatherArmorMeta armor = (LeatherArmorMeta) meta;
			maker = ofLeatherArmor(material);
			((LeatherItemMaker) maker).color(Color.fromRGB(armor.getColor().asRGB()));
		}

		if (xmaterial == XMaterial.PLAYER_HEAD) {
			SkullMeta skull = (SkullMeta) meta;
			maker = ofHead();
			if (skull.getOwner() != null)
				((HeadItemMaker) maker).skinName(skull.getOwner());
			else {
				Object profile = Ref.get(skull, HeadItemMaker.profileField);
				if (profile != null) {

					PropertyHandler properties = BukkitLoader.getNmsProvider().fromGameProfile(profile).getProperties().get("textures");

					String value = properties == null ? null : properties.getValues();
					if (value != null)
						((HeadItemMaker) maker).skinValues(value);
				}
			}
		}

		if (material.name().contains("POTION")) {
			PotionMeta potion = (PotionMeta) meta;

			maker = ofPotion(Potion.POTION);
			if (material.name().equalsIgnoreCase("LINGERING_POTIO"))
				maker = ofPotion(Potion.LINGERING);
			if (material.name().equalsIgnoreCase("SPLASH_POTION"))
				maker = ofPotion(Potion.SPLASH);

			List<PotionEffect> effects = new ArrayList<>(potion.getCustomEffects());

			if (!effects.isEmpty())
				((PotionItemMaker) maker).potionEffects(effects);
			if (Ref.isNewerThan(10)) // 1.11+
				if (potion.getColor() != null)
					((PotionItemMaker) maker).color(potion.getColor());
		}

		if (xmaterial == XMaterial.ENCHANTED_BOOK) {
			EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
			maker = ofEnchantedBook();
			if (book.hasStoredEnchants() && book.getStoredEnchants() != null)
				for (Entry<Enchantment, Integer> enchant : book.getStoredEnchants().entrySet())
					enchant(enchant.getKey(), enchant.getValue());
		} else if (meta.getEnchants() != null)
			for (Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet())
				enchant(enchant.getKey(), enchant.getValue());

		if (xmaterial == XMaterial.WRITTEN_BOOK || xmaterial == XMaterial.WRITABLE_BOOK) {
			BookMeta book = (BookMeta) meta;
			maker = ofBook();
			if (book.getAuthor() != null)
				((BookItemMaker) maker).author(book.getAuthor());
			if (Ref.isNewerThan(9)) // 1.10+
				((BookItemMaker) maker).generation(book.getGeneration().name());
			((BookItemMaker) maker).title(book.getTitle());
			if (!book.getPages().isEmpty())
				((BookItemMaker) maker).pages(book.getPages());
		}

		if (meta.getDisplayName() != null)
			maker.displayName(meta.getDisplayName());
		if (meta.getLore() != null && !meta.getLore().isEmpty())
			maker.lore(meta.getLore());
		// Unbreakable
		if (Ref.isNewerThan(10)) { // 1.11+
			if (meta.isUnbreakable())
				maker.unbreakable(true);
		} else if ((boolean) Ref.invoke(Ref.invoke(meta, "spigot"), "isUnbreakable"))
			try {
				maker.unbreakable(true);
			} catch (NoSuchFieldError | Exception e2) {
				// unsupported
			}
		// ItemFlags
		if (Ref.isNewerThan(7)) { // 1.8+
			List<String> flags = new ArrayList<>();
			for (ItemFlag flag : meta.getItemFlags())
				flags.add(flag.name());
			if (!flags.isEmpty())
				maker.itemFlags(flags);
		}
		// Modeldata
		if (Ref.isNewerThan(13)) { // 1.14+
			int modelData = meta.hasCustomModelData() ? meta.getCustomModelData() : 0;
			if (modelData != 0)
				maker.customModel(modelData);
		}
		return maker;
	}

	public ItemStack build() {
		if (material == null)
			throw new IllegalArgumentException("Material cannot be null");
		ItemStack item = data != 0 ? new ItemStack(material, amount, damage, data) : new ItemStack(material, amount, damage);
		if (nbt != null)
			item = BukkitLoader.getNmsProvider().setNBT(item, nbt.getNBT());
		if (item.getItemMeta() == null)
			throw new IllegalArgumentException("Cannot create ItemMeta for material type " + material);
		item.setItemMeta(apply(item.getItemMeta()));
		return item;
	}

	public static class HeadItemMaker extends ItemMaker {
		static final String URL_FORMAT = "https://api.mineskin.org/generate/url?url=%s&%s";
		static final Field profileField = Ref.field(Ref.craft("inventory.CraftMetaSkull"), "profile");

		private String owner;
		/**
		 * 0 = offlinePlayer 1 = player.values 2 = url.png
		 */
		private int ownerType;

		protected HeadItemMaker() {
			super(ItemMaker.skull);
		}

		public HeadItemMaker skinName(String name) {
			owner = name;
			ownerType = 0;
			return this;
		}

		public HeadItemMaker skinValues(String name) {
			owner = name;
			ownerType = 1;
			return this;
		}

		public HeadItemMaker skinUrl(String name) {
			owner = name;
			ownerType = 2;
			return this;
		}

		@Nullable
		public String getHeadOwner() {
			return owner;
		}

		/**
		 * @apiNote Return's head owner type. 0 = Name 1 = Values 2 = Url
		 * @return int Head owner type
		 */
		public int getHeadOwnerType() {
			return ownerType;
		}

		@Override
		protected ItemMeta apply(ItemMeta meta) {
			SkullMeta iMeta = (SkullMeta) meta;
			if (owner != null)
				switch (ownerType) {
				case 0:
					iMeta.setOwner(owner);
					break;
				case 1: {
					Ref.set(iMeta, HeadItemMaker.profileField, BukkitLoader.getNmsProvider().toGameProfile(GameProfileHandler.of("TheAPI", UUID.randomUUID(), PropertyHandler.of("textures", owner))));
					break;
				}
				case 2: {
					Ref.set(iMeta, HeadItemMaker.profileField,
							BukkitLoader.getNmsProvider().toGameProfile(GameProfileHandler.of("TheAPI", UUID.randomUUID(), PropertyHandler.of("textures", ItemMaker.fromUrl(owner)))));
					break;
				}
				default:
					break;
				}
			return super.apply(iMeta);
		}
	}

	public static class LeatherItemMaker extends ItemMaker {
		private Color color;

		protected LeatherItemMaker(Material material) {
			super(material);
		}

		public LeatherItemMaker color(Color color) {
			this.color = color;
			return this;
		}

		@Nullable
		public Color getColor() {
			return color;
		}

		@Override
		protected ItemMeta apply(ItemMeta meta) {
			LeatherArmorMeta iMeta = (LeatherArmorMeta) meta;
			if (color != null)
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
			this.author = StringUtils.colorize(author);
			return this;
		}

		@Nullable
		public String getAuthor() {
			return author;
		}

		public BookItemMaker title(String title) {
			this.title = StringUtils.colorize(title);
			return this;
		}

		@Nullable
		public String getTitle() {
			return title;
		}

		public BookItemMaker generation(String generation) {
			this.generation = generation;
			return this;
		}

		public BookItemMaker pages(String... pages) {
			return this.pages(Arrays.asList(pages));
		}

		@Nullable
		public String getGeneration() {
			return generation;
		}

		public BookItemMaker pages(List<String> pages) {
			this.pages = new ArrayList<>();
			for (String string : StringUtils.colorize(pages))
				this.pages.add(ComponentAPI.fromString(string));
			return this;
		}

		public BookItemMaker pagesComp(Component... pages) {
			return this.pagesComp(Arrays.asList(pages));
		}

		public BookItemMaker pagesComp(List<Component> pages) {
			this.pages = pages;
			return this;
		}

		@Nullable
		public List<Component> getPages() {
			return pages;
		}

		@Override
		protected ItemMeta apply(ItemMeta meta) {
			BookMeta iMeta = (BookMeta) meta;
			if (author != null)
				iMeta.setAuthor(author);
			if (pages != null)
				if (!Ref.isNewerThan(11) || Ref.serverType() == ServerType.BUKKIT) {
					List<String> page = new ArrayList<>(pages.size());
					for (Component comp : pages)
						page.add(comp.toString());
					iMeta.setPages(page);
				} else
					for (Component page : pages)
						iMeta.spigot().addPage((BaseComponent[]) ComponentAPI.bungee().fromComponents(page));
			if (Ref.isNewerThan(9) && generation != null)
				iMeta.setGeneration(Generation.valueOf(generation.toUpperCase()));
			if (title != null)
				iMeta.setTitle(title);
			return super.apply(iMeta);
		}
	}

	public static class EnchantedBookItemMaker extends ItemMaker {
		protected EnchantedBookItemMaker() {
			super(Material.ENCHANTED_BOOK);
		}

		@Override
		protected ItemMeta apply(ItemMeta meta) {
			EnchantmentStorageMeta iMeta = (EnchantmentStorageMeta) meta;
			if (super.displayName != null)
				iMeta.setDisplayName(super.displayName);
			if (super.lore != null)
				iMeta.setLore(super.lore);
			if (super.enchants != null)
				for (Entry<Enchantment, Integer> s : super.enchants.entrySet())
					iMeta.addStoredEnchant(s.getKey(), s.getValue(), true);
			if (Ref.isNewerThan(7) && super.itemFlags != null)
				for (String flag : super.itemFlags) {
					ItemFlag iFlag = ItemFlag.valueOf(flag.toUpperCase());
					if (iFlag != null)
						meta.addItemFlags(iFlag);
				}
			if (Ref.isNewerThan(13) && super.customModel != 0)
				iMeta.setCustomModelData(super.customModel);
			if (super.unbreakable)
				if (Ref.isNewerThan(10))
					iMeta.setUnbreakable(true);
				else
					try {
						Ref.invoke(Ref.invoke(meta, "spigot"), "setUnbreakable", true);
					} catch (NoSuchFieldError | Exception e2) {
						// unsupported
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
			this.effects = effects;
			return this;
		}

		@Nullable
		public List<PotionEffect> getPotionEffects() {
			return effects;
		}

		@Override
		protected ItemMeta apply(ItemMeta meta) {
			PotionMeta iMeta = (PotionMeta) meta;
			if (color != null && Ref.isNewerThan(10))
				iMeta.setColor(color);
			if (effects != null)
				for (PotionEffect effect : effects)
					iMeta.addCustomEffect(effect, true);
			return super.apply(iMeta);
		}
	}

	public static class ShulkerBoxItemMaker extends ItemMaker {
		private String name;
		private ItemStack[] contents;

		protected ShulkerBoxItemMaker(XMaterial xMaterial) {
			super(xMaterial.parseMaterial());
			super.data = xMaterial.getData();
		}

		public ShulkerBoxItemMaker name(String name) {
			this.name = name;
			return this;
		}

		@Nullable
		public String getName() {
			return name;
		}

		public ShulkerBoxItemMaker contents(ItemStack[] contents) {
			this.contents = contents;
			return this;
		}

		@Nullable
		public ItemStack[] getContents() {
			return contents;
		}

		@Override
		protected ItemMeta apply(ItemMeta meta) {
			BlockStateMeta iMeta = (BlockStateMeta) meta;
			ShulkerBox shulker = (ShulkerBox) iMeta.getBlockState();
			if (name != null)
				shulker.setCustomName(name);
			if (contents != null)
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
			return this.contents(Arrays.asList(contents));
		}

		public BundleItemMaker contents(List<ItemStack> contents) {
			this.contents = contents;
			return this;
		}

		@Nullable
		public List<ItemStack> getContents() {
			return contents;
		}

		@Override
		protected ItemMeta apply(ItemMeta meta) {
			BundleMeta iMeta = (BundleMeta) meta;
			if (contents != null)
				iMeta.setItems(contents);
			return super.apply(iMeta);
		}
	}

	public static class BannerItemMaker extends ItemMaker {
		private List<Pattern> patterns;

		protected BannerItemMaker(XMaterial xMaterial) {
			super(xMaterial.parseMaterial());
			super.data = xMaterial.getData();
		}

		public BannerItemMaker patterns(Pattern... contents) {
			return this.patterns(Arrays.asList(contents));
		}

		public BannerItemMaker patterns(List<Pattern> patterns) {
			this.patterns = patterns;
			return this;
		}

		@Nullable
		public List<Pattern> getPatterns() {
			return patterns;
		}

		@Override
		protected ItemMeta apply(ItemMeta meta) {
			BannerMeta iMeta = (BannerMeta) meta;
			if (patterns != null)
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
		return new LeatherItemMaker(ItemMaker.skull);
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
			m = mat;
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

		ShulkerBoxColor(XMaterial mat) {
			m = mat;
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
		NONE(XMaterial.WHITE_BANNER), WHITE(XMaterial.WHITE_BANNER), BLACK(XMaterial.BLACK_BANNER), BLUE(XMaterial.BLUE_BANNER), BROWN(XMaterial.BROWN_BANNER), CYAN(XMaterial.CYAN_BANNER),
		GRAY(XMaterial.GRAY_BANNER), GREEN(XMaterial.GREEN_BANNER), LIGHT_BLUE(XMaterial.LIGHT_BLUE_BANNER), LIGHT_GRAY(XMaterial.LIGHT_GRAY_BANNER), LIME(XMaterial.LIME_BANNER),
		MAGENTA(XMaterial.MAGENTA_BANNER), ORANGE(XMaterial.ORANGE_BANNER), YELLOW(XMaterial.YELLOW_BANNER), RED(XMaterial.RED_BANNER), PURPLE(XMaterial.PURPLE_BANNER), PINK(XMaterial.PINK_BANNER);

		private XMaterial m;

		BannerColor(XMaterial mat) {
			m = mat;
		}

		public XMaterial toMaterial() {
			return m;
		}
	}

	public static BannerItemMaker ofBanner(BannerColor color) {
		return new BannerItemMaker(color.toMaterial());
	}

	public static void saveToConfig(Config config, String path, ItemStack stack) {
		if (stack == null)
			return; // invalid item
		config.remove(path); // clear section
		if (!path.isEmpty() && !path.endsWith("."))
			path += ".";

		XMaterial type = XMaterial.matchXMaterial(stack);
		config.set(path + "type", type.name());
		if (stack.getDurability() != 0)
			config.set(path + "damage", stack.getDurability());
		config.set(path + "amount", stack.getAmount());
		ItemMeta meta = stack.getItemMeta();
		if (meta.getDisplayName() != null)
			config.set(path + "displayName", meta.getDisplayName());
		if (meta.getLore() != null && !meta.getLore().isEmpty())
			config.set(path + "lore", meta.getLore());

		if (Ref.isNewerThan(10)) { // 1.11+
			if (meta.isUnbreakable())
				config.set(path + "unbreakable", true);
		} else if ((boolean) Ref.invoke(Ref.invoke(meta, "spigot"), "isUnbreakable"))
			try {
				config.set(path + "unbreakable", true);
			} catch (NoSuchFieldError | Exception e2) {
				// unsupported
			}
		if (Ref.isNewerThan(7)) { // 1.8+
			List<String> flags = new ArrayList<>();
			for (ItemFlag flag : meta.getItemFlags())
				flags.add(flag.name());
			if (!flags.isEmpty())
				config.set(path + "itemFlags", flags);
		}
		if (Ref.isNewerThan(13)) { // 1.14+
			int modelData = meta.hasCustomModelData() ? meta.getCustomModelData() : 0;
			if (modelData != 0)
				config.set(path + "modelData", modelData);
		}

		if (type.name().contains("BANNER")) {
			BannerMeta banner = (BannerMeta) meta;
			List<String> patterns = new ArrayList<>();
			for (Pattern pattern : banner.getPatterns())
				patterns.add(pattern.getColor().name() + ":" + pattern.getPattern().name());
			if (!patterns.isEmpty())
				config.set(path + "banner.patterns", patterns);
		}
		if (type.name().contains("LEATHER_")) {
			LeatherArmorMeta armor = (LeatherArmorMeta) meta;
			config.set(path + "leather.color", "#" + Integer.toHexString(armor.getColor().asRGB()).substring(2));
		}
		if (type == XMaterial.PLAYER_HEAD) {
			SkullMeta skull = (SkullMeta) meta;
			if (skull.getOwner() != null) {
				config.set(path + "head.owner", skull.getOwner());
				config.set(path + "head.type", "PLAYER");
			} else {
				Object profile = Ref.get(skull, HeadItemMaker.profileField);
				if (profile != null) {

					PropertyHandler properties = BukkitLoader.getNmsProvider().fromGameProfile(profile).getProperties().get("textures");

					String value = properties == null ? null : properties.getValues();
					if (value != null) {
						config.set(path + "head.owner", value);
						config.set(path + "head.type", "VALUES");
					}
				}
			}
		}
		if (type.name().contains("POTION")) {
			PotionMeta potion = (PotionMeta) meta;
			if (Ref.isNewerThan(9))
				config.set(path + "potion.type", potion.getBasePotionData().getType().name());
			List<String> effects = new ArrayList<>();
			for (PotionEffect effect : potion.getCustomEffects())
				effects.add(effect.getType().getName() + ":" + effect.getDuration() + ":" + effect.getAmplifier() + ":" + effect.isAmbient() + ":" + effect.hasParticles());
			if (!effects.isEmpty())
				config.set(path + "potion.effects", effects);
			if (Ref.isNewerThan(10)) // 1.11+
				if (potion.getColor() != null)
					config.set(path + "potion.color", Integer.toHexString(potion.getColor().asRGB()));
		}
		List<String> enchants = new ArrayList<>();
		if (type == XMaterial.ENCHANTED_BOOK) {
			EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
			for (Entry<Enchantment, Integer> enchant : book.getStoredEnchants().entrySet())
				enchants.add(enchant.getKey().getName() + ":" + enchant.getValue().toString());
		} else
			for (Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet())
				enchants.add(enchant.getKey().getName() + ":" + enchant.getValue().toString());
		if (!enchants.isEmpty())
			config.set(path + "enchants", enchants);
		if (type == XMaterial.WRITTEN_BOOK || type == XMaterial.WRITABLE_BOOK) {
			BookMeta book = (BookMeta) meta;
			config.set(path + "book.author", book.getAuthor());
			if (Ref.isNewerThan(9)) // 1.10+
				config.set(path + "book.generation", book.getGeneration().name());
			config.set(path + "book.title", book.getTitle());
			if (!book.getPages().isEmpty())
				config.set(path + "book.pages", book.getPages());
		}

		NBTEdit nbt = new NBTEdit(stack);
		// remove unused tags
		nbt.remove("id");
		nbt.remove("Count");
		nbt.remove("lvl");
		nbt.remove("display");
		nbt.remove("Name");
		nbt.remove("Lore");
		nbt.remove("Damage");
		nbt.remove("color");
		nbt.remove("Unbreakable");
		nbt.remove("HideFlags");
		nbt.remove("Enchantments");
		nbt.remove("CustomModelData");
		nbt.remove("ench");
		if (!nbt.getKeys().isEmpty())
			config.set(path + "nbt", nbt.getNBT() + ""); // save clear nbt
	}

	@Nullable // Nullable if section is empty / type is invalid
	public static ItemStack loadFromConfig(Config config, String path) {
		if (!path.isEmpty() && !path.endsWith("."))
			path += ".";
		if (config.getString(path + "type", config.getString(path + "icon")) == null)
			return null; // missing type

		XMaterial type = XMaterial.matchXMaterial(config.getString(path + "type", config.getString(path + "icon")).toUpperCase()).orElse(XMaterial.STONE);
		ItemStack stack = type.parseItem();

		String nbt = config.getString(path + "nbt"); // additional nbt
		if (nbt != null)
			stack = BukkitLoader.getNmsProvider().setNBT(stack, BukkitLoader.getNmsProvider().parseNBT(nbt));

		stack.setAmount(config.getInt(path + "amount", 1));
		short damage = config.getShort(path + "damage", config.getShort(path + "durability"));
		if (damage != 0)
			stack.setDurability(damage);

		ItemMeta meta = stack.getItemMeta();
		String displayName = config.getString(path + "displayName", config.getString(path + "display-name"));
		if (displayName != null)
			meta.setDisplayName(StringUtils.colorize(displayName));
		List<String> lore = config.getStringList(path + "lore");
		if (!lore.isEmpty())
			meta.setLore(StringUtils.colorize(lore));
		if (config.getBoolean(path + "unbreakable"))
			if (Ref.isNewerThan(10)) // 1.11+
				meta.setUnbreakable(true);
			else
				try {
					Ref.invoke(Ref.invoke(meta, "spigot"), "setUnbreakable", true);
				} catch (NoSuchFieldError | Exception e2) {
					// unsupported
				}
		if (Ref.isNewerThan(7)) // 1.8+
			for (String flag : config.getStringList(path + "itemFlags"))
				meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));

		int modelData = config.getInt(path + "modelData");
		if (Ref.isNewerThan(13) && modelData != 0) // 1.14+
			meta.setCustomModelData(modelData);

		if (type.name().contains("BANNER")) {
			BannerMeta banner = (BannerMeta) meta;
			// Example: RED:STRIPE_TOP
			for (String pattern : config.getStringList(path + "banner.patterns")) {
				String[] split = pattern.split(":");
				banner.addPattern(new Pattern(DyeColor.valueOf(split[0].toUpperCase()), PatternType.valueOf(split[1].toUpperCase())));
			}
		}
		if (type.name().contains("LEATHER_") && config.getString(path + "leather.color") != null) {
			LeatherArmorMeta armor = (LeatherArmorMeta) meta;
			armor.setColor(Color.fromRGB(Integer.decode(config.getString(path + "leather.color"))));
		}
		if (type == XMaterial.PLAYER_HEAD) {
			SkullMeta skull = (SkullMeta) meta;
			String headOwner = config.getString(path + "head.owner");
			if (headOwner != null) {
				/*
				 * PLAYER VALUES URL
				 */
				String headType = config.getString(path + "head.type", "PLAYER").toUpperCase();
				if (headType.equals("PLAYER"))
					skull.setOwner(headOwner);
				if (headType.equals("VALUES") || headType.equals("URL")) {
					if (headType.equals("URL"))
						headOwner = ItemMaker.fromUrl(headOwner);
					Ref.set(skull, HeadItemMaker.profileField,
							BukkitLoader.getNmsProvider().toGameProfile(GameProfileHandler.of("TheAPI", UUID.randomUUID(), PropertyHandler.of("textures", headOwner))));
				}
			}
		}
		if (type.name().contains("POTION")) {
			PotionMeta potion = (PotionMeta) meta;
			if (Ref.isNewerThan(9) && config.getString(path + "potion.type") != null)
				potion.setBasePotionData(new PotionData(PotionType.valueOf(config.getString(path + "potion.type").toUpperCase())));
			for (String pattern : config.getStringList(path + "potion.effects")) {
				String[] split = pattern.split(":");
				// PotionEffectType type, int duration, int amplifier, boolean ambient, boolean
				// particles
				potion.addCustomEffect(new PotionEffect(PotionEffectType.getByName(split[0].toUpperCase()), StringUtils.getInt(split[1]), StringUtils.getInt(split[2]),
						split.length >= 4 ? StringUtils.getBoolean(split[3]) : true, split.length >= 5 ? StringUtils.getBoolean(split[4]) : true), true);
			}
			if (Ref.isNewerThan(10) && config.getString(path + "potion.color") != null) // 1.11+
				potion.setColor(Color.fromRGB(Integer.decode(config.getString(path + "potion.color"))));
		}
		if (type == XMaterial.ENCHANTED_BOOK) {
			EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
			for (String enchant : config.getStringList(path + "enchants")) {
				String[] split = enchant.split(":");
				book.addStoredEnchant(EnchantmentAPI.byName(split[0].toUpperCase()).getEnchantment(), split.length >= 2 ? StringUtils.getInt(split[1]) : 1, true);
			}
		} else
			for (String enchant : config.getStringList(path + "enchants")) {
				String[] split = enchant.split(":");
				meta.addEnchant(EnchantmentAPI.byName(split[0].toUpperCase()).getEnchantment(), split.length >= 2 ? StringUtils.getInt(split[1]) : 1, true);
			}
		if (type == XMaterial.WRITTEN_BOOK || type == XMaterial.WRITABLE_BOOK) {
			BookMeta book = (BookMeta) meta;
			if (config.getString(path + "book.author") != null)
				book.setAuthor(StringUtils.colorize(config.getString(path + "book.author")));
			if (Ref.isNewerThan(9) && config.getString(path + "book.generation") != null) // 1.10+
				book.setGeneration(Generation.valueOf(config.getString(path + "book.generation").toUpperCase()));
			if (config.getString(path + "book.title") != null)
				book.setTitle(StringUtils.colorize(config.getString(path + "book.title")));
			book.setPages(StringUtils.colorize(config.getStringList(path + "book.pages")));
		}
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemMaker of(ItemStack stack) {
		if (stack == null)
			return null; // invalid item

		XMaterial type = XMaterial.matchXMaterial(stack);

		ItemMaker maker = of(type.parseMaterial());

		ItemMeta meta = stack.getItemMeta();
		maker = maker.itemMeta(meta);

		if (stack.getDurability() != 0)
			maker.damage(stack.getDurability());
		maker.amount(stack.getAmount());

		NBTEdit nbt = new NBTEdit(stack);
		// remove unused tags
		nbt.remove("id");
		nbt.remove("Count");
		nbt.remove("lvl");
		nbt.remove("display");
		nbt.remove("Name");
		nbt.remove("Lore");
		nbt.remove("Damage");
		nbt.remove("color");
		nbt.remove("Unbreakable");
		nbt.remove("HideFlags");
		nbt.remove("Enchantments");
		nbt.remove("CustomModelData");
		nbt.remove("ench");
		if (!nbt.getKeys().isEmpty())
			maker.nbt(nbt);
		return maker;
	}

	@SuppressWarnings("unchecked")
	public static String fromUrl(String url) {
		try {
			java.net.URLConnection connection = new URL(url).openConnection();
			connection.setRequestProperty("User-Agent", "DevTec-JavaClient");
			HttpURLConnection conn = (HttpURLConnection) new URL(String.format(HeadItemMaker.URL_FORMAT, url, "name=DevTec&model=steve&visibility=1")).openConnection();
			conn.setRequestProperty("User-Agent", "DevTec-JavaClient");
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestMethod("POST");
			conn.connect();
			Map<String, Object> text = (Map<String, Object>) Json.reader().simpleRead(StreamUtils.fromStream(new GZIPInputStream(conn.getInputStream())));
			return (String) ((Map<String, Object>) ((Map<String, Object>) text.get("data")).get("texture")).get("value");
		} catch (Exception err) {
		}
		return null;
	}
}
