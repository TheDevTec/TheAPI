package me.devtec.theapi.utils.listener;

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
