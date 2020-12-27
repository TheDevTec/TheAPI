package me.devtec.theapi.utils.listener.events;

import me.devtec.theapi.sockets.Client;
import me.devtec.theapi.utils.listener.Event;

public class ServerReceiveMessaveEvent extends Event {
	String s;
	Client c;
	
	public ServerReceiveMessaveEvent(Client client, String text) {
		s=text;
		c=client;
	}
	
	public Client getClient() {
		return c;
	}
	
	public String getInput() {
		return s;
	}
}
