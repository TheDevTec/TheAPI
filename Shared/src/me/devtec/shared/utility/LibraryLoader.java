package me.devtec.shared.utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public interface LibraryLoader {
	public default void downloadFileFromUrl(String fileUrl, String pathFile) {
		try {
			downloadFileFromUrl(new URL(fileUrl), new File(pathFile));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public default void downloadFileFromUrl(String fileUrl, File file) {
		try {
			downloadFileFromUrl(new URL(fileUrl), file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public default void downloadFileFromUrl(URL url, File file) {
		try {
			if(file.exists() && !file.delete())return;
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			    OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			    InputStream in = url.openConnection().getInputStream();
			    byte[] buf = new byte[4096];
			    int r;
			    while ((r = in.read(buf)) != -1)
			        out.write(buf, 0, r);
			    if (in != null)
			        in.close();
			    if (out != null)
			        out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void load(File file);
	
	public boolean isLoaded(File file);
}
