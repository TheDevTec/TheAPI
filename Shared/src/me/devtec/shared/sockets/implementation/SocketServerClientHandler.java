package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.loaders.ByteLoader;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerPreReceiveFileEvent;
import me.devtec.shared.events.api.ServerReceiveDataEvent;
import me.devtec.shared.events.api.ServerReceiveFileEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;

public class SocketServerClientHandler implements SocketClient {
	private final String serverName;
	private final Socket socket;

	private DataInputStream in;
	private DataOutputStream out;
	private boolean connected = true;

	public SocketServerClientHandler(SocketServer server, String serverName, Socket socket) {
		this.socket=socket;
		try {
			in=new DataInputStream(socket.getInputStream());
			out=new DataOutputStream(socket.getOutputStream());
		}catch(Exception err) {
		}
		this.serverName=serverName;
		try {
			if(!isConnected())return;
			DataInputStream stream = in;
			new Thread(()->{
				while(isConnected()) {
					try {
						int task = stream.read();
						if(task==SocketServer.RECEIVE_DATA) {
							byte[] path = new byte[stream.read()];
							stream.read(path);
							ByteLoader loader = new ByteLoader();
							loader.load(path);
							Config data = new Config(loader);
							ServerReceiveDataEvent event = new ServerReceiveDataEvent(SocketServerClientHandler.this, data);
							EventManager.call(event);
							continue;
						}
						if(task==SocketServer.RECEIVE_FILE) {
							byte[] fileName = new byte[stream.read()];
							stream.read(fileName);
							ServerPreReceiveFileEvent event = new ServerPreReceiveFileEvent(SocketServerClientHandler.this, new String(fileName));
							EventManager.call(event);
							if(event.isCancelled()) {
								long size = stream.readLong();
								byte[] buffer = new byte[2*1024];
								int bytes;
								while (size > 0 && (bytes = stream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
									size -= bytes;
								continue;
							}
							File createdFile = new File(event.getFileDirectory()+event.getFileName());
							if(createdFile.exists())createdFile.delete();
							else {
								if(createdFile.getParentFile()!=null)
									createdFile.getParentFile().mkdirs();
								createdFile.createNewFile();
							}
							int bytes = 0;
							FileOutputStream fileOutputStream = new FileOutputStream(createdFile);
							long size = stream.readLong();
							byte[] buffer = new byte[2*1024];
							while (size > 0 && (bytes = stream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
								fileOutputStream.write(buffer,0,bytes);
								size -= bytes;
							}
							fileOutputStream.close();
							ServerReceiveFileEvent fileEvent = new ServerReceiveFileEvent(SocketServerClientHandler.this, createdFile);
							EventManager.call(fileEvent);
						}
					}catch(SocketException e) {

					}catch (IOException e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}).start();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
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
	public boolean isConnected() {
		return connected && socket!=null && !socket.isInputShutdown() && !socket.isOutputShutdown() && !socket.isClosed() && socket.isConnected();
	}

	@Override
	public void write(File file) {
		try {
			out.write(SocketServer.RECEIVE_FILE);
			out.write(file.getName().length());
			out.write(file.getName().getBytes());

			out.writeLong((int)file.length());
			FileInputStream fileInputStream = new FileInputStream(file);
			int bytes = 0;
			byte[] buffer = new byte[2*1024];
			while ((bytes=fileInputStream.read(buffer))!=-1)
				out.write(buffer,0,bytes);
			fileInputStream.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(Config data) {
		try {
			byte[] path = data.toByteArray();
			out.write(SocketServer.RECEIVE_DATA);
			out.write(path.length);
			out.write(path);
		}catch(Exception e) {
			e.printStackTrace();
		}
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

}
