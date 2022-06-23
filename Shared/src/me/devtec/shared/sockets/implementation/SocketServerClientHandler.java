package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerClientConnectRespondeEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;
import me.devtec.shared.sockets.SocketUtils;

public class SocketServerClientHandler implements SocketClient {
	private final String serverName;
	private final Socket socket;

	private DataInputStream in;
	private DataOutputStream out;
	private boolean connected = true;

	public SocketServerClientHandler(SocketServer server, String serverName, Socket socket) {
		this.socket=socket;
		try {
			in=new DataInputStream(socket.getInputStream());
			out=new DataOutputStream(socket.getOutputStream());
		}catch(Exception err) {
		}
		this.serverName=serverName;
		try {
			if(!isConnected())return;
			DataInputStream stream = in;
			new Thread(()->{
				while(isConnected()) {
					try {
						int task = stream.readInt();
						ServerClientConnectRespondeEvent crespondeEvent = new ServerClientConnectRespondeEvent(this, task);
						EventManager.call(crespondeEvent);
						SocketUtils.process(this, task);
					}catch(Exception e) {
						break;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}).start();
		} catch (Exception e1) {
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
		return connected && socket!=null && !socket.isInputShutdown() && !socket.isOutputShutdown() && !socket.isClosed() && socket.isConnected();
	}

	@Override
	public void start() {
		throw new RuntimeException("Can't connect a socket that is not from the server side");
	}

	@Override
	public void stop() {
		try {
			connected=false;
			socket.close();
		} catch (Exception e) {
		}
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

	@Override
	public DataInputStream getInputStream() {
		return in;
	}

	@Override
	public DataOutputStream getOutputStream() {
		return out;
	}

}
