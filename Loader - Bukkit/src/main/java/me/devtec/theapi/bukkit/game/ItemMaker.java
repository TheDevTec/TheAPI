package me.devtec.theapi.bukkit.game;

import com.google.common.collect.Multimap;
import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.annotations.Nullable;
import me.devtec.shared.components.Component;
import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.json.Json;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.game.itemmakers.*;
import me.devtec.theapi.bukkit.game.itemmakers.BannerItemMaker.BannerColor;
import me.devtec.theapi.bukkit.game.itemmakers.PotionItemMaker.Potion;
import me.devtec.theapi.bukkit.game.itemmakers.ShulkerBoxItemMaker.ShulkerBoxColor;
import me.devtec.theapi.bukkit.nms.GameProfileHandler.PropertyHandler;
import me.devtec.theapi.bukkit.nms.NBTEdit;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class ItemMaker implements Cloneable {
    protected static final Field SKIN_PROPERTIES = Ref.field(Ref.craft("profile.CraftPlayerProfile"), "properties");
    protected static final Field SKIN_VALUE = Ref.field(Ref.getClass("com.mojang.authlib.properties.Property"), "value");
    protected static final Field PROFILE_FIELD = Ref.field(Ref.craft("inventory.CraftMetaSkull"), "profile");
    protected static Method getBaseColor = Ref.method(BannerMeta.class, "getBaseColor");
    protected static Method getPlayerProfile = Ref.method(SkullMeta.class,"getPlayerProfile");
    protected static Method getOwnerProfile = Ref.method(SkullMeta.class,"getOwnerProfile");
    protected static Method getProperties = Ref.method(Ref.getClass("com.destroystokyo.paper.profile.PlayerProfile"),"getProperties");
    protected static Method getName = Ref.method(Ref.getClass("com.destroystokyo.paper.profile.ProfileProperty"),"getName");
    protected static Method getValue = Ref.method(Ref.getClass("com.destroystokyo.paper.profile.ProfileProperty"),"getValue");
    protected static Method getItems = Ref.method(Ref.getClass("org.bukkit.inventory.meta.BundleMeta"),"getItems");
    protected static Method setEnchantmentGlintOverride = Ref.method(ItemMeta.class,"setEnchantmentGlintOverride",boolean.class);

    protected Material material;
    protected int amount = 1;
    protected short damage;

    // additional
    protected String displayName;
    protected List<String> lore;
    protected Map<Enchantment, Integer> enchants;
    protected List<String> itemFlags;
    protected int customModel;
    protected boolean unbreakable;
    protected byte data;
    protected NBTEdit nbt;
    protected boolean enchantedGlow;

    protected ItemMaker(Material material) {
        this.material = material;
    }

    @Override
    public ItemMaker clone() {
        ItemMaker maker = of(material).amount(amount).damage(damage).rawDisplayName(displayName).rawLore(lore).customModel(customModel).data(data).unbreakable(unbreakable).itemFlags(itemFlags);
        if (enchants != null) {
            maker.enchants = new HashMap<>();
            maker.enchants.putAll(enchants);
        }
        if (nbt != null && nbt.getNBT() != null && !nbt.getKeys().isEmpty())
            maker.nbt(new NBTEdit(nbt.getNBT().toString()));
        return maker;
    }

    public Map<String, Object> serializeToMap() {
        Map<String, Object> map = new HashMap<>();
        try {
            XMaterial type = XMaterial.matchXMaterial(material);
            if (type == null)
                map.put("type", material.name()); // Modded item
            else
                map.put("type", type.name());
        } catch (IllegalArgumentException error) {
            map.put("type", material.name()); // Modded item
        }
        map.put("amount", amount);
        if (damage != 0)
            map.put("durability", damage);
        if (customModel != 0)
            map.put("customModel", customModel);
        if (data != 0)
            map.put("data", data);
        if (unbreakable)
            map.put("unbreakable", true);
        if (displayName != null && !displayName.isEmpty())
            map.put("displayName", displayName);
        if (lore != null && !lore.isEmpty())
            map.put("lore", lore);
        if (enchants != null) {
            Map<String, Integer> serialized = new HashMap<>(enchants.size());
            for (Entry<Enchantment, Integer> s : enchants.entrySet())
                serialized.put(s.getKey().getName(), s.getValue());
            map.put("enchants", serialized);
        }
        if (itemFlags != null)
            map.put("itemFlags", itemFlags);
        if (nbt != null && nbt.getNBT() != null && !nbt.getKeys().isEmpty())
            map.put("nbt", nbt.getNBT().toString());
        return map;
    }

    protected ItemMeta apply(ItemMeta meta) {
        if (displayName != null)
            meta.setDisplayName(displayName);
        if (lore != null)
            meta.setLore(lore);
        if (enchantedGlow)
            if (Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() >= 4)
                Ref.invoke(meta,setEnchantmentGlintOverride,true);
            else {
                if (itemFlags != null) {
                    itemFlags.add("HIDE_ENCHANTS");
                    itemFlags.add("HIDE_ATTRIBUTES");
                } else
                    itemFlags("HIDE_ENCHANTS", "HIDE_ATTRIBUTES");
                if (enchants == null || enchants.isEmpty())
                    enchant(EnchantmentAPI.DURABILITY.getEnchantment(), 1);
            }
        if (enchants != null)
            for (Entry<Enchantment, Integer> s : enchants.entrySet())
                meta.addEnchant(s.getKey(), s.getValue(), true);
        if (Ref.isNewerThan(7) && itemFlags != null)
            for (String flag : itemFlags)
                try {
                    ItemFlag iFlag = ItemFlag.valueOf(flag.toUpperCase());
                    meta.addItemFlags(iFlag);
                } catch (NoSuchFieldError | Exception ignored) {

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

    public ItemMaker type(Material material) {
        this.material = material;
        return this;
    }

    public ItemMaker type(XMaterial material) {
        return type(material.parseMaterial());
    }

    public Material getMaterial() {
        return material;
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
        displayName = ColorUtils.colorize(name);
        return this;
    }

    public ItemMaker rawDisplayName(String name) {
        displayName = name;
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
        this.lore = lore == null ? null : ColorUtils.colorize(new ArrayList<>(lore));
        return this;
    }

    public ItemMaker rawLore(String... lore) {
        return this.lore(Arrays.asList(lore));
    }

    public ItemMaker rawLore(List<String> lore) {
        this.lore = lore == null ? null : new ArrayList<>(lore);
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
        itemFlags = flag == null ? null : new ArrayList<>(flag);
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
        nbtEdit.remove("SkullOwner");
        nbtEdit.remove("BlockEntityTag");
        // book
        nbtEdit.remove("author");
        nbtEdit.remove("title");
        nbtEdit.remove("filtered_title");
        nbtEdit.remove("pages");
        nbtEdit.remove("resolved");
        nbtEdit.remove("generation");
        // banner
        nbtEdit.remove("base-color");
        nbtEdit.remove("patterns");
        nbtEdit.remove("pattern");

        nbt = nbtEdit;
        return this;
    }

    @Nullable
    public NBTEdit getNbt() {
        return nbt;
    }

    public ItemMaker enchanted() {
        enchantedGlow = true;
        return this;
    }

    public ItemMaker itemMeta(ItemMeta meta) {
        XMaterial xmaterial = XMaterial.matchXMaterial(material);
        ItemMaker maker = this;

        if (material.name().contains("BANNER")) {
            BannerMeta banner = (BannerMeta) meta;
            if (Ref.isNewerThan(20) || Ref.serverVersionInt() == 20 && Ref.serverVersionRelease() >= 4)
                maker = ofBanner(BannerColor.fromType(xmaterial));
            else {
                Object color = Ref.invoke(banner, getBaseColor);
                maker = ofBanner(color != null ? BannerColor.valueOf(color.toString().toUpperCase()) : BannerColor.NONE);
            }
            List<Pattern> patternlist = banner.getPatterns();
            if (!patternlist.isEmpty())
                ((BannerItemMaker) maker).patterns(patternlist);
        } else if (material.name().contains("SHULKER_BOX")) {
            BlockStateMeta iMeta = (BlockStateMeta) meta;
            ShulkerBox shulker = (ShulkerBox) iMeta.getBlockState();
            maker = ofShulkerBox(ShulkerBoxColor.fromType(xmaterial));
            ((ShulkerBoxItemMaker) maker).name(shulker.getCustomName());
            ((ShulkerBoxItemMaker) maker).contents(shulker.getInventory().getContents());
        } else if (material.name().contains("LEATHER_")) {
            LeatherArmorMeta armor = (LeatherArmorMeta) meta;
            maker = ofLeatherArmor(material);
            ((LeatherItemMaker) maker).color(Color.fromRGB(armor.getColor().asRGB()));
        } else if (xmaterial == XMaterial.PLAYER_HEAD) {
            SkullMeta skull = (SkullMeta) meta;
            maker = ofHead();
            if (Ref.isNewerThan(16) && Ref.serverType() == ServerType.PAPER) {
                Object profile = Ref.invoke(skull,getPlayerProfile);
                Collection<?> properties = (Collection<?>) Ref.invoke(profile,getProperties);
                for (Object property : properties)
                    if (Ref.invoke(property,getName).equals("textures")) {
                        ((HeadItemMaker) maker).skinValues((String)Ref.invoke(property,getValue));
                        break;
                    }
            } else if (Ref.isNewerThan(17)) {
                Object profile = Ref.invoke(skull,getOwnerProfile);
                @SuppressWarnings("unchecked")
                Multimap<String, Object> props = (Multimap<String, Object>) Ref.get(profile, SKIN_PROPERTIES);
                Collection<Object> coll = props.get("textures");
                String value = coll.isEmpty() ? null : (String) Ref.get(coll.iterator().next(), SKIN_VALUE);
                if (value != null)
                    ((HeadItemMaker) maker).skinValues(value);
            } else if (skull.getOwner() != null && !skull.getOwner().isEmpty())
                ((HeadItemMaker) maker).skinName(skull.getOwner());
            else {
                Object profile = Ref.get(skull, PROFILE_FIELD);
                if (profile != null) {
                    PropertyHandler properties = BukkitLoader.getNmsProvider().fromGameProfile(profile).getProperties().get("textures");
                    String value = properties == null ? null : properties.getValues();
                    if (value != null)
                        ((HeadItemMaker) maker).skinValues(value);
                }
            }
        } else if (material.name().contains("POTION")) {
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
        } else if (xmaterial == XMaterial.WRITTEN_BOOK || xmaterial == XMaterial.WRITABLE_BOOK) {
            BookMeta book = (BookMeta) meta;
            maker = xmaterial == XMaterial.WRITTEN_BOOK ? ofBook() : ofWritableBook();
            if (book.getAuthor() != null)
                ((BookItemMaker) maker).rawAuthor(book.getAuthor());
            if (Ref.isNewerThan(9)) // 1.10+
                ((BookItemMaker) maker).generation(book.getGeneration() == null ? null : book.getGeneration().name());
            ((BookItemMaker) maker).rawTitle(book.getTitle());
            if (!book.getPages().isEmpty())
                ((BookItemMaker) maker).rawPages(book.getPages());
        }

        if (xmaterial == XMaterial.ENCHANTED_BOOK) {
            EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
            maker = ofEnchantedBook();
            if (book.hasStoredEnchants())
                for (Entry<Enchantment, Integer> enchant : book.getStoredEnchants().entrySet())
                    enchant(enchant.getKey(), enchant.getValue());
        } else if (meta.hasEnchants())
            for (Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet())
                enchant(enchant.getKey(), enchant.getValue());

        if (meta.hasDisplayName())
            maker.rawDisplayName(meta.getDisplayName());
        if (meta.hasLore() && !meta.getLore().isEmpty())
            maker.rawLore(meta.getLore());
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

    @Override
    public int hashCode() {
        int hash = 1;
        if (material != null)
            hash = hash * 33 + material.hashCode();
        hash = hash * 33 + amount;
        hash = hash * 33 + damage;
        if (displayName != null)
            hash = hash * 33 + displayName.hashCode();
        if (lore != null)
            hash = hash * 33 + lore.hashCode();
        if (enchants != null)
            hash = hash * 33 + enchants.hashCode();
        if (itemFlags != null)
            hash = hash * 33 + itemFlags.hashCode();
        hash = hash * 33 + customModel;
        hash = hash * 33 + (unbreakable ? 1 : 0);
        hash = hash * 33 + data;
        if (nbt != null && nbt.getNBT() != null && !nbt.getKeys().isEmpty())
            hash = hash * 33 + nbt.getNBT().hashCode();
        return hash;
    }

    public ItemStack build() {
        if (material == null)
            throw new IllegalArgumentException("Material cannot be null");
        ItemStack item = data != 0 && Ref.isOlderThan(13) ? new ItemStack(material, amount, (short) 0, data) : new ItemStack(material, amount);
        if (damage != 0)
            item.setDurability(damage);
        if (nbt != null && !nbt.getKeys().isEmpty())
            item = BukkitLoader.getNmsProvider().setNBT(item, nbt.getNBT());
        if (item.getItemMeta() == null)
            throw new IllegalArgumentException("Cannot create ItemMeta for material type " + material);
        item.setItemMeta(apply(item.getItemMeta()));
        return item;
    }

    public static ItemMaker of(XMaterial material) {
        switch (material) {
            case WRITABLE_BOOK:
                return ofWritableBook();
            case WRITTEN_BOOK:
                return ofBook();
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return ofLeatherArmor(material.parseMaterial());
            case ENCHANTED_BOOK:
                return ofEnchantedBook();
            case POTION:
            case LINGERING_POTION:
            case SPLASH_POTION:
                return ofPotion(Potion.fromType(material));
            case BLACK_SHULKER_BOX:
            case BLUE_SHULKER_BOX:
            case BROWN_SHULKER_BOX:
            case CYAN_SHULKER_BOX:
            case GRAY_SHULKER_BOX:
            case GREEN_SHULKER_BOX:
            case LIGHT_BLUE_SHULKER_BOX:
            case LIGHT_GRAY_SHULKER_BOX:
            case ORANGE_SHULKER_BOX:
            case LIME_SHULKER_BOX:
            case MAGENTA_SHULKER_BOX:
            case PINK_SHULKER_BOX:
            case PURPLE_SHULKER_BOX:
            case RED_SHULKER_BOX:
            case WHITE_SHULKER_BOX:
            case YELLOW_SHULKER_BOX:
            case SHULKER_BOX:
                return ofShulkerBox(ShulkerBoxColor.fromType(material));
            case BUNDLE:
                return ofBundle();
            case PLAYER_HEAD:
                return ofHead();
            default:
                break;
        }
        if (material.getId() == 425 || material.getId() == 177)
            return ofBanner(BannerColor.fromType(material));
        return new ItemMaker(material.parseMaterial()).data(material.getData());
    }

    public static ItemMaker of(Material bukkitMaterial) {
        try {
            XMaterial material = XMaterial.matchXMaterial(bukkitMaterial);
            if (material != null) {
                return of(material);
            }
        } catch (IllegalArgumentException error) { // Modded item or null
        }
        return new ItemMaker(bukkitMaterial);
    }

    public static ItemMaker of(ItemStack stack) {
        if (stack == null)
            return null; // invalid item
        ItemMaker maker = of(stack.getType());

        if (Ref.isOlderThan(13) && stack.getData() != null)
            maker.data(stack.getData().getData());

        ItemMeta meta = stack.getItemMeta();
        maker = maker.itemMeta(meta);

        if (stack.getDurability() != 0)
            maker.damage(stack.getDurability());
        maker.amount(stack.getAmount());

        maker.nbt(new NBTEdit(stack));
        return maker;
    }

    public static HeadItemMaker ofHead() {
        return new HeadItemMaker();
    }

    public static LeatherItemMaker ofLeatherArmor(Material material) {
        return new LeatherItemMaker(material);
    }

    public static BookItemMaker ofBook() {
        return new BookItemMaker(true);
    }

    public static BookItemMaker ofWritableBook() {
        return new BookItemMaker(false);
    }

    public static EnchantedBookItemMaker ofEnchantedBook() {
        return new EnchantedBookItemMaker();
    }

    public static PotionItemMaker ofPotion(Potion potionType) {
        return new PotionItemMaker(potionType.toMaterial());
    }

    public static ShulkerBoxItemMaker ofShulkerBox(ShulkerBoxColor color) {
        return new ShulkerBoxItemMaker(color.toMaterial());
    }

    public static BundleItemMaker ofBundle() {
        return new BundleItemMaker();
    }

    public static BannerItemMaker ofBanner(BannerColor color) {
        return new BannerItemMaker(color.toMaterial());
    }

    public static void saveToConfig(Config config, String path, ItemStack stack) {
        if (stack == null)
            return; // invalid item
        config.remove(path); // clear section
        if (!path.isEmpty() && path.charAt(path.length() - 1) != '.')
            path = path + '.';

        XMaterial type;
        try {
            type = XMaterial.matchXMaterial(stack);
            if (type == null) {
                type = XMaterial.STONE;
                config.set(path + "type", stack.getType().name()); // Modded item
            } else
                config.set(path + "type", type.name());
        } catch (IllegalArgumentException error) {
            type = XMaterial.STONE;
            config.set(path + "type", stack.getType().name()); // Modded item
        }
        config.set(path + "amount", stack.getAmount());
        if (stack.hasItemMeta()) {
            ItemMeta meta = stack.getItemMeta();
            if (stack.getDurability() != 0)
                config.set(path + "damage", stack.getDurability());
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
            if (type == XMaterial.BUNDLE) {
                List<String> contents = new ArrayList<>();
                for (ItemStack itemStack : (List<ItemStack>) Ref.invoke(meta,getItems))
                    if (itemStack != null && itemStack.getType() != Material.AIR)
                        contents.add(Json.writer().simpleWrite(ItemMaker.of(itemStack).serializeToMap()));
                if (!contents.isEmpty())
                    config.set(path + "bundle.contents", contents);
            } else if (type.name().contains("SHULKER_BOX")) {
                BlockStateMeta iMeta = (BlockStateMeta) meta;
                ShulkerBox shulker = (ShulkerBox) iMeta.getBlockState();
                if (shulker.getCustomName() != null)
                    config.set(path + "shulker.name", shulker.getCustomName());
                List<String> contents = new ArrayList<>();
                for (ItemStack itemStack : shulker.getInventory().getContents())
                    contents.add(itemStack == null || itemStack.getType() == Material.AIR ? null : Json.writer().simpleWrite(ItemMaker.of(itemStack).serializeToMap()));
                if (!contents.isEmpty())
                    config.set(path + "shulker.contents", contents);
            } else if (type.name().endsWith("_BANNER") && meta instanceof BannerMeta) {
                BannerMeta banner = (BannerMeta) meta;
                List<String> patterns = new ArrayList<>();
                for (Pattern pattern : banner.getPatterns())
                    patterns.add(pattern.getColor().name() + ":" + pattern.getPattern().name());
                if (!patterns.isEmpty())
                    config.set(path + "banner.patterns", patterns);
            } else if (type.name().startsWith("LEATHER_") && meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta armor = (LeatherArmorMeta) meta;
                String hex = Integer.toHexString(armor.getColor().asRGB());
                config.set(path + "leather.color", "#" + (hex.length() > 6 ? hex.substring(2) : hex));
            } else if (type == XMaterial.PLAYER_HEAD && meta instanceof SkullMeta) {
                SkullMeta skull = (SkullMeta) meta;
                if (Ref.isNewerThan(16) && Ref.serverType() == ServerType.PAPER) {
                    Object profile = Ref.invoke(skull,getPlayerProfile);
                    Collection<?> properties = (Collection<?>) Ref.invoke(profile,getProperties);
                    for (Object property : properties)
                        if (Ref.invoke(property,getName).equals("textures")) {
                            config.set(path + "head.owner", (String)Ref.invoke(property,getValue));
                            config.set(path + "head.type", "VALUES");
                            break;
                        }
                } else if (Ref.isNewerThan(17)) {
                    Object profile = Ref.invoke(skull,getOwnerProfile);
                    @SuppressWarnings("unchecked")
                    Multimap<String, Object> props = (Multimap<String, Object>) Ref.get(profile, SKIN_PROPERTIES);
                    Collection<Object> coll = props.get("textures");
                    String value = coll.isEmpty() ? null : (String) Ref.get(coll.iterator().next(), SKIN_VALUE);
                    if (value != null) {
                        config.set(path + "head.owner", value);
                        config.set(path + "head.type", "VALUES");
                    }
                } else if (skull.getOwner() != null && !skull.getOwner().isEmpty()) {
                    config.set(path + "head.owner", skull.getOwner());
                    config.set(path + "head.type", "PLAYER");
                } else {
                    Object profile = Ref.get(skull, PROFILE_FIELD);
                    if (profile != null) {
                        PropertyHandler properties = BukkitLoader.getNmsProvider().fromGameProfile(profile).getProperties().get("textures");
                        String value = properties == null ? null : properties.getValues();
                        if (value != null) {
                            config.set(path + "head.owner", value);
                            config.set(path + "head.type", "VALUES");
                        }
                    }
                }
            } else if (type.name().contains("POTION") && meta instanceof PotionMeta) {
                PotionMeta potion = (PotionMeta) meta;
                List<String> effects = new ArrayList<>();
                for (PotionEffect effect : potion.getCustomEffects())
                    effects.add(effect.getType().getName() + ":" + effect.getDuration() + ":" + effect.getAmplifier() + ":" + effect.isAmbient() + ":" + effect.hasParticles());
                if (!effects.isEmpty())
                    config.set(path + "potion.effects", effects);
                if (Ref.isNewerThan(10) && potion.getColor() != null) {
                    String hex = Integer.toHexString(potion.getColor().asRGB());
                    config.set(path + "potion.color", "#" + (hex.length() > 6 ? hex.substring(2) : hex));
                }
            }
            List<String> enchants = new ArrayList<>();
            if (type == XMaterial.ENCHANTED_BOOK && meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
                for (Entry<Enchantment, Integer> enchant : book.getStoredEnchants().entrySet())
                    enchants.add(enchant.getKey().getName() + ":" + enchant.getValue().toString());
            } else
                for (Entry<Enchantment, Integer> enchant : meta.getEnchants().entrySet())
                    enchants.add(enchant.getKey().getName() + ":" + enchant.getValue().toString());
            if (!enchants.isEmpty())
                config.set(path + "enchants", enchants);
            if ((type == XMaterial.WRITTEN_BOOK || type == XMaterial.WRITABLE_BOOK) && meta instanceof BookMeta) {
                BookMeta book = (BookMeta) meta;
                config.set(path + "book.author", book.getAuthor());
                if (Ref.isNewerThan(9)) // 1.10+
                    config.set(path + "book.generation", book.getGeneration() == null ? null : book.getGeneration().name());
                config.set(path + "book.title", book.getTitle());
                if (!book.getPages().isEmpty())
                    config.set(path + "book.pages", book.getPages());
            }

            NBTEdit nbtEdit = new NBTEdit(stack);
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
            nbtEdit.remove("SkullOwner");
            nbtEdit.remove("BlockEntityTag");
            // book
            nbtEdit.remove("author");
            nbtEdit.remove("title");
            nbtEdit.remove("filtered_title");
            nbtEdit.remove("pages");
            nbtEdit.remove("resolved");
            nbtEdit.remove("generation");
            // banner
            nbtEdit.remove("base-color");
            nbtEdit.remove("patterns");
            nbtEdit.remove("pattern");
            if (!nbtEdit.getKeys().isEmpty())
                config.set(path + "nbt", nbtEdit.getNBT() + ""); // save clear nbt
        }
    }

    @Nullable // Nullable if map is empty / type is invalid
    public static ItemStack loadFromJson(Map<String, Object> serializedItem) {
        return loadFromJson(serializedItem, true);
    }

    @Nullable // Nullable if map is empty / type is invalid
    public static ItemStack loadFromJson(Map<String, Object> serializedItem, boolean colorize) {
        return loadFromJson(serializedItem, colorize, t -> t);
    }

    @Nullable // Nullable if map is empty / type is invalid
    public static ItemStack loadFromJson(Map<String, Object> serializedItem, boolean colorize, Function<String, String> replacer) {
        ItemMaker maker = loadMakerFromJson(serializedItem, colorize, replacer);
        return maker == null ? null : maker.build();
    }

    @Nullable // Nullable if section is empty / type is invalid
    public static ItemMaker loadMakerFromJson(Map<String, Object> serializedItem) {
        return loadMakerFromJson(serializedItem, true, t -> t);
    }

    @Nullable // Nullable if section is empty / type is invalid
    public static ItemMaker loadMakerFromJson(Map<String, Object> serializedItem, boolean colorize) {
        return loadMakerFromJson(serializedItem, colorize, t -> t);
    }

    @SuppressWarnings("unchecked")
    @Nullable // Nullable if section is empty / type is invalid
    public static ItemMaker loadMakerFromJson(Map<String, Object> serializedItem, boolean colorize, Function<String, String> replacer) {
        if (serializedItem.isEmpty() || !serializedItem.containsKey("type"))
            return null;
        String materialTypeName = serializedItem.get("type").toString();
        XMaterial type = XMaterial.matchXMaterial(materialTypeName.toUpperCase()).orElse(XMaterial.STONE);
        if (!type.isSupported())
            return null;
        ItemMaker maker;
        if (type == XMaterial.BUNDLE) {
            maker = ItemMaker.ofBundle();
            List<ItemStack> contents = new ArrayList<>();
            List<Map<String, Object>> serializedContents = (List<Map<String, Object>>) serializedItem.get("bundle.contents");
            for (Map<String, Object> pattern : serializedContents) {
                if (pattern == null)
                    continue;
                ItemMaker itemMaker = ItemMaker.loadMakerFromJson(pattern, colorize, replacer);
                if (itemMaker != null && itemMaker.getMaterial() != Material.AIR)
                    contents.add(itemMaker.build());
            }
            ((BundleItemMaker) maker).contents(contents);
        } else if (type.name().contains("SHULKER_BOX")) {
            maker = ItemMaker.ofShulkerBox(ShulkerBoxColor.fromType(type));
            String name = (String) serializedItem.get("shulker.name");
            if (name != null)
                ((ShulkerBoxItemMaker) maker).name(name);
            List<ItemStack> contents = new ArrayList<>(27);
            List<Map<String, Object>> serializedContents = (List<Map<String, Object>>) serializedItem.get("shulker.contents");
            for (Map<String, Object> pattern : serializedContents)
                if (pattern == null)
                    contents.add(null);
                else {
                    ItemMaker itemMaker = ItemMaker.loadMakerFromJson(pattern, colorize, replacer);
                    contents.add(itemMaker == null ? null : itemMaker.build());
                }
            ((ShulkerBoxItemMaker) maker).contents(contents.toArray(new ItemStack[27]));
        } else if (type.name().contains("BANNER")) {
            maker = ItemMaker.ofBanner(BannerColor.valueOf(type.name().substring(0, type.name().indexOf('_'))));
            // Example: RED:STRIPE_TOP
            List<Pattern> patterns = new ArrayList<>();
            if (serializedItem.containsKey("banner.patterns"))
                for (String pattern : (List<String>) serializedItem.get("banner.patterns")) {
                    String[] split = pattern.split(":");
                    patterns.add(new Pattern(DyeColor.valueOf(split[0].toUpperCase()), PatternType.valueOf(split[1].toUpperCase())));
                }
            ((BannerItemMaker) maker).patterns(patterns);
        } else if (type.name().contains("LEATHER_") && serializedItem.containsKey("leather.color"))
            maker = ItemMaker.ofLeatherArmor(type.parseMaterial()).color(Color.fromRGB(Integer.decode(serializedItem.get("leather.color").toString())));
        else if (type == XMaterial.PLAYER_HEAD) {
            maker = ItemMaker.ofHead();
            String headOwner = (String) serializedItem.get("head.owner");
            if (headOwner != null) {
                /*
                 * PLAYER VALUES URL
                 */
                String headType = serializedItem.getOrDefault("head.type", "PLAYER").toString().toUpperCase();
                if (headType.equalsIgnoreCase("PLAYER") || headType.equalsIgnoreCase("PLAYER_NAME") || headType.equalsIgnoreCase("NAME"))
                    ((HeadItemMaker) maker).skinName(replacer.apply(headOwner));
                else if (headType.equalsIgnoreCase("VALUES") || headType.equalsIgnoreCase("VALUE") || headType.equalsIgnoreCase("URL")) {
                    if (headType.equalsIgnoreCase("URL"))
                        headOwner = HeadItemMaker.fromUrl(replacer.apply(headOwner));
                    ((HeadItemMaker) maker).skinValues(headOwner);
                } else if (headType.equalsIgnoreCase("HDB")) {
                    if (HeadItemMaker.hasHDB())
                        headOwner = HeadItemMaker.getBase64OfId(replacer.apply(headOwner));
                    ((HeadItemMaker) maker).skinValues(headOwner);
                }
            }
        } else if (type == XMaterial.POTION || type == XMaterial.LINGERING_POTION || type == XMaterial.SPLASH_POTION) {
            maker = ItemMaker.ofPotion(type == XMaterial.POTION ? Potion.POTION : type == XMaterial.LINGERING_POTION ? Potion.LINGERING : Potion.SPLASH);
            List<PotionEffect> effects = new ArrayList<>();
            if (serializedItem.containsKey("potion.effects"))
                for (String pattern : (List<String>) serializedItem.get("potion.effects")) {
                    String[] split = pattern.split(":");
                    // PotionEffectType type, int duration, int amplifier, boolean ambient, boolean
                    // particles
                    effects.add(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(split[0].toUpperCase())), ParseUtils.getInt(split[1]), ParseUtils.getInt(split[2]),
                            split.length < 4 || ParseUtils.getBoolean(split[3]), split.length < 5 || ParseUtils.getBoolean(split[4])));
                }
            ((PotionItemMaker) maker).potionEffects(effects);
            if (serializedItem.containsKey("potion.color")) // 1.11+
                ((PotionItemMaker) maker).color(Color.fromRGB(Integer.decode(serializedItem.get("potion.color").toString())));
        } else if (type == XMaterial.ENCHANTED_BOOK)
            maker = ItemMaker.ofEnchantedBook();
        else if (type == XMaterial.WRITTEN_BOOK || type == XMaterial.WRITABLE_BOOK) {
            maker = type == XMaterial.WRITTEN_BOOK ? ItemMaker.ofBook() : ItemMaker.ofWritableBook();
            if (serializedItem.containsKey("book.author"))
                if (colorize)
                    ((BookItemMaker) maker).author(replacer.apply(serializedItem.get("book.author").toString()));
                else
                    ((BookItemMaker) maker).rawAuthor(replacer.apply(serializedItem.get("book.author").toString()));
            if (serializedItem.containsKey("book.generation")) // 1.10+
                ((BookItemMaker) maker).generation(serializedItem.get("book.generation").toString().toUpperCase());
            if (serializedItem.containsKey("book.title"))
                if (colorize)
                    ((BookItemMaker) maker).title(replacer.apply(serializedItem.get("book.title").toString()));
                else
                    ((BookItemMaker) maker).rawTitle(replacer.apply(serializedItem.get("book.title").toString()));

            if (serializedItem.containsKey("book.pages")) {
                List<String> inJson = (List<String>) serializedItem.get("book.pages");
                List<Component> components = new ArrayList<>(inJson.size());
                for (String json : inJson)
                    components.add(ComponentAPI.fromJson(replacer.apply(json)));
                ((BookItemMaker) maker).pagesComp(components);
            }
        } else {
            Material bukkitType; // Modded server support
            maker = type == XMaterial.STONE && !materialTypeName.equals("STONE") && (bukkitType = Material.getMaterial(materialTypeName)) != null ? ItemMaker.of(bukkitType) : ItemMaker.of(type);
        }

        String nbt = (String) serializedItem.get("nbt"); // additional nbt
        if (nbt != null)
            maker.nbt(new NBTEdit(replacer.apply(nbt)));

        if (serializedItem.containsKey("amount"))
            maker.amount(((Number) serializedItem.getOrDefault("amount", 1)).intValue());
        if (serializedItem.containsKey("durability")) {
            short damage = ((Number) serializedItem.get("durability")).shortValue();
            if (damage != 0)
                maker.damage(damage);
        }

        String displayName = (String) serializedItem.get("displayName");
        if (displayName != null)
            if (colorize)
                maker.displayName(replacer.apply(displayName));
            else
                maker.rawDisplayName(replacer.apply(displayName));
        if (serializedItem.containsKey("lore")) {
            List<String> lore = (List<String>) serializedItem.get("lore");
            if (!lore.isEmpty()) {
                if (colorize)
                    maker.lore(lore);
                else
                    maker.rawLore(lore);
                maker.getLore().replaceAll(replacer::apply);
            }
        }
        if (serializedItem.containsKey("unbreakable") && (boolean) serializedItem.get("unbreakable"))
            maker.unbreakable(true);
        if (Ref.isNewerThan(7)) // 1.8+
            if (serializedItem.containsKey("itemFlags"))
                maker.itemFlags((List<String>) serializedItem.get("itemFlags"));
        if (serializedItem.containsKey("customModel")) {
            int modelData = ((Number) serializedItem.get("customModel")).intValue();
            maker.customModel(modelData);
        }

        if (serializedItem.containsKey("enchants"))
            for (Entry<String, Object> enchant : ((Map<String, Object>) serializedItem.get("enchants")).entrySet()) {
                EnchantmentAPI enchantment = EnchantmentAPI.byName(enchant.getKey().toUpperCase());
                if (enchantment != null)
                    maker.enchant(enchantment == EnchantmentAPI.UKNOWN ? Enchantment.getByName(enchant.getKey().toUpperCase()) : enchantment.getEnchantment(),
                            ((Number) enchant.getValue()).intValue());
            }
        return maker;
    }

    @Nullable // Nullable if section is empty / type is invalid
    public static ItemStack loadFromConfig(Config config, String path) {
        return loadFromConfig(config, path, true);
    }

    @Nullable // Nullable if section is empty / type is invalid
    public static ItemStack loadFromConfig(Config config, String path, boolean colorize) {
        return loadFromConfig(config, path, colorize, t -> t);
    }

    @Nullable // Nullable if section is empty / type is invalid
    public static ItemStack loadFromConfig(Config config, String path, boolean colorize, Function<String, String> replacer) {
        ItemMaker maker = loadMakerFromConfig(config, path, colorize, replacer);
        return maker == null ? null : maker.build();
    }

    @Nullable // Nullable if section is empty / type is invalid
    public static ItemMaker loadMakerFromConfig(Config config, String path) {
        return loadMakerFromConfig(config, path, true, t -> t);
    }

    @Nullable // Nullable if section is empty / type is invalid
    public static ItemMaker loadMakerFromConfig(Config config, String path, boolean colorize) {
        return loadMakerFromConfig(config, path, colorize, t -> t);
    }

    @Nullable // Nullable if section is empty / type is invalid
    public static ItemMaker loadMakerFromConfig(Config config, String path, boolean colorize, Function<String, String> replacer) {
        if (!path.isEmpty() && path.charAt(path.length() - 1) != '.')
            path = path + '.';
        if (config.getString(path + "type", config.getString(path + "icon")) == null)
            return null; // missing type

        String materialTypeName = config.getString(path + "type", config.getString(path + "icon"));
        XMaterial type = XMaterial.matchXMaterial(materialTypeName.toUpperCase()).orElse(XMaterial.STONE);
        if (!type.isSupported())
            return null;
        ItemMaker maker;
        if (type == XMaterial.BUNDLE) {
            maker = ItemMaker.ofBundle();
            List<ItemStack> contents = new ArrayList<>();
            List<Map<String, Object>> serializedContents = config.getMapList(path + "bundle.contents");
            for (Map<String, Object> pattern : serializedContents) {
                if (pattern == null)
                    continue;
                ItemMaker itemMaker = ItemMaker.loadMakerFromJson(pattern, colorize, replacer);
                if (itemMaker != null && itemMaker.getMaterial() != Material.AIR)
                    contents.add(itemMaker.build());
            }
            ((BundleItemMaker) maker).contents(contents);
        } else if (type.name().contains("SHULKER_BOX")) {
            maker = ItemMaker.ofShulkerBox(ShulkerBoxColor.fromType(type));
            String name = config.getString(path + "shulker.name");
            if (name != null)
                ((ShulkerBoxItemMaker) maker).name(name);
            List<ItemStack> contents = new ArrayList<>(27);
            List<Map<String, Object>> serializedContents = config.getMapList(path + "shulker.contents");
            for (Map<String, Object> pattern : serializedContents)
                if (pattern == null)
                    contents.add(null);
                else {
                    ItemMaker itemMaker = ItemMaker.loadMakerFromJson(pattern, colorize, replacer);
                    contents.add(itemMaker == null ? null : itemMaker.build());
                }
            ((ShulkerBoxItemMaker) maker).contents(contents.toArray(new ItemStack[27]));
        } else if (type.name().contains("BANNER")) {
            maker = ItemMaker.ofBanner(BannerColor.valueOf(type.name().substring(0, type.name().indexOf('_'))));
            // Example: RED:STRIPE_TOP
            List<Pattern> patterns = new ArrayList<>();
            for (String pattern : config.getStringList(path + "banner.patterns")) {
                String[] split = pattern.split(":");
                patterns.add(new Pattern(DyeColor.valueOf(split[0].toUpperCase()), PatternType.valueOf(split[1].toUpperCase())));
            }
            ((BannerItemMaker) maker).patterns(patterns);
        } else if (type.name().contains("LEATHER_") && config.getString(path + "leather.color") != null)
            maker = ItemMaker.ofLeatherArmor(type.parseMaterial()).color(Color.fromRGB(Integer.decode(config.getString(path + "leather.color"))));
        else if (type == XMaterial.PLAYER_HEAD) {
            maker = ItemMaker.ofHead();
            String headOwner = config.getString(path + "head.owner");
            if (headOwner != null) {
                /*
                 * PLAYER VALUES URL
                 */
                String headType = config.getString(path + "head.type", "PLAYER").toUpperCase();
                if (headType.equalsIgnoreCase("PLAYER") || headType.equalsIgnoreCase("PLAYER_NAME") || headType.equalsIgnoreCase("NAME"))
                    ((HeadItemMaker) maker).skinName(replacer.apply(headOwner));
                else if (headType.equalsIgnoreCase("VALUES") || headType.equalsIgnoreCase("VALUE") || headType.equalsIgnoreCase("URL")) {
                    if (headType.equalsIgnoreCase("URL"))
                        headOwner = HeadItemMaker.fromUrl(replacer.apply(headOwner));
                    ((HeadItemMaker) maker).skinValues(headOwner);
                } else if (headType.equalsIgnoreCase("HDB")) {
                    if (HeadItemMaker.hasHDB())
                        headOwner = HeadItemMaker.getBase64OfId(replacer.apply(headOwner));
                    ((HeadItemMaker) maker).skinValues(headOwner);
                }
            }
        } else if (type == XMaterial.POTION || type == XMaterial.LINGERING_POTION || type == XMaterial.SPLASH_POTION) {
            maker = ItemMaker.ofPotion(type == XMaterial.POTION ? Potion.POTION : type == XMaterial.LINGERING_POTION ? Potion.LINGERING : Potion.SPLASH);
            List<PotionEffect> effects = new ArrayList<>();
            for (String pattern : config.getStringList(path + "potion.effects")) {
                String[] split = pattern.split(":");
                // PotionEffectType type, int duration, int amplifier, boolean ambient, boolean
                // particles
                effects.add(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(split[0].toUpperCase())), ParseUtils.getInt(split[1]), ParseUtils.getInt(split[2]),
                        split.length < 4 || ParseUtils.getBoolean(split[3]), split.length < 5 || ParseUtils.getBoolean(split[4])));
            }
            ((PotionItemMaker) maker).potionEffects(effects);
            if (config.getString(path + "potion.color") != null) // 1.11+
                ((PotionItemMaker) maker).color(Color.fromRGB(Integer.decode(config.getString(path + "potion.color"))));
        } else if (type == XMaterial.ENCHANTED_BOOK)
            maker = ItemMaker.ofEnchantedBook();
        else if (type == XMaterial.WRITTEN_BOOK || type == XMaterial.WRITABLE_BOOK) {
            maker = type == XMaterial.WRITTEN_BOOK ? ItemMaker.ofBook() : ItemMaker.ofWritableBook();
            if (config.getString(path + "book.author") != null)
                if (colorize)
                    ((BookItemMaker) maker).author(replacer.apply(config.getString(path + "book.author")));
                else
                    ((BookItemMaker) maker).rawAuthor(replacer.apply(config.getString(path + "book.author")));
            if (config.getString(path + "book.generation") != null) // 1.10+
                ((BookItemMaker) maker).generation(config.getString(path + "book.generation").toUpperCase());
            if (config.getString(path + "book.title") != null)
                if (colorize)
                    ((BookItemMaker) maker).title(replacer.apply(config.getString(path + "book.title")));
                else
                    ((BookItemMaker) maker).rawTitle(replacer.apply(config.getString(path + "book.title")));
            if (!config.getStringList(path + "book.pages").isEmpty()) {
                List<String> pages = config.getStringList(path + "book.pages");
                pages.replaceAll(replacer::apply);
                if (colorize)
                    ((BookItemMaker) maker).pages(pages);
                else
                    ((BookItemMaker) maker).rawPages(pages);
            }
        } else {
            Material bukkitType; // Modded server support
            maker = type == XMaterial.STONE && !materialTypeName.equals("STONE") && (bukkitType = Material.getMaterial(materialTypeName)) != null ? ItemMaker.of(bukkitType) : ItemMaker.of(type);
        }

        String nbt = config.getString(path + "nbt"); // additional nbt
        if (nbt != null)
            maker.nbt(new NBTEdit(replacer.apply(nbt)));

        maker.amount(config.getInt(path + "amount", 1));
        short damage = config.getShort(path + "damage", config.getShort(path + "durability"));
        if (damage != 0)
            maker.damage(damage);

        if (config.getBoolean(path + "enchanted"))
            maker.enchanted();

        String displayName = config.getString(path + "displayName", config.getString(path + "display-name"));
        if (displayName != null)
            if (colorize)
                maker.displayName(replacer.apply(displayName));
            else
                maker.rawDisplayName(replacer.apply(displayName));
        List<String> lore = config.getStringList(path + "lore");
        if (!lore.isEmpty()) {
            if (colorize)
                maker.lore(lore);
            else
                maker.rawLore(lore);
            maker.getLore().replaceAll(line -> replacer.apply(line));
        }
        if (config.getBoolean(path + "unbreakable"))
            maker.unbreakable(true);
        if (Ref.isNewerThan(7)) // 1.8+
            maker.itemFlags(config.getStringList(path + "itemFlags"));
        int modelData = config.getInt(path + "modelData");
        maker.customModel(modelData);

        for (String enchant : config.getStringList(path + "enchants")) {
            String[] split = enchant.split(":");
            EnchantmentAPI enchantment = EnchantmentAPI.byName(split[0].toUpperCase());
            if (enchantment != null)
                maker.enchant(enchantment == EnchantmentAPI.UKNOWN ? Enchantment.getByName(split[0].toUpperCase()) : enchantment.getEnchantment(), split.length >= 2 ? ParseUtils.getInt(split[1]) : 1);
        }
        return maker;
    }
}
