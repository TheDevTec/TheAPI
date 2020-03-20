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
    private String name,title = "TheAPI";
    private Player ss;
    private Objective dd;
    private List<String> s = new ArrayList<String>();
    @SuppressWarnings("deprecation")
	public ScoreboardAPIV2(Player p) {
    	this.ss=p;
    	name=p.getName();
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
		if(TheAPI.getPlayer(name) ==null)return;
		Scoreboard b = ss.getScoreboard();
		if(!send || send && b != v && b.getObjectives().isEmpty()||b==null) {
			send=true;
	    	ss.setScoreboard(v);
		}
		if(!dd.getDisplayName().equals(title))
		dd.setDisplayName(title);
	    HashMap<Integer,String> a=new HashMap<Integer, String>();
		int i = 15;
		for(String s : this.s) {
			if(i!=0) {
				a.put(i, s);
				--i;
			}
		}
	    HashMap<Integer,String> entry=new HashMap<Integer,String>();
	   List<String> added=new ArrayList<String>();
		for(String df: v.getEntries()) {
			entry.put(dd.getScore(df).getScore(),df);
		}
		for(Integer aa : a.keySet()) {
			try {
			if(entry.get(aa) != null && !a.get(aa).equals(entry.get(aa))) {
			v.resetScores(entry.get(aa));
			entry.remove((int)aa);
			dd.getScore(a.get(aa)).setScore(aa);
			added.add(a.get(aa));
			}}catch(Exception our) {}
			}
			for(Integer o : a.keySet()) {
				if(entry.containsKey(o)==false && !added.contains(a.get(o))) {
				dd.getScore(a.get(o)).setScore(o);
				}
			}
			this.s=new ArrayList<String>();
	}
}
