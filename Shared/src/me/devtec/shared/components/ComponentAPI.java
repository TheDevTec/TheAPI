package me.devtec.shared.components;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import me.devtec.shared.Ref;
import me.devtec.shared.utility.StringUtils;

public class ComponentAPI {
	static Pattern url = Pattern.compile("(w{3}\\\\.|[a-zA-Z0-9+&@#/%?=~_|!:,.;-]+:\\/\\/)?[a-zA-Z0-9+&@#/%?=~_|!:,.;-]+\\w\\.[a-zA-Z0-9+&@#/%?=~_|!:,.;-]{1,}\\w");
	static Bungee<?> bungee;
	static Adventure<?> adventure;
	
	public static void init(Bungee<?> spigot, Adventure<?> adventure) {
		ComponentAPI.bungee=spigot;
		ComponentAPI.adventure=adventure;
	}

	public static Bungee<?> bungee() {
		return bungee;
	}

	public static Adventure<?> adventure() {
		return adventure;
	}
	
	public static String toString(Component input) {
		return input.toString(); //Are you lazy or stupid?
	}
	
	public static Component fromString(String input) {
		return fromString(input, /*Depends on version & software*/ Ref.serverType().isBukkit() && Ref.isNewerThan(15), input.contains("http"));
	}
	
	public static Component fromString(String input, boolean hexMode) {
		return fromString(input, hexMode, input.contains("http"));
	}
	
	public static Component fromString(String input, boolean hexMode, boolean urlMode) {
		Component start = new Component();
		Component current = start;
		
		StringBuilder builder = new StringBuilder();
		char prev = 0;
		
		//REQUIRES hexMode ENABLED
		String hex = null;
		
		for(int i = 0; i < input.length(); ++i) {
			char c = input.charAt(i);
			
			//COLOR or FORMAT
			if(prev == '§') {
				prev = c;
				if(hexMode && c == 'x') {
					builder.deleteCharAt(builder.length()-1); //Remove §
					hex = "#";
					continue;
				}
				//COLOR
				if(c >= 97 && c <= 102 || c >= 48 && c <= 57) { // a-f or 0-9
					if(hex != null) {
						hex += c;
						builder.deleteCharAt(builder.length()-1); //Remove §
						if(hex.length()==7) {
							current.setText(builder.toString()); //Current builder into text
							builder.delete(0, builder.length()); //Clear builder
							current = current.setExtra(new Component()); //Create new component
							current.setColor(hex); //Set current format component to bold
							hex = null; //reset hex
						}
						continue;
					}
					builder.deleteCharAt(builder.length()-1); //Remove §
					current.setText(builder.toString()); //Current builder into text
					if(current.getText().trim().isEmpty()) { //Just space or empty Component.. fast fix
						current.setColor(null);
						current.setFormatFromChar('r', false);
					}
					builder.delete(0, builder.length()); //Clear builder
					current = current.setExtra(new Component()); //Create new component
					current.setColorFromChar(c);
					continue;
				}
				//FORMAT
				if(c >= 107 && c <= 111 || c == 114) {
					hex = null;
					builder.deleteCharAt(builder.length()-1); //Remove §
					current.setText(builder.toString()); //Current builder into text
					builder.delete(0, builder.length()); //Clear builder
					Component before = current;
					current = before.setExtra(new Component().copyOf(before)); //Create new component
					current.setFormatFromChar(c, c != 114); //Set current format to 'true' or reset all
					continue;
				}
				//Is this bug?
			}
			prev = c;
			
			if(urlMode && c == ' ') {
				//URL
				
				String[] split = builder.toString().split(" ");
				
				if(checkHttp(split[split.length-1])) {
					hex = null;
					current.setText(builder.toString().substring(0, builder.toString().length()-split[split.length-1].length())); //Current builder into text
					builder.delete(0, builder.length()); //Clear builder
					Component before = current;
					if(current.getText().trim().isEmpty()) { //replace current)
						current.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, split[split.length-1]));
						current.setText(split[split.length-1]);
					}else {
						current = before.setExtra(new Component().copyOf(before)); //Create new component
						current.setColor(before.getColor());
						current.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, split[split.length-1]));
						current.setText(split[split.length-1]);
					}
					
					current = current.setExtra(new Component().copyOf(before)); //Create new component
					current.setColor(before.getColor());
					builder.append(c);
					continue;
				}
			}
			builder.append(c);
		}
		current.setText(builder.toString());
		if(urlMode) {

			String[] split = builder.toString().split(" ");
			
			if(checkHttp(split[split.length-1])) {
				current.setText(builder.toString().substring(0, builder.toString().length()-split[split.length-1].length())); //Current builder into text
				builder.delete(0, builder.length()); //Clear builder
				Component before = current;
				current = before.setExtra(new Component().copyOf(before)); //Create new component
				current.setColor(before.getColor());
				current.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, split[split.length-1]));
				current.setText(split[split.length-1]);
			}
		}
		return start;
	}
	
	private static boolean checkHttp(String text) {
		return url.matcher(text).find();
	}
	
	public static List<Map<String, Object>> toJsonList(Component c){
		List<Map<String, Object>> i = new LinkedList<>();
		while(c!=null) {
			i.add(c.toJsonMap());
			c=c.getExtra();
		}
		return i;
	}
	
	public static List<Map<String, Object>> toJsonList(String text){
		List<Map<String, Object>> i = new LinkedList<>();
		Component c = fromString(text);
		while(c!=null) {
			i.add(c.toJsonMap());
			c=c.getExtra();
		}
		return i;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> fixJsonList(List<Map<String, Object>> lists) { //usable for ex. chat format
		if(lists==null)return null;
		ListIterator<Map<String, Object>> it = lists.listIterator();
		while(it.hasNext()) {
			Map<String, Object> text = it.next();
			Map<String, Object> hover=(Map<String, Object>) text.get("hoverEvent");
			Map<String, Object> click=(Map<String, Object>) text.get("clickEvent");
			if(hover!=null)
				hover=convertMapValues("hoverEvent",hover);
			if(click!=null)
				click=convertMapValues("clickEvent",click);
			String interact=(String) text.get("insertion");
			boolean remove = false;
			for(Entry<String, Object> s : text.entrySet()) {
				if(s.getKey().equals("color")||s.getKey().equals("insertion"))continue;
				if(s.getValue()instanceof String) {
					Component c = fromString((String) s.getValue(), false);
					if(c.getText()!=null&&!c.getText().isEmpty()||c.getExtra()!=null) {
						try {
							if(!remove) {
							it.remove();
							remove=true;
							}
						}catch(Exception err) {}
						while(c!=null){
							Map<String, Object> d = c.toJsonMap();
							if(!d.containsKey("color") && text.containsKey("color"))
								d.put("color", text.get("color"));
							if(hover!=null && !d.containsKey("hoverEvent"))
								d.put("hoverEvent", hover);
							if(click!=null && !d.containsKey("clickEvent"))
								d.put("clickEvent", click);
							if(interact!=null && !d.containsKey("insertation"))
								d.put("insertion", interact);
							it.add(d);
							c=c.getExtra();
						}
					}

				}else
					if(s.getValue()instanceof Map) //hoverEvent or clickEvent
						text.put(s.getKey(), s.getValue());
					else
						if(s.getValue()instanceof List) //extras
							text.put(s.getKey(), fixJsonList((List<Map<String, Object>>) s.getValue()));
			}
		}
		return lists;
	}
	
	@SuppressWarnings("unchecked")
	public static String listToString(List<?> list) {
		StringBuilder b = new StringBuilder(list.size()*16);
		for(Object text : list)
			if(text instanceof Map)
				b.append(StringUtils.colorize(getColor("" + ((Map<String, Object>) text).getOrDefault("color", "")))).append(((Map<String, Object>)text).get("text"));
			else
				b.append(StringUtils.colorize(text+""));
		return b.toString();
	}
	
	static String getColor(String color) {
		if(color.trim().isEmpty())return "";
		if(color.startsWith("#"))return color;
		try {
		return "§"+Component.colorToChar(color);
		}catch(Exception | NoSuchFieldError err) {
			return "";
		}
	}
	
	private static Map<String, Object> convertMapValues(String key, Map<String, Object> hover) {
		Object val = hover.getOrDefault("value", hover.getOrDefault("content", hover.getOrDefault("contents", null)));
		if(val==null)hover.put("value", "");
		else {
			if(key.equalsIgnoreCase("hoverEvent")) {
				if(val instanceof Collection || val instanceof Map) {
					Object ac = hover.get("action");
					hover.clear();
					hover.put("action", ac);
					hover.put("value", val);
				}else {
					Object ac = hover.get("action");
					hover.clear();
					hover.put("action", ac);
					hover.put("value", toJsonList(val+""));
				}
			}else {
				if(val instanceof Collection || val instanceof Map) {
					Object ac = hover.get("action");
					hover.clear();
					hover.put("action", ac);
					hover.put("value", val);
				}else {
					Object ac = hover.get("action");
					hover.clear();
					hover.put("action", ac);
					hover.put("value", val+"");
				}
			}
		}
		return hover;
	}
}
