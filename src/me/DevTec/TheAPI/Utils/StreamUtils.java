package me.DevTec.TheAPI.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.DevTec.TheAPI.Utils.Json.Reader;
import me.DevTec.TheAPI.Utils.Json.Writer;

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
			return new String(ByteStreams.toByteArray(stream), StandardCharsets.UTF_8);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				stream.close();
			} catch (Exception e) {}
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
		} finally {
			try {
				stream.close();
			} catch (Exception e) {}
		}
	}
}
