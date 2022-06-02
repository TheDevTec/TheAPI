package me.devtec.shared.events.api;

import me.devtec.shared.events.Event;
import me.devtec.shared.sockets.SocketClient;

public class ServerClientConnectRespondeEvent extends Event {
	private final SocketClient client;
	private final int responde;

	public ServerClientConnectRespondeEvent(SocketClient client, int responde) {
		this.client = client;
		this.responde = responde;
	}

	public SocketClient getClient() {
		return client;
	}

	public int getResponde() {
		return responde;
	}
}
