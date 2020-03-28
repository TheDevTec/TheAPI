package me.Straiker123;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

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
	 * @see see Warning, this method change whole player name, plugins these checking UUID will kick/ban you from server, for ex.: UUIDSpoofFix
	 * @param name
	 * New player name
	 */
	@SuppressWarnings("deprecation")
	public void setPlayerName(String name) {
	    try {
	        Method getHandle = p.getClass().getMethod("getHandle");
	        Object entityPlayer = getHandle.invoke(p);
	        boolean gameProfileExists = false;
	        try {
	            Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
	            gameProfileExists = true;
	        }catch(Exception e) {
	        	
	        }
	        try {
	            Class.forName("com.mojang.authlib.GameProfile");
	            gameProfileExists = true;
	        }catch(Exception e) {
	        	
	        }
	        if (!gameProfileExists) {
	            Field nameField = entityPlayer.getClass().getSuperclass().getDeclaredField("name");
	            nameField.setAccessible(true);
	            nameField.set(entityPlayer, name);
	        } else {
	            Object profile = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);
	            Field ff = profile.getClass().getDeclaredField("name");
	            ff.setAccessible(true);
	            ff.set(profile, name);
	        }
	            for (Player p : TheAPI.getOnlinePlayers()) {
	                p.hidePlayer(this.p);
	                p.showPlayer(this.p);
	        }
	    } catch (Exception e) {
	    }
	}
	/**
	 * @see see Set player name tag
	 * @param teamName
	 * By teamName you can sort players in tablist -> create sorted tablist
	 */
	public void setNameTag(Team team) {
		setNameTag(team.getName(), p.getScoreboard());
	}
	/**
	 * Set player name tag
	 * @param teamName
	 * By teamName you can sort players in tablist -> create sorted tablist
	 */
	public void setNameTag(String team) {
		for(Player p : TheAPI.getOnlinePlayers())
		setNameTag(team, p.getScoreboard());
	}
	/**
	 * @see see Set player name tag
	 * @param teamName
	 * By teamName you can sort players in tablist -> create sorted tablist
	 */
	@SuppressWarnings("deprecation")
	public void setNameTag(String teamName, Scoreboard sb) {
		if(teamName==null)teamName="z";
		if(sb==null)sb=p.getScoreboard();
        if (teamName.length() > 16) {
        	teamName = teamName.substring(0, 15);
        }
		if(sb.getTeam(teamName)==null)sb.registerNewTeam(teamName);
		Team t = sb.getTeam(teamName);
		
		try {
		t.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
		}catch(Exception | NoSuchMethodError nope) {}
		if(suffix !=null) {
			if(TheAPI.isOlder1_9()) {
	            if (suffix.length() > 16) {
	            	suffix = suffix.substring(0, 15);
	            }
	            if(!t.getSuffix().equals(TheAPI.colorize(suffix)))
	    		t.setSuffix(TheAPI.colorize(suffix));
			}else {
			try {
	            if (suffix.length() > 32) {
	                suffix = suffix.substring(0, 31);
	            }
	            if(!t.getSuffix().equals(TheAPI.colorize(suffix)))
		t.setSuffix(TheAPI.colorize(suffix));
		}catch(Exception e) {}
		}}
		if(prefix !=null) {
			if(TheAPI.isOlder1_9()) {
	            if (prefix.length() > 16) {
	            	prefix = suffix.substring(0, 15);
	            }
	            if(!t.getPrefix().equals(TheAPI.colorize(prefix)))
	    		t.setPrefix(TheAPI.colorize(prefix));
			}else {
			try {
            if (prefix.length() > 32) {
            	prefix = prefix.substring(0, 31);
            }
            if(!t.getPrefix().equals(TheAPI.colorize(prefix)))
    		t.setPrefix(TheAPI.colorize(prefix));
		}catch(Exception e) {}}
    		if (TheAPI.isNewVersion())
    			t.setColor(TheAPI.getStringUtils().getColor(prefix));
		}
		if(!t.hasPlayer(p))
		t.addPlayer(p);
		
	}
	/**
	 * @see see Reset player name tag to default
	 */
	public void resetNameTag() {
		for(Team t : p.getScoreboard().getTeams())t.unregister();
	}

}
