package me.Straiker123;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.Straiker123.Scheduler.Tasker;

public class GameAPI {
	private ConfigAPI w;
	private String s;

	public GameAPI(String name) {
		s = name;
		w = LoaderClass.gameapi;
	}

	public String getName() {
		return s;
	}

	public void createArena(String arena, String arenaName) {
		w.set(s + ".Arenas." + arena + ".Name", arenaName);
		LoaderClass.gameapi.save();
	}

	public void deleteArena(String arena) {
		w.set(s + ".Arenas." + arena, null);
		LoaderClass.gameapi.save();
	}

	public boolean inGamePlayer(Player player) {
		for (String arena : w.getConfigurationSection(s + ".Arenas").getKeys(false)) {
			if (inGame(arena)) {
				return getPlayersInGame(arena).contains(player);
			}
		}
		return false;
	}

	public boolean existArena(String arena) {
		return w.getString(s + ".Arenas." + arena) != null;
	}

	public void setArenaInGame(String arena, boolean set) {
		w.set(s + ".Arenas." + arena + ".InGame", set);
		LoaderClass.gameapi.save();
	}

	public void addPlayer(String arena, String team, String player) {
		List<String> list = w.getStringList(s + ".Arenas." + arena + ".Teams." + team);
		list.add(player);
		w.set(s + ".Arenas." + arena + ".Teams." + team, list);
		LoaderClass.gameapi.save();
	}

	public void removePlayer(String arena, String team, String player) {
		List<String> list = w.getStringList(s + ".Arenas." + arena + ".Teams." + team);
		list.remove(player);
		w.set(s + ".Arenas." + arena + ".Teams." + team, list);
		LoaderClass.gameapi.save();
	}

	public void kickAllPlayers(String arena) {
		w.set(s + ".Arenas." + arena + ".Teams", null);
		LoaderClass.gameapi.save();
	}

	public static enum Setting {
		arena_time, start_time, max_players, min_players, min_teams, runnable_on_end
	}

	public void arenaSetting(String arena, Setting ss, Object value) {
		if (ss == Setting.runnable_on_end) {
			LoaderClass.win_rewards.put(s + ":" + arena, (Runnable) value);
			w.set(s + ".Arenas." + arena + ".Setting.win_rewards", true);
			return;
		}
		w.set(s + ".Arenas." + arena + ".Setting." + ss.toString(), value);
		LoaderClass.gameapi.save();
	}

	public List<Player> getPlayersInGame(String arena) {
		List<Player> i = new ArrayList<Player>();
		if (getTeamsInGame(arena).isEmpty() == false)
			for (String s : getTeamsInGame(arena)) {
				for (Player d : getPlayersInTeam(arena, s)) {
					if (d == null)
						continue;
					i.add(d);
				}
			}
		return i;
	}

	public List<String> getTeamsInGame(String arena) {
		List<String> i = new ArrayList<String>();
		for (String s : w.getConfigurationSection(s + ".Arenas." + arena + ".Teams").getKeys(false))
			i.add(s);
		return i;
	}

	public List<Player> getPlayersInTeam(String arena, String team) {
		List<Player> i = new ArrayList<Player>();
		if (w.getString(s + ".Arenas." + arena + ".Teams." + team) != null) {
			for (String s : w.getStringList(s + ".Arenas." + arena + ".Teams." + team)) {
				if (TheAPI.getPlayer(s) == null) {
					removePlayer(arena, team, s);
					continue;
				} else
					i.add(TheAPI.getPlayer(s));
			}
		}
		return i;
	}

	public String getPlayerTeam(String arena, Player player) {
		for (String team : getTeamsInGame(arena))
			if (getPlayersInTeam(arena, team).contains(player))
				return team;
		return null;
	}

	public String getPlayerArena(Player player) {
		for (String arena : getArenas())
			if (getPlayerTeam(arena, player) != null)
				return arena;
		return null;
	}

	public List<String> getArenas() {
		List<String> l = new ArrayList<String>();
		if (w.getString(s + ".Arenas") != null)
			for (String s : w.getConfigurationSection(s + ".Arenas").getKeys(false))
				l.add(s);
		return l;
	}

	private String createName(String arena, String team) {
		String s = "0";
		if (w.getString(s + ".Arenas." + arena + ".Teams-Locations." + team) != null) {
			for (int i = 0; i > -1; ++i) {
				if (w.getString(s + ".Arenas." + arena + ".Teams-Locations." + team + "." + i) == null) {
					s = i + "";
					break;
				}
			}
		}
		return s;
	}

	public void addLocationArena(String arena, String team, Location location) {
		w.set(s + ".Arenas." + arena + ".Teams-Locations." + team + "." + createName(arena, team), location);
	}

	public List<Location> getLocationsArena(String arena, String team) {
		List<Location> l = new ArrayList<Location>();
		if (w.getString(s + ".Arenas." + arena + ".Teams-Locations." + team) != null)
			for (String ss : w.getConfigurationSection(s + ".Arenas." + arena + ".Teams-Locations." + team)
					.getKeys(false)) {
				if (((Location) w.get(s + ".Arenas." + arena + ".Teams-Locations." + team + "." + ss)) != null
						&& ((Location) w.get(s + ".Arenas." + arena + ".Teams-Locations." + team + "." + ss))
								.getWorld() != null) {
					l.add((Location) w.get(s + ".Arenas." + arena + ".Teams-Locations." + team + "." + ss));
				}
			}
		return l;
	}

	public void removeLocationArena(String arena, String team, String id) {
		w.set(s + ".Arenas." + arena + ".Teams-Locations." + team + "." + id, null);
		LoaderClass.gameapi.save();
	}

	public void removeAllLocationsArena(String arena, String team, boolean confirm) {
		if (confirm) {
			w.set(s + ".Arenas." + arena + ".Teams-Locations." + team, null);
			LoaderClass.gameapi.save();
		}
	}

	public boolean inGame(String arena) {
		return w.getBoolean(s + ".Arenas." + arena + ".InGame");
	}

	public void startArena(String arena) {
		if (w.getString(s + ".Arenas." + arena + ".Setting.min_players") != null) {
			int mis = getPlayersInGame(arena).size() - w.getInt(s + ".Arenas." + arena + ".Setting.min_players");
			if (mis < 0) {
				TheAPI.getConsole().sendMessage(
						TheAPI.colorize("&2TheGameAPI &b> &6In arena " + arena + " missing " + mis + " players"));
				return;
			}
		}
		if (w.getString(s + ".Arenas." + arena + ".Setting.max_players") != null) {
			if (getPlayersInGame(arena).size() - 1 > w.getInt(s + ".Arenas." + arena + ".Setting.max_players")) {
				TheAPI.getConsole()
						.sendMessage(TheAPI.colorize("&2TheGameAPI &b> &6In arena " + arena + " is too much players"));
				return;
			}
		}
		if (w.getString(s + ".Arenas." + arena + ".Setting.min_teams") != null) {
			if (getTeamsInGame(arena).size() > w.getInt(s + ".Arenas." + arena + ".Setting.min_teams")) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize("&2TheGameAPI &b> &6Arena " + arena
						+ " required minimal " + w.getInt(s + ".Arenas." + arena + ".Setting.min_teams") + " teams"));
				return;
			}
		}
		new Tasker() {
			int time = 0;

			@Override
			public void run() {
				++time;
				String timer = -1 * (w.getInt(s + ".Arenas." + arena + ".Setting.start_time") - time) + "";
				for (Player s : getPlayersInGame(arena)) {
					TheAPI.sendActionBar(s, LoaderClass.config.getConfig().getString("GameAPI.StartingIn")
							.replace("%time%", timer).replace("%arena%", arena));
					if (time >= w.getInt(s + ".Arenas." + arena + ".Setting.start_time")) {
						cancel();
						TheAPI.sendActionBar(s,
								LoaderClass.config.getConfig().getString("GameAPI.Start").replace("%arena%", arena));

						List<Object> l = new ArrayList<Object>();
						for (Location a : getLocationsArena(arena, getPlayerTeam(arena, s)))
							l.add(a);
						if (!l.isEmpty())
							s.teleport((Location) TheAPI.getRandomFromList(l));
						return;
					}
				}
			}
		}.repeating(20, 20);

		setArenaInGame(arena, true);

		LoaderClass.GameAPI_Arenas.put(arena, new Tasker() {
			int time = 0;

			@Override
			public void run() {
				++time;

				for (String s : w.getConfigurationSection(s + ".Arenas." + arena + ".Teams").getKeys(false))
					for (String d : w.getStringList(s + ".Arenas." + arena + ".Teams." + s)) {
						if (TheAPI.getPlayer(d) == null) {
							List<String> list = w.getStringList(s + ".Arenas." + arena + ".Teams." + s);
							list.remove(d);
							w.set(s + ".Arenas." + arena + ".Teams." + s, list);
						}
					}
				if (getPlayersInGame(arena).size() <= 1)
					time = w.getInt(s + ".Arenas." + arena + ".Setting.arena_time");
				if (time >= w.getInt(s + ".Arenas." + arena + ".Setting.arena_time")) {
					stopArena(arena, true);
					return;
				}
			}
		}.repeating(20, 20));
	}

	public void stopArena(String arena, boolean runnable_on_end) {
		w.set(s + ".Arenas." + arena + ".InGame", false);
		Tasker.cancelTask(LoaderClass.GameAPI_Arenas.get(arena));
		if (runnable_on_end) {
			try {
				LoaderClass.win_rewards.get(s + ":" + arena).run();
			} catch (Exception e) {

			}
		}
		w.set(s + ".Arenas." + arena + ".Players", null);
		w.set(s + ".Arenas." + arena + ".Teams", null);
		LoaderClass.gameapi.save();
	}

}
