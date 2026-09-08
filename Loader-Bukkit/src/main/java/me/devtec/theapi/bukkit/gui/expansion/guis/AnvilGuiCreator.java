package me.devtec.theapi.bukkit.gui.expansion.guis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.scheduler.Scheduler;
import me.devtec.shared.scheduler.Tasker;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.GUI.ClickType;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.ItemGUI;
import me.devtec.theapi.bukkit.gui.expansion.GuiCreator;
import me.devtec.theapi.bukkit.gui.expansion.actions.Action;
import me.devtec.theapi.bukkit.gui.expansion.actions.EventType;
import me.devtec.theapi.bukkit.gui.expansion.actions.Task;
import me.devtec.theapi.bukkit.gui.expansion.conditions.Condition;
import me.devtec.theapi.bukkit.gui.expansion.items.ConditionItem;
import me.devtec.theapi.bukkit.gui.expansion.items.ItemPackage;
import me.devtec.theapi.bukkit.gui.expansion.items.StaticItemPackage;
import me.devtec.theapi.bukkit.gui.expansion.utils.Utils;

public class AnvilGuiCreator implements GuiCreator {

	private static final Map<String, Object> EMPTY = Collections.emptyMap();

	@Getter
	final String id;
	@Getter
	final Config config;

	String title;
	String staticTitle;
	boolean dynamicTitle;

	final Map<Integer, ItemGUI> staticItems = new HashMap<>();
	final Map<Character, ItemPackage> dynamicItems = new HashMap<>();
	final Map<Character, ConditionItem> conditionItems = new HashMap<>();

	final Map<EventType, List<Action>> eventActions = new EnumMap<>(EventType.class);
	final Map<String, List<Action>> customActions = new HashMap<>();
	final List<Task> schedulers = new ArrayList<>();

	public AnvilGuiCreator(String id, Config config) {
		this.id = id;
		this.config = config;
		reload();
	}

	@Override
	public HolderGUI open(Player player) {
		final UUID uuid = player.getUniqueId();
		final Config data = sharedData.computeIfAbsent(uuid, key -> new Config());

		runActions(eventActions.get(EventType.BEFORE_OPEN_MENU), null, player, data, EMPTY);

		final List<Integer> schedulerIds = schedulers.isEmpty() ? Collections.<Integer>emptyList()
				: new ArrayList<>(schedulers.size());

		final AnvilGUI gui = new AnvilGUI(dynamicTitle ? Utils.replacePlaceholders(title, null, uuid) : staticTitle) {

			private boolean schedulersCancelled;

			private void cancelSchedulers() {
				if (schedulersCancelled)
					return;
				schedulersCancelled = true;

				for (int id : schedulerIds)
					Scheduler.cancelTask(id);
			}

			@Override
			public void onClose(Player player, CloseReason reason) {
				cancelSchedulers();

				if (reason != CloseReason.CHANGING_MENU) {
					Config data = sharedData.get(player.getUniqueId());
					runActions(eventActions.get(EventType.CLOSE_MENU), this, player, data, renamePlaceholders(this));

					sharedData.remove(player.getUniqueId());
				}
			}
		};

		for (final Task task : schedulers) {
			final List<Action> taskActions = task.getActions();

			schedulerIds.add(new Tasker() {
				@Override
				public void run() {
					Map<String, Object> placeholders = renamePlaceholders(gui);

					for (char itemId : task.getItems())
						updateItem(gui, player, data, itemId, placeholders);

					runActions(taskActions, gui, player, data, placeholders);
				}
			}.runRepeating(task.getTime(), task.getTime()));
		}

		for (Entry<Integer, ItemGUI> entry : staticItems.entrySet())
			gui.setItem(entry.getKey(), entry.getValue());

		for (Entry<Character, ItemPackage> entry : dynamicItems.entrySet()) {
			final ItemPackage itemPackage = entry.getValue();
			ItemGUI item = createDynamicItem(itemPackage, gui, player, data);

			for (int slot : itemPackage.getSlots())
				gui.setItem(slot, item);
		}

		for (Entry<Character, ConditionItem> entry : conditionItems.entrySet()) {
			ConditionItem conditionItem = entry.getValue();
			Map<String, Object> placeholders = renamePlaceholders(gui);
			ItemPackage itemPackage = conditionItem.test(player, data, placeholders);

			if (itemPackage == null || itemPackage.getItem() == null)
				continue;

			ItemGUI item;

			if (itemPackage instanceof StaticItemPackage)
				item = ((StaticItemPackage) itemPackage).getItemGui();
			else
				item = createDynamicItem(itemPackage, gui, player, data);

			for (int slot : conditionItem.getSlots())
				gui.setItem(slot, item);
		}

		gui.open(player);

		runActions(eventActions.get(EventType.OPEN_MENU), gui, player, data, renamePlaceholders(gui));
		return gui;
	}

	@Override
	public void updateItem(HolderGUI gui, Player player, char itemId) {
		Config data = sharedData.get(player.getUniqueId());

		if (data == null)
			return;

		updateItem(gui, player, data, itemId, gui instanceof AnvilGUI ? renamePlaceholders((AnvilGUI) gui) : EMPTY);
	}

	private void updateItem(HolderGUI gui, Player player, Config data, char itemId, Map<String, Object> placeholders) {
		ItemPackage item = dynamicItems.get(itemId);

		if (item != null && !item.getSlots().isEmpty()) {
			ItemStack newItem = Utils.applyPlaceholders(item.getTypePlaceholder(), item.getItem(), placeholders,
					player);
			ItemGUI itemGui = gui.getItemGUI(item.getSlots().get(0));

			if (itemGui == null)
				itemGui = createDynamicItem(item, newItem, data);
			else
				itemGui.setItem(newItem);

			for (int slot : item.getSlots())
				gui.setItem(slot, itemGui);
		}

		final ConditionItem conditionItem = conditionItems.get(itemId);

		if (conditionItem == null || conditionItem.getSlots().isEmpty())
			return;

		final ItemPackage packageItem = conditionItem.test(player, data, placeholders);

		if (packageItem == null || packageItem.getItem() == null) {
			for (int slot : conditionItem.getSlots())
				gui.remove(slot);
			return;
		}

		if (packageItem instanceof StaticItemPackage) {
			ItemGUI itemGui = ((StaticItemPackage) packageItem).getItemGui();

			for (int slot : conditionItem.getSlots())
				gui.setItem(slot, itemGui);

			return;
		}

		ItemStack newItem = Utils.applyPlaceholders(packageItem.getTypePlaceholder(), packageItem.getItem(),
				placeholders, player);

		/*
		 * Musí vzniknout nový ItemGUI. Jinak by po přepnutí HAS <-> NOT zůstal starý
		 * onClick callback.
		 */
		ItemGUI itemGui = createDynamicItem(packageItem, newItem, data);

		for (int slot : conditionItem.getSlots())
			gui.setItem(slot, itemGui);
	}

	@Override
	public void reload() {
		eventActions.clear();
		customActions.clear();
		staticItems.clear();
		dynamicItems.clear();
		conditionItems.clear();
		schedulers.clear();

		title = config.getString("title", "NOT_SET");
		dynamicTitle = Utils.checkForPlaceholders(title);
		staticTitle = dynamicTitle ? null : ColorUtils.colorize(title);

		if (config.getString("lines") == null || config.get("lines") instanceof Collection) {
			BukkitLoader.getPlugin(BukkitLoader.class).getLogger().warning(
					"[GuiExpansion] Failed to load Anvil Gui; Anvil accepts only 3 items, use String, not List, file: "
							+ config.getFile().getName());
			return;
		}

		if (config.exists("events")) {
			loadEvent(EventType.BEFORE_OPEN_MENU, "events.before_open_menu");
			loadEvent(EventType.OPEN_MENU, "events.open_menu");
			loadEvent(EventType.CLOSE_MENU, "events.close_menu");
		}

		for (String scheduler : config.getKeys("scheduler")) {
			List<String> configuredItems = config.getStringList("scheduler." + scheduler + ".items");

			if (configuredItems.isEmpty())
				continue;

			List<Character> items = new ArrayList<>(configuredItems.size());

			for (String item : configuredItems)
				if (item != null && !item.isEmpty())
					items.add(item.charAt(0));

			if (items.isEmpty())
				continue;

			schedulers.add(new Task(items,
					Utils.createActions(this, config.getStringList("scheduler." + scheduler + ".actions")),
					config.getLong("scheduler." + scheduler + ".time")));
		}

		for (String actionName : config.getKeys("customActions"))
			customActions.put(actionName, createConfiguredActions("customActions." + actionName, true));

		Map<Character, ItemGUI> staticCache = new HashMap<>();
		String line = config.getString("lines");

		if (line.length() > 3)
			BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
					.warning("[GuiExpansion] Anvil Gui accepts only 3 items, ignoring slots after 3, file: "
							+ config.getFile().getName());

		int length = Math.min(3, line.length());

		for (int pos = 0; pos < length; ++pos) {
			char c = line.charAt(pos);

			if (c == ' ')
				continue;

			ItemGUI cached = staticCache.get(c);

			if (cached != null) {
				staticItems.put(pos, cached);
				continue;
			}

			ItemPackage dynamic = dynamicItems.get(c);

			if (dynamic != null) {
				dynamic.addSlot(pos);
				continue;
			}

			ConditionItem condition = conditionItems.get(c);

			if (condition != null) {
				condition.addSlot(pos);
				continue;
			}

			String path = "items." + c;

			if (config.existsKey(path + ".conditions")) {
				ItemPackage has = config.exists(path + ".has") ? createItemPackage(path + ".has", pos)
						: emptyPackage(pos);

				ItemPackage not = config.exists(path + ".not") ? createItemPackage(path + ".not", pos)
						: emptyPackage(pos);

				if (has == null)
					has = emptyPackage(pos);
				if (not == null)
					not = emptyPackage(pos);

				List<Condition> conditions = Utils.createConditions(config.getStringList(path + ".conditions"));
				conditionItems.put(c, new ConditionItem(conditions, pos, has, not));
				continue;
			}

			ItemPackage itemPackage = createItemPackage(path, pos);

			if (itemPackage == null)
				continue;

			if (itemPackage instanceof StaticItemPackage) {
				ItemGUI itemGui = ((StaticItemPackage) itemPackage).getItemGui();

				staticCache.put(c, itemGui);
				staticItems.put(pos, itemGui);
			} else
				dynamicItems.put(c, itemPackage);
		}
	}

	private void loadEvent(EventType type, String path) {
		if (config.existsKey(path))
			eventActions.put(type, Utils.createActions(this, config.getStringList(path)));
	}

	private ItemPackage createItemPackage(String path, int pos) {
		ItemMaker maker = ItemMaker.loadMakerFromConfig(config, path);

		if (maker == null) {
			BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
					.warning("[GuiExpansion] Failed to load item at " + path + " in " + config.getFile().getName());
			return null;
		}

		List<Action> actions = createConfiguredActions(path + ".click", false);
		String typePlaceholder = config.getString(path + ".type");

		if (Utils.checkForPlaceholders(maker) || typePlaceholder != null && Utils.checkForPlaceholders(typePlaceholder))
			return new ItemPackage(typePlaceholder, maker, pos, actions);

		final List<Action> itemActions = actions;

		ItemGUI itemGui = new ItemGUI(maker.build()) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				runActions(itemActions, gui, player, sharedData.get(player.getUniqueId()),
						gui instanceof AnvilGUI ? renamePlaceholders((AnvilGUI) gui) : EMPTY);
			}
		};

		return new StaticItemPackage(typePlaceholder, itemGui, maker, pos, actions);
	}

	private List<Action> createConfiguredActions(String path, boolean economy) {
		final List<Action> actions = Utils.createActions(this, config.getStringList(path + ".actions"));
		final List<String> messages = config.getStringList(path + ".messages");
		final List<String> commands = config.getStringList(path + ".commands");

		if (!commands.isEmpty())
			actions.add(0, new Action() {
				@Override
				public void run(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
					Map<String, Object> values = ensureRenamePlaceholder(gui, placeholders);
					String playerName = player.getName();
					UUID uuid = player.getUniqueId();

					for (String command : commands) {
						String value = Utils.replacePlaceholders(command, values, uuid);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
								Utils.replaceLiteral(value, "{player}", playerName));
					}
				}

				@Override
				public boolean shouldSync() {
					return true;
				}
			});

		if (!messages.isEmpty())
			actions.add(0, (gui, player, sharedData, placeholders) -> {
				Map<String, Object> values = ensureRenamePlaceholder(gui, placeholders);
				UUID uuid = player.getUniqueId();

				for (String message : messages)
					player.sendMessage(Utils.replacePlaceholders(message, values, uuid));
			});

		if (economy) {
			final String deposit = config.getString(path + ".economy.deposit");
			final String withdraw = config.getString(path + ".economy.withdraw");

			final boolean hasDeposit = deposit != null && !deposit.isEmpty();
			final boolean hasWithdraw = withdraw != null && !withdraw.isEmpty();

			if (hasDeposit || hasWithdraw) {
				final boolean dynamicDeposit = hasDeposit && Utils.checkForPlaceholders(deposit);
				final boolean dynamicWithdraw = hasWithdraw && Utils.checkForPlaceholders(withdraw);

				final double staticDeposit = hasDeposit && !dynamicDeposit ? ParseUtils.getDouble(deposit) : 0;
				final double staticWithdraw = hasWithdraw && !dynamicWithdraw ? ParseUtils.getDouble(withdraw) : 0;

				actions.add(0, (gui, player, sharedData, placeholders) -> {
					Map<String, Object> values = ensureRenamePlaceholder(gui, placeholders);
					UUID uuid = player.getUniqueId();
					String playerName = player.getName();
					String worldName = player.getWorld().getName();

					if (hasDeposit) {
						double value = dynamicDeposit
								? ParseUtils.getDouble(Utils.replacePlaceholders(deposit, values, uuid))
								: staticDeposit;

						BukkitLoader.getEconomyHook().deposit(playerName, worldName, value);
					}

					if (hasWithdraw) {
						double value = dynamicWithdraw
								? ParseUtils.getDouble(Utils.replacePlaceholders(withdraw, values, uuid))
								: staticWithdraw;

						BukkitLoader.getEconomyHook().withdraw(playerName, worldName, value);
					}
				});
			}
		}

		return actions;
	}

	private ItemGUI createDynamicItem(final ItemPackage itemPackage, final AnvilGUI gui, Player player, Config data) {
		Map<String, Object> placeholders = renamePlaceholders(gui);
		ItemStack item = Utils.applyPlaceholders(itemPackage.getTypePlaceholder(), itemPackage.getItem(), placeholders,
				player);

		return createDynamicItem(itemPackage, item, data);
	}

	private ItemGUI createDynamicItem(final ItemPackage itemPackage, ItemStack item, final Config data) {
		return new ItemGUI(item) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				runActions(itemPackage.getActions(), gui, player, data,
						gui instanceof AnvilGUI ? renamePlaceholders((AnvilGUI) gui) : EMPTY);
			}
		};
	}

	private static ItemPackage emptyPackage(int pos) {
		return new ItemPackage(null, null, pos, Collections.<Action>emptyList());
	}

	private static Map<String, Object> renamePlaceholders(AnvilGUI gui) {
		Map<String, Object> result = new HashMap<>(2);
		result.put("renameText", gui.getRenameText());
		return result;
	}

	private static Map<String, Object> ensureRenamePlaceholder(HolderGUI gui, Map<String, Object> placeholders) {
		String renameText = gui instanceof AnvilGUI ? ((AnvilGUI) gui).getRenameText() : "";

		if (placeholders != null && renameText.equals(placeholders.get("renameText")))
			return placeholders;

		Map<String, Object> result = new HashMap<>(placeholders == null ? 2 : Math.max(2, placeholders.size() + 1));

		if (placeholders != null && !placeholders.isEmpty())
			result.putAll(placeholders);

		result.put("renameText", renameText);
		return result;
	}

	private static void runActions(List<Action> actions, HolderGUI gui, Player player, Config data,
			Map<String, Object> placeholders) {

		if (actions == null || actions.isEmpty())
			return;

		for (int i = 0; i < actions.size(); ++i) {
			Action action = actions.get(i);

			if (action.shouldSync()) {
				action.runSync(i + 1, actions, gui, player, data, placeholders);
				return;
			}

			action.run(gui, player, data, placeholders);
		}
	}

	@Override
	public Map<String, List<Action>> getCustomActions() {
		return customActions;
	}

	@Override
	public Map<EventType, List<Action>> getEventActions() {
		return eventActions;
	}
}