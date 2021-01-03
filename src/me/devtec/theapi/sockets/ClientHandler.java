package me.devtec.theapi.sockets;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
	final DataInputStream dis; 
    final ObjectOutputStream dos; 
    final Socket s;
    String name;
    boolean access;
    final Server ser;
	ServerClient c;
    
    public ClientHandler(Server server, Socket s, DataInputStream dis, ObjectOutputStream dos) {
        this.s = s;
        ser=server;
        this.dis = dis; 
        this.dos = dos;
    }
    
    public void end() {
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
        while (s.isConnected()) {
            try {
            	String received = dis.readUTF();
            	if(received.startsWith("login:")) {
            		if(!access) {
            		access=true;
                	received=received.replaceFirst("login:", "");
                	name=received;
            		}
            		continue;
            	}
            	if(received.equals("exit")) {
            		end();
            		break;
            	}
            	if(access)
            	ser.read(c, received.replaceFirst("chat:", ""));
            } catch (Exception e) {}
        }
        end();
    }
    
    public String getUser() {
    	return name;
    }
}
