package me.devtec.theapi.sockets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;

public abstract class Client { 
	private PrintWriter send;
	private BufferedReader receive;
	private Socket s;
	private String name,ip,pas;
	private int port;
	
	private Data data = new Data();
	
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
						    	receive = new BufferedReader(new InputStreamReader(s.getInputStream()));
						        send = new PrintWriter(s.getOutputStream(), true);
						    	send.println("login:"+name);
						    	send.println("password:"+pas);
						    	send.flush();
						        break;
					    	}
				 		}catch(Exception err) {
				 			if(c++ >= trottle) {
				 				ccc=false;
				 				break;
				 			}
				 			Thread.sleep(50*60);
				 		}
			 		}
					}catch(Exception er) {}
					if(ccc) {
						Data reader = new Data();
						while(s.isConnected()) {
							try {
								String text = receive.readLine();
								if(text.equals("exit")) {
									reconnect(trottle);
									break;
								}
								reader.reload(text);
								read(reader);
								Thread.sleep(50);
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
    
    public void write(String path, Object send) {
    	data.set(path, send);
    }
    
    public void send() {
    	try {
    		send.println(data.toString(DataType.BYTE));
	    	send.flush();
		} catch (Exception e) {}
		data.clear();
    }

	public void exit() {
		try {
    		send.println("exit");
	    	send.flush();
			send.close(); 
			receive.close(); 
	        s.close();
		}catch(Exception err) {}
	}
    
    public abstract void read(Data data);
}

