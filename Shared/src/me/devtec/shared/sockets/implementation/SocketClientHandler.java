package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import me.devtec.shared.API;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ClientResponde;
import me.devtec.shared.events.api.ServerClientConnectedEvent;
import me.devtec.shared.events.api.ServerClientRespondeEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketUtils;

public class SocketClientHandler implements SocketClient {
	public static byte[] serverName;

	private final String ip;
	private final int port;
	private Socket socket;
	private boolean connected;
	private boolean manuallyClosed;
	private byte[] password;

	private DataInputStream in;
	private  DataOutputStream out;
	private int task = 0;
	private int ping;

	private boolean lock;

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
		return ping;
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
		if(!API.isEnabled())
			return;
		try {
			while(API.isEnabled() && !checkRawConnected()) {
				socket=tryConnect();
				if(!checkRawConnected())
					try {
						Thread.sleep(5000);
					} catch (Exception e) {
					}
			}
			if(!checkRawConnected()) { //What happened? API is disabled?
				start();
				return;
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
			if(checkRawConnected() && in.readInt()==ClientResponde.LOGIN.getResponde()) {
				out.writeInt(password.length);
				out.write(password);
				int result = in.readInt(); // backwards support
				ServerClientRespondeEvent respondeEvent = new ServerClientRespondeEvent(SocketClientHandler.this, result);
				EventManager.call(respondeEvent);
				if(result==ClientResponde.REQUEST_NAME.getResponde()) {
					out.writeInt(SocketClientHandler.serverName.length);
					out.write(SocketClientHandler.serverName);
					result = in.readInt(); //await for respond
					respondeEvent = new ServerClientRespondeEvent(SocketClientHandler.this, result);
					EventManager.call(respondeEvent);
					if(result==ClientResponde.ACCEPTED_LOGIN.getResponde())
						openConnection();
				}
			}
		} catch (Exception e) {
			socket=null;
			connected=false;
			try {
				Thread.sleep(5000);
			} catch (Exception err) {
			}
			start();
		}
	}

	private void openConnection() {
		connected=true;
		manuallyClosed=false;
		//LOGGED IN, START READER
		new Thread(()->{
			ServerClientConnectedEvent connectedEvent = new ServerClientConnectedEvent(SocketClientHandler.this);
			EventManager.call(connectedEvent);
			while(API.isEnabled() && isConnected()) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				if(!isLocked())
					try {
						task = in.readInt();
						if(task==ClientResponde.PING.getResponde()) {
							long pingTime = in.readLong();
							ping = (int)(-pingTime + System.currentTimeMillis()/100);
							out.writeInt(ClientResponde.PONG.getResponde());
							out.writeInt(ping);
							try {
								Thread.sleep(100);
							} catch (Exception e) {
							}
							continue;
						}
						ServerClientRespondeEvent crespondeEvent = new ServerClientRespondeEvent(SocketClientHandler.this, task);
						EventManager.call(crespondeEvent);
						SocketUtils.process(this, task);
					} catch (Exception e) {
						break;
					}
			}
			if(socket!=null && connected && !manuallyClosed) {
				stop();
				start();
			}
		}).start();
	}

	private Socket tryConnect() {
		try {
			Socket socket=new Socket(ip, port);
			socket.setReuseAddress(true);
			socket.setKeepAlive(true);
			socket.setReceiveBufferSize(4*1024);
			socket.setTcpNoDelay(true);
			socket.setTrafficClass(0x02);
			socket.setSendBufferSize(4*1024);
			return socket;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public void stop() {
		manuallyClosed=true;
		connected=false;
		try {
			socket.close();
		} catch (Exception e) {
		}
		socket=null;
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

	@Override
	public boolean canReconnect() {
		return true;
	}

	@Override
	public void lock() {
		lock=true;
	}

	@Override
	public void unlock() {
		lock=false;
	}

	@Override
	public boolean isLocked() {
		return lock;
	}

}
