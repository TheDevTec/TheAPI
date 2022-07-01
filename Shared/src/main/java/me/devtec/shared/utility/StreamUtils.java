package me.devtec.shared.utility;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StreamUtils {

	/**
	 * @apiNote Read InputStream and convert into String with
	 *          {@link System#lineSeparator()} as separator of lines
	 * @return String
	 */
	public static String fromStream(File file) {
		try {
			return StreamUtils.fromStream(new FileInputStream(file));
		} catch (Exception e) {
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
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8), 4096);
			StringBuilder sb = new StringBuilder(2048);
			String content;
			while ((content = br.readLine()) != null) {
				if (sb.length() != 0)
					sb.append(System.lineSeparator());
				sb.append(content);
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @apiNote Read InputStream and convert into List<String> without seperator of
	 *          lines
	 * @return List<String>
	 */
	public static List<String> fromStreamToList(InputStream stream) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8), 4096);
			List<String> lines = new ArrayList<>();
			String content;
			while ((content = br.readLine()) != null)
				lines.add(content);
			br.close();
			return lines;
		} catch (Exception e) {
			return null;
		}
	}
}
