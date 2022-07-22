package me.devtec.shared.utility;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreamUtils {

	/**
	 * @apiNote Read InputStream and convert into String with
	 *          {@link System#lineSeparator()} as separator of lines
	 * @return String
	 */
	public static String fromStream(File file) {
		try {
			FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
			StringBuilder out = new StringBuilder();

			int bufferSize = 4096;
			if (bufferSize > channel.size())
				bufferSize = (int) channel.size();
			ByteBuffer buff = ByteBuffer.allocate(bufferSize);
			while (channel.read(buff) > 0) {
				for (byte charr : buff.array())
					out.append((char) charr);
				buff.clear();
			}
			return out.toString();
		} catch (Exception err) {
			return null;
		}
	}

	/**
	 * @apiNote Read InputStream and convert into String with
	 *          {@link System#lineSeparator()} as separator of lines
	 * @return String
	 */
	public static String fromStream(InputStream stream) {
		try {
			ReadableByteChannel channel = Channels.newChannel(stream);
			StringBuilder out = new StringBuilder();

			int bufferSize = 2048;
			ByteBuffer buff = ByteBuffer.allocate(bufferSize);
			while (channel.read(buff) > 0) {
				for (byte charr : buff.array())
					out.append((char) charr);
				buff.clear();
			}
			return out.toString();
		} catch (Exception err) {
			return null;
		}
	}

	/**
	 * @apiNote Read InputStream and convert into List<String> without seperator of
	 *          lines
	 * @return List<String>
	 */
	public static List<String> fromStreamToList(InputStream stream) {
		String readen = fromStream(stream);
		String[] splitLines = readen.split(System.lineSeparator());
		List<String> lines = new ArrayList<>();
		Collections.addAll(lines, splitLines);
		return lines;
	}
}
