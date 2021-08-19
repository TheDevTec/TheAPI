package me.devtec.theapi.apis;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class NameTagAPI {
	private String prefix, suffix;
	private final Player p;

	public NameTagAPI(Player p, String prefix, String suffix) {
		this.p = p;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	/**
	 * @apiNote Return input Player
	 * @return Player
	 */
	public Player getPlayer() {
		return p;
	}

	/**
	 * @apiNote Get prefix above player's head
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @apiNote Get suffix above player's head
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @apiNote Change player's prefix above head
	 * @param name New prefix
	 */
	public void setPrefix(String name) {
		prefix = name;
	}

	/**
	 * @apiNote Change player's suffix above head
	 * @param name New suffix
	 */
	public void setSuffix(String name) {
		suffix = name;
	}

	/**
	 * @apiNote Warning, this method change whole player name, plugins these
	 *      checking UUID will kick/ban you from server, for ex.: UUIDSpoofFix
	 * @param name New player name
	 */

	public void setPlayerName(String name) {
		try {
			boolean gameProfileExists = false;
			try {
				Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
				gameProfileExists = true;
			} catch (Exception e) {
				try {
					Class.forName("com.mojang.authlib.GameProfile");
					gameProfileExists = true;
				} catch (Exception er) {
				}
			}
			if (!gameProfileExists) {
				Ref.set(Ref.player(p), "name", name);
			} else {
				Object profile = Ref.invoke(Ref.player(p), "getProfile");
				Ref.set(profile, "name", name);
			}
			for (Player p : TheAPI.getOnlinePlayers()) {
				p.hidePlayer(this.p);
				p.showPlayer(this.p);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * @apiNote Set player name tag
	 * @param team By team you can sort players in tablist -> create sorted
	 *                 tablist
	 */
	public void setNameTag(Team team) {
		setNameTag(team.getName(), p.getScoreboard());
	}

	/**
	 * Set player name tag
	 * 
	 * @param team By teamName you can sort players in tablist -> create sorted
	 *                 tablist
	 */
	public void setNameTag(String team) {
		for (Player p : TheAPI.getOnlinePlayers())
			setNameTag(team, p.getScoreboard());
	}

	/**
	 * @apiNote Set player name tag
	 * @param teamName By teamName you can sort players in tablist -> create sorted
	 *                 tablist
	 */

	public void setNameTag(String teamName, Scoreboard sb) {
		if (teamName == null)teamName = "z";
		if (sb == null)sb = p.getScoreboard();
		if (teamName.length() > 16)teamName = teamName.substring(0, 15);
		Team t = sb.getTeam(teamName);
		if (t == null)t=sb.registerNewTeam(teamName);
		if (suffix != null) {
			if (TheAPI.isOlderThan(9)) {
				String cut = TheAPI.colorize(suffix);
				if (cut.length() > 16)
					cut = cut.substring(0, 15);
				if (!t.getSuffix().equals(cut))
					t.setSuffix(cut);
			} else {
				try {
					String cut = TheAPI.colorize(suffix);
					if (cut.length() > 32) {
						cut = cut.substring(0, 31);
					}
					if (!t.getSuffix().equals(cut))
						t.setSuffix(cut);
				} catch (Exception e) {
				}
			}
		}
		if (prefix != null) {
			if (TheAPI.isOlderThan(9)) {
				String cut = TheAPI.colorize(prefix);
				if (cut.length() > 16)
					cut = cut.substring(0, 15);
				if (!t.getPrefix().equals(cut))
					t.setPrefix(cut);
			} else {
				try {
					String cut = TheAPI.colorize(prefix);
					if (cut.length() > 32) {
						cut = cut.substring(0, 31);
					}
					if (!t.getPrefix().equals(cut))
						t.setPrefix(cut);
				} catch (Exception e) {
				}
				if (TheAPI.isNewVersion())
					if(!ChatColor.getLastColors(prefix).trim().equals("")) {
						t.setColor(ChatColor.getByChar(ChatColor.getLastColors(prefix).substring(1,2)));
					}else
						t.setColor(ChatColor.WHITE);
			}
		}
		if (!t.hasPlayer(p))
			t.addPlayer(p);

	}

	/**
	 * @apiNote Reset player name tag to default
	 */
	public void resetNameTag() {
		for (Team t : p.getScoreboard().getTeams())
			t.unregister();
	}

}
