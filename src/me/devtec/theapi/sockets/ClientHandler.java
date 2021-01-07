package me.devtec.theapi.sockets;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;

public class ClientHandler extends Thread {
	final BufferedReader dis; 
    final PrintWriter dos; 
    final Socket s;
    String name;
    boolean access, accessFull;
    final Server ser;
	ServerClient c;
	private final Data data = new Data();
    
    public ClientHandler(Server server, Socket s, BufferedReader dis2, PrintWriter dos2) {
        this.s = s;
        ser=server;
        this.dis = dis2; 
        this.dos = dos2;
    }
    
    public void exit() {
    	ser.sockets.remove(s);
    	data.clear();
    	try {
    		dos.println("exit");
    		dos.flush(); 
    		dos.close();
    		dis.close();
			s.close();
		} catch (Exception e) {
		}
    	interrupt();
    }
    
    public void write(String path, Object object) {
    	data.set(path, object);
    }
    
    public void send() {
    	try {
    		dos.println(data.toString(DataType.BYTE));
    		dos.flush(); 
		} catch (Exception e) {}
    	data.clear();
    }
  
    @Override
    public void run() {
        while (s.isConnected()) {
            try {
            	String received = dis.readLine();
            	if(received!=null) {
                	if(received.startsWith("login:")) {
                		if(!access) {
                		access=true;
                    	name=received.replaceFirst("login:", "");
                		}
                		continue;
                	}
                	if(received.startsWith("password:")) {
                		if(access && !accessFull && ser.pas.equals(received.replaceFirst("password:", "")))
                			accessFull=true;
                		continue;
                	}
	            	if(received.equals("exit")) {
	            		exit();
	            		break;
	            	}
	            	if(access && accessFull) {
	            		Data data = new Data();
	            		data.reload(received);
	            		ser.read(c, data);
	            	}
	            }else Thread.sleep(50);
            } catch (Exception e) {
            	try {
					Thread.sleep(50);
				} catch (Exception e1) {
				}
            }
        }
        exit();
    }
    
    public String getUser() {
    	return name;
    }
}
