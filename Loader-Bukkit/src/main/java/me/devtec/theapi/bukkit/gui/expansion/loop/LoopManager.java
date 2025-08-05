package me.devtec.theapi.bukkit.gui.expansion.loop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import me.devtec.theapi.bukkit.gui.expansion.items.ConditionItem;
import me.devtec.theapi.bukkit.gui.expansion.items.ItemPackage;
import me.devtec.theapi.bukkit.gui.expansion.utils.Utils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;
import me.devtec.theapi.bukkit.xseries.XMaterial;

public class LoopManager {
	private static final Map<String, Callable<ResultItemCallable>> constructors = new HashMap<>();

	public static ResultItemCallable createByName(String name) {
		try {
			return constructors.get(name.toLowerCase()).call();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void register(String name, Callable<ResultItemCallable> callable) {
		constructors.put(name.toLowerCase(), callable);
	}

	public static void unregister(String name) {
		constructors.remove(name.toLowerCase());
	}

	public static void registerDefaults() {
		register("player", () -> (holder, player, slotItemWithConditions, defaultSlotItem) -> {
			List<ItemGUI> items = new ArrayList<>();
			playerLoop: for (Player online : BukkitLoader.getOnlinePlayers())
				if (player.canSee(online)) {
					Map<String, Object> placeholders = new HashMap<>();
					placeholders.put("target", online.getName());
					placeholders.put("target_health", online.getHealth());
					placeholders.put("target_max_health", online.getMaxHealth());
					placeholders.put("target_world", online.getWorld().getName());
					for (ConditionItem condition : slotItemWithConditions) {
						ItemPackage result = condition.test(online, placeholders);
						if (result != null && result.getItem() != null) {
							items.add(new ItemGUI(Utils.applyPlaceholders(result.getTypePlaceholder(), result.getItem(),
									placeholders, online)) {

								@Override
								public void onClick(Player player, HolderGUI gui, ClickType click) {
									result.runActions(gui, player, placeholders);
								}
							});
							continue playerLoop;
						}
					}
					if (defaultSlotItem != null && defaultSlotItem.getItem() != null)
						items.add(new ItemGUI(Utils.applyPlaceholders(defaultSlotItem.getTypePlaceholder(),
								defaultSlotItem.getItem(), placeholders, online)) {

							@Override
							public void onClick(Player player, HolderGUI gui, ClickType click) {
								defaultSlotItem.runActions(gui, player, placeholders);
							}
						});
				}
			return items;
		});
		register("world", () -> (holder, player, slotItemWithConditions, defaultSlotItem) -> {
			List<ItemGUI> items = new ArrayList<>();
			worldLoop: for (World world : Bukkit.getWorlds()) {
				Map<String, Object> placeholders = new HashMap<>();
				placeholders.put("world", world.getName());
				for (ConditionItem condition : slotItemWithConditions) {
					ItemPackage result = condition.test(player, placeholders);
					if (result != null && result.getItem() != null) {
						items.add(new ItemGUI(Utils.applyPlaceholders(result.getTypePlaceholder(), result.getItem(),
								placeholders, player)) {

							@Override
							public void onClick(Player player, HolderGUI gui, ClickType click) {
								result.runActions(gui, player, placeholders);
							}
						});
						continue worldLoop;
					}
				}
				if (defaultSlotItem != null && defaultSlotItem.getItem() != null)
					items.add(new ItemGUI(Utils.applyPlaceholders(defaultSlotItem.getTypePlaceholder(),
							defaultSlotItem.getItem(), placeholders, player)) {

						@Override
						public void onClick(Player player, HolderGUI gui, ClickType click) {
							defaultSlotItem.runActions(gui, player, placeholders);
						}
					});
			}
			return items;
		});
		register("biome", () -> (holder, player, slotItemWithConditions, defaultSlotItem) -> {
			List<ItemGUI> items = new ArrayList<>();
			worldLoop: for (Biome biome : Biome.values()) {
				Map<String, Object> placeholders = new HashMap<>();
				placeholders.put("biome", biome.name());
				for (ConditionItem condition : slotItemWithConditions) {
					ItemPackage result = condition.test(player, placeholders);
					if (result != null && result.getItem() != null) {
						items.add(new ItemGUI(Utils.applyPlaceholders(result.getTypePlaceholder(), result.getItem(),
								placeholders, player)) {

							@Override
							public void onClick(Player player, HolderGUI gui, ClickType click) {
								result.runActions(gui, player, placeholders);
							}
						});
						continue worldLoop;
					}
				}
				if (defaultSlotItem != null && defaultSlotItem.getItem() != null)
					items.add(new ItemGUI(Utils.applyPlaceholders(defaultSlotItem.getTypePlaceholder(),
							defaultSlotItem.getItem(), placeholders, player)) {

						@Override
						public void onClick(Player player, HolderGUI gui, ClickType click) {
							defaultSlotItem.runActions(gui, player, placeholders);
						}
					});
			}
			return items;
		});
		register("material", () -> (holder, player, slotItemWithConditions, defaultSlotItem) -> {
			List<ItemGUI> items = new ArrayList<>();
			worldLoop: for (XMaterial material : XMaterial.VALUES) {
				if (!material.isSupported() || material.isAir() || !material.parseMaterial().isItem())
					continue;
				Map<String, Object> placeholders = new HashMap<>();
				placeholders.put("material", material.name());
				placeholders.put("bukkitMaterial", material.parseMaterial().name().toLowerCase());
				placeholders.put("materialName", material.getFormattedName());
				for (ConditionItem condition : slotItemWithConditions) {
					ItemPackage result = condition.test(player, placeholders);
					if (result != null && result.getItem() != null) {
						items.add(new ItemGUI(Utils.applyPlaceholders(result.getTypePlaceholder(), result.getItem(),
								placeholders, player)) {

							@Override
							public void onClick(Player player, HolderGUI gui, ClickType click) {
								result.runActions(gui, player, placeholders);
							}
						});
						continue worldLoop;
					}
				}
				if (defaultSlotItem != null && defaultSlotItem.getItem() != null)
					items.add(new ItemGUI(Utils.applyPlaceholders(defaultSlotItem.getTypePlaceholder(),
							defaultSlotItem.getItem(), placeholders, player)) {

						@Override
						public void onClick(Player player, HolderGUI gui, ClickType click) {
							defaultSlotItem.runActions(gui, player, placeholders);
						}
					});
			}
			return items;
		});
	}
}
