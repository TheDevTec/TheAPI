package me.Straiker123;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;


public class ScoreboardAPIV2 {
    private Scoreboard v;
    private HashMap<Integer,String> a=new HashMap<Integer, String>();
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
		if(!send || send && ss.getScoreboard() != v && ss.getScoreboard().getObjectives().isEmpty()||ss.getScoreboard()==null) {
			send=true;
	    	ss.setScoreboard(v);
		}
		dd.setDisplayName(title);
		int i = 15;
		for(String s : this.s) {
			if(i!=0) {
				a.put(i, s);
				--i;
			}
		}
		i=15;
		List<String> entry = new ArrayList<String>();
		for(String s : v.getEntries())entry.add(s);
		for(String aa : entry) {
			if(!a.get(i).equals(aa))
			v.resetScores(aa);
			--i;
			}
			for(Integer o : a.keySet()) {
				if(entry.size() < o || entry.size() > o && !entry.get(o).equals(a.get(o)))
				dd.getScore(a.get(o)).setScore(o);
			}
			this.s=new ArrayList<String>();
	}
}
