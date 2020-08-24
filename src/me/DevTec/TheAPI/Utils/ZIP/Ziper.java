package me.DevTec.TheAPI.Utils.ZIP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Ziper {
	public static File zip(String zipName, File... files) {
        try {
		    FileOutputStream fos = new FileOutputStream("plugins/TheAPI/ZIP/"+zipName+".zip");
		    ZipOutputStream zipOut = new ZipOutputStream(fos);
		    for(File file : files) {
		    FileInputStream fis = new FileInputStream(file);
		    ZipEntry zipEntry = new ZipEntry(file.getName());
		    zipOut.putNextEntry(zipEntry);
		    byte[] bytes = new byte[1024];
		    int length;
		    while((length = fis.read(bytes)) >= 0) {
		        zipOut.write(bytes, 0, length);
		    }
		    zipOut.close();
		    fis.close();
		    }
		    fos.close();
        }catch(Exception e) {}
        return new File("plugins/TheAPI/ZIP/"+zipName+".zip");
    }
}
