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
	private Set<Reader> readers = new HashSet<>();
	protected Map<Socket, ServerClient> sockets = new UnsortedMap<>();
	protected Map<String, Set<String>> queue = new UnsortedMap<>();
	private ServerSocket server;
	protected String pass;
	private boolean run=true;
	
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
					while(run) {
						Socket s = null;
			            try {
							s = server.accept();
							if(sockets.containsKey(s))continue;
			                DataInputStream dis = new DataInputStream(s.getInputStream()); 
			                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			                ClientHandler handler = new ClientHandler(Server.this, s, dis, dos);
			                sockets.put(s, new ServerClient(handler));
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
		for(ServerClient s : sockets.values())
			s.send(text);
	}
	
	public void send(String client, String text) {
		boolean found = false;
		for(ServerClient s : sockets.values()) {
			if(s.getName().equals(client)) {
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
			run=false;
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