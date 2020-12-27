package me.devtec.theapi.utils.listener.events;

import me.devtec.theapi.utils.listener.Cancellable;
import me.devtec.theapi.utils.listener.Event;

public class ConsoleLogEvent extends Event implements Cancellable {
	private String mes, level;
	private boolean cancel;
	
	public ConsoleLogEvent(String message, String level) {
		mes=message;
		this.level=level;
	}
	
	public String getLevel() {
		return level;
	}
	
	public String getMessage() {
		return mes;
	}
	
	public void setMesssage(String message) {
		mes=message;
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
