package me.devtec.theapi.utils.listener;

import me.devtec.theapi.TheAPI;

public interface Listener {

	public default Listener register() {
		TheAPI.register(this);
		return this;
	}

	public default Listener unregister() {
		TheAPI.unregister(this);
		return this;
	}
}
