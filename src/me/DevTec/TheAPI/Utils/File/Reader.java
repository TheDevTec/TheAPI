package me.DevTec.TheAPI.Utils.File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Reader {

	public static String read(File f) {
		Reader a = new Reader(f);
		String d = a.read(true);
		a.close();
		return d;
	}

	public static String read(File f, boolean split) {
		Reader a = new Reader(f);
		String d = a.read(split);
		a.close();
		return d;
	}

	private BufferedReader sc;
	public Reader(File f) {
		try {
			FileInputStream fis = new FileInputStream(f);
		    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
		    sc = new BufferedReader(isr);
		} catch (Exception e) {
		}
	}

	public String read() {
		return read(true);
	}

	public String read(boolean split) {
		StringBuffer buffer = new StringBuffer();
		reset();
		sc.lines().iterator().forEachRemaining(s -> buffer.append(s+(split?System.lineSeparator():"")));
		return buffer.toString();
	}

	public void close() {
		if (sc != null)
			try {
				sc.close();
			} catch (Exception e) {
			}
	}

	public void reset() {
		if (sc != null)
			try {
				sc.reset();
			} catch (Exception e) {
			}
	}
}
