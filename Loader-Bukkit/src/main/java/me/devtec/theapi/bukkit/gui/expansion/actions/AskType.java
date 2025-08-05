package me.devtec.theapi.bukkit.gui.expansion.actions;

import me.devtec.shared.utility.ParseUtils;

public enum AskType {
	EQUALS, MORE_OR_EQUALS, MORE, LOWER, LOWER_OR_EQUALS, NOT_SAME, CONTAINS;

	public static AskType parseType(String text) {
		for (int i = 0; i < text.length(); ++i) {
			char c = text.charAt(i);
			if (c == '?' && text.charAt(i + 1) == '=')
				return CONTAINS;
			if (c == '=' && text.charAt(i + 1) == '=')
				return EQUALS;
			if (c == '!' && text.charAt(i + 1) == '=')
				return NOT_SAME;
			if (c == '>') {
				if (text.charAt(i + 1) == '=')
					return MORE_OR_EQUALS;
				return MORE;
			}
			if (c == '<') {
				if (text.charAt(i + 1) == '=')
					return LOWER_OR_EQUALS;
				return LOWER;
			}
		}
		return null;
	}

	public boolean compare(String first, String second) {
		first = first.toLowerCase();
		second = second.toLowerCase();
		switch (this) {
		case EQUALS:
			boolean status = false;
			for (String value : second.split("\\|\\|"))
				status |= first.equals(value);
			return status;
		case NOT_SAME:
			return !first.equals(second);
		case CONTAINS:
			status = false;
			for (String value : second.split("\\|\\|")) {
				int star = value.indexOf('*');
				if (star != -1) {
					String prefix = second.substring(0, star);
					String suffix = second.substring(star + 1);
					status |= (prefix.isEmpty() ? true : first.startsWith(prefix))
							&& (suffix.isEmpty() ? true : first.endsWith(suffix));
				} else
					status |= value.isEmpty() ? true : first.contains(value);
			}
			return status;
		case LOWER:
			double firstNumber = ParseUtils.getDouble(first);
			double secondNumber = ParseUtils.getDouble(second);
			return firstNumber < secondNumber;
		case LOWER_OR_EQUALS:
			firstNumber = ParseUtils.getDouble(first);
			secondNumber = ParseUtils.getDouble(second);
			return firstNumber <= secondNumber;
		case MORE:
			firstNumber = ParseUtils.getDouble(first);
			secondNumber = ParseUtils.getDouble(second);
			return firstNumber > secondNumber;
		case MORE_OR_EQUALS:
			firstNumber = ParseUtils.getDouble(first);
			secondNumber = ParseUtils.getDouble(second);
			return firstNumber >= secondNumber;
		default:
			break;
		}
		return false;
	}
}
