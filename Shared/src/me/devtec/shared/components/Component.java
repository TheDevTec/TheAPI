package me.devtec.shared.components;

import java.util.LinkedHashMap;
import java.util.Map;

public class Component {
	
	private Component extra;
	private String text;
	
	//COLOR & FORMATS
	private String color;
	private boolean bold;
	private boolean italic;
	private boolean obfuscated;
	private boolean strikethrough;
	private boolean underlined;
	
	//ADDITIONAL
	private ClickEvent clickEvent;
	private HoverEvent hoverEvent;
	private String insertion;
	private String font;
	
	public Component() {
		
	}
	
	public Component(String text) {
		this.text=text;
	}
	
	public Component getExtra() {
		return extra;
	}
	
	public Component setExtra(Component extra) {
		this.extra=extra;
		return this;
	}
	
	public String getText() {
		return text;
	}
	
	public Component setText(String text) {
		this.text=text;
		return this;
	}
	
	public String getColor() {
		return color;
	}
	
	public Component setColor(String color) {
		this.color=color;
		return this;
	}
	
	public String getInsertion() {
		return insertion;
	}
	
	public Component setInsertion(String insertion) {
		this.insertion=insertion;
		return this;
	}
	
	public String getFont() {
		return font;
	}
	
	public Component setFont(String font) {
		this.font=font;
		return this;
	}
	
	public String toString() {
		return getLegacyColor()+getFormats()+text;
	}

	public ClickEvent getClickEvent() {
		return clickEvent;
	}

	public Component setClickEvent(ClickEvent event) {
		clickEvent = event;
		return this;
	}

	public HoverEvent getHoverEvent() {
		return hoverEvent;
	}

	public Component setHoverEvent(HoverEvent event) {
		hoverEvent = event;
		return this;
	}

	public String getLegacyColor() {
		return color==null||color.isEmpty()?"":color.charAt(0)=='#'?color:'§'+color;
	}

	public String getFormats() {
		StringBuilder b = new StringBuilder();
		if(bold)b.append("§l");
		if(underlined)b.append("§n");
		if(strikethrough)b.append("§m");
		if(italic)b.append("§o");
		if(obfuscated)b.append("§k");
		return b.toString();
	}

	public Component resetFormats() {
		bold=false;
		italic=false;
		obfuscated=false;
		strikethrough=false;
		underlined=false;
		return this;
	}
	
	public Component setBold(boolean status) {
		bold=status;
		return this;
	}
	
	public Component setItalic(boolean status) {
		italic=status;
		return this;
	}
	
	public Component setStrikethrough(boolean status) {
		strikethrough=status;
		return this;
	}
	
	public Component setObfuscated(boolean status) {
		obfuscated=status;
		return this;
	}
	
	public Component setUnderlined(boolean status) {
		underlined=status;
		return this;
	}
	
	public boolean isBold() {
		return bold;
	}
	
	public boolean isItalic() {
		return italic;
	}
	
	public boolean isStrikethrough() {
		return strikethrough;
	}
	
	public boolean isObfuscated() {
		return obfuscated;
	}
	
	public boolean isUnderlined() {
		return underlined;
	}

	public String toJson() {
		String color = this.color;
		String json = "{\"text\":\""+getText()+"\"";
		if(color!=null&&!color.isEmpty()) {
			if(!color.startsWith("#"))
				color=colorName(color.charAt(0));
			json+=",\"color\":\""+color+"\"";
		}
		if(clickEvent!=null)
			json+=",\"clickEvent\":"+clickEvent.toJson();
		if(hoverEvent!=null)
			json+=",\"hoverEvent\":"+hoverEvent.toJson();
		if(font!=null) {
			json+=",\"font\":\""+font+"\"";
		}
		if(insertion!=null) {
			json+=",\"insertion\":\""+insertion+"\"";
		}
		if(bold) {
			json+=",\"bold\":true";
		}
		if(italic) {
			json+=",\"italic\":true";
		}
		if(strikethrough) {
			json+=",\"strikethrough\":true";
		}
		if(obfuscated) {
			json+=",\"obfuscated\":true";
		}
		if(underlined) {
			json+=",\"underlined\":true";
		}
		return json+'}';
	}

	public Map<String, Object> toJsonMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		String color = this.color;
		map.put("text", getText());
		if(color!=null&&!color.isEmpty()) {
			if(!color.startsWith("#"))
				color=colorName(color.charAt(0));
			map.put("color", color);
		}
		if(clickEvent!=null) {
			map.put("clickEvent", clickEvent.toJsonMap());
		}
		if(hoverEvent!=null) {
			map.put("hoverEvent", hoverEvent.toJsonMap());
		}
		if(font!=null) {
			map.put("font", font);
		}
		if(insertion!=null) {
			map.put("insertion", insertion);
		}
		if(bold) {
			map.put("bold", true);
		}
		if(italic) {
			map.put("italic", true);
		}
		if(strikethrough) {
			map.put("strikethrough", true);
		}
		if(obfuscated) {
			map.put("obfuscated", true);
		}
		if(underlined) {
			map.put("underlined", true);
		}
		return map;
	}

	private String colorName(char charAt) {
		switch(Character.toLowerCase(charAt)) {
		case '0':
			return "black";
		case '1':
			return "dark_blue";
		case '2':
			return "dark_green";
		case '3':
			return "dark_aqua";
		case '4':
			return "dark_red";
		case '5':
			return "dark_purple";
		case '6':
			return "gold";
		case '7':
			return "gray";
		case '8':
			return "dark_gray";
		case '9':
			return "blue";
		case 'a':
			return "green";
		case 'b':
			return "aqua";
		case 'c':
			return "red";
		case 'd':
			return "light_purple";
		case 'e':
			return "yellow";
		}
		return "white";
	}
}
