package me.devtec.theapi.bukkit.gui.expansion.conditions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.expansion.actions.AskType;
import me.devtec.theapi.bukkit.gui.expansion.utils.Utils;

public class ConditionManager {
	private static final Map<String, Function<String, Condition>> conditions = new HashMap<>();

	public static Condition createByName(String name, String values) {
		return conditions.get(name.toLowerCase()).apply(values);
	}

	public static void register(String name, Function<String, Condition> conditionCreator) {
		conditions.put(name.toLowerCase(), conditionCreator);
	}

	public static void unregister(String name) {
		conditions.remove(name.toLowerCase());
	}

	public static void registerDefaults() {
		register("check_balance", values -> {
			boolean hasPlaceholders = Utils.checkForPlaceholders(values);
			return (player, sharedData, placeholders) -> {
				String input = values;
				if (hasPlaceholders)
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				return BukkitLoader.getEconomyHook().getBalance(player.getName(), player.getWorld().getName()) >= ParseUtils
						.getDouble(input);
			};
		});
		register("check_permission", values -> {
			boolean hasPlaceholders = Utils.checkForPlaceholders(values);
			return (player, sharedData, placeholders) -> {
				String input = values;
				if (hasPlaceholders)
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				return player.hasPermission(input);
			};
		});
		register("check_placeholder", values -> {
			AskType ask = AskType.parseType(values);
			String v = "";
			String r = "";
			if (ask != null)
				switch (ask) {
				case EQUALS:
					int locate = values.indexOf("==");
					v = values.substring(0, locate);
					r = values.substring(locate + 2);
					break;
				case LOWER:
					locate = values.indexOf("<");
					v = values.substring(0, locate);
					r = values.substring(locate + 1);
					break;
				case LOWER_OR_EQUALS:
					locate = values.indexOf("<=");
					v = values.substring(0, locate);
					r = values.substring(locate + 2);
					break;
				case MORE:
					locate = values.indexOf(">");
					v = values.substring(0, locate);
					r = values.substring(locate + 1);
					break;
				case MORE_OR_EQUALS:
					locate = values.indexOf(">=");
					v = values.substring(0, locate);
					r = values.substring(locate + 2);
					break;
				case NOT_SAME:
					locate = values.indexOf("!=");
					v = values.substring(0, locate);
					r = values.substring(locate + 2);
					break;
				case CONTAINS:
					locate = values.indexOf("?=");
					v = values.substring(0, locate);
					r = values.substring(locate + 2);
					break;
				default:
					BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
					.warning("[GUiExpansion] Condition check_placeholder in the condition with values '" + values
							+ "' doesn't contain check type (X==Z, X!=Z...)");
					return (player, sharedData, placeholders) -> false;
				}
			else {
				BukkitLoader.getPlugin(BukkitLoader.class).getLogger()
				.warning("[GUiExpansion] Condition check_placeholder in the condition with values '" + values
						+ "' doesn't contain check type (X==Z, X!=Z...)");
				return (player, sharedData, placeholders) -> false;
			}
			String value = v.trim();
			String resultValue = r.trim();
			boolean hasPlaceholdersValue = Utils.checkForPlaceholders(value);
			boolean hasPlaceholdersResult = Utils.checkForPlaceholders(resultValue);
			return (player, sharedData, placeholders) -> {
				String input = value;
				String result = resultValue;
				if (hasPlaceholdersValue)
					input = Utils.replacePlaceholders(input, placeholders, player.getUniqueId());
				if (hasPlaceholdersResult)
					result = Utils.replacePlaceholders(result, placeholders, player.getUniqueId());
				return ask.compare(input, result);
			};
		});
	}
}
