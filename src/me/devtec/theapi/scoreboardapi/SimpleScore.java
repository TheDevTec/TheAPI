package me.devtec.theapi.scoreboardapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.entity.Player;

import me.devtec.theapi.TheAPI;

public class SimpleScore {
	private static final HashMap<String, ScoreboardAPI> scores = new HashMap<>();
	private static boolean ver = TheAPI.isNewVersion();
	private String name = "TheAPI";
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
			if(sb.getLines().size()>lines.size())
			sb.removeUpperLines(lines.size()-1);
			if(ver) {
				int i = 0;
				for (String line : lines)
					sb.setLine(i++, line);
			}else {
				int i = lines.size()>16?16:lines.size();
				for (String line : lines)
					sb.setLine(--i, line);
			}
		}
		lines.clear();
	}
	
	private ScoreboardAPI getOrCreate(Player player) {
		ScoreboardAPI a = scores.getOrDefault(player.getName(), null);
		if(a==null) {
			a=new ScoreboardAPI(player, ver?0:-1);
			scores.put(player.getName(), a);
		}
		return a;
	}
}
