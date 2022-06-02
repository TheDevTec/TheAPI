package me.devtec.shared.sockets.implementation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerClientConnectEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;

public class SocketServerHandler implements SocketServer {
	private final String serverName;
	private final int port;
	private ServerSocket serverSocket;
	private final Map<String, SocketClient> connected = new ConcurrentHashMap<>();
	private final String password;
	public SocketServerHandler(String serverName, int port, String password) {
		this.serverName=serverName;
		this.port=port;
		this.password=password;
	}

	@Override
	public String serverName() {
		return serverName;
	}

	@Override
	public Map<String, SocketClient> connectedClients() {
		return connected;
	}

	@Override
	public boolean isRunning() {
		return serverSocket!=null && serverSocket.isBound() && !serverSocket.isClosed();
	}

	@Override
	public void start() {
		try {
			serverSocket=new ServerSocket(port);
			serverSocket.setReuseAddress(true);
			new Thread(() -> {
				while(isRunning()) {
					try {
						Socket socket = serverSocket.accept();
						if(socket!=null && !isAlreadyConnected(socket))
							handleConnection(socket);
					} catch (Exception e) {
						//Nothing is connecting
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void handleConnection(Socket socket) {
		try {
			socket.getOutputStream().write(SocketServer.PROCESS_LOGIN);
			int size = socket.getInputStream().read();
			if(size != password.length()) {
				socket.getOutputStream().write(SocketServer.DECNILED);
				socket.close();
				//CLIENT DECNILED EVENT?
				return;
			}
			byte[] text = new byte[size];
			socket.getInputStream().read(text);
			if(!password.equals(new String(text))) {
				socket.getOutputStream().write(SocketServer.DECNILED);
				socket.close();
				//CLIENT DECNILED EVENT?
				return;
			}
			socket.getOutputStream().write(SocketServer.RECEIVE_NAME);
			size = socket.getInputStream().read();
			text = new byte[size];
			socket.getInputStream().read(text);
			String serverName = new String(text);
			ServerClientConnectEvent event = new ServerClientConnectEvent(socket, serverName);
			EventManager.call(event);
			if(event.isCancelled()) {
				socket.getOutputStream().write(SocketServer.DECNILED_PLUGIN);
				return;
			}
			socket.getOutputStream().write(SocketServer.ACCEPTED);
			connected.put(serverName, new SocketServerClientHandler(this, serverName, socket));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isAlreadyConnected(Socket socket) {
		for(SocketClient c : connected.values())
			if(c.getSocket().equals(socket))return true;
		return false;
	}

	@Override
	public void stop() {
		try {
			connected.values().forEach(SocketClient::end);
			connected.clear();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverSocket=null;
	}

}
