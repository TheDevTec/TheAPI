package me.devtec.shared.events.api;

import me.devtec.shared.events.Event;
import me.devtec.shared.sockets.SocketClient;

public class ServerClientConnectedEvent extends Event {
	private final SocketClient client;

	public ServerClientConnectedEvent(SocketClient client) {
		this.client = client;
	}

	public SocketClient getClient() {
		return client;
	}
}
