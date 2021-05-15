package me.devtec.theapi.utils.json;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.collect.Multimap;
import com.google.common.io.CharStreams;

import me.devtec.theapi.apis.EnchantmentAPI;
import me.devtec.theapi.utils.Position;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.reflections.Ref;

public class Reader implements JsonReader {
	//Do you want your own JsonParser? Override this one! Ref.set(Reader.class, "reader", <NewReaderJson>)
	private static JsonReader reader = new Reader();

	public static Object read(String json) {
		return reader.deserilize(json);
	}

	private static sun.misc.Unsafe unsafe = (sun.misc.Unsafe) Ref
			.getNulled(Ref.field(sun.misc.Unsafe.class, "theUnsafe"));
	private static Method fromJson = Ref.method(Ref.getClass("com.google.gson.Gson") != null ? Ref.getClass("com.google.gson.Gson")
			: Ref.getClass("com.google.gson.org.bukkit.craftbukkit.libs.com.google.gson.Gson"), "fromJson", String.class, Class.class);
	private static Object parser = Ref.invoke(Ref.newInstance(Ref.constructor(
					Ref.getClass("com.google.gson.GsonBuilder") != null ? Ref.getClass("com.google.gson.GsonBuilder")
							: Ref.getClass("com.google.gson.org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder"))), "create");

	public Object object(String json) {
		if (json == null)
			return null;
		Object parsed = json;
		try {
			parsed = map(json);
			if (parsed == null)
				parsed = collection(json);
		} catch (Exception e) {
		}
		if (parsed == null)
			parsed = json;
		if (parsed instanceof Comparable) {
			if(parsed instanceof String) {
				if(StringUtils.isBoolean(json))return StringUtils.getBoolean(json);
				if(StringUtils.isNumber(json))return StringUtils.getNumber(json);
			}
			return parsed;
		}
		return parseR(parsed);
	}

	public Collection<?> collection(String json) {
		if (json == null)
			return null;
		try {
			return (Collection<?>) Ref.invoke(parser, fromJson, json, Collection.class);
		} catch (Exception e1) {
		}
		return null;
	}

	public List<?> list(String json) {
		if (json == null)
			return null;
		try {
			return (List<?>) Ref.invoke(parser, fromJson, json, LinkedList.class);
		} catch (Exception e1) {
		}
		return null;
	}

	public Object[] array(String json) {
		if (json == null)
			return null;
		return collection(json).toArray();
	}

	public Map<?, ?> map(String json) {
		if (json == null)
			return null;
		try {
			return (Map<?, ?>) Ref.invoke(parser, fromJson, json, LinkedHashMap.class);
		} catch (Exception e1) {
		}
		return null;
	}

	private Object parse(Class<?> clazz, Object object) {
		if (clazz == Ref.nms("IChatBaseComponent") || clazz == Ref.nms("IChatMutableComponent")
				|| clazz == Ref.nms("ChatBaseComponent") || clazz == Ref.nms("ChatMessage")
				|| clazz == Ref.nms("ChatComponentText")
				|| clazz == Ref.getClass("net.md_5.bungee.api.chat.TextComponent"))
			return Ref.IChatBaseComponent(object + "");
		Object o = Ref.newInstance(Ref.constructor(clazz));
		try {
			o = unsafe.allocateInstance(clazz);
		} catch (Exception errr) {
		}
		if (o == null)
			return null;
		if (object instanceof Map<?, ?>)
			for (Entry<?, ?> s : ((Map<?, ?>) object).entrySet())
				setObject(o, s.getKey() + "", s.getValue());
		return o;
	}

	@SuppressWarnings("unchecked")
	private void setObject(Object o, String f, Object v) {
		if (v == null)
			Ref.set(o, f, v);
		Type c = Ref.field(o.getClass(), f).getGenericType();
		v = cast(v, Ref.getClass(c.getTypeName()));
		Matcher ma = Pattern.compile("Map<(.*?), (.*?)>").matcher(c.getTypeName());
		if (ma.find() && v instanceof Map) {
			@SuppressWarnings("rawtypes")
			HashMap uknown = new HashMap<>();
			for (Entry<?, ?> e : ((Map<?, ?>) v).entrySet())
				uknown.put(cast(e.getKey(), Ref.getClass(ma.group(1))), cast(e.getValue(), Ref.getClass(ma.group(2))));
			v = uknown;
		}
		ma = Pattern.compile("List<(.*?)>").matcher(c.getTypeName());
		if (ma.find() && v instanceof Collection) {
			@SuppressWarnings("rawtypes")
			ArrayList uknown = new ArrayList<>();
			for (Object e : ((Collection<?>) v))
				uknown.add(cast(e, Ref.getClass(ma.group(1))));
			v = uknown;
		}
		ma = Pattern.compile("Set<(.*?)>").matcher(c.getTypeName());
		if (ma.find() && v instanceof Collection) {
			@SuppressWarnings("rawtypes")
			HashSet uknown = new HashSet<>();
			for (Object e : ((Collection<?>) v))
				uknown.add(cast(e, Ref.getClass(ma.group(1))));
			v = uknown;
		}
		Ref.set(o, f, v);
	}

	private Object cast(Object v, Class<?> c) {
		if (v instanceof Comparable) {
			if (c == boolean.class | c == Boolean.class)
				v = StringUtils.getBoolean(v + "");
			if (c == String.class)
				v = v.toString();
			if (c == double.class | c == Double.class)
				v = StringUtils.getDouble(v + "");
			if (c == float.class | c == Float.class)
				v = StringUtils.getFloat(v + "");
			if (v instanceof Double)
				v = (int) (double) v;
			if (c == int.class | c == Integer.class)
				v = StringUtils.getInt(v + "");
			if (c == long.class | c == Long.class)
				v = StringUtils.getLong(v + "");
			if (c == byte.class | c == Byte.class)
				v = StringUtils.getByte(v + "");
			if (c == short.class | c == Short.class)
				v = StringUtils.getShort(v + "");
			if (c == BigDecimal.class)
				v = new BigDecimal(v + "");
		}

		return v;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object parseR(Object o) {
		if (o instanceof Collection) {
			Collection<Object> aw = new ArrayList<>();
			for (Object f : ((Collection<?>) o))
				aw.add(f instanceof String ? object((String) f) : f);
			o = aw;
			List<Object> cloneOfList = new ArrayList<>((Collection<Object>) o);
			((Collection<Object>) o).clear();
			for (Object s : cloneOfList)
				((Collection<Object>) o).add(parseR(s));
			return o;
		}
		if (o instanceof Map) {
			Map<Object, Object> aw = new HashMap<>();
			for (Entry<?, ?> f : ((Map<?, ?>) o).entrySet())
				aw.put(f.getKey() instanceof String ? object((String) f.getKey()) : f.getKey(), f.getValue());
			o = aw;
			Map<Object, Object> a = new HashMap<>();
			boolean c = false;
			for (Entry<?, ?> s : ((Map<?, ?>) o).entrySet()) {
				if (s.getKey().toString().startsWith("enum ")) {
					if (s.getKey().toString().replaceFirst("enum ", "").equals("org.bukkit.enchantments.Enchantment")) {
						a.put(s.getKey(), EnchantmentAPI.byName(s.getValue() + "").getEnchantment());
						c = true;
						continue;
					}
					a.put(s.getKey(), Ref.getNulled(Ref.getClass((s.getKey().toString()).replaceFirst("enum ", "")),
							s.getValue() + ""));
					c = true;
				} else if (s.getKey().toString().startsWith("class ")) {
					a.put(s.getKey(), parse(Ref.getClass((s.getKey().toString()).replaceFirst("class ", "")),
							parseR(s.getValue())));
					c = true;
				} else if (s.getKey().toString().startsWith("modifiedClass ")) {
					String which = s.getKey().toString().replaceFirst("modifiedClass ", "");
					if (which.equals("org.bukkit.inventory.ItemStack")) {
						Map<String, Object> values = (Map<String, Object>) s.getValue();
						ItemStack item = new ItemStack(Material.getMaterial((String) values.get("type")),
								((Number) values.get("amount")).intValue(),
								((Number) values.get("durability")).shortValue());
						item.setData(new MaterialData(item.getType(), ((Number) values.get("data")).byteValue()));
						try {
							if (values.containsKey("nbt")) {
								Object os = Ref.invokeNulled(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", item);
								Ref.invoke(os, "setTag", Ref.invokeNulled(Ref.nms("MojangsonParser"), "parse",
										(String) values.get("nbt")));
								item = (ItemStack) Ref.invokeNulled(Ref.craft("inventory.CraftItemStack"),
										"asBukkitCopy", os);
							}
						} catch (Exception err) {
						}
						ItemMeta meta = item.getItemMeta();
						if (values.containsKey("meta.name"))
							meta.setDisplayName((String) values.get("meta.name"));
						try {
						if (values.containsKey("meta.locName"))
							meta.setLocalizedName((String) values.get("meta.locName"));
						}catch(Exception | NoSuchMethodError e) {}
						if (values.containsKey("meta.lore"))
							meta.setLore((List<String>) values.get("meta.lore"));
						if (values.containsKey("meta.enchs")) {
							for (Entry<String, Double> enchs : ((Map<String, Double>) values.get("meta.enchs"))
									.entrySet())
								meta.addEnchant(Enchantment.getByName(enchs.getKey()), enchs.getValue().intValue(),
										true);
						}
						item.setItemMeta(meta);
						a.put(s.getKey(), item);
						c = true;
					} else if (which.equals("org.bukkit.Location")) {
						Map<String, Object> values = (Map<String, Object>) s.getValue();
						a.put(s.getKey(),
								new Location(Bukkit.getWorld((String) values.get("world")), (double) values.get("x"),
										(double) values.get("y"), (double) values.get("z"),
										(float) (double) values.get("yaw"), (float) (double) values.get("pitch")));
						c = true;
					} else if (which.equals("me.DevTec.TheAPI.Utils.Position")) {
						Map<String, Object> values = (Map<String, Object>) s.getValue();
						a.put(s.getKey(),
								new Position((String) values.get("world"), (double) values.get("x"),
										(double) values.get("y"), (double) values.get("z"),
										(float) (double) values.get("yaw"), (float) (double) values.get("pitch")));
						c = true;
					} else {
						a.put(s.getKey(), parseR(s.getValue()));
					}
				} else if (s.getKey().toString().startsWith("Map ") && s.getValue() instanceof Map) {
					Map map = (Map) Ref.newInstance(
							Ref.constructor(Ref.getClass((s.getKey().toString()).replaceFirst("Map ", ""))));
					for (Entry<?, ?> er : ((Map<?, ?>) s.getValue()).entrySet())
						map.put(parseR(er.getKey()), parseR(er.getValue()));
					a.put(s.getKey(), parseR(map));
					c = true;
				} else if (s.getKey().toString().startsWith("Multimap ")) {
					Multimap map = (Multimap) Ref.newInstance(
							Ref.constructor(Ref.getClass((s.getKey().toString()).replaceFirst("Multimap ", ""))));
					for (Entry<?, ?> er : ((Multimap<?, ?>) s.getValue()).entries())
						map.put(parseR(er.getKey()), parseR(er.getValue()));
					a.put(s.getKey(), parseR(map));
					c = true;
				} else if (s.getKey().toString().startsWith("Collection ")) {
					Collection map = (Collection) Ref.newInstance(
							Ref.constructor(Ref.getClass((s.getKey().toString()).replaceFirst("Collection ", ""))));
					for (Object er : (Collection) s.getValue())
						map.add(parseR(er));
					a.put(s.getKey(), parseR(map));
					c = true;
				} else {
					Object aa = s.getKey();
					if (!aa.equals(parseR(s.getKey()))) {
						aa = parseR(s.getKey());
						c = true;
					}
					a.put(aa, parseR(s.getValue()));
				}
			}
			if (a.size() == 1 && c)
				for (Object oa : a.values())
					return parseR(oa);
			return a;
		}
		return o;
	}

	@Override
	public Object deserilize(java.io.Reader reader) {
		try {
			return object(CharStreams.toString(reader));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object deserilize(String json) {
		return object(json);
	}
}
