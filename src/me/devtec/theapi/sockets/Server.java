package me.devtec.theapi.sockets;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.devtec.theapi.utils.datakeeper.maps.UnsortedMap;

public class Server {
	private Set<Reader> readers = new HashSet<>();
	protected Map<Socket, ServerClient> sockets = new UnsortedMap<>();
	private ServerSocket server;
	
	public Server(int port) {
		try {
			server = new ServerSocket(port);
			new Thread(new Runnable() {
				public void run() {
					while(!server.isClosed()) {
						try {
							Thread.sleep(1000);
			            	Socket s = server.accept();
							if(sockets.containsKey(s))sockets.get(s).exit();
					        s.setSoTimeout(100);
							DataInputStream dis = new DataInputStream(s.getInputStream()); 
							ObjectOutputStream dos = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
			                ClientHandler handler = new ClientHandler(Server.this, s, dis, dos);
			                sockets.put(s, new ServerClient(handler));
			                handler.start();
						}catch(Exception e) {}
					}
				}
			}).start();
		} catch (Exception e) {
		}
    }
	
	public void sendAll(String text) {
		for(ServerClient s : sockets.values())
			s.send(text);
	}
	
	public void send(String client, String text) {
		for(ServerClient s : sockets.values())
			if(s.getName().equals(client))
				s.send(text);
	}
	
	public void read(ServerClient client, final String text) {
		readers.forEach(a -> {
			a.read(client, text);
		});
	}
	
	public void register(Reader reader) {
		readers.add(reader);
	}
	
	public void unregister(Reader reader) {
		readers.remove(reader);
	}
    
    public void stop() {
		try {
	    	sockets.values().forEach(s -> {
				try {
					s.exit();
				} catch (Exception e) {
				}
			});
	    	sockets.clear();
	    	server.close();
		} catch (Exception e) {
		}
    }
}