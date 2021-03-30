package me.devtec.theapi.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.json.Maker;
import me.devtec.theapi.utils.json.Writer;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.nms.NMSAPI.ChatType;
import me.devtec.theapi.utils.reflections.Ref;

public class HoverMessage {

	public enum ClickAction {
		OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD
	}

	public enum HoverAction {
		SHOW_ITEM, SHOW_ACHIEVEMENT, SHOW_ENTITY, SHOW_TEXT
	}

	private Maker texts = new Maker();
	private HashMap<String, Object> maker = new HashMap<>();

	private boolean isSuper;
	
	public HoverMessage() {
		texts.add(maker);
	}

	public HoverMessage(String... text) {
		this();
		for (String extra : text)
			addText(extra);
	}

	@SuppressWarnings("unchecked")
	public HoverMessage(Map<? extends String, ?> json) {
		loadFromMap((Map<String, Object>) json);
	}
	
	public Maker getMaker() {
		return texts;
	}

	@SuppressWarnings("unchecked")
	public void	loadFromMap(Map<String, Object> json) {
		if(json.containsKey("text")) {
			addText(TheAPI.colorize("" + json.get("text")));
		}
		if(json.containsKey("clickEvent")) {
			if (json.get("clickEvent") instanceof Map) {
				Map<String, Object> s = (Map<String, Object>) json.get("clickEvent");
				setClickEvent(ClickAction.valueOf(s.get("action").toString().toUpperCase()), s.containsKey("contets")?s.get("contets"):s.get("value"));
			}
		}
		if(json.containsKey("hoverEvent")) {
			if (json.get("hoverEvent") instanceof Map) {
				Map<String, Object> s = (Map<String, Object>) json.get("hoverEvent");
				setHoverEvent(HoverAction.valueOf(s.get("action").toString().toUpperCase()), s.containsKey("contets")?s.get("contets"):s.get("value"));
			}
		}
		if(json.containsKey("keybind")) {
			setKeybind("" + json.get("keybind"));
		}
		if(json.containsKey("bold")) {
			setBold(StringUtils.getBoolean(json.get("bold")+""));
		}
		if(json.containsKey("italic")) {
			setItalic(StringUtils.getBoolean(json.get("italic")+""));
		}
		if(json.containsKey("underlined")) {
			setUnderlined(StringUtils.getBoolean(json.get("underlined")+""));
		}
		if(json.containsKey("strikethrough")) {
			setStrikethrough(StringUtils.getBoolean(json.get("strikethrough")+""));
		}
		if(json.containsKey("obfuscated")) {
			setObfuscated(StringUtils.getBoolean(json.get("obfuscated")+""));
		}
		if(json.containsKey("insertion")) {
			setInsertion("" + json.get("insertion"));
		}
		if(json.containsKey("color")) {
			setColor("" + json.get("color"));
		}
		if(json.containsKey("extra")) {
			if(json.get("extra") instanceof Map == false)return;
			Map<String, ?> jsons = (Map<String, ?>) json.get("extra");
			while(true) {
				if(jsons==null)break;
				if(jsons.containsKey("text")) {
					addText(TheAPI.colorize("" + jsons.get("text")));
				}
				if(jsons.containsKey("clickEvent")) {
					if (jsons.get("clickEvent") instanceof Map) {
						Map<String, Object> s = (Map<String, Object>) jsons.get("clickEvent");
						setClickEvent(ClickAction.valueOf(s.get("action").toString().toUpperCase()), s.containsKey("contets")?s.get("contets"):s.get("value"));
					}
				}
				if(jsons.containsKey("hoverEvent")) {
					if (jsons.get("hoverEvent") instanceof Map) {
						Map<String, Object> s = (Map<String, Object>) jsons.get("hoverEvent");
						setHoverEvent(HoverAction.valueOf(s.get("action").toString().toUpperCase()), s.containsKey("contets")?s.get("contets"):s.get("value"));
					}
				}
				if(jsons.containsKey("bold")) {
					setBold(StringUtils.getBoolean(jsons.get("bold")+""));
				}
				if(jsons.containsKey("italic")) {
					setItalic(StringUtils.getBoolean(jsons.get("italic")+""));
				}
				if(jsons.containsKey("underlined")) {
					setUnderlined(StringUtils.getBoolean(jsons.get("underlined")+""));
				}
				if(jsons.containsKey("strikethrough")) {
					setStrikethrough(StringUtils.getBoolean(jsons.get("strikethrough")+""));
				}
				if(jsons.containsKey("obfuscated")) {
					setObfuscated(StringUtils.getBoolean(jsons.get("obfuscated")+""));
				}
				if(jsons.containsKey("insertion")) {
					setInsertion("" + jsons.get("insertion"));
				}
				if(jsons.containsKey("keybind")) {
					setKeybind("" + jsons.get("keybind"));
				}
				if(jsons.containsKey("color")) {
					setColor("" + jsons.get("color"));
				}
				if(jsons.containsKey("extra")) {
					if(jsons.get("extra") instanceof Map == false)break;
					jsons = (Map<String, ?>) jsons.get("extra");
					continue;
				}
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public HoverMessage(Collection<?> texts) {
		this();
		for(Object text : texts) {
			if(text instanceof Map == false) {
				addText(text+"");
			}else
			loadFromMap((Map<String, Object>) text);
		}
	}

	public HoverMessage addText(String text) {
		if(text==null)return this;
		if (isSuper) {
			maker = new HashMap<>();
			maker.put("text", text);
			texts.add(maker);
			return this;
		}
		maker.put("text", text);
		isSuper=true;
		return this;
	}

	public HoverMessage setClickEvent(ClickAction action, Object value) {
		if(value==null||action==null)return this;
		HashMap<String, Object> o = new HashMap<>();
		o.put("action", action.name().toLowerCase());
		o.put("value", action==ClickAction.OPEN_URL?((value.toString().startsWith("https://")||value.toString().startsWith("http://"))?value:"http://"+value+(value.toString().endsWith("/")?"":"/")):value);
		maker.put("clickEvent", o);
		return this;
	}

	public HoverMessage setHoverEvent(HoverAction action, Object value) {
		if(value==null||action==null)return this;
		HashMap<String, Object> o = new HashMap<>();
		o.put("action", action.name().toLowerCase());
		o.put("value", value);
		maker.put("hoverEvent", o);
		return this;
	}

	public HoverMessage setKeybind(String value) {
		if(value==null)return this;
		maker.put("keybind", value);
		return this;
	}

	public HoverMessage setBold(boolean value) {
		maker.put("bold", value);
		return this;
	}

	public HoverMessage setObfuscated(boolean value) {
		maker.put("obfuscated", value);
		return this;
	}

	public HoverMessage setItalic(boolean value) {
		maker.put("italic", value);
		return this;
	}

	public HoverMessage setUnderlined(boolean value) {
		maker.put("underlined", value);
		return this;
	}

	public HoverMessage setStrikethrough(boolean value) {
		maker.put("strikethrough", value);
		return this;
	}

	public HoverMessage setInsertion(String value) {
		if(value==null)return this;
		maker.put("insertion", value);
		return this;
	}

	public HoverMessage setColor(ChatColor color) {
		if(color==null)return this;
		return setColor(color.name());
	}

	public HoverMessage setColor(String color) {
		if(color==null)return this;
		maker.put("color", color.toLowerCase());
		return this;
	}

	public HoverMessage setHoverEvent(String value) {
		if(value==null)return this;
		return setHoverEvent(HoverAction.SHOW_TEXT, value);
	}

	public String getJson() {
		return Writer.write(texts);
	}

	public String toString() {
		return getJson();
	}
	
	public String toLegacyText() {
		StringBuilder b = new StringBuilder();
		for(Object text : texts) {
			if(text instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) text;
				b.append(getColor(""+map.getOrDefault("color",""))+map.get("text"));
			}else {
				b.append(text+"");
			}
		}
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

	public void send(Collection<Player> players) {
		Ref.sendPacket(players, NMSAPI.getPacketPlayOutChat(ChatType.SYSTEM, NMSAPI.getIChatBaseComponentJson(getJson())));
	}

	public void send(Player player) {
		Ref.sendPacket(player, NMSAPI.getPacketPlayOutChat(ChatType.SYSTEM, NMSAPI.getIChatBaseComponentJson(getJson())));
	}
}
