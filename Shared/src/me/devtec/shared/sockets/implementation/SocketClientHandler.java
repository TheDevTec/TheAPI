package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import me.devtec.shared.API;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerClientDisconnectedEvent;
import me.devtec.shared.events.api.ServerClientRespondeEvent;
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
	private int task = 0;
	private long lastPing;
	private long lastPong;

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
	public int ping() {
		return (int) (-lastPing + lastPong);
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
				if(!checkRawConnected())
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}
			}
			try {
				in=new DataInputStream(socket.getInputStream());
				out=new DataOutputStream(socket.getOutputStream());
			}catch(Exception err) {
				connected=false;
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
				ServerClientRespondeEvent respondeEvent = new ServerClientRespondeEvent(SocketClientHandler.this, result);
				EventManager.call(respondeEvent);
				if(result==SocketServer.ACCEPTED) {
					connected=true;
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
								ServerClientRespondeEvent crespondeEvent = new ServerClientRespondeEvent(SocketClientHandler.this, task);
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
						try {
							socket.close();
						}catch(Exception err) {
						}
						ServerClientDisconnectedEvent event = new ServerClientDisconnectedEvent(this);
						EventManager.call(event);
						socket=null;
						if(API.isEnabled())
							start();
					}).start();
					//ping - pong service
					new Thread(()->{
						while(isConnected() && API.isEnabled())
							try {
								Thread.sleep(15000);
								if(lastPing-System.currentTimeMillis()/100 <= 0) {
									lastPing = System.currentTimeMillis()/100;
									out.writeInt(20);
								}
							} catch (Exception e) {
								connected=false;
								break;
							}
					}).start();
				}
			}
		} catch (Exception e) {
			connected=false;
			if(API.isEnabled()) {
				try {
					Thread.sleep(5000);
				} catch (Exception err) {
				}
				start();
			}
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
		} catch (Exception e) {
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
