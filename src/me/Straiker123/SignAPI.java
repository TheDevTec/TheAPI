package me.Straiker123;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

public class SignAPI {
	public SignAPI() {
		f = LoaderClass.data;
	}
	private ConfigAPI f = LoaderClass.data;
	//List<String> commands = Arrays.asList("string.here","next.string");
	public static enum SignAction {
		CONSOLE_COMMANDS,
		PLAYER_COMMANDS,
		BROADCAST,
		MESSAGES
	}

	public void removeSign(Location loc) {
		f.getConfig().set("Sign."+TheAPI.getStringUtils().getLocationAsString(loc),null);
	}
	
	public List<Location> getRegistredSigns(){
		List<Location> l = new ArrayList<Location>();
		if(f.getConfig().getString("Sign")!=null)
		for(String s : f.getConfig().getConfigurationSection("Sign").getKeys(false)) {
			Location d = TheAPI.getStringUtils().getLocationFromString(s);
			if(d.getBlock().getType().name().contains("SIGN"))
			l.add(d);
			else
				removeSign(d);
		}
		return l;
	}
	
	@Nullable
	public Sign getSignState(Location loc){
		Sign s = null;
		if(getRegistredSigns().contains(loc))
			s=(Sign)loc.getBlock().getState();
		return s;
	}
	
	public void setActions(Sign state, HashMap<SignAction, List<String>> options) {
		String l = TheAPI.getStringUtils().getLocationAsString(state.getLocation());
		for(SignAction s : options.keySet()) {
		switch(s) {
		case CONSOLE_COMMANDS:
			if(options.get(s) instanceof List)
			f.getConfig().set("Sign."+l+".CONSOLE_COMMANDS",options.get(s));
			break;
		case PLAYER_COMMANDS:
			if(options.get(s) instanceof List)
			f.getConfig().set("Sign."+l+".PLAYER_COMMANDS",options.get(s));
			break;
		case MESSAGES:
			if(options.get(s) instanceof List)
			f.getConfig().set("Sign."+l+".MESSAGES",options.get(s));
			break;
		case BROADCAST:
			if(options.get(s) instanceof List)
			f.getConfig().set("Sign."+l+".BROADCAST",options.get(s));
			break;
		}}
	}
	
	public HashMap<SignAction, List<String>> getSignActions(Sign state) {
		HashMap<SignAction, List<String>> a = new HashMap<SignAction, List<String>>();
		Location l = state.getLocation();
		String ff = TheAPI.getStringUtils().getLocationAsString(l);
		if(getRegistredSigns().contains(l)) {
			for(String s:f.getConfig().getConfigurationSection("Sign."+ff).getKeys(false)) {
				a.put(SignAction.valueOf(s), f.getConfig().getStringList("Sign."+ff+"."+s));
			}
		}
		return a;
	}
	
}
