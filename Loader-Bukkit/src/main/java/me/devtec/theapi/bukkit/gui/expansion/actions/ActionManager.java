package me.devtec.theapi.bukkit.gui.expansion.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.devtec.shared.API;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.json.Json;
import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.AnvilGUI;
import me.devtec.theapi.bukkit.gui.expansion.GuiCreator;
import me.devtec.theapi.bukkit.gui.expansion.utils.Utils;

public class ActionManager {
	private static final Map<String, ActionConstructor> actions = new HashMap<>();

	public static Action createByName(String name, GuiCreator holder, String values) {
		return actions.get(name.toLowerCase()).create(holder, values);
	}

	public static void register(String name, ActionConstructor actionCreator) {
		actions.put(name.toLowerCase(), actionCreator);
	}

	public static void unregister(String name) {
		actions.remove(name.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	public static void registerDefaults() {
		register("open_menu", (holder, values) -> {
			String id = values;
			return (gui, player, placeholders) -> {
				GuiCreator creator = GuiCreator.guis.get(id);
				if (creator != null)
					creator.open(player);
				else
					BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
					.warning("[GUiExpansion] Not found menu with id " + id + "!");
			};
		});
		register("close_menu", (holder, values) -> (gui, player, placeholders) -> {
			gui.close();
		});
		register("set_rename_text", (holder, values) -> (gui, player, placeholders) -> {
			if (gui instanceof AnvilGUI)
				BukkitLoader.getNmsProvider().postToMainThread(() -> ((AnvilGUI) gui).setRepairText(values));
		});
		// cache
		register("set_cache", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			String value = values.substring(values.indexOf(':') + 1);
			boolean hasPlaceholders = Utils.checkForPlaceholders(value);
			return (gui, player, placeholders) -> {
				String input = value;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				GuiCreator.sharedData.get(player.getUniqueId()).set(path, input);
			};
		});
		register("set_cache_if_absent", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			String value = values.substring(values.indexOf(':') + 1);
			boolean hasPlaceholders = Utils.checkForPlaceholders(value);
			return (gui, player, placeholders) -> {
				String input = value;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				GuiCreator.sharedData.get(player.getUniqueId()).setIfAbsent(path, input);
			};
		});
		register("plus_cache", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			String value = values.substring(values.indexOf(':') + 1);
			boolean hasPlaceholders = Utils.checkForPlaceholders(value);
			return (gui, player, placeholders) -> {
				String input = value;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				Config data = GuiCreator.sharedData.get(player.getUniqueId());
				data.set(path, data.getDouble(path) + ParseUtils.getDouble(input));
			};
		});
		register("minus_cache", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			String value = values.substring(values.indexOf(':') + 1);
			boolean hasPlaceholders = Utils.checkForPlaceholders(value);
			return (gui, player, placeholders) -> {
				String input = value;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				Config data = GuiCreator.sharedData.get(player.getUniqueId());
				data.set(path, data.getDouble(path) - ParseUtils.getDouble(input));
			};
		});
		register("remove_cache", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			return (gui, player, placeholders) -> {
				GuiCreator.sharedData.get(player.getUniqueId()).remove(path);
			};
		});
		register("server", (holder, values) -> {
			ByteArrayDataOutput output = ByteStreams.newDataOutput();
			output.writeUTF("Connect");
			output.writeUTF(values);
			byte[] data = output.toByteArray();
			return (gui, player, placeholders) -> {
				player.sendPluginMessage(BukkitLoader.getPlugin(BukkitLoader.class), "BungeeCord", data);
			};
		});
		register("clear_cache", (holder, values) -> (gui, player, placeholders) -> {
			GuiCreator.sharedData.get(player.getUniqueId()).reset();
		});
		register("update_item", (holder, values) -> {
			char itemId = values.charAt(0);
			return (gui, player, placeholders) -> {
				holder.updateItem(gui, player, itemId);
			};
		});
		// user data
		register("set_user", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			String value = values.substring(values.indexOf(':') + 1);
			boolean hasPlaceholders = Utils.checkForPlaceholders(value);
			return (gui, player, placeholders) -> {
				String input = value;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				API.getUser(player.getUniqueId()).set(path, input);
			};
		});
		register("set_user_if_absent", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			String value = values.substring(values.indexOf(':') + 1);
			boolean hasPlaceholders = Utils.checkForPlaceholders(value);
			return (gui, player, placeholders) -> {
				String input = value;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				API.getUser(player.getUniqueId()).setIfAbsent(path, input);
			};
		});
		register("plus_user", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			String value = values.substring(values.indexOf(':') + 1);
			boolean hasPlaceholders = Utils.checkForPlaceholders(value);
			return (gui, player, placeholders) -> {
				String input = value;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				Config data = API.getUser(player.getUniqueId());
				data.set(path, data.getDouble(path) + ParseUtils.getDouble(input));
			};
		});
		register("minus_user", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			String value = values.substring(values.indexOf(':') + 1);
			boolean hasPlaceholders = Utils.checkForPlaceholders(value);
			return (gui, player, placeholders) -> {
				String input = value;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				Config data = API.getUser(player.getUniqueId());
				data.set(path, data.getDouble(path) - ParseUtils.getDouble(input));
			};
		});
		register("remove_user", (holder, values) -> {
			String path = values.substring(0, values.indexOf(':'));
			return (gui, player, placeholders) -> {
				API.getUser(player.getUniqueId()).remove(path);
			};
		});
		register("check_balance", (holder, values) -> {
			String balance = values.substring(0, values.indexOf('?'));
			int yesEndAt = Utils.findEndOfPossibleJson(values, values.indexOf('?') + 1);
			String yes = values.substring(values.indexOf('?') + 1, yesEndAt);
			String yesName = yes.indexOf('{') == -1 ? yes : yes.substring(0, yes.indexOf('{'));
			String yesJson = yes.indexOf('{') == -1 ? null : yes.substring(yes.indexOf('{'));
			String no = values.substring(yesEndAt + 1);
			String noName = no.indexOf('{') == -1 ? no : no.substring(0, no.indexOf('{'));
			String noJson = no.indexOf('{') == -1 ? null : no.substring(no.indexOf('{'));
			boolean hasPlaceholders = Utils.checkForPlaceholders(balance);
			return (gui, player, placeholders) -> {
				String input = balance;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				if (BukkitLoader.getEconomyHook().getBalance(player.getName(), player.getWorld().getName()) >= ParseUtils
						.getDouble(input)) {
					Map<String, Object> innerPlaceholders = Collections.emptyMap();
					if (yesJson != null) {
						Object json = Json.reader().read(yesJson);
						if (json instanceof Map)
							innerPlaceholders = (Map<String, Object>) json;
						else
							BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
							.warning("[GUiExpansion] [Action] This is not json! " + yesJson);
					}
					Utils.processActions(holder, gui, player, innerPlaceholders, yesName);
				} else {
					Map<String, Object> innerPlaceholders = Collections.emptyMap();
					if (noJson != null) {
						Object json = Json.reader().read(noJson);
						if (json instanceof Map)
							innerPlaceholders = (Map<String, Object>) json;
						else
							BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
							.warning("[GUiExpansion] [Action] This is not json! " + noJson);
					}
					Utils.processActions(holder, gui, player, innerPlaceholders, noName);
				}
			};
		});
		register("check_permission", (holder, values) -> {
			String permission = values.substring(0, values.indexOf('?'));
			int yesEndAt = Utils.findEndOfPossibleJson(values, values.indexOf('?') + 1);
			String yes = values.substring(values.indexOf('?') + 1, yesEndAt);
			String yesName = yes.indexOf('{') == -1 ? yes : yes.substring(0, yes.indexOf('{'));
			String yesJson = yes.indexOf('{') == -1 ? null : yes.substring(yes.indexOf('{'));
			String no = values.substring(yesEndAt + 1);
			String noName = no.indexOf('{') == -1 ? no : no.substring(0, no.indexOf('{'));
			String noJson = no.indexOf('{') == -1 ? null : no.substring(no.indexOf('{'));
			boolean hasPlaceholders = Utils.checkForPlaceholders(permission);
			return (gui, player, placeholders) -> {
				String input = permission;
				if (hasPlaceholders) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				if (player.hasPermission(input)) {
					Map<String, Object> innerPlaceholders = Collections.emptyMap();
					if (yesJson != null) {
						Object json = Json.reader().read(yesJson);
						if (json instanceof Map)
							innerPlaceholders = (Map<String, Object>) json;
						else
							BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
							.warning("[GUiExpansion] [Action] This is not json! " + yesJson);
					}
					Utils.processActions(holder, gui, player, innerPlaceholders, yesName);
				} else {
					Map<String, Object> innerPlaceholders = Collections.emptyMap();
					if (noJson != null) {
						Object json = Json.reader().read(noJson);
						if (json instanceof Map)
							innerPlaceholders = (Map<String, Object>) json;
						else
							BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
							.warning("[GUiExpansion] [Action] This is not json! " + noJson);
					}
					Utils.processActions(holder, gui, player, innerPlaceholders, noName);
				}
			};
		});
		register("check_placeholder", (holder, values) -> {
			String valueWithResult = values.substring(0, values.indexOf('?'));
			AskType ask = AskType.parseType(valueWithResult);
			String v = "";
			String r = "";
			int locate = 0;
			if (ask != null)
				switch (ask) {
				case EQUALS:
					locate = valueWithResult.indexOf("==");
					v = valueWithResult.substring(0, locate);
					r = valueWithResult.substring(locate + 2);
					break;
				case LOWER:
					locate = valueWithResult.indexOf("<");
					v = valueWithResult.substring(0, locate);
					r = valueWithResult.substring(locate + 1);
					break;
				case LOWER_OR_EQUALS:
					locate = valueWithResult.indexOf("<=");
					v = valueWithResult.substring(0, locate);
					r = valueWithResult.substring(locate + 2);
					break;
				case MORE:
					locate = valueWithResult.indexOf(">");
					v = valueWithResult.substring(0, locate);
					r = valueWithResult.substring(locate + 1);
					break;
				case MORE_OR_EQUALS:
					locate = valueWithResult.indexOf(">=");
					v = valueWithResult.substring(0, locate);
					r = valueWithResult.substring(locate + 2);
					break;
				case NOT_SAME:
					locate = valueWithResult.indexOf("!=");
					v = valueWithResult.substring(0, locate);
					r = valueWithResult.substring(locate + 2);
					break;
				case CONTAINS:
					locate = valueWithResult.indexOf("?=");
					v = valueWithResult.substring(0, locate);
					r = valueWithResult.substring(locate + 2);
					break;
				default:
					BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
					.warning("[GUiExpansion] Action check_placeholder in the action with values '" + values
							+ "' doesn't contain check type (X==Z, X!=Z...)");
					return (gui, player, placeholders) -> {
					};
				}
			else {
				BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
				.warning("[GUiExpansion] Action check_placeholder in the action with values '" + values
						+ "' doesn't contain check type (X==Z, X!=Z...)");
				return (gui, player, placeholders) -> {
				};
			}
			String value = v;
			String resultValue = r;
			int yesEndAt = Utils.findEndOfPossibleJson(values, values.indexOf('?', locate) + 1);
			String yes = values.substring(values.indexOf('?', locate) + 1, yesEndAt);
			String yesName = yes.indexOf('{') == -1 ? yes : yes.substring(0, yes.indexOf('{'));
			String yesJson = yes.indexOf('{') == -1 ? null : yes.substring(yes.indexOf('{'));
			String no = values.substring(yesEndAt + 1);
			String noName = no.indexOf('{') == -1 ? no : no.substring(0, no.indexOf('{'));
			String noJson = no.indexOf('{') == -1 ? null : no.substring(no.indexOf('{'));
			boolean hasPlaceholdersValue = Utils.checkForPlaceholders(value);
			boolean hasPlaceholdersResult = Utils.checkForPlaceholders(resultValue);
			return (gui, player, placeholders) -> {
				String input = value;
				String result = resultValue;
				if (hasPlaceholdersValue) {
					if (gui instanceof AnvilGUI)
						input = input.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				}
				if (hasPlaceholdersResult) {
					if (gui instanceof AnvilGUI)
						result = result.replace("{renameText}", ((AnvilGUI) gui).getRenameText());
					result = Utils.replacePlaceholders(result, placeholders, player.getUniqueId());
				}
				if (ask.compare(input, result)) {
					Map<String, Object> innerPlaceholders = Collections.emptyMap();
					if (yesJson != null) {
						Object json = Json.reader().read(yesJson);
						if (json instanceof Map)
							innerPlaceholders = (Map<String, Object>) json;
						else
							BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
							.warning("[GUiExpansion] [Action] This is not json! " + yesJson);
					}
					Utils.processActions(holder, gui, player, innerPlaceholders, yesName);
				} else {
					Map<String, Object> innerPlaceholders = Collections.emptyMap();
					if (noJson != null) {
						Object json = Json.reader().read(noJson);
						if (json instanceof Map)
							innerPlaceholders = (Map<String, Object>) json;
						else
							BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
							.warning("[GUiExpansion] [Action] This is not json! " + noJson);
					}
					Utils.processActions(holder, gui, player, innerPlaceholders, noName);
				}
			};
		});
	}
}
