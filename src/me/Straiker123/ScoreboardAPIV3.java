package me.Straiker123;

import java.util.TreeMap;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.Straiker123.TheAPI;

public class ScoreboardAPIV3 {
	private TreeMap<Integer, String> lines = new TreeMap<Integer, String>();
	private Scoreboard sb;
	private Objective a;

	@SuppressWarnings("deprecation")
	public ScoreboardAPIV3(Player player) {
		sb = player.getServer().getScoreboardManager().getNewScoreboard();
		a = sb.getObjective("a");
		if (a == null)
			a = sb.registerNewObjective("a", "dummy");
		a.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(sb);
	}

	public void setName(String name) {
		if (name != null)
			a.setDisplayName(TheAPI.colorize(name));
	}

	public void setDisplayName(String name) {
		if (name != null)
			a.setDisplayName(TheAPI.colorize(name));
	}

	public void removeLine(int line) {
		if (!lines.containsKey(line))
			return;
		String team = lines.get(line);
		if (team == null)
			return;
		lines.remove(line);
		sb.resetScores(team);
	}

	public void setLine(int line, String value) {
		if (value == null)
			return;
		value = TheAPI.colorize(value);
		String team = lines.containsKey(line) ? lines.get(line) : lines.put(line, null);
		if (team != null && team.equals(value))
			return;
		if (team != null)
			sb.resetScores(team);
		lines.put(line, value);
		a.getScore(value).setScore(line);
	}
}