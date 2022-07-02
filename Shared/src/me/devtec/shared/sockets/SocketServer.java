package me.devtec.shared.sockets;

import java.util.List;

import me.devtec.shared.sockets.implementation.SocketServerHandler;

public interface SocketServer {
	public String serverName();

	public List<SocketClient> connectedClients();

	public boolean isRunning();

	public void start();

	public void stop();

	public void notifyDisconnect(SocketClient client);

	public static SocketServerHandler startServer(String serverName, int port, String password) {
		SocketServerHandler server = new SocketServerHandler(serverName, port, password);
		server.start();
		return server;
	}
}
