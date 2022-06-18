package me.devtec.shared.events.api;

import java.io.File;

import me.devtec.shared.events.Event;
import me.devtec.shared.sockets.SocketClient;

public class ServerReceiveFileEvent extends Event {
	private final File data;
	private final SocketClient client;

	public ServerReceiveFileEvent(SocketClient client, File received) {
		data = received;
		this.client = client;
	}

	public SocketClient getClient() {
		return client;
	}

	public File getFile() {
		return data;
	}
}
