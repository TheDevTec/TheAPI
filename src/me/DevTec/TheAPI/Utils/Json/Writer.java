package me.DevTec.TheAPI.Utils.Json;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Multimap;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class Writer {
	private static String write(Map<?, ?> values, boolean whiteSpaces, boolean addNulls, boolean fancy) {
		StringBuilder b = new StringBuilder("{");
		int i = 0;
		for (Entry<?, ?> e : values.entrySet()) {
			if (e.getKey() == null)
				continue;
			if (e.getValue() == null && addNulls || e.getValue() != null) {
				if (fancy) {
					b.append((i != 0 ? (whiteSpaces ? ", " : ",") : "") + System.lineSeparator() + "   \""
							+ object(e.getKey(), whiteSpaces, addNulls, true) + "\":");
					if (e.getValue() instanceof Comparable) {
						b.append(e.getValue() + "");
					} else if (e.getValue() instanceof Collection) {
						b.append(prepareCollection(addNulls, 1, (Collection<?>) e.getValue()));
					} else if (e.getValue() instanceof Map) {
						b.append(prepareMap(addNulls, 1, (Map<?, ?>) e.getValue()));
					} else if (e.getValue() instanceof Multimap) {
						b.append(prepareMap(addNulls, 1, (Multimap<?, ?>) e.getValue()));
					} else
						b.append(Writer.object(e.getValue(), whiteSpaces, addNulls, true));
				} else {
					b.append((i != 0 ? (whiteSpaces ? ", \"" : ",\"") : "\"")
							+ Writer.object(e.getKey(), whiteSpaces, addNulls, true) + "\":"
							+ object(e.getValue(), whiteSpaces, addNulls));
				}
				++i;
			}
		}
		return b.append((i > 0 ? (fancy ? System.lineSeparator() : "") : "") + "}").toString();
	}

	private static String write(Multimap<?, ?> values, boolean whiteSpaces, boolean addNulls, boolean fancy) {
		StringBuilder b = new StringBuilder("{");
		int i = 0;
		for (Entry<?, ?> e : values.entries()) {
			if (e.getKey() == null)
				continue;
			if (e.getValue() == null && addNulls || e.getValue() != null) {
				if (fancy) {
					b.append((i != 0 ? (whiteSpaces ? ", " : ",") : "") + System.lineSeparator() + "   \""
							+ Writer.object(e.getKey(), whiteSpaces, addNulls, true) + "\":");
					if (e.getValue() instanceof Comparable) {
						b.append(e.getValue() + "");
					} else if (e.getValue() instanceof Collection) {
						b.append(prepareCollection(addNulls, 1, (Collection<?>) e.getValue()));
					} else if (e.getValue() instanceof Map) {
						b.append(prepareMap(addNulls, 1, (Map<?, ?>) e.getValue()));
					} else if (e.getValue() instanceof Multimap) {
						b.append(prepareMap(addNulls, 1, (Multimap<?, ?>) e.getValue()));
					} else
						b.append((i != 0 ? (whiteSpaces ? ", " : ",") : "") + System.lineSeparator()
								+ Writer.object(e.getValue(), whiteSpaces, addNulls, true));
				} else {
					b.append((i != 0 ? (whiteSpaces ? ", \"" : ",\"") : "\"")
							+ Writer.object(e.getKey(), whiteSpaces, addNulls, true) + "\":"
							+ Writer.object(e.getValue(), whiteSpaces, addNulls));
				}
				++i;
			}
		}
		return b.append((i > 0 ? (fancy ? System.lineSeparator() : "") : "") + "}").toString();
	}

	private static String write(Collection<?> values, boolean whiteSpaces, boolean addNulls, boolean fancy) {
		StringBuilder b = new StringBuilder("[");
		int i = 0;
		for (Object e : values) {
			if (e == null && addNulls || e != null) {
				if (fancy) {
					if (e instanceof Comparable) {
						b.append((i != 0 ? (whiteSpaces ? ", " : ",") : "") + System.lineSeparator() + "  " + e);
					} else if (e instanceof Collection) {
						b.append((i != 0 ? (whiteSpaces ? ", " : ",") : "") + System.lineSeparator() + "  "
								+ prepareCollection(addNulls, 1, (Collection<?>) e));
					} else if (e instanceof Map) {
						b.append((i != 0 ? (whiteSpaces ? ", " : ",") : "") + System.lineSeparator() + "  "
								+ prepareMap(addNulls, 1, (Map<?, ?>) e));
					} else if (e instanceof Multimap) {
						b.append((i != 0 ? (whiteSpaces ? ", " : ",") : "") + System.lineSeparator() + "  "
								+ prepareMap(addNulls, 1, (Multimap<?, ?>) e));
					} else
						b.append((i != 0 ? (whiteSpaces ? ", " : ",") : "") + System.lineSeparator() + "  "
								+ object(e, whiteSpaces, addNulls, true) + "");
				} else {
					b.append((i != 0 ? (whiteSpaces ? ", " : ",") : "") + Writer.object(e, whiteSpaces, addNulls, fancy)
							+ "");
				}
				++i;
			}
		}
		return b.append((i > 0 ? (fancy ? System.lineSeparator() : "") : "") + "]").toString();
	}

	private static String prepareCollection(boolean addNulls, int spaces, Collection<?> list) {
		String space = "";
		String end = "";
		for (int i = 0; i < spaces + 1; ++i)
			space += "  ";
		end = space.replaceFirst("  ", "");
		StringBuilder b = new StringBuilder("[");
		int i = 0;
		for (Object o : list) {
			if (o == null && addNulls || o != null) {
				b.append(
						(i != 0 ? (",") : "") + System.lineSeparator() + space
								+ (o instanceof Collection ? prepareCollection(addNulls, spaces + 1, (Collection<?>) o)
										: (o instanceof Map ? prepareMap(addNulls, spaces + 1, (Map<?, ?>) o)
												: (o instanceof Multimap
														? prepareMap(addNulls, spaces + 1, (Multimap<?, ?>) o)
														: object(o, true, addNulls, true)))));
				++i;
			}
		}
		return b.append((i > 0 ? System.lineSeparator() : "") + (i > 0 ? end : "") + "]").toString();
	}

	private static String prepareMap(boolean addNulls, int spaces, Map<?, ?> list) {
		String space = "";
		String end = "";
		for (int i = 0; i < spaces + 1; ++i)
			space += "  ";
		end = space.replaceFirst("  ", "");
		StringBuilder b = new StringBuilder("{");
		int i = 0;
		for (Entry<?, ?> o : list.entrySet()) {
			if (o.getValue() == null && addNulls || o.getValue() != null) {
				b.append((i != 0 ? (",") : "") + System.lineSeparator() + space + "\"" + o.getKey() + "\":"
						+ (o.getValue() instanceof Collection
								? prepareCollection(addNulls, spaces + 1, (Collection<?>) o.getValue())
								: (o.getValue() instanceof Map
										? prepareMap(addNulls, spaces + 1, (Map<?, ?>) o.getValue())
										: object(o.getValue(), true, addNulls, true))));
				++i;
			}
		}
		return b.append((i > 0 ? System.lineSeparator() : "") + (i > 0 ? end : "") + "}").toString();
	}

	private static String prepareMap(boolean addNulls, int spaces, Multimap<?, ?> list) {
		String space = "";
		String end = "";
		for (int i = 0; i < spaces + 1; ++i)
			space += "  ";
		end = space.replaceFirst("  ", "");
		StringBuilder b = new StringBuilder("{");
		int i = 0;
		for (Entry<?, ?> o : list.entries()) {
			if (o.getValue() == null && addNulls || o.getValue() != null) {
				b.append((i != 0 ? (",") : "") + System.lineSeparator() + space + "\"" + o.getKey() + "\":"
						+ (o.getValue() instanceof Collection
								? prepareCollection(addNulls, spaces + 1, (Collection<?>) o.getValue())
								: (o.getValue() instanceof Map
										? prepareMap(addNulls, spaces + 1, (Map<?, ?>) o.getValue())
										: Writer.object(o.getValue(), true, addNulls, true))));
				++i;
			}
		}
		return b.append((i > 0 ? System.lineSeparator() : "") + (i > 0 ? end : "") + "}").toString();
	}

	public static String array(Object[] object, boolean whiteSpace, boolean addNulls) {
		if (object == null)
			return null;
		return write(Arrays.asList(object), whiteSpace, addNulls, false);
	}

	public static String array(Object[] object, boolean whiteSpace, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		return write(Arrays.asList(object), whiteSpace, addNulls, fancy);
	}

	public static String collection(Collection<?> object, boolean whiteSpace, boolean addNulls) {
		if (object == null)
			return null;
		return write(object, whiteSpace, addNulls, false);
	}

	public static String collection(Collection<?> object, boolean whiteSpace, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		return write(object, whiteSpace, addNulls, fancy);
	}

	public static String map(Map<?, ?> object, boolean whiteSpace, boolean addNulls) {
		if (object == null)
			return null;
		return write(object, whiteSpace, addNulls, false);
	}

	public static String map(Map<?, ?> object, boolean whiteSpace, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		return write(object, whiteSpace, addNulls, fancy);
	}

	public static String map(Multimap<?, ?> object, boolean whiteSpace, boolean addNulls) {
		if (object == null)
			return null;
		return write(object, whiteSpace, addNulls, false);
	}

	public static String map(Multimap<?, ?> object, boolean whiteSpace, boolean addNulls, boolean fancy) {
		if (object == null)
			return null;
		return write(object, whiteSpace, addNulls, fancy);
	}

	private static Method from = Ref.method(Ref.craft("util.CraftChatMessage"), "fromComponent",
			Ref.nms("IChatBaseComponent"));

	/**
	 * 
	 * @param object Object to parse to String
	 * @return String Object converted to String
	 */
	public static String object(Object w, boolean whiteSpace, boolean addNulls, boolean fancy) {
		if (w == null)
			return "null";
		if (w instanceof String || w.getClass() == Ref.nms("IChatBaseComponent")
				|| w.getClass() == Ref.nms("IChatMutableComponent") || w.getClass() == Ref.nms("ChatBaseComponent")
				|| w.getClass() == Ref.nms("ChatMessage") || w.getClass() == Ref.nms("ChatComponentText")
				|| w.getClass() == Ref.getClass("net.md_5.bungee.api.chat.TextComponent")) {
			if (w instanceof String)
				return f("" + w);
			String obj = w.getClass() == Ref.getClass("net.md_5.bungee.api.chat.TextComponent")
					? (String) Ref.invoke(w, "toLegacyText")
					: (String) Ref.invoke(Ref.craft("util.CraftChatMessage"), from,
							Ref.cast(Ref.nms("IChatBaseComponent"), w));
			Map<String, Object> enumMap = new HashMap<>();
			enumMap.put("class " + w.getClass().getName(), "\"" + f(obj) + "\"");
			return map(enumMap, whiteSpace, addNulls, fancy);
		}
		if (w instanceof Enum<?>) {
			Map<String, Object> enumMap = new HashMap<>();
			enumMap.put("enum " + w.getClass().getName(), w.toString());
			return map(enumMap, whiteSpace, addNulls, fancy);
		}
		if (w instanceof Comparable)
			return "" + w;
		if (w instanceof Object[])
			return array((Object[]) w, whiteSpace, addNulls, fancy);
		if (w instanceof Collection) {
			if (w instanceof ArrayList || w.getClass() == Ref.getClass("java.util.Arrays$ArrayList")
					|| w instanceof HashSet) {
				return collection((Collection<?>) w, whiteSpace, addNulls, fancy);
			}
			Map<String, Object> enumMap = new HashMap<>();
			enumMap.put("Collection " + w.getClass().getName(),
					collection((Collection<?>) w, whiteSpace, addNulls, fancy));
			return map(enumMap, whiteSpace, addNulls, fancy);
		}
		if (w instanceof Map) {
			if (w instanceof HashMap) {
				return map((Map<?, ?>) w, whiteSpace, addNulls, fancy);
			}
			Map<String, Object> enumMap = new HashMap<>();
			enumMap.put("Map " + w.getClass().getName(), map((Map<?, ?>) w, whiteSpace, addNulls, fancy));
			return map(enumMap, whiteSpace, addNulls, fancy);
		}
		if (w instanceof Multimap) {
			Map<String, Object> enumMap = new HashMap<>();
			enumMap.put("Multimap " + w.getClass().getName(), map((Multimap<?, ?>) w, whiteSpace, addNulls, fancy));
			return map(enumMap, whiteSpace, addNulls, fancy);
		}
		return map(convert(w, whiteSpace, addNulls), whiteSpace, addNulls, fancy);
	}

	/**
	 * 
	 * @param object Object to parse to String
	 * @return String Object converted to String
	 */
	public static String object(Object object, boolean whiteSpace, boolean addNulls) {
		return object(object, whiteSpace, addNulls, false);
	}

	private static List<String> FORBIDDEN = new ArrayList<>();
	static {
		FORBIDDEN.add("org.bukkit.craftbukkit." + TheAPI.getServerVersion()
				+ ".persistence.CraftPersistentDataAdapterContext");
		FORBIDDEN.add(
				"org.bukkit.craftbukkit." + TheAPI.getServerVersion() + ".persistence.CraftPersistentDataTypeRegistry");
	}

	private static Map<String, Object> convert(Object object, boolean whiteSpace, boolean addNulls) {
		Map<String, Object> item = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		for (Field f : Ref.getAllFields(object.getClass())) {
			if (Modifier.toString(f.getModifiers()).toLowerCase().contains("static"))
				continue;
			Object w = Ref.get(object, f);
			if (w == null && !addNulls || addNulls)
				continue;
			if (FORBIDDEN.contains(w.getClass().getName()))
				continue;
			if (w instanceof String || w.getClass() == Ref.nms("IChatBaseComponent")
					|| w.getClass() == Ref.nms("IChatMutableComponent") || w.getClass() == Ref.nms("ChatBaseComponent")
					|| w.getClass() == Ref.nms("ChatMessage") || w.getClass() == Ref.nms("ChatComponentText")
					|| w.getClass() == Ref.getClass("net.md_5.bungee.api.chat.TextComponent")) {
				if (w instanceof String) {
					map.put(f.getName(), f("" + w));
					continue;
				} else {
					String obj = w.getClass() == Ref.getClass("net.md_5.bungee.api.chat.TextComponent")
							? (String) Ref.invoke(w, "toLegacyText")
							: (String) Ref.invoke(
									Ref.craft("util.CraftChatMessage"), Ref.method(Ref.craft("util.CraftChatMessage"),
											"fromComponent", Ref.nms("IChatBaseComponent")),
									Ref.cast(Ref.nms("IChatBaseComponent"), w));
					Map<String, Object> enumMap = new HashMap<>();
					enumMap.put("class " + w.getClass().getName(), "\"" + f(obj) + "\"");
					map.put(f.getName(), enumMap);
					continue;
				}
			}
			if (w instanceof Enum<?>) {
				Map<String, Object> enumMap = new HashMap<>();
				enumMap.put("enum " + w.getClass().getName(), "\"" + w.toString() + "\"");
				map.put(f.getName(), enumMap);
				continue;
			}
			map.put(f.getName(), w);
		}
		item.put("class " + object.getClass().getName(), map);
		return item;
	}

	private static String f(String s) {
		StringBuilder a = new StringBuilder(s.length());
		boolean before = false;
		for (char c : s.toCharArray()) {
			if (c == '\\') {
				before = true;
				a.append(c);
				continue;
			}
			if (c == '"') {
				if (before)
					a.append('\\');
				a.append(c);
				before = false;
				continue;
			}
			before = false;
			a.append(c);
		}
		return a.toString();
	}
}
