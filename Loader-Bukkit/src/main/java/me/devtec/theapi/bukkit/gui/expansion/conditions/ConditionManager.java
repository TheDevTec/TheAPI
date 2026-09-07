package me.devtec.theapi.bukkit.gui.expansion.conditions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import me.devtec.shared.utility.ParseUtils;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.gui.expansion.actions.AskType;
import me.devtec.theapi.bukkit.gui.expansion.utils.Utils;

public class ConditionManager {

	private static final Map<String, Function<String, Condition>> conditions = new HashMap<>();

	public static Condition createByName(String name, String values) {
		Function<String, Condition> constructor = conditions.get(name.toLowerCase(Locale.ROOT));
		return constructor == null ? null : constructor.apply(values);
	}

	public static void register(String name, Function<String, Condition> conditionCreator) {
		conditions.put(name.toLowerCase(Locale.ROOT), conditionCreator);
	}

	public static void unregister(String name) {
		conditions.remove(name.toLowerCase(Locale.ROOT));
	}

	public static void registerDefaults() {
		register("check_balance", values -> {
			final boolean placeholders = Utils.checkForPlaceholders(values);

			if (!placeholders) {
				final double balance = ParseUtils.getDouble(values);
				return (player, sharedData, data) -> BukkitLoader.getEconomyHook().getBalance(player.getName(),
						player.getWorld().getName()) >= balance;
			}

			return (player, sharedData,
					data) -> BukkitLoader.getEconomyHook().getBalance(player.getName(),
							player.getWorld().getName()) >= ParseUtils
									.getDouble(Utils.replacePlaceholders(values, data, player.getUniqueId()));
		});

		register("check_permission", values -> {
			final boolean placeholders = Utils.checkForPlaceholders(values);

			if (!placeholders)
				return (player, sharedData, data) -> player.hasPermission(values);

			return (player, sharedData, data) -> player
					.hasPermission(Utils.replacePlaceholders(values, data, player.getUniqueId()));
		});

		register("check_placeholder", values -> {
			final AskType ask = AskType.parseType(values);

			if (ask == null) {
				warn("Condition check_placeholder in the condition with values '" + values
						+ "' doesn't contain check type (X==Z, X!=Z...)");
				return (player, sharedData, placeholders) -> false;
			}

			int at = operatorIndex(values, ask);

			if (at == -1) {
				warn("Condition check_placeholder in the condition with values '" + values
						+ "' doesn't contain check type (X==Z, X!=Z...)");
				return (player, sharedData, placeholders) -> false;
			}

			int length = operatorLength(ask);

			final String value = values.substring(0, at).trim();
			final String resultValue = values.substring(at + length).trim();

			final boolean valuePlaceholders = Utils.checkForPlaceholders(value);
			final boolean resultPlaceholders = Utils.checkForPlaceholders(resultValue);

			if (!valuePlaceholders && !resultPlaceholders) {
				final boolean result = ask.compare(value, resultValue);
				return (player, sharedData, placeholders) -> result;
			}

			if (!valuePlaceholders)
				return (player, sharedData, placeholders) -> ask.compare(value,
						Utils.replacePlaceholders(resultValue, placeholders, player.getUniqueId()));

			if (!resultPlaceholders)
				return (player, sharedData, placeholders) -> ask
						.compare(Utils.replacePlaceholders(value, placeholders, player.getUniqueId()), resultValue);

			return (player, sharedData, placeholders) -> ask.compare(
					Utils.replacePlaceholders(value, placeholders, player.getUniqueId()),
					Utils.replacePlaceholders(resultValue, placeholders, player.getUniqueId()));
		});
	}

	private static int operatorIndex(String values, AskType type) {
		switch (type) {
		case EQUALS:
			return values.indexOf("==");
		case NOT_SAME:
			return values.indexOf("!=");
		case CONTAINS:
			return values.indexOf("?=");
		case NOT_CONTAINS:
			return values.indexOf("?!");
		case REGEX:
			return values.indexOf("??");
		case LOWER_OR_EQUALS:
			return values.indexOf("<=");
		case MORE_OR_EQUALS:
			return values.indexOf(">=");
		case LOWER:
			return values.indexOf('<');
		case MORE:
			return values.indexOf('>');
		default:
			return -1;
		}
	}

	private static int operatorLength(AskType type) {
		switch (type) {
		case LOWER:
		case MORE:
			return 1;
		default:
			return 2;
		}
	}

	private static void warn(String message) {
		BukkitLoader.getPlugin(BukkitLoader.class).getLogger().warning("[GuiExpansion] " + message);
	}
}