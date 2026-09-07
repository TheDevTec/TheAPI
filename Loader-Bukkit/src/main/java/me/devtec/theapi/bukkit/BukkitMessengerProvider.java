package me.devtec.theapi.bukkit;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.devtec.shared.components.base.Component;
import me.devtec.shared.messaging.MessengerProvider;
import me.devtec.shared.messaging.TitleTimes;
import me.devtec.theapi.bukkit.nms.NmsProvider.ChatType;
import me.devtec.theapi.bukkit.nms.NmsProvider.TitleAction;

public final class BukkitMessengerProvider implements MessengerProvider {

	@Override
	public void send(Object receiver, Component component) {
		if (!(receiver instanceof CommandSender))
			return;

		CommandSender sender = (CommandSender) receiver;

		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (!player.isOnline())
				return;

			Object nmsComponent = BukkitLoader.getNmsProvider().toIChatBaseComponent(component);

			Object packet = BukkitLoader.getNmsProvider().packetChat(ChatType.SYSTEM, nmsComponent);

			BukkitLoader.getPacketHandler().send(player, packet);
			return;
		}

		sender.sendMessage(component.toString());
	}

	@Override
	public void send(Collection<?> receivers, Component component) {
		Object packet = null;
		String plain = null;

		for (Object receiver : receivers) {
			if (!(receiver instanceof CommandSender))
				continue;

			CommandSender sender = (CommandSender) receiver;

			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (!player.isOnline())
					continue;

				if (packet == null) {
					Object nmsComponent = BukkitLoader.getNmsProvider().toIChatBaseComponent(component);

					packet = BukkitLoader.getNmsProvider().packetChat(ChatType.SYSTEM, nmsComponent);
				}

				BukkitLoader.getPacketHandler().send(player, packet);
				continue;
			}

			if (plain == null)
				plain = component.toString();

			sender.sendMessage(plain);
		}
	}

	@Override
	public void actionBar(Object receiver, Component component) {
		if (receiver instanceof Player && ((Player) receiver).isOnline())
			BukkitLoader.getPacketHandler().send((Player) receiver,
					BukkitLoader.getNmsProvider().packetTitle(TitleAction.ACTIONBAR, component));
	}

	@Override
	public void actionBar(Collection<?> receivers, Component component) {
		Object packet = null;
		for (Object receiver : receivers)
			if (receiver instanceof Player && ((Player) receiver).isOnline()) {
				if (packet == null)
					packet = BukkitLoader.getNmsProvider().packetTitle(TitleAction.ACTIONBAR, component);
				BukkitLoader.getPacketHandler().send((Player) receiver, packet);
			}
	}

	@Override
	public void title(Object receiver, Component title, Component subtitle, TitleTimes times) {
		title(java.util.Collections.singletonList(receiver), title, subtitle, times);
	}

	@Override
	public void title(Collection<?> receivers, Component title, Component subtitle, TitleTimes times) {
		Object timingPacket = null;
		Object titlePacket = null;
		Object subtitlePacket = null;
		for (Object receiver : receivers)
			if (receiver instanceof Player && ((Player) receiver).isOnline()) {
				if (timingPacket == null) {
					timingPacket = BukkitLoader.getNmsProvider().packetTitle(TitleAction.TIMES,
							Component.EMPTY_COMPONENT, times.fadeIn(), times.stay(), times.fadeOut());
					titlePacket = BukkitLoader.getNmsProvider().packetTitle(TitleAction.TITLE, title);
					subtitlePacket = BukkitLoader.getNmsProvider().packetTitle(TitleAction.SUBTITLE, subtitle);
				}
				Player player = (Player) receiver;
				BukkitLoader.getPacketHandler().send(player, timingPacket);
				BukkitLoader.getPacketHandler().send(player, subtitlePacket);
				BukkitLoader.getPacketHandler().send(player, titlePacket);
			}
	}
}
