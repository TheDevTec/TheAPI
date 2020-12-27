package me.devtec.theapi.scoreboardapi;

import java.util.Collection;

import org.bukkit.entity.Player;

import me.devtec.theapi.utils.datakeeper.collections.UnsortedSet;
import me.devtec.theapi.utils.datakeeper.maps.UnsortedMap;

public class SimpleScore {
	private static final UnsortedMap<String, ScoreboardAPI> scores = new UnsortedMap<>();
	private String name = "TheAPI";
	private final UnsortedSet<String> lines = new UnsortedSet<>();
	
	public SimpleScore addLine(String line) {
		lines.add(line);
		return this;
	}
	
	public SimpleScore addLines(Collection<String> lines) {
		lines.addAll(lines);
		return this;
	}
	
	public SimpleScore setTitle(String title) {
		name=title;
		return this;
	}
	
	public void send(Player...players) {
		for(Player a : players) {
			ScoreboardAPI sb = getOrCreate(a);
			sb.setTitle(name);
			if(sb.getLines().size()>lines.size())
			sb.removeUpperLines(lines.size()-1);
			int i = 0;
			for (String line : lines) {
				sb.setLine(i++, line);
			}
		}
		lines.clear();
	}
	
	private ScoreboardAPI getOrCreate(Player player) {
		ScoreboardAPI a = scores.getOrDefault(player.getName(), null);
		if(a==null) {
			a=new ScoreboardAPI(player, 0);
			scores.put(player.getName(), a);
		}
		return a;
	}
}
