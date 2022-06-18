package me.devtec.shared.events.api;

import me.devtec.shared.events.Cancellable;
import me.devtec.shared.events.Event;
import me.devtec.shared.sockets.SocketClient;

public class ServerPreReceiveFileEvent extends Event implements Cancellable {
	private final String fileName;
	private String fileDirectory;
	private final SocketClient client;
	private boolean cancelled;

	public ServerPreReceiveFileEvent(SocketClient client, String received) {
		fileName = received;
		fileDirectory="downloads/";
		this.client = client;
	}

	public SocketClient getClient() {
		return client;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String directory) {
		fileDirectory=directory==null?"":directory;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled=cancel;
	}
}
