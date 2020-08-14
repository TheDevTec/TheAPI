package me.DevTec.Other;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class Compression {
	public static int COMPRESS=2;
	
	private static byte[] buf = new byte[1024];
	public static byte[] compress(byte[] in) {
		Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	    for(int i = 0; i < COMPRESS; ++i) {
		  compressor.setInput(in);
		  compressor.finish();
		  while (!compressor.finished())
			  byteStream.write(buf, 0, compressor.deflate(buf));
		  in=byteStream.toByteArray();
		  compressor.reset();
		  byteStream.reset();
		}
	    return in;
	}
	
	public static File zip(File file) {
		if(file==null)return null;
		try {
		FileInputStream inputStream = new FileInputStream(file);
	    FileOutputStream outputStream = new FileOutputStream(file);
	    DeflaterOutputStream compresser = new DeflaterOutputStream(outputStream);
	    int contents;
	    while ((contents=inputStream.read())!=-1)
	       compresser.write(contents);
	    inputStream.close();
	    compresser.close();
		}catch(Exception e) {}
		return new File(file.getParent());
	}
}
