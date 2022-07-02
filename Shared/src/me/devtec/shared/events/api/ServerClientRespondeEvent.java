package me.devtec.shared.events.api;

import me.devtec.shared.events.Event;
import me.devtec.shared.sockets.SocketClient;

public class ServerClientRespondeEvent extends Event {
	private final SocketClient client;
	private final int responde;

	public ServerClientRespondeEvent(SocketClient client, int responde) {
		this.client = client;
		this.responde = responde;
	}

	public SocketClient getClient() {
		return client;
	}

	public int getResponde() {
		return responde;
	}

	public ClientResponde getClientResponde() {
		return ClientResponde.fromResponde(responde);
	}
}
