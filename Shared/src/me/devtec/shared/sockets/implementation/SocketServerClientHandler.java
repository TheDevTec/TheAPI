package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;

import com.google.common.collect.Queues;

import me.devtec.shared.API;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ClientResponde;
import me.devtec.shared.events.api.ServerClientConnectedEvent;
import me.devtec.shared.events.api.ServerClientRespondeEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;
import me.devtec.shared.sockets.SocketUtils;
import me.devtec.shared.sockets.implementation.SocketAction.SocketActionEnum;

public class SocketServerClientHandler implements SocketClient {
	private final String serverName;
	private final Socket socket;
	private final SocketServer socketServer;

	private DataInputStream in;
	private DataOutputStream out;
	private boolean connected = true;
	private boolean manuallyClosed;
	private int task = 0;
	private int ping;
	private Queue<SocketAction> actions = Queues.newLinkedBlockingDeque();

	private boolean lock;

	public SocketServerClientHandler(SocketServer server, String serverName, Socket socket) throws IOException {
		this(server, new DataInputStream(socket.getInputStream()), new DataOutputStream(socket.getOutputStream()),
				serverName, socket);
	}

	public SocketServerClientHandler(SocketServer server, DataInputStream in, DataOutputStream out, String serverName,
			Socket socket) {
		this.socket = socket;
		socketServer = server;
		this.in = in;
		this.out = out;
		this.serverName = serverName;
		// LOGGED IN, START READER
		new Thread(() -> {
			ServerClientConnectedEvent connectedEvent = new ServerClientConnectedEvent(SocketServerClientHandler.this);
			EventManager.call(connectedEvent);
			unlock();
			while (API.isEnabled() && isConnected()) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
				if (!isLocked())
					try {
						task = in.readInt();
						if (task == ClientResponde.PONG.getResponde()) {
							ping = in.readInt();
							try {
								Thread.sleep(100);
							} catch (Exception e) {
							}
							continue;
						}
						ServerClientRespondeEvent crespondeEvent = new ServerClientRespondeEvent(
								SocketServerClientHandler.this, task);
						EventManager.call(crespondeEvent);
						SocketUtils.process(this, task);
					} catch (Exception e) {
						break;
					}
			}
			if (socket != null && connected && !manuallyClosed)
				stop();
		}).start();
		// ping - pong service
		new Thread(() -> {
			while (API.isEnabled() && isConnected())
				if (!isLocked())
					try {
						Thread.sleep(5000);
						out.writeInt(ClientResponde.PING.getResponde());
						out.writeLong(System.currentTimeMillis() / 100);
					} catch (Exception e) {
						break;
					}
			if (socket != null && connected && !manuallyClosed)
				stop();
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
		return ping;
	}

	@Override
	public boolean isConnected() {
		return connected && socket != null && !socket.isInputShutdown() && !socket.isOutputShutdown()
				&& !socket.isClosed() && socket.isConnected();
	}

	@Override
	public void start() {
		throw new RuntimeException("Can't connect a socket that is not from the server side");
	}

	public SocketServer getSocketServer() {
		return socketServer;
	}

	@Override
	public void stop() {
		manuallyClosed = true;
		connected = false;
		try {
			socket.close();
		} catch (Exception e) {
		}
		getSocketServer().notifyDisconnect(this);
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
		return false;
	}

	@Override
	public void lock() {
		lock = true;
	}

	@Override
	public void unlock() {
		lock = false;
		while (!actionsAfterUnlock().isEmpty()) {
			SocketAction value = actionsAfterUnlock().poll();
			if (value.action == SocketActionEnum.DATA)
				write(value.config);
			else
				writeWithData(value.config, value.fileName, value.file);
		}
	}

	@Override
	public boolean isLocked() {
		return lock;
	}

	@Override
	public boolean shouldAddToQueue() {
		return isLocked();
	}

	@Override
	public Queue<SocketAction> actionsAfterUnlock() {
		return actions;
	}

}
