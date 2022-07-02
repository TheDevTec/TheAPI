package me.devtec.shared.events.api;

import java.io.File;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.events.Event;
import me.devtec.shared.sockets.SocketClient;

public class ServerClientReceiveFileEvent extends Event {
	private final File file;
	private final Config data;
	private final SocketClient client;

	public ServerClientReceiveFileEvent(SocketClient client, Config data, File received) {
		file = received;
		this.data = data;
		this.client = client;
	}

	public SocketClient getClient() {
		return client;
	}

	public File getFile() {
		return file;
	}

	/**
	 *
	 * @apiNote Nullable
	 */
	public Config getData() {
		return data;
	}
}
