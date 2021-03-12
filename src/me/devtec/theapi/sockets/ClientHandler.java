package me.devtec.theapi.sockets;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import me.devtec.theapi.utils.datakeeper.Data;
import me.devtec.theapi.utils.datakeeper.DataType;
import me.devtec.theapi.utils.theapiutils.LoaderClass;

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

    
    private LinkedList<String> postQueue;
    
    public void send() {
    	if(accessFull) {
	    	try {
	    		dos.println(data.toString(DataType.BYTE));
	    		dos.flush();
			} catch (Exception e) {}
			data.clear();
    	}else {
    		if(postQueue==null)
    			postQueue = new LinkedList<>();
    		postQueue.add(data.toString(DataType.BYTE));
    		data.clear();
    	}
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
                		dos.println("logged");
                		dos.flush();
				    	for(String s : postQueue) {
				    		dos.println(s);
				    	}
				    	if(!postQueue.isEmpty())
				    		dos.flush();
				    	postQueue.clear();
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
	            }else Thread.sleep(LoaderClass.plugin.relog);
            } catch (Exception e) {
            	try {
					Thread.sleep(LoaderClass.plugin.relog);
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
