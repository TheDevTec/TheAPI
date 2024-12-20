package me.devtec.shared.components;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import me.devtec.shared.components.HoverEvent.Action;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeComponentAPI<T> implements ComponentTransformer<BaseComponent> {
    @Override
    public Component toComponent(BaseComponent value) {
        Component base = new Component("");
        List<Component> extra = new ArrayList<>();
        if (!value.toPlainText().isEmpty()) {
			extra.add(this.convert(value));
		}
        if (value.getExtra() != null) {
			for (BaseComponent extras : value.getExtra()) {
				this.doMagicLoop(extra, extras);
			}
		}
        base.setExtra(extra);
        return base;
    }

    private void doMagicLoop(List<Component> sub, BaseComponent value) {
        if (value.getExtra() != null) {
			for (BaseComponent extra : value.getExtra()) {
				sub.add(this.convert(extra));
			}
		}
    }

    private Component convert(BaseComponent value) {
        Component sub = new Component(value.toPlainText());
        if (value.getColor() != null) {
			sub.setColor(value.getColor().getName().toLowerCase());
		}
        sub.setFont(value.getFont());
        sub.setBold(value.isBold());
        sub.setItalic(value.isItalic());
        sub.setObfuscated(value.isObfuscated());
        sub.setStrikethrough(value.isStrikethrough());
        sub.setUnderlined(value.isUnderlined());
        if (value.getHoverEvent() != null) {
			if (Ref.serverType().isBukkit() && !Ref.isNewerThan(15) || value.getHoverEvent().isLegacy()) {
				switch (Action.valueOf(value.getHoverEvent().getAction().name())) {
                    case SHOW_ENTITY:
                        sub.setHoverEvent(new me.devtec.shared.components.HoverEvent(Action.SHOW_ENTITY, ComponentEntity.fromJson(BaseComponent.toLegacyText(value.getHoverEvent().getValue()))));
                        break;
                    case SHOW_ITEM:
                        sub.setHoverEvent(new me.devtec.shared.components.HoverEvent(Action.SHOW_ITEM, ComponentItem.fromJson(BaseComponent.toLegacyText(value.getHoverEvent().getValue()))));
                        break;
                    case SHOW_TEXT:
                        sub.setHoverEvent(new me.devtec.shared.components.HoverEvent(Action.SHOW_TEXT, toComponent(value.getHoverEvent().getValue())));
                        break;
                }
			} else {
				switch (Action.valueOf(value.getHoverEvent().getAction().name())) {
                    case SHOW_ENTITY:
                        net.md_5.bungee.api.chat.hover.content.Entity entity = (net.md_5.bungee.api.chat.hover.content.Entity) value.getHoverEvent().getContents().get(0);
                        sub.setHoverEvent(new me.devtec.shared.components.HoverEvent(Action.SHOW_ENTITY,
                                new ComponentEntity(entity.getType(), UUID.fromString(entity.getId())).setName(entity.getName() == null ? null : toComponent(entity.getName()))));
                        break;
                    case SHOW_ITEM:
                        net.md_5.bungee.api.chat.hover.content.Item item = (net.md_5.bungee.api.chat.hover.content.Item) value.getHoverEvent().getContents().get(0);
                        sub.setHoverEvent(new me.devtec.shared.components.HoverEvent(Action.SHOW_ITEM,
                                new ComponentItem(item.getId(), item.getCount()).setNbt(item.getTag() == null ? null : item.getTag().getNbt())));
                        break;
                    case SHOW_TEXT:
                        sub.setHoverEvent(new me.devtec.shared.components.HoverEvent(Action.SHOW_TEXT, toComponent(value.getHoverEvent().getValue())));
                        break;
                }
			}
		}
        if (value.getClickEvent() != null) {
			sub.setClickEvent(
                    new me.devtec.shared.components.ClickEvent(me.devtec.shared.components.ClickEvent.Action.valueOf(value.getClickEvent().getAction().name()), value.getClickEvent().getValue()));
		}
        sub.setInsertion(value.getInsertion());
        return sub;
    }

    @Override
    public BaseComponent fromComponent(Component component) {
        TextComponent base = this.convert(component);
        if (component.getExtra() != null) {
			this.convertAll(base, component.getExtra());
		}
        return base;
    }

    private void convertAll(TextComponent base, List<Component> extra2) {
        for (Component c : extra2) {
            base.addExtra(this.convert(c));
            if (c.getExtra() != null) {
				this.convertAll(base, c.getExtra());
			}
        }
    }

    private TextComponent convert(Component component) {
        TextComponent sub = new TextComponent(component.getText());
        if (component.getColor() != null) {
			if (component.getColor().startsWith("#") && (!Ref.serverType().isBukkit() || Ref.isNewerThan(15))) {
				sub.setColor(ChatColor.of(component.getColor()));
			} else {
				sub.setColor(ChatColor.valueOf(component.getColor().toUpperCase()));
			}
		}
        sub.setBold(component.isBold());
        sub.setItalic(component.isItalic());
        sub.setObfuscated(component.isObfuscated());
        sub.setUnderlined(component.isUnderlined());
        sub.setStrikethrough(component.isStrikethrough());
        if (component.getClickEvent() != null) {
			sub.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(component.getClickEvent().getAction().name()), component.getClickEvent().getValue()));
		}
        if (component.getHoverEvent() != null) {
			if (!Ref.serverType().isBukkit() || Ref.isNewerThan(15)) {
				switch (component.getHoverEvent().getAction()) {
                    case SHOW_ENTITY: {
                        ComponentEntity hover = (ComponentEntity) component.getHoverEvent().getValue();
                        sub.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY,
                                new net.md_5.bungee.api.chat.hover.content.Entity(hover.getType(), hover.getId().toString(), hover.getName() == null ? null : convert(hover.getName()))));
                        break;
                    }
                    case SHOW_ITEM: {
                        ComponentItem hover = (ComponentItem) component.getHoverEvent().getValue();
                        sub.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                                new net.md_5.bungee.api.chat.hover.content.Item(hover.getId(), hover.getCount(), hover.getNbt() == null ? null : ItemTag.ofNbt(hover.getNbt()))));
                        break;
                    }
                    default:
                        sub.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(this.fromComponents(component.getHoverEvent().getValue()))));
                        break;

                }
			} else {
				sub.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(component.getHoverEvent().getAction().name()), this.fromComponents(component.getHoverEvent().getValue())));
			}
		}
        if (Ref.serverType() == ServerType.BUNGEECORD || Ref.isNewerThan(8)) {
			sub.setInsertion(component.getInsertion());
		}
        if (component.getFont() != null && (!Ref.serverType().isBukkit() || Ref.isNewerThan(15))) {
			sub.setFont(component.getFont());
		}
        return sub;
    }

    @Override
    public BaseComponent fromComponent(List<Component> components) {
        if (components.isEmpty()) {
			return new TextComponent("");
		}
        BaseComponent base = this.fromComponent(components.get(0));
        for (int i = 1; i < components.size(); ++i) {
			base.addExtra(this.fromComponent(components.get(i)));
		}
        return base;
    }

    @Override
    public BaseComponent[] fromComponents(Component component) {
        return new BaseComponent[]{this.fromComponent(component)};
    }

    @Override
    public BaseComponent[] fromComponents(List<Component> components) {
        return new BaseComponent[]{this.fromComponent(components)};
    }
}
