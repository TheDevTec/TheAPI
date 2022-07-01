package me.devtec.shared.events.api;

import me.devtec.shared.events.Event;

import java.net.Socket;

public class ServerClientRejectedEvent extends Event {
	private final Socket socket;
	private final String serverName;

	public ServerClientRejectedEvent(Socket socket, String serverName) {
		this.socket = socket;
		this.serverName = serverName;
	}

	public Socket getSocket() {
		return socket;
	}

	/**
	 * @apiNote Server name is nullable
	 */
	public String getServerName() {
		return serverName;
	}
}
