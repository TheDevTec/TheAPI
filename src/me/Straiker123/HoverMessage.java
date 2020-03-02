package me.Straiker123;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import me.Straiker123.Utils.Packets;

public class HoverMessage {
	public HoverMessage(String message) {
		s=message;
	}
	
	public static enum ClickAction{
		SUGGEST_TEXT("suggest_command"),
		RUN_COMMAND("run_command"),
		OPEN_URL("open_url");
		private String real;
		ClickAction(String name) {
			real=name;
		}
		public String getName() {
			return real;
		}
	}
	public static enum HoverType {
		SHOW_ITEM("show_item"),
		SHOW_TEXT("show_text"),
		SHOW_ENTITY("show_entity"),
		SHOW_ACHIEVEMENT("show_archievement"),
		SHOW_STATISTIC("show_statistic");
		private String real;
		HoverType(String name) {
			real=name;
		}
		public String getName() {
			return real;
		}
	}
	HoverType sel;
	ClickAction c;
	String h,d,s;
	public void setHover(HoverType type, String hover) {
		sel=type;
		h=TheAPI.colorize(hover);
	}

	public void setClick(ClickAction type, String value) {
		c=type;
		d=TheAPI.colorize(value);
	}
	
	public String getJson() {
		String a = "{\"text\":\"" + s + "\"}";
    
    if (c==null && sel != null) {
    	
        if (sel == HoverType.SHOW_ACHIEVEMENT) {
            a = "{\"text\":\"" + s + "\",\"hoverEvent\":{\"action\":\"" + sel.getName() + "\",\"value\":\"achievement." + h + "\"}}";
        } else if (sel == HoverType.SHOW_STATISTIC) {
            a = "{\"text\":\"" + s + "\",\"hoverEvent\":{\"action\":\"" + sel.getName() + "\",\"value\":\"stat." + h + "\"}}";
        } else {
            a = "{\"text\":\"" + s + "\",\"hoverEvent\":{\"action\":\"" + sel.getName() + "\",\"value\":\"" + h + "\"}}";
        }
    }
    if (c != null && sel != null) {
        a = "{\"text\":\"" + s + "\",\"clickEvent\":{\"action\":\"" + c.getName() + "\",\"value\":\"" + d + "\"},\"hoverEvent\":{\"action\":\"" + sel.getName() + "\",\"value\":\"" + h + "\"}}";
    }
    if (c != null && sel ==null) {
        a = "{\"text\":\"" + s + "\",\"clickEvent\":{\"action\":\"" + sel.getName() + "\",\"value\":\"" + d + "\"}}";
    }
		return a;
	}
	
	
	public void send(Player s) {
        try {
            Constructor<?> p = Packets.getNMSClass("PacketPlayOutChat").getConstructor(Packets.getNMSClass("IChatBaseComponent"), byte.class);
            Object messageComponent = Packets.getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, getJson());
            Object packet = p.newInstance(messageComponent, (byte)1);
            Packets.sendPacket(s, packet);
        } catch (Exception ex) {
            try {
        	Constructor<?> p = Packets.getNMSClass("PacketPlayOutChat").getConstructor(Packets.getNMSClass("IChatBaseComponent"));
            Object messageComponent = Packets.getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, getJson());
            Object packet = p.newInstance(messageComponent);
            Packets.sendPacket(s, packet);
            } catch (Exception exs) {
            	
            }
        }
	}

}
