package me.devtec.theapi.utils.components;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class Component {
	private Component extra;
	private String text, url, color;
	private boolean bold = false, italic = false, obfuscated = false, strike = false, under = false;
	
	public Component() {
		
	}
	
	public Component(String text) {
		this.text=text;
	}
	
	public Component getExtra() {
		return extra;
	}
	
	public void setExtra(Component extra) {
		this.extra=extra;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text=text;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color=color;
	}
	
	public String toString() {
		return getLegacyColor()+getFormats()+text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLegacyColor() {
		return color==null||color.isEmpty()?"":color.charAt(0)=='#'?color:'§'+color;
	}

	public String getFormats() {
		StringBuilder b = new StringBuilder();
		if(bold)b.append("§l");
		if(under)b.append("§n");
		if(strike)b.append("§m");
		if(italic)b.append("§o");
		if(obfuscated)b.append("§k");
		return b.toString();
	}

	public void resetFormats() {
		bold=false;
		italic=false;
		obfuscated=false;
		strike=false;
		under=false;
	}
	
	public void setBold(boolean status) {
		bold=status;
	}
	
	public void setItalic(boolean status) {
		italic=status;
	}
	
	public void setStrikethrough(boolean status) {
		strike=status;
	}
	
	public void setObfuscated(boolean status) {
		obfuscated=status;
	}
	
	public void setUnderlined(boolean status) {
		under=status;
	}
	
	public boolean isBold() {
		return bold;
	}
	
	public boolean isItalic() {
		return italic;
	}
	
	public boolean isStrikethrough() {
		return strike;
	}
	
	public boolean isObfuscated() {
		return obfuscated;
	}
	
	public boolean isUnderlined() {
		return under;
	}

	public String toJson() {
		String color = this.color;
		if(!color.startsWith("#") && !color.isEmpty())
			color=ChatColor.getByChar(color.charAt(0)).name().toLowerCase();
		String json = "{\"text\":\""+getText()+"\"";
		if(color!=null&&!color.isEmpty())
			json+=",\"color\":\""+color+"\"";
		if(url!=null)
			json+=",\"clickEvent\":{\"action\":\"open_url\",\"value\":\""+url+"\"}";
		if(bold) {
			json+=",\"bold\":true";
		}
		if(italic) {
			json+=",\"italic\":true";
		}
		if(strike) {
			json+=",\"strikethrough\":true";
		}
		if(obfuscated) {
			json+=",\"obfuscated\":true";
		}
		if(under) {
			json+=",\"underlined\":true";
		}
		return json+'}';
	}

	public Map<String, Object> toJsonMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		String color = this.color;
		if(!color.startsWith("#") && !color.isEmpty())
			color=ChatColor.getByChar(color.charAt(0)).name().toLowerCase();
		map.put("text", getText());
		if(color!=null&&!color.isEmpty())
		map.put("color", color);
		if(url!=null) {
			Map<String, String> event = new LinkedHashMap<>();
			event.put("action", "open_url");
			event.put("value", url);
			map.put("clickEvent", event);
		}
		if(bold) {
			map.put("bold", true);
		}
		if(italic) {
			map.put("italic", true);
		}
		if(strike) {
			map.put("strikethrough", true);
		}
		if(obfuscated) {
			map.put("obfuscated", true);
		}
		if(under) {
			map.put("underlined", true);
		}
		return map;
	}
}
