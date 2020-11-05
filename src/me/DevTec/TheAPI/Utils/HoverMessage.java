package me.DevTec.TheAPI.Utils;

import java.util.Collection;

import org.bukkit.entity.Player;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.Json.Maker;
import me.DevTec.TheAPI.Utils.Json.Maker.MakerObject;
import me.DevTec.TheAPI.Utils.NMS.NMSAPI;

public class HoverMessage {

	public enum ClickAction {
		OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD
	}

	public enum HoverAction {
		SHOW_ITEM, SHOW_ACHIEVEMENT, SHOW_ENTITY, SHOW_TEXT
	}

	private Maker maker = new Maker();
	private MakerObject o = null;

	public HoverMessage(String... text) {
		for (String extra : text)
			addText(extra);
	}

	public HoverMessage addText(String text) {
		if (o != null)
			maker.add(o);
		o = maker.create();
		o.add("text", TheAPI.colorize(text));
		return this;
	}

	public HoverMessage setClickEvent(ClickAction action, String value) {
		MakerObject ac = maker.create();
		ac.put("action", action.name().toLowerCase());
		ac.put("value", TheAPI.colorize(value));
		o.add("clickEvent", ac.toString());
		return this;
	}

	public HoverMessage setHoverEvent(HoverAction action, Object value) {
		MakerObject ac = maker.create();
		ac.put("action", action.name().toLowerCase());
		ac.put("value", value instanceof String ? TheAPI.colorize("" + value) : value);
		o.add("hoverEvent", ac.toString());
		return this;
	}

	public HoverMessage setHoverEvent(String value) {
		return setHoverEvent(HoverAction.SHOW_TEXT, value);
	}

	public String getJson() {
		if (o != null)
			maker.add(o);
		o = null;
		return maker.toString();
	}

	public void send(Collection<Player> players) {
		for (Player p : players)
			send(p);
	}

	public void send(Player player) {
		NMSAPI.getNMSPlayerAPI(player).sendMessageJson(getJson());
	}
}
