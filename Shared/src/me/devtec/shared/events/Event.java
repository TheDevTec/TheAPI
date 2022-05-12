package me.devtec.shared.events;

public class Event {
	
	public String getEventName() {
		return getClass().getCanonicalName();
	}
}
