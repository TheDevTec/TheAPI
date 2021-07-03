package me.devtec.theapi.placeholderapi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import me.devtec.theapi.utils.StringUtils;

public class ThePlaceholderAPI {
	private final static Pattern finder = Pattern.compile("\\%(.*?)\\%"), // pattern for placeholder
			math = Pattern.compile("\\%math\\{((?:\\{??.*?)+)\\}\\%"),
			limiter = Pattern.compile("\\%limiter\\{((?:\\{??.*?)+),[ ]*((?:\\{??.*?)+),[ ]*((?:\\{??.*?)+)\\}\\%");
	
	private final static List<ThePlaceholder> reg = new ArrayList<>();

	public static void register(ThePlaceholder e) {
		if (isRegistered(e))
			return;
		reg.add(e);
	}

	public static void unregister(ThePlaceholder e) {
		if (!isRegistered(e))
			return;
		reg.remove(e);
	}

	public static boolean isRegistered(ThePlaceholder e) {
		return reg.contains(e);
	}

	public static List<ThePlaceholder> getPlaceholders() {
		return new ArrayList<>(reg); // to avoid manipulation with list
	}

	public static List<String> setPlaceholders(Player player, List<String> list) {
		list=new ArrayList<>(list);
		list.replaceAll(a -> setPlaceholders(player, a));
		return list;
	}

	public static Iterator<String> setPlaceholders(Player player, Iterator<String> list) {
		List<String> edited = new ArrayList<String>();
		while (list.hasNext())
			edited.add(setPlaceholders(player, list.next()));
		return edited.iterator();
	}

	public static String setPlaceholders(Player player, String textOrigin) {
		if (textOrigin == null || textOrigin.trim().isEmpty())
			return textOrigin;
		String text = textOrigin;
		Matcher m = math.matcher(text);
		while (m.find()) {
			text = text.replace(m.group(), StringUtils.calculate(PlaceholderAPI.setPlaceholders(player, m.group(1)))+"");
			m = math.matcher(text);
		}
		m = limiter.matcher(text);
		while (m.find()) {
			text = text.replace(m.group(), limit(StringUtils.getDouble(m.group(1)),StringUtils.getDouble(m.group(2)),StringUtils.getDouble(m.group(3)))+"");
			m = limiter.matcher(text);
		}
		Matcher found = finder.matcher(text);
		while (found.find()) {
			String g = found.group();
			String find = g.substring(1, g.length() - 1);
			int v = 0;
			for (Iterator<ThePlaceholder> r = reg.iterator(); r.hasNext();) {
				++v;
				ThePlaceholder get = r.next();
				String toReplace = get.onPlaceholderRequest(player, find);
				if (toReplace != null)
					text = text.replace(g, toReplace);
			}
			if (v != 0)
				continue;
			else
				break;
		}
		m = math.matcher(text);
		while (m.find()) {
			text = text.replace(m.group(), StringUtils.calculate(PlaceholderAPI.setPlaceholders(player, m.group(1)))+"");
			m = math.matcher(text);
		}
		m = limiter.matcher(text);
		while (m.find()) {
			text = text.replace(m.group(), limit(StringUtils.getDouble(m.group(1)),StringUtils.getDouble(m.group(2)),StringUtils.getDouble(m.group(3)))+"");
			m = limiter.matcher(text);
		}
		return text;
	}
	
	private static double limit(double val, double min, double max) {
		if(val<min)val=min;
		if(val>max)val=max;
		return val;
	}
}
