package me.DevTec.TheAPI.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.DataKeeper.Maps.UnsortedMap;
import me.DevTec.TheAPI.Utils.Json.Maker;
import me.DevTec.TheAPI.Utils.Json.Maker.MakerObject;
import me.DevTec.TheAPI.Utils.Json.Writer;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI.ChatType;
import me.DevTec.TheAPI.Utils.Reflections.Ref;

public class HoverMessage {

	public enum ClickAction {
		OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD
	}

	public enum HoverAction {
		SHOW_ITEM, SHOW_ACHIEVEMENT, SHOW_ENTITY, SHOW_TEXT
	}
	
	private Maker extras = new Maker();
	private MakerObject maker = new MakerObject(), extra;
	
	private boolean isSuper;
    private Object hover, click;
    private String color;
    private HoverAction hoverAction;
    private ClickAction clickAction;

	public HoverMessage() {
		
	} 
	
	HoverMessage(String... text) {
		for (String extra : text)
			addText(extra);
	}

	public HoverMessage(Map<? extends String, ?> json) {
		for(Entry<? extends String, ?> e : json.entrySet()) {
			if(e.getKey().equalsIgnoreCase("text")) {
				addText(TheAPI.colorize(""+e.getValue()));
				continue;
			}
			if(e.getKey().equalsIgnoreCase("clickEvent")) {
				if(e.getValue() instanceof Map || e.getValue() instanceof UnsortedMap) {
				Map<?,?> s = (Map<?,?>)e.getValue();
				setClickEvent(ClickAction.valueOf(s.get("action").toString().toUpperCase()), s.get("value"));
				}
				continue;
			}
			if(e.getKey().equalsIgnoreCase("hoverEvent")) {
				if(e.getValue() instanceof Map || e.getValue() instanceof UnsortedMap) {
				Map<?,?> s = (Map<?,?>)e.getValue();
				setHoverEvent(HoverAction.valueOf(s.get("action").toString().toUpperCase()), s.get("value"));
				}
				continue;
			}
			if(e.getKey().equalsIgnoreCase("color")) {
				setColor(""+e.getValue());
				continue;
			}
		}
	}
	
	public HoverMessage addText(String text) {
		if(hover != null && hoverAction != null || click != null && clickAction != null) {
			if(hover != null && hoverAction!=null) {
				HashMap<String, Object> o = new HashMap<>();
				o.put("action", hoverAction.name());
				o.put("value", hover);
				(isSuper?extra:maker).add("hoverEvent", o);
			}
			if(click != null && clickAction!=null) {
				HashMap<String, Object> o = new HashMap<>();
				o.put("action", clickAction.name());
				o.put("value", click);
				(isSuper?extra:maker).add("clickEvent", o);
			}
		}
		if(color!=null)
			(isSuper?extra:maker).add("color", color);
		if(maker.containsKey("text")) {
			isSuper=true;
			if(extra!=null)
			extras.add(extra);
			extra=new MakerObject();
			extra.add("text", text);
		}else
		maker.add("text", text);
		hover=null;
		click=null;
		hoverAction=null;
		clickAction=null;
		return this;
	}

	public HoverMessage setClickEvent(ClickAction action, Object value) {
		this.clickAction=action;
		this.click=value;
		return this;
	}

	public HoverMessage setHoverEvent(HoverAction action, Object value) {
		this.hoverAction=action;
		this.hover=value;
		return this;
	}

	public HoverMessage setColor(ChatColor color) {
		return setColor(color.name());
	}

	public HoverMessage setColor(String color) {
		this.color=color;
		return this;
	}

	public HoverMessage setHoverEvent(String value) {
		return setHoverEvent(HoverAction.SHOW_TEXT, value);
	}

	public String getJson() {
		UnsortedMap<Object, Object> copyOfMaker = new UnsortedMap<>(maker);
		UnsortedMap<Object, Object> copyOfExtra = extra!=null?new UnsortedMap<>(extra):null;
		Maker copyExtras = new Maker(extras);
		if(hover != null && hoverAction != null || click != null && clickAction != null) {
			if(hover != null && hoverAction!=null) {
				HashMap<String, Object> o = new HashMap<>();
				o.put("action", hoverAction.name().toLowerCase());
				o.put("value", hover);
				(isSuper?copyOfExtra:copyOfMaker).put("hoverEvent", o);
			}
			if(click != null && clickAction!=null) {
				HashMap<String, Object> o = new HashMap<>();
				o.put("action", clickAction.name().toLowerCase());
				o.put("value", click);
				(isSuper?copyOfExtra:copyOfMaker).put("clickEvent", o);
			}
		}
		if(color!=null)
			(isSuper?copyOfExtra:copyOfMaker).put("color", color);
		if(copyOfExtra!=null && !copyOfExtra.isEmpty())
		copyExtras.add(copyOfExtra);
		if(!extras.isEmpty())
		copyOfMaker.put("extra", extras);
		return Writer.write(copyOfMaker);
	}
	
	public String toString() {
		return getJson();
	}

	public void send(Collection<Player> players) {
		for (Player p : players)
			send(p);
	}

	public void send(Player player) {
		Ref.sendPacket(player, NMSAPI.getPacketPlayOutChat(ChatType.SYSTEM, NMSAPI.getIChatBaseComponentJson(getJson())));
	}
}
