package me.devtec.theapi.velocity.commands.selectors;

import java.util.ArrayList;
import java.util.List;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.commands.selectors.Selector;
import me.devtec.theapi.velocity.VelocityLoader;

public class VelocitySelectorUtils implements SelectorUtils {
	@Override
	public List<String> build(Selector selector)
	{
		List<String> list = new ArrayList<>();
		switch (selector) {
		case SERVER:
			for (RegisteredServer server : VelocityLoader.getServer().getAllServers())
				list.add(server.getServerInfo().getName());
			break;
		case BOOLEAN:
			list.add("true");
			list.add("false");
			break;
		case ENTITY_SELECTOR:
			if (VelocityLoader.getServer().getPlayerCount() == 0)
				break;
			list.add("*");
			list.add("@a");
			list.add("@e");
			list.add("@r");
			list.add("@s");
			list.add("@p");
		case PLAYER:
			for (Player player : VelocityLoader.getServer().getAllPlayers())
				list.add(player.getUsername());
			break;
		case INTEGER:
			list.add("{integer}");
			break;
		case NUMBER:
			list.add("{number}");
			break;
		default:
			break;
		}
		return list;
	}

	@Override
	public boolean check(Selector selector, String value)
	{
		switch (selector) {
		case BOOLEAN:
			return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
		case ENTITY_SELECTOR:
			boolean match = value.matches("\\*|@[AaEeRrSsPp]");
			if (match)
				return true;
			// Else continue to player
		case PLAYER:
			return !VelocityLoader.getServer().matchPlayer(value).isEmpty();
		case INTEGER:
			try {
				Integer.parseInt(value);
				return true;
			} catch (NoSuchFieldError | Exception err) {
			}
			break;
		case NUMBER:
			try {
				Double.parseDouble(value);
				return true;
			} catch (NoSuchFieldError | Exception err) {
			}
			break;
		case SERVER:
			return VelocityLoader.getServer().getServer(value).isPresent();
		default:
			break;
		}
		return false;
	}
}
