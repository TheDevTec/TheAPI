package me.DevTec.TheAPI.Utils.Listener;

import java.util.HashMap;

public class Event {
	private static final HashMap<String, HandlerList> lists = new HashMap<>();
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
		if(list==null) {
			list=new HandlerList();
			lists.put(event.getName(), list);
		}
		return list;
	}
}
