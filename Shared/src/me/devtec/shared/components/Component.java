package me.devtec.shared.components;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Component {
	private String text;
	private List<Component> extra;

	// COLOR & FORMATS
	private String color; // #RRGGBB (1.16+) or COLOR_NAME
	private boolean bold; // l
	private boolean italic; // o
	private boolean obfuscated; // k
	private boolean underlined; // n
	private boolean strikethrough; // m

	// ADDITIONAL
	private HoverEvent hoverEvent;
	private ClickEvent clickEvent;
	private String font;
	private String insertion;

	public Component() {

	}

	public Component(String text) {
		this.text = text;
	}

	public Component setText(String value) {
		this.text = value;
		return this;
	}

	public String getText() {
		return this.text;
	}

	public boolean isBold() {
		return this.bold;
	}

	public boolean isItalic() {
		return this.italic;
	}

	public boolean isObfuscated() {
		return this.obfuscated;
	}

	public boolean isUnderlined() {
		return this.underlined;
	}

	public boolean isStrikethrough() {
		return this.strikethrough;
	}

	public Component setBold(boolean status) {
		this.bold = status;
		return this;
	}

	public Component setItalic(boolean status) {
		this.italic = status;
		return this;
	}

	public Component setObfuscated(boolean status) {
		this.obfuscated = status;
		return this;
	}

	public Component setUnderlined(boolean status) {
		this.underlined = status;
		return this;
	}

	public Component setStrikethrough(boolean status) {
		this.strikethrough = status;
		return this;
	}

	public String getFont() {
		return this.font;
	}

	public Component setFont(String font) {
		this.font = font;
		return this;
	}

	public HoverEvent getHoverEvent() {
		return this.hoverEvent;
	}

	public Component setHoverEvent(HoverEvent hoverEvent) {
		this.hoverEvent = hoverEvent;
		return this;
	}

	public ClickEvent getClickEvent() {
		return this.clickEvent;
	}

	public Component setClickEvent(ClickEvent clickEvent) {
		this.clickEvent = clickEvent;
		return this;
	}

	public String getInsertion() {
		return this.insertion;
	}

	public Component setInsertion(String insertion) {
		this.insertion = insertion;
		return this;
	}

	public List<Component> getExtra() {
		return this.extra;
	}

	public void setExtra(List<Component> extra) {
		this.extra = extra;
	}

	public String getFormats() {
		StringBuilder builder = new StringBuilder();
		if (this.bold)
			builder.append('§').append('l');
		if (this.italic)
			builder.append('§').append('o');
		if (this.obfuscated)
			builder.append('§').append('k');
		if (this.underlined)
			builder.append('§').append('n');
		if (this.strikethrough)
			builder.append('§').append('m');
		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		String colorBefore = null;

		// COLOR
		if (this.color != null) {
			if (this.color.charAt(0) == '#')
				colorBefore = this.color;
			else
				colorBefore = "§" + this.colorToChar();
			builder.append(colorBefore);
		}

		// FORMATS
		String formatsBefore = this.getFormats();
		builder.append(formatsBefore);

		builder.append(this.text);

		if (this.extra != null)
			for (Component c : this.extra)
				builder.append(c.toString(colorBefore, formatsBefore));
		return builder.toString();
	}

	// Deeper toString with "anti" copying of colors & formats
	protected String toString(String parentColorBefore, String parentFormatsBefore) {
		StringBuilder builder = new StringBuilder();

		String colorBefore = parentColorBefore;

		// FORMATS
		String formatsBefore = this.getFormats();
		// COLOR
		if (this.color != null) {
			if (this.color.charAt(0) == '#')
				colorBefore = this.color;
			else
				colorBefore = "§" + this.colorToChar();
			if (!colorBefore.equals(parentColorBefore) || !formatsBefore.equals(parentFormatsBefore))
				builder.append(colorBefore);
		}

		// FORMATS
		if (!formatsBefore.equals(parentFormatsBefore))
			builder.append(formatsBefore);

		builder.append(this.text);

		if (this.extra != null)
			for (Component c : this.extra)
				builder.append(c.toString(colorBefore, formatsBefore));
		return builder.toString();
	}

	public Component setColor(String nameOrHex) {
		this.color = nameOrHex;
		return this;
	}

	public String getColor() {
		return this.color;
	}

	public char colorToChar() {
		return Component.colorToChar(this.color);
	}

	protected static char colorToChar(String color) {
		if (color != null)
			switch (color) {
			// a - f
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
			// 0 - 9
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
		switch (character) {
		// a - f
		case 97:
			this.color = "green";
			break;
		case 98:
			this.color = "aqua";
			break;
		case 99:
			this.color = "red";
			break;
		case 100:
			this.color = "light_purple";
			break;
		case 101:
			this.color = "yellow";
			break;
		case 102:
			this.color = "white";
			break;
		// 0 - 9
		case 48:
			this.color = "black";
			break;
		case 49:
			this.color = "dark_blue";
			break;
		case 50:
			this.color = "dark_green";
			break;
		case 51:
			this.color = "dark_aqua";
			break;
		case 52:
			this.color = "dark_red";
			break;
		case 53:
			this.color = "dark_purple";
			break;
		case 54:
			this.color = "gold";
			break;
		case 55:
			this.color = "gray";
			break;
		case 56:
			this.color = "dark_gray";
			break;
		case 57:
			this.color = "blue";
			break;
		default:
			this.color = null;
			break;
		}
		return this;
	}

	public Component setFormatFromChar(char character, boolean status) {
		switch (character) {
		case 107:
			this.obfuscated = status;
			break;
		case 108:
			this.bold = status;
			break;
		case 109:
			this.strikethrough = status;
			break;
		case 110:
			this.underlined = status;
			break;
		case 111:
			this.bold = status;
			break;
		default: // reset
			this.bold = false;
			this.italic = false;
			this.obfuscated = false;
			this.underlined = false;
			this.strikethrough = false;
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
		this.bold = selectedComp.bold;
		this.italic = selectedComp.italic;
		this.obfuscated = selectedComp.obfuscated;
		this.underlined = selectedComp.underlined;
		this.strikethrough = selectedComp.strikethrough;
		this.color = selectedComp.color;

		this.font = selectedComp.font;
		return this;
	}

	public Map<String, Object> toJsonMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		String color = this.color;
		map.put("text", this.getText());
		if (color != null)
			map.put("color", color);
		if (this.clickEvent != null)
			map.put("clickEvent", this.clickEvent.toJsonMap());
		if (this.hoverEvent != null)
			map.put("hoverEvent", this.hoverEvent.toJsonMap());
		if (this.font != null)
			map.put("font", this.font);
		if (this.insertion != null)
			map.put("insertion", this.insertion);
		if (this.bold)
			map.put("bold", true);
		if (this.italic)
			map.put("italic", true);
		if (this.strikethrough)
			map.put("strikethrough", true);
		if (this.obfuscated)
			map.put("obfuscated", true);
		if (this.underlined)
			map.put("underlined", true);
		return map;
	}
}
