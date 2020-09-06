package me.DevTec.TheAPI.PlaceholderAPI;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import me.DevTec.TheAPI.Utils.StringUtils;

public class ThePlaceholderAPI {
	private final static Pattern finder = Pattern.compile("\\%([^A-Za-z0-9]|[A-Za-z0-9])+\\%"),
			math = Pattern.compile("\\%math(\\{(?:\\{??[^A-Za-z\\{][ 0-9+*/^%()~.-]*?\\}))\\%"),
			toggle = Pattern.compile("\\%toggle(\\{[ ]*(?:([tT][rR][uU][eE]|[yY][eE][sS]|[nN][oO]|[oO][nN]|[oO][fF][fF]|[fF][aA][lL][sS][eE])*?[ ]*\\}))\\%");
	private final static List<ThePlaceholder> reg = Lists.newArrayList();
	
	public static void register(ThePlaceholder e) {
		if(isRegistered(e))return;
		reg.add(e);
	}
	
	public static void unregister(ThePlaceholder e) {
		if(!isRegistered(e))return;
		reg.add(e);
	}
	
	public static boolean isRegistered(ThePlaceholder e) {
		return reg.contains(e);
	}
	
	public static List<ThePlaceholder> getPlaceholders() {
		return reg;
	}

	public static List<String> setPlaceholders(Player player, List<String> list) {
		List<String> edited = Lists.newArrayList();
		for(Iterator<String> a = list.iterator(); a.hasNext();)edited.add(setPlaceholders(player, a.next()));
		return edited;
	}
	
	public static Iterator<String> setPlaceholders(Player player, Iterator<String> list) {
		List<String> edited = Lists.newArrayList();
		while(list.hasNext())edited.add(setPlaceholders(player, list.next()));
		return edited.iterator();
	}
	
	public static String setPlaceholders(Player player, String text) {
		while(true) { //math placeholder
			Matcher m = math.matcher(text);
			int v = 0;
			while(m.find()) {
				++v;
				String w = m.group();
				text=text.replace(w, (""+StringUtils.calculate(w.substring(6,w.length()-2))));
			}
			if(v!=0)continue;
			else break;
		}
		while(true) { //toggle placeholder
			Matcher m = toggle.matcher(text);
			int v = 0;
			while(m.find()) {
				++v;
				String w = m.group();
				if(w.equalsIgnoreCase("false")||w.equalsIgnoreCase("off")||w.equalsIgnoreCase("no"))
				text=text.replace(w, "true");
				if(w.equalsIgnoreCase("true")||w.equalsIgnoreCase("on")||w.equalsIgnoreCase("yes"))
				text=text.replace(w, "false");
			}
			if(v!=0)continue;
			else break;
		}
		Matcher found = finder.matcher(text);
		while(found.find()) {
			String g = found.group();
		String find = g.substring(1,g.length()-1);
		int v = 0;
		for(Iterator<ThePlaceholder> r = reg.iterator(); r.hasNext();) {
			++v;
			ThePlaceholder get = r.next();
			String toReplace = get.onPlaceholderRequest(player, find);
			if(toReplace!=null)
			text=text.replace("%"+find+"%", toReplace);
		}
		if(v!=0)continue;
		else
		break;
		}
		return text;
	}
}
