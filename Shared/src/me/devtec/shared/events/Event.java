package me.devtec.shared.events;

public class Event {
	private final HandlerList handlerList = HandlerList.getOrCreate(getEventName());

	public String getEventName() {
		return getClass().getCanonicalName();
	}
	
	public HandlerList getHandlerList() {
		return handlerList;
	}
}
