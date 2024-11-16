package me.devtec.shared.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import net.kyori.adventure.text.event.HoverEvent.ShowEntity;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
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
		Component comp = this.convert(value);
		if (comp.getText() != null && !comp.getText().isEmpty())
			sub.add(this.convert(value));
		for (net.kyori.adventure.text.Component extra : value.children())
			this.doMagicLoop(sub, extra);
	}

	private Component convert(net.kyori.adventure.text.Component value) {
		Component sub = new Component(value instanceof TextComponent ? ((TextComponent) value).content() : value.toString());
		if (value.color() != null)
			sub.setColor(value.color().asHexString());
		if (value.font() != null)
			sub.setFont(value.font().asString());
		Map<TextDecoration, State> decorations = value.style().decorations();
		sub.setBold(decorations.getOrDefault(TextDecoration.BOLD, State.NOT_SET) == State.TRUE);
		sub.setItalic(decorations.getOrDefault(TextDecoration.ITALIC, State.NOT_SET) == State.TRUE);
		sub.setObfuscated(decorations.getOrDefault(TextDecoration.OBFUSCATED, State.NOT_SET) == State.TRUE);
		sub.setStrikethrough(decorations.getOrDefault(TextDecoration.STRIKETHROUGH, State.NOT_SET) == State.TRUE);
		sub.setUnderlined(decorations.getOrDefault(TextDecoration.UNDERLINED, State.NOT_SET) == State.TRUE);

		if (value.hoverEvent() != null)
			if (value.hoverEvent().action() == Action.SHOW_TEXT || value.hoverEvent().action() == Action.SHOW_ACHIEVEMENT)
				sub.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, toComponent((net.kyori.adventure.text.Component) value.hoverEvent().value())));
			else if (value.hoverEvent().action() == Action.SHOW_ENTITY) {
				ShowEntity show = (ShowEntity) value.hoverEvent().value();
				sub.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new ComponentEntity(show.type().asString(), show.id()).setName(show.name() == null ? null : convert(show.name()))));
			} else if (value.hoverEvent().action() == Action.SHOW_ITEM) {
				ShowItem show = (ShowItem) value.hoverEvent().value();
				sub.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentItem(show.item().asString(), show.count()).setNbt(show.nbt() == null ? null : show.nbt().string())));
			}
		if (value.clickEvent() != null)
			sub.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(value.clickEvent().action().name()), value.clickEvent().value()));
		sub.setInsertion(value.insertion());
		return sub;
	}

	@Override
	public net.kyori.adventure.text.Component fromComponent(Component component) {
		net.kyori.adventure.text.Component base = this.convert(component);
		if (component.getExtra() != null)
			base = this.convertAll(base, component.getExtra());
		return base;
	}

	private net.kyori.adventure.text.Component convertAll(net.kyori.adventure.text.Component base, List<Component> extra2) {
		net.kyori.adventure.text.Component result = base;
		for (Component c : extra2) {
			result = result.append(this.convert(c));
			if (c.getExtra() != null)
				result = this.convertAll(result, c.getExtra());
		}
		return result;
	}

	private net.kyori.adventure.text.Component convert(Component component) {
		TextComponent sub = net.kyori.adventure.text.Component.text(component.getText());
		sub = sub.color(component.getColor() != null ? component.getColor().startsWith("#") ? TextColor.fromHexString(component.getColor()) : NamedTextColor.NAMES.value(component.getColor()) : null);
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
			sub = sub.clickEvent(net.kyori.adventure.text.event.ClickEvent.clickEvent(net.kyori.adventure.text.event.ClickEvent.Action.valueOf(component.getClickEvent().getAction().name()),
					component.getClickEvent().getValue()));
		if (component.getHoverEvent() != null)
			sub = sub.hoverEvent(this.makeHover(component.getHoverEvent()));
		if (component.getInsertion() != null)
			sub = sub.insertion(component.getInsertion());
		return component.getFont() == null ? sub : sub.font(Key.key(component.getFont()));
	}

	@Override
	public net.kyori.adventure.text.Component fromComponent(List<Component> components) {
		if (components.isEmpty())
			return net.kyori.adventure.text.Component.empty();
		net.kyori.adventure.text.Component base = this.fromComponent(components.get(0));
		for (int i = 1; i < components.size(); ++i)
			base = base.append(this.fromComponent(components.get(i)));
		return base;
	}

	private net.kyori.adventure.text.event.HoverEvent<?> makeHover(HoverEvent hoverEvent) {
		switch (hoverEvent.getAction()) {
		case SHOW_ENTITY: {
			ComponentEntity hover = (ComponentEntity) hoverEvent.getValue();
			return net.kyori.adventure.text.event.HoverEvent
					.showEntity(ShowEntity.showEntity(Key.key(hover.getType()), hover.getId(), hover.getName() == null ? null : this.fromComponent(hover.getName())));
		}
		case SHOW_ITEM: {
			ComponentItem hover = (ComponentItem) hoverEvent.getValue();
			return net.kyori.adventure.text.event.HoverEvent
					.showItem(ShowItem.showItem(Key.key(hover.getId()), hover.getCount(), hover.getNbt() == null ? null : BinaryTagHolder.binaryTagHolder(hover.getNbt())));
		}
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
