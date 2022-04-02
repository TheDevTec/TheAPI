package me.devtec.theapi.bungee.commands.selectors;

import java.util.ArrayList;
import java.util.List;

import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.commands.selectors.SelectorType;
import me.devtec.shared.utility.StringUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeSelectorUtils implements SelectorUtils {

	public boolean check(SelectorType[] selectorTypes, String string) {
		for(SelectorType selectorType : selectorTypes)
			switch(selectorType) {
			case PLAYER:
				return !ProxyServer.getInstance().matchPlayer(string).isEmpty();
			case SERVER:
				return ProxyServer.getInstance().getServerInfo(string)!=null;
			case NUMBER:
				return StringUtils.isNumber(string);
				default:
					break;
			}
		return false;
	}
	
	public List<String> buildSelectorKeys(SelectorType[] selectorTypes) {
		List<String> text = new ArrayList<>();
		for(SelectorType selectorType : selectorTypes)
			switch(selectorType) {
			case PLAYER:
				for(ProxiedPlayer s : ProxyServer.getInstance().getPlayers())
					text.add(s.getName());
				break;
			case SERVER:
				text.addAll(ProxyServer.getInstance().getServers().keySet());
				break;
			case NUMBER:
				text.add("{number}");
				break;
				default:
					break;
			}
		return text;
	}
}
