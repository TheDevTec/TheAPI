package me.straikerinacz.theapi.demo.sockets;

import me.devtec.shared.API;
import me.devtec.shared.dataholder.DataType;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerClientReceiveDataEvent;
import me.devtec.shared.sockets.SocketServer;
import me.devtec.shared.sockets.implementation.SocketServerHandler;

public class PrimaryServer {
	private static SocketServerHandler socketServer;

	public static void init() {
		API.setEnabled(true);

		PrimaryServer.socketServer = SocketServer.startServer("primary-server", 25567, "theapi-demo-testing-password");
		EventManager.register(event -> {
			ServerClientReceiveDataEvent dataEvent = (ServerClientReceiveDataEvent)event;
			System.out.println(dataEvent.getData().toString(DataType.JSON));

			API.setEnabled(false);
			PrimaryServer.stop();
		}).listen(ServerClientReceiveDataEvent.class);
	}

	public static void stop() {
		PrimaryServer.socketServer.stop();
	}
}
