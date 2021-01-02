package me.devtec.theapi.sockets;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import me.devtec.theapi.utils.datakeeper.collections.UnsortedSet;

public abstract class Client { 
	private DataOutputStream send;
	private DataInputStream receive;
	private Socket s;
	private String name, pass,ip;
	private UnsortedSet<String> e = new UnsortedSet<>();
	private int port;
	private long keep = System.currentTimeMillis();
	
    public Client(String name, String pass, String ip, int port) { 
        try {
        	this.name=name;
        	this.pass=pass;
        	this.ip=ip;
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
    
    public void reconnect(int trottle) {
		exit();
    	try {
			new Thread(new Runnable() {
				public void run() {
					boolean ccc = true;
					try {
			 		int c = 0;
			 		while(true) {
				 		try {
					    	s = new Socket(ip, port);
					        receive = new DataInputStream(s.getInputStream()); 
					        send = new DataOutputStream(s.getOutputStream());
					        send.writeUTF("ping");
					        receive.readUTF();
					        send.writeUTF("login:"+name);
					        send.writeUTF("password:"+pass);
					        break;
				 		}catch(Exception err) {
				 			Thread.sleep(50);
				 			if(c++ >= trottle) {
				 				ccc=false;
				 				break;
				 			}
				 		}
			 		}
					}catch(Exception er) {}
					if(ccc) {
						keep = System.currentTimeMillis()/20;
						boolean req = false;
						while(true) {
							if(System.currentTimeMillis()/20-keep >= 1000) {
								reconnect(trottle);
			            		break;
							}
							if(System.currentTimeMillis()/20-keep >= 500) {
								try {
									send.writeUTF("ping");
								} catch (Exception e) {
								}
							}
							try {
								String text = receive.readUTF();
								if(text.equals("ping")) {
									send.writeUTF("pong");
									continue;
								}
								if(text.equals("pong")) {
									keep=System.currentTimeMillis()/20;
									if(!req) {
										req=true;
										send.writeUTF("request");
									}
									continue;
								}
								if(text.equals("exit")) {
									reconnect(trottle);
									break;
								}
								if(text.equals("request")) {
									for(String a : e)write(a);
									e.clear();
									continue;
								}
								text=text.replaceFirst("chat:", "");
								read(text);
							}catch(Exception err) {}
						}
					}
				}
			}).start();
    	}catch(Exception e){
    	}
    }
    
    public boolean isConnected() {
    	return s!=null && !s.isClosed();
    }
    
    public void write(String tosend) {
    	try {
    		if(!isConnected()) {
    			e.add(tosend);
    			return;
    		}
    		send.writeUTF("chat:"+tosend);
		} catch (Exception e) {
		}
    }

	public void exit() {
		try {
    		send.writeUTF("exit");
			send.close(); 
			receive.close(); 
	        s.close();
		}catch(Exception err) {}
	}
    
    public abstract void read(String text);
}

