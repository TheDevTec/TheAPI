package me.devtec.theapi.utils.listener;

import me.devtec.theapi.utils.datakeeper.maps.UnsortedMap;

public class Event {
	private static final UnsortedMap<String, HandlerList> lists = new UnsortedMap<>();

	public Event() {
		lists.put(getClass().getName(), h);
	}

	private final HandlerList h = new HandlerList();
	private String name;

	public String getEventName() {
		if (name == null)
			name = getClass().getSimpleName();
		return name;
	}

	public static HandlerList getHandlerList(Class<? extends Event> event) {
		HandlerList list = lists.getOrDefault(event.getName(), null);
		if (list == null) {
			list = new HandlerList();
			lists.put(event.getName(), list);
		}
		return list;
	}
}
