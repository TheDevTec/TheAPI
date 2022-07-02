package me.devtec.shared.events.api;

import java.net.Socket;

import me.devtec.shared.events.Event;

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
