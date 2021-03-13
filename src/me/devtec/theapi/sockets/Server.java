package me.devtec.theapi.sockets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

public class Server {
	protected Set<Reader> readers = new HashSet<>();
	protected Map<Socket, ServerClient> sockets = new HashMap<>();
	protected ServerSocket server;
	protected String pas;
	
	public Server(String password, int port) {
		try {
			pas=password;
			server = new ServerSocket(port);
			new Thread(new Runnable() {
				public void run() {
					while(!server.isClosed()) {
						try {
			            	Socket s = server.accept();
							if(sockets.containsKey(s))sockets.get(s).exit();
					        s.setSoTimeout(LoaderClass.plugin.receive_speed);
					        BufferedReader dis = new BufferedReader(new InputStreamReader(s.getInputStream()));
					    	PrintWriter dos = new PrintWriter(s.getOutputStream(), true);
			                ClientHandler handler = new ClientHandler(Server.this, s, dis, dos);
			                sockets.put(s, new ServerClient(handler));
			                handler.start();
							Thread.sleep(LoaderClass.plugin.relog);
						}catch(Exception e) {}
					}
				}
			}).start();
		} catch (Exception e) {
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
	
	protected void read(ServerClient client, final Data data) {
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
    
    public void exit() {
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