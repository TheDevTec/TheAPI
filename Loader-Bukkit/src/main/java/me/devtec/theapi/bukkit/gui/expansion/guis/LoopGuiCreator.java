package me.devtec.theapi.bukkit.gui.expansion.guis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import me.devtec.theapi.bukkit.gui.GUI;
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
import me.devtec.theapi.bukkit.gui.expansion.loop.LoopManager;
import me.devtec.theapi.bukkit.gui.expansion.loop.ResultItemCallable;
import me.devtec.theapi.bukkit.gui.expansion.utils.Utils;

public class LoopGuiCreator implements GuiCreator {

	private static final Map<String, Object> EMPTY = Collections.emptyMap();

	@Getter
	final String id;
	@Getter
	final Config config;

	String title;
	String staticTitle;
	boolean dynamicTitle;
	int size;

	final Map<Integer, ItemGUI> staticItems = new HashMap<>();
	final Map<Character, ItemPackage> dynamicItems = new HashMap<>();
	final Map<Character, ConditionItem> conditionItems = new HashMap<>();

	final Map<EventType, List<Action>> eventActions = new EnumMap<>(EventType.class);
	final Map<String, List<Action>> customActions = new HashMap<>();
	final List<Task> schedulers = new ArrayList<>();

	final List<Integer> insertSlots = new ArrayList<>();
	final List<ConditionItem> slotItemWithConditions = new ArrayList<>();

	final Map<UUID, GUI> activeGuis = new HashMap<>();

	ItemPackage defaultSlotItem;

	char nextButtonChar;
	ConditionItem nextButton;
	char previousButtonChar;
	ConditionItem previousButton;

	ResultItemCallable callable;

	public LoopGuiCreator(String id, Config config) {
		this.id = id;
		this.config = config;
		reload();
	}

	@Override
	public HolderGUI open(Player player) {
		return openInitial(player, 1);
	}

	public HolderGUI open(Player player, int page) {
		return openInitial(player, Math.max(1, page));
	}

	private HolderGUI openInitial(Player player, int page) {
		UUID uuid = player.getUniqueId();
		Config data = sharedData.computeIfAbsent(uuid, key -> new Config());

		data.set("page", page).set("totalPages", page);

		runActions(eventActions.get(EventType.BEFORE_OPEN_MENU), null, player, data, EMPTY);

		List<ItemGUI> items = createLoopItems(player, data);
		GUI gui = openPage(player, page, items, data);

		runActions(eventActions.get(EventType.OPEN_MENU), gui, player, data, EMPTY);
		return gui;
	}

	private GUI openPage(final Player player, int requiredPage, final List<ItemGUI> itemGuis, final Config data) {
		final UUID uuid = player.getUniqueId();
		final int totalPages = totalPages(itemGuis.size());
		final int page = requiredPage > totalPages ? totalPages : requiredPage < 1 ? 1 : requiredPage;

		data.set("page", page).set("totalPages", totalPages);

		final List<Integer> schedulerIds = schedulers.isEmpty()
				? Collections.<Integer>emptyList()
						: new ArrayList<>(schedulers.size());

		final GUI gui = new GUI(buildTitle(player, page, totalPages), size) {

			private boolean schedulersCancelled;

			private void cancelSchedulers() {
				if (schedulersCancelled)
					return;

				schedulersCancelled = true;

				for (int id : schedulerIds)
					Scheduler.cancelTask(id);
			}

			@Override
			public void onPreClose(Player player) {
				cancelSchedulers();
			}

			@Override
			public void onClose(Player player) {
				cancelSchedulers();

				if (activeGuis.get(uuid) != this)
					return;

				runActions(eventActions.get(EventType.CLOSE_MENU), this, player, data, EMPTY);

				activeGuis.remove(uuid);

				if (sharedData.get(uuid) == data)
					sharedData.remove(uuid);
			}
		};

		for (final Task task : schedulers) {
			final List<Action> taskActions = task.getActions();

			schedulerIds.add(new Tasker() {
				@Override
				public void run() {
					if (activeGuis.get(uuid) != gui)
						return;

					for (char itemId : task.getItems())
						if (!updateItem(gui, player, data, itemId))
							return;

					runActions(taskActions, gui, player, data, EMPTY);
				}
			}.runRepeating(task.getTime(), task.getTime()));
		}

		for (Entry<Integer, ItemGUI> entry : staticItems.entrySet())
			gui.setItem(entry.getKey(), entry.getValue());

		for (Entry<Character, ItemPackage> entry : dynamicItems.entrySet()) {
			ItemPackage itemPackage = entry.getValue();
			ItemGUI item = createDynamicItem(itemPackage, player, data);

			for (int slot : itemPackage.getSlots())
				gui.setItem(slot, item);
		}

		for (Entry<Character, ConditionItem> entry : conditionItems.entrySet()) {
			ConditionItem conditionItem = entry.getValue();
			ItemPackage itemPackage = conditionItem.test(player, data, EMPTY);

			if (itemPackage == null || itemPackage.getItem() == null)
				continue;

			ItemGUI item;

			if (itemPackage instanceof StaticItemPackage)
				item = ((StaticItemPackage) itemPackage).getItemGui();
			else
				item = createDynamicItem(itemPackage, player, data);

			for (int slot : conditionItem.getSlots())
				gui.setItem(slot, item);
		}

		applyLoopItems(gui, itemGuis, page);
		applyPageButtons(gui, player, data, page, totalPages, itemGuis);

		activeGuis.put(uuid, gui);
		gui.open(player);

		return gui;
	}

	@Override
	public void updateItem(HolderGUI gui, Player player, char itemId) {
		Config data = sharedData.get(player.getUniqueId());

		if (data != null)
			updateItem(gui, player, data, itemId);
	}

	private boolean updateItem(HolderGUI gui, Player player, Config data, char itemId) {
		if (itemId == '#') {
			List<ItemGUI> itemGuis = createLoopItems(player, data);
			int totalPages = totalPages(itemGuis.size());
			int page = data.getInt("page");

			if (page < 1)
				page = 1;

			if (page > totalPages) {
				openPage(player, totalPages, itemGuis, data);
				return false;
			}

			int oldTotalPages = data.getInt("totalPages");

			if (oldTotalPages != totalPages) {
				data.set("totalPages", totalPages);
				gui.setTitle(buildTitle(player, page, totalPages));

				if (gui instanceof GUI)
					applyPageButtons((GUI) gui, player, data, page, totalPages, itemGuis);
			}

			if (gui instanceof GUI)
				applyLoopItems((GUI) gui, itemGuis, page);

			return true;
		}

		ItemPackage item = dynamicItems.get(itemId);

		if (item != null && !item.getSlots().isEmpty()) {
			ItemStack newItem = Utils.applyPlaceholders(item.getTypePlaceholder(), item.getItem(), player);
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
			return true;

		final ItemPackage packageItem = conditionItem.test(player, data, EMPTY);

		if (packageItem == null || packageItem.getItem() == null) {
			for (int slot : conditionItem.getSlots())
				gui.remove(slot);

			return true;
		}

		if (packageItem instanceof StaticItemPackage) {
			ItemGUI itemGui = ((StaticItemPackage) packageItem).getItemGui();

			for (int slot : conditionItem.getSlots())
				gui.setItem(slot, itemGui);

			return true;
		}

		ItemGUI itemGui = createDynamicItem(packageItem, player, data);

		for (int slot : conditionItem.getSlots())
			gui.setItem(slot, itemGui);

		return true;
	}

	private void applyLoopItems(GUI gui, List<ItemGUI> itemGuis, int page) {
		if (insertSlots.isEmpty())
			return;

		int slots = insertSlots.size();
		int start = (page - 1) * slots;
		int end = Math.min(start + slots, itemGuis.size());
		int pos = 0;

		for (int i = start; i < end; ++i)
			gui.setItem(insertSlots.get(pos++), itemGuis.get(i));

		for (; pos < slots; ++pos)
			gui.remove(insertSlots.get(pos));
	}

	private void applyPageButtons(final GUI gui, final Player player, final Config data, final int page,
			final int totalPages, final List<ItemGUI> itemGuis) {

		if (previousButton != null)
			if (page > 1)
				setPageButton(gui, player, data, previousButton, previousButton.getHas(), page, totalPages, page - 1, itemGuis);
			else
				setPageButton(gui, player, data, previousButton, previousButton.getNot(), page, totalPages, -1, itemGuis);

		if (nextButton != null)
			if (page < totalPages)
				setPageButton(gui, player, data, nextButton, nextButton.getHas(), page, totalPages, page + 1, itemGuis);
			else
				setPageButton(gui, player, data, nextButton, nextButton.getNot(), page, totalPages, -1, itemGuis);
	}

	private void setPageButton(final GUI gui, final Player player, final Config data, ConditionItem button,
			final ItemPackage itemPackage, int page, int totalPages, final int targetPage,
			final List<ItemGUI> itemGuis) {

		if (itemPackage == null || itemPackage.getItem() == null) {
			for (int slot : button.getSlots())
				gui.remove(slot);

			return;
		}

		ItemMaker maker = replacePage(itemPackage.getItem(), page, totalPages);
		ItemStack stack = Utils.applyPlaceholders(itemPackage.getTypePlaceholder(), maker, player);

		ItemGUI item = new ItemGUI(stack) {
			@Override
			public void onClick(Player player, HolderGUI holder, ClickType click) {
				runActions(itemPackage.getActions(), holder, player, data, EMPTY);

				if (targetPage != -1)
					openPage(player, targetPage, itemGuis, data);
			}
		};

		for (int slot : button.getSlots())
			gui.setItem(slot, item);
	}

	private List<ItemGUI> createLoopItems(Player player, Config data) {
		if (callable == null)
			return Collections.emptyList();

		List<ItemGUI> result = callable.callLoop(this, player, data, slotItemWithConditions, defaultSlotItem);
		return result == null ? Collections.<ItemGUI>emptyList() : result;
	}

	private int totalPages(int itemCount) {
		if (insertSlots.isEmpty())
			return 1;

		int perPage = insertSlots.size();
		return Math.max(1, (itemCount + perPage - 1) / perPage);
	}

	private String buildTitle(Player player, int page, int totalPages) {
		String result = replacePagePlaceholders(dynamicTitle ? title : staticTitle, page, totalPages);

		if (dynamicTitle)
			result = Utils.replacePlaceholders(result, null, player.getUniqueId());

		return result;
	}

	private String replacePagePlaceholders(String input, int page, int totalPages) {
		input = Utils.replaceLiteral(input, "{page}", String.valueOf(page));
		input = Utils.replaceLiteral(input, "{totalPages}", String.valueOf(totalPages));
		input = Utils.replaceLiteral(input, "{previousPage}", String.valueOf(page - 1));
		return Utils.replaceLiteral(input, "{nextPage}", String.valueOf(page + 1));
	}

	private ItemMaker replacePage(ItemMaker item, int page, int totalPages) {
		if (item.getDisplayName() != null)
			item.displayName(replacePagePlaceholders(item.getDisplayName(), page, totalPages));

		if (item.getLore() != null) {
			List<String> lore = item.getLore();

			for (int i = 0; i < lore.size(); ++i)
				lore.set(i, replacePagePlaceholders(lore.get(i), page, totalPages));
		}

		return item;
	}

	@Override
	public void reload() {
		eventActions.clear();
		customActions.clear();
		staticItems.clear();
		dynamicItems.clear();
		conditionItems.clear();
		schedulers.clear();

		insertSlots.clear();
		slotItemWithConditions.clear();

		defaultSlotItem = null;

		nextButtonChar = 0;
		nextButton = null;
		previousButtonChar = 0;
		previousButton = null;

		String loop = config.getString("loop.value");
		callable = loop == null ? null : LoopManager.createByName(loop);

		if (callable == null)
			warn("Failed to find loop '" + loop + "' in gui " + config.getFile().getName());

		List<String> lines = config.getStringList("lines");

		size = Math.min(54, Math.max(9, lines.size() * 9));
		title = config.getString("title", "NOT_SET");

		String titleCheck = replacePagePlaceholders(title, 1, 1);
		dynamicTitle = Utils.checkForPlaceholders(titleCheck);
		staticTitle = dynamicTitle ? null : ColorUtils.colorize(title);

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

			schedulers.add(new Task(
					items,
					Utils.createActions(this, config.getStringList("scheduler." + scheduler + ".actions")),
					config.getLong("scheduler." + scheduler + ".time")));
		}

		for (String actionName : config.getKeys("customActions"))
			customActions.put(actionName, createConfiguredActions("customActions." + actionName, true));

		for (String key : config.getKeys("loop.item.result.conditions")) {
			String path = "loop.item.result.conditions." + key;
			ItemPackage has = createItemPackage(path, 0);

			if (has == null)
				continue;

			slotItemWithConditions.add(new ConditionItem(
					Utils.createConditions(config.getStringList(path + ".check")),
					0, has, null));
		}

		if (config.existsKey("loop.item.result.type"))
			defaultSlotItem = createItemPackage("loop.item.result", 0);

		Map<Character, ItemGUI> staticCache = new HashMap<>();
		int pos = -1;

		layout:
			for (String line : lines)
				for (int i = 0; i < line.length(); ++i) {
					if (++pos >= size)
						break layout;

					char c = line.charAt(i);

					if (c == ' ')
						continue;

					if (c == '#') {
						insertSlots.add(pos);
						continue;
					}

					if (nextButtonChar != 0 && nextButtonChar == c) {
						nextButton.addSlot(pos);
						continue;
					}

					if (previousButtonChar != 0 && previousButtonChar == c) {
						previousButton.addSlot(pos);
						continue;
					}

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

					ConditionItem conditionItem = conditionItems.get(c);

					if (conditionItem != null) {
						conditionItem.addSlot(pos);
						continue;
					}

					String itemPath = "items." + c;

					if (config.existsKey(itemPath + ".conditions")) {
						ItemPackage has = config.exists(itemPath + ".has")
								? createItemPackage(itemPath + ".has", pos)
										: emptyPackage(pos);

						ItemPackage not = config.exists(itemPath + ".not")
								? createItemPackage(itemPath + ".not", pos)
										: emptyPackage(pos);

						if (has == null)
							has = emptyPackage(pos);

						if (not == null)
							not = emptyPackage(pos);

						List<Condition> conditions = Utils.createConditions(config.getStringList(itemPath + ".conditions"));
						conditionItems.put(c, new ConditionItem(conditions, pos, has, not));
						continue;
					}

					String loopItemPath = "loop.item." + c;

					if (config.existsKey(loopItemPath + ".action")) {
						ItemPackage has = config.exists(loopItemPath + ".available")
								? createItemPackage(loopItemPath + ".available", pos)
										: emptyPackage(pos);

						ItemPackage not = config.exists(loopItemPath + ".unavailable")
								? createItemPackage(loopItemPath + ".unavailable", pos)
										: emptyPackage(pos);

						if (has == null)
							has = emptyPackage(pos);

						if (not == null)
							not = emptyPackage(pos);

						String action = config.getString(loopItemPath + ".action");

						if (action != null)
							switch (action.toLowerCase(Locale.ROOT)) {
							case "next_page":
								nextButtonChar = c;
								nextButton = new ConditionItem(Collections.<Condition>emptyList(), pos, has, not);
								break;

							case "previous_page":
								previousButtonChar = c;
								previousButton = new ConditionItem(Collections.<Condition>emptyList(), pos, has, not);
								break;

							default:
								warn("Unknown loop action '" + action + "' for item " + c + " in gui "
										+ config.getFile().getName());
								break;
							}

						continue;
					}

					ItemPackage itemPackage = createItemPackage(itemPath, pos);

					if (itemPackage == null) {
						warn("Failed to find item " + c + " in the gui " + config.getFile().getName());
						continue;
					}

					if (itemPackage instanceof StaticItemPackage) {
						ItemGUI itemGui = ((StaticItemPackage) itemPackage).getItemGui();

						staticCache.put(c, itemGui);
						staticItems.put(pos, itemGui);
					} else
						dynamicItems.put(c, itemPackage);
				}

		if (insertSlots.isEmpty())
			warn("Loop gui " + config.getFile().getName() + " doesn't contain any '#' insert slots");
	}

	private void loadEvent(EventType type, String path) {
		if (config.existsKey(path))
			eventActions.put(type, Utils.createActions(this, config.getStringList(path)));
	}

	private ItemPackage createItemPackage(String path, int pos) {
		ItemMaker maker = ItemMaker.loadMakerFromConfig(config, path);

		if (maker == null) {
			warn("Failed to load item at " + path + " in " + config.getFile().getName());
			return null;
		}

		final List<Action> actions = createConfiguredActions(path + ".click", false);
		String typePlaceholder = config.getString(path + ".type");

		if (Utils.checkForPlaceholders(maker)
				|| typePlaceholder != null && Utils.checkForPlaceholders(typePlaceholder))
			return new ItemPackage(typePlaceholder, maker, pos, actions);

		ItemGUI itemGui = new ItemGUI(maker.build()) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				runActions(actions, gui, player, sharedData.get(player.getUniqueId()), EMPTY);
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
					UUID uuid = player.getUniqueId();
					String playerName = player.getName();

					for (String command : commands) {
						String value = Utils.replacePlaceholders(command, placeholders, uuid);
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
				UUID uuid = player.getUniqueId();

				for (String message : messages)
					player.sendMessage(Utils.replacePlaceholders(message, placeholders, uuid));
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
					UUID uuid = player.getUniqueId();
					String playerName = player.getName();
					String worldName = player.getWorld().getName();

					if (hasDeposit) {
						double value = dynamicDeposit
								? ParseUtils.getDouble(Utils.replacePlaceholders(deposit, placeholders, uuid))
										: staticDeposit;

						BukkitLoader.getEconomyHook().deposit(playerName, worldName, value);
					}

					if (hasWithdraw) {
						double value = dynamicWithdraw
								? ParseUtils.getDouble(Utils.replacePlaceholders(withdraw, placeholders, uuid))
										: staticWithdraw;

						BukkitLoader.getEconomyHook().withdraw(playerName, worldName, value);
					}
				});
			}
		}

		return actions;
	}

	private ItemGUI createDynamicItem(final ItemPackage itemPackage, Player player, final Config data) {
		return createDynamicItem(
				itemPackage,
				Utils.applyPlaceholders(itemPackage.getTypePlaceholder(), itemPackage.getItem(), player),
				data);
	}

	private ItemGUI createDynamicItem(final ItemPackage itemPackage, ItemStack item, final Config data) {
		return new ItemGUI(item) {
			@Override
			public void onClick(Player player, HolderGUI gui, ClickType click) {
				runActions(itemPackage.getActions(), gui, player, data, EMPTY);
			}
		};
	}

	private static ItemPackage emptyPackage(int slot) {
		return new ItemPackage(null, null, slot, Collections.<Action>emptyList());
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

	private static void warn(String message) {
		BukkitLoader.getPlugin(BukkitLoader.class).getLogger().warning("[GuiExpansion] " + message);
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