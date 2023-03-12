package me.devtec.theapi.bukkit.commands.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.commands.selectors.Selector;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.xseries.XMaterial;

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
			if ((s instanceof Player ? getPlayers(s) : BukkitLoader.getOnlinePlayers()).size() == 0)
				break;
			list.add("*");
			list.add("@a");
			list.add("@e");
			list.add("@r");
			list.add("@s");
			list.add("@p");
		case PLAYER:
			for (Player player : s instanceof Player ? getPlayers(s) : BukkitLoader.getOnlinePlayers())
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
		default:
			break;
		}
		return list;
	}

	private Collection<? extends Player> getPlayers(CommandSender s) {
		List<Player> players = new ArrayList<>();
		for (Player p : BukkitLoader.getOnlinePlayers())
			if (((Player) s).canSee(p))
				players.add(p);
		return players;
	}

	@Override
	public boolean check(CommandSender s, Selector selector, String value) {
		switch (selector) {
		case BIOME_TYPE:
			try {
				Biome.valueOf(value.toUpperCase());
				return true;
			} catch (NoSuchFieldError | Exception err) {
			}
			break;
		case MATERIAL:
			return value != null && !value.isEmpty() && XMaterial.matchXMaterial(value).isPresent() && XMaterial.matchXMaterial(value).get().isSupported();
		case BOOLEAN:
			return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
		case ENTITY_SELECTOR:
			boolean match = value.matches("@[AaEeRrSsPp]|\\*");
			if (match)
				return true;
			// Else continue to player
		case PLAYER:
			if (value.isEmpty())
				return false;
			Player player = Bukkit.getPlayer(value);
			if (player == null)
				return false;
			if (s instanceof Player && ((Player) s).canSee(player))
				return true;
			return true;
		case ENTITY_TYPE:
			try {
				EntityType.valueOf(value.toUpperCase());
				return true;
			} catch (NoSuchFieldError | Exception err) {
			}
			break;
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
		case WORLD:
			return Bukkit.getWorld(value) != null;
		default:
			break;
		}
		return false;
	}
}