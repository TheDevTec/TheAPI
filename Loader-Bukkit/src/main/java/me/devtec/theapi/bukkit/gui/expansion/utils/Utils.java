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

	public static ItemStack applyPlaceholders(String typePlaceholder, ItemMaker item, Map<String, Object> placeholders,
			Player player) {
		if (item.getMaterial() == Material.STONE && typePlaceholder != null)
			// Probably there's placeholder in the name of type
			item.type(XMaterial
					.matchXMaterial(Utils.replacePlaceholders(typePlaceholder, placeholders, player.getUniqueId()))
					.orElse(XMaterial.STONE));
		if (item.getDisplayName() != null)
			item.displayName(Utils.replacePlaceholders(item.getDisplayName(), placeholders, player.getUniqueId()));
		if (item.getLore() != null)
			item.getLore().replaceAll(input -> Utils.replacePlaceholders(input, placeholders, player.getUniqueId()));
		return item.build();
	}

	public static String replaceMath(UUID playerId, String input) {
		return replaceMath(playerId, input, 0, input.length());
	}

	private static String replaceMath(UUID playerId, String input, int start, int end) {
		StringContainer result = new StringContainer(input.length());
		Config user = null;
		for (int i = start; i < input.length() && i < end; ++i) {
			char c = input.charAt(i);
			if (c == 'm' && i + 4 < input.length() && input.charAt(i + 1) == 'i' && input.charAt(i + 2) == 'n'
					&& input.charAt(i + 3) == '(') {
				int times = 0;
				int splitPos = 0;
				int d = i + 4;
				boolean innerMath = false;
				for (; d < input.length(); ++d) {
					char e = input.charAt(d);
					if (e == '(') {
						innerMath = true;
						++times;
					}
					if (e == ',' && times == 0)
						splitPos = d;
					if (e == ')')
						if (--times == -1)
							break;
				}
				if (splitPos != 0 && times == -1) {
					if (innerMath) {
						String firstPart = null;
						if (find(input,'(', i + 4, splitPos))
							firstPart = replaceMath(playerId, input, i + 4, splitPos);
						String secondPart = null;
						if (find(input,'(', splitPos + 1, d))
							secondPart = replaceMath(playerId, input, splitPos + 1, d);
						Number first = firstPart == null ? ParseUtils.getNumber(input, i + 4, splitPos)
								: ParseUtils.getNumber(firstPart);
						Number second = secondPart == null ? ParseUtils.getNumber(input, splitPos + 1, d)
								: ParseUtils.getNumber(secondPart);
						if (first instanceof Double || first instanceof Float || second instanceof Double
								|| second instanceof Float)
							result.append(StringUtils.formatDouble(FormatType.BASIC,
									Math.min(first.doubleValue(), second.doubleValue())));
						else
							result.append(StringUtils.formatDouble(FormatType.BASIC,
									Math.min(first.longValue(), second.longValue())));
					} else {
						Number first = ParseUtils.getNumber(input, i + 4, splitPos);
						Number second = ParseUtils.getNumber(input, splitPos + 1, d);
						if (first instanceof Double || first instanceof Float || second instanceof Double
								|| second instanceof Float)
							result.append(StringUtils.formatDouble(FormatType.BASIC,
									Math.min(first.doubleValue(), second.doubleValue())));
						else
							result.append(StringUtils.formatDouble(FormatType.BASIC,
									Math.min(first.longValue(), second.longValue())));
					}
					i = d;
					continue;
				}
			}
			if (c == 'm' && i + 4 < input.length() && input.charAt(i + 1) == 'a' && input.charAt(i + 2) == 'x'
					&& input.charAt(i + 3) == '(') {
				int times = 0;
				int splitPos = 0;
				int d = i + 4;
				boolean innerMath = false;
				for (; d < input.length(); ++d) {
					char e = input.charAt(d);
					if (e == '(') {
						innerMath = true;
						++times;
					}
					if (e == ',' && times == 0)
						splitPos = d;
					if (e == ')')
						if (--times == -1)
							break;
				}
				if (splitPos != 0 && times == -1) {
					if (innerMath) {
						String firstPart = null;
						if (find(input,'(', i + 4, splitPos))
							firstPart = replaceMath(playerId, input, i + 4, splitPos);
						String secondPart = null;
						if (find(input,'(', splitPos + 1, d))
							secondPart = replaceMath(playerId, input, splitPos + 1, d);
						Number first = firstPart == null ? ParseUtils.getNumber(input, i + 4, splitPos)
								: ParseUtils.getNumber(firstPart);
						Number second = secondPart == null ? ParseUtils.getNumber(input, splitPos + 1, d)
								: ParseUtils.getNumber(secondPart);
						if (first instanceof Double || first instanceof Float || second instanceof Double
								|| second instanceof Float)
							result.append(StringUtils.formatDouble(FormatType.BASIC,
									Math.max(first.doubleValue(), second.doubleValue())));
						else
							result.append(StringUtils.formatDouble(FormatType.BASIC,
									Math.max(first.longValue(), second.longValue())));
					} else {
						Number first = ParseUtils.getNumber(input, i + 4, splitPos);
						Number second = ParseUtils.getNumber(input, splitPos + 1, d);
						if (first instanceof Double || first instanceof Float || second instanceof Double
								|| second instanceof Float)
							result.append(StringUtils.formatDouble(FormatType.BASIC,
									Math.max(first.doubleValue(), second.doubleValue())));
						else
							result.append(StringUtils.formatDouble(FormatType.BASIC,
									Math.max(first.longValue(), second.longValue())));
					}
					i = d;
					continue;
				}
			}
			if (c == 'c' && i + 5 < input.length() && input.charAt(i + 1) == 'a' && input.charAt(i + 2) == 'l'
					&& input.charAt(i + 3) == 'c' && input.charAt(i + 4) == '(') {
				int times = 0;
				int d = i + 5;
				boolean innerMath = false;
				for (; d < input.length(); ++d) {
					char e = input.charAt(d);
					if (e == '(') {
						innerMath = true;
						++times;
					}
					if (e == ')')
						if (--times == -1)
							break;
				}
				if (times == -1) {
					if (innerMath)
						result.append(StringUtils.formatDouble(FormatType.BASIC,
								MathUtils.calculate(replaceMath(playerId, input, i + 5, d))));
					else
						result.append(StringUtils.formatDouble(FormatType.BASIC, MathUtils.calculate(input, i + 5, d)));
					i = d;
					continue;
				}
			}
			if (c == 'u' && i + 4 < input.length() && input.charAt(i + 1) == 's' && input.charAt(i + 2) == 'e'
					&& input.charAt(i + 3) == 'r' && input.charAt(i + 4) == '(') {
				int times = 0;
				int d = i + 5;
				for (; d < input.length(); ++d) {
					char e = input.charAt(d);
					if (e == '(')
						++times;
					if (e == ')')
						if (--times == -1)
							break;
				}
				if (times == -1) {
					if (user == null)
						user = API.getUser(playerId);
					String value = "" + user.getString(input.substring(i + 5, d));
					result.append(ParseUtils.isNumber(value)
							? StringUtils.formatDouble(FormatType.BASIC, ParseUtils.getNumber(value).doubleValue())
									: value);
					i = d;
					continue;
				}
			}
			result.append(c);
		}
		return result.toString();
	}

	private static boolean find(String input, char c, int start, int end) {
		int result = input.indexOf(c, start);
		return result!=-1 && result<end;
	}

	public static List<Condition> createConditions(List<String> stringConditions) {
		List<Condition> conditions = new ArrayList<>();
		for (String action : stringConditions) {
			int splitAt = action.indexOf(':');
			conditions.add(
					ConditionManager.createByName((splitAt == -1 ? action : action.substring(0, splitAt)).toLowerCase(),
							splitAt == -1 ? "" : action.substring(splitAt + 1)));
		}
		return conditions;
	}

	public static List<Action> createActions(GuiCreator holder, List<String> stringActions) {
		List<Action> actions = new ArrayList<>();
		for (String action : stringActions) {
			int splitAt = action.indexOf(':');
			actions.add(
					ActionManager.createByName((splitAt == -1 ? action : action.substring(0, splitAt)).toLowerCase(),
							holder, splitAt == -1 ? "" : action.substring(splitAt + 1)));
		}
		return actions;
	}

	public static void processActions(GuiCreator holder, HolderGUI gui, Player player, Map<String, Object> placeholders,
			String actionName) {
		if (actionName.isEmpty() || "none".equals(actionName))
			return;
		List<Action> actions = holder.getCustomActions().get(actionName);
		if (actions == null)
			BukkitLoader.getPlugin(BukkitLoader.class).getLogger().warning("[GuiExpansion] Not found customAction " + actionName);
		else {
			int pos = 0;
			for (Action action : actions) {
				if (action.shouldSync()) {
					action.runSync(++pos, actions, gui, player, placeholders);
					break;
				}
				action.run(gui, player, placeholders);
				++pos;
			}
		}
	}

	public static int findEndOfPossibleJson(String action, int start) {
		int times = 0;
		int i = start;
		for (; i < action.length(); ++i) {
			char c = action.charAt(i);
			if (c == '{')
				++times;
			if (c == '}')
				--times;
			if (c == ':' && times == 0)
				break;
		}
		return i;
	}

	public static boolean checkForPlaceholders(ItemMaker maker) {
		boolean placeholders = false;
		if (maker.getDisplayName() != null)
			placeholders |= checkForPlaceholders(maker.getDisplayName());
		if (maker.getLore() != null)
			for (String line : maker.getLore())
				placeholders |= checkForPlaceholders(line);
		return placeholders;
	}

	public static String replacePlaceholders(String input, Map<String, Object> placeholders, UUID playerId) {
		if (placeholders != null)
			for (Entry<String, Object> entry : placeholders.entrySet())
				input = input.replace('{' + entry.getKey() + '}',
						entry.getValue() instanceof Number || ParseUtils.isNumber(entry.getValue() + "")
						? StringUtils.formatDouble(FormatType.BASIC,
								(entry.getValue() instanceof Number ? (Number) entry.getValue()
										: ParseUtils.getNumber(entry.getValue() + "")).doubleValue())
								: entry.getValue() + "");
		Config data = GuiCreator.sharedData.get(playerId);
		if(data!=null)
			for (String key : data.getKeys(true))
				input = input
				.replace('[' + key + ']',
						ParseUtils.isNumber(data.getString(key))
						? StringUtils.formatDouble(FormatType.BASIC,
								ParseUtils.getNumber(data.getString(key)).doubleValue())
								: data.getString(key));
		return ColorUtils.colorize(Utils.replaceMath(playerId, PlaceholderAPI.apply(input, playerId)));
	}

	public static boolean checkForPlaceholders(String line) {
		return line.indexOf('[') != -1 && line.indexOf(']') != -1 || line.indexOf('{') != -1 && line.indexOf('}') != -1
				|| line.indexOf('(') != -1 && line.indexOf(')') != -1
				|| line.indexOf('%') != -1 && line.indexOf('%', line.indexOf('%') + 1) != -1;
	}
}
