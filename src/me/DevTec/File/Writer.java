package me.DevTec.File;

import java.io.File;
import java.io.FileWriter;

public class Writer {
	private File a;
	private FileWriter w;
	public Writer(File f) {
		a=f;
		try {
		w=new FileWriter(a);
		} catch (Exception e) {
		}
	}
	
	public File getFile() {
		return a;
	}
	
	public void close() {
		try {
			w.close();
		} catch (Exception e) {
		}
	}
	
	public void write(String text) {
		try {
			w.write(text);
		} catch (Exception e) {
		}
	}
	
	public void append(char text) {
		try {
			w.append(text);
		} catch (Exception e) {
		}
	}
	
	public void flush() {
		try {
			w.flush();
		} catch (Exception e) {
		}
	}

	public void append(CharSequence text) {
		try {
			w.append(text);
		} catch (Exception e) {
		}
	}
}
