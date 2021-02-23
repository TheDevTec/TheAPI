package me.devtec.theapi.utils.listener.events;

import me.devtec.theapi.sockets.ServerClient;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.listener.Event;

public class ServerReceiveMessaveEvent extends Event {
	private Data s;
	private ServerClient c;
	
	public ServerReceiveMessaveEvent(ServerClient client, Data text) {
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
