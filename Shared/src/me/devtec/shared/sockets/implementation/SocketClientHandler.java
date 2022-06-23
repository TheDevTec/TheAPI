package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import me.devtec.shared.API;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerClientConnectRespondeEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;
import me.devtec.shared.sockets.SocketUtils;

public class SocketClientHandler implements SocketClient {
	public static byte[] serverName;

	private final String ip;
	private final int port;
	private Socket socket;
	private boolean connected;
	private byte[] password;

	private DataInputStream in;
	private  DataOutputStream out;

	public SocketClientHandler(String ip, int port, String password) {
		this.ip=ip;
		this.port=port;
		this.password=password.getBytes();
	}

	@Override
	public String serverName() {
		return new String(SocketClientHandler.serverName);
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
		return connected && checkRawConnected();
	}

	public boolean checkRawConnected() {
		return socket!=null && !socket.isInputShutdown() && !socket.isOutputShutdown() && !socket.isClosed() && socket.isConnected();
	}

	@Override
	public void start() {
		try {
			while(API.isEnabled() && !checkRawConnected()) {
				socket=tryConnect();
				if(socket==null || !checkRawConnected())
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}
			}
			try {
				in=new DataInputStream(socket.getInputStream());
				out=new DataOutputStream(socket.getOutputStream());
			}catch(Exception err) {
				if(API.isEnabled())
					start();
				return;
			}
			//PROCESS LOGIN
			if(checkRawConnected() && in.readInt()==SocketServer.PROCESS_LOGIN) {
				out.writeInt(password.length);
				out.write(password);
				int result = in.readInt();
				if(result==SocketServer.RECEIVE_NAME) {
					out.writeInt(SocketClientHandler.serverName.length);
					out.write(SocketClientHandler.serverName);
					result = in.readInt(); //await for respond
				}
				ServerClientConnectRespondeEvent respondeEvent = new ServerClientConnectRespondeEvent(SocketClientHandler.this, result);
				EventManager.call(respondeEvent);
				if(result==SocketServer.ACCEPTED) {
					connected=true;
					//LOGGED IN, START READER
					new Thread(()->{
						while(isConnected() && API.isEnabled()) {
							try {
								int task = in.readInt();
								ServerClientConnectRespondeEvent crespondeEvent = new ServerClientConnectRespondeEvent(SocketClientHandler.this, task);
								EventManager.call(crespondeEvent);
								SocketUtils.process(this, task);
							} catch (IOException e1) {
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						if(API.isEnabled())
							start();
					}).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Socket tryConnect() {
		try {
			Socket socket=new Socket(ip, port);
			socket.setReuseAddress(true);
			socket.setKeepAlive(true);
			socket.setReceiveBufferSize(4*1024);
			socket.setTcpNoDelay(true);
			return socket;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
