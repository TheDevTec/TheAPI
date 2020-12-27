package me.devtec.theapi.sockets;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.devtec.theapi.utils.datakeeper.collections.UnsortedSet;
import me.devtec.theapi.utils.datakeeper.maps.UnsortedMap;

public class Server {
	Set<Reader> readers = new HashSet<>();
	Set<ClientHandler> sockets = new HashSet<>();
	Map<String, Set<String>> queue = new UnsortedMap<>();
	ServerSocket server;
	String pass;
	
	public Server(String password, int port) {
		pass=password;
		try {
			server = new ServerSocket(port);
        	Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					stop();
				}
			}));
			new Thread(new Runnable() {
				public void run() {
					while(true) {
						Socket s = null;
			            try {
							s = server.accept();
			                DataInputStream dis = new DataInputStream(s.getInputStream()); 
			                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			                ClientHandler handler = new ClientHandler(Server.this, s, dis, dos);
			                sockets.add(handler);
			                handler.start();   
			            }catch (Exception e){
			                try {
			                	if(s!=null)
								s.close();
							} catch (IOException e1) {
							}
			            }
				}
			}}).start();
		} catch (Exception e) {
		}
    }
	
	public void sendAll(String text) {
		for(ClientHandler s : sockets)
			s.send(text);
	}
	
	public void send(String client, String text) {
		boolean found = false;
		for(ClientHandler s : sockets) {
			if(s.getUser().equals(client)) {
				s.send(text);
				found=true;
				break;
			}
		}
		if(!found) {
			Set<String> e = queue.getOrDefault(client, new UnsortedSet<>());
			e.add(text);
			queue.put(client, e);
		}
	}
	
	public void read(ClientHandler client, final String text) {
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
	    	sockets.forEach(s -> {
				try {
					s.end();
				} catch (Exception e) {
				}
			});
	    	sockets.clear();
	    	server.close();
		} catch (Exception e) {
		}
    }
}