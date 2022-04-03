package me.devtec.theapi.bungee.commands.selectors;

import java.util.ArrayList;
import java.util.List;

import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.commands.selectors.Selector;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeSelectorUtils implements SelectorUtils {
	@Override
	public List<String> build(Selector selector) {
		List<String> list = new ArrayList<>();
		switch(selector) {
		case SERVER:
			list.addAll(ProxyServer.getInstance().getServers().keySet());
			break;
		case BOOLEAN:
			list.add("true");
			list.add("false");
			break;
		case ENTITY_SELECTOR:
			if(ProxyServer.getInstance().getOnlineCount()==0)
				break;
			list.add("@a");
			list.add("@e");
			list.add("@r");
			list.add("@s");
			list.add("@p");
		case PLAYER:
			for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
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
	public boolean check(Selector selector, String value) {
		switch(selector) {
		case BOOLEAN:
			return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
		case ENTITY_SELECTOR:
			boolean match = value.matches("@[AaEeRrSsPp]");
			if(match)return true;
			//Else continue to player
		case PLAYER:
			return !ProxyServer.getInstance().matchPlayer(value).isEmpty();
		case INTEGER:
			try {
				Integer.parseInt(value);
				return true;
			}catch(NoSuchFieldError | Exception err) {}
			break;
		case NUMBER:
			try {
				Double.parseDouble(value);
				return true;
			}catch(NoSuchFieldError | Exception err) {}
			break;
		case SERVER:
			return ProxyServer.getInstance().getServerInfo(value) != null;
		default:
			break;
		}
		return false;
	}
}
