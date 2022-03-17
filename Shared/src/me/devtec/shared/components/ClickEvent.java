package me.devtec.shared.components;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClickEvent {
	public enum Action {
		CHANGE_PAGE, COPY_TO_CLIPBOARD, OPEN_FILE, OPEN_URL, RUN_COMMAND, SUGGEST_COMMAND;
	}
	
	private Action action;
	private String value;
	public ClickEvent(Action action, String value) {
		this.action=action;
		this.value=value;
	}
	
	public ClickEvent setAction(Action action) {
		this.action=action;
		return this;
	}
	
	public ClickEvent setValue(String value) {
		this.value=value;
		return this;
	}
	
	public Action getAction() {
		return action;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toJson() {
		return "{\"action\":\""+action.name().toLowerCase()+"\",\"value\":\""+value+"\"}";
	}
	
	public Map<String, Object> toJsonMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("action", action.name().toLowerCase());
		map.put("value", value);
		return map;
	}
}
