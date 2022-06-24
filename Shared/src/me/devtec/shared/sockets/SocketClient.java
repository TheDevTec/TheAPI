package me.devtec.shared.sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

import me.devtec.shared.dataholder.Config;

public interface SocketClient {
	public String serverName();

	public String ip();

	public int port();

	public boolean isConnected();

	public int ping();

	public default void write(String fileName, File file) {
		DataOutputStream out = getOutputStream();
		try {
			out.writeInt(SocketServer.RECEIVE_FILE);
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
			e.printStackTrace();
		}
	}

	public default void writeWithData(Config data, String fileName, File file) {
		DataOutputStream out = getOutputStream();
		try {
			out.writeInt(SocketServer.RECEIVE_DATA_AND_FILE);
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
			e.printStackTrace();
		}
	}

	public default void write(Config data) {
		DataOutputStream out = getOutputStream();
		try {
			byte[] path = data.toByteArray();
			out.writeInt(SocketServer.RECEIVE_DATA);
			out.writeInt(path.length);
			out.write(path);
			out.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public default void write(File file) {
		write(file.getName(), file);
	}

	public default void writeWithData(Config data, File file) {
		writeWithData(data, file.getName(), file);
	}

	public void start();

	public void stop();

	public DataInputStream getInputStream();

	public DataOutputStream getOutputStream();

	public Socket getSocket();
}
