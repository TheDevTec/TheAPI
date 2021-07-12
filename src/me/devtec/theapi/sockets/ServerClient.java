package me.devtec.theapi.sockets;

public class ServerClient implements SocketClient {
	private final ClientHandler c;
	
	public ServerClient(ClientHandler c) {
		this.c=c;
		c.c=this;
	}
	
	public String getName() {
		return c.getUser();
	}
	
	public void exit() {
		c.exit();
	}
	
	public void write(String path, Object value) {
		c.write(path, value);
	}
	
	public void send() {
		c.send();
	}
	
	public boolean isConnected() {
		return c.s.isConnected();
	}
}
