package me.DevTec.Other;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Decompression {
	public static int DECOMPRESS=2;

	public static byte[] decompress(byte[] in) {
		 Inflater decompressor = new Inflater(true);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for(int isf = 0; isf < DECOMPRESS; ++isf) {
		   decompressor.setInput(in);
		   byte[] buf = new byte[1024];
		   while (!decompressor.finished())
		       try {
		           bos.write(buf, 0, decompressor.inflate(buf));
		       } catch (DataFormatException e) {
		       }
		   decompressor.reset();
		  in=bos.toByteArray();
		  bos.reset();
		}
		return in;
	}

	public static InputStream unZip(File file) {
	    try{ 
	    ZipFile zip= new ZipFile(file);
	    ZipEntry entry = null;
	    for (Enumeration<?> e = zip.entries(); e.hasMoreElements();)
	        entry = (ZipEntry) e.nextElement();
	    InputStream in = zip.getInputStream(entry);
	    zip.close();
	    return in;
	    }catch(Exception e) {}
	    return null;

	}

	public static StringBuffer getText(InputStream in)  {
		StringBuffer out = new StringBuffer();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	    String line;
	    try {
	        while ((line = reader.readLine()) != null)
	            out.append(line);
	    } catch (Exception e) {
	    }
	    return out;
	}
}
