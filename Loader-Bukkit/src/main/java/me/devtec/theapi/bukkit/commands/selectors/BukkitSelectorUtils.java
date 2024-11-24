package me.devtec.theapi.bukkit.commands.selectors;

import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.commands.selectors.Selector;
import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BukkitSelectorUtils implements SelectorUtils<CommandSender> {
    @Override
    public List<String> build(CommandSender s, Selector selector) {
        List<String> list = new ArrayList<>();
        switch (selector) {
            case BIOME_TYPE:
                for (Biome biome : Biome.values())
                    list.add(biome.name());
                break;
            case MATERIAL:
                for (XMaterial material : XMaterial.VALUES)
                    if (material.isSupported() && material.parseMaterial().isItem() && !material.isAir())
                        list.add(material.name());
                break;
            case BOOLEAN:
                list.add("true");
                list.add("false");
                break;
            case ENTITY_SELECTOR:
                if (getPlayers(s).isEmpty())
                    break;
                list.add("*");
                list.add("@a");
                list.add("@e");
                list.add("@r");
                list.add("@s");
                list.add("@p");
            case PLAYER:
                for (Player player : getPlayers(s))
                    list.add(player.getName());
                break;
            case ENTITY_TYPE:
                for (EntityType biome : EntityType.values())
                    list.add(biome.name());
                break;
            case INTEGER:
                list.add("{integer}");
                break;
            case NUMBER:
                list.add("{number}");
                break;
            case WORLD:
                for (World world : Bukkit.getWorlds())
                    list.add(world.getName());
                break;
            case POSITION:
                list.add("~");
                list.add("{number}");
                break;
            default:
                break;
        }
        return list;
    }

    private Collection<? extends Player> getPlayers(CommandSender s) {
        if (s instanceof Player) {
            List<Player> players = new ArrayList<>();
            for (Player p : BukkitLoader.getOnlinePlayers())
                if (((Player) s).canSee(p))
                    players.add(p);
            return players;
        }
        return BukkitLoader.getOnlinePlayers();
    }

    @Override
    public boolean check(CommandSender s, Selector selector, String value) {
        if (value == null || value.isEmpty())
            return false;
        switch (selector) {
            case BIOME_TYPE:
                try {
                    Biome.valueOf(value.toUpperCase());
                    return true;
                } catch (NoSuchFieldError | Exception ignored) {
                }
                break;
            case MATERIAL:
                Optional<XMaterial> material = XMaterial.matchXMaterial(value);
                return material.isPresent() && material.get().isSupported();
            case BOOLEAN:
                return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
            case ENTITY_SELECTOR:
                char first = value.charAt(0);
                char second = value.length() == 2 ? toLowerCase(value.charAt(1)) : 0;
                if (first == '@' && (second == 'a' || second == 'e' || second == 'r' || second == 's' || second == 'p') || first == '*' && value.length() == 1)
                    return true;
                // Else continue to player
            case PLAYER:
                Player player = Bukkit.getPlayer(value);
                if (player == null)
                    return false;
                return !(s instanceof Player) || ((Player) s).canSee(player);
            case ENTITY_TYPE:
                try {
                    EntityType.valueOf(value.toUpperCase());
                    return true;
                } catch (NoSuchFieldError | Exception ignored) {
                }
                break;
            case INTEGER:
                return ParseUtils.isInt(value);
            case NUMBER:
                return ParseUtils.isNumber(value);
            case WORLD:
                return Bukkit.getWorld(value) != null;
            case POSITION:
                return ParseUtils.isNumber(value) || value.indexOf('~') != -1 || value.indexOf('+') != -1 || value.indexOf('-') != -1;
            default:
                break;
        }
        return false;
    }

    private char toLowerCase(int charAt) {
        return (char) (charAt <= 90 ? charAt + 32 : charAt);
    }
}