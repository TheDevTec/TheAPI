package me.devtec.shared.sockets;

public class ServerClient implements SocketClient {
	private final ClientHandler c;

	public ServerClient(ClientHandler c) {
		this.c = c;
		c.c = this;
	}

	@Override
	public String getName() {
		return this.c.getUser();
	}

	@Override
	public void exit() {
		this.c.exit();
	}

	@Override
	public void write(String path, Object value) {
		this.c.write(path, value);
	}

	@Override
	public void send() {
		this.c.send();
	}

	@Override
	public boolean isConnected() {
		return this.c.s.isConnected();
	}
}
