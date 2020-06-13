package me.DevTec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

import me.DevTec.Other.LoaderClass;
import me.DevTec.Other.Position;

public class SignAPI {
	public SignAPI() {
		f = LoaderClass.data;
	}

	private ConfigAPI f = LoaderClass.data;

	// List<String> commands = Arrays.asList("string.here","next.string");
	public static enum SignAction {
		CONSOLE_COMMANDS, PLAYER_COMMANDS, BROADCAST, MESSAGES
	}

	public void removeSign(Position loc) {
		f.set("Sign." + loc.toString(), null);
		f.save();
	}

	public List<Position> getRegistredSigns() {
		List<Position> l = new ArrayList<Position>();
		if (f.getString("Sign") != null)
			for (String s : f.getConfigurationSection("Sign", false)) {
				Position d =Position.fromString(s);
				if (d.getBlock().getType().name().contains("SIGN"))
					l.add(d);
				else
					removeSign(d);
			}
		return l;
	}

	@Nullable
	public Sign getSignState(Position loc) {
		Sign s = null;
		if (getRegistredSigns().contains(loc))
			s = (Sign) loc.getBlock().getState();
		return s;
	}

	public void setActions(Sign state, HashMap<SignAction, List<String>> options) {
		String l = new Position(state.getLocation()).toString();
		for (SignAction s : options.keySet()) {
			switch (s) {
			case CONSOLE_COMMANDS:
				if (options.get(s) instanceof List)
					f.set("Sign." + l + ".CONSOLE_COMMANDS", options.get(s));
				break;
			case PLAYER_COMMANDS:
				if (options.get(s) instanceof List)
					f.set("Sign." + l + ".PLAYER_COMMANDS", options.get(s));
				break;
			case MESSAGES:
				if (options.get(s) instanceof List)
					f.set("Sign." + l + ".MESSAGES", options.get(s));
				break;
			case BROADCAST:
				if (options.get(s) instanceof List)
					f.set("Sign." + l + ".BROADCAST", options.get(s));
				break;
			}
		}
		f.save();
	}

	public HashMap<SignAction, List<String>> getSignActions(Sign state) {
		HashMap<SignAction, List<String>> a = new HashMap<SignAction, List<String>>();
		Position l = new Position(state.getLocation());
		String ff = l.toString();
		if (getRegistredSigns().contains(l)) {
			for (String s : f.getConfigurationSection("Sign." + ff, false)) {
				a.put(SignAction.valueOf(s), f.getStringList("Sign." + ff + "." + s));
			}
		}
		return a;
	}

}
