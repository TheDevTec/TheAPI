package me.devtec.shared.sockets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ClientReceiveMessageEvent;
import me.devtec.shared.scheduler.Tasker;

public class Server {
	protected final Set<Reader> readers = new HashSet<>();
	protected final Map<Socket, ServerClient> sockets = new ConcurrentHashMap<>();
	protected ServerSocket server;
	protected final String pas;
	private boolean closed;
	
	public Server(String password, int port) {
		pas=password;
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(200);
			new Thread(new Runnable() {
				public void run() {
					while(!closed) {
						Socket receive;
						try {
							receive = server.accept();
						} catch (Exception e) {
							try {
								Thread.sleep(7000);
							} catch (Exception e1) {
							}
							continue;
						}
						BufferedReader reader;
						PrintWriter writer;
						try {
							reader = new BufferedReader(new InputStreamReader(receive.getInputStream()));
							writer = new PrintWriter(receive.getOutputStream(),true);
						} catch (Exception e) {
							try {
								receive.close();
							} catch (Exception e1) {
							}
							try {
								Thread.sleep(7000);
							} catch (Exception e1) {
							}
							continue;
						}
						ClientHandler handler = new ClientHandler(Server.this, receive, reader, writer);
		                sockets.put(receive, new ServerClient(handler));
		                handler.start();
					}
				}
			}).start();
		} catch (Exception e) {
			new Tasker() {
				public void run() {
					try {
					server = new ServerSocket(port);
					server.setSoTimeout(200);
					new Thread(new Runnable() {
						public void run() {
							while(!closed) {
								Socket receive;
								try {
									receive = server.accept();
								} catch (Exception e) {
									try {
										Thread.sleep(7000);
									} catch (Exception e1) {
									}
									continue;
								}
								BufferedReader reader;
								PrintWriter writer;
								try {
									reader = new BufferedReader(new InputStreamReader(receive.getInputStream()));
									writer = new PrintWriter(receive.getOutputStream(),true);
								} catch (Exception e) {
									try {
										receive.close();
									} catch (Exception e1) {
									}
									try {
										Thread.sleep(7000);
									} catch (Exception e1) {
									}
									continue;
								}
								ClientHandler handler = new ClientHandler(Server.this, receive, reader, writer);
				                sockets.put(receive, new ServerClient(handler));
				                handler.start();
							}
						}
					}).start();
					} catch (Exception e) {e.printStackTrace();}
				}
			}.runLater(100);
		}
    }
	
	public void writeAll(String path, Object object) {
		for(ServerClient s : sockets.values())
			s.write(path, object);
	}
	
	public void write(String client, String path, Object object) {
		for(ServerClient s : sockets.values())
			if(s.getName().equals(client))
				s.write(path, object);
	}
	
	public void sendAll() {
		for(ServerClient s : sockets.values())
			s.send();
	}
	
	public void send(String client) {
		for(ServerClient s : sockets.values())
			if(s.getName().equals(client))
				s.send();
	}
	
	protected void read(ServerClient client, final Config data) {
		EventManager.call(new ClientReceiveMessageEvent(client, data));
		readers.forEach(a -> {
			a.read(client, data);
		});
	}
	
	public void register(Reader reader) {
		readers.add(reader);
	}
	
	public void unregister(Reader reader) {
		readers.remove(reader);
	}
    
	public boolean isClosed() {
		return closed;
	}
	
    public void exit() {
		closed=true;
		try {
	    	sockets.values().forEach(s -> s.exit());
	    	sockets.clear();
		} catch (Exception e) {
		}
		try {
			server.getChannel().close();
		}catch (Exception e) {
		}
		try {
	    	server.close();
		}catch (Exception e) {
		}
    }
}