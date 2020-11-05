package me.DevTec.TheAPI.Utils.Json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

public class Reader {
	private static TypeAdapter<Collection<Object>> adapterList = new Gson()
			.getAdapter(new TypeToken<Collection<Object>>() {
			});
	private static TypeAdapter<Map<Object, Object>> adapterMap = new Gson()
			.getAdapter(new TypeToken<Map<Object, Object>>() {
			});

	public static Object object(String json) {
		if (json == null)
			return null;
		if (StringUtils.isNumber(json))
			return StringUtils.getNumber(json);
		if (StringUtils.isBoolean(json))
			return StringUtils.getBoolean(json);
		Object a = collection(json);
		if (a == null)
			a = map(json);
		else
			return parseR(a);
		if (a == null)
			return json;
		else
			return parseR(a);
	}

	public static Collection<Object> collection(String json) {
		if (json == null)
			return null;
		try {
			return adapterList.fromJson(json);
		} catch (Exception e1) {
		}
		return null;
	}

	public static List<Object> list(String json) {
		if (json == null)
			return null;
		return new ArrayList<>(collection(json));
	}

	public static Object[] array(String json) {
		if (json == null)
			return null;
		return collection(json).toArray();
	}

	public static Map<Object, Object> map(String json) {
		if (json == null)
			return null;
		try {
			return adapterMap.fromJson(json);
		} catch (Exception e1) {
		}
		return null;
	}

	private static Object parse(Class<?> clazz, Object object) {
		if (clazz == Ref.nms("IChatBaseComponent") || clazz == Ref.nms("IChatMutableComponent")
				|| clazz == Ref.nms("ChatBaseComponent") || clazz == Ref.nms("ChatMessage")
				|| clazz == Ref.nms("ChatComponentText")
				|| clazz == Ref.getClass("net.md_5.bungee.api.chat.TextComponent"))
			return Ref.IChatBaseComponent(object + "");
		Object o = Ref.newInstance(Ref.constructor(clazz));
		try {
			o = LoaderClass.unsafe.allocateInstance(clazz);
		} catch (Exception errr) {
		}
		if (o == null)
			return null;
		if (object instanceof Map<?, ?>)
			for (Entry<?, ?> s : ((Map<?, ?>) object).entrySet())
				setObject(o, s.getKey() + "", s.getValue());
		return o;
	}

	private static void setObject(Object o, String f, Object v) {
		if (v == null)
			Ref.set(o, f, v);
		Class<?> t = Ref.field(o.getClass(), f).getType();
		if (t == Integer.class || t == int.class)
			v = StringUtils.getInt(v + "");
		if (t == Double.class || t == double.class)
			v = StringUtils.getDouble(v + "");
		if (t == Float.class || t == float.class)
			v = StringUtils.getFloat(v + "");
		if (t == Long.class || t == long.class)
			v = StringUtils.getLong(v + "");
		if (t == Byte.class || t == byte.class)
			v = StringUtils.getByte(v + "");
		if (t == Short.class || t == short.class)
			v = StringUtils.getShort(v + "");
		Ref.set(o, f, v);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Object parseR(Object o) {
		if (o instanceof Collection) {
			List<Object> cloneOfList = new ArrayList<>((Collection<Object>) o);
			((Collection<Object>) o).clear();
			for (Object s : cloneOfList)
				((Collection<Object>) o).add(parseR(s));
			return o;
		}
		if (o instanceof Map) {
			Map<Object, Object> a = new HashMap<>();
			boolean c = false;
			for (Entry<?, ?> s : ((Map<?, ?>) o).entrySet()) {
				if (s.getKey().toString().startsWith("enum ")) {
					a.put(s.getKey(), Ref.getNulled(Ref.getClass((s.getKey().toString()).replaceFirst("enum ", "")),
							s.getValue() + ""));
					c = true;
				} else if (s.getKey().toString().startsWith("class ")) {
					a.put(s.getKey(), parse(Ref.getClass((s.getKey().toString()).replaceFirst("class ", "")),
							parseR(s.getValue())));
					c = true;
				} else if (s.getKey().toString().startsWith("Map ")) {
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
					a.put(s.getKey(), parseR(s.getValue()));
				}
			}
			if (a.size() == 1 && c)
				for (Object oa : a.values())
					return oa;
			return a;
		}
		return o;
	}

	private static Object fixObject(Object o) {
		if (o instanceof Collection)
			return fix((Collection<?>) o);
		if (o instanceof Map)
			return fix((Map<?, ?>) o);
		if (o instanceof Multimap)
			return fix((Multimap<?, ?>) o);
		return parseR(o);
	}

	private static Object fix(Collection<?> col) {
		@SuppressWarnings("unchecked")
		Collection<Object> fix = (Collection<Object>) Ref.newInstanceByClass(col.getClass().getName());
		for (Object o : col)
			fix.add(fixUknown(o));
		if (fix.size() == 1)
			for (Object o : fix)
				return o;
		return fix;
	}

	private static Object fix(Map<?, ?> col) {
		@SuppressWarnings("unchecked")
		Map<Object, Object> fix = (Map<Object, Object>) Ref.newInstanceByClass(col.getClass().getName());
		for (Entry<?, ?> o : col.entrySet())
			fix.put(fixUknown(o.getKey()), fixUknown(o.getValue()));
		if (fix.size() == 1)
			for (Object o : fix.values())
				return o;
		return fix;
	}

	private static Object fixUknown(Object o) {
		if (o instanceof Map) {
			Object preFix = fix((Map<?, ?>) o);
			if (preFix instanceof Map<?, ?> == false)
				return preFix;
			else {
				Map<?, ?> fixed = (Map<?, ?>) preFix;
				if (fixed.size() == 1) {
					Object toAdd = null;
					for (Object er : fixed.values())
						toAdd = er;
					return fixObject(toAdd);
				} else
					return fixed;
			}
		} else if (o instanceof Multimap) {
			Object preFix = fix((Multimap<?, ?>) o);
			if (preFix instanceof Multimap<?, ?> == false)
				return preFix;
			else {
				Multimap<?, ?> fixed = (Multimap<?, ?>) preFix;
				if (fixed.size() == 1) {
					Object toAdd = null;
					for (Object er : fixed.values())
						toAdd = er;
					return fixObject(toAdd);
				} else
					return fixed;
			}
		} else if (o instanceof Collection) {
			Object preFix = fix((Collection<?>) o);
			if (preFix instanceof Collection<?> == false)
				return preFix;
			else {
				Collection<?> fixed = (Collection<?>) preFix;
				if (fixed.size() == 1) {
					Object toAdd = null;
					for (Object er : fixed)
						toAdd = er;
					return fixObject(toAdd);
				} else
					return fixed;
			}
		}
		return fixObject(o);
	}

	private static Object fix(Multimap<?, ?> col) {
		@SuppressWarnings("unchecked")
		Multimap<Object, Object> fix = (Multimap<Object, Object>) Ref.newInstanceByClass(col.getClass().getName());
		for (Entry<?, ?> o : col.entries())
			fix.put(fixUknown(o.getKey()), fixUknown(o.getValue()));
		if (fix.size() == 1)
			for (Object o : fix.values())
				return o;
		return fix;
	}
}
