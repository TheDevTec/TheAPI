package me.devtec.theapi.bungee.commands.selectors;

import java.util.ArrayList;
import java.util.List;

import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.commands.selectors.Selector;
import me.devtec.shared.utility.ParseUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeSelectorUtils implements SelectorUtils<CommandSender> {
	@Override
	public List<String> build(CommandSender s, Selector selector) {
		List<String> list = new ArrayList<>();
		switch (selector) {
		case SERVER:
			list.addAll(ProxyServer.getInstance().getServers().keySet());
			break;
		case BOOLEAN:
			list.add("true");
			list.add("false");
			break;
		case ENTITY_SELECTOR:
			if (ProxyServer.getInstance().getOnlineCount() == 0)
				break;
			list.add("*");
			list.add("@a");
			list.add("@e");
			list.add("@r");
			list.add("@s");
			list.add("@p");
		case PLAYER:
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
				list.add(player.getName());
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
	public boolean check(CommandSender s, Selector selector, String value) {
		if (value == null || value.isEmpty())
			return false;
		switch (selector) {
		case BOOLEAN:
			return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
		case ENTITY_SELECTOR:
			char first = value.charAt(0);
			char second = value.length() > 1 ? toLowerCase(value.charAt(1)) : 0;
			if (first == '@' && (second == 'a' || second == 'e' || second == 'r' || second == 's' || second == 'p' && value.length() == 2) || first == '*' && value.length() == 1)
				return true;
			// Else continue to player
		case PLAYER:
			return !ProxyServer.getInstance().matchPlayer(value).isEmpty();
		case INTEGER:
			return ParseUtils.isInt(value);
		case NUMBER:
			return ParseUtils.isNumber(value);
		case SERVER:
			return ProxyServer.getInstance().getServerInfo(value) != null;
		default:
			break;
		}
		return false;
	}

	private char toLowerCase(int charAt) {
		return (char) (charAt > 100 ? charAt + 32 : charAt);
	}
}
