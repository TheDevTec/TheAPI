package me.devtec.shared.sockets;

import java.util.List;

public interface SocketServer {
	public String serverName();

	public List<SocketClient> connectedClients();

	public boolean isRunning();

	public void start();

	public void stop();

	public void notifyDisconnect(SocketClient client);
}
