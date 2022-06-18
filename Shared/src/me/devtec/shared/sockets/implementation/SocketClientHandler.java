package me.devtec.shared.sockets.implementation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.loaders.ByteLoader;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ServerClientConnectRespondeEvent;
import me.devtec.shared.events.api.ServerPreReceiveFileEvent;
import me.devtec.shared.events.api.ServerReceiveDataEvent;
import me.devtec.shared.events.api.ServerReceiveFileEvent;
import me.devtec.shared.sockets.SocketClient;
import me.devtec.shared.sockets.SocketServer;

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
		return null;
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
		return connected && socket!=null && socket.isConnected() && !socket.isClosed();
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
		try {
			socket=new Socket(ip, port);
			socket.setReuseAddress(true);
			socket.setReceiveBufferSize(1024*2);
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(true);
			try {
				in=new DataInputStream(socket.getInputStream());
				out=new DataOutputStream(socket.getOutputStream());
			}catch(Exception err) {
			}
			//PROCESS LOGIN
			if(socket.isConnected() && !socket.isClosed() && in.read()==SocketServer.PROCESS_LOGIN) {
				out.write(password.length);
				out.write(password);
				int result = in.read();
				if(result==SocketServer.RECEIVE_NAME) {
					out.write(SocketClientHandler.serverName.length);
					out.write(SocketClientHandler.serverName);
					result = in.read(); //await for respond
				}
				ServerClientConnectRespondeEvent respondeEvent = new ServerClientConnectRespondeEvent(this, result);
				EventManager.call(respondeEvent);
				if(result==SocketServer.ACCEPTED) {
					connected=true;
					//LOGGED IN, START READER
					DataInputStream stream = new DataInputStream(in);
					new Thread(()->{
						while(isConnected()) {
							try {
								int task = stream.read();
								switch (task) {
								case SocketServer.RECEIVE_DATA: {
									byte[] path = new byte[stream.read()];
									stream.read(path);
									ByteLoader loader = new ByteLoader();
									loader.load(path);
									Config data = new Config(loader);
									ServerReceiveDataEvent event = new ServerReceiveDataEvent(SocketClientHandler.this, data);
									EventManager.call(event);
									break;
								}
								case SocketServer.RECEIVE_NAME:
									out.write(SocketClientHandler.serverName.length);
									out.write(SocketClientHandler.serverName);
									break;
								case SocketServer.RECEIVE_FILE: {
									byte[] fileName = new byte[stream.read()];
									stream.read(fileName);
									ServerPreReceiveFileEvent event = new ServerPreReceiveFileEvent(SocketClientHandler.this, new String(fileName));
									EventManager.call(event);
									if(event.isCancelled()) {
										int size = stream.read();
										byte[] buffer = new byte[2*1024];
										int bytes;
										while (size > 0 && (bytes = stream.read(buffer, 0, Math.min(buffer.length, size))) != -1)
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
									while (size > 0 && (bytes = stream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
										fileOutputStream.write(buffer,0,bytes);
										size -= bytes;
									}
									fileOutputStream.close();
									ServerReceiveFileEvent fileEvent = new ServerReceiveFileEvent(SocketClientHandler.this, createdFile);
									EventManager.call(fileEvent);
									break;
								}
								default:
									break;
								}
							} catch (IOException e1) {
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
