package me.straikerinacz.theapi.demo.sockets;

import me.devtec.shared.API;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.scheduler.Tasker;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.implementation.SocketClientHandler;

public class SecondaryServer {
	private static SocketClientHandler socketClient;

	public static void init() {
		API.setEnabled(true);

		SocketClient.setServerName("secondary-server");
		SecondaryServer.socketClient = SocketClient.openConnection("192.168.50.29", 25567, "theapi-demo-testing-password");
		new Tasker() {

			@Override
			public void run() {
				SecondaryServer.writeTest();
			}
		}.runLater(20);
	}

	public static void writeTest() {
		SecondaryServer.socketClient.write(new Config().set("testing.path", true).set("testing.path2", 5));
	}

	public static void stop() {
		SecondaryServer.socketClient.stop();
	}
}
