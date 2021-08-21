package me.devtec.theapi.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.devtec.theapi.utils.json.JsonWriter;
import org.bukkit.ChatColor;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class ChatMessage {
	static final Pattern url = Pattern.compile("(w{3}\\\\.|[a-zA-Z0-9+&@#/%?=~_|!:,.;-]+:\\/\\/)?[a-zA-Z0-9+&@#/%?=~_|!:,.;-]+\\w\\.[a-zA-Z0-9+&@#/%?=~_|!:,.;-]{2,}\\w");
	private String text, color = "";
	private boolean bold = false, italic = false, obfuscated = false, strike = false, under = false, change = false;
	
	private final List<Map<String, Object>> join = new ArrayList<>();
	
	public ChatMessage(String text) {
		this.text=text;
		convert();
	}
	
	public static Object toIChatBaseComponent(String text) {
		return new ChatMessage(text).toNMS();
	}
	
	public static Object toIChatBaseComponent(ChatMessage text) {
		return text.toNMS();
	}
	
	public static ChatMessage fromIChatBaseComponent(Object component) {
		return new ChatMessage(NMSAPI.fromComponent(component));
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> fixListMap(List<Map<String, Object>> lists) {
		if(lists==null)return null;
		ListIterator<Map<String, Object>> it = lists.listIterator();
		while(it.hasNext()) {
			Map<String, Object> text = it.next();
			Map<String, Object> hover=(Map<String, Object>) text.get("hoverEvent"), click=(Map<String, Object>) text.get("clickEvent");
			String interact=(String) text.get("insertion");
			boolean remove = false;
			for(Entry<String, Object> s : text.entrySet()) {
				if(s.getKey().equals("color")||s.getKey().equals("insertion"))continue;
				if(s.getValue()instanceof String) {
					ChatMessage c = new ChatMessage((String) s.getValue());
					if(!c.join.isEmpty()) {
						try {
							if(!remove) {
							it.remove();
							remove=true;
							}
						}catch(Exception err) {}
						for(Map<String, Object> d : c.join) {
							if(!d.containsKey("color") && text.containsKey("color"))
								d.put("color", text.get("color"));
							if(hover!=null && !d.containsKey("hoverEvent"))
								d.put("hoverEvent", hover);
							if(click!=null && !d.containsKey("clickEvent"))
								d.put("clickEvent", click);
							if(interact!=null && !d.containsKey("insertation"))
								d.put("insertion", interact);
							it.add(d);
						}
					}
				}else
					if(s.getValue()instanceof Map) //hoverEvent
						text.put(s.getKey(), s.getValue());
					else
						if(s.getValue()instanceof List) //extras
							text.put(s.getKey(), fixListMap((List<Map<String, Object>>) s.getValue()));
			}
		}
		return lists;
	}
	
	public static ChatMessage fromString(String text) {
		return new ChatMessage(text);
	}
	
	private static final Constructor<?> chat = Ref.constructor(Ref.nmsOrOld("network.chat.ChatComponentText", "ChatComponentText"), String.class);
	private static final Constructor<?> clickEvent = Ref.constructor(Ref.nmsOrOld("network.chat.ChatClickable.ChatClickable", "ChatClickable"), Ref.nmsOrOld("network.chat.ChatClickable.ChatClickable$EnumClickAction", "EnumClickAction"), String.class);
	private static final Method addSibling=Ref.findMethodByName(Ref.nmsOrOld("network.chat.ChatComponentText", "ChatComponentText"), "addSibling");
	private static final Method getChatModif=Ref.method(Ref.nmsOrOld("network.chat.IChatBaseComponent", "IChatBaseComponent"), "getChatModifier");
	private static final Method setChatModif=Ref.findMethodByName(Ref.nmsOrOld("network.chat.ChatComponentText", "ChatComponentText"), "setChatModifier");
	private static final Method setBold=Ref.method(Ref.nmsOrOld("network.chat.ChatModifier", "ChatModifier"), "setBold", Boolean.class);
	private static final Method setItalic=Ref.method(Ref.nmsOrOld("network.chat.ChatModifier", "ChatModifier"), "setItalic", Boolean.class);
	private static final Method setRandom=Ref.method(Ref.nmsOrOld("network.chat.ChatModifier", "ChatModifier"), "setRandom", Boolean.class);
	private static final Method setStrikethrough=Ref.method(Ref.nmsOrOld("network.chat.ChatModifier", "ChatModifier"), "setStrikethrough", Boolean.class);
	private static final Method setChatClickable=Ref.method(Ref.nmsOrOld("network.chat.ChatModifier", "ChatModifier"), "setChatClickable", Ref.nmsOrOld("network.chat.ChatClickable", "ChatClickable"));
	private static final Method setUnderline=Ref.method(Ref.nmsOrOld("network.chat.ChatModifier", "ChatModifier"), "setUnderline", Boolean.class);
	private static final Method hex=Ref.method(Ref.nmsOrOld("network.chat.ChatHexColor", "ChatHexColor"), "a", String.class);
	private static Method colors=Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a", char.class);
	private static final Method setColorHex=Ref.method(Ref.nmsOrOld("network.chat.ChatModifier", "ChatModifier"), "setColor", Ref.nmsOrOld("network.chat.ChatHexColor", "ChatHexColor"));
	private static final Method setColorNormal=Ref.method(Ref.nmsOrOld("network.chat.ChatModifier", "ChatModifier"), "setColor", Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"));
	static int t;
	static {
		if(colors==null) {
			++t;
			colors=Ref.method(Ref.nmsOrOld("EnumChatFormat", "EnumChatFormat"), "a", int.class);
		}
	}
	
	private static final Object open_url = TheAPI.isNewerThan(16)?Ref.getNulled(Ref.nmsOrOld("network.chat.ChatClickable.ChatClickable$EnumClickAction", "EnumClickAction"), "a"):Ref.getNulled(Ref.nmsOrOld("network.chat.ChatClickable.ChatClickable$EnumClickAction", "EnumClickAction"), "OPEN_URL");
	
	public Object toNMS() {
		boolean first = true;
		Object main = null;
		Object ab = null;
		if(TheAPI.isNewerThan(15)) {
		for(Map<String, Object> s : join) {
			Object a = Ref.newInstance(chat, s.get("text"));
			Object mod = Ref.invoke(a, getChatModif);
			mod=Ref.invoke(mod, setBold, s.getOrDefault("bold",false));
			mod=Ref.invoke(mod, setItalic, s.getOrDefault("italic",false));
			mod=Ref.invoke(mod, setRandom, s.getOrDefault("obfuscated",false));
			mod=Ref.invoke(mod, setStrikethrough, s.getOrDefault("strikethrough",false));
			mod=Ref.invoke(mod, setUnderline, s.getOrDefault("underlined",false));
			if(s.get("color")!=null) {
				if(((String) s.get("color")).startsWith("#")) {
					mod=Ref.invoke(mod, setColorHex, getColorHex((String) s.get("color")));
				}else
					mod=Ref.invoke(mod, setColorNormal, getColorNormal(((String) s.get("color")).toUpperCase()));
			}
			if(s.get("clickEvent")!=null) {
				mod=Ref.invoke(mod, setChatClickable, Ref.newInstance(clickEvent, open_url, s.get("value")));
			}
			a=Ref.invoke(a, setChatModif, mod);
			if(first) {
				first=false;
				main=a;
				ab=main;
			}else
				ab=Ref.invoke(ab, addSibling, a);
		}
		}else {
			for(Map<String, Object> s : join) {
				Object a = Ref.newInstance(chat, (s.get("color")==null?"":""+getColorR(((String)s.get("color")).toUpperCase()))+
						build((boolean)s.getOrDefault("bold",false),(boolean)s.getOrDefault("italic",false),
								(boolean)s.getOrDefault("obfuscated",false),(boolean)s.getOrDefault("strikethrough",false)
								,(boolean)s.getOrDefault("underlined",false))+s.get("text"));
				Object mod = Ref.invoke(a, getChatModif);
				if(s.get("clickEvent")!=null) {
					mod=Ref.invoke(mod, setChatClickable, Ref.newInstance(clickEvent, open_url, s.get("value")));
				}
				a=Ref.invoke(a, setChatModif, mod);
				if(first) {
					first=false;
					main=a;
					ab=main;
				}else
					ab=Ref.invoke(ab, addSibling, a);
			}
		}
		if(first)main = Ref.newInstance(chat, "");
		return main;
	}

	private String build(boolean orDefault, boolean orDefault2, boolean orDefault3, boolean orDefault4, boolean orDefault5) {
		StringBuilder b = new StringBuilder();
		if(orDefault)b.append("§l");
		if(orDefault2)b.append("§o");
		if(orDefault3)b.append("§k");
		if(orDefault4)b.append("§m");
		if(orDefault5)b.append("§n");
		return b.toString();
	}

	private Object getColorHex(String object) {
		if(object.startsWith("#"))return Ref.invokeStatic(hex, object);
		return null;
	}
	
	private Object getColorNormal(String object) {
		if(object.startsWith("#"))return null;
		if(t==0)
			return Ref.invokeStatic(colors, (char)ChatColor.valueOf(object.toUpperCase()).getChar());
		return Ref.invokeStatic(colors, ChatColor.valueOf(object.toUpperCase()).ordinal());
	}
	
	private String getColorR(String object) {
		if(object.startsWith("#"))return null;
		return "§"+ChatColor.valueOf(object.toUpperCase()).getChar();
	}

	public String toLegacy() {
		StringBuilder b = new StringBuilder(join.size()*16);
		for(Map<String, Object> text : join)
			b.append(StringUtils.colorize(getColor("" + text.getOrDefault("color", "")))).append(text.get("text"));
		return b.toString();
	}
	
	String getColor(String color) {
		if(color.trim().isEmpty())return "";
		if(color.startsWith("#"))return color;
		try {
		return ChatColor.valueOf(color.toUpperCase())+"";
		}catch(Exception | NoSuchFieldError err) {
			return "";
		}
	}
	
	public List<Map<String, Object>> get(){
		return new ArrayList<>(join);
	}

	public String getJson() {
		String empty = "{\"text\":\"\"}";
		return join.isEmpty()? empty : JsonWriter.write(join);
	}
	
	private void convert() {
		if(text.equals("")) {
			return;
		}
		List<Object[]> colors = parse();
		for (Object[] objects : colors) {
			if (objects[0] == null || objects[0].toString().replaceAll("[&§][A-Fa-f0-9K-Ok-oRrXx]|#[A-Fa-f0-9]{6}", "").equals(""))
				continue;
			HashMap<String, Object> c = new HashMap<>();
			join.add(c);
			if (objects[0].toString().equals("")) continue;
			c.put("text", objects[0] + "");
			if (objects[1] != null && !objects[1].equals(""))
				c.put("color", objects[1] + "");
			for (int is = 2; is < 7; ++is)
				if (objects[is] != null)
					c.put(is == 2 ? "bold" : (is == 3 ? "italic" : (is == 4 ? "obfuscated" : (is == 5 ? "strikethrough" : "underlined"))), (boolean) objects[is]);
				else
					c.put(is == 2 ? "bold" : (is == 3 ? "italic" : (is == 4 ? "obfuscated" : (is == 5 ? "strikethrough" : "underlined"))), false);
			if (objects[7] != null && !objects[7].equals(""))
				c.put("clickEvent", objects[7]);
		}
	}
	
	public void reset() {
		text="";
		bold = false;
		italic = false;
		obfuscated = false;
		strike = false;
		under = false;
		change = false;
		color=null;
		join.clear();
	}
	
	public void reset(String text) {
		this.text=text;
		bold = false;
		italic = false;
		obfuscated = false;
		strike = false;
		under = false;
		change = false;
		color=null;
		join.clear();
		convert();
	}
	
	//text, color, bold, italic, obfus, strike, under, url
	private List<Object[]> parse() {
		List<Object[]> colors = new ArrayList<>();
		Object[] actual = new Object[8];
		colors.add(actual);
		int hex = 0;
		String val = "", url=null;
		for(char c : text.toCharArray()) {
			val+=c;
			Matcher mmm = ChatMessage.url.matcher(val);
			if(mmm.find() && !val.endsWith(" ")) {
				url=mmm.group();
				continue;
			}else if(url!=null) {
				String[] v = val.split(Pattern.quote(url));
				if(v.length>0 && !v[0].equals("")) {
					actual[0]=v[0];
					if(color!=null && !color.equals(""))
						actual[1]=color;
					if(bold)
						actual[2]= true;
					else actual[2]=null;
					if(italic)
						actual[3]= true;
					else actual[3]=null;
					if(obfuscated)
						actual[4]= true;
					else actual[4]=null;
					if(strike)
						actual[5]= true;
					else actual[5]=null;
					if(under)
						actual[6]= true;
					else actual[6]=null;
					actual=new Object[8];
					colors.add(actual);
				}
				val=v.length>=2?v[1]:"";
				setupUrl(actual, url);
				actual[0]=url;
				if(color!=null && !color.equals(""))
					actual[1]=color;
				if(bold)
					actual[2]= true;
				else actual[2]=null;
				if(italic)
					actual[3]= true;
				else actual[3]=null;
				if(obfuscated)
					actual[4]= true;
				else actual[4]=null;
				if(strike)
					actual[5]= true;
				else actual[5]=null;
				if(under)
					actual[6]= true;
				else actual[6]=null;
				url=null;
				actual=new Object[8];
				colors.add(actual);
				actual[0]=val;
			}
			boolean complete = false;
			String last = getLastColors(val);
			if(last.matches("§[Xx](§[A-Fa-f0-9]){6}")) {
				complete=true;
				hex=0;
				val=val.replaceAll("§[A-Fa-f0-9K-Ok-oRrXx]", "");
			}else {
			if(!last.equals("")) {
				if(!last.toLowerCase().startsWith("§x") && hex == 0) {
				complete=true;
				val=val.replaceAll("§[A-Fa-f0-9K-Ok-oRrXx]", "");
				}else ++hex;
			}}
			actual[0]=val;
			if(!last.equals("") && complete)
			applyChanges(actual, last);
			if(change) {
				change=false;
				if(!val.replaceAll("§[A-Fa-f0-9K-Ok-oRrXx]", "").equals("")) {
					val="";
					actual=new Object[8];
					colors.add(actual);
				}
			}
			if(color!=null && !color.equals(""))
				actual[1]=color;
			else actual[1]=null;
			if(bold)
				actual[2]= true;
			else actual[2]=null;
			if(italic)
				actual[3]= true;
			else actual[3]=null;
			if(obfuscated)
				actual[4]= true;
			else actual[4]=null;
			if(strike)
				actual[5]= true;
			else actual[5]=null;
			if(under)
				actual[6]= true;
			else actual[6]=null;
		}
		if(url!=null) {
			String[] v = val.split(Pattern.quote(url));
			if(v.length>0 && !v[0].equals("")) {
				actual[0]=v[0];
				if(color!=null && !color.equals(""))
					actual[1]=color;
				if(bold)
					actual[2]= true;
				else actual[2]=null;
				if(italic)
					actual[3]= true;
				else actual[3]=null;
				if(obfuscated)
					actual[4]= true;
				else actual[4]=null;
				if(strike)
					actual[5]= true;
				else actual[5]=null;
				if(under)
					actual[6]= true;
				else actual[6]=null;
				actual=new Object[8];
				colors.add(actual);
			}
			val=v.length>=2?v[1]:"";
			setupUrl(actual, url);
			actual[0]=url;
			if(color!=null && !color.equals(""))
				actual[1]=color;
			if(bold)
				actual[2]= true;
			else actual[2]=null;
			if(italic)
				actual[3]= true;
			else actual[3]=null;
			if(obfuscated)
				actual[4]= true;
			else actual[4]=null;
			if(strike)
				actual[5]= true;
			else actual[5]=null;
			if(under)
				actual[6]= true;
			else actual[6]=null;
			url=null;
			actual=new Object[8];
			colors.add(actual);
			actual[0]=val;
		}
		return colors;
	}
	private static final Pattern getLast = Pattern.compile("(§[Xx](§[A-Fa-f0-9]){6}|§[A-Fa-f0-9RrK-Ok-oUuXx])");

	static String getLastColors(String s) {
		Matcher m = getLast.matcher(s);
		StringBuilder colors = new StringBuilder();
		while(m.find()) {
			String last = m.group(1);
			if(last.matches("§[A-Fa-f0-9]|§[Xx](§[A-Fa-f0-9]){6}"))
				colors = new StringBuilder(last);
			else
				colors.append(last);
		}
		return colors.toString();
	}
	
	private void setupUrl(Object[] actual, String url) {
		Map<String, Object> map = new HashMap<>();
		map.put("action", "open_url");
		map.put("value", (url.startsWith("https://")||url.startsWith("http://"))?url:"http://"+url+(url.endsWith("/")?"":"/"));
		actual[7]=map;
	}

	boolean same(Object[] ac, String text) {
		Object[] actual = new Object[6];
		actual[0]=ac[1];
		actual[1]=ac[2];
		actual[2]=ac[3];
		actual[3]=ac[4];
		actual[4]=ac[5];
		actual[5]=ac[6];
		String val = "";
		for(char c : text.toCharArray()) {
			val+=c;
			String last = StringUtils.getLastColors(val);
			if(!last.equals(""))
			val=val.replaceAll("[&§][A-Fa-f0-9K-Ok-oRrXx]|#[A-Fa-f0-9]{6}", "");
			if(!last.equals(""))
			apply(actual, last);
		}
		return actual[0]==null?ac[1]==null: ac[1] == null || actual[0].equals(ac[1])
				&& actual[1] == ac[2]
				&& actual[2] == ac[3]
				&& actual[3] == ac[4]
				&& actual[4] == ac[5]
				&& actual[5] == ac[6];
	}

	private void apply(Object[] actual, String next) {
		if(next.contains("l")) {
			actual[1]=true;
		}else
		if(next.contains("o")) {
			actual[2]=true;
		}else
		if(next.contains("k")) {
			actual[3]=true;
		}else
		if(next.contains("m")) {
			actual[4]=true;
		}else
		if(next.contains("n")) {
			actual[5]=true;
		}else {
			if(!next.contains("r"))
				if(actual[1]==null || !actual[1].equals(getColorName(next))) {
					if(getColorName(next).equals("white"))actual[0]="";
					else
					actual[0]=getColorName(next);
				}
			for(int i = 1; i < 6; ++i)
				actual[i]=false;
		}
	}

	private void applyChanges(Object[] actual, String next) {
		if(next.contains("l")) {
			if(bold)return;
			bold=true;
			change=true;
		}else
		if(next.contains("o")) {
			if(italic)return;
			italic=true;
			change=true;
		}else
		if(next.contains("k")) {
			if(obfuscated)return;
			obfuscated=true;
			change=true;
		}else
		if(next.contains("m")) {
			if(strike)return;
			strike=true;
			change=true;
		}else
		if(next.contains("n")) {
			if(under)return;
			under=true;
			change=true;
		}else {
			if(!next.contains("r"))
				if(color==null || !color.equals(getColorName(next))) {
					color=getColorName(next);
					change=true;
				}
			if(bold) {
				bold=false;
				change=true;
			}
			if(italic) {
				italic=false;
				change=true;
			}
			if(obfuscated) {
				obfuscated=false;
				change=true;
			}
			if(strike) {
				strike=false;
				change=true;
			}
			if(under) {
				under=false;
				change=true;
			}
		}
	}
	
	private String getColorName(String next) {
		if(next.equals(""))return null;
		if(TheAPI.isNewerThan(15)) {
		if(next.startsWith("#")) {
			return next;
		}
		if(next.startsWith("&x")||next.startsWith("§x")) {
			String fixedHex = "[&§][xX][&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])[&§]([A-Fa-f0-9])";
			return next.replaceAll(fixedHex, "#$1$2$3$4$5$6");
		}}
		switch(next.substring(1)) {
		case "0":
			return "black";
		case "1":
			return "dark_blue";
		case "2":
			return "dark_green";
		case "3":
			return "dark_aqua";
		case "4":
			return "dark_red";
		case "5":
			return "dark_purple";
		case "6":
			return "gold";
		case "7":
			return "gray";
		case "8":
			return "dark_gray";
		case "9":
			return "blue";
		case "a":
			return "green";
		case "b":
			return "aqua";
		case "c":
			return "red";
		case "d":
			return "light_purple";
		case "e":
			return "yellow";
		case "f":
			return "white";
		}
		return "white";
	}
}
