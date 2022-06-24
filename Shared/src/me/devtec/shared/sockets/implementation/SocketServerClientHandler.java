package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import me.devtec.shared.API;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerClientRespondeEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;
import me.devtec.shared.sockets.SocketUtils;

public class SocketServerClientHandler implements SocketClient {
	private final String serverName;
	private final Socket socket;

	private DataInputStream in;
	private DataOutputStream out;
	private boolean connected = true;
	private int task = 0;
	private long lastPing;
	private long lastPong;

	public SocketServerClientHandler(SocketServer server, String serverName, Socket socket) {
		this.socket=socket;
		try {
			in=new DataInputStream(socket.getInputStream());
			out=new DataOutputStream(socket.getOutputStream());
		}catch(Exception err) {
		}
		this.serverName=serverName;
		//LOGGED IN, START READER
		lastPing = System.currentTimeMillis()/100;
		lastPong = System.currentTimeMillis()/100;
		new Thread(()->{
			while(isConnected() && API.isEnabled()) {
				try {
					task = in.readInt();
					if(task==20) { //ping
						out.writeInt(21);
						continue;
					}
					if(task==21) { //pong
						lastPong = System.currentTimeMillis()/100;
						continue;
					}
					ServerClientRespondeEvent crespondeEvent = new ServerClientRespondeEvent(SocketServerClientHandler.this, task);
					EventManager.call(crespondeEvent);
					SocketUtils.process(this, task);
				} catch (Exception e) {
					connected=false;
					break;
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
			}
			connected=false;
		}).start();
		//ping - pong service
		new Thread(()->{
			while(isConnected() && API.isEnabled())
				try {
					if(lastPing-System.currentTimeMillis()/100 + 5000 <= 0) {
						lastPing = System.currentTimeMillis()/100;
						out.writeInt(20);
					}
				} catch (Exception e) {
					connected=false;
					break;
				}
		}).start();
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
	public int ping() {
		return (int) (-lastPing + lastPong);
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
