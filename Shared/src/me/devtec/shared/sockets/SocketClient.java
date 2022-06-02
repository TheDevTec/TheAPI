package me.devtec.shared.sockets;

import java.net.Socket;

import me.devtec.shared.dataholder.Config;

public interface SocketClient {
	public String serverName();

	public String ip();

	public int port();

	public boolean isConnected();

	public void write(Config data);

	public void start();

	public void end();

	public Socket getSocket();
}
