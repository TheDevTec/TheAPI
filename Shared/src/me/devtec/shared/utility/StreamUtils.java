package me.devtec.shared.utility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.devtec.shared.json.Json;

public class StreamUtils {
	public static String fromStream(File file) {
		try {
			return fromStream(new FileInputStream(file));
		} catch (Exception e) {
			return null;
		}
	}

	public static Object fromStreamObject(File file) {
		try {
			return fromStreamObject(new FileInputStream(file));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String fromStream(InputStream stream) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8), 8192);
			StringBuilder sb = new StringBuilder(512);
			String content;
			while ((content = br.readLine()) != null)
				sb.append(content).append(System.lineSeparator());
			br.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	public static InputStream toStream(String text) {
		try {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF(text);
			return new ByteArrayInputStream(out.toByteArray());
		} catch (Exception e) {
			return null;
		}
	}
	
	public static InputStream toStreamObject(Object obj) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF(Json.writer().write(obj));
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	public static Object fromStreamObject(InputStream stream) {
		try {
			return Json.reader().read(ByteStreams.newDataInput(ByteStreams.toByteArray(stream)).readUTF()); //object
		} catch (Exception e) {
			return null;
		}
	}
}
