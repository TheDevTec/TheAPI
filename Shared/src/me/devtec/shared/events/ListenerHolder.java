package me.devtec.shared.events;

import java.util.Arrays;
import java.util.List;

public class ListenerHolder {
	protected EventListener listener;
	protected List<Class<? extends Event>> listen;
	
	public final List<Class<? extends Event>> getEvents() {
		return listen;
	}
	
	public final EventListener getListener() {
		return listener;
	}
	
	@SafeVarargs
	public final ListenerHolder listen(Class<? extends Event>... events) {
		return listen(Arrays.asList(events));
	}
	
	public final ListenerHolder listen(List<Class<? extends Event>> events) {
		listen=events;
		return this;
	}
}
