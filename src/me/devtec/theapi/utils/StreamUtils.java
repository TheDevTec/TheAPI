package me.devtec.theapi.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.devtec.theapi.utils.json.Reader;
import me.devtec.theapi.utils.json.Writer;

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
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			StringBuffer sb = new StringBuffer(512);
			String content;
			while ((content = br.readLine()) != null)
				sb.append(content+System.lineSeparator());
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
		out.writeUTF(Writer.write(obj));
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	public static Object fromStreamObject(InputStream stream) {
		try {
			return Reader.read(ByteStreams.newDataInput(ByteStreams.toByteArray(stream)).readUTF()); //object
		} catch (Exception e) {
			return null;
		}
	}
}
