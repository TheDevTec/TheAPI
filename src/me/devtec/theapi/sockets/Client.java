package me.devtec.theapi.sockets;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public abstract class Client { 
	private ObjectOutputStream send;
	private DataInputStream receive;
	private Socket s;
	private String name,ip;
	private int port;
	
    public Client(String name, String ip, int port) { 
        try {
        	this.name=name;
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
					    	if(s.isConnected()) {
						    	s.setSoTimeout(100);
						        receive = new DataInputStream(s.getInputStream()); 
								send = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
						        send.writeUTF("login:"+name);
						        break;
					    	}
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
						while(s.isConnected()) {
							try {
								String text = receive.readUTF();
								if(text.equals("exit")) {
									reconnect(trottle);
									break;
								}
								read(text.replaceFirst("chat:", ""));
							}catch(Exception err) {}
						}
						reconnect(trottle);
					}
				}
			}).start();
    	}catch(Exception e){
    	}
    }
    
    public boolean isConnected() {
    	return s!=null && s.isConnected();
    }
    
    public void write(String tosend) {
    	try {
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

