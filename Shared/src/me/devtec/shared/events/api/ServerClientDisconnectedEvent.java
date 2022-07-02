package me.devtec.shared.events.api;

import me.devtec.shared.events.Event;
import me.devtec.shared.sockets.SocketClient;

public class ServerClientDisconnectedEvent extends Event {
	private final SocketClient client;

	public ServerClientDisconnectedEvent(SocketClient client) {
		this.client = client;
	}

	public SocketClient getClient() {
		return client;
	}
}
