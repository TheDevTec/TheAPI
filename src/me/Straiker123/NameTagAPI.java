package me.Straiker123;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class NameTagAPI {
	String prefix;
	String suffix;
	Player p;
	public NameTagAPI(Player p, String prefix, String suffix) {
		this.p=p;
		this.prefix=prefix;
		this.suffix=suffix;
	}
	/**
	 * Set player name tag
	 * @param teamName
	 * By teamName you can sort players in tablist -> create sorted tablist
	 */
	@SuppressWarnings("deprecation")
	public void setNameTag(String teamName, Scoreboard sb) {
		if(teamName==null)teamName="z";
		if(sb==null)sb=p.getScoreboard();
		
		if(sb.getTeam(teamName)==null)sb.registerNewTeam(teamName);
		Team t = sb.getTeam(teamName);
		
		if(suffix !=null) {
			try {
	            if (suffix.length() > 64) {
	                suffix = suffix.substring(0, 63);
	            }
		t.setSuffix(TheAPI.colorize(suffix));
		}catch(Exception e) {
			try {
            if (suffix.length() > 32) {
                suffix = suffix.substring(0, 31);
            }
    		t.setSuffix(TheAPI.colorize(suffix));
			}catch(Exception es) {
	            if (suffix.length() > 16) {
	                suffix = suffix.substring(0, 15);
	            }
	    		t.setSuffix(TheAPI.colorize(suffix));
			}
		}
		}
		if(prefix !=null) {
			try {
	            if (prefix.length() > 64) {
	            	prefix = prefix.substring(0, 63);
	            }
	    		t.setPrefix(TheAPI.colorize(prefix));
		}catch(Exception e) {
			try {
            if (prefix.length() > 32) {
            	prefix = prefix.substring(0, 31);
            }
    		t.setPrefix(TheAPI.colorize(prefix));
			}catch(Exception es) {
	            if (prefix.length() > 16) {
	            	prefix = suffix.substring(0, 15);
	            }
	    		t.setPrefix(TheAPI.colorize(prefix));
			}
		}
    		if (TheAPI.isNewVersion())
    			t.setColor(fromPrefix(prefix));
		}
		t.addPlayer(p);
	}

	/**
	 * Reset player name tag to default
	 */
	public void resetNameTag() {
		for(Team t : p.getScoreboard().getTeams())t.unregister();
		p.setScoreboard(p.getServer().getScoreboardManager().getNewScoreboard());
	}
	
	private ChatColor fromPrefix(String prefix) {
		char colour = '\u0000';
		char[] chars = prefix.toCharArray();
		for (int i = 0; i < chars.length; ++i) {
    char code;
    char at = chars[i];
    if (at != '\u00a7' && at != '&' || i + 1 >= chars.length || ChatColor.getByChar((char)(code = chars[i + 1])) == null) continue;
    colour = code;
		}
		return colour == '\u0000' ? ChatColor.RESET : ChatColor.getByChar((char)colour);
	}

}
