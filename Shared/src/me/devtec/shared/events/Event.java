package me.devtec.shared.events;

public class Event {

	public String getEventName() {
		return this.getClass().getCanonicalName();
	}
}
