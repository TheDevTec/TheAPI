package me.devtec.theapi.bungee;

import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.components.base.Component;
import me.devtec.shared.messaging.MessengerProvider;
import me.devtec.shared.messaging.TitleTimes;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class BungeeMessengerProvider implements MessengerProvider {

	@Override
	public void send(Object receiver, Component component) {
		if (receiver instanceof CommandSender
				&& (!(receiver instanceof ProxiedPlayer) || ((ProxiedPlayer) receiver).isConnected()))
			((CommandSender) receiver).sendMessage((BaseComponent) ComponentAPI.bungee().fromComponent(component));
	}

	@Override
	public void actionBar(Object receiver, Component component) {
		if (receiver instanceof ProxiedPlayer && ((ProxiedPlayer) receiver).isConnected())
			((ProxiedPlayer) receiver).sendMessage(ChatMessageType.ACTION_BAR,
					(BaseComponent) ComponentAPI.bungee().fromComponent(component));
	}

	@Override
	public void title(Object receiver, Component title, Component subtitle, TitleTimes times) {
		if (receiver instanceof ProxiedPlayer && ((ProxiedPlayer) receiver).isConnected())
			ProxyServer.getInstance().createTitle()
					.title((BaseComponent) ComponentAPI.bungee().fromComponent(title))
					.subTitle((BaseComponent) ComponentAPI.bungee().fromComponent(subtitle))
					.fadeIn(times.fadeIn()).stay(times.stay()).fadeOut(times.fadeOut())
					.send((ProxiedPlayer) receiver);
	}
}
