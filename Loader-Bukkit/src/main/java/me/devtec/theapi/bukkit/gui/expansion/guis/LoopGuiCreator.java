package me.devtec.theapi.bukkit.gui.expansion.guis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	@Getter
	final String id;
	@Getter
	final Config config;
	String title;
	int size;
	Map<Integer, ItemGUI> staticItems = new HashMap<>();
	Map<Character, ItemPackage> dynamicItems = new HashMap<>();
	Map<Character, ConditionItem> conditionItems = new HashMap<>();

	Map<EventType, List<Action>> eventActions = new HashMap<>();
	Map<String, List<Action>> customActions = new HashMap<>();
	List<Task> schedulers = new ArrayList<>();
	// loop
	List<Integer> insertSlots = new ArrayList<>();

	List<ConditionItem> slotItemWithConditions = new ArrayList<>();
	ItemPackage defaultSlotItem;

	char nextButtonChar;
	ConditionItem nextButton;
	char previousButtonChar;
	ConditionItem previousButton;

	ResultItemCallable callable;

	public LoopGuiCreator(String id, Config config) {
		this.id=id;
		this.config = config;
		reload();
	}

	@Override
	public HolderGUI open(Player player) {
		sharedData.computeIfAbsent(player.getUniqueId(), i -> new Config()).set("page", 1).set("totalPages", 1);
		List<Action> actions = eventActions.get(EventType.BEFORE_OPEN_MENU);
		if (actions != null) {
			int pos = 0;
			for (Action action : actions) {
				if (action.shouldSync()) {
					action.runSync(++pos, actions, null, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
					break;
				}
				action.run(null, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
				++pos;
			}
		}
		GUI gui = open(player, 1, callable.callLoop(this, player, sharedData.get(player.getUniqueId()), slotItemWithConditions, defaultSlotItem));
		actions = eventActions.get(EventType.OPEN_MENU);
		if (actions != null) {
			int pos = 0;
			for (Action action : actions) {
				if (action.shouldSync()) {
					action.runSync(++pos, actions, gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
					break;
				}
				action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
				++pos;
			}
		}
		return gui;
	}

	public HolderGUI open(Player player, int page) {
		sharedData.computeIfAbsent(player.getUniqueId(), i -> new Config()).set("page", page).set("totalPages", page);
		List<Action> actions = eventActions.get(EventType.BEFORE_OPEN_MENU);
		if (actions != null) {
			int pos = 0;
			for (Action action : actions) {
				if (action.shouldSync()) {
					action.runSync(++pos, actions, null, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
					break;
				}
				action.run(null, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
				++pos;
			}
		}
		GUI gui = open(player, page, callable.callLoop(this, player, sharedData.get(player.getUniqueId()), slotItemWithConditions, defaultSlotItem));
		actions = eventActions.get(EventType.OPEN_MENU);
		if (actions != null) {
			int pos = 0;
			for (Action action : actions) {
				if (action.shouldSync()) {
					action.runSync(++pos, actions, gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
					break;
				}
				action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
				++pos;
			}
		}
		return gui;
	}

	private String replacePagePlaceholders(String input, int page, int totalPages) {
		return input.replace("{page}", page + "").replace("{totalPages}", totalPages + "")
				.replace("{previousPage}", page - 1 + "").replace("{nextPage}", page + 1 + "");
	}

	private GUI open(Player player, int requiredPage, List<ItemGUI> itemGuis) {
		int totalPages = Math.max(1,
				itemGuis.size() / insertSlots.size() + (itemGuis.size() % insertSlots.size() == 0 ? 0 : 1));
		int page = requiredPage>totalPages ? totalPages : requiredPage < 1 ? 1 : requiredPage;

		sharedData.computeIfAbsent(player.getUniqueId(), i -> new Config()).set("page", page).set("totalPages",
				totalPages);
		List<Integer> schedulersIds = schedulers.isEmpty() ? Collections.emptyList() : new ArrayList<>();
		GUI gui = new GUI(
				Utils.replacePlaceholders(replacePagePlaceholders(title, page, totalPages), null, player.getUniqueId()),
				size) {
			@Override
			public void onPreClose(Player player) {
				sharedData.remove(player.getUniqueId());
				for (int i : schedulersIds)
					Scheduler.cancelTask(i);
			}

			@Override
			public void onClose(Player player) {
				for (int i : schedulersIds)
					Scheduler.cancelTask(i);
				List<Action> actions = eventActions.get(EventType.CLOSE_MENU);
				if (actions != null) {
					int pos = 0;
					for (Action action : actions) {
						if (action.shouldSync()) {
							action.runSync(++pos, actions, this, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
							break;
						}
						action.run(this, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
						++pos;
					}
				}
			}
		};
		for (Task task : schedulers)
			schedulersIds.add(new Tasker() {

				@Override
				public void run() {
					for (char itemId : task.getItems())
						updateItem(gui, player, itemId);
					int pos = 0;
					for (Action action : task.getActions()) {
						if (action.shouldSync()) {
							action.runSync(++pos, task.getActions(), gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
							break;
						}
						action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
						++pos;
					}
				}
			}.runRepeating(task.getTime(), task.getTime()));
		for (Entry<Integer, ItemGUI> staticItem : staticItems.entrySet())
			gui.setItem(staticItem.getKey(), staticItem.getValue());
		for (Entry<Character, ItemPackage> dynamicItem : dynamicItems.entrySet()) {
			ItemGUI item = new ItemGUI(Utils.applyPlaceholders(dynamicItem.getValue().getTypePlaceholder(),
					dynamicItem.getValue().getItem(), player)) {

				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
					dynamicItem.getValue().runActions(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
				}
			};
			for (int slot : dynamicItem.getValue().getSlots())
				gui.setItem(slot, item);
		}
		for (Entry<Character, ConditionItem> conditionItem : conditionItems.entrySet()) {
			ItemPackage itemPackage = conditionItem.getValue().test(player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
			if (itemPackage.getItem() != null) {
				if (itemPackage instanceof StaticItemPackage) {
					StaticItemPackage staticPackage = (StaticItemPackage) itemPackage;
					for (int slot : conditionItem.getValue().getSlots())
						gui.setItem(slot, staticPackage.getItemGui());
					continue;
				}
				ItemGUI item = new ItemGUI(
						Utils.applyPlaceholders(itemPackage.getTypePlaceholder(), itemPackage.getItem(), player)) {

					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						itemPackage.runActions(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
					}
				};
				for (int slot : conditionItem.getValue().getSlots())
					gui.setItem(slot, item);
			}
		}
		// Now loop items
		int pos = 0;
		for (int i = page * insertSlots.size() - insertSlots.size(); i < page * insertSlots.size()
				&& i < itemGuis.size(); ++i)
			gui.setItem(insertSlots.get(pos++), itemGuis.get(i));
		ItemMaker maker;
		if (page > 1) {
			if ((maker = previousButton.getHas().getItem()) != null) {
				ItemGUI item = new ItemGUI(Utils.applyPlaceholders(previousButton.getHas().getTypePlaceholder(),
						replacePage(maker, page, totalPages), player)) {

					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						previousButton.getHas().runActions(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
						open(player, page - 1, itemGuis);
					}
				};
				for (int slot : previousButton.getSlots())
					gui.setItem(slot, item);
			}
		} else if ((maker = previousButton.getNot().getItem()) != null) {
			ItemGUI item = new ItemGUI(Utils.applyPlaceholders(previousButton.getNot().getTypePlaceholder(),
					replacePage(maker, page, totalPages), player)) {

				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
					previousButton.getNot().runActions(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
				}
			};
			for (int slot : previousButton.getSlots())
				gui.setItem(slot, item);
		}
		maker = null;
		if (page + 1 < totalPages) {
			if ((maker = nextButton.getHas().getItem()) != null) {
				ItemGUI item = new ItemGUI(Utils.applyPlaceholders(nextButton.getHas().getTypePlaceholder(),
						replacePage(maker, page, totalPages), player)) {

					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						nextButton.getHas().runActions(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
						open(player, page + 1, itemGuis);
					}
				};
				for (int slot : nextButton.getSlots())
					gui.setItem(slot, item);
			}
		} else if ((maker = nextButton.getNot().getItem()) != null) {
			ItemGUI item = new ItemGUI(Utils.applyPlaceholders(nextButton.getNot().getTypePlaceholder(),
					replacePage(maker, page, totalPages), player)) {

				@Override
				public void onClick(Player player, HolderGUI gui, ClickType click) {
					nextButton.getNot().runActions(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
				}
			};
			for (int slot : nextButton.getSlots())
				gui.setItem(slot, item);
		}
		gui.open(player);
		return gui;
	}

	@Override
	public void updateItem(HolderGUI gui, Player player, char itemId) {
		if (itemId == '#') {
			List<ItemGUI> itemGuis = callable.callLoop(this, player, sharedData.get(player.getUniqueId()), slotItemWithConditions, defaultSlotItem);
			Config data = sharedData.computeIfAbsent(player.getUniqueId(), i -> new Config());
			int totalPages = Math.max(1,
					itemGuis.size() / insertSlots.size() + (itemGuis.size() % insertSlots.size() == 0 ? 0 : 1));
			int page = data.getInt("page");
			if (page > totalPages)
				open(player, totalPages, itemGuis);
			else if (totalPages != data.getInt("totalPages")) {
				data.set("totalPages", totalPages);
				gui.setTitle(Utils.replacePlaceholders(replacePagePlaceholders(title, page, totalPages), null,
						player.getUniqueId()));
			}
			int pos = 0;
			for (int i = page * insertSlots.size() - insertSlots.size(); i < page * insertSlots.size()
					&& i < itemGuis.size(); ++i)
				gui.setItem(insertSlots.get(pos++), itemGuis.get(i));
			for (; pos < insertSlots.size(); ++pos)
				gui.remove(insertSlots.get(pos));
			return;
		}
		ItemPackage item = dynamicItems.get(itemId);
		if (item != null) {
			ItemStack newItem = Utils.applyPlaceholders(item.getTypePlaceholder(), item.getItem(), player);
			ItemGUI iGui = gui.getItemGUI(item.getSlots().get(0));
			iGui.setItem(newItem);
			for (int slot : item.getSlots())
				gui.setItem(slot, iGui);
		}
		ConditionItem conditionItem = conditionItems.get(itemId);
		if (conditionItem != null) {
			ItemPackage packageItem = conditionItem.test(player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
			if (packageItem.getItem() == null)
				for (int slot : conditionItem.getSlots())
					gui.remove(slot);
			else {
				// We have to create new itemgui
				if (packageItem instanceof StaticItemPackage) {
					StaticItemPackage staticPackage = (StaticItemPackage) packageItem;
					for (int slot : conditionItem.getSlots())
						gui.setItem(slot, staticPackage.getItemGui());
					return;
				}
				ItemStack newItem = Utils.applyPlaceholders(packageItem.getTypePlaceholder(), packageItem.getItem(),
						player);
				ItemGUI iGui = gui.getItemGUI(conditionItem.getSlots().get(0));
				if (iGui == null)
					// We have to create new itemgui
					iGui = new ItemGUI(newItem) {

					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						packageItem.runActions(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
					}
				};
				else
					iGui.setItem(newItem);
				for (int slot : conditionItem.getSlots())
					gui.setItem(slot, iGui);
			}
		}
	}

	private ItemMaker replacePage(ItemMaker item, int page, int totalPages) {
		if (item.getDisplayName() != null)
			item.displayName(replacePagePlaceholders(item.getDisplayName(), page, totalPages));
		if (item.getLore() != null)
			item.getLore().replaceAll(input -> replacePagePlaceholders(input, page, totalPages));
		return item;
	}

	@Override
	public void reload() {
		eventActions.clear();
		customActions.clear();
		this.staticItems.clear();
		this.dynamicItems.clear();
		insertSlots.clear();
		slotItemWithConditions.clear();
		nextButtonChar = 0;
		previousButtonChar = 0;
		callable = LoopManager.createByName(config.getString("loop.value"));
		List<String> lines = config.getStringList("lines");
		size = Math.min(54, Math.max(9, lines.size() * 9));
		title = config.getString("title", "NOT_SET");
		if (config.exists("events")) {
			if (config.existsKey("events.before_open_menu"))
				eventActions.put(EventType.BEFORE_OPEN_MENU,
						Utils.createActions(this, config.getStringList("events.before_open_menu")));
			if (config.existsKey("events.open_menu"))
				eventActions.put(EventType.OPEN_MENU,
						Utils.createActions(this, config.getStringList("events.open_menu")));
			if (config.existsKey("events.close_menu"))
				eventActions.put(EventType.CLOSE_MENU,
						Utils.createActions(this, config.getStringList("events.close_menu")));
		}
		for (String scheduler : config.getKeys("scheduler")) {
			List<Character> items = new ArrayList<>();
			for (String item : config.getStringList("scheduler." + scheduler + ".items"))
				items.add(item.charAt(0));
			if (items.isEmpty())
				continue;
			schedulers.add(new Task(items,
					Utils.createActions(this, config.getStringList("scheduler." + scheduler + ".actions")),
					config.getLong("scheduler." + scheduler + ".time")));
		}
		for (String actionName : config.getKeys("customActions")) {
			List<Action> actions = Utils.createActions(this,
					config.getStringList("customActions." + actionName + ".actions"));
			List<String> messages = config.getStringList("customActions." + actionName + ".messages");
			List<String> commands = config.getStringList("customActions." + actionName + ".commands");
			String economyDeposit = config.getString("customActions." + actionName + ".economy.deposit");
			String economyWithdraw = config.getString("customActions." + actionName + ".economy.withdraw");
			if (!commands.isEmpty())
				actions.add(0, new Action() {
					@Override
					public void run(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
						for (String command : commands) {
							String finalCommand = Utils.replacePlaceholders(command, Collections.emptyMap(), player.getUniqueId())
									.replace("{player}", player.getName());
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
						}
					}

					@Override
					public boolean shouldSync() {
						return true;
					}
				});
			if (!messages.isEmpty())
				actions.add(0,(gui, player, sharedData, placeholders) -> {
					for (String message : messages)
						player.sendMessage(ColorUtils
								.colorize(Utils.replacePlaceholders(message, placeholders, player.getUniqueId())));
				});
			if (economyDeposit != null && !economyDeposit.isEmpty()
					|| economyWithdraw != null && !economyWithdraw.isEmpty())
				actions.add(0,(gui, player, sharedData, placeholders) -> {
					if (economyDeposit != null && !economyDeposit.isEmpty())
						BukkitLoader.getEconomyHook().deposit(player.getName(), player.getWorld().getName(), ParseUtils.getDouble(
								Utils.replacePlaceholders(economyDeposit, placeholders, player.getUniqueId())));
					if (economyWithdraw != null && !economyWithdraw.isEmpty())
						BukkitLoader.getEconomyHook().withdraw(player.getName(), player.getWorld().getName(), ParseUtils.getDouble(
								Utils.replacePlaceholders(economyWithdraw, placeholders, player.getUniqueId())));
				});
			customActions.put(actionName, actions);
		}
		for (String key : config.getKeys("loop.item.result.conditions")) {
			ItemMaker maker = ItemMaker.loadMakerFromConfig(config, "loop.item.result.conditions." + key);
			List<String> messages = config.getStringList("loop.item.result.conditions." + key + ".click.messages");
			List<String> commands = config.getStringList("loop.item.result.conditions." + key + ".click.commands");
			List<Action> actions = Utils.createActions(this,
					config.getStringList("loop.item.result.conditions." + key + ".click.actions"));
			if (!commands.isEmpty())
				actions.add(0, new Action() {
					@Override
					public void run(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
						for (String command : commands) {
							String finalCommand = Utils.replacePlaceholders(command, placeholders, player.getUniqueId())
									.replace("{player}", player.getName());
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
						}
					}

					@Override
					public boolean shouldSync() {
						return true;
					}
				});
			if (!messages.isEmpty())
				actions.add(0,(gui, player, sharedData, placeholders) -> {
					for (String message : messages)
						player.sendMessage(ColorUtils
								.colorize(Utils.replacePlaceholders(message, placeholders, player.getUniqueId())));
				});
			String typePlaceholder = config.getString("loop.item.result.conditions." + key + ".type");
			ItemPackage has;
			if (Utils.checkForPlaceholders(maker) || Utils.checkForPlaceholders(typePlaceholder))
				has = new ItemPackage(typePlaceholder, maker, 0, actions);
			else
				has = new StaticItemPackage(typePlaceholder, new ItemGUI(maker.build()) {

					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						int pos = 0;
						for (Action action : actions) {
							if (action.shouldSync()) {
								action.runSync(++pos, actions, gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
								break;
							}
							action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
							++pos;
						}
					}
				}, maker, 0, actions);
			slotItemWithConditions.add(new ConditionItem(
					Utils.createConditions(config.getStringList("loop.item.result.conditions." + key + ".check")), 0,
					has, null));
		}
		if (config.existsKey("loop.item.result.type")) {
			ItemMaker maker = ItemMaker.loadMakerFromConfig(config, "loop.item.result");
			List<String> messages = config.getStringList("loop.item.result.click.messages");
			List<String> commands = config.getStringList("loop.item.result.click.commands");
			List<Action> actions = Utils.createActions(this, config.getStringList("loop.item.result.click.actions"));
			if (!commands.isEmpty())
				actions.add(0, new Action() {
					@Override
					public void run(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
						for (String command : commands) {
							String finalCommand = Utils.replacePlaceholders(command, placeholders, player.getUniqueId())
									.replace("{player}", player.getName());
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
						}
					}

					@Override
					public boolean shouldSync() {
						return true;
					}
				});
			if (!messages.isEmpty())
				actions.add(0,(gui, player, sharedData, placeholders) -> {
					for (String message : messages)
						player.sendMessage(ColorUtils
								.colorize(Utils.replacePlaceholders(message, placeholders, player.getUniqueId())));
				});
			String typePlaceholder = config.getString("loop.item.result.type");
			ItemPackage has;
			if (Utils.checkForPlaceholders(maker) || Utils.checkForPlaceholders(typePlaceholder))
				has = new ItemPackage(typePlaceholder, maker, 0, actions);
			else
				has = new StaticItemPackage(typePlaceholder, new ItemGUI(maker.build()) {

					@Override
					public void onClick(Player player, HolderGUI gui, ClickType click) {
						int pos = 0;
						for (Action action : actions) {
							if (action.shouldSync()) {
								action.runSync(++pos, actions, gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
								break;
							}
							action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
							++pos;
						}
					}
				}, maker, 0, actions);
			defaultSlotItem = has;
		}
		Map<Character, ItemGUI> staticItems = new HashMap<>();
		int pos = -1;
		for (String line : lines)
			for (char c : line.toCharArray()) {
				++pos;
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
				ItemGUI cached = staticItems.get(c);
				if (cached != null)
					this.staticItems.put(pos, cached);
				else {
					ItemPackage dynamic = dynamicItems.get(c);
					if (dynamic != null)
						dynamic.addSlot(pos);
					else {
						ConditionItem conditionItem = conditionItems.get(c);
						if (conditionItem != null)
							conditionItem.addSlot(pos);
						else if (config.existsKey("items." + c + ".conditions")) {
							ItemPackage has;
							ItemPackage not;
							if (config.exists("items." + c + ".has")) {
								ItemMaker maker = ItemMaker.loadMakerFromConfig(config, "items." + c + ".has");
								List<String> messages = config.getStringList("items." + c + ".has.click.messages");
								List<String> commands = config.getStringList("items." + c + ".has.click.commands");
								List<Action> actions = Utils.createActions(this,
										config.getStringList("items." + c + ".has.click.actions"));
								if (!commands.isEmpty())
									actions.add(0, new Action() {
										@Override
										public void run(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
											for (String command : commands) {
												String finalCommand = Utils
														.replacePlaceholders(command, placeholders,
																player.getUniqueId())
														.replace("{player}", player.getName());
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
											}
										}

										@Override
										public boolean shouldSync() {
											return true;
										}
									});
								if (!messages.isEmpty())
									actions.add(0,(gui, player, sharedData, placeholders) -> {
										for (String message : messages)
											player.sendMessage(ColorUtils.colorize(Utils.replacePlaceholders(message,
													placeholders, player.getUniqueId())));
									});
								String typePlaceholder = config.getString("items." + c + ".has.type");
								if (Utils.checkForPlaceholders(maker) || Utils.checkForPlaceholders(typePlaceholder))
									has = new ItemPackage(typePlaceholder, maker, pos, actions);
								else
									has = new StaticItemPackage(typePlaceholder, new ItemGUI(maker.build()) {

										@Override
										public void onClick(Player player, HolderGUI gui, ClickType click) {
											int pos = 0;
											for (Action action : actions) {
												if (action.shouldSync()) {
													action.runSync(++pos, actions, gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
													break;
												}
												action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
												++pos;
											}
										}
									}, maker, pos, actions);
							} else
								has = new ItemPackage(null, null, pos, Collections.emptyList());
							if (config.exists("items." + c + ".not")) {
								ItemMaker maker = ItemMaker.loadMakerFromConfig(config, "items." + c + ".not");
								List<String> messages = config.getStringList("items." + c + ".not.click.messages");
								List<String> commands = config.getStringList("items." + c + ".not.click.commands");
								List<Action> actions = Utils.createActions(this,
										config.getStringList("items." + c + ".not.click.actions"));
								if (!commands.isEmpty())
									actions.add(0, new Action() {
										@Override
										public void run(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
											for (String command : commands) {
												String finalCommand = Utils
														.replacePlaceholders(command, placeholders,
																player.getUniqueId())
														.replace("{player}", player.getName());
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
											}
										}

										@Override
										public boolean shouldSync() {
											return true;
										}
									});
								if (!messages.isEmpty())
									actions.add(0,(gui, player, sharedData, placeholders) -> {
										for (String message : messages)
											player.sendMessage(ColorUtils.colorize(Utils.replacePlaceholders(message,
													placeholders, player.getUniqueId())));
									});
								String typePlaceholder = config.getString("items." + c + ".not.type");
								if (Utils.checkForPlaceholders(maker) || Utils.checkForPlaceholders(typePlaceholder))
									not = new ItemPackage(typePlaceholder, maker, pos, actions);
								else
									not = new StaticItemPackage(typePlaceholder, new ItemGUI(maker.build()) {

										@Override
										public void onClick(Player player, HolderGUI gui, ClickType click) {
											int pos = 0;
											for (Action action : actions) {
												if (action.shouldSync()) {
													action.runSync(++pos, actions, gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
													break;
												}
												action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
												++pos;
											}
										}
									}, maker, pos, actions);
							} else
								not = new ItemPackage(null, null, pos, Collections.emptyList());
							List<Condition> conditions = Utils
									.createConditions(config.getStringList("items." + c + ".conditions"));
							conditionItems.put(c, new ConditionItem(conditions, pos, has, not));
							continue;
						} else if (config.existsKey("loop.item." + c + ".action")) {
							ItemPackage has;
							ItemPackage not;
							if (config.exists("loop.item." + c + ".available")) {
								ItemMaker maker = ItemMaker.loadMakerFromConfig(config,
										"loop.item." + c + ".available");
								List<String> messages = config
										.getStringList("loop.item." + c + ".available.click.messages");
								List<String> commands = config
										.getStringList("loop.item." + c + ".available.click.commands");
								List<Action> actions = Utils.createActions(this,
										config.getStringList("loop.item." + c + ".available.click.actions"));
								if (!commands.isEmpty())
									actions.add(0, new Action() {
										@Override
										public void run(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
											for (String command : commands) {
												String finalCommand = Utils
														.replacePlaceholders(command, placeholders,
																player.getUniqueId())
														.replace("{player}", player.getName());
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
											}
										}

										@Override
										public boolean shouldSync() {
											return true;
										}
									});
								if (!messages.isEmpty())
									actions.add(0,(gui, player, sharedData, placeholders) -> {
										for (String message : messages)
											player.sendMessage(ColorUtils.colorize(Utils.replacePlaceholders(message,
													placeholders, player.getUniqueId())));
									});
								String typePlaceholder = config.getString("loop.item." + c + ".available.type");
								if (Utils.checkForPlaceholders(maker) || Utils.checkForPlaceholders(typePlaceholder))
									has = new ItemPackage(typePlaceholder, maker, pos, actions);
								else
									has = new StaticItemPackage(typePlaceholder, new ItemGUI(maker.build()) {

										@Override
										public void onClick(Player player, HolderGUI gui, ClickType click) {
											int pos = 0;
											for (Action action : actions) {
												if (action.shouldSync()) {
													action.runSync(++pos, actions, gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
													break;
												}
												action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
												++pos;
											}
										}
									}, maker, pos, actions);
							} else
								has = new ItemPackage(null, null, pos, Collections.emptyList());
							if (config.exists("loop.item." + c + ".unavailable")) {
								ItemMaker maker = ItemMaker.loadMakerFromConfig(config,
										"loop.item." + c + ".unavailable");
								List<String> messages = config
										.getStringList("loop.item." + c + ".unavailable.click.messages");
								List<String> commands = config
										.getStringList("loop.item." + c + ".unavailable.click.commands");
								List<Action> actions = Utils.createActions(this,
										config.getStringList("loop.item." + c + ".unavailable.click.actions"));
								if (!commands.isEmpty())
									actions.add(0, new Action() {
										@Override
										public void run(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
											for (String command : commands) {
												String finalCommand = Utils
														.replacePlaceholders(command, placeholders,
																player.getUniqueId())
														.replace("{player}", player.getName());
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
											}
										}

										@Override
										public boolean shouldSync() {
											return true;
										}
									});
								if (!messages.isEmpty())
									actions.add(0,(gui, player, sharedData, placeholders) -> {
										for (String message : messages)
											player.sendMessage(ColorUtils.colorize(Utils.replacePlaceholders(message,
													placeholders, player.getUniqueId())));
									});
								String typePlaceholder = config.getString("loop.item." + c + ".unavailable.type");
								if (Utils.checkForPlaceholders(maker) || Utils.checkForPlaceholders(typePlaceholder))
									not = new ItemPackage(typePlaceholder, maker, pos, actions);
								else
									not = new StaticItemPackage(typePlaceholder, new ItemGUI(maker.build()) {

										@Override
										public void onClick(Player player, HolderGUI gui, ClickType click) {
											int pos = 0;
											for (Action action : actions) {
												if (action.shouldSync()) {
													action.runSync(++pos, actions, gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
													break;
												}
												action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
												++pos;
											}
											for (Action action : actions)
												action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
										}
									}, maker, pos, actions);
							} else
								not = new ItemPackage(null, null, pos, Collections.emptyList());
							switch (config.getString("loop.item." + c + ".action").toLowerCase()) {
							case "next_page":
								nextButtonChar = c;
								nextButton = new ConditionItem(Collections.emptyList(), pos, has, not);
								break;
							case "previous_page":
								previousButtonChar = c;
								previousButton = new ConditionItem(Collections.emptyList(), pos, has, not);
								break;
							}
							continue;
						}
						ItemMaker maker = ItemMaker.loadMakerFromConfig(config, "items." + c);
						if (maker == null) {
							BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
							.warning("[GuiExpansion] Failed to find item " + c + " in the gui " + config.getFile().getName());
							continue;
						}
						List<String> messages = config.getStringList("items." + c + ".click.messages");
						List<String> commands = config.getStringList("items." + c + ".click.commands");
						List<Action> actions = Utils.createActions(this,
								config.getStringList("items." + c + ".click.actions"));
						if (!commands.isEmpty())
							actions.add(0, new Action() {
								@Override
								public void run(HolderGUI gui, Player player, Config sharedData, Map<String, Object> placeholders) {
									for (String command : commands) {
										String finalCommand = Utils
												.replacePlaceholders(command, placeholders, player.getUniqueId())
												.replace("{player}", player.getName());
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
									}
								}

								@Override
								public boolean shouldSync() {
									return true;
								}
							});
						if (!messages.isEmpty())
							actions.add(0,(gui, player, sharedData, placeholders) -> {
								for (String message : messages)
									player.sendMessage(ColorUtils.colorize(
											Utils.replacePlaceholders(message, placeholders, player.getUniqueId())));
							});
						String typePlaceholder = config.getString("items." + c + ".type");
						if (Utils.checkForPlaceholders(maker) || Utils.checkForPlaceholders(typePlaceholder))
							dynamicItems.put(c, new ItemPackage(typePlaceholder, maker, pos, actions));
						else {
							ItemGUI item = new ItemGUI(maker.build()) {

								@Override
								public void onClick(Player player, HolderGUI gui, ClickType click) {
									int pos = 0;
									for (Action action : actions) {
										if (action.shouldSync()) {
											action.runSync(++pos, actions, gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
											break;
										}
										action.run(gui, player, sharedData.get(player.getUniqueId()), Collections.emptyMap());
										++pos;
									}
								}
							};
							staticItems.put(c, item);
							this.staticItems.put(pos, item);
						}
					}
				}
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
