package me.straikerinacz.theapi.demo.sockets;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.sockets.implementation.SocketClientHandler;

public class SecondaryServer {
	private static SocketClientHandler socketClient;

	public static void init() {
		SecondaryServer.setServerName("secondary-server");
		SecondaryServer.connectClient("localhost", 25567, "theapi-demo-testing-password");
	}

	public static void setServerName(String serverName) {
		SocketClientHandler.serverName=serverName.getBytes();
	}

	public static void connectClient(String ip, int port, String password) {
		SecondaryServer.socketClient = new SocketClientHandler(ip, port, password);
		SecondaryServer.socketClient.start();
		//TEST
		SecondaryServer.socketClient.write(new Config().set("testing.path", true).set("testing.path2", 5));
	}

	public static void stopClient() {
		SecondaryServer.socketClient.stop();
	}
}
