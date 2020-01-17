package me.Straiker123;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardAPI {
	Player p;
	HashMap<Integer,String> map = new HashMap<Integer, String>();
	String title = "&bTheAPI";
	Scoreboard s;
	public ScoreboardAPI(Player p, Scoreboard sb) {
		this.p=p;
		if(sb==null)s=p.getServer().getScoreboardManager().getNewScoreboard();
		else
			s=sb;
	}
	
	public void setTitle(String title) {
		if(title!=null)
		this.title=title;
	}
	
	public void addLine(String line, int position) {
		map.put(position,TheAPI.colorize(line));
	}
	public Scoreboard getScoreboard() {
		return s;
	}
	
	@SuppressWarnings("deprecation")
	public void create() {
		Objective d = s.getObjective("a");
		 if(d!=null)
			 d.unregister();
			 d=s.registerNewObjective("a", "dummy");
		d.setDisplaySlot(DisplaySlot.SIDEBAR);
		d.setDisplayName(TheAPI.colorize(title));
		if(map.isEmpty()==false && map!=null)
		  for(Integer w:map.keySet()) {
			  String s = map.get(w);
      		String tes = s;
			  try {
	        	if(tes.length()>64)tes=tes.substring(0,63);
	        	d.getScore(tes).setScore(w);
	        	}catch(Exception error) {
	        		try {
			        	if(tes.length()>32)tes=tes.substring(0,31);
			        d.getScore(tes).setScore(w);
	        		}catch(Exception error2) {
			        	if(tes.length()>16)tes=tes.substring(0,15);
			        d.getScore(tes).setScore(w);
	        		}
	        	}
			}
		p.setScoreboard(s);
	}
}
