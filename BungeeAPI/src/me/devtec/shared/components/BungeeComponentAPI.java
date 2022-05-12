package me.devtec.shared.components;

import java.util.ArrayList;
import java.util.List;

import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeComponentAPI<T> implements ComponentTransformer<BaseComponent> {
	@Override
	public Component toComponent(BaseComponent value) {
		Component base = new Component("");
		List<Component> extra = new ArrayList<>();
		if(!value.toPlainText().isEmpty()) {
			extra.add(convert(value));
		}
		if(value.getExtra()!=null)
			for(BaseComponent extras : value.getExtra()) 
				doMagicLoop(extra, extras);
		base.setExtra(extra);
		return base;
	}
	
	private void doMagicLoop(List<Component> sub, BaseComponent value) {
		if(value.getExtra()!=null)
			for(BaseComponent extra : value.getExtra()) {
				sub.add(convert(extra));
			}
	}
	
	private Component convert(BaseComponent value) {
		Component sub = new Component(value.toPlainText());
		if(value.getColor()!=null)
			sub.setColor(value.getColor().getName());
		sub.setFont(value.getFont());
		sub.setBold(value.isBold());
		sub.setItalic(value.isItalic());
		sub.setObfuscated(value.isObfuscated());
		sub.setStrikethrough(value.isStrikethrough());
		sub.setUnderlined(value.isUnderlined());
		if(value.getHoverEvent()!=null)
			sub.setHoverEvent(new me.devtec.shared.components.HoverEvent(me.devtec.shared.components.HoverEvent.Action.valueOf(value.getHoverEvent().getAction().name()), toComponent(value.getHoverEvent().getValue())));
		if(value.getClickEvent()!=null)
			sub.setClickEvent(new me.devtec.shared.components.ClickEvent(me.devtec.shared.components.ClickEvent.Action.valueOf(value.getClickEvent().getAction().name()), value.getClickEvent().getValue()));
		sub.setInsertion(value.getInsertion());
		return sub;
	}

	@Override
	public BaseComponent fromComponent(Component component) {
		TextComponent base = new TextComponent("");
		List<TextComponent> extra = new ArrayList<>();
		extra.add(convert(component));
		if(component.getExtra()!=null)
			convertAll(extra, component.getExtra());
		return base;
	}

	private void convertAll(List<TextComponent> extra, List<Component> extra2) {
		for(Component c : extra2) {
			extra.add(convert(c));
			if(c.getExtra()!=null) {
				convertAll(extra, c.getExtra());
			}
		}
	}

	private TextComponent convert(Component component) {
		TextComponent sub = new TextComponent(component.getText());
		if(Ref.serverType()==ServerType.BUNGEECORD || Ref.isNewerThan(15))
			sub.setColor(ChatColor.of(component.getColor()));
		else
			sub.setColor(ChatColor.valueOf(component.getColor()));
		sub.setBold(component.isBold());
		sub.setItalic(component.isItalic());
		sub.setObfuscated(component.isObfuscated());
		sub.setUnderlined(component.isUnderlined());
		sub.setStrikethrough(component.isStrikethrough());
		if(component.getClickEvent()!=null)
			sub.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(component.getClickEvent().getAction().name()), component.getClickEvent().getValue()));
		if(component.getHoverEvent()!=null)
			sub.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(component.getHoverEvent().getAction().name()), fromComponents(component.getHoverEvent().getValue())));
		if(Ref.serverType()==ServerType.BUNGEECORD || Ref.isNewerThan(8))
			sub.setInsertion(component.getInsertion());
		if(component.getFont() != null && (Ref.serverType()==ServerType.BUNGEECORD || Ref.isNewerThan(15)))
			sub.setFont(component.getFont());
		return sub;
	}

	@Override
	public BaseComponent fromComponent(List<Component> components) {
		TextComponent base = new TextComponent("");
		for(Component component : components) {
			base.addExtra(fromComponent(component));
		}
		return base;
	}
}
