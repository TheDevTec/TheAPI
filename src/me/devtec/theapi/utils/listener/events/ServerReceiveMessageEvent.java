package me.devtec.theapi.utils.listener.events;

import me.devtec.theapi.sockets.ServerClient;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.listener.Event;

public class ServerReceiveMessageEvent extends Event {
	private Data s;
	private ServerClient c;
	
	public ServerReceiveMessageEvent(ServerClient client, Data text) {
		s=text;
		c=client;
	}
	
	public ServerClient getClient() {
		return c;
	}
	
	public Data getInput() {
		return s;
	}
}
