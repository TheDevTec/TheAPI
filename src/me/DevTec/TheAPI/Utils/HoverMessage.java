package me.DevTec.TheAPI.Utils;

import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.Json.JsonMaker;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;

public class HoverMessage {

	public enum ClickAction {
		RUN_COMMAND, SUGGEST_COMMAND, OPEN_URL
	}

	public enum HoverAction {
		SHOW_ITEM, SHOW_ACHIEVEMENT, SHOW_ENTITY, SHOW_TEXT
	}
	
	private JsonMaker maker = new JsonMaker();

	public HoverMessage(String... text) {
		for (String extra : text)
			addText(extra);
	}

	public HoverMessage addText(String text) {
		maker.jsonObject();
		maker.add("text", TheAPI.colorize(text));
		return this;
	}

	@SuppressWarnings("unchecked")
	public HoverMessage setClickEvent(ClickAction action, String value) {
		JSONObject ac = new JSONObject();
		ac.put("action", action.name().toLowerCase());
		ac.put("value", TheAPI.colorize(value));
		maker.add("clickEvent", ac);
		return this;
	}

	@SuppressWarnings("unchecked")
	public HoverMessage setHoverEvent(HoverAction action, Object value) {
		JSONObject ac = new JSONObject();
		ac.put("action", action.name().toLowerCase());
		ac.put("value", value instanceof  String?TheAPI.colorize(""+value):value);
		maker.add("hoverEvent", ac);
		return this;
	}

	public HoverMessage setHoverEvent(String value) {
		return setHoverEvent(HoverAction.SHOW_TEXT, value);
	}

	public String getJson() {
		return maker.toString();
	}

	public void send(List<Player> players) {
		for (Player p : players)
			send(p);
	}

	public void send(Collection<Player> players) {
		for (Player p : players)
			send(p);
	}

	public void send(Player player) {
		NMSAPI.getNMSPlayerAPI(player).sendMessageJson(getJson());
	}
}
