package me.devtec.shared.sockets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import me.devtec.shared.API;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.DataType;

public abstract class Client implements SocketClient {
	private BufferedReader receive;
	private PrintWriter send;
	private Socket s;
	private String name;
	private String ip;
	private String password;
	private int port;
	private boolean logged;
	private boolean closed = true;

	private LinkedList<String> postQueue;

	private final Config data = new Config();

	public Client(String name, String password, String ip, int port) {
		try {
			this.name = name;
			this.ip = ip;
			this.password = password;
			this.port = port;
			this.reconnect(3000);
		} catch (Exception e) {
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getIP() {
		return this.ip;
	}

	public int getPort() {
		return this.port;
	}

	public void reconnect(int trottle) {
		this.exit();
		new Thread(new Runnable() {
			int c = 0;

			@Override
			public void run() {
				while (API.isEnabled() && this.c < trottle) {
					try {
						Client.this.s = new Socket(Client.this.ip, Client.this.port);
						Client.this.closed = false;
					} catch (Exception e2) {
						++this.c;
						try {
							Thread.sleep(3000);
						} catch (Exception e) {
						}
						continue;
					}
					if (Client.this.isConnected()) {
						try {
							Client.this.receive = new BufferedReader(
									new InputStreamReader(Client.this.s.getInputStream()));
							Client.this.send = new PrintWriter(Client.this.s.getOutputStream(), true);
							Client.this.send.println("login:" + Client.this.name);
							Client.this.send.flush();
							Client.this.send.println("password:" + Client.this.password);
							Client.this.send.flush();
							if (Client.this.receive.readLine() != null) { // logged in
								Client.this.logged = true;
								if (Client.this.postQueue != null && !Client.this.postQueue.isEmpty()) {
									for (String s : Client.this.postQueue) {
										Client.this.send.println(s);
										Client.this.send.flush();
									}
									Client.this.postQueue.clear();
								}
							}
						} catch (Exception e) {
							try {
								Client.this.s.close();
							} catch (Exception e1) {
							}
							continue;
						}
						new Thread(() -> {
							while (Client.this.isConnected()) {
								try {
									Thread.sleep(100);
								} catch (Exception e1) {
								}
								try {
									String read = Client.this.receive.readLine();
									if (read == null)
										continue;
									if (read.equals("exit")) {
										Client.this.closed = true;
										break;
									}
									Config reader = new Config();
									reader.reload(read);
									Client.this.read(reader);
								} catch (Exception e) {
								}
							}
							Client.this.reconnect(trottle);
						}).start();
						break;
					}
				}
			}
		}).start();
	}

	@Override
	public boolean isConnected() {
		return !this.closed;
	}

	@Override
	public void write(String path, Object send) {
		this.data.set(path, send);
	}

	@Override
	public void send() {
		if (this.logged)
			try {
				this.send.println(this.data.toString(DataType.BYTE));
				this.send.flush();
			} catch (Exception e) {
			}
		else {
			if (this.postQueue == null)
				this.postQueue = new LinkedList<>();
			this.postQueue.add(this.data.toString(DataType.BYTE));
		}
		this.data.clear();
	}

	@Override
	public void exit() {
		this.closed = true;
		this.logged = false;
		try {
			this.send.println("exit");
			this.send.flush();
			this.send.close();
			this.receive.close();
		} catch (Exception err) {
		}
		try {
			this.s.close();
		} catch (Exception err) {
		}
	}

	public abstract void read(Config data);
}
