package me.devtec.theapi.sockets;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
	final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s;
    String name;
    boolean access;
    final Server ser;
    boolean run = true, logged;
	long keep = System.currentTimeMillis();
	ServerClient c;
    
    public ClientHandler(Server server, Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        ser=server;
        this.dis = dis; 
        this.dos = dos;
    }
    
    public void end() {
    	run=false;
    	ser.sockets.remove(s);
    	try {
			dos.writeUTF("exit");
    		dos.close();
    		dis.close();
			s.close();
		} catch (Exception e) {
		}
    	interrupt();
    }
    
    public void send(String text) {
        try {
			dos.writeUTF("chat:"+text);
		} catch (Exception e) {
		}
    }
  
    @Override
    public void run() {
    	keep = System.currentTimeMillis();
        while (run) {
			if(System.currentTimeMillis()-keep >= 1000) {
        		end();
        		break;
			}
			if(System.currentTimeMillis()-keep >= 500) {
				try {
					dos.writeUTF("ping");
				} catch (Exception e) {
				}
			}
			
            try {
            	String received = dis.readUTF();
            	if(received.equals("pong")) {
            		keep=System.currentTimeMillis();
            		continue;
            	}
            	if(received.startsWith("login:")) {
            		if(access)continue;
            		access=true;
                	received=received.replaceFirst("login:", "");
                	name=received;
            		continue;
            	}
            	if(received.startsWith("password:") && access) {
            		if(logged)continue;
                	received=received.replaceFirst("password:", "");
                	if(received.equals(ser.pass))
                		logged=true;
            		continue;
            	}
            	if(received.equals("exit")) {
            		end();
            		break;
            	}
            	if(received.equals("ping")) {
            		dos.writeUTF("pong");
            		continue;
            	}
            	if(!access || !logged)continue;
            	if(received.equals("request")) {
            		if(ser.queue.get(name)!=null)
            		for(String q : ser.queue.get(name))send(q);
            		ser.queue.remove(name);
            		dos.writeUTF("request");
            		continue;
            	}
            	received=received.replaceFirst("chat:", "");
            	ser.read(c, received);
            } catch (Exception e) {
            } 
        }
    }
    
    public String getUser() {
    	return name;
    }
}
