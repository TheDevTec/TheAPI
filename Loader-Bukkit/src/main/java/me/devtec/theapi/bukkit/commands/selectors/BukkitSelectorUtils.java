package me.devtec.theapi.bukkit.commands.selectors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.devtec.shared.commands.manager.SelectorUtils;
import me.devtec.shared.commands.selectors.Selector;
import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.xseries.XMaterial;

public class BukkitSelectorUtils implements SelectorUtils<CommandSender> {

	private static final List<String> BOOLEAN = immutable("true", "false");
	private static final List<String> INTEGER = Collections.singletonList("{integer}");
	private static final List<String> NUMBER = Collections.singletonList("{number}");
	private static final List<String> POSITION = immutable("~", "{number}");
	private static final List<String> ENTITY_SELECTORS = immutable("*", "@a", "@e", "@r", "@s", "@p");

	@Override
	public List<String> build(CommandSender sender, Selector selector) {
		switch (selector) {
		case BIOME_TYPE:
			return StaticValues.BIOMES;

		case MATERIAL:
			return StaticValues.MATERIALS;

		case BOOLEAN:
			return BOOLEAN;

		case ENTITY_SELECTOR: {
			Collection<? extends Player> players = getPlayers(sender);

			if (players.isEmpty())
				return Collections.emptyList();

			List<String> result = new ArrayList<>(players.size() + ENTITY_SELECTORS.size());
			result.addAll(ENTITY_SELECTORS);

			for (Player player : players)
				result.add(player.getName());

			return result;
		}

		case PLAYER: {
			Collection<? extends Player> players = getPlayers(sender);

			if (players.isEmpty())
				return Collections.emptyList();

			List<String> result = new ArrayList<>(players.size());

			for (Player player : players)
				result.add(player.getName());

			return result;
		}

		case ENTITY_TYPE:
			return StaticValues.ENTITY_TYPES;

		case INTEGER:
			return INTEGER;

		case NUMBER:
			return NUMBER;

		case WORLD: {
			List<World> worlds = Bukkit.getWorlds();

			if (worlds.isEmpty())
				return Collections.emptyList();

			List<String> result = new ArrayList<>(worlds.size());

			for (World world : worlds)
				result.add(world.getName());

			return result;
		}

		case POSITION:
			return POSITION;

		default:
			return Collections.emptyList();
		}
	}

	private Collection<? extends Player> getPlayers(CommandSender sender) {
		Collection<? extends Player> online = BukkitLoader.getOnlinePlayers();

		if (!(sender instanceof Player))
			return online;

		Player player = (Player) sender;
		List<Player> visible = new ArrayList<>(online.size());

		for (Player target : online)
			if (player.canSee(target))
				visible.add(target);

		return visible;
	}

	@Override
	public boolean check(CommandSender sender, Selector selector, String value) {
		if (value == null || value.isEmpty())
			return false;

		switch (selector) {
		case BIOME_TYPE:
			try {
				Biome.valueOf(value.toUpperCase(Locale.ROOT));
				return true;
			} catch (IllegalArgumentException ignored) {
				return false;
			}

		case MATERIAL: {
			Optional<XMaterial> material = XMaterial.matchXMaterial(value);

			if (!material.isPresent())
				return false;

			XMaterial result = material.get();

			if (!result.isSupported() || result.isAir())
				return false;

			Material bukkit = result.parseMaterial();
			return bukkit != null && isItem(bukkit);
		}

		case BOOLEAN:
			return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);

		case ENTITY_SELECTOR:
			if (isEntitySelector(value))
				return true;

		case PLAYER: {
			Player player = Bukkit.getPlayer(value);

			if (player == null)
				return false;

			return !(sender instanceof Player) || ((Player) sender).canSee(player);
		}

		case ENTITY_TYPE:
			try {
				EntityType.valueOf(value.toUpperCase(Locale.ROOT));
				return true;
			} catch (IllegalArgumentException ignored) {
				return false;
			}

		case INTEGER:
			return ParseUtils.isInt(value);

		case NUMBER:
			return ParseUtils.isNumber(value);

		case WORLD:
			return Bukkit.getWorld(value) != null;

		case POSITION:
			return ParseUtils.isNumber(value)
					|| value.indexOf('~') != -1
					|| value.indexOf('+') != -1
					|| value.indexOf('-') != -1;

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

	private static boolean isItem(Material material) {
		Method method = StaticValues.MATERIAL_IS_ITEM;

		if (method == null)
			return true;

		try {
			return Boolean.TRUE.equals(method.invoke(material));
		} catch (Exception ignored) {
			return true;
		}
	}

	private static List<String> immutable(String... values) {
		List<String> result = new ArrayList<>(values.length);
		Collections.addAll(result, values);
		return Collections.unmodifiableList(result);
	}

	private static final class StaticValues {

		private static final Method MATERIAL_IS_ITEM = findMaterialIsItem();
		private static final List<String> BIOMES = loadBiomes();
		private static final List<String> ENTITY_TYPES = loadEntityTypes();
		private static final List<String> MATERIALS = loadMaterials();

		private static Method findMaterialIsItem() {
			try {
				return Material.class.getMethod("isItem");
			} catch (Exception ignored) {
				return null;
			}
		}

		private static List<String> loadBiomes() {
			Biome[] values = Biome.values();
			List<String> result = new ArrayList<>(values.length);

			for (Biome biome : values)
				result.add(biome.name());

			return Collections.unmodifiableList(result);
		}

		private static List<String> loadEntityTypes() {
			EntityType[] values = EntityType.values();
			List<String> result = new ArrayList<>(values.length);

			for (EntityType type : values)
				result.add(type.name());

			return Collections.unmodifiableList(result);
		}

		private static List<String> loadMaterials() {
			List<String> result = new ArrayList<>(XMaterial.VALUES.length);

			for (XMaterial material : XMaterial.VALUES) {
				if (!material.isSupported() || material.isAir())
					continue;

				Material bukkit = material.parseMaterial();

				if (bukkit != null && isItem(bukkit))
					result.add(material.name());
			}

			return Collections.unmodifiableList(result);
		}
	}
}