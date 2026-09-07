package me.devtec.theapi.bukkit.gui.expansion.actions;

import java.util.regex.Pattern;

import me.devtec.shared.utility.ParseUtils;

public enum AskType {
	EQUALS, MORE_OR_EQUALS, MORE, LOWER, LOWER_OR_EQUALS, NOT_SAME, CONTAINS, NOT_CONTAINS, REGEX;

	public static AskType parseType(String text) {
		for (int i = 0, max = text.length() - 1; i < text.length(); ++i) {
			char c = text.charAt(i);

			if (i < max) {
				char next = text.charAt(i + 1);

				if (c == '?')
					switch (next) {
					case '=':
						return CONTAINS;
					case '!':
						return NOT_CONTAINS;
					case '?':
						return REGEX;
					default:
						break;
					}

				if (c == '=')
					if (next == '=') return EQUALS;

				if (c == '!')
					if (next == '=') return NOT_SAME;

				if (c == '>') return next == '=' ? MORE_OR_EQUALS : MORE;
				if (c == '<') return next == '=' ? LOWER_OR_EQUALS : LOWER;
			} else {
				if (c == '>') return MORE;
				if (c == '<') return LOWER;
			}
		}
		return null;
	}

	public boolean compare(String first, String second) {
		switch (this) {
		case EQUALS:
			return equalsAnyIgnoreCase(first, second);

		case NOT_SAME:
			return !equalsAnyIgnoreCase(first, second);

		case REGEX:
			return Pattern.compile(second, Pattern.CASE_INSENSITIVE).matcher(first).find();

		case CONTAINS:
			return containsAnyIgnoreCase(first, second);

		case NOT_CONTAINS:
			return !containsAnyIgnoreCase(first, second);

		case LOWER:
			return ParseUtils.getDouble(first) < ParseUtils.getDouble(second);

		case LOWER_OR_EQUALS:
			return ParseUtils.getDouble(first) <= ParseUtils.getDouble(second);

		case MORE:
			return ParseUtils.getDouble(first) > ParseUtils.getDouble(second);

		case MORE_OR_EQUALS:
			return ParseUtils.getDouble(first) >= ParseUtils.getDouble(second);

		default:
			return false;
		}
	}

	private static boolean equalsAnyIgnoreCase(String first, String values) {
		int from = 0;

		while (true) {
			int at = values.indexOf("||", from);
			int end = at == -1 ? values.length() : at;

			if (regionEqualsIgnoreCase(first, values, from, end))
				return true;

			if (at == -1)
				return false;

			from = at + 2;
		}
	}

	private static boolean containsAnyIgnoreCase(String first, String values) {
		int from = 0;

		while (true) {
			int at = values.indexOf("||", from);
			int end = at == -1 ? values.length() : at;

			if (containsValueIgnoreCase(first, values, from, end))
				return true;

			if (at == -1)
				return false;

			from = at + 2;
		}
	}

	private static boolean containsValueIgnoreCase(String input, String source, int from, int to) {
		if (from == to)
			return true;

		int star = source.indexOf('*', from);

		if (star < 0 || star >= to)
			return indexOfIgnoreCase(input, source, from, to) != -1;

		int inputAt = 0;

		if (star > from) {
			int prefixLength = star - from;

			if (input.length() < prefixLength || !input.regionMatches(true, 0, source, from, prefixLength))
				return false;

			inputAt = prefixLength;
		}

		int patternAt = star + 1;

		while (true) {
			star = source.indexOf('*', patternAt);

			if (star < 0 || star >= to) {
				int suffixLength = to - patternAt;

				if (suffixLength == 0)
					return true;

				int suffixAt = input.length() - suffixLength;

				return suffixAt >= inputAt
						&& input.regionMatches(true, suffixAt, source, patternAt, suffixLength);
			}

			int partLength = star - patternAt;

			if (partLength != 0) {
				int found = indexOfIgnoreCase(input, source, patternAt, star, inputAt);

				if (found == -1)
					return false;

				inputAt = found + partLength;
			}

			patternAt = star + 1;
		}
	}

	private static boolean regionEqualsIgnoreCase(String value, String source, int from, int to) {
		int length = to - from;
		return value.length() == length && value.regionMatches(true, 0, source, from, length);
	}

	private static int indexOfIgnoreCase(String value, String source, int sourceFrom, int sourceTo) {
		return indexOfIgnoreCase(value, source, sourceFrom, sourceTo, 0);
	}

	private static int indexOfIgnoreCase(String value, String source, int sourceFrom, int sourceTo, int fromIndex) {
		int length = sourceTo - sourceFrom;

		if (length == 0)
			return fromIndex;

		int max = value.length() - length;

		for (int i = fromIndex; i <= max; ++i)
			if (value.regionMatches(true, i, source, sourceFrom, length))
				return i;

		return -1;
	}
}
