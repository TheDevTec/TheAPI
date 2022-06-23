package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerClientConnectEvent;
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
			out.writeInt(SocketServer.PROCESS_LOGIN);
			if(socket.isInputShutdown() || socket.isOutputShutdown())return;
			int size = in.readInt();
			if(size != password.length()) {
				out.writeInt(SocketServer.REJECTED);
				socket.close();
				//CLIENT REJECTED EVENT?
				return;
			}
			byte[] text = new byte[size];
			in.read(text);
			if(!password.equals(new String(text))) {
				out.writeInt(SocketServer.REJECTED);
				socket.close();
				//CLIENT REJECTED EVENT?
				return;
			}
			out.writeInt(SocketServer.RECEIVE_NAME);
			size = in.readInt();
			text = new byte[size];
			in.read(text);
			String serverName = new String(text);
			ServerClientConnectEvent event = new ServerClientConnectEvent(socket, serverName);
			EventManager.call(event);
			if(event.isCancelled()) {
				out.writeInt(SocketServer.REJECTED_PLUGIN);
				return;
			}
			out.writeInt(SocketServer.ACCEPTED);
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
			connected.forEach(SocketClient::stop);
			connected.clear();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		serverSocket=null;
	}

}
