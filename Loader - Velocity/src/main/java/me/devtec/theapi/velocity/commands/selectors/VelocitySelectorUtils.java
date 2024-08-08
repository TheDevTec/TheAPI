package me.devtec.theapi.velocity.commands.selectors;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.commands.selectors.Selector;
import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.velocity.VelocityLoader;

import java.util.ArrayList;
import java.util.List;

public class VelocitySelectorUtils implements SelectorUtils<CommandSource> {
    @Override
    public List<String> build(CommandSource s, Selector selector) {
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
    public boolean check(CommandSource s, Selector selector, String value) {
        if (value == null || value.isEmpty())
            return false;
        switch (selector) {
            case BOOLEAN:
                return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
            case ENTITY_SELECTOR:
                char first = value.charAt(0);
                char second = value.length() == 2 ? toLowerCase(value.charAt(1)) : 0;
                if (first == '@' && (second == 'a' || second == 'e' || second == 'r' || second == 's' || second == 'p') || first == '*' && value.length() == 1)
                    return true;
                // Else continue to player
            case PLAYER:
                return !VelocityLoader.getServer().matchPlayer(value).isEmpty();
            case INTEGER:
                return ParseUtils.isInt(value);
            case NUMBER:
                return ParseUtils.isNumber(value);
            case SERVER:
                return VelocityLoader.getServer().getServer(value).isPresent();
            default:
                break;
        }
        return false;
    }

    private char toLowerCase(int charAt) {
        return (char) (charAt <= 90 ? charAt + 32 : charAt);
    }
}
