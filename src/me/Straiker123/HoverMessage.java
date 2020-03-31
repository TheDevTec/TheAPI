package me.Straiker123;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import me.Straiker123.Utils.Packets;

public class HoverMessage {
	
    public enum ClickAction {
        RUN_COMMAND,
        SUGGEST_COMMAND,
        OPEN_URL
    }
 
    private List<String> extras = new ArrayList<String>();
 
    public HoverMessage(String... text) {
        for(String extra : text)
            addText(extra);
    }
 
    public HoverMessage addText(String text) {
           String regex = "[&§]{1}([a-fA-Fl-oL-O0-9]){1}";
           text = text.replaceAll(regex, "§$1");
           if(!Pattern.compile(regex).matcher(text).find()) {
        	   extras.add("{text:\"" + text + "\"}");
              return this;
           }
           String[] words = text.split(regex);
 
           int index = words[0].length();
           for(String word : words) {
               try {
                   if(index != words[0].length())
                	   extras.add("{text:\"" + word + "\""+","+"§"+text.charAt(index - 1)+"}");
               } catch(Exception e){}
               index += word.length() + 2;
           }
           return this;
    }
 
    public HoverMessage setClickEvent(ClickAction action, String value) {
    	String lastText = extras.get(extras.size() - 1);
        lastText = lastText.substring(0, lastText.length() - 1)
                + ","+"clickEvent:{action:" + action.toString().toLowerCase()
                + ",value:\"" + value + "\"}"+"}";
        extras.remove(extras.size() - 1);
        extras.add(lastText);
        return this;
    }
 
    public HoverMessage setHoverEvent(String value) {
    	String lastText = extras.get(extras.size() - 1);
        lastText = lastText.substring(0, lastText.length() - 1)
                + ","+"hoverEvent:{action:show_text,value:\"" + value + "\"}"+"}";
        extras.remove(extras.size() - 1);
        extras.add(lastText);
        return this;
    }
    
    public String getJson() {
        if(extras.size() <= 1) return extras.size() == 0 ? "{text:\"\"}" : extras.get(0);
        String text = extras.get(0).substring(0, extras.get(0).length() - 1) + ",extra:[";
        extras.remove(0);;
        for (String extra : extras)
            text = text + extra + ",";
        text = text.substring(0, text.length() - 1) + "]}";
        return text;
    }
    
	public void send(Player... s) {
		for(Player p:s)send(p);
	}
    
	public void send(Player s) {
        try {
            Constructor<?> p = Packets.getNMSClass("PacketPlayOutChat").getConstructor(Packets.getNMSClass("IChatBaseComponent"), byte.class);
            Object messageComponent = Packets.getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, toString());
            Object packet = p.newInstance(messageComponent, (byte)1);
            Packets.sendPacket(s, packet);
        } catch (Exception ex) {
            try {
        	Constructor<?> p = Packets.getNMSClass("PacketPlayOutChat").getConstructor(Packets.getNMSClass("IChatBaseComponent"));
            Object messageComponent = Packets.getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, toString());
            Object packet = p.newInstance(messageComponent);
            Packets.sendPacket(s, packet);
            } catch (Exception exs) {}
        }
	}

}
