package me.devtec.theapi.utils.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.theapiutils.LoaderClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ComponentAPI {
	static Pattern url = Pattern.compile("(w{3}\\\\.|[a-zA-Z0-9+&@#/%?=~_|!:,.;-]+:\\/\\/)?[a-zA-Z0-9+&@#/%?=~_|!:,.;-]+\\w\\.[a-zA-Z0-9+&@#/%?=~_|!:,.;-]{1,}\\w");
	
	public static Object toIChatBaseComponent(String text, boolean ignoreUrls) {
		if(text==null)return null;
		return LoaderClass.nmsProvider.toIChatBaseComponent(toComponent(text, ignoreUrls));
	}
	
	public static Object toIChatBaseComponent(Component component) {
		if(component==null)return null;
		return LoaderClass.nmsProvider.toIChatBaseComponent(component);
	}
	
	public static Object toIChatBaseComponent(List<Component> components) {
		if(components==null)return null;
		return LoaderClass.nmsProvider.toIChatBaseComponent(components);
	}
	
	public static Object toIChatBaseComponents(Component component) {
		if(component==null)return null;
		return LoaderClass.nmsProvider.toIChatBaseComponents(component);
	}
	
	public static Object toIChatBaseComponents(List<Component> components) {
		if(components==null)return null;
		return LoaderClass.nmsProvider.toIChatBaseComponents(components);
	}

	public static String fromIChatBaseComponent(Object component) {
		if(component==null)return null;
		return LoaderClass.nmsProvider.fromIChatBaseComponent(component);
	}
	
	public static BaseComponent toBaseComponent(Component c) {
		if(c==null)return null;
		TextComponent main = new TextComponent(), current = main;
		while(c!=null) {
			if(main!=current) {
				TextComponent next = new TextComponent(c.getText());
				current.addExtra(next);
				current=next;
			}else {
				current.setText(c.getText());
			}
			if(c.getColor()!=null && !c.getColor().isEmpty()) {
				if(c.getColor().startsWith("#"))
					current.setColor(ChatColor.of(c.getColor()));
				else
					current.setColor(ChatColor.getByChar(c.getColor().charAt(0)));
			}
			if(c.getClickEvent()!=null)
				current.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				current.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(c.getHoverEvent().getAction().name()), toBaseComponents(c.getHoverEvent().getValue())));
			current.setBold(c.isBold());
			current.setItalic(c.isItalic());
			current.setObfuscated(c.isObfuscated());
			current.setUnderlined(c.isUnderlined());
			current.setStrikethrough(c.isStrikethrough());
			c=c.getExtra();
		}
		return main;
	}
	
	public static BaseComponent toBaseComponent(List<Component> cc) {
		if(cc==null)return null;
		TextComponent main = new TextComponent(), current = main;
		for(Component c : cc) {
			if(main!=current) {
				TextComponent next = new TextComponent(c.getText());
				current.addExtra(next);
				current=next;
			}else {
				current.setText(c.getText());
			}
			if(c.getColor()!=null && !c.getColor().isEmpty()) {
				if(c.getColor().startsWith("#"))
					current.setColor(ChatColor.of(c.getColor()));
				else
					current.setColor(ChatColor.getByChar(c.getColor().charAt(0)));
			}
			if(c.getClickEvent()!=null)
				current.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				current.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(c.getHoverEvent().getAction().name()), toBaseComponents(c.getHoverEvent().getValue())));
			current.setBold(c.isBold());
			current.setItalic(c.isItalic());
			current.setObfuscated(c.isObfuscated());
			current.setUnderlined(c.isUnderlined());
			current.setStrikethrough(c.isStrikethrough());
		}
		return main;
	}
	
	public static BaseComponent[] toBaseComponents(Component c) {
		if(c==null)return null;
		List<TextComponent> chat = new ArrayList<>();
		while(c!=null) {
			TextComponent current = new TextComponent(c.getText());
			if(c.getColor()!=null && !c.getColor().isEmpty()) {
				if(c.getColor().startsWith("#"))
					current.setColor(ChatColor.of(c.getColor()));
				else
					current.setColor(ChatColor.getByChar(c.getColor().charAt(0)));
			}
			if(c.getClickEvent()!=null)
				current.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				current.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(c.getHoverEvent().getAction().name()), toBaseComponents(c.getHoverEvent().getValue())));
			current.setBold(c.isBold());
			current.setItalic(c.isItalic());
			current.setObfuscated(c.isObfuscated());
			current.setUnderlined(c.isUnderlined());
			current.setStrikethrough(c.isStrikethrough());
			chat.add(current);
			c=c.getExtra();
		}
		return chat.toArray(new TextComponent[0]);
	}
	
	public static BaseComponent[] toBaseComponents(List<Component> cc) {
		if(cc==null)return null;
		List<TextComponent> chat = new ArrayList<>();
		for(Component c : cc) {
			TextComponent current = new TextComponent(c.getText());
			if(c.getColor()!=null && !c.getColor().isEmpty()) {
				if(c.getColor().startsWith("#"))
					current.setColor(ChatColor.of(c.getColor()));
				else
					current.setColor(ChatColor.getByChar(c.getColor().charAt(0)));
			}
			if(c.getClickEvent()!=null)
				current.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(c.getClickEvent().getAction().name()), c.getClickEvent().getValue()));
			if(c.getHoverEvent()!=null)
				current.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(c.getHoverEvent().getAction().name()), toBaseComponents(c.getHoverEvent().getValue())));
			current.setBold(c.isBold());
			current.setItalic(c.isItalic());
			current.setObfuscated(c.isObfuscated());
			current.setUnderlined(c.isUnderlined());
			current.setStrikethrough(c.isStrikethrough());
			chat.add(current);
		}
		return chat.toArray(new TextComponent[0]);
	}
	
	public static List<Component> toComponents(String legacy, boolean ignoreUrls) {
		if(legacy==null)return null;
		Component c = toComponent(legacy,ignoreUrls);
		List<Component> com = new ArrayList<>();
		while(c!=null) {
			com.add(c);
			c=c.getExtra();
		}
		return com;
	}
	
	public static Component toComponent(String legacy, boolean ignoreUrls) {
		if(legacy==null)return null;
		Component start = new Component(), current = start;
		
		StringBuilder builder = new StringBuilder();
		if(ignoreUrls) {
			String color = "";
			boolean wasBeforeColorChar = false, inHexLoop = false;
			for(int i = 0; i < legacy.length(); ++i) {
				char c = legacy.charAt(i);
				if(c=='§') {
					wasBeforeColorChar=true;
					continue;
				}
				if(wasBeforeColorChar) {
					wasBeforeColorChar=false;
					if(c>=48 && c<=57||c>=97 && c<=102||c>=65 && c<=70) {
						if(!inHexLoop||color.length()==7) {
							current.setText(builder.toString());
							current.setColor(color);
							builder.delete(0, builder.length());
							Component next=new Component();
							current.setExtra(next);
							current=next;
							inHexLoop=false;
							color=String.valueOf(c);
							current.setColor(color);
						}else {
							color+=c;
						}
					}else if(c==120||c==88) {
						current.setText(builder.toString());
						current.setColor(color);
						builder.delete(0, builder.length());
						Component next=new Component();
						current.setExtra(next);
						current=next;
						
						inHexLoop=true;
						color="#";
					}else {
						if(c==114||c==82) {
							current.setText(builder.toString());
							builder.delete(0, builder.length());
							Component next=new Component();
							next.setColor(color);
							current.setExtra(next);
							current=next;
							current.resetFormats();
						}else if(c>=107 && c<=111||c>=75&& c<=79) {
							if(builder.length()!=0) {
								current.setText(builder.toString());
								builder.delete(0, builder.length());
								Component next=new Component();
								next.setColor(color);
								next.setBold(current.isBold());
								next.setItalic(current.isItalic());
								next.setStrikethrough(current.isStrikethrough());
								next.setObfuscated(current.isObfuscated());
								next.setUnderlined(current.isUnderlined());
								current.setExtra(next);
								current=next;
							}
							switch(c) {
							case 'k':
							case 'K':
								current.setObfuscated(true);
								break;
							case 'l':
							case 'L':
								current.setBold(true);
								break;
							case 'm':
							case 'M':
								current.setStrikethrough(true);
								break;
							case 'n':
							case 'N':
								current.setUnderlined(true);
								break;
							case 'o':
							case 'O':
								current.setItalic(true);
								break;
							}
						}
					}
					continue;
				}
				builder.append(c);
			}
			if(builder.length()!=0) {
				current.setText(builder.toString());
				current.setColor(color);
			}
			return start;
		}
		String color = "", colorBeforeSpace = null, url = null;
		boolean wasBeforeColorChar = false, inHexLoop = false;
		for(int i = 0; i < legacy.length(); ++i) {
			char c = legacy.charAt(i);
			if(c=='§') {
				if((url=doUrlCheck(builder.toString()))!=null) {
					builder.append('&'); //fix
					wasBeforeColorChar=false;
					continue;
				}
				wasBeforeColorChar=true;
				continue;
			}
			if(wasBeforeColorChar) {
				if(url!=null) {
					builder.append(c);
					url+='c';
					inHexLoop=false;
					continue;
				}
				wasBeforeColorChar=false;
				if(c>=48 && c<=57||c>=97 && c<=102||c>=65 && c<=70) {
					if(!inHexLoop||color.length()==7) {
						current.setText(builder.toString());
						current.setColor(color);
						builder.delete(0, builder.length());
						Component next=new Component();
						current.setExtra(next);
						current=next;
						inHexLoop=false;
						color=String.valueOf(c);
						current.setColor(color);
					}else {
						color+=c;
					}
				}else if(c==120||c==88) {
					current.setText(builder.toString());
					current.setClickEvent(new me.devtec.theapi.utils.components.ClickEvent(me.devtec.theapi.utils.components.ClickEvent.Action.OPEN_URL, url));
					current.setColor(color);
					builder.delete(0, builder.length());
					Component next=new Component();
					current.setExtra(next);
					current=next;
					
					inHexLoop=true;
					color="#";
				}else {
					if(c==114||c==82) {
						current.setText(builder.toString());
						current.setClickEvent(new me.devtec.theapi.utils.components.ClickEvent(me.devtec.theapi.utils.components.ClickEvent.Action.OPEN_URL, url));
						builder.delete(0, builder.length());
						Component next=new Component();
						next.setColor(color);
						current.setExtra(next);
						current=next;
						current.resetFormats();
					}else if(c>=107 && c<=111||c>=75&& c<=79) {
						if(builder.length()!=0) {
							current.setText(builder.toString());
							current.setClickEvent(new me.devtec.theapi.utils.components.ClickEvent(me.devtec.theapi.utils.components.ClickEvent.Action.OPEN_URL, url));
							builder.delete(0, builder.length());
							Component next=new Component();
							next.setColor(color);
							next.setBold(current.isBold());
							next.setItalic(current.isItalic());
							next.setStrikethrough(current.isStrikethrough());
							next.setObfuscated(current.isObfuscated());
							next.setUnderlined(current.isUnderlined());
							current.setExtra(next);
							current=next;
						}
						switch(c) {
						case 'k':
						case 'K':
							current.setObfuscated(true);
							break;
						case 'l':
						case 'L':
							current.setBold(true);
							break;
						case 'm':
						case 'M':
							current.setStrikethrough(true);
							break;
						case 'n':
						case 'N':
							current.setUnderlined(true);
							break;
						case 'o':
						case 'O':
							current.setItalic(true);
							break;
						}
					}
				}
				continue;
			}
			if(c==' ') {
				if((url=doUrlCheck(builder.toString()))!=null) {
					if(url.indexOf(' ')!=-1) {
						String full = url;
						String[] split = url.split(" ");
						full=full.replace(url=split[split.length-1],"");
						current.setText(full);
						current.setColor(colorBeforeSpace);
						Component next=new Component();
						next.setBold(current.isBold());
						next.setItalic(current.isItalic());
						next.setStrikethrough(current.isStrikethrough());
						next.setObfuscated(current.isObfuscated());
						next.setUnderlined(current.isUnderlined());
						current.setExtra(next);
						current=next;
					}
					current.setText(url);
					current.setClickEvent(new me.devtec.theapi.utils.components.ClickEvent(me.devtec.theapi.utils.components.ClickEvent.Action.OPEN_URL, url));
					url=null;
					current.setColor(color);
					builder.delete(0, builder.length());
					Component next=new Component();
					next.setColor(color);
					next.setBold(current.isBold());
					next.setItalic(current.isItalic());
					next.setStrikethrough(current.isStrikethrough());
					next.setObfuscated(current.isObfuscated());
					next.setUnderlined(current.isUnderlined());
					current.setExtra(next);
					current=next;
				}
				colorBeforeSpace=color;
			}
			builder.append(c);
		}
		if(builder.length()!=0) {
			if((url=doUrlCheck(builder.toString()))!=null) {
				if(url.indexOf(' ')!=-1) {
					String full = url;
					String[] split = url.split(" ");
					full=full.replace(url=split[split.length-1],"");
					current.setText(full);
					current.setColor(colorBeforeSpace);
					Component next=new Component();
					next.setBold(current.isBold());
					next.setItalic(current.isItalic());
					next.setStrikethrough(current.isStrikethrough());
					next.setObfuscated(current.isObfuscated());
					next.setUnderlined(current.isUnderlined());
					current.setExtra(next);
					current=next;
				}
				current.setText(url);
				current.setClickEvent(new me.devtec.theapi.utils.components.ClickEvent(me.devtec.theapi.utils.components.ClickEvent.Action.OPEN_URL, url));
				current.setColor(color);
			}else {
				current.setText(builder.toString());
				current.setColor(color);
			}
		}
		return start;
	}
	private static String doUrlCheck(String string) {
		return url.matcher(string).find()?string:null;
	}

	public static String toString(Component c) {
		if(c==null)return null;
		StringBuilder builder = new StringBuilder();
		String color = null, formats = null;
		while(c!=null) {
			if(color!=null && formats!=null && (color+formats).equals(c.getLegacyColor()+c.getFormats()))
				builder.append(c.getText());
			else
				builder.append(c.toString());
			color=c.getLegacyColor();
			formats=c.getFormats();
			c=c.getExtra();
		}
		return builder.toString();
	}

	public static String toString(List<Component> cc) {
		if(cc==null)return null;
		StringBuilder builder = new StringBuilder();
		String before = null;
		for(Component c : cc) {
			if(before!=null && before.equals(c.getLegacyColor()+c.getFormats()))
				builder.append(c.getText());
			else
				builder.append(c.toString());
			before=c.getLegacyColor()+c.getFormats();
			c=c.getExtra();
		}
		return builder.toString();
	}
	
	public static List<Map<String, Object>> toJsonList(Component c){
		List<Map<String, Object>> i = new LinkedList<>();
		while(c!=null) {
			i.add(c.toJsonMap());
			c=c.getExtra();
		}
		return i;
	}
	
	public static List<Map<String, Object>> toJsonList(List<Component> cc){
		List<Map<String, Object>> i = new LinkedList<>();
		for(Component c : cc) {
			i.add(c.toJsonMap());
		}
		return i;
	}
	
	public static List<Map<String, Object>> toJsonList(String text, boolean ignoreUrls){
		List<Map<String, Object>> i = new LinkedList<>();
		Component c = ComponentAPI.toComponent(text, ignoreUrls);
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
			Map<String, Object> hover=(Map<String, Object>) text.get("hoverEvent"), click=(Map<String, Object>) text.get("clickEvent");
			if(hover!=null)
				hover=ff("hoverEvent",hover);
			if(click!=null)
				click=ff("clickEvent",click);
			String interact=(String) text.get("insertion");
			boolean remove = false;
			for(Entry<String, Object> s : text.entrySet()) {
				if(s.getKey().equals("color")||s.getKey().equals("insertion"))continue;
				if(s.getValue()instanceof String) {
					Component c = ComponentAPI.toComponent((String) s.getValue(), false);
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
	public static String toStringJson(List<Object> list) {
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
		return ChatColor.valueOf(color.toUpperCase())+"";
		}catch(Exception | NoSuchFieldError err) {
			return "";
		}
	}
	
	private static Map<String, Object> ff(String key, Map<String, Object> hover) {
		Object val = hover.getOrDefault("value", hover.getOrDefault("content", hover.getOrDefault("contents", null)));
		if(val==null)hover.put("value", "");
		else {
			if(key.equalsIgnoreCase("hoverEvent")) {
				if(val instanceof Collection || val instanceof Map) {
					Object ac = hover.get("action");
					hover.clear();
					hover.put("action", ac);
					hover.put("value", ac);
				}else {
					Object ac = hover.get("action");
					hover.clear();
					hover.put("action", ac);
					hover.put("value", toJsonList(val+"",true));
				}
			}else {
				if(val instanceof Collection || val instanceof Map) {
					Object ac = hover.get("action");
					hover.clear();
					hover.put("action", ac);
					hover.put("value", ac);
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
