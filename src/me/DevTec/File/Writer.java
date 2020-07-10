package me.DevTec.File;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	private File a;
	public Writer(File f) {
		a=f;
	}
	
	public File getFile() {
		return a;
	}
	
	public void write(String text) {
		FileWriter r = null;
		try {
		r=new FileWriter(a);
		} catch (IOException e) {
		}
		try {
			r.write(text);
			r.close();
		} catch (IOException e) {
		}
	}
	
	public void append(char text) {
		FileWriter r = null;
		try {
		r=new FileWriter(a);
		} catch (IOException e) {
		}
		try {
			r.append(text);
			r.close();
		} catch (IOException e) {
		}
	}
	
	public void flush() {
		FileWriter r = null;
		try {
		r=new FileWriter(a);
		} catch (IOException e) {
		}
		try {
			r.flush();
			r.close();
		} catch (IOException e) {
		}
	}
}
