package me.devtec.theapi.utils.listener.events;

import me.devtec.theapi.sockets.SocketClient;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.listener.Event;

public class ClientReceiveMessageEvent extends Event {
	private Data s;
	private SocketClient c;
	
	public ClientReceiveMessageEvent(SocketClient client, Data text) {
		s=text;
		c=client;
	}
	
	public SocketClient getClient() {
		return c;
	}
	
	public Data getInput() {
		return s;
	}
}
