package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import me.devtec.shared.API;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ClientResponde;
import me.devtec.shared.events.api.ServerClientDisconnectedEvent;
import me.devtec.shared.events.api.ServerClientPreConnectEvent;
import me.devtec.shared.events.api.ServerClientRejectedEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;

public class SocketServerHandler implements SocketServer {
	private final String serverName;
	private final int port;
	private ServerSocket serverSocket;
	private final List<SocketClient> connected = new ArrayList<>();
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
	public List<SocketClient> connectedClients() {
		return connected;
	}

	@Override
	public boolean isRunning() {
		return serverSocket!=null && serverSocket.isBound() && !serverSocket.isClosed();
	}

	@Override
	public void notifyDisconnect(SocketClient client) {
		ServerClientDisconnectedEvent event = new ServerClientDisconnectedEvent(client);
		EventManager.call(event);
		connected.remove(client);
	}

	@Override
	public void start() {
		try {
			serverSocket=new ServerSocket(port);
			serverSocket.setReuseAddress(true);
			new Thread(() -> {
				while(API.isEnabled() && isRunning()) {
					try {
						Socket socket = serverSocket.accept();
						if(socket!=null && !isAlreadyConnected(socket))
							handleConnection(socket);
					} catch (Exception e) {
						//Nothing is connecting
					}
					try {
						Thread.sleep(1000);
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
			if(socket.isInputShutdown() || socket.isOutputShutdown())return;
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeInt(ClientResponde.PROCESS_LOGIN.getResponde());
			if(socket.isInputShutdown() || socket.isOutputShutdown())return;
			int size = in.readInt();
			if(size != password.length()) {
				out.writeInt(ClientResponde.REJECTED_PASSWORD.getResponde());
				socket.close();
				ServerClientRejectedEvent rejectedEvent = new ServerClientRejectedEvent(socket, serverName);
				EventManager.call(rejectedEvent);
				return;
			}
			byte[] text = new byte[size];
			in.read(text);
			if(!password.equals(new String(text))) {
				out.writeInt(ClientResponde.REJECTED_PASSWORD.getResponde());
				socket.close();
				ServerClientRejectedEvent rejectedEvent = new ServerClientRejectedEvent(socket, serverName);
				EventManager.call(rejectedEvent);
				return;
			}
			out.writeInt(ClientResponde.RECEIVE_NAME.getResponde());
			size = in.readInt();
			text = new byte[size];
			in.read(text);
			String serverName = new String(text);
			ServerClientPreConnectEvent event = new ServerClientPreConnectEvent(socket, serverName);
			EventManager.call(event);
			if(event.isCancelled()) {
				out.writeInt(ClientResponde.REJECTED_PLUGIN.getResponde());
				ServerClientRejectedEvent rejectedEvent = new ServerClientRejectedEvent(socket, serverName);
				EventManager.call(rejectedEvent);
				return;
			}
			out.writeInt(ClientResponde.ACCEPTED.getResponde());
			connected.add(new SocketServerClientHandler(this, serverName, socket));
		} catch (Exception e) {
		}
	}

	private boolean isAlreadyConnected(Socket socket) {
		for(SocketClient c : connected)
			if(c.getSocket().equals(socket))return true;
		return false;
	}

	@Override
	public void stop() {
		try {
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		connected.forEach(SocketClient::stop);
		connected.clear();
		serverSocket=null;
	}

}
