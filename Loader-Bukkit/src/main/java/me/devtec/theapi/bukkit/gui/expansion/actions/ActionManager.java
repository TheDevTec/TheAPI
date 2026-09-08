package me.devtec.theapi.bukkit.gui.expansion.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.devtec.shared.API;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.json.Json;
import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.expansion.GuiCreator;
import me.devtec.theapi.bukkit.gui.expansion.guis.LoopGuiCreator;
import me.devtec.theapi.bukkit.gui.expansion.utils.Utils;

public class ActionManager {

	private static final Map<String, ActionConstructor> actions = new HashMap<>();
	private static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();

	private static final int SET = 0;
	private static final int SET_IF_ABSENT = 1;
	private static final int PLUS = 2;
	private static final int MINUS = 3;
	private static final int REMOVE = 4;

	public static Action createByName(String name, GuiCreator holder, String values) {
		ActionConstructor constructor = actions.get(name.toLowerCase(Locale.ROOT));
		return constructor == null ? null : constructor.create(holder, values);
	}

	public static void register(String name, ActionConstructor actionCreator) {
		actions.put(name.toLowerCase(Locale.ROOT), actionCreator);
	}

	public static void unregister(String name) {
		actions.remove(name.toLowerCase(Locale.ROOT));
	}

	public static void registerDefaults() {

		register("open_menu", (holder, values) -> {
			final String id = values;

			return (gui, player, sharedData, placeholders) -> {
				GuiCreator creator = GuiCreator.guis.get(id);

				if (creator == null) {
					BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
							.warning("[GuiExpansion] Not found menu with id " + id + "!");
					return;
				}

				if (creator instanceof LoopGuiCreator && placeholders.containsKey("page")) {
					Object page = placeholders.get("page");
					((LoopGuiCreator) creator).open(player, page instanceof Number ? ((Number) page).intValue() : 1);
				} else
					creator.open(player);
			};
		});

		register("close_menu", (holder, values) -> (gui, player, sharedData, placeholders) -> gui.close());

		register("set_rename_text", (holder, values) -> (gui, player, sharedData, placeholders) -> {
			if (gui instanceof AnvilGUI)
				BukkitLoader.getNmsProvider().postToMainThread(() -> ((AnvilGUI) gui).setRepairText(values));
		});

		registerDataActions("cache", false);
		registerDataActions("user", true);

		register("clear_cache", (holder, values) -> (gui, player, sharedData, placeholders) -> {
			Config config = GuiCreator.sharedData.get(player.getUniqueId());
			if (config != null)
				config.reset();
		});

		register("server", (holder, values) -> {
			ByteArrayDataOutput output = ByteStreams.newDataOutput();
			output.writeUTF("Connect");
			output.writeUTF(values);

			final byte[] data = output.toByteArray();

			return (gui, player, sharedData, placeholders) -> player
					.sendPluginMessage(BukkitLoader.getPlugin(BukkitLoader.class), "BungeeCord", data);
		});

		register("update_item", (holder, values) -> {
			final char itemId = values.charAt(0);
			return (gui, player, sharedData, placeholders) -> holder.updateItem(gui, player, itemId);
		});

		registerCheck("check_balance", (player, value) -> BukkitLoader.getEconomyHook().getBalance(player.getName(),
				player.getWorld().getName()) >= ParseUtils.getDouble(value));

		registerCheck("check_permission", Player::hasPermission);

		register("check_placeholder", (holder, values) -> {
			final Condition condition = parseCondition(values);

			if (condition == null) {
				warn("Action check_placeholder in the action with values '" + values
						+ "' doesn't contain check type (X==Z, X!=Z...)");
				return emptyAction();
			}

			final BranchPair branches = parseBranches(values, condition.branchAt);

			if (branches == null) {
				warn("Action check_placeholder in the action with values '" + values
						+ "' doesn't contain valid yes/no actions");
				return emptyAction();
			}

			return (gui, player, sharedData, placeholders) -> {
				String input = condition.left.resolve(gui, player, placeholders);
				String result = condition.right.resolve(gui, player, placeholders);

				ActionBranch branch = condition.type.compare(input, result) ? branches.yes : branches.no;

				Utils.processActions(holder, gui, player, sharedData, branch.getPlaceholders(gui, player, placeholders),
						branch.name);
			};
		});
	}

	private static void registerDataActions(String suffix, boolean user) {
		registerDataAction("set_" + suffix, user, SET);
		registerDataAction("set_" + suffix + "_if_absent", user, SET_IF_ABSENT);
		registerDataAction("plus_" + suffix, user, PLUS);
		registerDataAction("minus_" + suffix, user, MINUS);
		registerDataAction("remove_" + suffix, user, REMOVE);
	}

	private static void registerDataAction(String name, final boolean user, final int operation) {
		register(name, (holder, values) -> {
			int at = values.indexOf(':');

			if (at == -1 && operation != REMOVE) {
				warn("Action " + name + " with values '" + values + "' doesn't contain ':'");
				return emptyAction();
			}

			final String path = at == -1 ? values : values.substring(0, at);
			final DynamicValue value = operation == REMOVE ? null : new DynamicValue(values.substring(at + 1));

			return (gui, player, sharedData, placeholders) -> {
				Config data = user ? API.getUser(player.getUniqueId())
						: GuiCreator.sharedData.computeIfAbsent(player.getUniqueId(), t -> new Config());

				if (operation == REMOVE) {
					data.remove(path);
					return;
				}

				String input = value.resolve(gui, player, placeholders);

				switch (operation) {
				case SET:
					data.set(path, input);
					break;
				case SET_IF_ABSENT:
					data.setIfAbsent(path, input);
					break;
				case PLUS:
					data.set(path, data.getDouble(path) + ParseUtils.getDouble(input));
					break;
				case MINUS:
					data.set(path, data.getDouble(path) - ParseUtils.getDouble(input));
					break;
				}
			};
		});
	}

	private static void registerCheck(String name, final PlayerCheck check) {
		register(name, (holder, values) -> {
			int at = values.indexOf('?');

			if (at == -1) {
				warn("Action " + name + " with values '" + values + "' doesn't contain '?'");
				return emptyAction();
			}

			final DynamicValue value = new DynamicValue(values.substring(0, at));
			final BranchPair branches = parseBranches(values, at);

			if (branches == null) {
				warn("Action " + name + " with values '" + values + "' doesn't contain valid yes/no actions");
				return emptyAction();
			}

			return (gui, player, sharedData, placeholders) -> {
				ActionBranch branch = check.check(player, value.resolve(gui, player, placeholders)) ? branches.yes
						: branches.no;

				Utils.processActions(holder, gui, player, sharedData, branch.getPlaceholders(gui, player, placeholders),
						branch.name);
			};
		});
	}

	private static Condition parseCondition(String values) {
		AskType type = null;
		int at = -1;
		int length = 0;

		for (int i = 0; i < values.length(); ++i) {
			char c = values.charAt(i);

			if (i + 1 < values.length()) {
				char next = values.charAt(i + 1);

				if (c == '=' && next == '=') {
					type = AskType.EQUALS;
					at = i;
					length = 2;
					break;
				}

				if (c == '!' && next == '=') {
					type = AskType.NOT_SAME;
					at = i;
					length = 2;
					break;
				}

				if (c == '<' && next == '=') {
					type = AskType.LOWER_OR_EQUALS;
					at = i;
					length = 2;
					break;
				}

				if (c == '>' && next == '=') {
					type = AskType.MORE_OR_EQUALS;
					at = i;
					length = 2;
					break;
				}

				if (c == '?' && next == '=') {
					type = AskType.CONTAINS;
					at = i;
					length = 2;
					break;
				}

				if (c == '?' && next == '!') {
					type = AskType.NOT_CONTAINS;
					at = i;
					length = 2;
					break;
				}

				if (c == '?' && next == '?') {
					type = AskType.REGEX;
					at = i;
					length = 2;
					break;
				}
			}

			if (c == '<') {
				type = AskType.LOWER;
				at = i;
				length = 1;
				break;
			}

			if (c == '>') {
				type = AskType.MORE;
				at = i;
				length = 1;
				break;
			}
		}

		if (type == null)
			return null;

		int branchAt = values.indexOf('?', at + length);

		if (branchAt == -1)
			return null;

		return new Condition(type, new DynamicValue(values.substring(0, at)),
				new DynamicValue(values.substring(at + length, branchAt)), branchAt);
	}

	private static BranchPair parseBranches(String values, int separator) {
		if (separator < 0 || separator + 1 >= values.length())
			return null;

		int yesEndAt = Utils.findEndOfPossibleJson(values, separator + 1);

		if (yesEndAt < separator + 1 || yesEndAt >= values.length())
			return null;

		String yes = values.substring(separator + 1, yesEndAt);
		String no = yesEndAt + 1 < values.length() ? values.substring(yesEndAt + 1) : "";

		return new BranchPair(new ActionBranch(yes), new ActionBranch(no));
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> parsePlaceholders(String value) {
		if (value == null)
			return EMPTY_MAP;

		Object json = Json.reader().read(value);

		if (json instanceof Map)
			return (Map<String, Object>) json;

		warn("[Action] This is not json! " + value);
		return EMPTY_MAP;
	}

	private static Action emptyAction() {
		return (gui, player, sharedData, placeholders) -> {
		};
	}

	private static void warn(String message) {
		BukkitLoader.getPlugin(BukkitLoader.class).getLogger().warning("[GuiExpansion] " + message);
	}

	private interface PlayerCheck {
		boolean check(Player player, String value);
	}

	private static final class DynamicValue {

		private final String value;
		private final boolean placeholders;
		private final boolean renameText;

		private DynamicValue(String value) {
			this.value = value;
			this.placeholders = Utils.checkForPlaceholders(value);
			this.renameText = value.indexOf("{renameText}") != -1;
		}

		private String resolve(Object gui, Player player, Map<String, Object> values) {
			if (!placeholders && !renameText)
				return value;

			String result = value;

			if (renameText && gui instanceof AnvilGUI)
				result = result.replace("{renameText}", ((AnvilGUI) gui).getRenameText());

			if (placeholders)
				result = Utils.replacePlaceholders(result, values, player.getUniqueId());

			return result;
		}
	}

	private static final class ActionBranch {

		private final String name;
		private final DynamicValue json;

		private ActionBranch(String value) {
			int at = value.indexOf('{');

			name = at == -1 ? value : value.substring(0, at);
			json = at == -1 ? null : new DynamicValue(value.substring(at));
		}

		private Map<String, Object> getPlaceholders(Object gui, Player player, Map<String, Object> placeholders) {
			return json == null ? EMPTY_MAP : parsePlaceholders(json.resolve(gui, player, placeholders));
		}
	}

	private static final class BranchPair {

		private final ActionBranch yes;
		private final ActionBranch no;

		private BranchPair(ActionBranch yes, ActionBranch no) {
			this.yes = yes;
			this.no = no;
		}
	}

	private static final class Condition {

		private final AskType type;
		private final DynamicValue left;
		private final DynamicValue right;
		private final int branchAt;

		private Condition(AskType type, DynamicValue left, DynamicValue right, int branchAt) {
			this.type = type;
			this.left = left;
			this.right = right;
			this.branchAt = branchAt;
		}
	}
}