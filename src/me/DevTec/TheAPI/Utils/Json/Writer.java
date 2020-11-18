package me.DevTec.TheAPI.Utils.Json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.internal.LinkedTreeMap;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.Position;
import me.DevTec.TheAPI.Utils.DataKeeper.Collections.LinkedSet;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.NonSortedMap;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class Writer implements JsonWriter {
	private List<String> FORBIDDEN = new ArrayList<>();
	public Writer() {
		FORBIDDEN.add("org.bukkit.craftbukkit." + TheAPI.getServerVersion()
		+ ".persistence.CraftPersistentDataAdapterContext");
FORBIDDEN.add(
		"org.bukkit.craftbukkit." + TheAPI.getServerVersion() + ".persistence.CraftPersistentDataTypeRegistry");
	}
	
	private static JsonWriter writer = new Writer();
	
	public static String write(Object object) {
		return writer.serilize(object);
	}
	
	public static String write(Object object, boolean fancy) {
		return writer.serilize(object, fancy);
	}
	
	private Map<Object, Object> fix(Map<?, ?> o, boolean fancy, boolean addNulls) {
		Map<Object, Object> map = new NonSortedMap<>();
		for(Entry<?, ?> e : o.entrySet())
			map.put(object2(e.getKey(), fancy, addNulls), object2(e.getValue(), fancy, addNulls));
		return map;
	}
	
	private Collection<Object> fix(Collection<?> o, boolean fancy, boolean addNulls) {
		Collection<Object> map = new ArrayList<>();
		for(Object e : o)
			map.add(object2(e, fancy, addNulls));
		return map;
	}

	private static Object pretty = Ref.invoke(Ref.invoke(Ref.newInstance(Ref.constructor(Ref.getClass("com.google.gson.GsonBuilder") != null ? Ref.getClass("com.google.gson.GsonBuilder") : Ref.getClass("com.google.gson.org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder")))
			,"setPrettyPrinting"),"create"),
			simple=Ref.invoke(Ref.newInstance(Ref.constructor(Ref.getClass("com.google.gson.GsonBuilder") != null ? Ref.getClass("com.google.gson.GsonBuilder") : Ref.getClass("com.google.gson.org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder"))),"create");
			
	public String array(Object[] object, boolean addNulls) {
		return array(object, addNulls, false);
	}

	public String array(Object[] object, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		return (String) Ref.invoke((fancy?pretty:simple),Ref.method(pretty.getClass(), "toJson", Object.class),fix(Arrays.asList(object), fancy, addNulls));
	}

	public String collection(Collection<?> object, boolean addNulls) {
		return collection(object, addNulls, false);
	}

	public String collection(Collection<?> object, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		
		return (String) Ref.invoke((fancy?pretty:simple),Ref.method(pretty.getClass(), "toJson", Object.class),fix(object, fancy, addNulls));
	}

	public String map(Map<?, ?> object, boolean addNulls) {
		return map(object, addNulls, false);
	}

	public String map(Map<?, ?> object, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		return (String) Ref.invoke((fancy?pretty:simple),Ref.method(pretty.getClass(), "toJson", Object.class),fix(object, fancy, addNulls));
	}

	private static Method from = Ref.method(Ref.craft("util.CraftChatMessage"), "fromComponent",
			Ref.nms("IChatBaseComponent"));

	/**
	 * 
	 * @param object Object to parse to String
	 * @return String Object converted to String
	 */
	public String object(Object w, boolean addNulls, boolean fancy) {
		if (w == null)
			return "null";
		if(w instanceof Location) {
			Location stack = (Location)w;
			Map<String, Object> done = new HashMap<>();
			Map<String, Object> items = new HashMap<>();
			items.put("world", stack.getWorld().getName());
			items.put("x", stack.getX());
			items.put("y", stack.getY());
			items.put("z", stack.getZ());
			items.put("yaw", stack.getYaw());
			items.put("pitch", stack.getPitch());
			done.put("modifiedClass org.bukkit.Location", items);
			return map(done, addNulls, fancy);
		}
		if(w instanceof Position) {
			Position stack = (Position)w;
			Map<String, Object> done = new HashMap<>();
			Map<String, Object> items = new HashMap<>();
			items.put("world", stack.getWorldName());
			items.put("x", stack.getX());
			items.put("y", stack.getY());
			items.put("z", stack.getZ());
			items.put("yaw", stack.getYaw());
			items.put("pitch", stack.getPitch());
			done.put("modifiedClass me.DevTec.TheAPI.Utils.Position", items);
			return map(done, addNulls, fancy);
		}
		if(w instanceof ItemStack) {
			ItemStack stack = ((ItemStack)w).clone();
			Map<String, Object> done = new HashMap<>();
			Map<String, Object> items = new HashMap<>();
			items.put("type", stack.getType().name());
			items.put("amount", stack.getAmount());
			items.put("data", stack.getData().getData());
			items.put("durability", stack.getDurability());
			if(stack.hasItemMeta()) {
			ItemMeta meta = stack.getItemMeta();
			if(meta.hasEnchants()) {
			Map<String, Integer> enchs = new HashMap<>();
			for(Entry<Enchantment, Integer> e : stack.getEnchantments().entrySet())
				enchs.put(e.getKey().getName(), e.getValue());
			items.put("meta.enchs", enchs);
			}
			if(meta.hasDisplayName())
			items.put("meta.name", meta.getDisplayName());
			if(meta.hasLore())
			items.put("meta.lore", meta.getLore());
			if(meta.hasLocalizedName())
			items.put("meta.locName", meta.getLocalizedName());
			meta.setDisplayName(null);
			meta.setLore(null);
			meta.setLocalizedName(null);
			stack.setItemMeta(meta);
			for(Enchantment e : stack.getEnchantments().keySet())
				stack.removeEnchantment(e);
			}
			Object tag = Ref.invoke(Ref.invokeNulled(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", stack), "getTag");
			if(tag!=null && !(boolean)Ref.invoke(tag, "isEmpty")) {
		        items.put("nbt", tag.toString());
			}
			done.put("modifiedClass org.bukkit.inventory.ItemStack", items);
			return map(done, addNulls, fancy);
		}
		if (w instanceof String || w.getClass() == Ref.nms("IChatBaseComponent")
				|| w.getClass() == Ref.nms("IChatMutableComponent") || w.getClass() == Ref.nms("ChatBaseComponent")
				|| w.getClass() == Ref.nms("ChatMessage") || w.getClass() == Ref.nms("ChatComponentText")
				|| w.getClass() == Ref.getClass("net.md_5.bungee.api.chat.TextComponent")) {
			if (w instanceof String) {
				return "" + w;
			}
			String obj = w.getClass() == Ref.getClass("net.md_5.bungee.api.chat.TextComponent")
					? (String) Ref.invoke(w, "toLegacyText")
					: (String) Ref.invoke(Ref.craft("util.CraftChatMessage"), from,
							Ref.cast(Ref.nms("IChatBaseComponent"), w));
			Map<String, Object> enumMap = new NonSortedMap<>();
			enumMap.put("class " + w.getClass().getName(), obj);
			return map(enumMap, addNulls, fancy);
		}
		if (w instanceof Enum<?>) {
			Map<String, Object> enumMap = new NonSortedMap<>();
			enumMap.put("enum " + w.getClass().getName(), w.toString());
			return map(enumMap, addNulls, fancy);
		}
		if (w instanceof Comparable)
			return w.toString();
		if (w instanceof Object[])
			return array((Object[]) w, addNulls, fancy);
		if (w instanceof Collection) {
			if (w instanceof ArrayList || w instanceof LinkedSet || w.getClass() == Ref.getClass("java.util.Arrays$ArrayList")
					|| w instanceof HashSet || w instanceof LinkedList || w instanceof LinkedHashSet) {
				return collection((Collection<?>) w, addNulls, fancy);
			}
			Map<String, Object> enumMap = new NonSortedMap<>();
			enumMap.put("Collection " + w.getClass().getName(), w);
			return map(enumMap, addNulls, fancy);
		}
		if (w instanceof Map) {
			if (w instanceof NonSortedMap || w instanceof HashMap || w instanceof LinkedHashMap || w instanceof HashMap || w instanceof HashMap) {
				return map((Map<?, ?>) w, addNulls, fancy);
			}
			Map<String, Object> enumMap = new NonSortedMap<>();
			enumMap.put("Map " + w.getClass().getName(), w);
			return map(enumMap, addNulls, fancy);
		}
		return (String) Ref.invoke((fancy?pretty:simple),"toJson",(Object)convert(w, fancy, addNulls));
	}
	
	public Object object2(Object w, boolean fancy, boolean addNulls) {
		if (w == null)
			return null;
		if(w instanceof Location) {
			Location stack = (Location)w;
			Map<String, Object> done = new HashMap<>();
			Map<String, Object> items = new HashMap<>();
			items.put("world", stack.getWorld().getName());
			items.put("x", stack.getX());
			items.put("y", stack.getY());
			items.put("z", stack.getZ());
			items.put("yaw", stack.getYaw());
			items.put("pitch", stack.getPitch());
			done.put("modifiedClass org.bukkit.Location", items);
			return done;
		}
		if(w instanceof Position) {
			Position stack = (Position)w;
			Map<String, Object> done = new HashMap<>();
			Map<String, Object> items = new HashMap<>();
			items.put("world", stack.getWorldName());
			items.put("x", stack.getX());
			items.put("y", stack.getY());
			items.put("z", stack.getZ());
			items.put("yaw", stack.getYaw());
			items.put("pitch", stack.getPitch());
			done.put("modifiedClass me.DevTec.TheAPI.Utils.Position", items);
			return done;
		}
		if(w instanceof ItemStack) {
			ItemStack stack = ((ItemStack)w).clone();
			Map<String, Object> done = new HashMap<>();
			Map<String, Object> items = new HashMap<>();
			items.put("type", stack.getType().name());
			items.put("amount", stack.getAmount());
			items.put("data", stack.getData().getData());
			items.put("durability", stack.getDurability());
			ItemMeta meta = stack.getItemMeta();
			if(meta.hasEnchants()) {
			Map<String, String> enchs = new HashMap<>();
			for(Entry<Enchantment, Integer> e : stack.getEnchantments().entrySet())
				enchs.put(e.getKey().getName(), e.getValue().toString());
			items.put("meta.enchs", enchs);
			}
			if(meta.hasDisplayName())
			items.put("meta.name", meta.getDisplayName());
			if(meta.hasLore())
			items.put("meta.lore", meta.getLore());
			if(meta.hasLocalizedName())
			items.put("meta.locName", meta.getLocalizedName());
			meta.setDisplayName(null);
			meta.setLore(null);
			meta.setLocalizedName(null);
			stack.setItemMeta(meta);
			for(Enchantment e : stack.getEnchantments().keySet())
				stack.removeEnchantment(e);
			Object tag = Ref.invoke(Ref.invokeNulled(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", stack), "getTag");
			if(tag!=null && !(boolean)Ref.invoke(tag, "isEmpty"))
		        items.put("nbt", tag.toString());
			done.put("modifiedClass org.bukkit.inventory.ItemStack", items);
			return done;
		}
		if (w instanceof String || w.getClass() == Ref.nms("IChatBaseComponent")
				|| w.getClass() == Ref.nms("IChatMutableComponent") || w.getClass() == Ref.nms("ChatBaseComponent")
				|| w.getClass() == Ref.nms("ChatMessage") || w.getClass() == Ref.nms("ChatComponentText")
				|| w.getClass() == Ref.getClass("net.md_5.bungee.api.chat.TextComponent")) {
			if (w instanceof String) {
				return w+"";
			}
			String obj = w.getClass() == Ref.getClass("net.md_5.bungee.api.chat.TextComponent")
					? (String) Ref.invoke(w, "toLegacyText")
					: (String) Ref.invoke(Ref.craft("util.CraftChatMessage"), from,
							Ref.cast(Ref.nms("IChatBaseComponent"), w));
			Map<String, Object> enumMap = new HashMap<>();
			enumMap.put("class " + w.getClass().getName(), obj);
			return enumMap;
		}
		if(w instanceof Enchantment) {
			return "{\"enum org.bukkit.enchantments.Enchantment\":\""+((Enchantment)w).getName()+"\"}";
		}
		if (w instanceof Enum<?>) {
			Map<String, Object> enumMap = new HashMap<>();
			enumMap.put("enum " + w.getClass().getName(), w.toString());
			return enumMap;
		}
		if (w instanceof Comparable)
			return w;
		if (w instanceof Object[])
			return fix(Arrays.asList((Object[]) w), fancy, addNulls);
		if (w instanceof Collection) {
			if (w instanceof ArrayList || w instanceof LinkedSet || w.getClass() == Ref.getClass("java.util.Arrays$ArrayList")
					|| w instanceof HashSet || w instanceof LinkedList || w instanceof LinkedHashSet) {
				return fix((Collection<?>) w, fancy, addNulls);
			}
			Map<String, Object> enumMap = new HashMap<>();
			enumMap.put("Collection " + w.getClass().getName(),
					fix((Collection<?>) w, fancy, addNulls));
			return enumMap;
		}
		if (w instanceof Map) {
			if(w instanceof NonSortedMap || w instanceof HashMap || w instanceof LinkedHashMap || w instanceof TreeMap || w instanceof LinkedTreeMap || w instanceof WeakHashMap)
				return fix((Map<?, ?>) w, fancy, addNulls);
			Map<String, Object> enumMap = new HashMap<>();
			enumMap.put("Map " + w.getClass().getName(), map((Map<?, ?>) w, addNulls));
			return fix(enumMap, fancy, addNulls);
		}
		return convert(w, fancy, addNulls);
	}

	private Object convert(Object object, boolean fancy, boolean addNulls) {
		Map<String, Object> item = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		for (Field f : Ref.getAllFields(object.getClass())) {
			if (Modifier.toString(f.getModifiers()).toLowerCase().contains("static"))
				continue;
			Object w = Ref.get(object, f);
			if (w == null && !addNulls || w!=null && FORBIDDEN.contains(w.getClass().getName()))
				continue;
			if(w==null) {
				map.put(f.getName(), null);
				continue;
			}
			map.put(f.getName(), object2(w, fancy, addNulls));
			continue;
		}
		item.put("class " + object.getClass().getName(), map);
		return item;
	}

	@Override
	public String serilize(java.io.Writer writer, Object item) {
		String write = object(item, false, true);
		try {
			writer.write(write);
		} catch (Exception e) {
		}
		return write;
	}

	@Override
	public String serilize(Object item) {
		return object(item, false, false);
	}

	@Override
	public String serilize(Object item, boolean fancy) {
		return object(item, false, fancy);
	}
}
