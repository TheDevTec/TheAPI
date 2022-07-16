package me.devtec.shared.events.api;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.events.Cancellable;
import me.devtec.shared.events.Event;
import me.devtec.shared.sockets.SocketClient;

public class ServerClientPreReceiveFileEvent extends Event implements Cancellable {
	private String fileName;
	private String fileDirectory;
	private final Config data;
	private final SocketClient client;
	private boolean cancelled;

	public ServerClientPreReceiveFileEvent(SocketClient client, Config data, String received) {
		fileName = received;
		fileDirectory = "downloads/";
		this.client = client;
		this.data = data;
	}

	public SocketClient getClient() {
		return client;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String name) {
		fileName = name;
	}

	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String directory) {
		fileDirectory = directory == null ? "" : directory;
	}

	/**
	 *
	 * @apiNote Nullable
	 */
	public Config getData() {
		return data;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}
