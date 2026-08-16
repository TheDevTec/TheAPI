package me.devtec.shared.components;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.devtec.shared.Ref;
import me.devtec.shared.utility.ParseUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent.Action;
import net.kyori.adventure.text.event.HoverEvent.ShowEntity;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
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
		Component sub = new Component(
				value instanceof TextComponent ? ((TextComponent) value).content() : value.toString());
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
			if (value.hoverEvent().action() == Action.SHOW_TEXT
			|| value.hoverEvent().action() == Action.SHOW_ACHIEVEMENT)
				sub.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						toComponent((net.kyori.adventure.text.Component) value.hoverEvent().value())));
			else if (value.hoverEvent().action() == Action.SHOW_ENTITY) {
				ShowEntity show = (ShowEntity) value.hoverEvent().value();
				sub.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY,
						new ComponentEntity(show.type().asString(), show.id())
						.setName(show.name() == null ? null : convert(show.name()))));
			} else if (value.hoverEvent().action() == Action.SHOW_ITEM) {
				ShowItem show = (ShowItem) value.hoverEvent().value();
				sub.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
						new ComponentItem(show.item().asString(), show.count())
						.setNbt(show.nbt() == null ? null : show.nbt().string())));
			}
		if (value.clickEvent() != null){
			me.devtec.shared.components.ClickEvent.Action event = ClickEvent.Action.valueOf(value.clickEvent().action().name());
			switch(event){
			case CHANGE_PAGE:
				sub.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE,
						useLegacy ? (String)Ref.invoke(value.clickEvent(), getValue) : Ref.invoke(Ref.invoke(value.clickEvent(), getPayload), getInteger)+""));
				break;
			default:
				sub.setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE,
						useLegacy ? (String)Ref.invoke(value.clickEvent(), getValue) : (String)Ref.invoke(Ref.invoke(value.clickEvent(), getPayload), getTextValue)));
				break;
			}
		}
		sub.setInsertion(value.insertion());
		return sub;
	}

	private static Method getPayload = Ref.method(net.kyori.adventure.text.event.ClickEvent.class, "payload");
	private static Method getValue = Ref.method(net.kyori.adventure.text.event.ClickEvent.class, "value");
	private static boolean useLegacy = getPayload==null;
	private static Method getInteger = Ref.method(Ref.getClass("net.kyori.adventure.text.event.ClickEvent$Payload.Int"), "integer");
	private static Method getTextValue = Ref.method(Ref.getClass("net.kyori.adventure.text.event.ClickEvent$Payload.Text"), "value");
	private static Method createIntegerPayload = Ref.method(Ref.getClass("net.kyori.adventure.text.event.ClickEvent$Payload"), "integer", int.class);
	private static Method createTextPayload = Ref.method(Ref.getClass("net.kyori.adventure.text.event.ClickEvent$Payload"), "string", String.class);
	private static Method createClickEvent = useLegacy ? Ref.method(net.kyori.adventure.text.event.ClickEvent.class, "clickEvent", net.kyori.adventure.text.event.ClickEvent.Action.class, String.class)
			: Ref.method(net.kyori.adventure.text.event.ClickEvent.class, "clickEvent", net.kyori.adventure.text.event.ClickEvent.Action.class, Object.class);

	@Override
	public net.kyori.adventure.text.Component fromComponent(Component component) {
		net.kyori.adventure.text.Component base = this.convert(component);
		if (component.getExtra() != null)
			base = this.convertAll(base, component.getExtra());
		return base;
	}

	private net.kyori.adventure.text.Component convertAll(net.kyori.adventure.text.Component base,
			List<Component> extra2) {
		net.kyori.adventure.text.Component result = base;
		for (Component c : extra2) {
			result = result.append(this.convert(c));
			if (c.getExtra() != null)
				result = this.convertAll(result, c.getExtra());
		}
		return result;
	}

	private net.kyori.adventure.text.Component convert(Component component) {
		Style style = Style.empty();
		if (component.isBold())
			style = style.decorate(TextDecoration.BOLD);
		if (component.isItalic())
			style = style.decorate(TextDecoration.ITALIC);
		if (component.isObfuscated())
			style = style.decorate(TextDecoration.OBFUSCATED);
		if (component.isUnderlined())
			style = style.decorate(TextDecoration.UNDERLINED);
		if (component.isStrikethrough())
			style = style.decorate(TextDecoration.STRIKETHROUGH);
		if (component.getClickEvent() != null)
			switch(component.getClickEvent().getAction()){
			case CHANGE_PAGE:
				style = style
				.clickEvent((net.kyori.adventure.text.event.ClickEvent)Ref.invokeStatic(createClickEvent, net.kyori.adventure.text.event.ClickEvent.Action.CHANGE_PAGE,
						useLegacy ? component.getClickEvent().getValue() : Ref.invokeStatic(createIntegerPayload, ParseUtils.getInt(component.getClickEvent().getValue()))));
				break;
			default:
				style = style
				.clickEvent((net.kyori.adventure.text.event.ClickEvent)Ref.invokeStatic(createClickEvent, net.kyori.adventure.text.event.ClickEvent.Action.NAMES.value(component.getClickEvent().getAction().name()),
						useLegacy ? component.getClickEvent().getValue() : Ref.invokeStatic(createTextPayload, component.getClickEvent().getValue())));
				break;
			}
		if (component.getHoverEvent() != null)
			style = style.hoverEvent(this.makeHover(component.getHoverEvent()));
		if (component.getInsertion() != null)
			style = style.insertion(component.getInsertion());
		if (component.getFont() != null)
			style = style.font(Key.key(component.getFont()));
		style = style.color(component.getColor() != null
				? component.getColor().startsWith("#") ? TextColor.fromHexString(component.getColor())
						: NamedTextColor.NAMES.value(component.getColor().toLowerCase())
						: null);
		return net.kyori.adventure.text.Component.text(component.getText() == null ? "" : component.getText(), style);
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
			return net.kyori.adventure.text.event.HoverEvent.showEntity(ShowEntity.showEntity(Key.key(hover.getType()),
					hover.getId(), hover.getName() == null ? null : this.fromComponent(hover.getName())));
		}
		case SHOW_ITEM: {
			ComponentItem hover = (ComponentItem) hoverEvent.getValue();
			return net.kyori.adventure.text.event.HoverEvent.showItem(ShowItem.showItem(Key.key(hover.getId()),
					hover.getCount(), hover.getNbt() == null ? null : BinaryTagHolder.binaryTagHolder(hover.getNbt())));
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
