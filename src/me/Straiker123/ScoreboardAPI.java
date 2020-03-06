package me.Straiker123;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardAPI {
	Player p;
	HashMap<Integer,String> map = new HashMap<Integer, String>();
	String title = "&bTheAPI";
	Scoreboard s;
	Team a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,q;
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
	private void prepare() {
		Objective dg = s.getObjective("a");
		 if(dg!=null)
			 dg.unregister();
			 dg=s.registerNewObjective("a", "dummy");
		dg.setDisplaySlot(DisplaySlot.SIDEBAR);
		dg.setDisplayName(TheAPI.colorize(title));
		int isf = 0;
		if(map.isEmpty()==false && map!=null)
		  for(Integer w:map.keySet()) {
			  if(isf >= 16)break;
			  String s = map.get(w);
			  if(isf==0) {
				  if(this.s.getTeam(""+isf) == null)
				  a=this.s.registerNewTeam(""+isf);
				  else
					  a=this.s.getTeam(""+isf);
				  a.setDisplayName(s);
			  }
			  if(isf==1) {
				  if(this.s.getTeam(""+isf) == null)
				  b=this.s.registerNewTeam(""+isf);
				  else
					  b=this.s.getTeam(""+isf);
				  b.setDisplayName(s);
			  }
				  if(isf==2) {
					  if(this.s.getTeam(""+isf) == null)
						  c=this.s.registerNewTeam(""+isf);
						  else
							  c=this.s.getTeam(""+isf);
					  c.setDisplayName(s);
				  }
				  if(isf==3) {
					  if(this.s.getTeam(""+isf) == null)
						  d=this.s.registerNewTeam(""+isf);
						  else
							  d=this.s.getTeam(""+isf);
					  d.setDisplayName(s);
				  }
				  if(isf==4) {
					  if(this.s.getTeam(""+isf) == null)
						  e=this.s.registerNewTeam(""+isf);
						  else
							  e=this.s.getTeam(""+isf);
					  e.setDisplayName(s);
				  }
				  if(isf==5) {
					  if(this.s.getTeam(""+isf) == null)
						  f=this.s.registerNewTeam(""+isf);
						  else
							  f=this.s.getTeam(""+isf);
					  f.setDisplayName(s);
				  }
				  if(isf==6) {
					  if(this.s.getTeam(""+isf) == null)
						  g=this.s.registerNewTeam(""+isf);
						  else
							  g=this.s.getTeam(""+isf);
					  g.setDisplayName(s);
				  }
				  if(isf==7) {
					  if(this.s.getTeam(""+isf) == null)
						  h=this.s.registerNewTeam(""+isf);
						  else
							  h=this.s.getTeam(""+isf);
					  h.setDisplayName(s);
				  }
				  if(isf==8) {
					  if(this.s.getTeam(""+isf) == null)
						  i=this.s.registerNewTeam(""+isf);
						  else
							  i=this.s.getTeam(""+isf);
					  i.setDisplayName(s);
				  }
				  if(isf==9) {
					  if(this.s.getTeam(""+isf) == null)
						  j=this.s.registerNewTeam(""+isf);
						  else
							  j=this.s.getTeam(""+isf);
					  j.setDisplayName(s);
				  }
				  if(isf==10) {
					  if(this.s.getTeam(""+isf) == null)
						  k=this.s.registerNewTeam(""+isf);
						  else
							  k=this.s.getTeam(""+isf);
					  k.setDisplayName(s);
				  }
				  if(isf==11) {
					  if(this.s.getTeam(""+isf) == null)
						  l=this.s.registerNewTeam(""+isf);
						  else
							  l=this.s.getTeam(""+isf);
					  l.setDisplayName(s);
				  }
				  if(isf==12) {
					  if(this.s.getTeam(""+isf) == null)
					  m=this.s.registerNewTeam(""+isf);
					  else
						  m=this.s.getTeam(""+isf);
					  m.setDisplayName(s);
				  }
				  if(isf==13) {
					  if(this.s.getTeam(""+isf) == null)
						  n=this.s.registerNewTeam(""+isf);
						  else
							  n=this.s.getTeam(""+isf);
					  n.setDisplayName(s);
				  }
				  if(isf==14) {
					  if(this.s.getTeam(""+isf) == null)
						  o=this.s.registerNewTeam(""+isf);
						  else
							  o=this.s.getTeam(""+isf);
					  o.setDisplayName(s);
				  }
				  if(isf==15) {
					  if(this.s.getTeam(""+isf) == null)
						  q=this.s.registerNewTeam(""+isf);
						  else
							  q=this.s.getTeam(""+isf);
					  q.setDisplayName(s);
				  }
				  ++isf;
		  }
		isf=0;
		  }

	public void create() {
		prepare();
		Objective dg = s.getObjective("a");
		int isf = 0;
		if(map.isEmpty()==false && map!=null)
		  for(Integer w:map.keySet()) {
			  if(isf >= 16)break;
			  if(isf==0)
		  dg.getScore(a.getDisplayName()).setScore(w);
			  if(isf==1)
		  dg.getScore(b.getDisplayName()).setScore(w);
			  if(isf==2)
		  dg.getScore(c.getDisplayName()).setScore(w);
			  if(isf==3)
		  dg.getScore(d.getDisplayName()).setScore(w);
			  if(isf==4)
		  dg.getScore(e.getDisplayName()).setScore(w);
			  if(isf==5)
		  dg.getScore(f.getDisplayName()).setScore(w);
			  if(isf==6)
		  dg.getScore(g.getDisplayName()).setScore(w);
			  if(isf==7)
		  dg.getScore(h.getDisplayName()).setScore(w);
			  if(isf==8)
		  dg.getScore(i.getDisplayName()).setScore(w);
			  if(isf==9)
		  dg.getScore(j.getDisplayName()).setScore(w);
			  if(isf==10)
		  dg.getScore(k.getDisplayName()).setScore(w);
			  if(isf==11)
		  dg.getScore(l.getDisplayName()).setScore(w);
			  if(isf==12)
		  dg.getScore(m.getDisplayName()).setScore(w);
			  if(isf==13)
		  dg.getScore(n.getDisplayName()).setScore(w);
			  if(isf==14)
		  dg.getScore(o.getDisplayName()).setScore(w);
			  if(isf==15)
		  dg.getScore(q.getDisplayName()).setScore(w);
			  ++isf;
		  }
		p.setScoreboard(s);
	}
}
