package me.devtec.shared.sockets;

import java.io.File;
import java.net.Socket;

import me.devtec.shared.dataholder.Config;

public interface SocketClient {
	public String serverName();

	public String ip();

	public int port();

	public boolean isConnected();

	public void write(Config data);

	public default void write(File file) {
		write(file.getName(), file);
	}

	public void write(String fileName, File file);

	public void start();

	public void stop();

	public Socket getSocket();
}
