package me.devtec.theapi.velocity;

import java.time.Duration;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import me.devtec.shared.components.ComponentAPI;
import me.devtec.shared.components.base.Component;
import me.devtec.shared.messaging.MessengerProvider;
import me.devtec.shared.messaging.TitleTimes;
import net.kyori.adventure.title.Title;

public final class VelocityMessengerProvider implements MessengerProvider {

	@Override
	public void send(Object receiver, Component component) {
		if (receiver instanceof CommandSource
				&& (!(receiver instanceof Player) || ((Player) receiver).isActive()))
			((CommandSource) receiver).sendMessage(convert(component));
	}

	@Override
	public void actionBar(Object receiver, Component component) {
		if (receiver instanceof Player && ((Player) receiver).isActive())
			((Player) receiver).sendActionBar(convert(component));
	}

	@Override
	public void title(Object receiver, Component title, Component subtitle, TitleTimes times) {
		if (receiver instanceof Player && ((Player) receiver).isActive())
			((Player) receiver).showTitle(Title.title(convert(title), convert(subtitle),
					Title.Times.times(Duration.ofMillis(times.fadeIn() * 50L),
							Duration.ofMillis(times.stay() * 50L), Duration.ofMillis(times.fadeOut() * 50L))));
	}

	private static net.kyori.adventure.text.Component convert(Component component) {
		return (net.kyori.adventure.text.Component) ComponentAPI.adventure().fromComponent(component);
	}
}
