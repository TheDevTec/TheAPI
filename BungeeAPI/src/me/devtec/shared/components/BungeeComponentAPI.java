package me.devtec.shared.components;

import java.util.List;

import me.devtec.shared.Ref;
import me.devtec.shared.Ref.ServerType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeComponentAPI<T> implements Bungee<BaseComponent> {
	
	public BaseComponent toBaseComponent(Component component) {
		TextComponent base = new TextComponent();
		while(component!=null) {
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
				sub.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(component.getHoverEvent().getAction().name()), toBaseComponents(component.getHoverEvent().getValue())));
			if(Ref.serverType()==ServerType.BUNGEECORD || Ref.isNewerThan(8))
				sub.setInsertion(component.getInsertion());
			if(component.getFont()!=null)
				if(Ref.serverType()==ServerType.BUNGEECORD || Ref.isNewerThan(15))
					sub.setFont(component.getFont());
			base.addExtra(sub);
			component=component.getExtra();
		}
		return base;
	}

	public BaseComponent toBaseComponent(List<Component> components) {
		TextComponent base = new TextComponent();
		for(Component component : components) {
			base.addExtra(toBaseComponent(component));
		}
		return base;
	}
}
