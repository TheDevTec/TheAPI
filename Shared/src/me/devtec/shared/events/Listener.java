package me.devtec.shared.events;

public interface Listener {

	public default Listener register() {
		HandlerList.register(this);
		return this;
	}

	public default Listener unregister() {
		HandlerList.unregister(this);
		return this;
	}
}
