package me.devtec.shared.sockets.implementation;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.loaders.ByteLoader;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerClientConnectRespondeEvent;
import me.devtec.shared.events.api.ServerReceiveDataEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;

public class SocketClientHandler implements SocketClient {
	public static byte[] serverName;

	private final String ip;
	private final int port;
	private Socket socket;
	private boolean connected;
	private byte[] password;
	public SocketClientHandler(String ip, int port, String password) {
		this.ip=ip;
		this.port=port;
		this.password=password.getBytes();
	}

	@Override
	public String serverName() {
		return null;
	}

	@Override
	public String ip() {
		return ip;
	}

	@Override
	public int port() {
		return port;
	}

	@Override
	public boolean isConnected() {
		return connected && socket!=null && socket.isConnected() && !socket.isClosed();
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
		try {
			socket=new Socket(ip, port);
			socket.setReuseAddress(true);
			socket.setTcpNoDelay(true);
			//PROCESS LOGIN
			if(socket.isConnected() && !socket.isClosed() && socket.getInputStream().read()==SocketServer.PROCESS_LOGIN) {
				socket.getOutputStream().write(password.length);
				socket.getOutputStream().write(password);
				int result = socket.getInputStream().read();
				if(result==SocketServer.RECEIVE_NAME) {
					socket.getOutputStream().write(SocketClientHandler.serverName.length);
					socket.getOutputStream().write(SocketClientHandler.serverName);
					result = socket.getInputStream().read(); //await for respond
				}
				ServerClientConnectRespondeEvent respondeEvent = new ServerClientConnectRespondeEvent(this, result);
				EventManager.call(respondeEvent);
				if(result==SocketServer.ACCEPTED) {
					connected=true;
					//LOGGED IN, START READER
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
									ServerReceiveDataEvent event = new ServerReceiveDataEvent(SocketClientHandler.this, data);
									EventManager.call(event);
									return;
								}
								if(task==SocketServer.RECEIVE_NAME) {
									socket.getOutputStream().write(SocketClientHandler.serverName.length);
									socket.getOutputStream().write(SocketClientHandler.serverName);
								}
							} catch (IOException e1) {
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void end() {
		try {
			connected=false;
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
