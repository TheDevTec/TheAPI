package me.devtec.shared.events.api;

import java.net.Socket;

import me.devtec.shared.events.Cancellable;
import me.devtec.shared.events.Event;

public class ServerClientPreConnectEvent extends Event implements Cancellable {
	private final Socket socket;
	private final String serverName;
	private boolean cancelled;

	public ServerClientPreConnectEvent(Socket socket, String serverName) {
		this.socket = socket;
		this.serverName = serverName;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getServerName() {
		return serverName;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}
