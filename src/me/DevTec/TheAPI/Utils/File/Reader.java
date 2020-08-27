package me.DevTec.TheAPI.Utils.File;

import java.io.File;
import java.util.Scanner;

public class Reader {
	
	public static String read(File f, boolean split) {
		Reader a = new Reader(f);
		String d = a.read(split);
		a.close();
		return d;
	}
	
	private Scanner sc;
	public Reader(File f) {
		try {
		sc = new Scanner(f);
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
	    while (hasLine()) {
	    	if(split)
		    	buffer.append(readLine()+System.lineSeparator());
	    	else
	    	buffer.append(readLine());
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
