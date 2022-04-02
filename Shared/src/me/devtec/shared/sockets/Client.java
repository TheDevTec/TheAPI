package me.devtec.shared.sockets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import me.devtec.shared.API;
import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.DataType;

public abstract class Client implements SocketClient { 
	private BufferedReader receive; 
	private PrintWriter send; 
	private Socket s;
	private String name,ip,pas;
	private int port;
	
	private final Config data = new Config();
	
    public Client(String name, String password, String ip, int port) { 
        try {
        	this.name=name;
        	this.ip=ip;
        	pas=password;
        	this.port=port;
        	reconnect(3000);
        }catch(Exception e){
        }
    }
    
    public String getName() {
    	return name;
    }
    
    public String getIP() {
    	return ip;
    }
    
    public int getPort() {
    	return port;
    }
    
    private boolean logged,closed=true;
    public void reconnect(int trottle) {
		exit();
		new Thread(new Runnable() {
			int c = 0;
			public void run() {
				while(API.isEnabled() && c < trottle) {
					try {
						s = new Socket(ip, port);
						closed=false;
					} catch (Exception e2) {
			 			++c;
						try {
							Thread.sleep(3000);
						} catch (Exception e) {
						}
						continue;
					}
					if(isConnected()) {
						try {
							receive = new BufferedReader(new InputStreamReader(s.getInputStream()));
					    	send = new PrintWriter(s.getOutputStream(),true);
					    	send.println("login:"+name);
					    	send.flush();
					    	send.println("password:"+pas);
					    	send.flush();
					    	if(receive.readLine()!=null) { //logged in
						    	logged=true;
						    	if(postQueue!=null && !postQueue.isEmpty()) {
							    	for(String s : postQueue) {
							    		send.println(s);
								    	send.flush();
							    	}
							    	postQueue.clear();
						    	}
					        }
						} catch (Exception e) {
							try {
								s.close();
							} catch (Exception e1) {
							}
							continue;
						}
						new Thread(new Runnable() {
							public void run() {
								while(isConnected()) {
									try {
										Thread.sleep(100);
									} catch (Exception e1) {
									}
									try {
									String read = receive.readLine();
									if(read==null)continue;
									if(read.equals("exit")) {
										closed=true;
										break;
									}
									Config reader = new Config();
									reader.reload(read);
									read(reader);
									}catch(Exception e) {}
								}
								reconnect(trottle);
							}
						}).start();
						break;
					}
				}
			}
		}).start();
    }
    
    public boolean isConnected() {
    	return !closed;
    }
    
    public void write(String path, Object send) {
    	data.set(path, send);
    }
    
    private LinkedList<String> postQueue;
    
    public void send() {
    	if(logged) {
	    	try {
	    		send.println(data.toString(DataType.BYTE));
		    	send.flush();
			} catch (Exception e) {}
		}else {
    		if(postQueue==null)
    			postQueue = new LinkedList<>();
    		postQueue.add(data.toString(DataType.BYTE));
		}
		data.clear();
	}

	public void exit() {
		closed=true;
    	logged=false;
		try {
    		send.println("exit");
	    	send.flush();
			send.close(); 
			receive.close();
		}catch(Exception err) {}
		try {
			s.close();
		}catch(Exception err) {}
	}
    
    public abstract void read(Config data);
}

