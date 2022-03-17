package me.devtec.shared.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.devtec.shared.json.Json;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class AdventureComponentAPI<T> implements Adventure<net.kyori.adventure.text.Component> {
	
	public net.kyori.adventure.text.Component toBaseComponent(Component component) {
		TextComponent base = net.kyori.adventure.text.Component.text("");
		while(component!=null) {
			TextComponent sub = net.kyori.adventure.text.Component.text(component.getText());
			sub=sub.color(component.getColor().startsWith("#") ? TextColor.fromHexString(component.getColor()) : NamedTextColor.NAMES.value(component.getColor()));
			if(component.isBold())
				sub=sub.decorate(TextDecoration.BOLD);
			if(component.isItalic())
				sub=sub.decorate(TextDecoration.ITALIC);
			if(component.isObfuscated())
				sub=sub.decorate(TextDecoration.OBFUSCATED);
			if(component.isUnderlined())
				sub=sub.decorate(TextDecoration.UNDERLINED);
			if(component.isStrikethrough())
				sub=sub.decorate(TextDecoration.STRIKETHROUGH);
			if(component.getClickEvent()!=null)
				sub=sub.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.valueOf(component.getClickEvent().getAction().name()), component.getClickEvent().getValue()));
			if(component.getHoverEvent()!=null)
				sub=sub.hoverEvent(makeHover(component.getHoverEvent()));
			sub=sub.insertion(component.getInsertion());
			sub=(TextComponent) sub.font(Key.key(component.getFont()));
			base.append(sub);
			component=component.getExtra();
		}
		return base;
	}

	@SuppressWarnings("unchecked")
	private net.kyori.adventure.text.event.HoverEvent<?> makeHover(HoverEvent hoverEvent) {
		Map<String,Object> map = (Map<String, Object>) Json.reader().simpleRead(hoverEvent.getValue().getText());
		Map<String,Object> hover = (Map<String, Object>) map.get("hoverEvent");

		Object val = hover.getOrDefault("value", hover.get("contents"));
		if(val instanceof List)val=((List<?>)val).get(0);
		Map<String, Object> value =((Map<String,Object>)val);
		
		switch(hoverEvent.getAction()) {
		case SHOW_ACHIEVEMENT:
			return null;
		case SHOW_ENTITY:
			return net.kyori.adventure.text.event.HoverEvent.showEntity(Key.key("minecraft:"+value.getOrDefault("type","zombie")), UUID.fromString(value.getOrDefault("id",UUID.randomUUID().toString())+""), value.get("name")==null?null:toBaseComponent(ComponentAPI.toComponent(value.get("name")+"", true)));
		case SHOW_ITEM:
			return net.kyori.adventure.text.event.HoverEvent.showItem(Key.key("minecraft:"+value.getOrDefault("id","air")), (int)(double)value.getOrDefault("count",1.0), BinaryTagHolder.binaryTagHolder(Json.writer().simpleWrite(value.getOrDefault("tag",new HashMap<>()))));
		case SHOW_TEXT:
			return net.kyori.adventure.text.event.HoverEvent.showText(toBaseComponent(hoverEvent.getValue()));
		}
		return null;
	}

	public net.kyori.adventure.text.Component toBaseComponent(List<Component> components) {
		TextComponent base = net.kyori.adventure.text.Component.text("");
		for(Component component : components) {
			base.append(toBaseComponent(component));
		}
		return base;
	}

	public net.kyori.adventure.text.Component[] toBaseComponents(Component component) {
		return new net.kyori.adventure.text.Component[] {toBaseComponent(component)};
	}

	public net.kyori.adventure.text.Component[] toBaseComponents(List<Component> components) {
		return new net.kyori.adventure.text.Component[] {toBaseComponent(components)};
	}
}
