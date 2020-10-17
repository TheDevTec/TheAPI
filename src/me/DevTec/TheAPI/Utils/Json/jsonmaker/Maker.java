package me.DevTec.TheAPI.Utils.Json.jsonmaker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import me.DevTec.TheAPI.APIs.ItemCreatorAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.Reflections.Ref;
import me.DevTec.TheAPI.Utils.TheAPIUtils.LoaderClass;

@SuppressWarnings("restriction")
public class Maker {
	private List<String> values = new ArrayList<>();

	public Maker add(MakerObject o) {
		return add(o.toString());
	}

	public Maker add(Object o) {
		values.add(objectToJson(o));
		return this;
	}

	public MakerObject create() {
		return new MakerObject();
	}
	
	public String toString() {
		return listToJson(values);
	}
	
	public class MakerObject {
		HashMap<Object, Object> o = new HashMap<>();
		public Maker getMaker() {
			return Maker.this;
		}
		
		public MakerObject add(Object key, Object item) {
			o.put(key, item);
			return this;
		}

		public MakerObject put(Object key, Object item) {
			add(key, item);
			return this;
		}
		
		public MakerObject remove(Object key) {
			o.remove(key);
			return this;
		}
		
		public Object get(Object key) {
			return o.get(key);
		}
		
		public boolean containsKey(Object key) {
			return o.containsKey(key);
		}
		
		public boolean containsValue(Object value) {
			return o.containsKey(value);
		}
		
		public String toString() {
			return mapToJson(o);
		}
	}
	
	private static String replaceSplitters(String s) {
		StringBuilder b = new StringBuilder();
		int paused = 0;
		for(char c : s.toCharArray())
			if(c=='\\') {
				if(paused == 0)
				paused=1;
				else b.append("\\");
			}else {
				if(paused==1){
					if(c!='"'&&c!='['&&c!=']')
					b.append("\\");
				}
				b.append(c);
				paused=0;
			}
		return b.toString();
	}
	
	private static String addSplitters(String s) {
		StringBuilder b = new StringBuilder();
		for(char c : (""+s).toCharArray()) {
			if(c=='"'||c=='['||c==']')
				b.append("\\");
			b.append(c);
		}
		return b.toString();
	}
	
	public static String listToJson(List<?> list) {
		if(list == null)return null;
		String a = "[";
		for(Object o : list)
			if(o==null)
				a+=", null";
			else
				a+=", \""+addSplitters(objectToJson(o))+"\"";
		return a.replaceFirst(", ", "")+"]";
	}
	
	public static String arrayToJson(Object[] list) {
		if(list == null)return null;
		return listToJson(Arrays.asList(list));
	}
	
	public static String objectToJson(Object o) {
		if(o == null)return null;
		if(o instanceof Comparable<?>)return o+"";
		if(o instanceof ItemStack)return itemToJson((ItemStack) o);
		if(o instanceof Location)return locationToJson((Location) o);
		if(o instanceof Enum<?>)return enumToJson(o);
		if(o instanceof List<?>)return listToJson((List<?>)o);
		if(o instanceof Map<?, ?>)return mapToJson((Map<?, ?>)o);
		if(o instanceof Object[])return arrayToJson((Object[])o);
		String a = "\""+o.getClass().getName()+"\":{";
		for(Field f : o.getClass().getDeclaredFields()) {
			if(Modifier.toString(f.getModifiers()).contains("static"))continue;
			Object w = Ref.get(o, f);
			if(w==null)continue;
			a+=", \""+f.getName()+"\":\""+addSplitters(objectToJson(w))+"\"";
		}
		return (a.replaceFirst(", ", "")+"}");
	}
	
	public static Object objectFromJson(String o) {
		if(o==null||o.equals("null"))return null;
		try {
		if((o.startsWith("'") && o.endsWith("'") || o.startsWith("\"") && o.endsWith("\"")) && o.length()>1)o=o.substring(1, o.length()-1);
		Object a = mapFromJson(o);
		if(a!=null)return a;
		a = listFromJson(o);
		if(a!=null)return a;
		a = locationFromJson(o);
		if(a!=null)return a;
		a = enumToJson(o);
		if(a!=null)return a;
		a = enumFromJson(o);
		if(a!=null)return a;
		a = itemFromJson(o);
		if(a!=null)return a;
		Matcher m = splitterOfJson.matcher(o);
		if(!m.find())
			return StringUtils.isNumber(replaceSplitters(o))?StringUtils.getNumber(replaceSplitters(o)) : (StringUtils.isBoolean(replaceSplitters(o))?StringUtils.getBoolean(replaceSplitters(o)):replaceSplitters(o));
		try {
			a=Ref.getClass(m.group(1)).newInstance();
		} catch (Exception e) {
			try {
				a=LoaderClass.unsafe.allocateInstance(Ref.getClass(m.group(1)));
			}catch(Exception | StackOverflowError er) {}
		}
		if(a==null) return null;
		Matcher f = keys.matcher(m.group(2));
		while(f.find()) {
			String field = replaceSplitters(f.group(1));
			String object = replaceSplitters(f.group(2));
			Ref.set(a, field, object.equals("null")?null:objectFromJson(object));
		}
		return a;
		}catch(Exception | StackOverflowError er) {
			return null;
		}
	}
	
	public static String enumToJson(Object enumm) {
		if(enumm == null || enumm instanceof Enum<?> == false)return null;
		return "{\""+enumm.getClass().getName()+"\":\""+enumm+"\"}";
	}
	
	public static Object enumFromJson(String enumm) {
		if(enumm == null)return null;
		Object a = null;
		Matcher s = getterOfEnum.matcher(enumm);
		if(s.find())
			a=Ref.get(null, Ref.field(Ref.getClass(replaceSplitters(s.group(1))), replaceSplitters(s.group(2))));
		return a;
	}
	
	private static Pattern isList = Pattern.compile("\\[(\".*?(?!\\\\[\\]]).\"|null)\\]"),
		    getterOfList = Pattern.compile("(\".*?(?!\\\\[\"]).\"|null)"),
			getterOfEnum = Pattern.compile("\\{\"(.*?(?!\\\\[\"]).)\":\"(.*?(?!\\\\[\"]).)\"\\}"),
			isMap = Pattern.compile("\\[(\".*?(?!\\\\[\\]]).\"=\".*?(?!\\\\[\\]]).\")\\]"),
			keys = Pattern.compile("\"(.*?(?!\\\\[\"]).)\":(\".*?(?!\\\\[\"]).\"|null)"),
			mapKeys = Pattern.compile("\"(.*?(?!\\\\\").)\"=\"(.*?(?!\\\\\").)\""),
			splitterOfJson = Pattern.compile("\"(.*?(?!\\\\[\"]).)\":\\{(\".*?(?!\\\\[\"]).\":[\"]*.*?(?!\\\\[\"]).[\"]*)\\}");
	
	public static List<?> listFromJson(String list) {
		if(list==null)return null;
		try {
		Matcher m = isList.matcher(list);
		if(m.find()) {
			List<Object> a = new ArrayList<>();
			Matcher f = getterOfList.matcher(m.group(1));
			while(f.find())
				a.add(objectFromJson(replaceSplitters(f.group(1))));
			return a;
		}
		}catch(Exception | StackOverflowError er) {}
		return null;
	}
	
	public static Map<?, ?> mapFromJson(String map) {
		if(map==null)return null;
		try {
		Matcher m = isMap.matcher(map);
		if(m.find()) {
			Map<Object, Object> a = new HashMap<>();
			try {
			Matcher f = mapKeys.matcher(m.group(1));
			while(f.find())
				a.put(objectFromJson(replaceSplitters(f.group(1))), objectFromJson(replaceSplitters(f.group(2))));
			}catch(Exception empty) {}
			return a;
		}
		}catch(Exception | StackOverflowError er) {}
		return null;
	}
	
	public static Object[] arrayFromJson(String array) {
		if(array==null)return null;
		return listFromJson(array).toArray();
	}
	
	public static String mapToJson(Map<?, ?> map) {
		String a = "[";
		for(Entry<?, ?> o : map.entrySet())
			a+=", \""+addSplitters(objectToJson(o.getKey()))+"\"=\""+addSplitters(objectToJson(o.getValue()))+"\"";
		return a.replaceFirst(", ", "")+"]";
	}
	
	@SuppressWarnings("deprecation")
	public static String itemToJson(ItemStack ss) {
		if(ss == null)return null;
		ItemStack stack = ss.clone();
		Maker m = new Maker();
		MakerObject contents = m.create();
		contents.add("type", ss.getType().name());
		contents.add("amount", ss.getAmount());
		if(ss.getDurability()>0)
		contents.add("durability", ss.getDurability());
		ItemMeta meta = stack.getItemMeta();
		if(meta.hasLore())
		contents.add("lore", listToJson(meta.getLore()));
		if(meta.hasEnchants()) {
			String a = "[";
			for(Entry<Enchantment, Integer> o : meta.getEnchants().entrySet())
				a+=", \""+addSplitters(objectToJson(o.getKey().getName()))+"\"=\""+addSplitters(objectToJson(o.getValue()))+"\"";
		contents.add("enchants", a.replaceFirst(", ", "")+"]");
		}
		if(meta.hasDisplayName())
		contents.add("displayName", meta.getDisplayName());
		if(meta.hasDisplayName())
		meta.setDisplayName(null);
		if(meta.hasLore())
			meta.setLore(null);
		if(meta.hasEnchants()) {
			for(Enchantment e : meta.getEnchants().keySet())
			meta.removeEnchant(e);
		}
		stack.setItemMeta(meta);
		Object tag = Ref.invoke(Ref.invokeNulled(Ref.method(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), stack), "getTag");
		if(tag!=null)
			contents.add("tag", (String)Ref.invoke(tag, "asString"));
		return m.add(m.create().add(ItemStack.class.getName(), contents.toString()).toString()).toString();
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static ItemStack itemFromJson(String a) {
		if(a == null)return null;
		try {
			if((a.startsWith("'") && a.endsWith("'") || a.startsWith("\"") && a.endsWith("\"")) && a.length()>1)a=a.substring(1, a.length()-1);
			Map<?, ?> items = (Map<?, ?>) ((Map<?, ?>)((List<?>) Maker.listFromJson(a)).get(0)).get("org.bukkit.inventory.ItemStack");
			ItemStack stack = ItemCreatorAPI.create(Material.matchMaterial((String)items.get("type")), (int)items.get("amount"), null, null, items.containsKey("durability")?(int)items.get("durability"):0);
			Object item = Ref.invokeNulled(Ref.method(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), stack);
			Ref.invoke(item, Ref.method(Ref.nms("ItemStack"), "setTag", Ref.nms("NBTTagCompound")), Ref.invokeNulled(Ref.method(Ref.nms("MojangsonParser"), "parse", String.class), (String)items.get("tag")));
			ItemMeta meta = (ItemMeta)Ref.invokeNulled(Ref.method(Ref.craft("inventory.CraftItemStack"), "getItemMeta", Ref.nms("ItemStack")), item);
			meta.setDisplayName((String)items.get("displayName"));
			meta.setLore((List<String>)items.get("lore"));
			stack.setItemMeta(meta);
			if(items.containsKey("enchants")) {
			Map<?, ?> enchs = (Map<?, ?>) items.get("enchants");
			Map<Enchantment, Integer> fixed = new HashMap<>(enchs.size());
			for(Entry<?, ?> e : enchs.entrySet())
				if(e.getKey() instanceof Enchantment || e.getKey() instanceof String)
				fixed.put(e.getKey() instanceof String?Enchantment.getByName((String)e.getKey()):(Enchantment)e.getKey(), StringUtils.getInt(""+e.getValue()));
			stack.addUnsafeEnchantments(fixed);
			}
			return stack;
		}catch(Exception | StackOverflowError er) {
			return null;
		}
	}
	
	public static String locationToJson(Location loc) {
		if(loc==null || loc.getWorld()==null)return null;
		Maker m = new Maker();
		MakerObject contents = m.create();
		contents.add("world", loc.getWorld().getName());
		contents.add("x", loc.getX());
		contents.add("y", loc.getY());
		contents.add("z", loc.getZ());
		contents.add("yaw", loc.getYaw());
		contents.add("pitch", loc.getPitch());
		return m.add(m.create().add(Location.class.getName(), contents.toString()).toString()).toString();
	}
	
	public static Location locationFromJson(String loc) {
		if(loc == null)return null;
		try {
			if((loc.startsWith("'") && loc.endsWith("'") || loc.startsWith("\"") && loc.endsWith("\"")) && loc.length()>1)loc=loc.substring(1, loc.length()-1);
			Map<?, ?> items = (Map<?, ?>) ((Map<?, ?>)((List<?>) Maker.listFromJson(loc)).get(0)).get("org.bukkit.Location");
			return new Location(Bukkit.getWorld((String)items.get("world")), (double)items.get("x"), (double)items.get("y"), (double)items.get("z"), (float)(double)items.get("yaw"), (float)(double)items.get("pitch"));
		}catch(Exception | StackOverflowError er) {
			return null;
		}
	}
}
