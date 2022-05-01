package me.devtec.shared.utility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.devtec.shared.json.Json;

public class StreamUtils {
	
	/**
	 * @apiNote Read InputStream and convert into String with {@link System#lineSeparator()} as separator of lines
	 * @return String
	 */
	public static String fromStream(File file) {
		try {
			return fromStream(new FileInputStream(file));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @apiNote Read InputStream and convert into String with {@link System#lineSeparator()} as separator of lines
	 * @return String
	 */
	public static String fromStream(InputStream stream) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8), 4096);
			StringBuilder sb = new StringBuilder(2048);
			String content;
			while ((content = br.readLine()) != null) {
				if(sb.length() != 0)sb.append(System.lineSeparator());
				sb.append(content);
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @apiNote Read InputStream and convert into List<String> without seperator of lines
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
	
	/**
	 * @apiNote Write String into created InputStream
	 * @return InputStream
	 */
	public static InputStream toStream(String text) {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF(text);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @apiNote Write String into created InputStream
	 * @return InputStream
	 */
	public static InputStream toStreamObject(Object obj) {
		return toStream(Json.writer().write(obj));
	}
	
	/**
	 * @apiNote Read Object from InputStream
	 * @return Object
	 */
	public static Object fromStreamObject(InputStream stream) {
		return Json.reader().read(fromStream(stream));
	}
	
	/**
	 * @apiNote Read Object from InputStream
	 * @return Object
	 */
	public static Object fromStreamObject(File file) {
		return Json.reader().read(fromStream(file));
	}
}
