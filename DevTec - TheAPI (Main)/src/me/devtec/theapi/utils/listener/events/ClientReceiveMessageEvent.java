package me.devtec.theapi.utils.listener.events;

import me.devtec.theapi.sockets.SocketClient;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.listener.Event;

public class ClientReceiveMessageEvent extends Event {
	private final Data data;
	private final SocketClient client;
	
	public ClientReceiveMessageEvent(SocketClient client, Data data) {
		this.data=data;
		this.client=client;
	}
	
	public SocketClient getClient() {
		return client;
	}
	
	public Data getInput() {
		return data;
	}
}
