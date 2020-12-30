package me.devtec.theapi.sockets;

public class ServerClient {
	private final ClientHandler c;
	
	public ServerClient(ClientHandler c) {
		this.c=c;
		c.c=this;
	}
	
	public String getName() {
		return c.getUser();
	}
	
	public void exit() {
		c.end();
	}
	
	public void send(String text) {
		c.send(text);
	}
	
	public boolean isConnected() {
		return c.run;
	}
}
