package me.devtec.shared.placeholders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderAPI {
	public static Pattern placeholderLookup = Pattern.compile("\\%(.*?)\\%"); // %PLACEHOLDER_NAME%
	
	private static List<PlaceholderExpansion> extensions = new ArrayList<>();
	
	public static void register(PlaceholderExpansion ext) {
		unregister(ext); //Unregister placeholders with same name
		extensions.add(ext);
	}
	
	public static void unregister(PlaceholderExpansion ext) {
		extensions.remove(ext);
		//Lookup for same names
		Iterator<PlaceholderExpansion> iterator = extensions.iterator();
		while(iterator.hasNext()) {
			PlaceholderExpansion reg = iterator.next();
			if(reg.getName().equalsIgnoreCase(ext.getName()))iterator.remove();
		}
	}
	
	public static String apply(String text, UUID player) {
		Matcher match = placeholderLookup.matcher(text);
		while(match.find()) {
			String placeholder = match.group(1);
			for(PlaceholderExpansion ext : extensions) {
				String value = ext.apply(placeholder, player);
				if(value!=null) {
					text=text.replace(match.group(), value);
					break;
				}
			}
		}
		return text;
	}
	
	//Legacy support
	
	public static boolean isRegistered(String extensionName) {
		Iterator<PlaceholderExpansion> iterator = extensions.iterator();
		while(iterator.hasNext()) {
			PlaceholderExpansion reg = iterator.next();
			if(reg.getName().equalsIgnoreCase(extensionName))return true;
		}
		return false;
	}
	
	public static void unregister(String extensionName) {
		Iterator<PlaceholderExpansion> iterator = extensions.iterator();
		while(iterator.hasNext()) {
			PlaceholderExpansion reg = iterator.next();
			if(reg.getName().equalsIgnoreCase(extensionName))iterator.remove();
		}
	}
}
