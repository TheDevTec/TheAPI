package me.straikerinacz.theapi.demo.sockets;

import me.devtec.shared.sockets.implementation.SocketClientHandler;
import me.devtec.shared.sockets.implementation.SocketServerHandler;

public class PrimaryServer {
	private static SocketServerHandler socketServer;

	public static void init() {
		PrimaryServer.setServerName("primary-server");
		PrimaryServer.startSocketServer(25567, "theapi-demo-testing-password");
	}

	public static void setServerName(String serverName) {
		SocketClientHandler.serverName=serverName.getBytes();
	}

	public static void startSocketServer(int port, String password) {
		PrimaryServer.socketServer = new SocketServerHandler(new String(SocketClientHandler.serverName), port, password);
		PrimaryServer.socketServer.start();
	}

	public static void stopSocketServer() {
		PrimaryServer.socketServer.stop();
	}
}
