package me.DevTec.TheAPI.Utils.File;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

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

	private Scanner sc;
	public Reader(File f) {
		try {
			sc = new Scanner(new FileReader(f));
		} catch (Exception e) {
		}
	}

	public String read() {
		return read(true);
	}

	public String read(boolean split) {
		StringBuffer buffer = new StringBuffer();
		reset();
		try {
			while (sc.hasNextLine()) {
				if (split)
					buffer.append(sc.nextLine() + System.lineSeparator());
				else
					buffer.append(sc.nextLine());
			}
			reset();
		} catch (Exception e) {
		}
		return buffer.toString();
	}

	public String readLine() {
		if (sc != null)
			return sc.nextLine();
		return null;
	}

	public boolean hasNext() {
		if (sc != null)
			return sc.hasNextLine();
		return false;
	}

	public void close() {
		if (sc != null)
			sc.close();
	}

	public void reset() {
		if (sc != null)
			sc.reset();
	}
}
