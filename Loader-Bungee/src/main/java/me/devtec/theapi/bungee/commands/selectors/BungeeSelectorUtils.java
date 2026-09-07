package me.devtec.theapi.bungee.commands.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.commands.selectors.Selector;
import me.devtec.shared.utility.ParseUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeSelectorUtils implements SelectorUtils<CommandSender> {

	private static final List<String> BOOLEAN = immutable("true", "false");
	private static final List<String> INTEGER = Collections.singletonList("{integer}");
	private static final List<String> NUMBER = Collections.singletonList("{number}");
	private static final List<String> POSITION = immutable("~", "{number}");
	private static final List<String> ENTITY_SELECTORS = immutable("*", "@a", "@e", "@r", "@s", "@p");

	@Override
	public List<String> build(CommandSender sender, Selector selector) {
		switch (selector) {
		case BOOLEAN:
			return BOOLEAN;

		case ENTITY_SELECTOR: {
			Collection<ProxiedPlayer> players = getPlayers(sender);

			if (players.isEmpty())
				return Collections.emptyList();

			List<String> result = new ArrayList<>(players.size() + ENTITY_SELECTORS.size());
			result.addAll(ENTITY_SELECTORS);

			for (ProxiedPlayer player : players)
				result.add(player.getName());

			return result;
		}

		case PLAYER: {
			Collection<ProxiedPlayer> players = getPlayers(sender);

			if (players.isEmpty())
				return Collections.emptyList();

			List<String> result = new ArrayList<>(players.size());

			for (ProxiedPlayer player : players)
				result.add(player.getName());

			return result;
		}

		case INTEGER:
			return INTEGER;

		case NUMBER:
			return NUMBER;

		case POSITION:
			return POSITION;

		case SERVER:
			return new ArrayList<>(ProxyServer.getInstance().getServers().keySet());

		default:
			return Collections.emptyList();
		}
	}

	private Collection<ProxiedPlayer> getPlayers(CommandSender sender) {
		return ProxyServer.getInstance().getPlayers();
	}

	@Override
	public boolean check(CommandSender sender, Selector selector, String value) {
		if (value == null || value.isEmpty())
			return false;

		switch (selector) {
		case BOOLEAN:
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);

		case ENTITY_SELECTOR:
			if (isEntitySelector(value))
				return true;

		case PLAYER: {
			return !ProxyServer.getInstance().matchPlayer(value).isEmpty();
		}

		case INTEGER:
			return ParseUtils.isInt(value);

		case NUMBER:
			return ParseUtils.isNumber(value);

		case POSITION:
			return ParseUtils.isNumber(value) || value.indexOf('~') != -1 || value.indexOf('+') != -1
					|| value.indexOf('-') != -1;

		case SERVER:
			return ProxyServer.getInstance().getServerInfo(value) != null;

		default:
			return false;
		}
	}

	private static boolean isEntitySelector(String value) {
		if (value.length() == 1)
			return value.charAt(0) == '*';

		if (value.length() != 2 || value.charAt(0) != '@')
			return false;

		char c = value.charAt(1);

		if (c >= 'A' && c <= 'Z')
			c = (char) (c + 32);

		return c == 'a' || c == 'e' || c == 'r' || c == 's' || c == 'p';
	}

	private static List<String> immutable(String... values) {
		List<String> result = new ArrayList<>(values.length);
		Collections.addAll(result, values);
		return Collections.unmodifiableList(result);
	}
}
