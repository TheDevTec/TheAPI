package me.devtec.shared.sockets;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.devtec.shared.dataholder.Config;
import me.devtec.shared.dataholder.loaders.ByteLoader;
import me.devtec.shared.events.EventManager;
import me.devtec.shared.events.api.ClientResponde;
import me.devtec.shared.events.api.ServerClientPreReceiveFileEvent;
import me.devtec.shared.events.api.ServerClientReceiveDataEvent;
import me.devtec.shared.events.api.ServerClientReceiveFileEvent;

public class SocketUtils {
	public static Config readConfig(DataInputStream in) throws IOException {
		byte[] path = new byte[in.readInt()];
		in.read(path);
		return new Config(ByteLoader.fromBytes(path));
	}

	public static String readText(DataInputStream in) throws IOException {
		byte[] path = new byte[in.readInt()];
		in.read(path);
		return new String(path);
	}

	public static boolean readFile(DataInputStream in, FileOutputStream out, File file) {
		int bytes;
		long origin;
		try {
			long size = in.readLong();
			origin = size;
			byte[] buffer = new byte[16 * 1024];
			long total = 0;
			while (total < size && (bytes = in.read(buffer, 0,
					size - total > buffer.length ? buffer.length : (int) (size - total))) > 0) {
				out.write(buffer, 0, bytes);
				total += bytes;
			}
			out.close();
		} catch (Exception err) {
			err.printStackTrace();
			return false;
		}
		return origin == file.length();
	}

	public static boolean process(SocketClient client, int taskId) throws IOException {
		DataInputStream in = client.getInputStream();
		Config data = null;
		switch (ClientResponde.fromResponde(taskId)) {
		case RECEIVE_DATA: {
			ServerClientReceiveDataEvent event = new ServerClientReceiveDataEvent(client, SocketUtils.readConfig(in));
			EventManager.call(event);
			return true;
		}
		case RECEIVE_DATA_AND_FILE:
			client.lock();
			data = SocketUtils.readConfig(in);
		case RECEIVE_FILE:
			client.lock();
			ServerClientPreReceiveFileEvent event = new ServerClientPreReceiveFileEvent(client, data,
					SocketUtils.readText(in));
			EventManager.call(event);
			if (event.isCancelled()) {
				client.getOutputStream().writeInt(ClientResponde.REJECTED_FILE.getResponde());
				client.getOutputStream().flush();
				client.unlock();
				return true;
			}
			client.getOutputStream().writeInt(ClientResponde.ACCEPTED_FILE.getResponde());
			client.getOutputStream().flush();
			String folder = event.getFileDirectory();
			if (!folder.isEmpty() && !folder.endsWith("/"))
				folder += "/";
			File createdFile = SocketUtils.findUsableName(folder + event.getFileName());
			FileOutputStream out = new FileOutputStream(createdFile);
			if (!SocketUtils.readFile(in, out, createdFile)) {
				client.getOutputStream().writeInt(ClientResponde.FAILED_DOWNLOAD_FILE.getResponde());
				client.getOutputStream().flush();
				createdFile.delete(); // Failed to download file! Repeat.
				client.unlock();
				return true;
			}
			client.getOutputStream().writeInt(ClientResponde.SUCCESSFULLY_DOWNLOADED_FILE.getResponde());
			client.getOutputStream().flush();
			ServerClientReceiveFileEvent fileEvent = new ServerClientReceiveFileEvent(client, data, createdFile);
			EventManager.call(fileEvent);
			client.unlock();
			return true;
		default:
			break;
		}
		return false;
	}

	private static File findUsableName(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			String end = fileName.split("\\.")[fileName.split("\\.").length - 1];
			return SocketUtils
					.findUsableName(fileName.substring(0, fileName.length() - (end.length() + 1)) + "-copy." + end);
		}
		if (file.getParentFile() != null)
			file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (Exception e) {
		}
		return file;
	}
}
