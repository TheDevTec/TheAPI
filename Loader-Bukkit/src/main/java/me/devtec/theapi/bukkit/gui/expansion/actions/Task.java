package me.devtec.theapi.bukkit.gui.expansion.actions;

import java.util.List;

public class Task {
	private final List<Character> items;
	private final List<Action> actions;
	private final long time;

	public Task(List<Character> items, List<Action> actions, long time) {
		this.items = items;
		this.actions = actions;
		this.time = time;
	}

	public List<Character> getItems() {
		return items;
	}

	public List<Action> getActions() {
		return actions;
	}

	public long getTime() {
		return time;
	}
}
