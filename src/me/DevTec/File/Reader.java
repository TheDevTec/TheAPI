package me.DevTec.File;

import java.io.File;
import java.util.Scanner;

public class Reader {
	private File a;
	private Scanner sc;
	public Reader(File f) {
		a=f;
		try {
		sc = new Scanner(a);
		} catch (Exception e) {
		}
	}
	
	public String read() {
		StringBuffer buffer = new StringBuffer();
		try {
	    while (hasLine()) {
	    	buffer.append(readLine()+System.lineSeparator());
	    }
	    reset();
		} catch (Exception e) {
		}
	    return buffer.toString();
	}
	
	public boolean hasLine() {
	    return sc.hasNextLine();
	}
	
	public String readLine() {
	    return sc.nextLine();
	}
	
	public void close() {
		 sc.close();
	}
	
	public void reset() {
		 sc.reset();
	}
}
