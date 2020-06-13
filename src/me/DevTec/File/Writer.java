package me.DevTec.File;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	private File a;
	private FileWriter r;
	public Writer(File f) {
		a=f;
		try {
		r=new FileWriter(f);
		} catch (IOException e) {
		}
	}
	
	public File getFile() {
		return a;
	}
	
	public void write(String text) {
		try {
			r.write(text);
		} catch (IOException e) {
		}
	}
	
	public void close() {
		try {
			r.close();
		} catch (IOException e) {
		}
	}
}
