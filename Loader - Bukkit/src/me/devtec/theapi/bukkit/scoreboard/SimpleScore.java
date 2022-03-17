package me.devtec.theapi.bukkit.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import me.devtec.shared.Ref;

public class SimpleScore {
	public static final Map<String, ScoreboardAPI> scores = new HashMap<>();
	private String name = "";
	private final ArrayList<String> lines = new ArrayList<>();
	
	public SimpleScore addLine(String line) {
		lines.add(line);
		return this;
	}
	
	public SimpleScore addLines(Collection<String> lines) {
		this.lines.addAll(lines);
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
			if(!Ref.isNewerThan(7)) {
				Collections.reverse(lines);
				if(lines.size()>15) {
					for(int i = 15; i < lines.size(); ++i)
					lines.remove(i);
				}
			}
			if(sb.getLines().size()>lines.size())
				sb.removeUpperLines(lines.size()-1);
			int i = 0;
			for (String line : lines)
				sb.setLine(++i, line);
		}
		lines.clear();
	}
	
	private ScoreboardAPI getOrCreate(Player player) {
		ScoreboardAPI a = scores.get(player.getName());
		if(a==null)
			scores.put(player.getName(), a=new ScoreboardAPI(player, Ref.isNewerThan(7)?0:-1));
		return a;
	}
}
