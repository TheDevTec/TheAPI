package me.devtec.shared.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import me.devtec.shared.json.Json;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

public class AdventureComponentAPI<T> implements ComponentTransformer<net.kyori.adventure.text.Component> {

	@Override
	public Component toComponent(net.kyori.adventure.text.Component value) {
		Component base = this.convert(value);
		List<Component> extra = new ArrayList<>();
		for (net.kyori.adventure.text.Component extras : value.children())
			this.doMagicLoop(extra, extras);
		base.setExtra(extra);
		return base;
	}

	private void doMagicLoop(List<Component> sub, net.kyori.adventure.text.Component value) {
		for (net.kyori.adventure.text.Component extra : value.children())
			sub.add(this.convert(extra));
	}

	private Component convert(net.kyori.adventure.text.Component value) {
		Component sub = new Component(
				value instanceof TextComponent ? ((TextComponent) value).content() : value.toString());
		if (value.color() != null)
			sub.setColor(value.color().asHexString());
		sub.setFont(value.font().asString());
		sub.setBold(value.style().decorations().getOrDefault(TextDecoration.BOLD, State.NOT_SET) == State.TRUE);
		sub.setItalic(value.style().decorations().getOrDefault(TextDecoration.ITALIC, State.NOT_SET) == State.TRUE);
		sub.setObfuscated(
				value.style().decorations().getOrDefault(TextDecoration.OBFUSCATED, State.NOT_SET) == State.TRUE);
		sub.setStrikethrough(
				value.style().decorations().getOrDefault(TextDecoration.STRIKETHROUGH, State.NOT_SET) == State.TRUE);
		sub.setUnderlined(
				value.style().decorations().getOrDefault(TextDecoration.UNDERLINED, State.NOT_SET) == State.TRUE);
		// HOVEREVENT CONVERSION IS UNSUPPORTED
		// Help wanted
		// if(value.hoverEvent()!=null)
		// sub.setHoverEvent(new
		// me.devtec.shared.components.HoverEvent(me.devtec.shared.components.HoverEvent.Action.valueOf(value.hoverEvent().action().name()),
		// value.hoverEvent().value()));
		if (value.clickEvent() != null)
			sub.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(value.clickEvent().action().name()),
					value.clickEvent().value()));
		sub.setInsertion(value.insertion());
		return sub;
	}

	@Override
	public net.kyori.adventure.text.Component fromComponent(Component component) {
		net.kyori.adventure.text.Component base = this.convert(component);
		if (component.getExtra() != null)
			this.convertAll(base, component.getExtra());
		return base;
	}

	private void convertAll(net.kyori.adventure.text.Component base, List<Component> extra2) {
		for (Component c : extra2) {
			base.append(this.convert(c));
			if (c.getExtra() != null)
				this.convertAll(base, c.getExtra());
		}
	}

	private net.kyori.adventure.text.Component convert(Component component) {
		TextComponent sub = net.kyori.adventure.text.Component.text(component.getText());
		sub = sub.color(component.getColor().startsWith("#") ? TextColor.fromHexString(component.getColor())
				: NamedTextColor.NAMES.value(component.getColor()));
		if (component.isBold())
			sub = sub.decorate(TextDecoration.BOLD);
		if (component.isItalic())
			sub = sub.decorate(TextDecoration.ITALIC);
		if (component.isObfuscated())
			sub = sub.decorate(TextDecoration.OBFUSCATED);
		if (component.isUnderlined())
			sub = sub.decorate(TextDecoration.UNDERLINED);
		if (component.isStrikethrough())
			sub = sub.decorate(TextDecoration.STRIKETHROUGH);
		if (component.getClickEvent() != null)
			sub = sub
					.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(
							net.kyori.adventure.text.event.ClickEvent.Action
									.valueOf(component.getClickEvent().getAction().name()),
							component.getClickEvent().getValue()));
		if (component.getHoverEvent() != null)
			sub = sub.hoverEvent(this.makeHover(component.getHoverEvent()));
		sub = sub.insertion(component.getInsertion());
		return sub.font(Key.key(component.getFont()));
	}

	@Override
	public net.kyori.adventure.text.Component fromComponent(List<Component> components) {
		net.kyori.adventure.text.Component base = net.kyori.adventure.text.Component.text("");
		boolean first = true;
		for (Component component : components)
			if (first) {
				first = false;
				base = this.fromComponent(component);
			} else
				base.append(this.fromComponent(component));
		return base;
	}

	@SuppressWarnings("unchecked")
	private net.kyori.adventure.text.event.HoverEvent<?> makeHover(HoverEvent hoverEvent) {
		Map<String, Object> map = (Map<String, Object>) Json.reader().simpleRead(hoverEvent.getValue().getText());
		Map<String, Object> hover = (Map<String, Object>) map.get("hoverEvent");

		Object val = hover.getOrDefault("value", hover.get("contents"));
		if (val instanceof List)
			val = ((List<?>) val).get(0);
		Map<String, Object> value = (Map<String, Object>) val;

		switch (hoverEvent.getAction()) {
		case SHOW_ENTITY:
			return net.kyori.adventure.text.event.HoverEvent.showEntity(
					Key.key("minecraft:" + value.getOrDefault("type", "pig")),
					UUID.fromString(value.getOrDefault("id", UUID.randomUUID().toString()) + ""),
					value.get("name") == null ? null
							: this.fromComponent(ComponentAPI.fromString(value.get("name") + "")));
		case SHOW_ITEM:
			return net.kyori.adventure.text.event.HoverEvent.showItem(
					Key.key("minecraft:" + value.getOrDefault("id", "air")),
					(int) (double) value.getOrDefault("count", 1.0), BinaryTagHolder.binaryTagHolder(
							Json.writer().simpleWrite(value.getOrDefault("tag", new ConcurrentHashMap<>()))));
		case SHOW_TEXT:
			return net.kyori.adventure.text.event.HoverEvent.showText(this.fromComponent(hoverEvent.getValue()));
		default:
			break;
		}
		return null;
	}

	@Override
	public net.kyori.adventure.text.Component[] fromComponents(Component component) {
		return new net.kyori.adventure.text.Component[] { this.fromComponent(component) };
	}

	@Override
	public net.kyori.adventure.text.Component[] fromComponents(List<Component> components) {
		return new net.kyori.adventure.text.Component[] { this.fromComponent(components) };
	}
}
