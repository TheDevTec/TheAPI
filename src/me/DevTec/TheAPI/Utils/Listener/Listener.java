package me.DevTec.TheAPI.Utils.Listener;

import me.DevTec.TheAPI.TheAPI;

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
