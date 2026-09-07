package me.devtec.theapi.bukkit.gui.expansion.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devtec.shared.API;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.StringContainer;
import me.devtec.shared.placeholders.PlaceholderAPI;
import me.devtec.shared.utility.ColorUtils;
import me.devtec.shared.utility.MathUtils;
import me.devtec.shared.utility.ParseUtils;
import me.devtec.shared.utility.StringUtils;
import me.devtec.shared.utility.StringUtils.FormatType;
import me.devtec.theapi.bukkit.BukkitLoader;
import me.devtec.theapi.bukkit.game.ItemMaker;
import me.devtec.theapi.bukkit.gui.HolderGUI;
import me.devtec.theapi.bukkit.gui.expansion.GuiCreator;
import me.devtec.theapi.bukkit.gui.expansion.actions.Action;
import me.devtec.theapi.bukkit.gui.expansion.actions.ActionManager;
import me.devtec.theapi.bukkit.gui.expansion.conditions.Condition;
import me.devtec.theapi.bukkit.gui.expansion.conditions.ConditionManager;
import me.devtec.theapi.bukkit.xseries.XMaterial;

public class Utils {

	public static ItemStack applyPlaceholders(String typePlaceholder, ItemMaker item, Player player) {
		return applyPlaceholders(typePlaceholder, item, null, player);
	}

	public static ItemStack applyPlaceholders(String typePlaceholder, ItemMaker item, Map<String, Object> placeholders, Player player) {
		ReplaceContext context = new ReplaceContext(placeholders, player.getUniqueId());

		if (item.getMaterial() == Material.STONE && typePlaceholder != null)
			item.type(XMaterial.matchXMaterial(context.replace(typePlaceholder)).orElse(XMaterial.STONE));

		if (item.getDisplayName() != null)
			item.displayName(context.replace(item.getDisplayName()));

		if (item.getLore() != null) {
			List<String> lore = item.getLore();

			for (int i = 0; i < lore.size(); ++i)
				lore.set(i, context.replace(lore.get(i)));
		}

		return item.build();
	}

	public static String replaceMath(UUID playerId, String input) {
		return replaceMath(playerId, input, 0, input.length());
	}

	private static String replaceMath(UUID playerId, String input, int start, int end) {
		StringContainer result = new StringContainer(end - start);
		Config user = null;

		for (int i = start; i < end; ++i) {
			char c = input.charAt(i);

			if (c == 'm' && (input.startsWith("min(", i) || input.startsWith("max(", i))) {
				boolean min = input.charAt(i + 1) == 'i';

				int times = 0;
				int splitPos = 0;
				int d = i + 4;
				boolean innerMath = false;

				for (; d < end; ++d) {
					char e = input.charAt(d);

					if (e == '(') {
						innerMath = true;
						++times;
					} else if (e == ',' && times == 0)
						splitPos = d;
					else if (e == ')' && --times == -1)
						break;
				}

				if (splitPos != 0 && times == -1) {
					Number first;
					Number second;

					if (innerMath) {
						String firstPart = find(input, '(', i + 4, splitPos) ? replaceMath(playerId, input, i + 4, splitPos) : null;
						String secondPart = find(input, '(', splitPos + 1, d) ? replaceMath(playerId, input, splitPos + 1, d) : null;

						first = firstPart == null ? ParseUtils.getNumber(input, i + 4, splitPos) : ParseUtils.getNumber(firstPart);
						second = secondPart == null ? ParseUtils.getNumber(input, splitPos + 1, d) : ParseUtils.getNumber(secondPart);
					} else {
						first = ParseUtils.getNumber(input, i + 4, splitPos);
						second = ParseUtils.getNumber(input, splitPos + 1, d);
					}

					appendMinMax(result, first, second, min);
					i = d;
					continue;
				}
			}

			if (c == 'c' && input.startsWith("calc(", i)) {
				int times = 0;
				int d = i + 5;
				boolean innerMath = false;

				for (; d < end; ++d) {
					char e = input.charAt(d);

					if (e == '(') {
						innerMath = true;
						++times;
					} else if (e == ')' && --times == -1)
						break;
				}

				if (times == -1) {
					double value = innerMath
							? MathUtils.calculate(replaceMath(playerId, input, i + 5, d))
									: MathUtils.calculate(input, i + 5, d);

					result.append(StringUtils.formatDouble(FormatType.BASIC, value));
					i = d;
					continue;
				}
			}

			if (c == 'u' && input.startsWith("user(", i)) {
				int times = 0;
				int d = i + 5;

				for (; d < end; ++d) {
					char e = input.charAt(d);

					if (e == '(')
						++times;
					else if (e == ')' && --times == -1)
						break;
				}

				if (times == -1) {
					if (user == null)
						user = API.getUser(playerId);

					String value = String.valueOf(user.getString(input.substring(i + 5, d)));
					result.append(formatValue(value));

					i = d;
					continue;
				}
			}

			if (c == 'f' && input.startsWith("format(", i)) {
				int times = 0;
				int d = i + 7;

				for (; d < end; ++d) {
					char e = input.charAt(d);

					if (e == '(')
						++times;
					else if (e == ')' && --times == -1)
						break;
				}

				if (times == -1) {
					result.append(formatValue(formatText(input.substring(i + 7, d))));
					i = d;
					continue;
				}
			}

			result.append(c);
		}

		return result.toString();
	}

	private static void appendMinMax(StringContainer result, Number first, Number second, boolean min) {
		if (first instanceof Double || first instanceof Float || second instanceof Double || second instanceof Float)
			result.append(StringUtils.formatDouble(FormatType.BASIC,
					min ? Math.min(first.doubleValue(), second.doubleValue()) : Math.max(first.doubleValue(), second.doubleValue())));
		else
			result.append(StringUtils.formatDouble(FormatType.BASIC,
					min ? Math.min(first.longValue(), second.longValue()) : Math.max(first.longValue(), second.longValue())));
	}

	private static String formatText(String name) {
		StringContainer result = new StringContainer(name.length());
		int start = 0;
		boolean first = true;

		for (int i = 0; i <= name.length(); ++i) {
			if (i != name.length() && name.charAt(i) != '_')
				continue;

			if (i == start) {
				start = i + 1;
				continue;
			}

			if (!first)
				result.append(' ');

			boolean lower = !first && (equalsRegion(name, start, i, "OF") || equalsRegion(name, start, i, "THE"));

			if (lower)
				for (int j = start; j < i; ++j)
					result.append(Character.toLowerCase(name.charAt(j)));
			else {
				result.append(name.charAt(start));

				for (int j = start + 1; j < i; ++j)
					result.append(Character.toLowerCase(name.charAt(j)));
			}

			first = false;
			start = i + 1;
		}

		return result.toString();
	}

	private static boolean equalsRegion(String input, int start, int end, String value) {
		return end - start == value.length() && input.regionMatches(start, value, 0, value.length());
	}

	private static boolean find(String input, char c, int start, int end) {
		int result = input.indexOf(c, start);
		return result != -1 && result < end;
	}

	public static List<Condition> createConditions(List<String> stringConditions) {
		List<Condition> conditions = new ArrayList<>(stringConditions.size());

		for (String value : stringConditions) {
			int splitAt = value.indexOf(':');
			String name = splitAt == -1 ? value : value.substring(0, splitAt);
			Condition condition = ConditionManager.createByName(name, splitAt == -1 ? "" : value.substring(splitAt + 1));

			if (condition != null)
				conditions.add(condition);
			else
				BukkitLoader.getPlugin(BukkitLoader.class).getLogger().warning("[GuiExpansion] Not found condition " + name);
		}

		return conditions;
	}

	public static List<Action> createActions(GuiCreator holder, List<String> stringActions) {
		List<Action> actions = new ArrayList<>(stringActions.size());

		for (String value : stringActions) {
			int splitAt = value.indexOf(':');
			String name = splitAt == -1 ? value : value.substring(0, splitAt);
			Action action = ActionManager.createByName(name, holder, splitAt == -1 ? "" : value.substring(splitAt + 1));

			if (action != null)
				actions.add(action);
			else
				BukkitLoader.getPlugin(BukkitLoader.class).getLogger().warning("[GuiExpansion] Not found action " + name);
		}

		return actions;
	}

	public static void processActions(GuiCreator holder, HolderGUI gui, Player player, Config sharedData,
			Map<String, Object> placeholders, String actionName) {

		if (actionName.isEmpty() || "none".equals(actionName))
			return;

		List<Action> actions = holder.getCustomActions().get(actionName);

		if (actions == null) {
			BukkitLoader.getPlugin(BukkitLoader.class).getLogger().warning("[GuiExpansion] Not found customAction " + actionName);
			return;
		}

		for (int i = 0; i < actions.size(); ++i) {
			Action action = actions.get(i);

			if (action.shouldSync()) {
				action.runSync(i + 1, actions, gui, player, sharedData, placeholders);
				return;
			}

			action.run(gui, player, sharedData, placeholders);
		}
	}

	public static int findEndOfPossibleJson(String action, int start) {
		int depth = 0;
		boolean quoted = false;
		boolean escaped = false;

		for (int i = start; i < action.length(); ++i) {
			char c = action.charAt(i);

			if (escaped) {
				escaped = false;
				continue;
			}

			if (quoted && c == '\\') {
				escaped = true;
				continue;
			}

			if (c == '"') {
				quoted = !quoted;
				continue;
			}

			if (quoted)
				continue;

			if (c == '{')
				++depth;
			else if (c == '}')
				--depth;
			else if (c == ':' && depth == 0)
				return i;
		}

		return action.length();
	}

	public static boolean checkForPlaceholders(ItemMaker maker) {
		if (maker.getDisplayName() != null && checkForPlaceholders(maker.getDisplayName()))
			return true;

		if (maker.getLore() != null)
			for (String line : maker.getLore())
				if (checkForPlaceholders(line))
					return true;

		return false;
	}

	public static String replacePlaceholders(String input, Map<String, Object> placeholders, UUID playerId) {
		return new ReplaceContext(placeholders, playerId).replace(input);
	}

	public static boolean checkForPlaceholders(String line) {
		if (line == null)
			return false;

		if (hasPair(line, '[', ']') || hasPair(line, '{', '}') || hasPercentPlaceholder(line))
			return true;

		return hasMath(line);
	}

	public static String replaceLiteral(String input, String target, String replacement) {
		int at = input.indexOf(target);

		if (at == -1)
			return input;

		int from = 0;
		StringBuilder result = new StringBuilder(input.length() + Math.max(0, replacement.length() - target.length()));

		do {
			result.append(input, from, at).append(replacement);
			from = at + target.length();
			at = input.indexOf(target, from);
		} while (at != -1);

		return result.append(input, from, input.length()).toString();
	}

	private static boolean hasPair(String input, char start, char end) {
		int at = input.indexOf(start);
		return at != -1 && input.indexOf(end, at + 1) != -1;
	}

	private static boolean hasPercentPlaceholder(String input) {
		int at = input.indexOf('%');
		return at != -1 && input.indexOf('%', at + 1) != -1;
	}

	private static boolean hasMath(String input) {
		return input.indexOf("min(") != -1 || input.indexOf("max(") != -1
				|| input.indexOf("calc(") != -1 || input.indexOf("user(") != -1
				|| input.indexOf("format(") != -1;
	}

	private static String formatValue(Object value) {
		String text = String.valueOf(value);

		if (value instanceof Number)
			return StringUtils.formatDouble(FormatType.BASIC, ((Number) value).doubleValue());

		if (ParseUtils.isNumber(text))
			return StringUtils.formatDouble(FormatType.BASIC, ParseUtils.getNumber(text).doubleValue());

		return text;
	}

	private static final class ReplaceContext {

		private final Map<String, Object> placeholders;
		private final UUID playerId;
		private final Config data;

		private String[] placeholderKeys;
		private String[] placeholderValues;

		private String[] dataKeys;
		private String[] dataValues;

		private boolean placeholdersLoaded;
		private boolean dataLoaded;

		private ReplaceContext(Map<String, Object> placeholders, UUID playerId) {
			this.placeholders = placeholders;
			this.playerId = playerId;
			this.data = GuiCreator.sharedData.get(playerId);
		}

		private String replace(String input) {
			if (input == null)
				return null;

			if (placeholders != null && hasPair(input, '{', '}')) {
				loadPlaceholders();

				for (int i = 0; i < placeholderKeys.length; ++i)
					input = replaceLiteral(input, placeholderKeys[i], placeholderValues[i]);
			}

			if (data != null && hasPair(input, '[', ']')) {
				loadData();

				for (int i = 0; i < dataKeys.length; ++i)
					input = replaceLiteral(input, dataKeys[i], dataValues[i]);
			}

			if (hasPercentPlaceholder(input))
				input = PlaceholderAPI.apply(input, playerId);

			if (hasMath(input))
				input = replaceMath(playerId, input);

			return ColorUtils.colorize(input);
		}

		private void loadPlaceholders() {
			if (placeholdersLoaded)
				return;

			placeholdersLoaded = true;

			int size = placeholders.size();
			placeholderKeys = new String[size];
			placeholderValues = new String[size];

			int i = 0;

			for (Entry<String, Object> entry : placeholders.entrySet()) {
				placeholderKeys[i] = '{' + entry.getKey() + '}';
				placeholderValues[i] = formatValue(entry.getValue());
				++i;
			}
		}

		private void loadData() {
			if (dataLoaded)
				return;

			dataLoaded = true;

			List<String> keys = new ArrayList<>();
			List<String> values = new ArrayList<>();

			for (String key : data.getKeys(true)) {
				keys.add('[' + key + ']');
				values.add(formatValue(data.getString(key)));
			}

			dataKeys = keys.toArray(new String[keys.size()]);
			dataValues = values.toArray(new String[values.size()]);
		}
	}
}