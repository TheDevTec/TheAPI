package me.DevTec.File;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {
	private File a;
	private Scanner r;
	private boolean closed;
	public Reader(File f) {
		a=f;
		try {
			r=new Scanner(f);
		} catch (FileNotFoundException e) {
		}
	}
	
	public File getFile() {
		return a;
	}
	
	public boolean hasNext() {
		return r.hasNextLine();
	}
	
	public void reset() {
		r.reset();
		closed=false;
	}
	
	public String read() {
		 if(hasNext())return r.nextLine();
		 if(!closed) {
			 closed=true;
			 r.close();
		}
		return null;
	}
}
