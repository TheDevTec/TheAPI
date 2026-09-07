package me.devtec.theapi.bukkit.gui.expansion.loop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import me.devtec.shared.dataholder.Config;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;
import me.devtec.theapi.bukkit.gui.expansion.items.ConditionItem;
import me.devtec.theapi.bukkit.gui.expansion.items.ItemPackage;
import me.devtec.theapi.bukkit.gui.expansion.utils.Utils;
import me.devtec.theapi.bukkit.xseries.XMaterial;

public class LoopManager {

	private static final Map<String, Callable<ResultItemCallable>> constructors = new HashMap<>();
	private static final Biome[] BIOMES = Biome.values();
	private static final MaterialEntry[] MATERIALS = loadMaterials();

	public static ResultItemCallable createByName(String name) {
		Callable<ResultItemCallable> constructor = constructors.get(name.toLowerCase(Locale.ROOT));
		if (constructor == null) return null;

		try {
			return constructor.call();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void register(String name, Callable<ResultItemCallable> callable) {
		constructors.put(name.toLowerCase(Locale.ROOT), callable);
	}

	public static void unregister(String name) {
		constructors.remove(name.toLowerCase(Locale.ROOT));
	}

	public static void registerDefaults() {

		register("player", () -> (holder, player, sharedData, conditions, defaultItem) -> {
			List<ItemGUI> items = new ArrayList<ItemGUI>();

			for (Player online : BukkitLoader.getOnlinePlayers()) {
				if (!player.canSee(online)) continue;

				Map<String, Object> placeholders = new HashMap<String, Object>(8);
				placeholders.put("target", online.getName());
				placeholders.put("target_health", online.getHealth());
				placeholders.put("target_max_health", online.getMaxHealth());
				placeholders.put("target_world", online.getWorld().getName());

				ItemPackage result = findItem(online, sharedData, placeholders, conditions, defaultItem);
				if (result != null) items.add(createItem(result, online, sharedData, placeholders));
			}

			return items;
		});

		register("world", () -> (holder, player, sharedData, conditions, defaultItem) -> {
			List<World> worlds = Bukkit.getWorlds();
			List<ItemGUI> items = new ArrayList<ItemGUI>(worlds.size());

			for (World world : worlds) {
				Map<String, Object> placeholders = new HashMap<String, Object>(2);
				placeholders.put("world", world.getName());

				ItemPackage result = findItem(player, sharedData, placeholders, conditions, defaultItem);
				if (result != null) items.add(createItem(result, player, sharedData, placeholders));
			}

			return items;
		});

		register("biome", () -> (holder, player, sharedData, conditions, defaultItem) -> {
			List<ItemGUI> items = new ArrayList<ItemGUI>(BIOMES.length);

			for (Biome biome : BIOMES) {
				Map<String, Object> placeholders = new HashMap<String, Object>(2);
				placeholders.put("biome", biome.name());

				ItemPackage result = findItem(player, sharedData, placeholders, conditions, defaultItem);
				if (result != null) items.add(createItem(result, player, sharedData, placeholders));
			}

			return items;
		});

		register("material", () -> (holder, player, sharedData, conditions, defaultItem) -> {
			List<ItemGUI> items = new ArrayList<ItemGUI>(MATERIALS.length);

			for (MaterialEntry material : MATERIALS) {
				Map<String, Object> placeholders = new HashMap<String, Object>(4);
				placeholders.put("material", material.name);
				placeholders.put("bukkitMaterial", material.bukkitName);
				placeholders.put("materialName", material.formattedName);

				ItemPackage result = findItem(player, sharedData, placeholders, conditions, defaultItem);
				if (result != null) items.add(createItem(result, player, sharedData, placeholders));
			}

			return items;
		});
	}

	private static ItemPackage findItem(Player player, Config sharedData, Map<String, Object> placeholders,
			List<ConditionItem> conditions, ItemPackage defaultItem) {

		for (ConditionItem condition : conditions) {
			ItemPackage result = condition.test(player, sharedData, placeholders);
			if (result != null && result.getItem() != null) return result;
		}

		return defaultItem != null && defaultItem.getItem() != null ? defaultItem : null;
	}

	private static ItemGUI createItem(final ItemPackage result, final Player placeholderPlayer,
			final Config sharedData, final Map<String, Object> placeholders) {

		return new ItemGUI(Utils.applyPlaceholders(result.getTypePlaceholder(), result.getItem(), placeholders, placeholderPlayer)) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				result.runActions(gui, player, sharedData, placeholders);
			}
		};
	}

	private static MaterialEntry[] loadMaterials() {
		List<MaterialEntry> result = new ArrayList<>();

		for (XMaterial material : XMaterial.VALUES) {
			if (!material.isSupported() || material.isAir()) continue;

			Material bukkit = material.parseMaterial();
			if (bukkit == null || !bukkit.isItem()) continue;

			result.add(new MaterialEntry(
					material.name(),
					bukkit.name().toLowerCase(Locale.ROOT),
					material.getFormattedName()));
		}

		return result.toArray(new MaterialEntry[result.size()]);
	}

	private static final class MaterialEntry {

		private final String name;
		private final String bukkitName;
		private final String formattedName;

		private MaterialEntry(String name, String bukkitName, String formattedName) {
			this.name = name;
			this.bukkitName = bukkitName;
			this.formattedName = formattedName;
		}
	}
}