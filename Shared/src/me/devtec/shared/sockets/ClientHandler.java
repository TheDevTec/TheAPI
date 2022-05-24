package me.devtec.shared.sockets;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.DataType;

public class ClientHandler extends Thread {
	protected final BufferedReader dis;
	protected final PrintWriter dos;
	protected final Socket s;
	protected String name;
	private boolean access;
	private boolean accessFull;
	protected final Server ser;
	protected ServerClient c;
	protected final Config data = new Config();
	protected boolean closed;

	private LinkedList<String> postQueue;

	public ClientHandler(Server server, Socket s, BufferedReader dis2, PrintWriter dos2) {
		this.s = s;
		this.ser = server;
		this.dis = dis2;
		this.dos = dos2;
	}

	public void exit() {
		this.closed = true;
		this.ser.sockets.remove(this.s);
		this.data.clear();
		try {
			this.dos.println("exit");
			this.dos.flush();
			this.dos.close();
			this.dis.close();
		} catch (Exception e) {
		}
		try {
			this.s.close();
		} catch (Exception e) {
		}
		this.interrupt();
	}

	public void write(String path, Object object) {
		this.data.set(path, object);
	}

	public void send() {
		if (this.accessFull)
			try {
				this.dos.println(this.data.toString(DataType.BYTE));
				this.dos.flush();
			} catch (Exception e) {
			}
		else {
			if (this.postQueue == null)
				this.postQueue = new LinkedList<>();
			this.postQueue.add(this.data.toString(DataType.BYTE));
		}
		this.data.clear();
	}

	public boolean isConnected() {
		return !this.closed;
	}

	@Override
	public void run() {
		while (this.isConnected())
			try {
				String read = this.dis.readLine();
				if (read == null)
					continue;
				if (read.startsWith("login:")) {
					if (!this.access) {
						this.access = true;
						this.name = read.substring(6);
					}
					continue;
				}
				if (read.startsWith("password:")) {
					if (this.access && !this.accessFull && this.ser.pas.equals(read.substring(9)))
						this.accessFull = true;
					this.dos.println("logged");
					this.dos.flush();
					if (this.postQueue != null && !this.postQueue.isEmpty()) {
						for (String s : this.postQueue)
							this.dos.println(s);
						this.dos.flush();
						this.postQueue.clear();
					}
					continue;
				}
				if (read.equals("exit")) {
					this.closed = true;
					break;
				}
				if (this.access && this.accessFull) {
					Config data = new Config();
					data.reload(read);
					this.ser.read(this.c, data);
				}
			} catch (Exception e) {
				break;
			}
		this.exit();
	}

	public String getUser() {
		return this.name;
	}
}
