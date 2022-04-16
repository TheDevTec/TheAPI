package me.devtec.shared.components;

import java.util.LinkedHashMap;
import java.util.Map;

public class Component {
	private String text;
	private Component extra;

	//COLOR & FORMATS
	private String color; //#RRGGBB (1.16+) or COLOR_NAME
	private boolean bold; //l
	private boolean italic; //o
	private boolean obfuscated; //k
	private boolean underlined; //n
	private boolean strikethrough; //m
	
	//ADDITIONAL
	private HoverEvent hoverEvent;
	private ClickEvent clickEvent;
	private String font;
	private String insertion;
	
	public Component setText(String value) {
		text = value;
		return this;
	}

	public String getText() {
		return text;
	}
	
	public boolean isBold() {
		return bold;
	}
	
	public boolean isItalic() {
		return italic;
	}
	
	public boolean isObfuscated() {
		return obfuscated;
	}
	
	public boolean isUnderlined() {
		return underlined;
	}
	
	public boolean isStrikethrough() {
		return strikethrough;
	}
	
	public String getFont() {
		return font;
	}
	
	public Component setFont(String font) {
		this.font=font;
		return this;
	}
	
	public HoverEvent getHoverEvent() {
		return hoverEvent;
	}
	
	public Component setHoverEvent(HoverEvent hoverEvent) {
		this.hoverEvent=hoverEvent;
		return this;
	}
	
	public ClickEvent getClickEventt() {
		return clickEvent;
	}
	
	public Component setClickEvent(ClickEvent clickEvent) {
		this.clickEvent=clickEvent;
		return this;
	}
	
	public String getInsertion() {
		return insertion;
	}
	
	public Component setInsertion(String insertion) {
		this.insertion=insertion;
		return this;
	}
	
	public Component getExtra() {
		return extra;
	}
	
	public Component setExtra(Component extra) {
		if(extra != this)
			this.extra=extra;
		return extra;
	}
	
	public String getFormats() {
		StringBuilder builder = new StringBuilder();
		if(bold)builder.append('§').append('l');
		if(italic)builder.append('§').append('o');
		if(obfuscated)builder.append('§').append('k');
		if(underlined)builder.append('§').append('n');
		if(strikethrough)builder.append('§').append('m');
		return builder.toString();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		String colorBefore = null;
		
		//COLOR
		if(color != null) {
			if(color.charAt(0) == '#')
				colorBefore=color;
			else
				colorBefore="§"+colorToChar();
			builder.append(colorBefore);
		}
		
		//FORMATS
		String formatsBefore = getFormats();
		builder.append(formatsBefore);
		
		builder.append(text);
		
		if(extra != null) {
			builder.append(extra.toString(colorBefore, formatsBefore));
		}
		return builder.toString();
	}
	
	//Deeper toString with "anti" copying of colors & formats
	protected String toString(String parentColorBefore, String parentFormatsBefore) {
		StringBuilder builder = new StringBuilder();

		String colorBefore = parentColorBefore;

		//FORMATS
		String formatsBefore = getFormats();
		//COLOR
		if(color != null) {
			if(color.charAt(0) == '#')
				colorBefore=color;
			else
				colorBefore="§"+colorToChar();
			if(!colorBefore.equals(parentColorBefore) || !formatsBefore.equals(parentFormatsBefore))
				builder.append(colorBefore);
		}
		
		//FORMATS
		if(!formatsBefore.equals(parentFormatsBefore))
			builder.append(formatsBefore);
		
		builder.append(text);
		
		if(extra != null) {
			builder.append(extra.toString(colorBefore, formatsBefore));
		}
		return builder.toString();
	}

	public Component setColor(String nameOrHex) {
		color = nameOrHex;
		return this;
	}

	public String getColor() {
		return color;
	}

	public char colorToChar() {
		return colorToChar(color);
	}

	protected static char colorToChar(String color) {
		if(color!=null)
			switch(color) {
			//a - f
			case "green":
				return 97;
			case "aqua":
				return 98;
			case "red":
				return 99;
			case "light_purple":
				return 100;
			case "yellow":
				return 101;
			case "white":
				return 102;
			//0 - 9
			case "black":
				return 48;
			case "dark_blue":
				return 49;
			case "dark_green":
				return 50;
			case "dark_aqua":
				return 51;
			case "dark_red":
				return 52;
			case "dark_purple":
				return 53;
			case "gold":
				return 54;
			case "gray":
				return 55;
			case "dark_gray":
				return 56;
			case "blue":
				return 57;
				default:
					break;
			}
		return 0;
	}

	public Component setColorFromChar(char character) {
		switch(character) {
		//a - f
		case 97:
			color = "green";
			break;
		case 98:
			color = "aqua";
			break;
		case 99:
			color = "red";
			break;
		case 100:
			color = "light_purple";
			break;
		case 101:
			color = "yellow";
			break;
		case 102:
			color = "white";
			break;
		//0 - 9
		case 48:
			color = "black";
			break;
		case 49:
			color = "dark_blue";
			break;
		case 50:
			color = "dark_green";
			break;
		case 51:
			color = "dark_aqua";
			break;
		case 52:
			color = "dark_red";
			break;
		case 53:
			color = "dark_purple";
			break;
		case 54:
			color = "gold";
			break;
		case 55:
			color = "gray";
			break;
		case 56:
			color = "dark_gray";
			break;
		case 57:
			color = "blue";
			break;
			default:
				color = null;
				break;
		}
		return this;
	}

	public Component setFormatFromChar(char character, boolean status) {
		switch(character) {
		case 107:
			obfuscated = status;
			break;
		case 108:
			bold = status;
			break;
		case 109:
			strikethrough = status;
			break;
		case 110:
			underlined = status;
			break;
		case 111:
			bold = status;
			break;
			default: //reset
				bold = false;
				italic = false;
				obfuscated = false;
				underlined = false;
				strikethrough = false;
				break;
		}
		return this;
	}
	
	/**
	 * @apiNote Copy formats & additional settings from {@value selectedComp}
	 * @param selectedComp Component
	 * @return Component
	 */
	public Component copyOf(Component selectedComp) {
		bold = selectedComp.bold;
		italic = selectedComp.italic;
		obfuscated = selectedComp.obfuscated;
		underlined = selectedComp.underlined;
		strikethrough = selectedComp.strikethrough;

		font = selectedComp.font;
		return this;
	}

	public Map<String, Object> toJsonMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		String color = this.color;
		map.put("text", getText());
		if(color!=null) {
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
}
