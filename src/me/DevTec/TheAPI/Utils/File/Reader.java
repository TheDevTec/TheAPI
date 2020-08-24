package me.DevTec.TheAPI.Utils.File;

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
