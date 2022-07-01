package me.devtec.shared.events.api;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.events.Event;
import me.devtec.shared.sockets.SocketClient;

public class ServerClientReceiveDataEvent extends Event {
	private final Config data;
	private final SocketClient client;

	public ServerClientReceiveDataEvent(SocketClient client, Config data) {
		this.data = data;
		this.client = client;
	}

	public SocketClient getClient() {
		return client;
	}

	public Config getData() {
		return data;
	}
}
