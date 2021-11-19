package me.devtec.theapi.utils.listener.events;

import me.devtec.theapi.utils.listener.Cancellable;
import me.devtec.theapi.utils.listener.Event;

public class ConsoleLogEvent extends Event implements Cancellable {
	private String message;
    private final String level;
	private boolean cancel;
	
	public ConsoleLogEvent(String message, String level) {
		this.message=message;
		this.level=level;
	}
	
	public String getLevel() {
		return level;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMesssage(String message) {
		this.message=message;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel=cancel;
	}

}
