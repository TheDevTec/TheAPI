package me.DevTec.TheAPI.Utils.Json.jsonmaker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
					if(c!='"')
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
			if(c=='"')
				b.append("\\");
			b.append(c);
		}
		return b.toString();
	}
	
	public static String listToJson(List<?> list) {
		if(list == null)return null;
		String a = "{\"List\":[";
		for(Object o : list)
			if(o==null)
				a+=", null";
			else
				a+=", \""+addSplitters(objectToJson(o))+"\"";
		return a.replaceFirst(", ", "")+"]}";
	}
	
	public static String arrayToJson(Object[] list) {
		if(list == null)return null;
		String a = "{\"Array!"+list.getClass().getName().substring(2, list.getClass().getName().length()-1)+"\":[";
		for(Object o : list)
			a+=", \""+addSplitters(objectToJson(o))+"\"";
		return a.replaceFirst(", ", "")+"]}";
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
		String a = "{\""+o.getClass().getName()+"\":{";
		for(Field f : o.getClass().getDeclaredFields()) {
			if(Modifier.toString(f.getModifiers()).contains("static"))continue;
			Object w = Ref.get(o, f);
			if(w==null)continue;
			a+=", \""+f.getName()+"\":\""+addSplitters(objectToJson(w))+"\"";
		}
		return (a.replaceFirst(", ", "")+"}}");
	}
	
	public static Object objectFromJson(String o) {
		if(o==null||o.equals("null"))return null;
		if((o.startsWith("'") && o.endsWith("'") || o.startsWith("\"") && o.endsWith("\"")) && o.length()>1)o=o.substring(1, o.length()-1);
		Object a = itemFromJson(o);
		if(a!=null)return a;
		a = locationFromJson(o);
		if(a!=null)return a;
		a = listFromJson(o);
		if(a!=null)return a;
		a = enumToJson(o);
		if(a!=null)return a;
		a = enumFromJson(o);
		if(a!=null)return a;
		a = mapFromJson(o);
		if(a!=null)return a;
		Matcher m = splitterOfJson.matcher(o);
		if(!m.find())
			return StringUtils.isNumber(replaceSplitters(o))?StringUtils.getNumber(replaceSplitters(o)) : (StringUtils.isBoolean(replaceSplitters(o))?StringUtils.getBoolean(replaceSplitters(o)):replaceSplitters(o));
		try {
			a=Ref.getClass(m.group(1)).newInstance();
		} catch (Exception e) {
			try {
				a=LoaderClass.unsafe.allocateInstance(Ref.getClass(m.group(1)));
			}catch(Exception er) {
				
			}
		}
		if(a==null) return null;
		Matcher f = keys.matcher(m.group(2));
		while(f.find()) {
			String field = replaceSplitters(f.group(1));
			String object = replaceSplitters(f.group(2));
			Ref.set(a, field, object.equals("null")?null:objectFromJson(object));
		}
		return a;
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
	
	private static Pattern isList = Pattern.compile("\\{\"([lL][iI][sS][tT]|[Aa][Rr][Rr][Aa][Yy]!.*?)\":\\[(.*)\\]\\}"),
		    getterOfList = Pattern.compile("(\".*?(?!\\\\[\"]).\"|null)"),
			getterOfEnum = Pattern.compile("\\{\"(.*?(?!\\\\[\"]).)\":\"(.*?(?!\\\\[\"]).)\"\\}"),
			isMap = Pattern.compile("\\{\"Map:.*?(?!\\\\[\"]).\":\\[(\".*?(?!\\\\[\"]).\":(\".*?(?!\\\\[\"]).\"|null))[, ]*\\]\\}"),
			keys = Pattern.compile("\"(.*?(?!\\\\[\"]).)\":(\".*?(?!\\\\[\"]).\"|null)"),
			splitterOfJson = Pattern.compile("\\{\"(.*?(?!\\\\[\"]).)\":\\{(\".*?(?!\\\\[\"]).\":[\"]*.*?(?!\\\\[\"]).[\"]*)\\}\\}");
	
	public static List<?> listFromJson(String list) {
		if(list==null)return null;
		Matcher m = isList.matcher(list);
		if(m.find()) {
			List<Object> a = new ArrayList<>();
			Matcher f = getterOfList.matcher(m.group(2));
			while(f.find())
				a.add(objectFromJson(replaceSplitters(f.group(1))));
			return a;
		}
		return null;
	}
	
	public static Map<?, ?> mapFromJson(String map) {
		if(map==null)return null;
		Matcher m = isMap.matcher(map);
		if(m.find()) {
			Map<Object, Object> a = m.group().toLowerCase().contains("linked")?new LinkedHashMap<>():new HashMap<>();
			if(a!=null) {
				Matcher f = keys.matcher(m.group(1));
				while(f.find()) {
					a.put(objectFromJson(replaceSplitters(f.group(1))), objectFromJson(replaceSplitters(f.group(2))));
			}}
			return a;
		}
		return null;
	}
	
	public static Object[] arrayFromJson(String array) {
		if(array==null)return null;
		Matcher m = isList.matcher(array);
		if(m.find()) {
			List<Object> a = new ArrayList<>();
			if(a!=null) {
				Matcher f = getterOfList.matcher(m.group(2));
				while(f.find())
					a.add(objectFromJson(replaceSplitters(f.group(1))));
			}
			return a.toArray();
		}
		return null;
	}
	
	public static String mapToJson(Map<?, ?> map) {
		String a = "{\"Map:"+(map.getClass().getName().contains("Linked")?"Linked":"Hash")+"\":[";
		for(Entry<?, ?> o : map.entrySet())
			a+=", \""+addSplitters(objectToJson(o.getKey()))+"\":\""+addSplitters(objectToJson(o.getValue()))+"\"";
		return a.replaceFirst(", ", "")+"]}";
	}
	
	@SuppressWarnings("deprecation")
	public static String itemToJson(ItemStack stack) {
		if(stack == null)return null;
		String a = "{\""+ItemStack.class.getName()+"\":{";
		a+="\"type\":\""+stack.getType().name()+"\", ";
		a+="\"amount\":\""+stack.getAmount()+"\", ";
		a+="\"durability\":\""+stack.getDurability()+"\"";
		Object tag = Ref.invoke(Ref.invokeNulled(Ref.method(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), stack), "getTag");
		if(tag!=null && !(boolean)Ref.invoke(tag, "isEmpty"))
		a+=", \"meta\":\""+Ref.invoke(tag, "asString")+"\"";
		return a+"}";
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack itemFromJson(String stack) {
		if(stack == null)return null;
		ItemStack a = null;
		if(stack.startsWith("{\"org.bukkit.inventory.ItemStack\":{")) {
			stack=stack.replaceFirst(Pattern.quote("{\"org.bukkit.inventory.ItemStack\":{\"type\":\""), "");
			String material=stack.split("\", ")[0];
			String amount = stack.replaceFirst(material+"\", \"amount\":\"", "").split("\", ")[0];
			String dur = stack.replaceFirst(stack.split("\", ")[0]+"\", "+stack.split("\", ")[1]+"\", \"durability\":\"", "").split("\", ")[0];
			a = new ItemStack(Material.getMaterial(material), StringUtils.getInt(amount), StringUtils.getShort(dur));
			try {
			if(stack.replaceFirst(stack.split("\", ")[0]+"\", "+stack.split("\", ")[1]+"\", \"durability\":\"", "").split("\", ")[1].startsWith("\"meta\":")) {
				stack=(stack.replaceFirst(stack.split("\", ")[0]+"\", "+stack.split("\", ")[1]+"\", \"durability\":\"", "").split("\", ")[1]).replaceFirst("\"meta\":", "");
				stack=stack.substring(1, stack.length()-2);
				Object item = Ref.invokeNulled(Ref.method(Ref.craft("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class), a);
				Ref.invoke(item, Ref.method(Ref.nms("ItemStack"), "setTag", Ref.nms("NBTTagCompound")), Ref.invokeNulled(Ref.method(Ref.nms("MojangsonParser"), "parse", String.class), stack));
				return (ItemStack)Ref.invokeNulled(Ref.method(Ref.craft("inventory.CraftItemStack"), "asBukkitCopy", Ref.nms("ItemStack")), item);
			}
			}catch(Exception er) {}
		}
		return a;
	}
	
	public static String locationToJson(Location loc) {
		if(loc==null || loc.getWorld()==null)return null;
		String a = "{\""+Location.class.getName()+"\":{";
		a+="\"world\":\""+loc.getWorld().getName()+"\", ";
		a+="\"x\":\""+loc.getX()+"\", ";
		a+="\"y\":\""+loc.getY()+"\", ";
		a+="\"z\":\""+loc.getZ()+"\", ";
		a+="\"Yaw\":\""+loc.getYaw()+"\", ";
		a+="\"Pitch\":\""+loc.getPitch()+"\"}";
		return a;
	}
	
	public static Location locationFromJson(String loc) {
		if(loc==null)return null;
		Location a = null;
		if(loc.startsWith("{\"org.bukkit.Location\":{")) {
			loc=loc.replaceFirst(Pattern.quote("{\"org.bukkit.Location\":{\"world\":\""), "");
			String world=loc.split("\", ")[0];
			String x = loc.replaceFirst(world+"\", \"x\":\"", "").split("\", ")[0];
			String y = loc.replaceFirst(loc.split("\", ")[0]+"\", "+loc.split("\", ")[1]+"\", \"y\":\"", "").split("\", ")[0];
			String z = loc.replaceFirst(loc.split("\", ")[0]+"\", "+loc.split("\", ")[1]+"\", "+loc.split("\", ")[2]+"\", \"z\":\"", "").split("\", ")[0];
			String yaw = loc.replaceFirst(loc.split("\", ")[0]+"\", "+loc.split("\", ")[1]+"\", "+loc.split("\", ")[2]+"\", "+loc.split("\", ")[3]+"\", \"yaw\":\"", "").split("\", ")[0];
			String pitch = loc.replaceFirst(loc.split("\", ")[0]+"\", "+loc.split("\", ")[1]+"\", "+loc.split("\", ")[2]+"\", "+loc.split("\", ")[3]+"\", "+loc.split("\", ")[4]+"\", \"pitch\":\"", "").split("\", ")[0];
			a = new Location(Bukkit.getWorld(world), StringUtils.getDouble(x), StringUtils.getDouble(y), StringUtils.getDouble(z), StringUtils.getFloat(yaw), StringUtils.getFloat(pitch));
		}
		return a;
	}
}
