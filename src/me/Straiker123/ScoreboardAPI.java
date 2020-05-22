package me.Straiker123;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.Straiker123.TheAPI;

public class ScoreboardAPI {
	private Scoreboard sb;
	private String f = "None";
	private Objective a;

	@SuppressWarnings("deprecation")
	public ScoreboardAPI(Player player) {
		sb = player.getServer().getScoreboardManager().getNewScoreboard();
		a = sb.getObjective("a");
		if (a == null)
			a = sb.registerNewObjective("a", "dummy");
		a.setDisplaySlot(DisplaySlot.SIDEBAR);
		player.setScoreboard(sb);
	}

	public synchronized void setName(String name) {
		if (name != null && !f.equals(TheAPI.colorize(name)))
			a.setDisplayName(TheAPI.colorize(name));
	}

	public synchronized void setDisplayName(String name) {
		setName(name);
	}

	public synchronized String getLine(int line) {
		if(sb.getTeam(line+"")!=null)
		return sb.getTeam(line+"").getPrefix();
		return null;
	}

	public synchronized void removeLine(int line) {
		sb.resetScores(line+"");
		if(sb.getTeam(line+"")!=null)
		sb.getTeam(line+"").unregister();
	}
	
	public synchronized void setLine(int line, String value) {
		if (value == null)return;
		value = TheAPI.colorize(value);
        Team t = sb.getTeam(line+"");
        if(t == null) {
        	t=sb.registerNewTeam(line+"");
            t.addEntry(line+"");
        }
        if(!t.getPrefix().equals(value)) {
        t.setPrefix(value);
        a.getScore(line+"").setScore(line);
        }
	}
}