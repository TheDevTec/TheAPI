package me.DevTec.Zip;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.Lists;

public class JarReader {
	private File a;
	public JarReader(File f) {
		a=f;
	}
	
	public File getFile() {
		return a;
	}
	
	public List<JarEntry> getEntries() {
		List<JarEntry> e = Lists.newArrayList();
		try {
			@SuppressWarnings("resource")
			JarFile file = new JarFile(a);
			if (file != null) {
				   Enumeration<? extends JarEntry> entries = file.entries();
				   if (entries != null) {
				      while (entries.hasMoreElements()) {
				    	 e.add(entries.nextElement());
				      }
		}}}catch(Exception erer) {}
		return e;
	}
	
	public String[] read(String fileName) {
		try {
			@SuppressWarnings("resource")
			JarFile file = new JarFile(a);
			if (file != null) {
			      for (JarEntry entry : getEntries()) {
			          if(entry.getName().equals(fileName)) {
			         InputStream is = file.getInputStream(entry);
			         int readBytes;
			         ArrayList<Character> c = Lists.newArrayList();
			         while ((readBytes = is.read()) != -1) {
			        	 c.add((char) readBytes);
		              }
			         return new String(ArrayUtils.toPrimitive(c.toArray(new Character[c.size()]))).split("\r");
			    }
			}}
		}catch(Exception erer) {}
		return null;
	}
}
