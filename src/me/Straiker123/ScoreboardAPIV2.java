package me.Straiker123;

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
    private HashMap<Integer,String> a=new HashMap<Integer, String>();
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
    	if(title!=null && !title.equals(""))
    	this.title=TheAPI.colorize(title);
    }
    private int cu;
    public void addLine(String line) {
    	a.put(15-cu,TheAPI.colorize(line));
    	++cu;
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
		cu=0;
		if(TheAPI.getPlayer(name) ==null)return;
		Scoreboard b = ss.getScoreboard();
		if(!send || send && b != v && b.getObjectives().isEmpty()||b==null) {
			send=true;
	    	ss.setScoreboard(v);
		}
		if(!dd.getDisplayName().equals(title))
		dd.setDisplayName(title);
	    HashMap<Integer,String> entry=new HashMap<Integer,String>();
		for(String df: v.getEntries()) {
			entry.put(dd.getScore(df).getScore(),df);
		}
		for(Integer aa : a.keySet()) {
			if(entry.containsKey(aa)) {
			if(!a.get(aa).equals(entry.get(aa))) {
			v.resetScores(entry.get(aa));
			dd.getScore(a.get(aa)).setScore(aa);
			}
			}else
				dd.getScore(a.get(aa)).setScore(aa);
			}
			a.clear();
	}
}
