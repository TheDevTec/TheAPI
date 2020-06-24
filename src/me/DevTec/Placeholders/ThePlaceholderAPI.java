package me.DevTec.Placeholders;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import me.DevTec.TheAPI;

public class ThePlaceholderAPI {
	private final static Pattern finder = Pattern.compile("\\%([^A-Za-z0-9]|[A-Za-z0-9])+\\%"),
			math = Pattern.compile("\\%math(\\{(?:\\{??[^A-Za-z\\{][0-9+*/^%()~.-]*?\\}))\\%");
	private final static List<ThePlaceholder> reg = Lists.newArrayList();
	
	public void register(ThePlaceholder e) {
		if(isRegistered(e))return;
		reg.add(e);
	}
	
	public void unregister(ThePlaceholder e) {
		if(!isRegistered(e))return;
		reg.add(e);
	}
	
	public boolean isRegistered(ThePlaceholder e) {
		return reg.contains(e);
	}
	
	public List<ThePlaceholder> getPlaceholders() {
		return reg;
	}
	
	public List<String> setPlaceholders(Player player, List<String> list) {
		List<String> edited = Lists.newArrayList();
		for(Iterator<String> a = list.iterator(); a.hasNext();)edited.add(setPlaceholders(player, a.next()));
		return edited;
	}
	
	public Iterator<String> setPlaceholders(Player player, Iterator<String> list) {
		List<String> edited = Lists.newArrayList();
		while(list.hasNext())edited.add(setPlaceholders(player, list.next()));
		return edited.iterator();
	}
	
	public String setPlaceholders(Player player, String text) {
		text=replaceMath(text);
		Matcher found = finder.matcher(text);
		while(found.find()) {
			String g = found.group();
		String find = g.substring(1,g.length()-1);
		for(Iterator<ThePlaceholder> r = reg.iterator(); r.hasNext();) {
			ThePlaceholder get = r.next();
			String toReplace = get.onPlaceholderRequest(player, find);
			if(toReplace!=null)
			text=toReplace;
			}
		}
		return text;
	}
	
	private String replaceMath(String text) {
		Matcher m = math.matcher(text);
		int v = 0;
		while(m.find()) {
			++v;
			String w = m.group();
			text=text.replace(w, (""+TheAPI.getStringUtils().calculate(w.substring(6,w.length()-2))).replaceAll("\\.0", ""));
		}
		if(v!=0)
		return replaceMath(text);
		return text;
	}
}
