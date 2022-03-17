package me.devtec.shared.components;

import java.util.LinkedHashMap;
import java.util.Map;

import me.devtec.shared.json.Json;

public class HoverEvent {
	public enum Action {
		SHOW_ACHIEVEMENT, SHOW_ENTITY, SHOW_ITEM, SHOW_TEXT;
	}
	
	private Action action;
	private Component value;
	public HoverEvent(Action action, Component value) {
		this.action=action;
		this.value=value;
	}
	
	public HoverEvent setAction(Action action) {
		this.action=action;
		return this;
	}
	
	public HoverEvent setValue(Component value) {
		this.value=value;
		return this;
	}
	
	public Action getAction() {
		return action;
	}
	
	public Component getValue() {
		return value;
	}
	
	public String toJson() {
		return "{\"action\":\""+action.name().toLowerCase()+"\",\"value\":\""+Json.writer().simpleWrite(ComponentAPI.toJsonList(value))+"\"}";
	}
	
	public Map<String, Object> toJsonMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("action", action.name().toLowerCase());
		map.put("value", ComponentAPI.toJsonList(value));
		return map;
	}
}
