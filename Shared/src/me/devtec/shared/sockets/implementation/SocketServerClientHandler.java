package me.devtec.shared.sockets.implementation;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.loaders.ByteLoader;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerReceiveDataEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;

public class SocketServerClientHandler implements SocketClient {
	private final String serverName;
	private final Socket socket;
	public SocketServerClientHandler(SocketServer server, String serverName, Socket socket) {
		this.socket=socket;
		this.serverName=serverName;
		try {
			if(!isConnected())return;
			InputStream stream = socket.getInputStream();
			new Thread(()->{
				while(isConnected()) {
					try {
						int task = stream.read();
						if(task==SocketServer.RECEIVE_DATA) {
							byte[] path = new byte[stream.read()];
							stream.read(path);
							ByteLoader loader = new ByteLoader();
							loader.load(path);
							Config data = new Config(loader);
							ServerReceiveDataEvent event = new ServerReceiveDataEvent(SocketServerClientHandler.this, data);
							EventManager.call(event);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}).start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public String serverName() {
		return serverName;
	}

	@Override
	public String ip() {
		return socket.getInetAddress().getHostName();
	}

	@Override
	public int port() {
		return socket.getPort();
	}

	@Override
	public boolean isConnected() {
		return socket!=null && socket.isConnected() && !socket.isClosed();
	}

	@Override
	public void write(Config data) {
		try {
			byte[] path = data.toByteArray();
			socket.getOutputStream().write(SocketServer.RECEIVE_DATA);
			socket.getOutputStream().write(path.length);
			socket.getOutputStream().write(path);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		throw new RuntimeException("Can' t connect a socket that is not from the server side");
	}

	@Override
	public void end() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

}
