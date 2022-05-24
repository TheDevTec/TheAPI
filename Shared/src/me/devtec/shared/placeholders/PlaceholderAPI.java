package me.devtec.shared.placeholders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderAPI {
	public static Pattern placeholderLookup = Pattern.compile("\\%(.*?)\\%"); // %PLACEHOLDER_NAME%

	private static List<PlaceholderExpansion> extensions = new ArrayList<>();

	public static void register(PlaceholderExpansion ext) {
		PlaceholderAPI.unregister(ext); // Unregister placeholders with same name
		PlaceholderAPI.extensions.add(ext);
	}

	public static void unregister(PlaceholderExpansion ext) {
		PlaceholderAPI.extensions.remove(ext);
		// Lookup for same names
		Iterator<PlaceholderExpansion> iterator = PlaceholderAPI.extensions.iterator();
		while (iterator.hasNext()) {
			PlaceholderExpansion reg = iterator.next();
			if (reg.getName().equalsIgnoreCase(ext.getName()))
				iterator.remove();
		}
	}

	public static String apply(String text, UUID player) {
		Matcher match = PlaceholderAPI.placeholderLookup.matcher(text);
		while (match.find()) {
			String placeholder = match.group(1);
			for (PlaceholderExpansion ext : PlaceholderAPI.extensions) {
				String value = ext.apply(placeholder, player);
				if (value != null) {
					text = text.replace(match.group(), value);
					break;
				}
			}
		}
		return text;
	}

	public static List<String> apply(List<String> text, UUID player) {
		ListIterator<String> list = text.listIterator();
		while (list.hasNext()) {
			String val = list.next();
			list.set(PlaceholderAPI.apply(val, player));
		}
		return text;
	}

	public static PlaceholderExpansion getExpansion(String extensionName) {
		Iterator<PlaceholderExpansion> iterator = PlaceholderAPI.extensions.iterator();
		while (iterator.hasNext()) {
			PlaceholderExpansion reg = iterator.next();
			if (reg.getName().equalsIgnoreCase(extensionName))
				return reg;
		}
		return null;
	}

	public static boolean isRegistered(String extensionName) {
		Iterator<PlaceholderExpansion> iterator = PlaceholderAPI.extensions.iterator();
		while (iterator.hasNext()) {
			PlaceholderExpansion reg = iterator.next();
			if (reg.getName().equalsIgnoreCase(extensionName))
				return true;
		}
		return false;
	}

	public static void unregister(String extensionName) {
		Iterator<PlaceholderExpansion> iterator = PlaceholderAPI.extensions.iterator();
		while (iterator.hasNext()) {
			PlaceholderExpansion reg = iterator.next();
			if (reg.getName().equalsIgnoreCase(extensionName))
				iterator.remove();
		}
	}
}
