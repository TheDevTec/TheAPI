package me.DevTec.File;

import java.io.File;
import java.util.Scanner;

public class Reader {
	private File a;
	public Reader(File f) {
		a=f;
	}
	
	public String read() {
		StringBuffer buffer = new StringBuffer();
		try {
		Scanner sc = new Scanner(a);
	    while (sc.hasNextLine()) {
	    	buffer.append(sc.nextLine()+System.lineSeparator());
	    }
	    sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return buffer.toString();
	}
}
