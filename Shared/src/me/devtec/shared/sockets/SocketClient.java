package me.devtec.shared.sockets;

public interface SocketClient {
	
	public String getName();
	
	public void exit();
	
	public void write(String path, Object value);
	
	public void send();
	
	public boolean isConnected();
}
