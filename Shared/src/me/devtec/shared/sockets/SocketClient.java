package me.devtec.shared.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.events.api.ClientResponde;

public interface SocketClient {
	public String serverName();

	public String ip();

	public int port();

	public boolean isConnected();

	public boolean canReconnect();

	public int ping();

	public default void write(String fileName, File file) {
		if(fileName==null || file == null)return;
		DataOutputStream out = getOutputStream();
		try {
			out.writeInt(ClientResponde.RECEIVE_FILE.getResponde());
			byte[] bytesData = fileName.getBytes();
			out.writeInt(bytesData.length);
			out.write(bytesData);

			out.writeLong(file.length());
			FileInputStream fileInputStream = new FileInputStream(file);
			int bytes = 0;
			byte[] buffer = new byte[2*1024];
			while ((bytes=fileInputStream.read(buffer))!=-1)
				out.write(buffer,0,bytes);
			fileInputStream.close();
			out.flush();
		}catch(Exception e) {
			stop();
			if(canReconnect())
				start();
		}
	}

	public default void writeWithData(Config data, String fileName, File file) {
		if(data == null || fileName==null || file == null)return;
		DataOutputStream out = getOutputStream();
		try {
			out.writeInt(ClientResponde.RECEIVE_DATA_AND_FILE.getResponde());
			//data
			byte[] path = data.toByteArray();
			out.writeInt(path.length);
			out.write(path);
			//file
			byte[] bytesData = fileName.getBytes();
			out.writeInt(bytesData.length);
			out.write(bytesData);

			out.writeLong(file.length());
			FileInputStream fileInputStream = new FileInputStream(file);
			int bytes = 0;
			byte[] buffer = new byte[2*1024];
			while ((bytes=fileInputStream.read(buffer))!=-1)
				out.write(buffer,0,bytes);
			fileInputStream.close();
			out.flush();
		}catch(Exception e) {
			stop();
			if(canReconnect())
				start();
		}
	}

	public default void write(Config data) {
		if(data == null)return;
		DataOutputStream out = getOutputStream();
		try {
			byte[] path = data.toByteArray();
			out.writeInt(ClientResponde.RECEIVE_DATA.getResponde());
			out.writeInt(path.length);
			out.write(path);
			out.flush();
		}catch(Exception e) {
			stop();
			if(canReconnect())
				start();
		}
	}

	public default void write(File file) {
		if(file == null)return;
		write(file.getName(), file);
	}

	public default void writeWithData(Config data, File file) {
		if(data == null || file == null)return;
		writeWithData(data, file.getName(), file);
	}

	public void start();

	public void stop();

	public DataInputStream getInputStream();

	public DataOutputStream getOutputStream();

	public Socket getSocket();
}
