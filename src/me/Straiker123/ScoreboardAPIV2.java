package me.Straiker123;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


public class ScoreboardAPIV2 {
    private Scoreboard v;
    private HashMap<Integer,Team> a=new HashMap<Integer, Team>();
    private String title = "TheAPI";
    private Player ss;
    private Objective dd;
    private List<String> s = new ArrayList<String>();
    @SuppressWarnings("deprecation")
	public ScoreboardAPIV2(Player p) {
    	this.ss=p;
    	v=p.getServer().getScoreboardManager().getNewScoreboard();
    	if(v.getObjective("a")==null) {
    		dd=v.registerNewObjective("a", "dummy");
    	}else
    		dd=v.getObjective("a");
		dd.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public void setTitle(String title) {
    	if(title!=null)
    	this.title=TheAPI.colorize(title);
    }
    
    public void addLine(String line) {
    	s.add(TheAPI.colorize(line));
    }
    
    public Scoreboard getScoreboard() {
    	return v;
    }
    
    public Objective getObjective() {
    	return dd;
    }
    
    public Player getPlayer() {
    	return ss;
    }
    
    public void setLines(List<String> lines) {
    	for(String d : lines)addLine(d);
    }
    
    boolean send = false;
	public void create() {
		if(!send || ss.getScoreboard() != v && ss.getScoreboard().getObjectives().isEmpty()) {
			send=true;
	    	ss.setScoreboard(v);
		}
		dd.setDisplayName(title);
		List<String> items = Arrays.asList("a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p");
		int i = 15;
		for(String s : this.s) {
			if(i!=0) {
				Team b= v.getTeam(items.get(i));
				if(b==null)b=v.registerNewTeam(items.get(i));
				b.setDisplayName(s);
				a.put(i, b);
				--i;
			}
		}
		i=15;
		List<String> entry = new ArrayList<String>();
		for(String s : v.getEntries())entry.add(s);
		for(String aa : entry) {
			if(!a.get(i).getDisplayName().equals(aa))
			v.resetScores(aa);
			--i;
			}
			for(Integer o : a.keySet()) {
				if(entry.size() < o || entry.size() > o && !entry.get(o).equals(a.get(o).getDisplayName()))
				dd.getScore(a.get(o).getDisplayName()).setScore(o);
			}
			this.s=new ArrayList<String>();
	}
}
