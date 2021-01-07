package me.devtec.theapi.utils.listener.events;

import me.devtec.theapi.sockets.Client;
import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.listener.Event;

public class ClientReceiveMessaveEvent extends Event {
	private Data s;
	private Client c;
	
	public ClientReceiveMessaveEvent(Client client, Data text) {
		s=text;
		c=client;
	}
	
	public Client getClient() {
		return c;
	}
	
	public Data getInput() {
		return s;
	}
}